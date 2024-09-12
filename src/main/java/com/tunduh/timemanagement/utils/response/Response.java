package com.tunduh.timemanagement.utils.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Response {
    public static <T> ResponseEntity<?> renderJSON(T data, String message, HttpStatus httpStatus) {
        WebResponse<?> response = WebResponse.builder()
                .message(message)
                .status(httpStatus.getReasonPhrase())
                .data(data)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }

    public static <T> ResponseEntity<?> renderJSON(T data, String message) {
        return renderJSON(data, message, HttpStatus.OK);
    }

    public static <T> ResponseEntity<?> renderJSON(T data) {
        return renderJSON(data, "Success");
    }

    public static <T> ResponseEntity<?> renderJSON(String message) {
        return renderJSON(message);
    }
}

