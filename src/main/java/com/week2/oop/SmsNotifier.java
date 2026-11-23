package com.week2.oop;

/**
 * 短信通知实现 —— 继承 AbstractNotifier，只需要实现「怎么发」这一步。
 *
 * <p>【继承 extends】子类自动拥有父类的所有非 private 成员和方法。
 * 这里 SmsNotifier 白拿了 validate()、send() 的完整流程，只写了自己特有的 doSend()。
 *
 * <p>【重写 @Override】子类实现父类的抽象方法。@Override 这个注解不加也能编译，
 * 但强烈建议加：万一方法签名写错了（比如参数类型不对），编译器会直接报错，
 * 而不是悄悄变成「一个新方法」，运行时才发现没生效。
 */
public class SmsNotifier extends AbstractNotifier {

    /**
     * 短信签名，真实项目里必须有（运营商要求）。
     * 【成员变量】抽象类可以有字段，接口不行（接口里的字段隐式是 public static final 常量）。
     */
    private final String sign;

    /**
     * 【构造方法】名字和类名一样、没有返回值。
     * new SmsNotifier("快诊580") 时执行。
     */
    public SmsNotifier(String sign) {
        this.sign = sign;
    }

    /**
     * 【super 关键字】调用父类的成员。这里没用到，但记住这个写法。
     */

    @Override
    protected void doSend(String to, String content) {
        // 真实项目这里会调短信服务商的 SDK / HTTP 接口
        // 练习里就打印出来，能看见效果就够了
        System.out.println("【短信】→ " + to + "：" + content + "【" + sign + "】");
    }
}
