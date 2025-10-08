package co.com.anfega.consumer;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private String message;
    private T content;
}
