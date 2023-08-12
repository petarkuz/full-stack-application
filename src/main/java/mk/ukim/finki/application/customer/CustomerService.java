package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.exception.DuplicateResourceException;
import mk.ukim.finki.application.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return this.customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer customerId) {
        return this.customerDao
                .selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId)));
    }

    public Customer saveCustomer(CustomerRegistrationRequest request) {
        String email = request.email();

        if (this.customerDao.existsCustomerWithEmail(email))
            throw new DuplicateResourceException("customer with email [%s] already exists".formatted(email));

        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.age()
        );

        return this.customerDao.saveCustomer(customer);
    }
}
