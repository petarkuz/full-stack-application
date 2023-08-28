package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.AbstractTestcontainers;
import mk.ukim.finki.application.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void existsCustomerByEmail() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Gender gender = Gender.MALE;
        Customer customer = new Customer(name, email, age, gender);

        this.underTest.save(customer);

        // When
        boolean exists = this.underTest.existsCustomerByEmail(email);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // Given
        String email = "";
        // When
        boolean exists = this.underTest.existsCustomerByEmail(email);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void existsCustomerById() {
        // Given
        String email = FAKER.internet().emailAddress();
        String name = FAKER.name().firstName();
        Integer age = FAKER.random().nextInt(16, 99);
        Gender gender = Gender.MALE;
        Customer customer = new Customer(name, email, age, gender);

        this.underTest.save(customer);

        Long id = this.underTest.findAll()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        boolean exists = this.underTest.existsCustomerById(id);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        Long id = -1L;

        // When
        boolean exists = this.underTest.existsCustomerById(id);

        // Then
        assertThat(exists).isFalse();
    }
}