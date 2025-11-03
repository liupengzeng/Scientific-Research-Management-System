package com.university.research.common.core;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AjaxResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;

    public static <T> AjaxResult<T> success() {
        return (AjaxResult<T>) success("操作成功");
    }



    public static <T> AjaxResult<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> AjaxResult<T> success(String msg, T data) {
        return new AjaxResult<>(200, msg, data);
    }

    public static <T> AjaxResult<T> error() {
        return error("操作失败");
    }

    public static <T> AjaxResult<T> error(String msg) {
        return error(500, msg);
    }

    public static <T> AjaxResult<T> error(int code, String msg) {
        return new AjaxResult<>(code, msg, null);
    }

    // 构造函数、getter、setter...

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


}