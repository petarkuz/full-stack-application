package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;
import mk.ukim.finki.application.exception.DuplicateResourceException;
import mk.ukim.finki.application.exception.RequestValidationException;
import mk.ukim.finki.application.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        this.underTest = new CustomerService(this.customerDao, passwordEncoder, customerDTOMapper);
    }

    @Test
    void getAllCustomers() {
        // Given

        // When
        this.underTest.getAllCustomers();

        // Then
        verify(this.customerDao).selectAllCustomers();
    }

    @Test
    void getCustomerById() {
        // Given
        Long id = 1L;
        Customer customer = new Customer(
                id, "Foo", "foo.fighter@rnr.com", "password", 1, Gender.MALE);

        CustomerDTO expected = this.customerDTOMapper.apply(customer);

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        CustomerDTO actualCustomer = this.underTest.getCustomerById(id);

        // Then
        assertThat(actualCustomer).isEqualTo(expected);
    }

    @Test
    void willThrowWhenCustomerReturnEmptyOptional() {
        // Given
        Long id = 1L;

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> this.underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }

    @Test
    void saveCustomer() {
        // Given
        String email = "foo.fighter@rnr.com";
        String name = "Foo";
        Integer age = 1;
        Gender gender = Gender.MALE;
        String password = "password";
        String passwordHash = "7a05fb50454f474b8c5f0c7148adc157";
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        // When
        when(this.customerDao.existsCustomerWithEmail(email))
                .thenReturn(false);

        when(this.passwordEncoder.encode(password))
                .thenReturn(passwordHash);

        this.underTest.saveCustomer(customerRegistrationRequest);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(this.customerDao).saveCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
        assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
        assertThat(capturedCustomer.getGender()).isEqualTo(gender);
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given
        String email = "foo.fighter@rnr.com";
        String name = "Foo";
        String password = "password";
        Integer age = 1;
        Gender gender = Gender.MALE;
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        // When
        when(this.customerDao.existsCustomerWithEmail(email))
                .thenReturn(true);

        // Then
        assertThatThrownBy(() -> this.underTest.saveCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("customer with email [%s] already exists".formatted(email));

        verify(this.customerDao, never()).saveCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        Long id = 1L;

        // When
        when(this.customerDao.existsCustomerWithId(id))
                .thenReturn(true);

        this.underTest.deleteCustomerById(id);

        // Then
        verify(this.customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdExistsWhileDeletingACustomer() {
        // Given
        Long id = 1L;

        // When
        when(this.customerDao.existsCustomerWithId(id))
                .thenReturn(false);

        // Then
        assertThatThrownBy(() -> this.underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%d] not found".formatted(id));

        verify(this.customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void updateCustomerName() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String password = "password";
        String name = "Foo";
        Integer age = 1;
        Gender gender = Gender.MALE;

        String requestName = "Dave";

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                requestName, null, null, null
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        this.underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(this.customerDao).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(requestName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
        assertThat(capturedCustomer.getGender()).isEqualTo(gender);
    }

    @Test
    void updateCustomerEmail() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String password = "password";
        String name = "Foo";
        Integer age = 1;
        Gender gender = Gender.MALE;

        String requestEmail = "dave.grohl@foo.com";

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, requestEmail, null, null
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));
        when(this.customerDao.existsCustomerWithEmail(requestEmail))
                .thenReturn(false);

        this.underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(this.customerDao).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(requestEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
        assertThat(capturedCustomer.getGender()).isEqualTo(gender);
    }

    @Test
    void willThrowWhenEmailExistsWhileUpdatingACustomer() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String password = "password";
        String name = "Foo";
        Integer age = 1;
        Gender gender = Gender.MALE;

        String requestEmail = "dave.grohl@foo.com";

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, requestEmail, null, null
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));
        when(this.customerDao.existsCustomerWithEmail(requestEmail))
                .thenReturn(true);

        // Then
        assertThatThrownBy(() -> this.underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("customer with email [%s] already exists".formatted(requestEmail));

        verify(this.customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomerAge() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String password = "password";
        String name = "Foo";
        Integer age = 1;
        Gender gender = Gender.MALE;

        Integer requestAge = 54;

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, null, requestAge, null
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        this.underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(this.customerDao).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(requestAge);
        assertThat(capturedCustomer.getGender()).isEqualTo(gender);
    }

    @Test
    void updateCustomerGender() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String password = "password";
        String name = "Foo";
        Integer age = 1;
        Gender gender = Gender.MALE;

        Gender requestGender = Gender.FEMALE;

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, null, null, requestGender
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        this.underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(this.customerDao).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(name);
        assertThat(capturedCustomer.getEmail()).isEqualTo(email);
        assertThat(capturedCustomer.getAge()).isEqualTo(age);
        assertThat(capturedCustomer.getGender()).isEqualTo(requestGender);
    }

    @Test
    void updateCustomersAllProperties() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String name = "Foo";
        String password = "password";
        Integer age = 1;
        Gender gender = Gender.MALE;

        String requestName = "Dave";
        String requestEmail = "dave.grohl@foo.com";
        Integer requestAge = 54;
        Gender requestGender = Gender.FEMALE;

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                requestName, requestEmail, requestAge, requestGender
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));
        when(this.customerDao.existsCustomerWithEmail(requestEmail))
                .thenReturn(false);

        this.underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(this.customerDao).updateCustomer(argumentCaptor.capture());

        Customer capturedCustomer = argumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(requestName);
        assertThat(capturedCustomer.getEmail()).isEqualTo(requestEmail);
        assertThat(capturedCustomer.getAge()).isEqualTo(requestAge);
        assertThat(capturedCustomer.getGender()).isEqualTo(requestGender);
    }

    @Test
    void willThrowAndNotUpdateCustomersWhenNothingToUpdate() {
        // Given
        Long id = 1L;
        String email = "foo.fighter@rnr.com";
        String name = "Foo";
        String password = "password";
        Integer age = 1;
        Gender gender = Gender.MALE;

        Customer customer = new Customer(
                id, name, email, password, age, gender);
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                name, email, age, gender
        );

        // When
        when(this.customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        // Then
        assertThatThrownBy(() -> this.underTest.updateCustomer(id, request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        verify(this.customerDao, never()).updateCustomer(any());
    }
}