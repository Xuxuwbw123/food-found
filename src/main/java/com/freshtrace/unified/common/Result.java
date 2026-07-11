package com.freshtrace.unified.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success() { return success(null); }
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200); r.setMessage("操作成功"); r.setData(data); return r;
    }
    public static <T> Result<T> success(String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(200); r.setMessage(msg); r.setData(data); return r;
    }
    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500); r.setMessage(msg); return r;
    }
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code); r.setMessage(msg); return r;
    }

    // Aliases for food-found compatibility
    public static <T> Result<T> ok(T data) { return success(data); }
    public static <T> Result<T> ok() { return success(); }
    public static <T> Result<T> fail(String msg) { return error(msg); }
    public static <T> Result<T> fail(int code, String msg) { return error(code, msg); }
}
