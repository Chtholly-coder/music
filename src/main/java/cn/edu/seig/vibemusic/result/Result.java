package cn.edu.seig.vibemusic.result;

/**
 * 统一响应结果类
 * 用于封装API返回数据
 * @param <T> 响应数据类型
 */
public class Result<T> {

    /**
     * 业务状态码：0-成功，1-失败
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    // 构造方法
    public Result() {}

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 返回成功结果（带数据）
     * @param data 响应数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "操作成功", data);
    }

    /**
     * 返回成功结果（无数据）
     * @return Result对象
     */
    public static Result<Void> success() {
        return new Result<>(0, "操作成功", null);
    }

    /**
     * 返回成功结果（带自定义消息和数据）
     * @param message 提示信息
     * @param data 响应数据
     * @return Result对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);
    }

    /**
     * 返回成功结果（带自定义消息）
     * @param message 提示信息
     * @return Result对象
     */
    public static Result<Void> success(String message) {
        return new Result<>(0, message, null);
    }

    /**
     * 返回失败结果（默认消息）
     * @return Result对象
     */
    public static Result<Void> error() {
        return new Result<>(1, "操作失败", null);
    }

    /**
     * 返回失败结果（自定义消息）
     * @param message 错误信息
     * @return Result对象
     */
    public static Result<Void> error(String message) {
        return new Result<>(1, message, null);
    }

    // Getter和Setter方法
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}


