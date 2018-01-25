package pl.waw.sgh.bank;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRespository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);
}
