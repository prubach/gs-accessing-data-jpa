package pl.waw.sgh.bank.ui;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import pl.waw.sgh.bank.data.Customer;
import pl.waw.sgh.bank.data.CustomerRepository;

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
public class CustomerEditor extends VerticalLayout {

	private final CustomerRepository repository;

	/**
	 * The currently edited customer
	 */
	private Customer customer;

	/* Fields to edit properties in Customer entity */
	TextField firstName = new TextField("First name");
	TextField lastName = new TextField("Last name");
	Binder<Customer> customerBinder;

	/* Action buttons */
	Button save = new Button("Save", new Icon(VaadinIcon.SAFE));
	Button delete = new Button("Delete", new Icon(VaadinIcon.TRASH));
	Button cancel = new Button("Cancel", new Icon(VaadinIcon.CLOSE));
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	@Autowired
	public CustomerEditor(CustomerRepository repository) {
		this.repository = repository;

		add(firstName, lastName, actions);

		// Configure and style components
		setSpacing(true);

		// wire action buttons to save, delete and reset
		save.addClickListener(e -> {
			try {
				customerBinder.writeBean(customer);
				repository.save(customer);
			} catch (ValidationException ve) {
				Notification.show("Problem validating customer " + ve.getMessage());
			}
		});
		delete.addClickListener(e -> repository.delete(customer));
		cancel.addClickListener(e -> editCustomer(customer));
		setVisible(false);
	}

	public interface ChangeHandler {

		void onChange();
	}

	public final void editCustomer(Customer c) {
		final boolean persisted = c.getCustomerID() != null;
		if (persisted) {
			// Find fresh entity for editing
			Optional<Customer> cust = repository.findById(c.getCustomerID());
			customer = cust.get();
		}
		else {
			customer = c;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving

		customerBinder = new Binder<>(Customer.class);
		customerBinder.bindInstanceFields(this);
		customerBinder.readBean(customer);

		setVisible(true);

		// A hack to ensure the whole form is visible
		save.focus();
		// Select all text in firstName field automatically
		//firstName.selectAll();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		save.addClickListener(e -> h.onChange());
		delete.addClickListener(e -> h.onChange());
	}

}
