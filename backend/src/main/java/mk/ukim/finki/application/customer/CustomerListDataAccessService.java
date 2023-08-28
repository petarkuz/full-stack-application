package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    private static List<Customer> customerList;

    static {
        customerList = new ArrayList<>();
        customerList.add(new Customer(1L, "Alex", "alexdumphy@gmail.com", 22, Gender.FEMALE));
        customerList.add(new Customer(2L, "Haily", "queenh@gmail.com", 25, Gender.FEMALE));
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customerList;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
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

    @Override
    public void deleteCustomerById(Long id) {
        customerList.stream()
                .filter(customer -> customer.getId().equals(id))
                .findFirst()
                .ifPresent(customerList::remove);
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        return customerList.stream()
                .anyMatch(customer -> customer.getId().equals(id));
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        deleteCustomerById(customer.getId());
        customerList.add(customer);

        return customer;
    }

}
