# java-week2 · 第二周学习练习

> 周期：2026-11-23 ~ 2026-11-27
> 位置：`C:\Users\zengy\Desktop\快诊580\后端学习记录\java-week2`
> 独立仓库，与 `java-week1`、`demo-api` 都无关。

---

## 一、考核项对照

| # | 考核项 | 本仓库对应位置 | 状态 |
|---|---|---|---|
| 1 | 编写通用工具类（含泛型方法）+ 单元测试 | `src/main/java/com/week2/util/CommonUtils.java`<br>`src/test/java/com/week2/util/CommonUtilsTest.java` | 🟡 3 个方法已示范，3 个 TODO 待实现 |
| 2 | 口头讲解 OOP 三大特性，抽查通过 | 讲稿见 `第二周学习计划.md` 第五节 | ⬜ |
| 3 | 10 道多表查询 SQL（含 JOIN 与分组统计） | `sql/week2.sql`（已附答案） | ⬜ 待逐条跑通 |
| 4 | Git 提交记录完整 | 本仓库提交历史 | ⬜ |

---

## 二、目录结构

```
java-week2/
├── pom.xml                    # Java 8 + JUnit 5 + surefire 3.1.2
├── 第二周学习计划.md           # 五天排期 + 口播讲稿
├── src/main/java/com/week2/
│   ├── oop/                   # 面向对象：接口 / 抽象类 / 继承 / 多态
│   │   ├── Notifier.java          接口（含 Java 8 default 方法）
│   │   ├── AbstractNotifier.java  抽象类（模板方法模式）
│   │   ├── SmsNotifier.java       实现：短信
│   │   ├── WechatNotifier.java    实现：企业微信
│   │   └── OopDemo.java           多态演示入口
│   ├── collection/
│   │   └── CollectionDemo.java    List/Map/Set 及四种遍历方式
│   ├── exception/
│   │   ├── ExceptionDemo.java     受检/非受检、finally、try-with-resources
│   │   └── BizException.java      自定义业务异常
│   └── util/
│       └── CommonUtils.java       ★ 考核项 1
└── src/test/java/com/week2/util/
    └── CommonUtilsTest.java       ★ 考核项 1 的单元测试
```

> **为什么第二周不用第一周那种简化结构**
> 第一周是纯练习，`sourceDirectory` 直接指向 `practice/` 就行。
> 第二周要跑单元测试，Maven 的 surefire 插件只认 `src/test/java` 这个固定目录，
> 所以换成了标准结构 —— 你公司项目也是这个样子，正好提前适应。

---

## 三、怎么跑

### 先设 JAVA_HOME（每个新终端都要）

```bash
export JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-8.0.502.7-hotspot'
```

> 你机器上 `JAVA_HOME` 之前没设，Maven 会摸到只有 JRE 的目录然后报
> `No compiler is provided`。这是第一周踩过的坑。

### 跑单元测试（考核项 1 的核心验证）

```bash
cd "/c/Users/zengy/Desktop/快诊580/后端学习记录/java-week2"
mvn test
```

期望看到：

```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 7
BUILD SUCCESS
```

**7 个 Skipped 是我故意标的 `@Disabled`**，对应 3 个 TODO 方法。
你把 TODO 实现完，把对应的 `@Disabled` 删掉，再跑一次，Skipped 应该变 0。

### 跑三个演示类

```bash
# 方式 A：IDEA 里点 main 左边的绿三角（推荐，中文不会乱码）
# 方式 B：命令行

java -Dfile.encoding=UTF-8 -cp target/classes com.week2.oop.OopDemo
java -Dfile.encoding=UTF-8 -cp target/classes com.week2.collection.CollectionDemo
java -Dfile.encoding=UTF-8 -cp target/classes com.week2.exception.ExceptionDemo
```

> ⚠️ **不要用 `mvn exec:java` 看中文输出**。Maven 自己的日志流会走 GBK，
> 在 Git Bash 里打印出来是乱码（第一周验证过，绕不开）。
> 要么用上面的 `java -cp` 方式，要么直接在 IDEA 里跑。

---

## 四、进度表

### OOP 部分

| 文件 | 学什么 | 完成 |
|---|---|---|
| `Notifier.java` | 接口、default 方法、面向接口编程 | ⬜ |
| `AbstractNotifier.java` | 抽象类、模板方法模式、final 方法 | ⬜ |
| `SmsNotifier.java` | 继承、@Override、构造方法 | ⬜ |
| `WechatNotifier.java` | 重写 default 方法 | ⬜ |
| `OopDemo.java` | 多态、向上转型、封装收益 | ⬜ |
| **自己写 `EmailNotifier.java`** | 综合练习（OopDemo 末尾有要求） | ⬜ |

### 集合部分

| 项 | 内容 | 完成 |
|---|---|---|
| List | 4 种遍历（for-i / for-each / iterator / lambda） | ⬜ |
| Map | 3 种遍历（keySet / entrySet / lambda）+ `getOrDefault` | ⬜ |
| Set | 去重 | ⬜ |
| TODO 1-3 | Stream 过滤 / 收集 / 求平均 | ⬜ |

### 异常与泛型

| 项 | 内容 | 完成 |
|---|---|---|
| 受检 vs 非受检 | Java 独有概念，前端没有 | ⬜ |
| try-catch-finally | 执行顺序，finally 在 return 之前 | ⬜ |
| 多重 catch | 子类异常必须在父类前面 | ⬜ |
| 自定义异常 | BizException + 错误码 | ⬜ |
| try-with-resources | AutoCloseable 自动关闭 | ⬜ |
| 泛型 | 泛型方法 `<T>`、双泛型 `<K, V>`、类型推断 | ⬜ |

### 工具类（考核项 1）

| 方法 | 状态 |
|---|---|
| `isBlank(String)` | ✅ 已实现，测试已通过 |
| `maskPhone(String)` | ✅ 已实现，测试已通过 |
| `<T> T defaultIfNull(T, T)` | ✅ 已实现（泛型方法示范），测试已通过 |
| `<T> List<T> filter(List<T>, Predicate<T>)` | ⬜ TODO 1 |
| `<K,V> Map<V,List<K>> groupKeysByValue(Map<K,V>)` | ⬜ TODO 2 |
| `String capitalize(String)` | ⬜ TODO 3 |

### 多表查询（考核项 3）

| # | 类型 | 完成 |
|---|---|---|
| Q01-Q02 | INNER vs LEFT JOIN 对比 | ⬜ |
| Q03 | LEFT JOIN + COUNT（含 COUNT 陷阱） | ⬜ |
| Q04 | 三表 JOIN | ⬜ |
| Q05-Q07 | GROUP BY / HAVING / 多聚合 | ⬜ |
| Q08-Q09 | 子查询 IN / NOT EXISTS | ⬜ |
| Q10 | 三表 JOIN + 分组 + 排序综合 | ⬜ |

---

## 五、提交规范

第二周同样要求 **≥ 8 次提交，每天都有**。

⚠️ **第一周的欠账**：`java-week1` 目前只有 1 次提交（初始化），P01-P09 的改动全在工作区没提交。
考核看的是连续性，第一周已经补不回来了，但**第二周别再攒着**。

格式：`<类型>: <做了什么>`

```
feat: Day1 完成 OOP 三大特性练习与 EmailNotifier
feat: Day2 完成集合遍历练习与 Stream 三个 TODO
feat: Day3 实现 CommonUtils 的 filter 与 groupKeysByValue
feat: Day4 补全单元测试，Skipped 归零
feat: Day5 完成 10 道多表查询并整理口播讲稿
```

---

## 六、环境

- JDK：`C:\Program Files\Eclipse Adoptium\jdk-8.0.502.7-hotspot`（Java 8）
- Maven：3.9.16
- MySQL：8.0.46，SQL 练习用 `demo_week2` 库（和你手动建的 `java_week1` 库互不影响）
- Git 身份：本仓库 `psychewwy <435682616@qq.com>`（个人身份，全局仍是公司邮箱）
