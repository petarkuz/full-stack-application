package mk.ukim.finki.application.customer;

public record CustomerRegistrationRequest (
    String name,
    String email,
    Integer age
) {

}
