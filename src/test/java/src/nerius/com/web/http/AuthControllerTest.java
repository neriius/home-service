package src.nerius.com.web.http;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import src.nerius.com.security.service.AuthService;
import src.nerius.com.security.service.JwtService;
import src.nerius.com.web.http.dto.AuthResponseDto;
import src.nerius.com.web.http.dto.client.UserRegistrationDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // отключает Spring Security фильтры
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void registerUser_success() throws Exception {
        Mockito.when(authService.registerUser(any(UserRegistrationDto.class)))
                .thenReturn(org.springframework.http.ResponseEntity.ok(new AuthResponseDto("jwtToken")));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"login\": \"user1\", \"password\": \"pass123\", \"role\": \"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value("jwtToken"));
    }
}

