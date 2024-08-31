package example.springsecuritytistory.repository;

import example.springsecuritytistory.security.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2UserRepository extends JpaRepository<OAuth2UserEntity, Long> {
    boolean existsByProviderIdAndProviderName(String providerId, OAuth2Provider providerName);
}
