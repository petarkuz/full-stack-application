package mk.ukim.finki.application.customer;

import mk.ukim.finki.application.enums.Gender;

import java.util.List;

public record CustomerDTO (
        Long id,
        String name,
        String email,
        Gender gender,
        Integer age,
        List<String> roles,
        String username
) {
}
