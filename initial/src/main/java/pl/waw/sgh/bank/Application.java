package pl.waw.sgh.bank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.debug("Hello in Debug");
        log.info("Hello in Info");
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public CommandLineRunner demo(CustomerRepository custRepo, AccountRepository accRepo) {
        return (args) -> {
            Customer c1 = new Customer("Anne", "Smith");
            Customer c2 = new Customer("John", "Brown");
            custRepo.save(c1);
            custRepo.save(c2);
            //List<Customer> custs = custRepo.findByLastName("Smith");
            Iterable<Customer> custs = custRepo.findAll();
            for (Customer c : custs) {
                log.info("Customer: " + c);
            }
            Account acc1 = new DebitAccount(c1);
            Account acc2 = new SavingsAccount(c1);
            Account acc3 = new DebitAccount(c2);
            Account acc4 = new SavingsAccount(c2);
            accRepo.save(acc1);
            accRepo.save(acc2);
            accRepo.save(acc3);
            accRepo.save(acc4);

            log.info("We have " + accRepo.count() + " accounts");
        };
    }


}
