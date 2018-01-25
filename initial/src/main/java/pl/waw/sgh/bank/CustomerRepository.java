package pl.waw.sgh.bank;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//@RepositoryRestResource(collectionResourceRel = "customer", path = "customer")
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByLastName(@Param("lastName") String lastName);
}
