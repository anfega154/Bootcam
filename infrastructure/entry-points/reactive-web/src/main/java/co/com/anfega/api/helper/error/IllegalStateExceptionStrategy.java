package co.com.anfega.api.helper.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class IllegalStateExceptionStrategy implements ExceptionStrategy {
    @Override
    public boolean supports(Throwable ex) {
        return ex instanceof IllegalStateException;
    }

    @Override
    public HttpStatus getStatus(Throwable ex) {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getError(Throwable ex) {
        return "Conflict";
    }
}
