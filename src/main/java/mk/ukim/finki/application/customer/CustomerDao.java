package mk.ukim.finki.application.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Long id);

    Customer saveCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    void deleteCustomerById(Long id);

    boolean existsCustomerWithId(Long id);

    Customer updateCustomer(Customer customer);
}
