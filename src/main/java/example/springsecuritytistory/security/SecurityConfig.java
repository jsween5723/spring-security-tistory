package example.springsecuritytistory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.springsecuritytistory.common.ExceptionResponse;
import example.springsecuritytistory.common.Response;
import example.springsecuritytistory.service.Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final OAuth2Loader oAuth2Loader;
    private final JwtEncoder jwtEncoder;
    @Value("${spring.security.jwt.access.expires-in:3600}")
    private long ACCESS_TOKEN_EXPIRES_IN;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable);
        http.csrf(AbstractHttpConfigurer::disable);
        http.securityContext(
            conf -> conf.securityContextRepository(new MeHttpSessionSecurityContextRepository()));
        http.formLogin(conf -> conf.loginProcessingUrl("/api/v1/auth/login")
                                   .successHandler(this::successHandler)
                                   .failureHandler(this::entryPoint));
        http.oauth2Login(conf -> conf.authorizationEndpoint(end -> end.baseUri("/oauth2/login"))
                                     .userInfoEndpoint(end -> end.userService(oAuth2Loader))
                                     .loginProcessingUrl("/api/v1/oauth2/login/*")
                                     .successHandler(this::successHandler)
                                     .failureHandler(this::entryPoint));
        http.oauth2ResourceServer(conf -> conf.jwt(
            jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverterGenerator())));
        http.exceptionHandling(e -> e.authenticationEntryPoint(this::entryPoint)
                                     .accessDeniedHandler(this::accessDeniedHandler));
        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    private Converter<Jwt, UsernamePasswordAuthenticationToken> jwtAuthenticationConverterGenerator() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        return (Jwt jwtSrc) -> {
            String id = jwtSrc.getSubject();
            String username = jwtSrc.getClaimAsString("username");
            Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(
                jwtSrc);
            Set<UserRole> roles = authorities.stream()
                                             .map(role -> role.getAuthority()
                                                              .replace("ROLE_", ""))
                                             .map(UserRole::valueOf)
                                             .collect(Collectors.toSet());
            Provider provider = new Provider(Long.parseLong(id), username, roles);
            return new UsernamePasswordAuthenticationToken(provider, null, authorities);
        };
    }

    private void successHandler(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof Me me) {
            sendResponse(response, new JwtResponse(me.toProvider()));
            return;
        }
        sendResponse(response, "로그인에 성공했습니다.");
    }

    @Getter
    private final class JwtResponse {

        private final OAuth2AccessToken accessToken;

        private JwtResponse(Provider provider) {
            Instant issuedAt = Instant.now();
            accessToken = toAccessToken(provider, issuedAt, ACCESS_TOKEN_EXPIRES_IN);
        }

        private OAuth2AccessToken toAccessToken(Provider provider, Instant issuedAt,
            long expiresIn) {
            Instant expiredAt = issuedAt.plus(Duration.ofSeconds(expiresIn));
            JwtClaimsSet claims = JwtClaimsSet.builder()
                                              .claims((claim) -> claim.putAll(provider.toClaims()))
                                              .expiresAt(expiredAt)
                                              .issuedAt(issuedAt)
                                              .build();
            Jwt encode = jwtEncoder.encode(JwtEncoderParameters.from(claims));
            return new OAuth2AccessToken(TokenType.BEARER, encode.getTokenValue(), issuedAt,
                expiredAt);
        }
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
