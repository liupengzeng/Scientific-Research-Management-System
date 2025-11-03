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

    /**
     * 返回成功结果（带消息）
     *
     * @param msg 消息
     * @return AjaxResult
     */
    public static <T> AjaxResult<T> success(String msg) {
        return new AjaxResult<>(200, msg, null);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return this.code == 200;
    }

    /**
     * 返回分页成功结果
     *
     * @param pageResult 分页结果
     * @param <T>        数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult<PageResult<T>> page(PageResult<T> pageResult) {
        return success("查询成功", pageResult);
    }
}