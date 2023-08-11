package mk.ukim.finki.application;

import mk.ukim.finki.application.customer.Customer;
import mk.ukim.finki.application.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            List<Customer> customers = List.of(
                    new Customer("Alex", "alexdumphy@gmail.com", 22),
                    new Customer("Haily", "queenh@gmail.com", 25)
            );

            customerRepository.saveAll(customers);
        };

    }
}
