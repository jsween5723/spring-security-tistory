package example.springsecuritytistory.security;

import example.springsecuritytistory.service.Provider;
import java.util.Collection;
import java.util.Set;
import org.springframework.security.core.userdetails.UserDetails;

public record UserWithPassword(Long id, String username, String password,
                               Set<UserRole> authorities) implements UserDetails, Me {

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

    @Override
    public Provider toProvider() {
        return new Provider(id, username, authorities);
    }
}
