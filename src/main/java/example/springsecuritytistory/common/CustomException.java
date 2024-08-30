package example.springsecuritytistory.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public final class CustomException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public CustomException(ExceptionCode code) {
        super(code.message);
        this.code = code.name();
        this.status = code.status;
    }
}
