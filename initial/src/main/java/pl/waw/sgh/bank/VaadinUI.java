package pl.waw.sgh.bank;

import com.vaadin.annotations.Theme;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    private CustomerRepository custRepo;

    private AccountRepository accRepo;

    private final Grid grid;

    private final Grid accountGrid;

    private final CustomerEditor custEditor;

    private final AccountEditor accEditor;

    private final Button addNewCustBtn;

    private final Button addNewDebitAccBtn;

    private final Button addNewSavingsAccBtn;

    private HorizontalLayout newAccLayout;

    @Autowired
    public VaadinUI(CustomerRepository custRepo, CustomerEditor customerEditor, AccountRepository accRepo, AccountEditor accountEditor) {
        this.custRepo = custRepo;
        this.accRepo = accRepo;
        this.grid = new Grid(Customer.class);
        this.accountGrid = new Grid(Account.class);
        this.custEditor = customerEditor;
        this.accEditor = accountEditor;
        this.addNewCustBtn = new Button("New Customer", FontAwesome.PLUS);
        this.addNewDebitAccBtn = new Button("New debit acc", FontAwesome.PLUS);
        this.addNewSavingsAccBtn = new Button("New savings acc", FontAwesome.PLUS);
    }

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout customerLayout = new VerticalLayout(addNewCustBtn, grid, custEditor);

        newAccLayout = new HorizontalLayout(addNewDebitAccBtn, addNewSavingsAccBtn);

        VerticalLayout accountsLayout = new VerticalLayout(accountGrid, newAccLayout, accEditor);

        HorizontalLayout mainLayout = new HorizontalLayout(customerLayout, accountsLayout);
        setContent(mainLayout);

        grid.setHeight(300, Unit.PIXELS);
        grid.setColumns("customerID", "firstName", "lastName");

        accountGrid.setHeight(200, Unit.PIXELS);
        accountGrid.setColumns("accountID", "savings", "balance");
        listCustomer();
        listAccounts(null);

        grid.addSelectionListener(e -> {
           if (e.getAllSelectedItems().isEmpty()) {
               listAccounts(null);
           } else {
               Customer customer = new ArrayList<Customer>(grid.getSelectedItems()).get(0);
               listAccounts(customer);
               custEditor.editCustomer(customer);
           }
        });

        accountGrid.addSelectionListener(e -> {
            if (e.getAllSelectedItems().isEmpty()) {
                accEditor.setVisible(false);
            } else  {
                Account selAcc = new ArrayList<Account>(accountGrid.getSelectedItems()).get(0);
                accEditor.editAccount(selAcc);
            }

        });

        custEditor.setChangeHandler(() -> {
                    custEditor.setVisible(false);
                    listCustomer();
                });
        accEditor.setChangeHandler(() -> {
            accEditor.setVisible(false);
            listAccounts(new ArrayList<Customer>(grid.getSelectedItems()).get(0));
        });

        addNewCustBtn.addClickListener(e -> {
           custEditor.editCustomer(new Customer("",""));
        });

        addNewDebitAccBtn.addClickListener(e -> {
            Account newAcc = new DebitAccount(getSelCustomer());
            accEditor.editAccount(newAcc);
        });

        addNewSavingsAccBtn.addClickListener(e -> {
            accEditor.editAccount(new SavingsAccount(getSelCustomer()));
        });

    }

    private Customer getSelCustomer() {
        return new ArrayList<Customer>(grid.getSelectedItems()).get(0);
    }

    private void listCustomer() {
        grid.setDataProvider(new ListDataProvider<Customer>(
                custRepo.findAll()
        ));
    }

    private void listAccounts(Customer customer) {
        if (customer==null) {
            accountGrid.setDataProvider(
                    new ListDataProvider<Account>(new ArrayList<>()));
        } else {
            accountGrid.setDataProvider(new ListDataProvider<Account>(
                    accRepo.findByCustomer(customer)
            ));
        }
    }

}

