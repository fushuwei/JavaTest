/* Postgre SQL */
select
    "wybs",
    "kcbm",
    "kcmc",
    "kcywmc",
    "zhouxs",
    "zxs",
    "llxs",
    "syxs",
    "kcjj",
    "kcjbm",
    "kclbm",
    "jcbm",
    "cksm",
    "skyylxm",
    "kcfzrgh",
    "kcksdwbm",
    "kcksrq",
    "sfyjc",
    "sfyx",
    "tstamp"
from
    (
        SELECT
            SN AS WYBS,
            /*唯一标识*/
            课程代码 AS KCBM,
            /*课程编码*/
            课程名称 AS KCMC,
            /*课程名称*/
            英文名称 AS KCYWMC,
            /*课程英文名称*/
            NULL AS ZHOUXS,
            /*周学时*/
            NULL AS ZXS,
            /*总学时*/
            NULL AS LLXS,
            /*理论学时*/
            NULL AS SYXS,
            /*实验学时*/
            课程简介 AS KCJJ,
            /*课程简介*/
            NULL AS KCJBM,
            /*课程级别码*/
            NULL AS KCLBM,
            /*课程类别码*/
            NULL AS JCBM,
            /*教材编码*/
            参考书 AS CKSM,
            /*参考书目*/
            NULL AS SKYYLXM,
            /*授课语言类型码*/
            NULL AS KCFZRGH,
            /*课程负责人工号*/
            所属院系代码 AS KCKSDWBM,
            /*课程开设单位编码*/
            NULL AS KCKSRQ,
            /*课程开设日期*/
            NULL AS SFYJC,
            /*是否有教材*/
            NULL AS SFYX,
            /*是否有效*/
            to_char(NOW(), 'YYYYMMDD HH24MISS') AS TSTAMP
            /*时间戳*/
        FROM
            t_yjs_d培课程信息
    ) t
