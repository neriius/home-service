package src.nerius.com.web.http.dto.client;

import src.nerius.com.db.entity.User;

public record UserRegistrationDto(String login, String password, User.UserRoles role) {
}
