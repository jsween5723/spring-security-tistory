package example.springsecuritytistory.common;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExceptionCode {
    INTERNAL_ERROR("서버 내부 오류", HttpStatus.INTERNAL_SERVER_ERROR);
    public final String message;
    public final HttpStatus status;
}
