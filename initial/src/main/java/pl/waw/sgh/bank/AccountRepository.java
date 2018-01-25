package pl.waw.sgh.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;

//@RepositoryRestResource(collectionResourceRel = "account", path = "account")
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomer(@Param("customer") Customer customer);
}
