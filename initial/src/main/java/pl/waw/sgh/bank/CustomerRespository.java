package pl.waw.sgh.bank;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "customer", path = "customer")
public interface CustomerRespository extends PagingAndSortingRepository<Customer, Long> {

    List<Customer> findByLastName(@Param("lastName") String lastName);
}
