package example.springsecuritytistory.common;

public record Response<T>(boolean success, T body) {
    public Response(T body){
        this(body.getClass() != ExceptionResponse.class, body);
    }
}
