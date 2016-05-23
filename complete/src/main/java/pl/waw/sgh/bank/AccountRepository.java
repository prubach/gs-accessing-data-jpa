package pl.waw.sgh.bank;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findByCustomer(Customer customer);

    Account findByAccountID(Long accountId);
}
