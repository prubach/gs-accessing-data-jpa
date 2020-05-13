package pl.waw.sgh.bank.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import pl.waw.sgh.bank.data.*;

import java.util.ArrayList;

/**
 * This is the default (and only) view in this example.
 * <p>
 * It demonstrates how to create a form using Vaadin and the Binder. The backend
 * service and data class are in the <code>.data</code> package.
 */
@Route("")
public class MainView extends VerticalLayout {

    private final CustomerRepository repo;

    private final AccountRepository accountRepo;

    private final CustomerEditor editor;

    private final AccountEditor accountEditor;

    private Grid grid;

    private Grid accountGrid;

    private TextField filter;

    private Button addNewBtn;

    private Button addNewDebitAccountBtn;

    private Button addNewSavingsAccountBtn;

    private HorizontalLayout newAccountsLayout;

    /**
     * Flag for disabling first run for password validation
     */
    private boolean enablePasswordValidation;

    /**
     * We use Spring to inject the backend into our view
     */
    public MainView(@Autowired CustomerRepository repo, @Autowired CustomerEditor editor, @Autowired AccountRepository accountRepo, @Autowired AccountEditor accountEditor) {
        H3 title = new H3("Bank Application");
        this.repo = repo;
        this.editor = editor;
        this.accountRepo = accountRepo;
        this.accountEditor = accountEditor;
        this.grid = new Grid(Customer.class);
        this.accountGrid = new Grid(Account.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New customer");
        this.addNewDebitAccountBtn = new Button("New Debit");
        this.addNewSavingsAccountBtn = new Button("New Savings");

        /*
         * Create the components we'll need
         */

        // build layout
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);

        VerticalLayout customerLayout = new VerticalLayout(grid, editor);

        newAccountsLayout = new HorizontalLayout(addNewDebitAccountBtn, addNewSavingsAccountBtn);

        VerticalLayout accountLayout = new VerticalLayout(accountGrid, newAccountsLayout, accountEditor);

        HorizontalLayout grids = new HorizontalLayout(customerLayout, accountLayout);

        VerticalLayout mainLayout = new VerticalLayout(actions, grids);

        add(title);
        add(mainLayout);

        newAccountsLayout.setVisible(false);

        // Configure layouts and components
        actions.setSpacing(true);
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        grid.setHeight("300px");
        grid.setWidth("350px");
        grid.setColumns("customerID", "firstName", "lastName");

        accountGrid.setHeight("300px");
        accountGrid.setWidth("350px");
        accountGrid.setColumns("accountID", "savings", "balance");

        //accountGrid.getColumn("savings").setRenderer(new ImageRenderer());

        filter.setPlaceholder("Filter by last name");

        // Hook logic to components

        // Replace listing with filtered content when user changes filter
        filter.addValueChangeListener(e -> listCustomers(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.addSelectionListener(e -> {
            if (e.getAllSelectedItems().isEmpty()) {
                editor.setVisible(false);
                listAccounts(null);
            }
            else {
                Customer selCust = new ArrayList<Customer>(grid.getSelectedItems()).get(0);
                editor.editCustomer(selCust);
                listAccounts(selCust);
            }
        });


        accountGrid.addSelectionListener(e -> {
            if (e.getAllSelectedItems().isEmpty()) {
                accountEditor.setVisible(false);
            }
            else {
                Account selAcc = new ArrayList<Account>(accountGrid.getSelectedItems()).get(0);
                accountEditor.editAccount(selAcc);
            }
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", "")));

        addNewDebitAccountBtn.addClickListener(e -> accountEditor.editAccount(
                new DebitAccount(new ArrayList<Customer>(grid.getSelectedItems()).get(0))));

        addNewSavingsAccountBtn.addClickListener(e -> accountEditor.editAccount(
                new SavingsAccount(new ArrayList<Customer>(grid.getSelectedItems()).get(0))));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listCustomers(filter.getValue());
        });

        // Listen changes made by the editor, refresh data from backend
        accountEditor.setChangeHandler(() -> {
            accountEditor.setVisible(false);
            listAccounts(new ArrayList<Customer>(grid.getSelectedItems()).get(0));
            //listCustomers(filter.getValue());
        });

        // Initialize listing
        listCustomers(null);
        listAccounts(null);
    }


    private void listCustomers(String text) {
        if (StringUtils.isEmpty(text)) {
            grid.setDataProvider(
                    new ListDataProvider<Customer>(repo.findAll()));
            newAccountsLayout.setVisible(false);
        }
        else {
            grid.setDataProvider(new ListDataProvider<Customer>(
                    repo.findByLastNameStartsWithIgnoreCase(text)));
            newAccountsLayout.setVisible(false);
        }
    }
    // end::listCustomers[]


    // tag::listAccounts[]
    private void listAccounts(Customer owner) {
        if (owner==null) {
            accountGrid.setDataProvider(
                    new ListDataProvider<Account>(new ArrayList<Account>()));
        }
        else {
            accountGrid.setDataProvider((new ListDataProvider<Account>(
                    accountRepo.findByCustomer(owner))));
            newAccountsLayout.setVisible(true);
        }
    }
    // end::listAccounts[]
}
