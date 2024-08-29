package example.springsecuritytistory.security;

import java.util.Collection;
import org.springframework.security.core.userdetails.UserDetails;

public record UserWithPassword(String username, String password, Collection<UserRole> authorities) implements
    UserDetails {

    @Override
    public Collection<UserRole> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
