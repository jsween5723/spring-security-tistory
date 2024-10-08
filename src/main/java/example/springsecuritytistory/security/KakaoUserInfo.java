package example.springsecuritytistory.security;

import com.fasterxml.jackson.annotation.JsonProperty;

record KakaoUserInfo(Long id, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) {
    record KakaoAccount(Profile profile, String name, String email, @JsonProperty("phone_number") String phoneNumber, String ci) {

        record Profile(String nickname, @JsonProperty("thumbnail_image_url") String thumbnail_image_url,
                       @JsonProperty("profile_image_url") String profileImageUrl) {

        }
    }
}

