package src.nerius.com.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import src.nerius.com.db.controllers.UserRepositoryController;
import src.nerius.com.db.entity.User;
import src.nerius.com.web.http.dto.AuthResponseDto;
import src.nerius.com.web.http.dto.client.UserLoginDto;
import src.nerius.com.web.http.dto.client.UserRegistrationDto;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private UserRepositoryController userRepositoryController;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        userRepositoryController = mock(UserRepositoryController.class);
        authService = new AuthService(passwordEncoder, jwtService, userRepositoryController);
    }

    @Test
    void registerUser_success() {
        UserRegistrationDto dto = new UserRegistrationDto("newUser", "pass123", User.UserRoles.ADMIN);

        when(userRepositoryController.existsByLogin(dto.login())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hashedPassword");
        when(jwtService.generateToken(null, "USER", dto.login())).thenReturn("jwtToken");

        ResponseEntity<AuthResponseDto> response = authService.registerUser(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().jwtToken()).isEqualTo("jwtToken");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryController).createUser(userCaptor.capture());
        assertThat(userCaptor.getValue().getLogin()).isEqualTo("newUser");
        assertThat(userCaptor.getValue().getPassword()).isEqualTo("hashedPassword");
        assertThat(userCaptor.getValue().getRoles()).isEqualTo(User.UserRoles.ADMIN);
    }

    @Test
    void registerUser_conflict() {
        UserRegistrationDto dto = new UserRegistrationDto("existing", "pass123", User.UserRoles.ADMIN);

        when(userRepositoryController.existsByLogin(dto.login())).thenReturn(true);

        ResponseEntity<AuthResponseDto> response = authService.registerUser(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(409);
        assertThat(response.getBody().jwtToken()).contains("уже существует");
        verify(userRepositoryController, never()).createUser(any());
    }

    @Test
    void loginUser_success() {
        UserLoginDto dto = new UserLoginDto("user1", "pass123");
        User mockUser = new User();
        mockUser.setLogin("user1");
        mockUser.setPassword("hashed");

        when(userRepositoryController.getUserByLogin(dto.login())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(dto.password(), mockUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(null, "USER", dto.login())).thenReturn("jwtToken");

        ResponseEntity<AuthResponseDto> response = authService.loginUser(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().jwtToken()).isEqualTo("jwtToken");
    }

    @Test
    void loginUser_invalidPassword() {
        UserLoginDto dto = new UserLoginDto("user1", "wrong");
        User mockUser = new User();
        mockUser.setLogin("user1");
        mockUser.setPassword("1234");
        mockUser.setRoles(User.UserRoles.ADMIN);
        when(userRepositoryController.getUserByLogin(dto.login())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(dto.password(), mockUser.getPassword())).thenReturn(false);

        ResponseEntity<AuthResponseDto> response = authService.loginUser(dto);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
    }
}
