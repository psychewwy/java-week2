package com.week2.oop;

import java.util.ArrayList;
import java.util.List;

/**
 * 面向对象三大特性的演示入口。
 *
 * <p>跑法：
 * <pre>
 * cd java-week2
 * mvn -q compile
 * mvn -q exec:java -Dexec.mainClass=com.week2.oop.OopDemo
 * </pre>
 * 或者 IDEA 里点 main 左边的绿三角。
 *
 * <p>【三大特性一句话版】
 * <ul>
 *   <li><b>封装</b>：把数据和行为包在一起，只暴露该暴露的。field 用 private，对外给方法。</li>
 *   <li><b>继承</b>：子类复用父类的代码。extends。</li>
 *   <li><b>多态</b>：同一个父类型变量，运行时执行不同子类的逻辑。</li>
 * </ul>
 */
public class OopDemo {

    public static void main(String[] args) {

        // ==========================================================
        // 一、多态（Polymorphism）—— 本周最核心的概念
        // ==========================================================
        //
        // 【关键】变量类型写的是 Notifier（接口），new 出来的是具体实现类。
        // 编译时：只看 Notifier 有没有 send 方法 → 有，编译通过
        // 运行时：看实际对象是谁 → SmsNotifier 就调 SmsNotifier 的，WechatNotifier 同理
        //
        // 这叫「向上转型」。反过来（父类型转成具体子类型）叫向下转型，要强转。
        List<Notifier> notifiers = new ArrayList<>();
        notifiers.add(new SmsNotifier("快诊580"));
        notifiers.add(new WechatNotifier("1000011"));

        System.out.println("===== 多态演示：同一个 send()，不同表现 =====");
        for (Notifier notifier : notifiers) {
            // 这一行代码没变，但实际打印的内容完全不同
            // 因为 notifier 指向的对象不同
            notifier.send("13800008888", "您的预约已确认，请按时就诊");
        }

        // ==========================================================
        // 二、封装（Encapsulation）
        // ==========================================================
        //
        // 上面这句如果传 null 会怎样？试试把 "13800008888" 换成 null。
        // 会抛 IllegalArgumentException —— 因为 AbstractNotifier.validate() 拦住了。
        //
        // 调用方不需要知道「有没有校验、怎么校验」，只管调 send。
        // 想改校验规则，只改 AbstractNotifier 一处，所有渠道一起生效。
        // 这就是封装的收益：把变化关在一个地方。
        System.out.println("\n===== 封装演示：非法参数被统一拦截 =====");
        try {
            notifiers.get(0).send(null, "测试");
        } catch (IllegalArgumentException e) {
            System.out.println("拦截成功：" + e.getMessage());
        }

        // ==========================================================
        // 三、继承（Inheritance）
        // ==========================================================
        //
        // SmsNotifier 和 WechatNotifier 都没有写 validate()，
        // 但它们都能校验参数 —— 因为继承自 AbstractNotifier。
        System.out.println("\n===== 继承演示：子类白拿父类能力 =====");
        for (Notifier notifier : notifiers) {
            System.out.println("渠道名：" + notifier.channelName()
                    + "｜父类：" + notifier.getClass().getSuperclass().getSimpleName());
        }

        // ==========================================================
        // 四、TODO 练习：自己加一个邮件渠道
        // ==========================================================
        //
        // 要求：
        //   1. 新建 EmailNotifier.java，继承 AbstractNotifier
        //   2. 实现 doSend，打印「【邮件】→ xxx@xx.com：内容」
        //   3. 加一个成员变量 String subject（邮件主题），构造方法传入
        //   4. 把 new EmailNotifier("预约确认") 加进上面的 notifiers 列表，重新跑一次
        //
        // 判断你写对没有：
        //   - 不需要写 validate，null 参数照样能被拦住
        //   - 新增这个类，上面 for 循环一行代码都没改，但它自动生效了
        //   → 这就是「对扩展开放，对修改关闭」的开闭原则
        //
        // TODO: 在这里加 EmailNotifier
    }
}
