package example.springsecuritytistory.controller;

import example.springsecuritytistory.common.Response;
import example.springsecuritytistory.service.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserManager userManager;
    @GetMapping("authentication")
    public Authentication getMe(Authentication authentication) {
        return authentication;
    }

    @GetMapping("principal")
    public Object getPrincipal(@AuthenticationPrincipal Object principal) {
        return principal;
    }

    @PostMapping
    public Response<String> join(@RequestBody UserJoinRequest request) {
        long userId = userManager.join(request.toJoinUser());
        userManager.renewInformation(userId, request.toInformation());
        return new Response<>("가입 성공");
    }
}
