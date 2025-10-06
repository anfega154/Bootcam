package co.com.anfega.api.helper.api;

public class LocalApiResponse<T> {
    private String message;
    private T content;

    public LocalApiResponse(String message, T content) {
        this.message = message;
        this.content = content;
    }

    public LocalApiResponse(String message) {
        this.message = message;
    }

    public String getmessage() { return message; }
    public T getcontent() { return content; }
}