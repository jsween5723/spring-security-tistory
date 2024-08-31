package example.springsecuritytistory.repository;

import example.springsecuritytistory.security.GrantedOAuth2User;
import example.springsecuritytistory.security.OAuth2Provider;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2UserEntity extends BaseEntity {
    @Id @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private OAuth2Provider providerName;
    private String providerId;
    @JoinColumn(name = "userId")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public OAuth2UserEntity(GrantedOAuth2User grantedOAuth2User, Long userId) {
        providerName = grantedOAuth2User.getProviderName();
        providerId = grantedOAuth2User.getProviderId();
        user = new UserEntity(userId);
    }
}
