package example.springsecuritytistory.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.springsecuritytistory.repository.OAuth2UserEntity;
import example.springsecuritytistory.repository.OAuth2UserRepository;
import example.springsecuritytistory.service.UserManager;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
class OAuth2Loader implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final ObjectMapper objectMapper;
    private final DefaultOAuth2UserService service = new DefaultOAuth2UserService();
    private final UserManager userManager;
    private final OAuth2UserRepository repository;


    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = service.loadUser(userRequest)
                                                .getAttributes();
        GrantedOAuth2User oAuthMember = toOAuth2User(userRequest.getClientRegistration()
                                                                .getClientName(), attributes);
        Long userId = userManager.getIdByUsername(oAuthMember.getUsername());
        if (userId == null) {
            userId = userManager.join(oAuthMember.toJoinUser());
        }
        if (!repository.existsByProviderIdAndProviderName(oAuthMember.getProviderId(),
            oAuthMember.getProviderName())) {
            repository.save(new OAuth2UserEntity(oAuthMember, userId));
        }
        userManager.renewInformation(userId, oAuthMember.toInformation());
        oAuthMember.assignUserId(userId);
        return oAuthMember;
    }

    private GrantedOAuth2User toOAuth2User(String clientName, Map<String, Object> attributes) {
        switch (clientName) {
            case "kakao" -> {
                KakaoUserInfo userInfo = objectMapper.convertValue(attributes, KakaoUserInfo.class);
                return new GrantedOAuth2User(userInfo, attributes);
            }
            default -> throw new RuntimeException("지원하지 않는 간편로그인입니다.");
        }
    }
}