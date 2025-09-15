package src.nerius.com.security.service;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import src.nerius.com.db.controllers.UserRepositoryController;
import src.nerius.com.db.entity.User;
import src.nerius.com.web.http.dto.AuthResponseDto;
import src.nerius.com.web.http.dto.device.DeviceLoginDto;
import src.nerius.com.web.http.dto.client.UserLoginDto;
import src.nerius.com.web.http.dto.client.UserRegistrationDto;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepositoryController userRepositoryController;

    public AuthService(PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserRepositoryController userRepositoryController) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepositoryController = userRepositoryController;
    }


    public ResponseEntity<AuthResponseDto> registerUser(UserRegistrationDto dto) {
        if (userRepositoryController.existsByLogin(dto.login())) {
            return ResponseEntity
                    .status(AuthStatus.CONFLICT.getCode())
                    .body(new AuthResponseDto("Пользователь с таким логином уже существует"));
        }

        User user = new User();
        user.setLogin(dto.login());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(dto.role());

        userRepositoryController.createUser(user);

        String token = jwtService.generateClientToken(user.getLogin());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    public ResponseEntity<AuthResponseDto> loginUser(UserLoginDto dto) {
        User user = userRepositoryController.getUserByLogin(dto.login())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(dto.password(), user.getPassword())) {
            return ResponseEntity
                    .status(AuthStatus.UNAUTHORIZED.getCode())
                    .body(new AuthResponseDto(""));
        }

        String token = jwtService.generateClientToken(user.getLogin());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    public ResponseEntity<AuthResponseDto> loginDevice(DeviceLoginDto dto) {
        String token = jwtService.generateDeviceToken(dto.deviceAuthKey());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @Getter
    public enum AuthStatus {
        OK(200),
        UNAUTHORIZED(401),
        CONFLICT(409);

        private final int code;

        AuthStatus(int code) {
            this.code = code;
        }
    }

}


