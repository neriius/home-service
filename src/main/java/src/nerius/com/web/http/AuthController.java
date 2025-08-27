package src.nerius.com.web.http;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import src.nerius.com.web.http.dto.AuthResponseDto;
import src.nerius.com.web.http.dto.device.DeviceLoginDto;
import src.nerius.com.web.http.dto.client.UserLoginDto;
import src.nerius.com.web.http.dto.client.UserRegistrationDto;
import src.nerius.com.security.service.AuthService;

@Log4j2
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody UserRegistrationDto dto) {
        return authService.registerUser(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody UserLoginDto dto) {
        return authService.loginUser(dto);
    }

    @PostMapping("/loginDevice")
    public ResponseEntity<AuthResponseDto> loginDevice(@RequestBody DeviceLoginDto dto) {
        log.debug("Device connecting: {}", dto.toString());
        return authService.loginDevice(dto);
    }
}
