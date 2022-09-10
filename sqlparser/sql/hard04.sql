SELECT
    T.bjdm,
    /*班级代码*/
    T.bjjc,
    /*班级简称*/
    T.bjmc,
    /*班级名称*/
    T.bjrs,
    /*班级人数*/
    T.bzrgh,
    /*班主任职工号*/
    T.bzxh,
    /*班长学号*/
    T.dwh,
    /*单位号*/
    T1.DWMC,
    /*单位名称*/
    T.fdyh,
    /*辅导员号*/
    T.jbny,
    /*建班年月*/
    T.ssnj,
    /*所属年级*/
    T.xq,
    /*校区*/
    T.xz,
    /*学制*/
    T.zydm,
    /*专业代码*/
    T2.zymc zydm_ms,
    /*专业名称*/
    T.zyfxdm,
    /*专业方向代码*/
    --T4.ZYFXMC ZYFXDM_MS,/*专业方向*/
    DECODE(T.xslbm, '05', '421', '02', '442', '06', '411', '99', '42102', T.xslbm) xslbm,
    /*学生类别*/
    T.pyccm,
    /*培养层次码*/
    t3.mc pycc,
    /*培养层次*/
    T.jwfdy,
    /*教务辅导员*/
    T.bz,
    /*备注*/
    T.tstamp /*时间戳*/
FROM
    usr_udw.t_gxxx_bjjbxx T
        LEFT JOIN usr_udw.t_gxxx_yxsdwxx T1 ON
            T.DWH = T1.DWDM
        LEFT JOIN USR_UDW.t_gxjx_bzkszyxx T2 ON
            T.ZYDM = T2.ZYDM
        LEFT JOIN USR_UDW.dm_gb_pyccm T3 ON
            T.PYCCM = t3.dm
--LEFT JOIN USR_ODS.t_xg_om_zyfx T4 ON T.ZYFXDM=T4.ZYFXDM
