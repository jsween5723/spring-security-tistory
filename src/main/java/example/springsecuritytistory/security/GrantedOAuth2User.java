package example.springsecuritytistory.security;

import example.springsecuritytistory.security.KakaoUserInfo.KakaoAccount;
import example.springsecuritytistory.service.JoinUser;
import example.springsecuritytistory.service.Provider;
import example.springsecuritytistory.service.UserInformation;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public final class GrantedOAuth2User implements OAuth2User, Me {

    private final OAuth2Provider providerName;
    private final String providerId;
    private Long userId;
    private String username;
    private String name;
    private final Map<String, Object> attributes;
    private final Set<UserRole> authorities;

    GrantedOAuth2User(KakaoUserInfo kakaoUserInfo, Map<String, Object> attributes) {
        this.providerName = OAuth2Provider.kakao;
        this.providerId = kakaoUserInfo.id().toString();
        this.userId = null;
        if (kakaoUserInfo.kakaoAccount() != null) {
            KakaoAccount account = kakaoUserInfo.kakaoAccount();
            this.username = account.email();
            this.name = account.name();
        }
        this.attributes = attributes;
        this.authorities = Set.of(UserRole.USER);
    }

    JoinUser toJoinUser() {
        return new JoinUser(username, UUID.randomUUID().toString());
    }

    UserInformation toInformation() {
        return new UserInformation(name);
    }
    void assignUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public Provider toProvider() {
        return new Provider(userId, username, authorities);
    }
}
