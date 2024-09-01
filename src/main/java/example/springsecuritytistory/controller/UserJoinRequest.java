package example.springsecuritytistory.controller;

import example.springsecuritytistory.service.JoinUser;
import example.springsecuritytistory.service.UserInformation;

public record UserJoinRequest(String username, String name, String password) {
    UserInformation toInformation() {
        return new UserInformation(name);
    }

    JoinUser toJoinUser() {
        return new JoinUser(username, password);
    }
}
