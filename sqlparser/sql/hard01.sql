SELECT xnxq                id,
       JC                  JC_CODE,
       JC                  JC_NAME,
       KSSJ || '-' || JSSJ BEGIN_TIME, /**开始时间(8:00:00)**/
       KSSJ || '-' || JSSJ END_TIME /**结束时间(9:00:00)**/
FROM (select DJKSSJ KSSJ, DJJSSJD JSSJ, A.JC
      from (select DJKSSJ, row_number() over(order by DJKSSJ) jc
            from (SELECT DISTINCT CASE
                                      WHEN LENGTH(DJKSSJ) = 3
                                          THEN 0 || SUBSTR(DJKSSJ, 1, 1) || ':' || SUBSTR(DJKSSJ, 2, 2)
                                      ELSE SUBSTR(DJKSSJ, 1, 2) || ':' || SUBSTR(DJKSSJ, 3, 2) END AS DJKSSJ
                  FROM T_GXJX_BKSPKXXXX)) A
               LEFT JOIN
           (select DJJSSJD, row_number() over(order by DJJSSJD) jc
            from (SELECT DISTINCT CASE
                                      WHEN LENGTH(DJJSSJ) = 3
                                          THEN 0 || SUBSTR(DJJSSJ, 1, 1) || ':' || SUBSTR(DJJSSJ, 2, 2)
                                      ELSE SUBSTR(DJJSSJ, 1, 2) || ':' || SUBSTR(DJJSSJ, 3, 2) END AS DJJSSJD
                  FROM T_GXJX_BKSPKXXXX)) B
           on a.jc = b.jc)
         cross join
     (select distinct substr(xnxq, 0, 9) || substr(xnxq, -1, 1) xnxq from T_GXJX_BKSPKXXXX)
