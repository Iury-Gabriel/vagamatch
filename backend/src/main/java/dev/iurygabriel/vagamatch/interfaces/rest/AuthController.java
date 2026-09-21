package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.identity.application.AuthenticateUseCase;
import dev.iurygabriel.vagamatch.identity.application.RegisterUserUseCase;
import dev.iurygabriel.vagamatch.interfaces.rest.dto.AuthDtos.AuthResponse;
import dev.iurygabriel.vagamatch.interfaces.rest.dto.AuthDtos.LoginRequest;
import dev.iurygabriel.vagamatch.interfaces.rest.dto.AuthDtos.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserUseCase registerUser;
    private final AuthenticateUseCase authenticate;

    public AuthController(RegisterUserUseCase registerUser, AuthenticateUseCase authenticate) {
        this.registerUser = registerUser;
        this.authenticate = authenticate;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return AuthResponse.from(registerUser.register(request.email(), request.password()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return AuthResponse.from(authenticate.login(request.email(), request.password()));
    }
}
