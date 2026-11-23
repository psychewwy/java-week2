package com.week2.oop;

/**
 * 通知渠道抽象类 —— 把各个渠道「共有的流程」抽出来，只留差异部分给子类。
 *
 * <p>【抽象类 vs 接口，怎么选】
 * <table border="1">
 *   <tr><th></th><th>接口</th><th>抽象类</th></tr>
 *   <tr><td>方法</td><td>只有声明（Java 8 后可加 default）</td><td>可以有完整实现</td></tr>
 *   <tr><td>字段</td><td>只能是常量</td><td>可以有普通成员变量</td></tr>
 *   <tr><td>继承</td><td>一个类可以实现多个接口</td><td>只能继承一个（Java 单继承）</td></tr>
 *   <tr><td>表达的含义</td><td>「能做什么」（能力）</td><td>「是什么」（同类事物的共性）</td></tr>
 * </table>
 *
 * <p>【实际项目的用法】两者配合：接口对外暴露能力，抽象类在里面抽公共逻辑。
 * 你公司项目里 {@code BaseController}、{@code BaseService} 就是这个角色。
 *
 * <p>【模板方法模式】这个类是典型实现：send() 定义了固定流程
 * （先校验 → 再发送），把「怎么发送」这一步留给子类（doSend）。
 * 这样新增一个渠道，只要实现 doSend，校验逻辑天然就有了，不会漏。
 */
public abstract class AbstractNotifier implements Notifier {

    /**
     * 【final 关键字】表示这个方法不能被子类重写。
     *
     * <p>为什么加 final：这是模板方法模式的骨架，流程必须固定。
     * 如果子类把 send() 重写了、跳过校验，那这套约束就失效了。
     */
    @Override
    public final void send(String to, String content) {
        // 第 1 步：统一校验（所有渠道都要做，所以放在父类）
        validate(to, content);
        // 第 2 步：具体怎么发，交给子类（每个渠道不一样）
        doSend(to, content);
        // 第 3 步：统一收尾（真实项目里这里会记日志、埋点统计）
        System.out.println("  [日志] 渠道=" + channelName() + " 接收方=" + to + " 发送完成");
    }

    /**
     * 【抽象方法】只有声明没有方法体，子类必须实现。
     *
     * <p>【protected】修饰符：只有自己和子类能调用，外部拿不到。
     * 这里是内部流程的一环，不该让外部直接调用（否则绕过校验）。
     */
    protected abstract void doSend(String to, String content);

    /**
     * 【private】只有本类能用，子类也不行。纯内部实现细节。
     */
    private void validate(String to, String content) {
        if (to == null || to.trim().isEmpty()) {
            // 【异常】这里抛的是运行时异常，调用方可以不写 try-catch
            // 第三部分会专门讲异常，先知道「参数不对要抛异常」就行
            throw new IllegalArgumentException("接收方不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
    }
}
