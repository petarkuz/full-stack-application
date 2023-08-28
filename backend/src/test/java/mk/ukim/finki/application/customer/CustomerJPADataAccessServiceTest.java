package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        this.underTest = new CustomerJPADataAccessService(this.customerRepository);
    }

    @Test
    void selectAllCustomers() {
        // Given

        // When
        this.underTest.selectAllCustomers();

        // Then
        verify(this.customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        // Given
        Long id = 1L;

        // When
        this.underTest.selectCustomerById(id);

        // Then
        verify(this.customerRepository).findById(id);
    }

    @Test
    void saveCustomer() {
        // Given
        Customer customer = new Customer(
                1L, "Foo", "foo.fighter@rnr.com", 1, Gender.MALE);

        // When
        this.underTest.saveCustomer(customer);

        // Then
        verify(this.customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        String email = "foo.fighter@rnr.com";

        // When
        this.underTest.existsCustomerWithEmail(email);

        // Then
        verify(this.customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        // Given
        Long id = 1L;

        // When
        this.underTest.deleteCustomerById(id);

        // Then
        verify(this.customerRepository).deleteById(id);
    }

    @Test
    void existsCustomerWithId() {
        // Given
        Long id = 1L;

        // When
        this.underTest.existsCustomerWithId(id);

        // Then
        verify(this.customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        Customer customer = new Customer(
                1L, "Foo", "foo.fighter@rnr.com", 1, Gender.MALE);

        // When
        this.underTest.updateCustomer(customer);

        // Then
        verify(this.customerRepository).save(customer);
    }
}