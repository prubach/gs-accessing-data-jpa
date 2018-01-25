package pl.waw.sgh.bank;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

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
	Binder<Account> accBinder;

	/* Action buttons */
	Button save = new Button("Save", FontAwesome.SAVE);
	Button delete = new Button("Delete", FontAwesome.TRASH_O);
	CssLayout actions = new CssLayout(save, delete);

	@Autowired
	public AccountEditor(AccountRepository repository) {
		this.repository = repository;

		addComponents(balance, actions);

		// Configure and style components
		setSpacing(true);
		actions.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(ShortcutAction.KeyCode.ENTER);

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
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editAccount(Account c) {
		final boolean persisted = c.getAccountID() != null;
		if (persisted) {
			// Find fresh entity for editing
			account = repository.findOne(c.getAccountID());
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
	}

}
