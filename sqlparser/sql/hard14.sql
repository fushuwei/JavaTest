select
    a.*,
    b.EMAIL email,
    b.MOBILE sjh
from
    (
        select
            T.Jzggh AS ZGH,
            T.XM AS XM,
            '' AS YWXM,
            T.XMPY AS XMJP,
            T.SZYXBC AS SZDWDM,
            T.CYM AS ZYM,
            T.XBM AS XBM,
            to_char(T.CSRQ,
                    'yyyy-mm-dd')AS CSRQ,
            T.CSDHR AS CSDM,
            T.Jgmhr AS JG,
            T.MZM AS MZM,
            T.GJM AS GJDQM,
            case
                when t.zjlxm in ('01', '91') then '1'
                when t.zjlxm in ('02', 'A') then 'A'
                when t.zjlxm in ('03', '2') then '2'
                when t.zjlxm in ('04', '3') then '3'
                when t.zjlxm = '08' then 'B'
                when t.zjlxm = '4' then '4'
                when t.zjlxm = '5' then '5'
                when t.zjlxm = '9' then '9'
                when t.zjlxm in ('Z', '99')then 'Z'
                when t.zjlxm = '6' then '6'
                when t.zjlxm = '7' then '7'
                when t.zjlxm = '8' then '8'
                else null
                end as SFZJLXM,
            T.ZJHM AS SFZJH,
            T.ZJYXQ AS SFZJYXQ,
            T.HYZKM AS HYZKM,
            decode(T.GATQM,
                   '00',
                   '100',
                   T.GATQM)AS GATQWM,
            T.ZZMMM AS ZZMMM,
            T.JKZK AS JKZKM,
            T.XYZJ AS XYZJM,
            T.XX AS XXM,
            t.ssdwm AS SZKS,
            T.JFXS AS JFXS,
            T.SZBZYX AS BZYXBC,
            T.SZBZEJBM AS BZXSKS,
            T.RBDJRQ AS RBDJRQ,
            to_char(t.sccjgzny,
                    'yyyy-mm-dd') AS CJGZRQ,
            to_char(T.LXNY,
                    'yyyy-mm-dd') AS LXRQ,
            T.ZZRQ AS ZZRQ,
            T.JRBDWXS AS JRBDWXS,
            T.JZGLY AS JZGLY,
            T.HWJL AS HWJL,
            decode(T.JZGLB,
                   '53',
                   '100',
                   'A1',
                   '11',
                   'A2',
                   '13',
                   'C',
                   '30',
                   'D',
                   '40',
                   'A5',
                   '50',
                   'B',
                   '20',
                   'A3',
                   '12',
                   'A4',
                   '14',
                   'A6',
                   'A6',
                   NULL) AS JZGLB,
            T.RYSF AS RYSF,
            T.XSFQSRQ AS XSFQSRQ,
            T.YRFS AS YRFS,
            T.QDHTQK AS QDHTQK,
            t.jsbdrq AS HTQKBDRQ,
            T.ZGZTM AS ZGZT,
            t.dqzt AS ZBZT,
            to_char(t.jyrq,
                    'yyyy-mm-dd') AS ZBZTQSRQ,
            T.ZGXWID AS ZGXWCC,
            T.ZGXW AS ZGXW,
            T.SXWDW AS SXWDW,
            to_char(T.SXWRQ,
                    'yyyy-mm-dd') AS SXWRQ,
            decode(T.ZGXL,
                   '0',
                   '99',
                   T.ZGXL) AS ZGXL,
            T.BYXX AS BYXXHDW,
            T.SXZY AS SXZY,
            T.XZ AS XZ,
            to_char(T.RXRQ,
                    'yyyy-mm-dd') AS RXRQ,
            to_char(T.BYNY,
                    'yyyy-mm-dd') AS BYRQ,
            t.jszc AS ZYJSZW,
            t.zwjb AS ZYJSZWJB,
            to_char(t.JSZCPDNY,
                    'yyyy-mm-dd') as ZYJSZWPDRQ,
            t.przw AS PRZYJSZW,
            '' AS PRZYJSZWJB,
            to_char(t.przwny,
                    'yyyy-mm-dd') AS PRZYJSZWQSRQ,
            t.dzzwmc AS DZZW,
            T.DZZWJB AS DZZWJB,
            T.RZBMM AS RZBM,
            T.RZBMMC AS RZBMMC,
            T.RZRQ AS RZRQ,
            to_char(T.RXZWJBRQ,
                    'yyyy-mm-dd') AS RXZWJBRQ,
            T.RYFLM AS RYFL,
            '' AS GRJSZW,
            t.jsgzdj AS GRJSDJ,
            to_char(t.JSZGNY,
                    'yyyy-mm-dd') AS GRDJPDRQ,
            T.GWLB AS GWLB,
            T.GWDJ AS GWDJ,
            to_char(T.GWPRNY,
                    'yyyy-mm-dd') AS GWPRQSRQ,
            T.SFFDY AS SFFDY,
            T.SFZZCXXLZXGZ AS SFZZCSXLZXGZ,
            T.SFCYXLZXZGZS AS SFCYXLZXZGZS,
            t.gwmc AS XNGW,
            gw.mc AS ZYGWMC,
            T.SFSJT AS SFSJT,
            T.SJTLB AS SJTLB,
            T.SJT AS SJTDJ,
            t.zzmmm AS ZZMM,
            to_char(T.CJDPSJ,
                    'yyyy-mm-dd') AS CJDPRQ,
            T.CJDPSZDW AS CJDPSZDW,
            t.csgz AS XZYCSXK,
            t.XKM AS YJXK,
            T.EJXK AS EJXK,
            T.XKMLJY AS XKMLKJ,
            T.XKML AS XKSML,
            T.XCSZY AS XCSZYYJFX,
            T.SJH AS YDDH,
            /*case when T.SJH like '111%' or T.SJH like '12222%' or T.SJH like '123456%' or T.SJH like '10000%' then null
         else T.SJH end AS YDDH, */
            T.EMAIL AS DZXX,
            T.GZDH AS BGDH,
            T.BGDD AS BGDD,
            T.JTZZ AS JTZZ,
            sysdate AS GCSJ
        from
            T_RS_JZGJBXX T
                left join
            (
                select
                    *
                from
                    (
                        select
                            v.Jzggh,
                            vv.dzzwmc,
                            row_number() over (partition by vv.zgh
			order by
				vv.rzny desc) r,
                            vv.rzny
                        from
                            yzu_ods.T_RS_JZGJBXX v
                                left join
                            yzu_ods.t_rs_dzzwxx vv on
                                    v.zgh = vv.zgh
                        where
                                rzzk = '1'
                          and vv.dzzwmc != '33')
                where
                        r = 1) tt
            on
                    t.Jzggh = tt.Jzggh
                left join t_rs_zygw gw on
                    t.zygw = gw.dm ) a
        left join yzu_sfrz.rjsfrz_yzdxsjh b on
            a.zgh = b.userid
where
        zgh not in('7000', '7001', '7002', '0', 'cs002', 'cs003', '8000')
