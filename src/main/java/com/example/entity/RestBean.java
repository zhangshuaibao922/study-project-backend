package com.example.entity;

import com.example.constants.SystemConstants;
import lombok.Data;

@Data
public class RestBean<T> {
    private int status;
    private boolean success;
    private T message;

    public RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static <T> RestBean<T> success() {
        return new RestBean<>(SystemConstants.SUCCESS, true, null);
    }

    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(SystemConstants.SUCCESS, true, data);
    }

    public static <T> RestBean<T> failure(int startus) {
        return new RestBean<>(startus,false,null);
    }
    public static <T> RestBean<T> failure(int startus,T data) {
        return new RestBean<>(startus,false,data);
    }
}
