package com.week2.exception;

/**
 * 异常处理演示。
 *
 * <p>跑法：{@code mvn -q exec:java -Dexec.mainClass=com.week2.exception.ExceptionDemo}
 *
 * <p>【前端对照】
 * <pre>
 * // 前端
 * try { ... } catch (e) { console.error(e) }
 *
 * // Java 一模一样
 * try { ... } catch (Exception e) { e.printStackTrace(); }
 * </pre>
 * 语法几乎一样，但 Java 多了一个前端完全没有的东西：**受检异常**。
 */
public class ExceptionDemo {

    public static void main(String[] args) {

        // ==========================================================
        // 一、受检异常 vs 非受检异常（Java 独有，必懂）
        // ==========================================================
        //
        // 【受检异常 Checked】编译器强制你处理，不写 try-catch 或 throws 就编译不过。
        //   典型：IOException、SQLException、InterruptedException
        //   为什么这样设计：这类错误（文件不存在、数据库连不上）是「预期内可能发生的」，
        //   编译器逼你正面处理，不许装作看不见。
        //
        // 【非受检异常 Unchecked】RuntimeException 及其子类，编译器不管。
        //   典型：NullPointerException、IllegalArgumentException、ArrayIndexOutOfBounds
        //   这类通常是「代码写错了」，属于 bug，不该靠 try-catch 兜。
        //
        // 前端没有这个区分 —— 所有错误都是运行时才炸，所以第一次见会不习惯。
        System.out.println("===== 一、受检 vs 非受检 =====");

        // 这行如果不写 try-catch，编译直接报错：Unhandled exception: java.io.IOException
        try {
            String config = readConfig("config.yml");
            System.out.println("受检异常：这行不写 try-catch 编译都过不了 → " + config);
        } catch (Exception e) {
            System.out.println("受检异常必须处理：" + e.getMessage());
        }

        // 这一行不写 try-catch 也能通过编译（因为 ArithmeticException 是非受检的），
        // 但运行到这里会真的抛异常、程序中断。所以实际写代码还是得兜住。
        try {
            int result = divide(10, 0);
            System.out.println("结果是：" + result);
        } catch (ArithmeticException e) {
            System.out.println("非受检异常：编译期不管你，运行时才炸 → " + e.getMessage());
        }

        // ==========================================================
        // 二、try-catch-finally
        // ==========================================================
        //
        // 【finally】不管有没有异常都会执行，常用来释放资源（关流、还连接）。
        // 【执行顺序】try 里 return 了，finally 也会先跑完再 return。
        System.out.println("\n===== 二、finally 一定执行 =====");
        System.out.println("返回值：" + withFinally());

        // ==========================================================
        // 三、多个 catch：从具体到宽泛
        // ==========================================================
        //
        // 【顺序硬规则】子类异常必须写在父类前面。
        // 如果 Exception 写在前，后面的 catch 永远进不去，编译器直接报错。
        System.out.println("\n===== 三、多重 catch =====");
        try {
            Object obj = null;
            obj.toString();                       // 这里会抛 NullPointerException
        } catch (NullPointerException e) {        // 具体的在前
            System.out.println("空指针：" + e.getMessage());
        } catch (RuntimeException e) {            // 宽泛的在后
            System.out.println("运行时异常：" + e.getMessage());
        }

        // ==========================================================
        // 四、自定义异常（公司项目必备）
        // ==========================================================
        //
        // 【为什么要自己定义】业务错误需要带上错误码，方便前端区分提示、
        // 也方便后端日志统计。JDK 自带的异常没有业务语义。
        //
        // 你项目里的 BizException 就是干这个的：
        //   throw new BizException("线索不存在");
        // 然后全局异常处理器统一捕获，转成 {"code":500,"message":"线索不存在"}
        System.out.println("\n===== 四、自定义业务异常 =====");
        try {
            checkAge(15);
        } catch (BizException e) {
            System.out.println("业务异常｜错误码=" + e.getCode() + "｜消息=" + e.getMessage());
        }

        // ==========================================================
        // 五、try-with-resources（Java 7+，处理资源的标准写法）
        // ==========================================================
        //
        // 实现 AutoCloseable 接口的资源，可以写在 try 的括号里，
        // 用完自动 close，不用自己写 finally。
        // 实际项目里读文件、操作数据库连接都是这个写法。
        System.out.println("\n===== 五、try-with-resources =====");
        try (FakeConnection conn = new FakeConnection()) {
            conn.query("SELECT * FROM student");
        } catch (Exception e) {
            System.out.println("出错了：" + e.getMessage());
        }
        // 注意：上面没写 conn.close()，但 FakeConnection 的 close 已经被自动调用

        // ==========================================================
        // 六、三个必须改掉的坏习惯
        // ==========================================================
        //
        // ① 吞异常 —— 最常见的线上事故来源
        //    try { ... } catch (Exception e) { }        ❌ 出了问题一点痕迹都没有
        //    try { ... } catch (Exception e) { log.error("xxx失败", e); }  ✅
        //
        // ② 用异常做流程控制
        //    try { return list.get(0); } catch (IndexOutOfBoundsException e) { return null; }
        //    ❌ 异常很贵（要生成堆栈），判断 list.isEmpty() 就行
        //
        // ③ catch 了不处理也不抛
        //    catch (Exception e) { System.out.println("出错了"); }
        //    ❌ 上层完全不知道失败了，业务会继续往下走
    }

    // ------------------------------------------------------------
    // 下面是被调用的方法
    // ------------------------------------------------------------

    /**
     * 【throws】声明这个方法可能抛受检异常，把处理责任交给调用方。
     *
     * <p>这里用 RuntimeException 模拟「文件读不到」，实际会抛 IOException。
     * 注意方法签名上的 {@code throws IOException}：没有它，方法里 new IOException 就编译不过。
     */
    private static String readConfig(String path) throws java.io.IOException {
        if (path == null) {
            throw new java.io.IOException("配置文件不存在：" + path);
        }
        return "配置内容";
    }

    /**
     * 整数除法，除数为 0 时抛非受检异常。
     * 【注意】这里没写 throws，因为 ArithmeticException 是 RuntimeException 的子类，编译器不管。
     */
    private static int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("除数不能为 0");
        }
        return a / b;
    }

    /** 演示 finally 的执行时机 */
    private static String withFinally() {
        try {
            System.out.println("  try 开始");
            return "try 的返回值";
        } finally {
            // 会先打印这句，再真正 return
            System.out.println("  finally 执行（在 return 之前）");
        }
    }

    /** 业务校验，不通过就抛自定义异常 */
    private static void checkAge(int age) {
        if (age < 18) {
            throw new BizException(40001, "年龄未满 18 周岁");
        }
    }

    /**
     * 模拟一个需要关闭的资源。
     *
     * <p>【implements AutoCloseable】实现这个接口，才能写在 try 的括号里。
     * 你项目里 MyBatis 的 SqlSession、JDK 的 InputStream 都实现了它。
     */
    static class FakeConnection implements AutoCloseable {
        void query(String sql) {
            System.out.println("  执行 SQL：" + sql);
        }

        @Override
        public void close() {
            System.out.println("  连接已自动关闭（不用你写 close）");
        }
    }
}
