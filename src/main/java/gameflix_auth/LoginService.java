package gameflix_auth;

import java.util.Locale;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyHash;

    public LoginService(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyHash = passwordEncoder.encode(
            UUID.randomUUID().toString()
        );
    }

    public TokenResponse login(LoginRequest request) {
        String password = request.password();

        if (request.email() == null
                || request.email().isBlank()
                || request.email().length() > 254
                || password == null
                || password.length() < 8
                || password.length() > 64
                || !password.matches("[\\x20-\\x7E]+")) {
            throw new InvalidCredentialsException();
        }

        String email = request.email()
            .strip()
            .toLowerCase(Locale.ROOT);

        User user = users.findByEmail(email).orElse(null);

        String hash = user == null
            ? dummyHash
            : user.getPasswordHash();

        boolean passwordMatches = passwordEncoder.matches(
            password,
            hash
        );

        if (user == null || !passwordMatches) {
            throw new InvalidCredentialsException();
        }

        return jwtService.issue(user);
    }
}
