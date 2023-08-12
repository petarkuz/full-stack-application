package mk.ukim.finki.application.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer customerId);

    Customer saveCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);
}
