-- ============================================================
-- 第二周 SQL 练习 week2.sql（含答案版）
-- 考核项 3：10 道多表查询（含 JOIN 与分组统计）
-- 执行方式：Navicat 新建查询 → 粘贴 → Ctrl+Shift+R（执行全部）
-- ============================================================
--
-- 【和第一周的区别】
--   week1 是单表增删改查；week2 全是多表。
--   多表查询就三件事：JOIN 怎么连、GROUP BY 怎么分、子查询怎么嵌。
--
-- 【场景说明】用你公司的线索系统，四张表：
--   hospital      医院（线索归属）
--   sys_user      操作人（跟进的人）
--   clue          线索主表
--   clue_follow   跟进记录（一条线索可以跟多次）
--
-- 【表关系图】
--   hospital 1 --- n clue 1 --- n clue_follow n --- 1 sys_user
--   一家医院有多条线索，一条线索有多条跟进记录，每条跟进由某个操作人做的
-- ============================================================


-- ============================================================
-- 第一部分：建库建表 + 准备数据
-- ============================================================

CREATE DATABASE IF NOT EXISTS demo_week2
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE demo_week2;

-- 有外键依赖，删表要反着删
DROP TABLE IF EXISTS clue_follow;
DROP TABLE IF EXISTS clue;
DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS hospital;


-- ------------------------------------------------------------
-- 表 1：hospital 医院表
-- ------------------------------------------------------------
CREATE TABLE hospital (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    hospital_name VARCHAR(100)    NOT NULL                COMMENT '医院名称',
    city          VARCHAR(50)     NULL     DEFAULT NULL   COMMENT '城市',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医院表';


-- ------------------------------------------------------------
-- 表 2：sys_user 操作人表
-- ------------------------------------------------------------
CREATE TABLE sys_user (
    id        BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_name VARCHAR(50)     NOT NULL                COMMENT '姓名',
    dept      VARCHAR(50)     NULL     DEFAULT NULL   COMMENT '部门',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作人表';


-- ------------------------------------------------------------
-- 表 3：clue 线索表
-- ------------------------------------------------------------
CREATE TABLE clue (
    id            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    customer_name VARCHAR(50)     NOT NULL                COMMENT '客户姓名',
    phone         VARCHAR(11)     NOT NULL                COMMENT '手机号',

    -- 允许 NULL：线索可能还没分配医院，这是真实业务里常见的情况
    -- 也正因为有 NULL，下面 LEFT JOIN 的题目才有意义
    hospital_id   BIGINT UNSIGNED NULL     DEFAULT NULL   COMMENT '所属医院id',

    status        TINYINT         NOT NULL DEFAULT 0      COMMENT '状态 0待跟进 1跟进中 2已成交 3已流失',
    create_time   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    PRIMARY KEY (id),
    -- 关联列必须有索引，否则 JOIN 时对主表每行都要扫全表
    KEY idx_hospital_id (hospital_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线索表';


-- ------------------------------------------------------------
-- 表 4：clue_follow 跟进记录表
-- ------------------------------------------------------------
CREATE TABLE clue_follow (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    clue_id     BIGINT UNSIGNED NOT NULL                COMMENT '线索id',
    content     VARCHAR(200)    NULL     DEFAULT NULL   COMMENT '跟进内容',
    user_id     BIGINT UNSIGNED NULL     DEFAULT NULL   COMMENT '操作人id',
    follow_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '跟进时间',

    PRIMARY KEY (id),
    KEY idx_clue_id (clue_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='线索跟进记录表';


-- ------------------------------------------------------------
-- 插入数据
-- ------------------------------------------------------------

INSERT INTO hospital (id, hospital_name, city) VALUES
  (1, '康宁医院', '上海'),
  (2, '仁和医院', '北京'),
  (3, '惠民医院', '上海'),
  (4, '新开的医院', '广州');        -- 这家没有任何线索，专门用来验证 LEFT JOIN

INSERT INTO sys_user (id, user_name, dept) VALUES
  (1, '徐姐', '运营部'),
  (2, '邓主任', '医生');

-- 6 条线索：医院1两条、医院2两条、医院3一条、周八没分配医院(hospital_id 为 NULL)
INSERT INTO clue (id, customer_name, phone, hospital_id, status, create_time) VALUES
  (1, '张三', '13800001111', 1,    2, '2026-11-01 09:00:00'),
  (2, '李四', '13800002222', 1,    1, '2026-11-02 10:00:00'),
  (3, '王五', '13800003333', 2,    2, '2026-11-03 11:00:00'),
  (4, '赵六', '13800004444', 2,    0, '2026-11-05 14:00:00'),
  (5, '孙七', '13800005555', 3,    3, '2026-11-06 15:00:00'),
  (6, '周八', '13800006666', NULL, 0, '2026-11-07 16:00:00');

-- 5 条跟进：线索1跟了2次、线索2跟了1次、线索3跟了2次；线索4/5/6 一次都没跟
INSERT INTO clue_follow (id, clue_id, content, user_id, follow_time) VALUES
  (1, 1, '电话已接通，客户有意向', 1, '2026-11-01 10:30:00'),
  (2, 1, '已发送方案，等待回复',   2, '2026-11-02 09:20:00'),
  (3, 2, '客户在考虑中',           1, '2026-11-03 16:00:00'),
  (4, 3, '已预约到院参观',         1, '2026-11-04 08:40:00'),
  (5, 3, '到院完成，已成交',       2, '2026-11-05 17:10:00');


-- ============================================================
-- 第二部分：10 道多表查询（含答案）
-- ============================================================
-- 数据全景：
--   hospital: 1康宁(上海) 2仁和(北京) 3惠民(上海) 4新开的医院(广州，无线索)
--   clue:     1张三→医院1 2李四→医院1 3王五→医院2 4赵六→医院2 5孙七→医院3 6周八→NULL
--   follow:   clue1×2次 / clue2×1次 / clue3×2次，clue4/5/6 无
-- ============================================================


-- ---------- 一、JOIN 类（01-04）----------

-- 【Q01】INNER JOIN 两表：查线索及其所属医院名
-- 【业务场景】线索列表页，要显示医院名称而不是 hospital_id
-- 【要点】INNER JOIN 只返回两表都匹配的行。
--        周八的 hospital_id 是 NULL，匹配不上任何医院 → 被丢弃，结果 5 条
-- ✅ 答案：
SELECT c.id,
       c.customer_name,
       c.phone,
       h.hospital_name
FROM clue c
INNER JOIN hospital h ON c.hospital_id = h.id;


-- 【Q02】LEFT JOIN 同样查一遍，对比结果差异
-- 【业务场景】「所有线索」列表——还没分配医院的也要能看到
-- 【要点】LEFT JOIN 以左表(clue)为准，右表匹配不上填 NULL。
--        所以周八也出现，hospital_name 是 NULL，结果 6 条
-- ✅ 答案：
SELECT c.id,
       c.customer_name,
       h.hospital_name
FROM clue c
LEFT JOIN hospital h ON c.hospital_id = h.id;


-- 【Q03】LEFT JOIN + 分组：统计每家医院的线索数（没线索的医院也要显示 0）
-- 【业务场景】医院管理列表页的「线索数」列
-- 【要点】⚠️ 这是全场最容易错的一题——
--        必须写 COUNT(c.id)，不能写 COUNT(*)！
--        因为医院 4 没有线索，LEFT JOIN 后那行是 (医院4, NULL)，
--        COUNT(*) 会把这行也算 1 条 → 显示成「1 条线索」，错的
--        COUNT(c.id) 统计的是非 NULL 值，NULL 不计入 → 正确显示 0
-- ✅ 答案：
SELECT h.id,
       h.hospital_name,
       COUNT(c.id) AS clue_count
FROM hospital h
LEFT JOIN clue c ON h.id = c.hospital_id
GROUP BY h.id, h.hospital_name
ORDER BY clue_count DESC;


-- 【Q04】三表 JOIN：查跟进明细（客户 + 医院 + 跟进人）
-- 【业务场景】跟进记录列表，一屏要看到客户是谁、哪家医院、谁跟的
-- 【要点】JOIN 是链式的：A JOIN B ON ... JOIN C ON ...
--        每加一个表就要多一个 ON 条件，写完检查是不是每个 ON 都有
--        f.user_id = u.id 这个 ON 不能漏，否则会变成笛卡尔积
-- ✅ 答案：
SELECT c.customer_name,
       h.hospital_name,
       f.content,
       u.user_name,
       f.follow_time
FROM clue c
INNER JOIN hospital h   ON c.hospital_id = h.id
INNER JOIN clue_follow f ON c.id = f.clue_id
INNER JOIN sys_user u    ON f.user_id = u.id
ORDER BY f.follow_time;


-- ---------- 二、分组统计类（05-07）----------

-- 【Q05】GROUP BY 单表分组：每种状态各有多少条线索
-- 【业务场景】线索漏斗图 / 看板顶部的状态分布
-- 【要点】GROUP BY 之后 SELECT 里只能出现分组列和聚合函数
--        想让状态显示成文字，实际项目里要么在代码里映射，要么用 CASE WHEN
-- ✅ 答案：
SELECT status,
       COUNT(*) AS clue_count
FROM clue
GROUP BY status
ORDER BY status;


-- 【Q06】GROUP BY + HAVING：只看线索数 ≥ 2 的医院
-- 【业务场景】找出重点合作医院
-- 【要点】HAVING 是「分组之后再过滤」，WHERE 是「分组之前过滤行」。
--        这里不能用 WHERE COUNT(...) —— 聚合函数只能出现在 HAVING 里
-- ✅ 答案：
SELECT h.hospital_name,
       COUNT(c.id) AS clue_count
FROM hospital h
INNER JOIN clue c ON h.id = c.hospital_id
GROUP BY h.id, h.hospital_name
HAVING COUNT(c.id) >= 2
ORDER BY clue_count DESC;


-- 【Q07】多聚合函数：每家医院的线索数 + 最早 + 最近线索时间
-- 【业务场景】医院详情页的「合作概况」
-- 【要点】一个 GROUP BY 里可以同时用 COUNT / MIN / MAX / AVG / SUM
--        用 LEFT JOIN 保证 0 条线索的医院也出现（时间显示 NULL）
-- ✅ 答案：
SELECT h.hospital_name,
       COUNT(c.id)        AS clue_count,
       MIN(c.create_time) AS first_clue_time,
       MAX(c.create_time) AS last_clue_time
FROM hospital h
LEFT JOIN clue c ON h.id = c.hospital_id
GROUP BY h.id, h.hospital_name
ORDER BY clue_count DESC;


-- ---------- 三、子查询类（08-09）----------

-- 【Q08】子查询 IN：查「有跟进记录」的线索
-- 【业务场景】筛选已跟进的线索
-- 【要点】IN (子查询) 适合「子查询返回一列值」的场景。
--        等价的 EXISTS 写法性能通常更好（大表时）：
--          SELECT * FROM clue c WHERE EXISTS (SELECT 1 FROM clue_follow f WHERE f.clue_id = c.id);
-- ✅ 答案：
SELECT *
FROM clue
WHERE id IN (SELECT clue_id FROM clue_follow);


-- 【Q09】子查询 NOT EXISTS：查「一次都没跟进」的线索
-- 【业务场景】待跟进提醒列表
-- 【要点】⚠️ 别写 NOT IN！如果子查询结果里有 NULL，
--        NOT IN 会返回空集（因为 x NOT IN (1,2,NULL) 结果是 NULL 不是 true）。
--        用 NOT EXISTS 最安全。
-- ✅ 答案：
SELECT c.*
FROM clue c
WHERE NOT EXISTS (
    SELECT 1 FROM clue_follow f WHERE f.clue_id = c.id
);


-- ---------- 四、综合题（10）----------

-- 【Q10】三表 JOIN + 分组 + 排序：每家医院的跟进次数与最近跟进时间
-- 【业务场景】运营看板「各医院跟进活跃度」，按最近跟进时间倒序
-- 【要点】这题把前面所有东西串起来了：
--         三表 JOIN 取到跟进数据 → GROUP BY 按医院分组
--         → COUNT 算次数、MAX 算最近时间 → ORDER BY 排序
--        用 INNER JOIN 是因为「没有跟进的医院」不需要出现在活跃度排行里。
--        如果业务要求全都显示，就换成 LEFT JOIN，并把 COUNT(f.id) 用上
-- ✅ 答案：
SELECT h.hospital_name,
       COUNT(f.id)          AS follow_count,
       MAX(f.follow_time)   AS last_follow_time
FROM hospital h
INNER JOIN clue c        ON h.id = c.hospital_id
INNER JOIN clue_follow f ON c.id = f.clue_id
GROUP BY h.id, h.hospital_name
ORDER BY last_follow_time DESC;


-- ============================================================
-- 做完之后的验证清单
-- ============================================================
--   □ Q01 是 5 条、Q02 是 6 条，能说出为什么差一条（周八的 hospital_id 为 NULL）
--   □ Q03 里医院4 显示 0 条，能解释为什么必须用 COUNT(c.id) 而不是 COUNT(*)
--   □ Q04 能说出如果漏掉 f.user_id = u.id 会发生什么（笛卡尔积，条数暴涨）
--   □ Q06 能说出 WHERE 和 HAVING 的区别
--   □ Q09 能说出为什么不用 NOT IN
--   □ 随便挑一道，能在 EXPLAIN 里看到 key 用上了 idx_hospital_id
--
-- 全部打勾后：
--   cd "/c/Users/zengy/Desktop/快诊580/后端学习记录/java-week2"
--   git add .
--   git commit -m "feat: Day1 完成 10 道多表查询练习"
--   git push
-- ============================================================
