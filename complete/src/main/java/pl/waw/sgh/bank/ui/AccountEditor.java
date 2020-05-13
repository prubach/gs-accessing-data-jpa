package pl.waw.sgh.bank.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import pl.waw.sgh.bank.data.Account;
import pl.waw.sgh.bank.data.AccountRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form in
 * multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for all your
 * forms - less code, better UX. See e.g. AbstractForm in Virin
 * (https://vaadin.com/addon/viritin).
 */
@SpringComponent
@UIScope
public class AccountEditor extends VerticalLayout {

	private final AccountRepository repository;

	/**
	 * The currently edited account
	 */
	private Account account;

	/* Fields to edit properties in account entity */
	//TextField customer = new TextField("Customer");
	TextField balance = new TextField("Balance");
	TextField transferDest = new TextField("Transfer To (Account ID)");
	TextField transferBalance = new TextField("Transfer Balance");

	Binder<Account> accBinder;

	/* Action buttons */
	Button save = new Button("Save", new Icon(VaadinIcon.SAFE));
	Button delete = new Button("Delete", new Icon(VaadinIcon.TRASH));
	Button transfer = new Button("Transfer", new Icon(VaadinIcon.ARROW_FORWARD));
	HorizontalLayout actions = new HorizontalLayout(save, delete, transfer);

	@Autowired
	public AccountEditor(AccountRepository repository) {
		this.repository = repository;

		add(balance, transferDest, transferBalance, actions);

		// Configure and style components
		setSpacing(true);
		//save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.addClickShortcut(Key.SAVE);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> {
					try {
						accBinder.writeBean(account);
						repository.save(account);
					} catch (ValidationException ve) {
						Notification.show("Problem validating account " + ve.getMessage());
					}
		});
		delete.addClickListener(e -> repository.delete(account));
		transfer.addClickListener(e -> {
			try {
				Long toAccId = Long.parseLong(transferDest.getValue());
				BigDecimal transferBal = new BigDecimal(transferBalance.getValue());
				Optional<Account> toAccountOpt = repository.findById(toAccId);
				Account toAccount = toAccountOpt.get();
				// No validation yet !!!
				toAccount.setBalance(toAccount.getBalance().add(transferBal));
				repository.save(toAccount);
				account.setBalance(account.getBalance().subtract(transferBal));
				repository.save(account);
			} catch (NumberFormatException pe) {
				Notification.show("Problem parsing the ID");
			}
		});
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editAccount(Account c) {
		final boolean persisted = c.getAccountID() != null;
		if (persisted) {
			// Find fresh entity for editing
			Optional<Account> toAccountOpt = repository.findById(c.getAccountID());
			account = toAccountOpt.get();
		}
		else {
			account = c;
		}

		// Bind account properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		accBinder = new Binder<>(Account.class);
		accBinder.forField(balance).withConverter(new StringToBigDecimalConverter("Must be a number"))
				.bind(Account::getBalance, Account::setBalance);
		accBinder.readBean(account);

		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		//customer.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
		transfer.addClickListener(e -> h.onChange());
	}

}
