package com.week2.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 通用工具类 —— 第二周考核项 1 的产出。
 *
 * <p>【这个类示范的四个规范，公司项目里都这么写】
 * <ol>
 *   <li>类用 {@code final} 修饰：禁止被继承。工具类不该有子类。</li>
 *   <li>构造方法私有：禁止 new。工具类只提供静态方法，实例化没意义。</li>
 *   <li>所有方法 {@code static}：调用时 {@code CommonUtils.isBlank(x)}，不用创建对象。</li>
 *   <li>方法内不持有状态：同样的输入永远同样的输出，好测试也好并发。</li>
 * </ol>
 *
 * <p>【你项目里的对应】{@code StringUtils}、{@code DateUtils}、{@code SecurityUtils}，
 * 大部分公司直接用 Apache Commons Lang 的 {@code StringUtils}，但面试常让你手写。
 *
 * <p>【泛型是什么，为什么需要它】
 * <pre>
 * // 没有泛型（Java 5 之前）：
 * List list = new ArrayList();
 * list.add("张三");
 * list.add(123);              // 能塞进去，因为存的是 Object
 * String s = (String) list.get(1);   // 运行时炸：ClassCastException
 *
 * // 有泛型：
 * List&lt;String&gt; list = new ArrayList&lt;&gt;();
 * list.add(123);              // 编译期就报错，根本跑不起来
 * String s = list.get(0);     // 不用强转，编译器知道是 String
 * </pre>
 * 一句话：<b>泛型把运行时才发现的类型错误，提前到编译期。</b>
 * 前端 TS 的 {@code Array<string>} 是同一个思路，区别是 TS 编译后类型信息就没了，
 * Java 的类型信息能保留到运行时（虽然也有类型擦除，但反射能拿到）。
 */
public final class CommonUtils {

    /** 私有构造方法：外部 new 不出来，只能用静态方法 */
    private CommonUtils() {
        throw new AssertionError("工具类不允许实例化");
    }

    // ================================================================
    // 一、普通方法示范
    // ================================================================

    /**
     * 判断字符串是否为空白。
     *
     * <p>【为什么不用 == null 判断就完事】
     * 实际数据里 {@code ""}（空串）和 {@code "   "}（一堆空格）都算无效，
     * 只判 null 会漏。这个方法把三种情况一起处理了。
     *
     * @param str 待判断的字符串，可以为 null
     * @return null / 空串 / 全是空白字符 → true
     */
    public static boolean isBlank(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }
        // 【trim()】去掉首尾空白。注意它返回新字符串，不改变原字符串（String 不可变）
        return str.trim().isEmpty();
    }

    /**
     * 手机号脱敏：13800008888 → 138****8888
     *
     * <p>【真实业务场景】线索列表、客户详情、导出 Excel 里，手机号都必须脱敏。
     * 这是合规要求（个人信息保护法），不是可选项。
     *
     * <p>【边界处理】长度不足 11 位怎么办？直接原样返回。
     * 真实项目里更严谨的做法是抛异常或返回空串，看业务约定。
     *
     * @param phone 手机号
     * @return 脱敏后的手机号；入参为空时返回空串
     */
    public static String maskPhone(String phone) {
        if (isBlank(phone)) {
            return "";
        }
        String trimmed = phone.trim();
        if (trimmed.length() != 11) {
            // 不是标准 11 位手机号，不做处理，原样返回
            return trimmed;
        }
        // substring(0,3) 取前 3 位，substring(7) 取第 7 位到末尾
        // 下标从 0 开始，和 JS 的 slice 一致
        return trimmed.substring(0, 3) + "****" + trimmed.substring(7);
    }

    // ================================================================
    // 二、泛型方法示范（本周重点）
    // ================================================================

    /**
     * 值为 null 时返回默认值。
     *
     * <p>【泛型方法怎么读】方法签名里的 {@code <T>} 就是关键：
     * <pre>
     * public static &lt;T&gt; T defaultIfNull(T value, T defaultValue)
     *                ↑  ↑           ↑        ↑
     *             声明泛型 返回类型   参数类型  参数类型
     * </pre>
     * {@code <T>} 写在返回值类型前面，表示「这个方法自己声明了一个类型变量 T」。
     * 调用时编译器会根据传入的实参自动推断 T 到底是什么：
     * <pre>
     * String s = defaultIfNull(null, "默认");   // T 推断为 String
     * Integer i = defaultIfNull(null, 0);       // T 推断为 Integer
     * </pre>
     *
     * <p>【泛型方法的 T 和泛型类的 T 不是一回事】
     * 泛型类的 T 在整个类里有效（如 {@code List<T>}）；
     * 泛型方法的 T 只在当前方法里有效，出了方法就没这个概念了。
     *
     * @param value        原值
     * @param defaultValue 默认值
     * @param <T>          任意类型
     * @return value 不为 null 时返回 value，否则返回 defaultValue
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    // ================================================================
    // 三、TODO：这三个自己实现（实现完去跑测试）
    // ================================================================

    /**
     * 【TODO 1】按条件过滤集合，返回新集合。
     *
     * <p>要求：
     * <ul>
     *   <li>source 为 null 或空 → 返回空集合（不要返回 null！）</li>
     *   <li>predicate 返回 true 的元素放进结果</li>
     *   <li>不能修改传进来的 source 集合</li>
     * </ul>
     *
     * <p>提示：用 for 循环 + {@code predicate.test(item)} 判断即可，
     * 想挑战一下可以用 Stream 的 {@code filter().collect()}。
     *
     * <p>【为什么要泛型】这个方法要能过滤 {@code List<String>}、{@code List<Integer>}、
     * {@code List<ClueVO>}… 如果不写泛型，只能针对某一种类型写一个方法。
     * 有了 {@code <T>}，一个方法通吃所有类型，而且取出来的元素类型还是准的。
     *
     * @param source    源集合
     * @param predicate 判断条件（Java 8 函数式接口，传个 Lambda 就行）
     * @param <T>       元素类型
     * @return 符合条件的新集合
     */
    public static <T> List<T> filter(List<T> source, Predicate<T> predicate) {
        // TODO 1: 在这里实现
        // 提示：
        //   List<T> result = new ArrayList<>();
        //   if (source == null || source.isEmpty()) { return result; }
        //   for (T item : source) { if (predicate.test(item)) { result.add(item); } }
        //   return result;
        throw new UnsupportedOperationException("TODO 1 还没实现");
    }

    /**
     * 【TODO 2】把 Map 按 value 分组，得到 value → key 列表。
     *
     * <p>示例：
     * <pre>
     * 入参：{张三=一班, 李四=二班, 王五=一班}
     * 出参：{一班=[张三, 王五], 二班=[李四]}
     * </pre>
     * 真实场景：按科室分组统计医生、按状态分组统计线索。
     *
     * <p>【双泛型 K, V】这个方法同时声明了两个类型变量：
     * <pre>
     * public static &lt;K, V&gt; Map&lt;V, List&lt;K&gt;&gt; groupKeysByValue(Map&lt;K, V&gt; source)
     * </pre>
     * 注意返回类型的 key 是 V、value 是 {@code List<K>} —— 正好和入参反过来了。
     *
     * <p>提示：遍历 {@code source.entrySet()}，用
     * {@code result.computeIfAbsent(value, k -> new ArrayList<>()).add(key)}
     * 一行就能搞定，想想为什么。
     *
     * @param source 源 Map
     * @param <K>    key 类型
     * @param <V>    value 类型
     * @return 分组后的 Map，value 相同的 key 会被归到一起
     */
    public static <K, V> Map<V, List<K>> groupKeysByValue(Map<K, V> source) {
        // TODO 2: 在这里实现
        // 提示：
        //   Map<V, List<K>> result = new HashMap<>();
        //   if (source == null) { return result; }
        //   for (Map.Entry<K, V> entry : source.entrySet()) {
        //       result.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
        //             .add(entry.getKey());
        //   }
        //   return result;
        throw new UnsupportedOperationException("TODO 2 还没实现");
    }

    /**
     * 【TODO 3】首字母大写，其余不变。空字符串返回空字符串。
     *
     * <p>示例：{@code "hello" → "Hello"}、{@code "java后端" → "Java后端"}
     *
     * <p>提示：{@code str.substring(0, 1).toUpperCase() + str.substring(1)}。
     * 注意先判空，否则 null 会 NPE、空串会 StringIndexOutOfBoundsException。
     *
     * @param str 原字符串
     * @return 首字母大写后的字符串
     */
    public static String capitalize(String str) {
        // TODO 3: 在这里实现
        throw new UnsupportedOperationException("TODO 3 还没实现");
    }
}
