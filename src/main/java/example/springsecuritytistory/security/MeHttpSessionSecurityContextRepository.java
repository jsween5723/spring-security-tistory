package example.springsecuritytistory.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

class MeHttpSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request,
        HttpServletResponse response) {
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof Me me) {
            context.setAuthentication(new UsernamePasswordAuthenticationToken(me.toProvider(), null,
                authentication.getAuthorities()));
        }
        super.saveContext(context, request, response);
    }
}
