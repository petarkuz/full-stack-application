package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.exception.DuplicateResourceException;
import mk.ukim.finki.application.exception.RequestValidationException;
import mk.ukim.finki.application.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder, CustomerDTOMapper customerDTOMapper) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
        this.customerDTOMapper = customerDTOMapper;
    }

    public List<CustomerDTO> getAllCustomers() {
        return this.customerDao.selectAllCustomers()
                .stream()
                .map(this.customerDTOMapper)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerById(Long id) {
        return this.customerDao
                .selectCustomerById(id)
                .map(this.customerDTOMapper)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));
    }

    private void checkIfEmailExists(String requestEmail) {
        if (this.customerDao.existsCustomerWithEmail(requestEmail))
            throw new DuplicateResourceException("customer with email [%s] already exists".formatted(requestEmail));
    }

    public void saveCustomer(CustomerRegistrationRequest request) {
        String email = request.email();
        checkIfEmailExists(email);

        Customer customer = new Customer(
                request.name(),
                request.email(),
                this.passwordEncoder.encode(request.password()),
                request.age(),
                request.gender());

        this.customerDao.saveCustomer(customer);
    }

    public void deleteCustomerById(Long id) {
        if (!this.customerDao.existsCustomerWithId(id))
            throw new ResourceNotFoundException("customer with id [%d] not found".formatted(id));

        this.customerDao.deleteCustomerById(id);
    }

    public Customer updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = this.customerDao
                .selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with id [%s] not found".formatted(id)));

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
            customer.setGender(request.gender());
            dataChanged = true;
        }

        if (!dataChanged)
            throw new RequestValidationException("no data changes found");

        return this.customerDao.updateCustomer(customer);
    }

}