package src.nerius.com.web.websocket;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import src.nerius.com.security.service.JwtService;

import java.net.URI;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // Получаем токен из query параметров URL
        URI uri = request.getURI();
        String query = uri.getQuery(); // например token=JWT_ТОКЕН
        if (query == null || !query.contains("token=")) {
            return false; // токен не найден
        }

        String token = null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring("token=".length());
                break;
            }
        }

        if (token == null || token.isEmpty()) {
            return false; // токен пустой
        }

        try {
            Claims claims = jwtService.parseToken(token);
            attributes.put("claims", claims);
            return true;
        } catch (Exception e) {
            return false; // токен неверный
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
