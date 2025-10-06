package co.com.anfega.api.helper.error;

import org.springframework.http.HttpStatus;

public interface ExceptionStrategy {
    boolean supports(Throwable ex);
    HttpStatus getStatus(Throwable ex);
    String getError(Throwable ex);
}
