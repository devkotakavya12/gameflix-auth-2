package gameflix_auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/me")
    public UserProfile me(@AuthenticationPrincipal Jwt jwt) {
        Integer userId;

        try {
            userId = Integer.valueOf(jwt.getSubject());
        } catch (NumberFormatException exception) {
            throw new InvalidCredentialsException();
        }

        User user = users.findById(userId)
            .orElseThrow(InvalidCredentialsException::new);

        return UserProfile.from(user);
    }
}

