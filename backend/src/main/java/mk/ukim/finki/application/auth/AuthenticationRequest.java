package mk.ukim.finki.application.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
