package com.week2.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CommonUtils 的单元测试 —— 第二周考核项 1 的「附单元测试」。
 *
 * <p>跑法：
 * <pre>
 * cd java-week2
 * set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.502.7-hotspot
 * mvn test
 * </pre>
 * IDEA 里点类名左边的绿三角可以一次跑全部，点单个方法左边的三角只跑那一个。
 *
 * <p>【单元测试到底在测什么】
 * 测的不是「代码能不能跑」，而是「给定输入，输出是否符合预期」。
 * 重点是<b>边界情况</b>：null、空串、0、负数、超大值。
 * 你写的代码在生产环境挂掉，90% 是边界没处理。
 *
 * <p>【JUnit 5 几个核心注解】
 * <table border="1">
 *   <tr><td>@Test</td><td>标记这是一个测试方法，方法必须 public void 且无参数</td></tr>
 *   <tr><td>@DisplayName</td><td>给测试起个人话名字，报告里显示这个而不是方法名</td></tr>
 *   <tr><td>@BeforeEach</td><td>每个测试方法执行前跑一次，用来准备数据</td></tr>
 *   <tr><td>@Disabled</td><td>禁用这个测试，报告里显示为 skipped</td></tr>
 * </table>
 *
 * <p>【命名规范】方法名用 {@code 被测方法_场景_预期结果}，
 * 比如 {@code isBlank_nullInput_returnsTrue}。看名字就知道测的什么。
 */
@DisplayName("CommonUtils 工具类测试")
class CommonUtilsTest {

    // ================================================================
    // 一、isBlank 的测试（示范）
    // ================================================================

    @Test
    @DisplayName("isBlank：null 应返回 true")
    void isBlank_nullInput_returnsTrue() {
        // assertTrue(条件) 断言条件为真，不为真测试就失败
        assertTrue(CommonUtils.isBlank(null));
    }

    @Test
    @DisplayName("isBlank：空串应返回 true")
    void isBlank_emptyString_returnsTrue() {
        assertTrue(CommonUtils.isBlank(""));
    }

    @Test
    @DisplayName("isBlank：纯空格应返回 true")
    void isBlank_spacesOnly_returnsTrue() {
        assertTrue(CommonUtils.isBlank("   \t\n  "));
    }

    @Test
    @DisplayName("isBlank：有内容应返回 false")
    void isBlank_normalString_returnsFalse() {
        assertFalse(CommonUtils.isBlank("张三"));
    }

    /**
     * 【一个测试方法里断言多组数据】这也是允许的，但一旦中间某个断言失败，
     * 后面的就不会执行了。数据多的时候建议用 @ParameterizedTest。
     */
    @Test
    @DisplayName("isBlank：首尾有空格但中间有内容，不算空白")
    void isBlank_paddedText_returnsFalse() {
        assertFalse(CommonUtils.isBlank("  张三  "));
    }

    // ================================================================
    // 二、maskPhone 的测试（示范）
    // ================================================================

    @Test
    @DisplayName("maskPhone：标准 11 位手机号，中间四位变星号")
    void maskPhone_normalPhone_masksMiddle() {
        // assertEquals(期望值, 实际值) —— 注意参数顺序，写反了失败信息会很误导
        assertEquals("138****8888", CommonUtils.maskPhone("13800008888"));
    }

    @Test
    @DisplayName("maskPhone：null 应返回空串")
    void maskPhone_null_returnsEmpty() {
        assertEquals("", CommonUtils.maskPhone(null));
    }

    @Test
    @DisplayName("maskPhone：空串应返回空串")
    void maskPhone_emptyString_returnsEmpty() {
        assertEquals("", CommonUtils.maskPhone(""));
    }

    @Test
    @DisplayName("maskPhone：长度不是 11 位，原样返回不脱敏")
    void maskPhone_wrongLength_returnsAsIs() {
        assertEquals("12345", CommonUtils.maskPhone("12345"));
    }

    @Test
    @DisplayName("maskPhone：前后有空格，先 trim 再脱敏")
    void maskPhone_withSpaces_trimsFirst() {
        assertEquals("138****8888", CommonUtils.maskPhone("  13800008888  "));
    }

    // ================================================================
    // 三、泛型方法 defaultIfNull 的测试（示范重点）
    // ================================================================

    @Test
    @DisplayName("defaultIfNull：null 返回默认值")
    void defaultIfNull_null_returnsDefault() {
        // 【泛型自动推断】这里传的是 String，编译器推断 T = String，
        // 所以返回类型直接就是 String，不需要强转
        String actual = CommonUtils.defaultIfNull(null, "默认值");
        assertEquals("默认值", actual);
    }

    @Test
    @DisplayName("defaultIfNull：有值就返回原值")
    void defaultIfNull_hasValue_returnsValue() {
        String actual = CommonUtils.defaultIfNull("实际值", "默认值");
        assertEquals("实际值", actual);
    }

    @Test
    @DisplayName("defaultIfNull：同一个方法能处理 Integer 类型")
    void defaultIfNull_integerType_works() {
        // 这就是泛型的价值：同一个方法，换个类型照样能用，而且不用强转
        Integer actual = CommonUtils.defaultIfNull(null, 0);
        assertEquals(Integer.valueOf(0), actual);
    }

    @Test
    @DisplayName("defaultIfNull：集合类型也能处理")
    void defaultIfNull_listType_works() {
        List<String> actual = CommonUtils.defaultIfNull(null, Collections.emptyList());
        assertTrue(actual.isEmpty());
    }

    @Test
    @DisplayName("defaultIfNull：null 默认值也允许")
    void defaultIfNull_bothNull_returnsNull() {
        // assertNull 断言结果是 null
        assertNull(CommonUtils.defaultIfNull(null, null));
    }

    // ================================================================
    // 四、TODO 方法的测试（实现后删掉 @Disabled）
    // ================================================================

    /**
     * 【@Disabled 的作用】让这个测试暂时跳过，mvn test 会显示 skipped 而不是失败。
     * 你把 CommonUtils.filter 实现完，就把下面这行 @Disabled 删掉，再跑一次。
     */
    @Test
    @Disabled("等 CommonUtils.filter 实现后再启用")
    @DisplayName("filter：筛出偶数")
    void filter_evenNumbers_returnsEvenOnly() {
        List<Integer> source = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> actual = CommonUtils.filter(source, n -> n % 2 == 0);
        assertEquals(Arrays.asList(2, 4, 6), actual);
    }

    @Test
    @Disabled("等 CommonUtils.filter 实现后再启用")
    @DisplayName("filter：null 入参返回空集合而不是 null")
    void filter_nullInput_returnsEmptyList() {
        List<String> actual = CommonUtils.filter(null, s -> true);
        // 【重要】工具方法返回集合时，永远返回空集合，不要返回 null。
        // 否则调用方每个地方都要判空，漏一个就 NPE。
        assertTrue(actual.isEmpty());
    }

    @Test
    @Disabled("等 CommonUtils.filter 实现后再启用")
    @DisplayName("filter：不能修改原集合")
    void filter_doesNotModifySource() {
        List<Integer> source = Arrays.asList(1, 2, 3);
        CommonUtils.filter(source, n -> n > 1);
        assertEquals(3, source.size());
    }

    @Test
    @Disabled("等 CommonUtils.groupKeysByValue 实现后再启用")
    @DisplayName("groupKeysByValue：value 相同的 key 归到一组")
    void groupKeysByValue_groupsCorrectly() {
        Map<String, String> source = new HashMap<>();
        source.put("张三", "一班");
        source.put("李四", "二班");
        source.put("王五", "一班");

        Map<String, List<String>> actual = CommonUtils.groupKeysByValue(source);

        assertEquals(2, actual.size());
        assertEquals(2, actual.get("一班").size());
        assertTrue(actual.get("一班").contains("张三"));
        assertTrue(actual.get("一班").contains("王五"));
    }

    @Test
    @Disabled("等 CommonUtils.capitalize 实现后再启用")
    @DisplayName("capitalize：首字母变大写")
    void capitalize_normalString_upperFirstLetter() {
        assertEquals("Hello", CommonUtils.capitalize("hello"));
    }

    @Test
    @Disabled("等 CommonUtils.capitalize 实现后再启用")
    @DisplayName("capitalize：空串返回空串，不抛异常")
    void capitalize_emptyString_returnsEmpty() {
        assertEquals("", CommonUtils.capitalize(""));
    }

    @Test
    @Disabled("等 CommonUtils.capitalize 实现后再启用")
    @DisplayName("capitalize：null 返回空串或 null，由你定义")
    void capitalize_null_handled() {
        // 【这里故意不写死期望值】先想清楚你的方法对 null 该怎么处理：
        // 返回 "" 还是返回 null？两种都可以，但必须和方法的 javadoc 一致。
        // 定了之后把下面的断言改成你的预期。
        String actual = CommonUtils.capitalize(null);
        assertEquals("", actual);
    }

    // ================================================================
    // 五、附加：怎么测「抛异常」
    // ================================================================

    /**
     * 【assertThrows】断言这段代码会抛出指定异常。
     *
     * <p>很多人只测正常路径，不测异常路径。但线上事故大多来自异常路径 ——
     * 参数非法时到底有没有拦住？拦住之后抛的是什么异常？这些都得测。
     */
    @Test
    @DisplayName("assertThrows 用法示范：除零会抛 ArithmeticException")
    void demo_assertThrows() {
        ArithmeticException e = assertThrows(
                ArithmeticException.class,
                () -> {
                    int x = 1 / 0;
                }
        );
        // 还能进一步断言异常消息
        assertTrue(e.getMessage() != null);
    }
}
