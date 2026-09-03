package com.shopkart.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(
            description = "HTTP status code",
            example = "200"
    )
    private int status;

    @Schema(
            description = "Response message",
            example = "Product created successfully"
    )
    private String message;

    @Schema(description = "Response data")
    private T data;

    public ApiResponse(
            int status,
            String message,
            T data) {

        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}