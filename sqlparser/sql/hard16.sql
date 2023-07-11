SELECT T.*, T2.MOBILE AS SJHM2
FROM (
/*1，本科生*/
         SELECT T1.XM,
                T1.XH                                     RYH,
                T1.XBM,
                T4.MS                                  AS XB,
                '身份证'                                  AS ZJLX,
                T1.SFZJH                                  ZJH,
                T2.YXSH                                AS DWDM,
                T5.DWMC,
                NULL                                   AS SJDWH,
                NULL                                   AS SJDWMC,
                '1'                                    AS RYLXM,
                '本科生'                                     RYLX,
                T3.YDDH                                AS SJHM,
                T3.DZXX                                AS YX,
                NULL                                   AS XQ,
                DECODE(T2.XJZT, '在籍', '1', '不在籍', '0') AS SFXXRY,
                DECODE(T1.SFZX, '1', '是', '0', '否')    AS SFXNRY
         FROM T_GXXS_BKSJBXX T1
                  INNER JOIN T_GXXS_BKSXJJBXX T2 ON T1.XH = T2.XH
                  LEFT JOIN T_GXXS_BKSGRTXXX T3 ON T1.XH = T3.XH
                  LEFT JOIN DM_GB_XBM T4 ON T1.XBM = T4.DM
                  LEFT JOIN T_GXXX_YXSDWXX T5 ON T5.DWH = T2.YXSH
         UNION ALL
/*2,研究生,4非全日制研究生*/
         SELECT T1.XM,
                T1.XH                        RYH,
                T1.XBM,
                T3.MS   AS                   XB,
                '身份证'   AS                   ZJLX,
                T1.SFZJH                     ZJH,
                T2.YXSH AS                   DWDM,
                T4.DWMC,
                NULL    AS                   SJDWH,
                NULL    AS                   SJDWMC,
                CASE SUBSTR(T1.XH, '6', '1')
                    WHEN '1' THEN '2'
                    WHEN '2' THEN '2'
                    WHEN '3' THEN '4'
                    WHEN '4' THEN '4'
                    END                      RYLXM,
                CASE SUBSTR(T1.XH, '6', '1')
                    WHEN '1' THEN '研究生'
                    WHEN '2' THEN '研究生'
                    WHEN '3' THEN '非全日制研究生'
                    WHEN '4' THEN '非全日制研究生'
                    END                      RYLX,
                NULL    AS                   SJHM,
                NULL    AS                   YX,
                NULL    AS                   XQ,
                DECODE(XSDQZTM, 1, '1', '0') SFXXRY,
                DECODE(XSDQZTM, 1, '是', '否') SFXNRY
         FROM T_GXXS_YJSJBXX T1
                  LEFT JOIN T_GXXS_YJSXJJBXX T2 ON T1.XH = T2.XH
                  LEFT JOIN DM_GB_XBM T3 ON T1.XBM = T3.DM
                  LEFT JOIN T_GXXX_YXSDWXX T4 ON T2.YXSH = T4.DWH
/*3教职工,8劳务派遣人员,-6单位聘用人员*/
         UNION ALL
         SELECT T1.XM,
                T1.ZGH                    RYH,
                T1.XBM,
                T4.MS                  AS XB,
                '身份证'                  AS ZJLX,
                T1.SFZJH                  ZJH,
                SUBSTR(T1.SZDWH, 1, 3) AS DWDM,
                T5.DWMC,
                rpad(T1.SZDWH, 5, '0') AS SJDWH,
                T6.DWMC                AS SJDWMC,
                CASE
                    WHEN T2.YRFSDM IN ('204', '102', '201', '221', '101', '220', '219', '216', '222', '225') THEN '3'
                    WHEN T2.YRFSDM IN ('224', '223', '300', '226') THEN '8'
                    WHEN T2.YRFSDM IN ('218') THEN '6'
                    ELSE '9'
                    END                   RYLXM,
                CASE
                    WHEN T2.YRFSDM IN ('204', '102', '201', '221', '101', '220', '219', '216', '222', '225') THEN '教职工'
                    WHEN T2.YRFSDM IN ('224', '223', '300', '226') THEN '劳务派遣人员'
                    WHEN T2.YRFSDM IN ('218') THEN '单位聘用人员'
                    ELSE '其他'
                    END                   RYLX,
                T3.YDDH                AS SJHM,
                T3.DZXX                AS YX,
                NULL                   AS XQ,
                CASE
                    WHEN T1.DQZTM IN ('04', '15', '18', '11', '12', '22', '23', '14', '26') THEN '1'
                    ELSE '0'
                    END                   SFXXRY,
                CASE
                    WHEN T1.DQZTM IN ('04', '11', '12', '22') THEN '是'
                    ELSE '否'
                    END                   SFXNRY
         FROM T_GXJG_JZGJBXX T1
                  LEFT JOIN T_GXJG_JZGZXXX T2 ON T1.ZGH = T2.ZGH
                  LEFT JOIN T_GXJG_JZGGRTXFSXX T3 ON T1.ZGH = T3.ZGH
                  LEFT JOIN DM_GB_XBM T4 ON T1.XBM = T4.DM
                  LEFT JOIN T_GXXX_YXSDWXX T5 ON SUBSTR(T1.SZDWH, 1, 3) = T5.DWH
                  LEFT JOIN T_GXXX_YXSDWXX T6 ON rpad(T1.SZDWH, 5, '0') = T6.DWH

/*5留学生*/
         UNION ALL
         SELECT T1.HZXM                            AS XM,
                T1.XH                              AS RYH,
                DECODE(T1.XBM, '1', '2', '0', '1') AS XBM,
                DECODE(T1.XBM, '1', '男', '2', '女') AS XB,
                '护照'                               AS ZJLX,
                T1.HZHM                            AS ZJH,
                T3.DWH                             AS DWDM,
                T4.YXSH                            AS DWMC,
                NULL                               AS SJDWH,
                NULL                               AS SJDWMC,
                '5'                                AS RYLXM,
                '留学生'                              AS RYLX,
                T5.DQLXDH                          AS SJHM,
                T5.SQRGRDJXX                       AS YX,
                NULL                               AS XQ,
                DECODE(T4.XJZTM, '0', '1', '0')       SFXXRY,
                DECODE(T4.XJZTM, '0', '是', '否')       SFXNRY
         FROM T_GXWS_LHLXSJBXX T1
                  LEFT JOIN T_GXWS_LHLXSXJXX T4 ON T1.XH = T4.XH
                  LEFT JOIN T_GXWS_LHLXSTXXX T5 ON T1.XH = T5.XH
                  LEFT JOIN T_GXXX_YXSDWXX T3 ON T3.DWMC = T4.YXSH
         WHERE T3.DWCC = '1'
/*6后勤聘用人员*/
         UNION ALL
         SELECT T1.XM,
                T1.RYGH                              AS RYH,
                T1.XBM,
                T3.MS                                AS XB,
                '身份证'                                AS ZJLX,
                T1.SFZJH                             AS ZJH,
                SUBSTR(T1.SZBMH, 1, 3)               AS DWDM,
                T2.DWMC,
                NULL                                 AS SJDWH,
                NULL                                 AS SJDWMC,
                '6'                                  AS RYLXM,
                '后勤聘用人员'                             AS RYLX,
                T1.LXDH                              AS SJHM,
                NULL                                 AS YX,
                NULL                                 AS XQ,
                DECODE(T1.DQZTM, '1', '1', '0', '0') AS SFXXRY,
                DECODE(T1.DQZTM, '1', '是', '0', '否') AS SFXNRY
         FROM T_GXJG_HQRYXXB T1
                  LEFT JOIN T_GXXX_YXSDWXX T2 ON T2.DWH = SUBSTR(T1.SZBMH, 1, 3)
                  LEFT JOIN DM_GB_XBM T3 ON T1.XBM = T3.DM
         WHERE T1.ID LIKE '6%'
/*7离退休人员*/
         UNION ALL
         SELECT T1.XM,
                T1.LTXH                            AS RYH,
                DECODE(T1.XBM, '男', '1', '女', '2') AS XBM,
                T1.XBM                             AS XB,
                '身份证'                              AS ZJLX,
                T1.SFZJH                           AS ZJH,
                '517'                              AS DWDM,
                '离退休工作处'                           AS DWMC,
                NULL                               AS SJDWH,
                NULL                               AS SJDWMC,
                '7'                                AS RYLXM,
                '离退休人员'                            AS RYLX,
                YDDH                               AS SJHM,
                NULL                               AS YX,
                NULL                               AS XQ,
                CASE T1.SFLS
                    WHEN '否' THEN '1'
                    ELSE '0'
                    END                               SFXXRY,
                CASE T1.SFLS
                    WHEN '否' THEN '是'
                    ELSE '否'
                    END                               SFXNRY
         FROM T_GXJG_JZGLTXXX T1
         WHERE LENGTH(T1.LTXH) = 5
/*9协作单位人员*/
         UNION ALL
         SELECT A.XM                               AS XM,
                A.RYH                              AS RYH,
                A.XBM                              AS XBM,
                DECODE(A.XBM, '1', '男', '2', '女')  AS XB,
                '身份证'                              AS ZJLX,
                A.SFZJH                            AS ZJH,
                SUBSTR(A.SSDWH, 1, 3)              AS DWDM,
                C.DWMC                             AS DWMC,
                NULL                               AS SJDWH,
                NULL                               AS SJDWMC,
                '9'                                AS RYLXM,
                '协作单位人员'                           AS RYLX,
                A.YDDH                             AS SJHM,
                NULL                               AS YX,
                NULL                               AS XQ,
                DECODE(A.SFYX, '1', '1', '0', '0') AS SFXXRY,
                DECODE(A.SFYX, '1', '是', '0', '否') AS SFXNRY

         FROM T_GXJG_XZDWRYXX A
                  LEFT JOIN T_GXXX_YXSDWXX C ON C.DWH = SUBSTR(A.SSDWH, 1, 3)) T
         LEFT JOIN USR_ODS.T_BSDT_SYS_UICM_USER T2 ON T2.ID_NUMBER = T.RYH
