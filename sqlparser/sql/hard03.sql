SELECT
        X1.BIANH/*||'-'||X1.BH ||'-'||X1.APBID ||'-'||X2.TZDBH*/ ||'-'||X2.BH||'-'||X2.APBID    AS ZJ,/*主键(开课编号-上课教师ID-课程安排ID*/
        X1.JSXM,/*教师名称*/
        X1.JSGH,/*教工号*/
        X1.XNXQ,/*学年学期*/
        X1.KCMC,/*课程名称*/
        (CASE WHEN X2.HSJSGH IS NULL AND X2.HSJSMC IS NULL THEN '否' ELSE '是' END )  AS SFHS,
        X2.BH,
        X2.APBID,
        X2.HSJSMC,/*合上教师姓名*/
        X2.HSJSGH,/*合上教师工号*/
        CASE
            WHEN X1.KCDL IN ('通识课','普通课') THEN '理论课'
            ELSE X1.KCDL END AS KCDL,/*课程大类*/
        X1.KCLB,/*课程类别*/
        X1.SKDX,/*授课对象*/
        '本科生' AS  XSLB,/*学生类别*/
        X1.XKRS,/*选课人数*/
        X1.XSDW,/*学时单位*/
        X1.ZXS,/*总学时*/
        X1.JHXS,/*计划学时*/
        TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') AS TSTAMP /*时间戳*/
FROM
    (
        SELECT
            T5.TZDLB,
            T5.BIANH,  /*通知单号*/
            T5.XNXQ,/*学年学期*/
            T5.KCBH,/*课程编号*/
            T5.KCMC,/*课程名称*/
/*T4.BIANH AS BH,*/
/*TT4.APBID,*/
            T4.JSGH,/*教工号*/
            T4.JSXM1  AS  JSXM,/*教师名称*/
            ''  AS  HSLS,/*合上老师*/
            T5.KCLB,/*课程类别*/
            T5.KCDL,/*课程大类*/
            CASE WHEN T5.KETLSMC LIKE '临时%' THEN T5.KETLSMC ELSE T5.KETMC  END AS SKDX,/*授课对象*/
            T5.SFQYWK  AS  SFQYKC,/*是否全英课程*/
            ''  AS  SFJK,    /*是否金课*/
            T5.XKRS  AS  XKRS,/*选课人数*/
            CASE  WHEN  T4.XSDW='1'  THEN  '节'  WHEN    T4.XSDW='2'    THEN  '周'  WHEN  T4.XSDW='3'  THEN  '天'
                END  XSDW,/*学时单位*/
            T5.ZXS,/*总学时*/
            T5.JHXS,/*计划学时*/
            T4.SSDWMC
        FROM
--开课通知单是主表
(
    SELECT M.XNXQ,M.SJXS,M.BIANH,M.KCBH,M.KKDWMC,M.KETMC,M.KETLSMC,M.ZXS,M.JHXS,M1.DMMC AS KCLB ,M2.KCMC,M2.KCDLM,M3.DMMC AS KCDL,M2.SFQYWK,M.XKRS,M.TZDLB,M.LLXS --理论学时
    FROM    USR_UDW.T_GXJX_BZKSKKXX  M
                INNER  JOIN  USR_UDW.DM_XB_JW_KCLBM  M1
                             ON  M.KCLBM=M1.DM
                INNER  JOIN  USR_UDW.T_GXJX_BZKSKCXX  M2
                             ON  M.KCBH=M2.KCID
                INNER  JOIN  USR_UDW.DM_XB_JW_KCDLM  M3
                             ON  M2.KCDLM=M3.DM
    WHERE  SFTK='0'   AND  XKRS!=0
)T5
    INNER  JOIN
(SELECT T4.TZDBH,T4.JSGH,T4.JSXM1,T4.SSDWMC,T4.SFSK,T4.SFZRKLS,T4.XSDW/*,T4.BIANH,T4.APBID*/ FROM
    (SELECT T3.TZDBH, T2.BIANH,T2.APBID,T2.JSGH,T6.JSXM1,T6.SSDWMC,T2.SFSK,T2.SFZRKLS,T3.XSDW,T2.PXH
     FROM  USR_UDW.T_GXJX_BKSJXAPB  T3
               INNER  JOIN  USR_UDW.T_GXJX_BZKSKCAP  T2
                            ON  T2.APBID=T3.BIANH  AND T3.PK='1' AND T2.SFSK='1'
               INNER  JOIN    USR_UDW.T_GXJX_JXRYJBXX  T6
                              ON  T2.JSGH=T6.GH WHERE  T3.XSHIFL='1'
    ) T4
 GROUP BY T4.TZDBH,T4.JSGH,T4.JSXM1,T4.SSDWMC,T4.SFSK,T4.SFZRKLS,T4.XSDW/*,T4.BIANH,T4.APBID*/) T4
ON T4.TZDBH=T5.BIANH
    )X1
        INNER  JOIN
    -----------------------合上教师---------------------
        (
            SELECT DISTINCT
                R.BIANH AS BH,R.APBID,R.JSGH,R.JSXM1,R.HSJSGH,R.HSJSMC,R1.TZDBH  FROM
                                                                                     (SELECT
                                                                                          A.BIANH,
                                                                                          A.APBID,
                                                                                          A.JSGH,
                                                                                          A.JSXM1,
                                                                                          (SELECT LISTAGG(JSGH,',') WITHIN GROUP (ORDER BY PXH) FROM USR_UDW.T_GXJX_BZKSKCAP B WHERE B.APBID=A.APBID AND A.JSGH<>B.JSGH AND B.SFSK='1') AS HSJSGH,
                                                                                     (SELECT LISTAGG(JSXM1,',') WITHIN GROUP (ORDER BY PXH) FROM (SELECT T.BIANH,T.APBID,T.JSGH,T.XNXQ,T1.JSXM1,T.PXH FROM USR_UDW.T_GXJX_BZKSKCAP T
                                                                                         INNER JOIN USR_UDW.T_GXJX_JXRYJBXX T1 ON T.JSGH=T1.GH AND T.SFSK='1')C WHERE C.APBID=A.APBID AND A.JSXM1<>C.JSXM1 ) AS HSJSMC,
                                                                                     A.XNXQ FROM
(SELECT T.BIANH,T.APBID,T.JSGH,T.XNXQ,T1.JSXM1 FROM USR_UDW.T_GXJX_BZKSKCAP T INNER JOIN USR_UDW.T_GXJX_JXRYJBXX T1 ON T.JSGH=T1.GH AND T.SFSK='1')A)R
        INNER JOIN USR_UDW.T_GXJX_BKSJXAPB R1
                   ON R1.BIANH=R.APBID AND R1.PK='1'
    )X2
ON  X1.BIANH=X2.TZDBH  AND X1.JSXM=X2.JSXM1
WHERE X1.KCDL IN ('普通课','通识课')
ORDER BY ZJ DESC
