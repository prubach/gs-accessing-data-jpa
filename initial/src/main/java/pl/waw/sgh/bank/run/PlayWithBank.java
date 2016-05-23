package pl.waw.sgh.bank.run;

import pl.waw.sgh.bank.Account;
import pl.waw.sgh.bank.Bank;
import pl.waw.sgh.bank.Customer;
import pl.waw.sgh.bank.exceptions.BankException;

import java.math.BigDecimal;

/**
 * Created by prubac on 4/15/2016.
 */
public class PlayWithBank {

    public static void main(String[] args) {

        Bank bank = new Bank();
        Customer c1 = bank.createCustomer("John", "Smith");
        Account a1_1 = bank.createAccount(c1, false);
        Account a1_2 = bank.createAccount(c1, true);

        Customer c2 = bank.createCustomer("Anne", "Brown");
        Account a2_1 = bank.createAccount(c2, false);
        Account a2_2 = bank.createAccount(c2, true);

        try {
            a1_2.deposit(new BigDecimal(3435.67));

            System.out.println(bank);

            Account a = bank.getAccountById(2L);

            System.out.println("Found: " + a);

            bank.transfer(2L, 1L, 14000d);
        } catch (BankException be) {
            System.out.println(be.getMessage());
        }
        System.out.println(bank);
    }
}
