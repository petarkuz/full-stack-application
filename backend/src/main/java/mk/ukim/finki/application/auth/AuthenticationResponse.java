package mk.ukim.finki.application.auth;

import mk.ukim.finki.application.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
        ) {
}
