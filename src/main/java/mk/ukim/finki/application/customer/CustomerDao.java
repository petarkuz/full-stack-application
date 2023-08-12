package mk.ukim.finki.application.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer id);

    Customer saveCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    void deleteCustomerById(Integer id);

    boolean existsCustomerWithId(Integer id);

    Customer updateCustomer(Customer customer);
}
