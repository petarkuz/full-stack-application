package mk.ukim.finki.application.journey;

import com.github.javafaker.Faker;
import mk.ukim.finki.application.auth.AuthenticationRequest;
import mk.ukim.finki.application.auth.AuthenticationResponse;
import mk.ukim.finki.application.customer.CustomerDTO;
import mk.ukim.finki.application.customer.CustomerRegistrationRequest;
import mk.ukim.finki.application.enums.Gender;
import mk.ukim.finki.application.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    private final Faker faker = new Faker();

    private static final String CUSTOMER_PATH = "/api/v1/customers";
    private static final String AUTHENTICATION_PATH = "/api/v1/auth/login";

    @Test
    void canLoginACustomer() {
        String name = this.faker.name().firstName();
        String email = this.faker.internet().emailAddress();
        int age = this.faker.number().numberBetween(16, 99);
        Gender gender = Math.random() > 0.5 ? Gender.MALE : Gender.FEMALE;
        String password = "password";

        CustomerRegistrationRequest customerRequest = new CustomerRegistrationRequest(
                name, email, password, age, gender
        );

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(email, password);

        this.webTestClient.post()
                .uri(AUTHENTICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        this.webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = this.webTestClient.post()
                .uri(AUTHENTICATION_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        String jwt = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        AuthenticationResponse responseBody = result.getResponseBody();
        CustomerDTO customerDTO = responseBody.customerDTO();

        assertThat(this.jwtUtil.isValidToken(jwt, customerDTO.username()));

        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }


}
