package dev.iurygabriel.vagamatch.interfaces.rest;

import dev.iurygabriel.vagamatch.identity.application.AuthResult;
import dev.iurygabriel.vagamatch.identity.application.AuthenticateUseCase;
import dev.iurygabriel.vagamatch.identity.application.RegisterUserUseCase;
import dev.iurygabriel.vagamatch.identity.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController (camada web)")
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RegisterUserUseCase registerUser;

    @MockitoBean
    private AuthenticateUseCase authenticate;

    @Test
    @DisplayName("POST /api/auth/register devolve token")
    void register() throws Exception {
        when(registerUser.register(any(), any()))
                .thenReturn(new AuthResult("jwt-token", "iury@x.com", Role.USER));

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"iury@x.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/login devolve token e papel")
    void login() throws Exception {
        when(authenticate.login(any(), any()))
                .thenReturn(new AuthResult("jwt-admin", "admin@vagamatch.dev", Role.ADMIN));

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"admin@vagamatch.dev\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }
}
