package mk.ukim.finki.application.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao {

    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return this.customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return this.customerRepository.findById(customerId);
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return this.customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public void deleteCustomerById(Integer id) {
        this.customerRepository.deleteById(id);
    }

    @Override
    public boolean existsCustomerWithId(Integer id) {
        return this.customerRepository.existsCustomerById(id);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        return this.customerRepository.save(customer);
    }
}
