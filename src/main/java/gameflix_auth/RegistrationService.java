package gameflix_auth;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository users,
            PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public UserProfile register(RegisterRequest request) {
        String email = request.email()
            .strip()
            .toLowerCase(Locale.ROOT);

        String passwordHash = passwordEncoder.encode(
            request.password()
        );

        User user = new User(
            request.username().strip(),
            email,
            passwordHash
        );

        return UserProfile.from(users.saveAndFlush(user));
    }
}
