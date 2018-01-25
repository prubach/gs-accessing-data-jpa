package pl.waw.sgh.bank;

import com.vaadin.annotations.Theme;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

    private CustomerRepository custRepo;

    private AccountRepository accRepo;

    private final Grid grid;

    private final Grid accountGrid;

    @Autowired
    public VaadinUI(CustomerRepository custRepo, AccountRepository accRepo) {
        this.custRepo = custRepo;
        this.accRepo = accRepo;
        this.grid = new Grid(Customer.class);
        this.accountGrid = new Grid(Account.class);
    }

    @Override
    protected void init(VaadinRequest request) {
        Label myFirstlabel = new Label("Hello world");
        VerticalLayout customerLayout = new VerticalLayout(myFirstlabel, grid, accountGrid);
        setContent(customerLayout);

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
           }
        });

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

