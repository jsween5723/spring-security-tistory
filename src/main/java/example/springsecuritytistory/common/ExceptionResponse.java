package example.springsecuritytistory.common;

public record ExceptionResponse(String code, String message) {
    public static ExceptionResponse of(RuntimeException e) {
        return new ExceptionResponse(ExceptionCode.INTERNAL_ERROR.name(), e.getLocalizedMessage());
    }


    public static ExceptionResponse of(CustomException e) {
        return new ExceptionResponse(e.getCode(), e.getLocalizedMessage());
    }
}
