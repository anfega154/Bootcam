package co.com.anfega.api.helper.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class AccessDeniedExceptionStrategy implements ExceptionStrategy {
    @Override
    public boolean supports(Throwable ex) {
        return ex instanceof java.nio.file.AccessDeniedException;
    }

    @Override
    public HttpStatus getStatus(Throwable ex) {
        return HttpStatus.FORBIDDEN;
    }

    @Override
    public String getError(Throwable ex) {
        return "Access Denied";
    }
}
