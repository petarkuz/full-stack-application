package mk.ukim.finki.application.journey;

import com.github.javafaker.Faker;
import mk.ukim.finki.application.customer.CustomerDTO;
import mk.ukim.finki.application.customer.CustomerRegistrationRequest;
import mk.ukim.finki.application.customer.CustomerUpdateRequest;
import mk.ukim.finki.application.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    private static final String CUSTOMER_PATH = "/api/v1/customers";
    private final Faker faker = new Faker();

    @Test
    void canSaveACustomer() {
        String name = this.faker.name().firstName();
        String email = this.faker.internet().emailAddress();
        int age = this.faker.number().numberBetween(16, 99);
        Gender gender = Math.random() > 0.5 ? Gender.MALE : Gender.FEMALE;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, "password", age, gender
        );

        String jwt = this.webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        List<CustomerDTO> responseBody = this.webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        Long id = responseBody.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        CustomerDTO expectedCustomer = new CustomerDTO(
                id, name, email, gender,  age, List.of("ROLE_USER"), email);

        assertThat(responseBody)
                //.usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        this.webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteACustomer() {
        String name = this.faker.name().firstName();
        String email = this.faker.internet().emailAddress();
        int age = this.faker.number().numberBetween(16, 99);
        Gender gender = Math.random() > 0.5 ? Gender.MALE : Gender.FEMALE;
        String password = "password";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );
        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                name, email + ".mk", password, age, gender
        );//Because after we delete the first customer, his token will be lost, and we will not be able to perform get request. From there we will get 403 instead of 404.

        this.webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        String jwt = this.webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        List<CustomerDTO> responseBody = this.webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        Long id = responseBody.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        this.webTestClient.delete()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk();

        this.webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateACustomer() {
        String name = this.faker.name().firstName();
        String email = this.faker.internet().emailAddress();
        int age = this.faker.number().numberBetween(16, 99);
        Gender gender = Math.random() > 0.5 ? Gender.MALE : Gender.FEMALE;
        String password = "password";
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        String jwt = this.webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(HttpHeaders.AUTHORIZATION)
                .get(0);

        List<CustomerDTO> responseBody = this.webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {})
                .returnResult()
                .getResponseBody();

        Long id = responseBody.stream()
                .filter(c -> c.email().equals(email))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        String updatedName = faker.name().firstName();
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                updatedName, null, null, null
        );

        this.webTestClient.put()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk();

        CustomerDTO updatedCustomer = this.webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(jwt))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CustomerDTO.class)
                .returnResult()
                .getResponseBody();

        CustomerDTO expectedCustomer = new CustomerDTO(
                id, updatedName, email, gender, age, List.of("ROLE_USER"), email);

        assertThat(updatedCustomer)
                .isEqualTo(expectedCustomer);
    }
}
