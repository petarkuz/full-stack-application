package mk.ukim.finki.application.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
