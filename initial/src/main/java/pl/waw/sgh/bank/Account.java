package pl.waw.sgh.bank;

import pl.waw.sgh.bank.exceptions.IllegalDataException;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by prubac on 4/15/2016.
 */
@Entity
public abstract class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accountID;

    @ManyToOne
    private Customer customer;

    private BigDecimal balance;

    private Boolean savings = true;

    public Account() {
    }

    public Account(Long accountID, Customer customer) {
        this.accountID = accountID;
        this.customer = customer;
        this.balance = new BigDecimal(0);
    }

    public Account(Customer customer) {
        this.customer = customer;
        this.balance = new BigDecimal(0);
    }

    public boolean isSavings() {
        return savings;
    }

    public void setSavings(boolean savings) {
        this.savings = savings;
    }

    public Long getAccountID() {
        return accountID;
    }

    public void setAccountID(Long accountID) {
        this.accountID = accountID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) throws IllegalDataException {
        if (amount.compareTo(new BigDecimal(0))<=0)
            throw new IllegalDataException(
                    "Can't deposit on Account ID: "
                            + accountID +
                            " negative amount: " + amount);
        this.balance = balance.add(amount);
    }

    public void charge(BigDecimal amount)  throws IllegalDataException {
        if (amount.compareTo(new BigDecimal(0))<=0)
            throw new IllegalDataException(
                    "Can't charge Account ID: "
                            + accountID +
                            " negative amount: " + amount);

        balance = balance.subtract(amount);
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                .replace("Account", "") + "{" +
                "ID=" + accountID +
                ", " + balance.setScale(2,BigDecimal.ROUND_HALF_EVEN) +
                ", cust=" + customer.getLastName() +
                '}';
    }
}
