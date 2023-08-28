package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age,
        Gender gender
) {
}
