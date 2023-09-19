package mk.ukim.finki.application;

import com.github.javafaker.Faker;
import mk.ukim.finki.application.customer.Customer;
import mk.ukim.finki.application.customer.CustomerRepository;
import mk.ukim.finki.application.enums.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
//            List<Customer> customers = List.of(
//                    new Customer("Alex", "alexdumphy@gmail.com", 22),
//                    new Customer("Haily", "queenh@gmail.com", 25)
//            );

            Faker faker = new Faker();

            String firstName = faker.name().firstName();
            String laseName = faker.name().lastName();
            Gender gender = Math.random() > 0.5 ? Gender.MALE : Gender.FEMALE;

            Customer customer = new Customer(
                    firstName,
                    String.format("%s.%s@app.com", firstName.toLowerCase(), laseName.toLowerCase()),
                    passwordEncoder.encode("password"), faker.number().numberBetween(16, 99),
                    gender);

            customerRepository.save(customer);
            System.out.println(email);
        };

    }
}