package example.springsecuritytistory.service;

import example.springsecuritytistory.security.UserRole;
import java.util.Set;

public record Provider(long userId, String username, Set<UserRole> roles) {

}
