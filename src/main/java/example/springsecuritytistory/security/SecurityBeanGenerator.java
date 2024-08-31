package example.springsecuritytistory.security;

import example.springsecuritytistory.repository.UserEntity;
import example.springsecuritytistory.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeanGenerator {
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            UserEntity userEntity = userRepository.findByUsername(username)
                                                  .orElseThrow(() -> new RuntimeException(
                                                      "로그인 정보가 존재하지 않습니다."));
            return new UserWithPassword(userEntity.getId(), userEntity.getUsername(),
                userEntity.getPassword(), userEntity.getRoles());
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
