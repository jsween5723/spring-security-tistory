package example.springsecuritytistory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.springsecuritytistory.common.ExceptionResponse;
import example.springsecuritytistory.common.Response;
import example.springsecuritytistory.repository.UserEntity;
import example.springsecuritytistory.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable).formLogin(
                conf -> conf.loginProcessingUrl("/api/v1/auth/login").successHandler(
                    ((request, response, authentication) -> sendResponse(response, "로그인 성공")))
                    .failureHandler(this::entryPoint))
            .exceptionHandling(e -> e.authenticationEntryPoint(this::entryPoint)
                .accessDeniedHandler(this::accessDeniedHandler));
        return http.build();
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
        response.getWriter().write(bodyString);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 존재하지 않습니다."));
            return new UserWithPassword(userEntity.getUsername(), userEntity.getPassword(),
                userEntity.getRoles());
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
