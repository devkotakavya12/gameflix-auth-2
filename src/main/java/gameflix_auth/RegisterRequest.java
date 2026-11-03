package gameflix_auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank
    @Size(max = 50)
    String username,

    @NotBlank
    @Email
    @Size(max = 254)
    String email,

    @NotBlank
    @Size(min = 8, max = 64)
    @Pattern(
        regexp = "[\\x20-\\x7E]+",
        message = "Password must use printable ASCII characters"
    )
    String password

) {
    @Override
    public String toString() {
        return "RegisterRequest[credentials redacted]";
    }
}
