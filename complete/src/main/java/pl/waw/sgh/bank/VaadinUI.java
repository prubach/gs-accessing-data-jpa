package pl.waw.sgh.bank;

import com.vaadin.data.provider.ListDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.annotations.Theme;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.util.ArrayList;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

	private final CustomerRepository repo;

	private final AccountRepository accountRepo;

	private final CustomerEditor editor;

	private final AccountEditor accountEditor;

	private final Grid grid;

	private final Grid accountGrid;

	private final TextField filter;

	private final Button addNewBtn;

	private final Button addNewDebitAccountBtn;

	private final Button addNewSavingsAccountBtn;

	private HorizontalLayout newAccountsLayout;

	@Autowired
	public VaadinUI(CustomerRepository repo, CustomerEditor editor, AccountRepository accountRepo, AccountEditor accountEditor) {
		this.repo = repo;
		this.editor = editor;
		this.accountRepo = accountRepo;
		this.accountEditor = accountEditor;
		this.grid = new Grid(Customer.class);
		this.accountGrid = new Grid(Account.class);
		this.filter = new TextField();
		this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
		this.addNewDebitAccountBtn = new Button("New debit account", FontAwesome.PLUS);
		this.addNewSavingsAccountBtn = new Button("New savings account", FontAwesome.PLUS);
	}

	@Override
	protected void init(VaadinRequest request) {
		// build layout
		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);

		VerticalLayout customerLayout = new VerticalLayout(grid, editor);

		newAccountsLayout = new HorizontalLayout(addNewDebitAccountBtn, addNewSavingsAccountBtn);

		VerticalLayout accountLayout = new VerticalLayout(accountGrid, newAccountsLayout, accountEditor);

		HorizontalLayout grids = new HorizontalLayout(customerLayout, accountLayout);

		VerticalLayout mainLayout = new VerticalLayout(actions, grids);

		setContent(mainLayout);

		newAccountsLayout.setVisible(false);

		// Configure layouts and components
		actions.setSpacing(true);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		grid.setHeight(300, Unit.PIXELS);
		grid.setColumns("customerID", "firstName", "lastName");

		accountGrid.setHeight(300, Unit.PIXELS);
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

	// tag::listCustomers[]
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
