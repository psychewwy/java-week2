package com.week2.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合框架演示：List / Map / Set 及遍历方式。
 *
 * <p>跑法：{@code mvn -q exec:java -Dexec.mainClass=com.week2.collection.CollectionDemo}
 *
 * <p>【和前端对照】
 * <table border="1">
 *   <tr><th>Java</th><th>前端</th><th>特点</th></tr>
 *   <tr><td>ArrayList</td><td>Array</td><td>有序、可重复、按下标取。查快增删慢</td></tr>
 *   <tr><td>LinkedList</td><td>无直接对应</td><td>有序、可重复。增删快查慢（链表）</td></tr>
 *   <tr><td>HashMap</td><td>Object / Map</td><td>键值对，key 无序</td></tr>
 *   <tr><td>LinkedHashMap</td><td>Map</td><td>键值对，保持插入顺序</td></tr>
 *   <tr><td>HashSet</td><td>Set</td><td>不重复、无序</td></tr>
 * </table>
 *
 * <p>【最关键的区别】Java 集合有泛型，装进去就定了类型；
 * 取出来不用强转，也不会出现「本来是数字变成字符串」这种事。
 */
public class CollectionDemo {

    public static void main(String[] args) {

        // ==========================================================
        // 一、List —— 有序、可重复
        // ==========================================================
        //
        // <String> 是泛型：这个 list 只能装 String。
        // 前端 Array 什么都能塞，Java 的 List 泛型定死了，安全得多。
        List<String> names = new ArrayList<>();
        names.add("张三");
        names.add("李四");
        names.add("王五");
        names.add("张三");          // 允许重复

        System.out.println("===== List 四种遍历方式 =====");

        // ① 普通 for（按下标，和 JS 的 for(let i=0;i<arr.length;i++) 一样）
        //    names.size() 是元素个数，不是 length
        for (int i = 0; i < names.size(); i++) {
            System.out.println("for-i     : " + names.get(i));
        }

        // ② 增强 for（最常用，等价于 JS 的 for...of）
        for (String name : names) {
            System.out.println("for-each  : " + name);
        }

        // ③ Iterator（老写法，现在少用，但看老代码会碰到）
        //    ⚠️ 遍历时如果要删除元素，必须用 Iterator.remove()，
        //       用 list.remove() 会抛 ConcurrentModificationException
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            System.out.println("iterator  : " + it.next());
        }

        // ④ forEach + Lambda（Java 8，最简洁，和 JS 的 arr.forEach 一样）
        names.forEach(name -> System.out.println("lambda    : " + name));

        // 常用方法（和 JS Array 对照）
        System.out.println("\n个数：" + names.size());              // JS: length
        System.out.println("包含李四：" + names.contains("李四"));   // JS: includes
        System.out.println("李四下标：" + names.indexOf("李四"));    // JS: indexOf
        System.out.println("是否为空：" + names.isEmpty());          // JS: length === 0

        // ==========================================================
        // 二、Map —— 键值对
        // ==========================================================
        //
        // 【为什么用 LinkedHashMap 而不是 HashMap】
        // HashMap 不保证顺序（打印出来可能是乱序的）；
        // LinkedHashMap 按插入顺序排，调试时看得清楚。
        // 实际项目里要顺序就 LinkedHashMap，不在乎顺序用 HashMap（性能略好）。
        Map<String, Integer> scoreMap = new LinkedHashMap<>();
        scoreMap.put("张三", 85);
        scoreMap.put("李四", 78);
        scoreMap.put("王五", 92);
        scoreMap.put("张三", 90);   // key 相同 → 覆盖，不会变成两条

        System.out.println("\n===== Map 三种遍历方式 =====");

        // ① keySet：只要 key（性能最差，每次 get 都要再查一次）
        //    前端：Object.keys(obj).forEach(k => ...)
        for (String key : scoreMap.keySet()) {
            System.out.println("keySet    : " + key + " = " + scoreMap.get(key));
        }

        // ② entrySet：同时拿 key 和 value（推荐，只查一次）
        //    前端：Object.entries(obj).forEach(([k,v]) => ...)
        for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
            System.out.println("entrySet  : " + entry.getKey() + " = " + entry.getValue());
        }

        // ③ forEach + Lambda（Java 8）
        scoreMap.forEach((key, value) -> System.out.println("lambda    : " + key + " = " + value));

        // 【必记】getOrDefault —— 这是 Java Map 和 JS 最大的用法差异
        // JS 里 obj[key] 不存在返回 undefined，你可以直接判断；
        // Java 里 map.get(不存在的key) 返回 null，如果要累加会 NPE。
        System.out.println("\n不存在的key：" + scoreMap.get("赵六"));           // null
        System.out.println("用默认值兜底：" + scoreMap.getOrDefault("赵六", 0)); // 0

        // ==========================================================
        // 三、Set —— 不重复
        // ==========================================================
        //
        // 最常用场景：去重。JS 里是 [...new Set(arr)]，Java 里是 new HashSet<>(list)。
        List<String> withDup = Arrays.asList("张三", "李四", "张三", "王五", "李四");
        Set<String> noDup = new HashSet<>(withDup);

        System.out.println("\n===== Set 去重 =====");
        System.out.println("去重前：" + withDup + "（" + withDup.size() + " 个）");
        System.out.println("去重后：" + noDup + "（" + noDup.size() + " 个）");
        // ⚠️ HashSet 是「无序」的，打印顺序和插入顺序无关，这不是 bug

        // ==========================================================
        // 四、TODO 练习
        // ==========================================================
        //
        // 用 Stream 完成下面三件事（第一周学过 Lambda，正好用上）：
        //   1. 从 names 里筛出名字长度等于 2 的（提示：filter + n -> n.length() == 2）
        //   2. 把 scoreMap 里分数大于 80 的 key 收集成一个 List
        //      （提示：entrySet().stream().filter(e -> e.getValue() > 80)
        //             .map(Map.Entry::getKey).collect(Collectors.toList())）
        //   3. 求 scoreMap 所有分数的平均值
        //      （提示：values().stream().mapToInt(Integer::intValue).average()）
        //
        // 这三条是公司项目里出现频率最高的集合操作，务必自己写一遍。
        //
        // TODO 1:
        // TODO 2:
        // TODO 3:
    }
}
