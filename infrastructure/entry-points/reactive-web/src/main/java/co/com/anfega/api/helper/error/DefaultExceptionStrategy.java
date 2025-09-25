package co.com.anfega.api.helper.error;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DefaultExceptionStrategy implements ExceptionStrategy {
    @Override
    public boolean supports(Throwable ex) {
        return true;
    }

    @Override
    public HttpStatus getStatus(Throwable ex) {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getError(Throwable ex) {
        return "Ha ocurrido un error inesperado: " + ex.getMessage();
    }
}
