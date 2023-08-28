package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;

public record CustomerRegistrationRequest (
    String name,
    String email,
    Integer age,
    Gender gender
) {

}
