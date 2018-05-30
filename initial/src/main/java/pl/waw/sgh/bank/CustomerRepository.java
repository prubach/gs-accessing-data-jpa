package pl.waw.sgh.bank;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CustomerRepository extends CrudRepository<Customer,Integer>{

    List<Customer> findByLastName(String name);

    List<Customer> findByLastNameAndFirstName(String name, String fname);
}
