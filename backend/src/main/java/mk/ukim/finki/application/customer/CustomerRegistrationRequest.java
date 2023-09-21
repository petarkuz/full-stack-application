package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;

public record CustomerRegistrationRequest (
        String name,
        String email,
        String password,
        Integer age,
        Gender gender
) {
}
