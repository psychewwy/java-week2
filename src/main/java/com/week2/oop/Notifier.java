package com.week2.oop;

/**
 * 通知渠道接口 —— 定义「能发消息」这个契约。
 *
 * <p>【接口是什么】一组方法的声明，只规定「能做什么」，不管「怎么做」。
 * 谁实现这个接口，就必须把方法写出来。
 *
 * <p>【前端对照】就是 TS 的 interface：
 * <pre>
 * interface Notifier {
 *   send(to: string, content: string): void
 * }
 * </pre>
 * 区别：TS 的 interface 编译后消失，只做类型检查；Java 的 interface 是真实存在的类型，
 * 可以用来声明变量、做多态、做依赖注入（Spring 里到处是接口）。
 *
 * <p>【为什么项目里到处都是接口】
 * 业务代码只依赖接口，不依赖具体实现。将来从「短信」换成「企业微信」，
 * 只要换一个实现类，调用的地方一行都不用改。这叫「面向接口编程」。
 */
public interface Notifier {

    /**
     * 发消息。接口里的方法默认就是 public abstract，不用写修饰符。
     *
     * @param to      接收方（手机号 / 用户id / 邮箱，由实现类自己解释）
     * @param content 消息内容
     */
    void send(String to, String content);

    /**
     * 渠道名称。
     *
     * <p>【default 方法】Java 8 新增的语法：接口里可以有带方法体的方法。
     * 好处是给接口加新方法时，已有的实现类不用全部改一遍。
     *
     * <p>这里用 getClass().getSimpleName() 拿到实际实现类的类名，
     * 所以 SmsNotifier 调这个方法返回 "SmsNotifier"。
     */
    default String channelName() {
        return getClass().getSimpleName();
    }
}
