package co.com.anfega.api.helper.client;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private String message;
    private T content;
}
