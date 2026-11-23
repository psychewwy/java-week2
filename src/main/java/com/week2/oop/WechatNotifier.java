package com.week2.oop;

/**
 * 企业微信通知实现 —— 和 SmsNotifier 是「兄弟关系」，都继承同一个父类。
 *
 * <p>对比一下两个类：结构完全一样，只有 doSend 里的逻辑不同。
 * 这就是继承的价值：把共性（校验、日志、流程）提到父类，子类只写差异。
 */
public class WechatNotifier extends AbstractNotifier {

    /** 企业微信的应用 id，真实项目里从配置文件读，这里写死方便演示 */
    private final String agentId;

    public WechatNotifier(String agentId) {
        this.agentId = agentId;
    }

    @Override
    protected void doSend(String to, String content) {
        // 真实项目调企业微信的 message/send 接口
        System.out.println("【企业微信】→ 用户" + to + "：" + content + "（agentId=" + agentId + "）");
    }

    /**
     * 【重写 default 方法】接口里的 default 方法，子类也可以覆盖。
     * 这里不用 getClass().getSimpleName()，而是一个中文名，更友好。
     *
     * <p>注意：接口的 default 方法用 @Override 修饰，这一点和重写父类方法一样。
     */
    @Override
    public String channelName() {
        return "企业微信渠道";
    }
}
