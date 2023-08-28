package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.exception.DuplicateResourceException;
import mk.ukim.finki.application.exception.RequestValidationException;
import mk.ukim.finki.application.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return this.customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Long id) {
        return this.customerDao
                .selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
    }

    private void checkIfEmailExists(String requestEmail) {
        if (this.customerDao.existsCustomerWithEmail(requestEmail))
            throw new DuplicateResourceException("customer with email [%s] already exists".formatted(requestEmail));
    }

    public Customer saveCustomer(CustomerRegistrationRequest request) {
        String email = request.email();
        checkIfEmailExists(email);

        Customer customer = new Customer(
                request.name(),
                request.email(),
                request.age(),
                request.gender());

        return this.customerDao.saveCustomer(customer);
    }

    public void deleteCustomerById(Long id) {
        if (!this.customerDao.existsCustomerWithId(id))
            throw new ResourceNotFoundException("customer with id [%d] not found".formatted(id));

        this.customerDao.deleteCustomerById(id);
    }

    public Customer updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = getCustomerById(id);

        boolean dataChanged = false;

        if (request.name() != null && !Objects.equals(request.name(), customer.getName())) {
            customer.setName(request.name());
            dataChanged = true;
        }
        if (request.email() != null && !Objects.equals(request.email(), customer.getEmail())) {
            checkIfEmailExists(request.email());
            customer.setEmail(request.email());
            dataChanged = true;
        }
        if (request.age() != null && !Objects.equals(request.age(), customer.getAge())) {
            customer.setAge(request.age());
            dataChanged = true;
        }
        if (request.gender() != null && !Objects.equals(request.gender(), customer.getGender())) {
            customer.setAge(request.age());
            dataChanged = true;
        }

        if (!dataChanged)
            throw new RequestValidationException("no data changes found");

        return this.customerDao.updateCustomer(customer);
    }

}