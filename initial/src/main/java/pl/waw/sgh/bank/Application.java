package pl.waw.sgh.bank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner demo(CustomerRespository custRepo, AccountRepository accRepo) {
        return args -> {
            Customer kl1 = new Customer("John", "Brown");
            Customer kl2 = new Customer("Anne", "Smith");

            System.out.println("Storing customers");
            custRepo.save(kl1);
            custRepo.save(kl2);

            Account ac1 = new DebitAccount(kl1);
            Account ac2 = new SavingsAccount(kl2);
            accRepo.save(ac1);
            accRepo.save(ac2);

//            CustomerDao.getInstance().delete(3);
            //CustomerDao.getInstance().update(4, "Joanne", "D'Arch", "email");

            System.out.println("Retrieving customers");
            Iterable<Customer> customers = custRepo.findAll();
            for (Customer customer : customers) {
                System.out.println("-----------------------------");
                System.out.println("ID: " + customer.getCustomerID());
                System.out.println("FirstName: " + customer.getFirstName());
                System.out.println("LastName: " + customer.getLastName());
                System.out.println("-----------------------------");
            }
        };
    }

}
