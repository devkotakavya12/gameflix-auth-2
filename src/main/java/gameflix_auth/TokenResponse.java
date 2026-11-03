package gameflix_auth;

public record TokenResponse(
    String token,
    String tokenType,
    long expiresIn
) {
}
