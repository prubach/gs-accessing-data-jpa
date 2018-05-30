package pl.waw.sgh.bank;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Integer> {

    List<Account> findByCustomer(Customer customer);

}
