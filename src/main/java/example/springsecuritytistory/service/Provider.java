package example.springsecuritytistory.service;

import example.springsecuritytistory.security.UserRole;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.util.Strings;

public record Provider(long userId, String username, Set<UserRole> roles) {
    public Map<String, Object> toClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("sub", String.valueOf(userId));
        claims.put("authorities", Strings.join(roles, ' '));
        return claims;
    }
}
