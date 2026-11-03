package gameflix_auth;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;

    public AuthController(
            RegistrationService registrationService,
            LoginService loginService) {
        this.registrationService = registrationService;
        this.loginService = loginService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfile> register(
            @Valid @RequestBody RegisterRequest request) {

        UserProfile profile = registrationService.register(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(profile);
    }

    @PostMapping("/login")
    public TokenResponse login(
            @RequestBody LoginRequest request) {
        return loginService.login(request);
    }
}
