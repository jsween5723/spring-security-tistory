package example.springsecuritytistory.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping("authentication")
    public Authentication getMe(Authentication authentication) {
        return authentication;
    }

    @GetMapping("principal")
    public Object getPrincipal(@AuthenticationPrincipal Object principal) {
        return principal;
    }
}
