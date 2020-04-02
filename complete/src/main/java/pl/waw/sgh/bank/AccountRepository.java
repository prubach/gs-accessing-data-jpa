package pl.waw.sgh.bank;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "account", path = "account")
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {

    /*
        Example call:

        http://localhost:8080/account/search/findByCustomer?customer=http://localhost:8080/customer/1
     */

    @RestResource(path = "findByCustomer", rel = "findByCustomer")
    List<Account> findByCustomer(@Param("customer") Customer customer);

    Account findByAccountID(@Param("id") Long accountId);
}
