package example.springsecuritytistory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.springsecuritytistory.common.ExceptionResponse;
import example.springsecuritytistory.common.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final OAuth2Loader oAuth2Loader;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(conf -> conf.loginProcessingUrl("/api/v1/auth/login")
                                   .successHandler(this::successHandler)
                                   .failureHandler(this::entryPoint));
        http.oauth2Login(conf -> conf.authorizationEndpoint(end -> end.baseUri("/oauth2/login"))
                                     .userInfoEndpoint(end -> end.userService(oAuth2Loader))
                                     .loginProcessingUrl("/api/v1/oauth2/login/*")
                                     .successHandler(this::successHandler)
                                     .failureHandler(this::entryPoint));
        http.exceptionHandling(e -> e.authenticationEntryPoint(this::entryPoint)
                                     .accessDeniedHandler(this::accessDeniedHandler));
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    private void successHandler(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Me me) {
            SecurityContextHolder.getContext()
                                 .setAuthentication(
                                     new UsernamePasswordAuthenticationToken(me.toProvider(), null,
                                         authentication.getAuthorities()));
        }
        sendResponse(response, "로그인 성공");
    }

    private void entryPoint(HttpServletRequest request, HttpServletResponse response,
        RuntimeException e) throws IOException {
        response.setStatus(401);
        sendResponse(response, ExceptionResponse.of(e));
    }

    private void accessDeniedHandler(HttpServletRequest request, HttpServletResponse response,
        RuntimeException e) throws IOException {
        response.setStatus(403);
        sendResponse(response, ExceptionResponse.of(e));
    }

    private <T> void sendResponse(HttpServletResponse response, T e) throws IOException {
        Response<T> body = new Response<>(e);
        String bodyString = objectMapper.writeValueAsString(body);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
                .write(bodyString);
    }

}
