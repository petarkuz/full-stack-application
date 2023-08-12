package mk.ukim.finki.application.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    private static List<Customer> customerList;

    static {
        customerList = new ArrayList<>();
        customerList.add(new Customer(1, "Alex", "alexdumphy@gmail.com", 22));
        customerList.add(new Customer(2, "Haily", "queenh@gmail.com", 25));
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerList;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customerList.stream()
                .filter(customer -> customerId.equals(customer.getId()))
                .findFirst();
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        customerList.add(customer);
        return customer;
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customerList.stream()
                .anyMatch(customer -> customer.getEmail().equals(email));
    }

}
