package com.week2.exception;

/**
 * 自定义业务异常 —— 公司项目里每个业务模块基本都有一个。
 *
 * <p>【继承谁】继承 RuntimeException（非受检），不继承 Exception（受检）。
 * 原因：业务异常会从 Service 一路冒泡到 Controller，如果定义成受检异常，
 * 中间每一层都要写 throws，代码会到处是噪音。定义成非受检，想抛就抛，
 * 最后在全局异常处理器里统一兜住。
 *
 * <p>【你项目里的对应】{@code BizException}，配合 {@code @RestControllerAdvice} 使用：
 * <pre>
 * &#64;ExceptionHandler(BizException.class)
 * public Result&lt;Void&gt; handle(BizException e) {
 *     return Result.fail(e.getCode(), e.getMessage());
 * }
 * </pre>
 *
 * <p>【serialVersionUID 是干什么的】Java 序列化时的版本号。
 * 虽然非受检异常很少被序列化，但 IDE 会警告，实际项目里都习惯加上。
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 业务错误码，前端靠它决定弹什么提示 */
    private final int code;

    public BizException(int code, String message) {
        // 【super(message)】把消息传给父类 RuntimeException，
        // 之后调 e.getMessage() 就能拿到
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
