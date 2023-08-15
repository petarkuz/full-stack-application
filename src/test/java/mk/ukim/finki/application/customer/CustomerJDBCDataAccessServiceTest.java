package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        this.underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                this.customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);
        // When
        List<Customer> actualCustomer = this.underTest.selectAllCustomers();

        // Then
        assertThat(actualCustomer).isNotEmpty();
        assertThat(actualCustomer).hasSize(1);
    }

    @Test
    void selectCustomerById() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        // Then
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(name);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(age);
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        Long id = -1L;

        // When
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        // Then
        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void saveCustomer() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);


        // When
        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // Then
        assertThat(this.underTest.selectCustomerById(id)).isPresent();
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        // When
        boolean exists = this.underTest.existsCustomerWithEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = "";
        // When
        boolean exists = this.underTest.existsCustomerWithEmail(email);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        this.underTest.deleteCustomerById(id);

        // Then
        assertThat(this.underTest.selectCustomerById(id)).isNotPresent();
    }

    @Test
    void existsCustomerWithId() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean exists = this.underTest.existsCustomerWithId(id);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        Long id = -1L;

        // When
        boolean exists = this.underTest.existsCustomerWithId(id);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void updateCustomersName() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String updateName = "NewName";

        // When
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(updateName);

        this.underTest.updateCustomer(updatedCustomer);

        // Then
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(updateName);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(age);
        });
    }

    @Test
    void updateCustomersAge() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Integer updateAge = 11;

        // When
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setAge(updateAge);

        this.underTest.updateCustomer(updatedCustomer);

        // Then
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(name);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(updateAge);
        });
    }

    @Test
    void updateCustomersEmail() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String updateEmail = FAKER.internet().emailAddress();

        // When
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail(updateEmail);

        this.underTest.updateCustomer(updatedCustomer);

        // Then
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(name);
            assertThat(c.getEmail()).isEqualTo(updateEmail);
            assertThat(c.getAge()).isEqualTo(age);
        });
    }

    @Test
    void willNotUpdateCustomersWhenNothingToUpdate() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);

        this.underTest.updateCustomer(updatedCustomer);

        // Then
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(name);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getAge()).isEqualTo(age);
        });
    }

    @Test
    void updateCustomersAllProperties() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Customer customer = new Customer(name, email, age);

        this.underTest.saveCustomer(customer);

        Long id = this.underTest.selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String updateName = "NewName";
        String updateEmail = FAKER.internet().emailAddress();
        Integer updateAge = 11;

        // When
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(updateName);
        updatedCustomer.setEmail(updateEmail);
        updatedCustomer.setAge(updateAge);

        this.underTest.updateCustomer(updatedCustomer);

        // Then
        Optional<Customer> actualCustomer = this.underTest.selectCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValue(updatedCustomer);
    }
}