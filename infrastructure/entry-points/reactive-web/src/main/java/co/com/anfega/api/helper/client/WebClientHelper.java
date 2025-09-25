package co.com.anfega.api.helper.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class WebClientHelper {

    private final WebClient webClient;

    public WebClientHelper(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public <T> Mono<T> get(String url, Map<String, String> headers, ParameterizedTypeReference<T> responseType) {
        return webClient.get()
                .uri(url)
                .headers(httpHeaders -> setHeaders(httpHeaders, headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T, B> Mono<T> post(String url, Map<String, String> headers, B body, ParameterizedTypeReference<T> responseType) {
        return webClient.post()
                .uri(url)
                .headers(httpHeaders -> setHeaders(httpHeaders, headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T, B> Mono<T> put(String url, Map<String, String> headers, B body, ParameterizedTypeReference<T> responseType) {
        return webClient.put()
                .uri(url)
                .headers(httpHeaders -> setHeaders(httpHeaders, headers))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(responseType);
    }

    public <T> Mono<T> delete(String url, Map<String, String> headers, ParameterizedTypeReference<T> responseType) {
        return webClient.delete()
                .uri(url)
                .headers(httpHeaders -> setHeaders(httpHeaders, headers))
                .retrieve()
                .bodyToMono(responseType);
    }

    private void setHeaders(HttpHeaders httpHeaders, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
    }
}