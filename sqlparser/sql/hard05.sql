SELECT
    DISTINCT
    substr(T.cjsj,1,8)AS 日期,
    T.szst AS  食堂选项,
    T.dk AS 所在档口,
    '微信支付' AS 支付方式,
    nvl(T1.CS,'0') AS 早餐次数,
    nvl(T2.CS,'0')  AS 午餐次数,
    nvl(T3.CS,'0')AS 晚餐次数,
    nvl(T4.CS,'0')  AS 宵夜次数,
    nvl(T1.CS,'0')+nvl(T2.CS,'0')+nvl(T3.CS,'0')+nvl(T4.CS,'0')  AS 总次数
FROM T_GXWXXYK_ZFXX T
         LEFT JOIN (SELECT substr(cjsj,1,8)AS RQ,szst,dk,to_char(COUNT(*))  AS CS FROM T_GXWXXYK_ZFXX WHERE CD LIKE '%早餐%' GROUP  BY substr(cjsj,1,8),szst,dk )T1
                   ON SUBSTR(T.cjsj,1,8)=T1.RQ AND T1.SZST=T.szst AND T.DK=T1.DK
         LEFT JOIN (SELECT substr(cjsj,1,8)AS RQ,szst,dk,to_char(COUNT(*))  AS CS FROM T_GXWXXYK_ZFXX WHERE CD LIKE '%午餐%' GROUP  BY substr(cjsj,1,8),szst,dk )T2
                   ON SUBSTR(T.cjsj,1,8)=T2.RQ AND T2.SZST=T.szst AND T.DK=T2.DK
         LEFT JOIN (SELECT substr(cjsj,1,8)AS RQ,szst,dk,to_char(COUNT(*))  AS CS FROM T_GXWXXYK_ZFXX WHERE CD LIKE '%晚餐%' GROUP  BY substr(cjsj,1,8),szst,dk )T3
                   ON SUBSTR(T.cjsj,1,8)=T3.RQ AND T3.SZST=T.szst AND T.DK=T3.DK
         LEFT JOIN (SELECT substr(cjsj,1,8)AS RQ,szst,dk,to_char(COUNT(*))  AS CS FROM T_GXWXXYK_ZFXX WHERE CD LIKE '%宵夜%' GROUP  BY substr(cjsj,1,8),szst,dk )T4
                   ON SUBSTR(T.cjsj,1,8)=T4.RQ AND T4.SZST=T.szst AND T.DK=T4.DK
