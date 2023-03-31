/* Postgre SQL */
select
    "wlkh",
    "yktkh",
    "yktzhlx",
    "yktyhlx",
    "ryh",
    "xm",
    "kzqbye",
    "kbzqbye",
    "xszqbye",
    "xsbzqbye",
    "djje",
    "zye",
    "ljykcs",
    "xbm",
    "dwmc",
    "sfzjlxm",
    "sfzjh",
    "lxdh",
    "yhzh",
    "zcrq",
    "gqrq",
    "zhbz",
    "tstamp"
from
    (
        SELECT
            A.SCARDSNR as WLKH
            /*物理卡号*/
            ,
            A.CUSTOMERID as YKTKH
            /*一卡通卡号*/
            ,
            CASE
                A.CARDKIND
                WHEN '1' THEN 'M1卡'
                WHEN '2' THEN 'CPU卡'
                WHEN '10' THEN '临时虚拟卡'
                ELSE '异常'
                END as YKTZHLX
            /*一卡通账号类型*/
            ,
            A.CARDSFID as YKTYHLX
            /*一卡通用户类型*/
            ,
            A.OUTID as RYH
            /*学号/职工号*/
            ,
            A.NAME as XM
            /*姓名*/
            ,
            C.oddfare AS KZQBYE
            /*卡主钱包余额*/
            ,
            C.suboddfare AS KBZQBYE
            /*卡补助钱包余额*/
            ,
            C.onlinemainoddfare AS XSZQBYE
            /*线上主钱包余额*/
            ,
            C.onlinesuboddfare AS XSBZQBYE
            /*线上补助钱包余额*/
            ,
            C.fzfare AS DJJE
            /*冻结金额*/
            ,
            C.totalfare AS ZYE
            /*总余额*/
            ,
            '' as LJYKCS
            /*累计用卡次数*/
            ,
            CASE
                A.SEX
                WHEN '1' THEN '1'
                WHEN '0' THEN '2'
                ELSE '0'
                END as XBM
            /*性别码*/
            ,
            D.dpname as DWMC
            /*单位名称*/
            ,
            A.CERTIFICATEID as SFZJLXM
            /*身份证件类型码*/
            ,
            A.IDCARDNO as SFZJH
            /*身份证件号*/
            ,
            '' as LXDH
            /*电话*/
            ,
            '' as YHZH
            /*银行账号*/
            ,
            to_char(A.OPENDT, 'YYYYMMDD') as ZCRQ
            /*注册日期*/
            ,
            to_char(A.NOUSEDATE, 'YYYYMMDD') as GQRQ
            /*过期日期*/
            ,
            A.STATUS as ZHBZ
            /*账户标志*/
            ,
            to_char(NOW(), 'YYYYMMDD HH24MISS') as TSTAMP
            /*时间戳*/
        FROM
            T_YKT_BASE_CUSTOMERS A
                left join T_YKT_BASE_CUSTOMERS_PHOTO B on A.outid = B.outid
                LEFT JOIN (
                /*只是为了获取余额（oddfare是卡主钱包余额，suboddfare是卡补助钱包余额，onlinemainoddfare是线上主钱包余额，onlinesuboddfare是线上补助钱包余额，fzfare是冻结金额，totalfare是总资产）*/
                SELECT
                    t.*,
                    c.dpcode,
                    c.dpname
                FROM
                    (
                        SELECT
                            s.customerid,
                            s.name,
                            s.custdept,
                            s.outid,
                            s.oddfare,
                            s.suboddfare,
                            s.oddfareacc,
                            s.suboddfareacc,
                            COALESCE (t.mainoddfare, 0) AS onlinemainoddfare,
                            COALESCE (t.suboddfare, 0) AS onlinesuboddfare,
                            COALESCE (t.creditoddfare, 0) AS onlinecreditoddfare,
                            COALESCE (p.planfare, 0) AS planfare,
                            COALESCE (q.plansubfare, 0) AS plansubfare,
                            COALESCE (r.planrepayfare, 0) AS planrepayfare,
                            COALESCE (r.plansubrepayfare, 0) AS plansubrepayfare,
                            COALESCE (SUM (g.fzfare), 0) AS fzfare,
                            COALESCE (SUM (g.subsidyfzfare), 0) AS subsidyfzfare,
                            (
                                            COALESCE (t.mainoddfare, 0) - COALESCE (t.creditoddfare, 0) + COALESCE (p.planfare, 0) - COALESCE (r.planrepayfare, 0)
                                ) AS mainfare,
                            (
                                        COALESCE (t.suboddfare, 0) + COALESCE (q.plansubfare, 0) - COALESCE (r.plansubrepayfare, 0)
                                ) AS subfare,
                            (
                                            s.oddfare + COALESCE (t.mainoddfare, 0) + COALESCE (t.creditoddfare, 0) + COALESCE (p.planfare, 0) - COALESCE (r.planrepayfare, 0) + COALESCE (SUM (g.fzfare), 0)
                                ) AS maintotalfare,
                            (
                                            s.suboddfare + COALESCE (t.suboddfare, 0) + COALESCE (q.plansubfare, 0) - COALESCE (r.plansubrepayfare, 0) + COALESCE (SUM (g.subsidyfzfare), 0)
                                ) AS subtotalfare,
                            (
                                                    s.oddfare + s.suboddfare + COALESCE (t.mainoddfare, 0) + COALESCE (t.creditoddfare, 0) + COALESCE (p.planfare, 0) - COALESCE (r.planrepayfare, 0) + COALESCE (SUM (g.fzfare), 0) + COALESCE (t.suboddfare, 0) + COALESCE (q.plansubfare, 0) - COALESCE (r.plansubrepayfare, 0) + COALESCE (SUM (g.subsidyfzfare), 0)
                                ) AS totalfare,
                            COALESCE (a.resetdt, TO_DATE ('9999-12-31', 'yyyy-MM-dd')) AS resetdt,
                            COALESCE (t.payver, 0) AS payver,
                            s.dpid
                        FROM
                            t_ykt_base_customers s
                                LEFT JOIN (
                                SELECT
                                    r.olid,
                                    COALESCE (SUM (r.mainopcount), 0) AS mainopcount,
                                    COALESCE (SUM (r.mainsaveopcount), 0) AS mainsaveopcount,
                                    COALESCE (SUM (r.subopcount), 0) AS subopcount,
                                    COALESCE (SUM (r.subsaveopcount), 0) AS subsaveopcount,
                                    COALESCE (SUM (r.creditopcount), 0) AS creditopcount,
                                    COALESCE (SUM (r.creditsaveopcount), 0) AS creditsaveopcount,
                                    COALESCE (SUM (r.mainoddfare), 0) AS mainoddfare,
                                    --20
                                    COALESCE (SUM (r.suboddfare), 0) AS suboddfare,
                                    COALESCE (SUM (r.creditoddfare), 0) AS creditoddfare,
                                    COALESCE (r.payver, 0) AS payver,
                                    COALESCE (r.dpid, 0) AS dpid
                                FROM
                                    (
                                        SELECT
                                            a.olid,
                                            a.dpid,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.opcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.opcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.opcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.saveopcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainsaveopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.saveopcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subsaveopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.saveopcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditsaveopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.oddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainoddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.oddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS suboddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.oddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditoddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.sumconsumefare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainsumconsumefare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.sumconsumefare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subsumconsumefare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.sumconsumefare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditsumconsumefare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.sumaddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainsumaddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.sumaddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subsumaddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.sumaddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditsumaddfare,
                                            a.payver
                                        FROM
                                            t_ykt_online_base_account a
                                                LEFT JOIN t_ykt_online_base_account_child b ON a.olid = b.olid
                                                AND a.dpid = b.dpid
                                    ) r
                                GROUP BY
                                    r.olid,
                                    r.dpid,
                                    r.payver
                            ) t ON s.customerid = t.olid
                                AND s.dpid = t.dpid
                                LEFT JOIN t_ykt_online_base_account_child a ON s.customerid = a.olid
                                AND s.dpid = t.dpid
                                AND a.wallettype = 11
                                LEFT JOIN (
                                SELECT
                                    a.customerid,
                                    a.dpid,
                                    COALESCE (SUM (a.opfare), 0) AS planfare
                                FROM
                                    t_ykt_rec_subsidy_plan a
                                WHERE
                                        a.notecase = 0
                                  AND a.affairstatus = 0
                                GROUP BY
                                    a.customerid,
                                    a.dpid
                            ) p ON s.customerid = p.customerid
                                AND s.dpid = p.dpid
                                LEFT JOIN (
                                SELECT
                                    a.customerid,
                                    a.dpid,
                                    COALESCE (SUM (a.opfare), 0) AS plansubfare
                                FROM
                                    t_ykt_rec_subsidy_plan a
                                WHERE
                                        a.notecase = 1
                                  AND a.affairstatus = 0
                                GROUP BY
                                    a.customerid,
                                    a.dpid
                            ) q ON s.customerid = q.customerid
                                AND s.dpid = q.dpid
                                LEFT JOIN (
                                SELECT
                                    a.customerid,
                                    a.dpid,
                                    COALESCE (SUM (opfare), 0) AS planrepayfare,
                                    COALESCE (SUM (subopfare), 0) AS plansubrepayfare
                                FROM
                                    (
                                        SELECT
                                            a.customerid,
                                            a.dpid,
                                            CASE
                                                SUBSTR (a.repaystatus, 1, 1)
                                                WHEN '0' THEN COALESCE (a.repayfare, 0)
                                                END AS opfare,
                                            CASE
                                                SUBSTR (a.repaystatus, 2, 1)
                                                WHEN '0' THEN COALESCE (a.subrepayfare, 0)
                                                END AS subopfare
                                        FROM
                                            t_ykt_rec_repay_plan a
                                        WHERE
                                                a.affairstatus <> 2
                                    ) a
                                GROUP BY
                                    a.customerid,
                                    a.dpid
                            ) r ON s.customerid = r.customerid
                                AND s.dpid = r.dpid
                                LEFT JOIN t_ykt_rec_card_make g ON s.customerid = g.customerid
                                AND g.cardsn < s.cardsn
                                AND g.cflg = 0
                                AND s.dpid = g.dpid
                        GROUP BY
                            s.customerid,
                            s.name,
                            s.custdept,
                            s.outid,
                            s.dpid,
                            t.payver,
                            a.resetdt,
                            s.oddfare,
                            s.suboddfare,
                            s.oddfareacc,
                            s.suboddfareacc,
                            p.planfare,
                            q.plansubfare,
                            r.planrepayfare,
                            r.plansubrepayfare,
                            t.mainoddfare,
                            t.suboddfare,
                            t.creditoddfare
                        UNION ALL
                        SELECT
                            s.customerid,
                            s.name,
                            s.custdept,
                            s.outid,
                            s.oddfare,
                            s.suboddfare,
                            s.oddfareacc,
                            s.suboddfareacc,
                            COALESCE (t.mainoddfare, 0) AS onlinemainoddfare,
                            COALESCE (t.suboddfare, 0) AS onlinesuboddfare,
                            COALESCE (t.creditoddfare, 0) AS onlinecreditoddfare,
                            COALESCE (p.planfare, 0) AS planfare,
                            COALESCE (q.plansubfare, 0) AS plansubfare,
                            COALESCE (r.planrepayfare, 0) AS planrepayfare,
                            COALESCE (r.plansubrepayfare, 0) AS plansubrepayfare,
                            COALESCE (SUM (g.fzfare), 0) AS fzfare,
                            COALESCE (SUM (g.subsidyfzfare), 0) AS subsidyfzfare,
                            (
                                                COALESCE (t.mainoddfare, 0) - COALESCE (t.creditoddfare, 0) + COALESCE (p.planfare, 0) - COALESCE (r.planrepayfare, 0) + COALESCE (SUM (g.fzfare), 0)
                                ) AS mainfare,
                            (
                                            COALESCE (t.suboddfare, 0) + COALESCE (q.plansubfare, 0) - COALESCE (r.plansubrepayfare, 0) + COALESCE (SUM (g.subsidyfzfare), 0)
                                ) AS subfare,
                            (
                                            s.oddfare + COALESCE (t.mainoddfare, 0) + COALESCE (t.creditoddfare, 0) + COALESCE (p.planfare, 0) - COALESCE (r.planrepayfare, 0) + COALESCE (SUM (g.fzfare), 0)
                                ) AS maintotalfare,
                            (
                                            s.suboddfare + COALESCE (t.suboddfare, 0) + COALESCE (q.plansubfare, 0) - COALESCE (r.plansubrepayfare, 0) + COALESCE (SUM (g.subsidyfzfare), 0)
                                ) AS subtotalfare,
                            (
                                                    s.oddfare + s.suboddfare + COALESCE (t.mainoddfare, 0) + COALESCE (t.creditoddfare, 0) + COALESCE (p.planfare, 0) - COALESCE (r.planrepayfare, 0) + COALESCE (SUM (g.fzfare), 0) + COALESCE (t.suboddfare, 0) + COALESCE (q.plansubfare, 0) - COALESCE (r.plansubrepayfare, 0) + COALESCE (SUM (g.subsidyfzfare), 0)
                                ) AS totalfare,
                            COALESCE (a.resetdt, TO_DATE ('9999-12-31', 'yyyy-MM-dd')) AS resetdt,
                            COALESCE (t.payver, 0) AS payver,
                            s.dpid
                        FROM
                            t_ykt_rec_writeoff s
                                LEFT JOIN (
                                SELECT
                                    r.olid,
                                    COALESCE (SUM (r.mainopcount), 0) AS mainopcount,
                                    COALESCE (SUM (r.mainsaveopcount), 0) AS mainsaveopcount,
                                    COALESCE (SUM (r.subopcount), 0) AS subopcount,
                                    COALESCE (SUM (r.subsaveopcount), 0) AS subsaveopcount,
                                    COALESCE (SUM (r.creditopcount), 0) AS creditopcount,
                                    COALESCE (SUM (r.creditsaveopcount), 0) AS creditsaveopcount,
                                    COALESCE (SUM (r.mainoddfare), 0) AS mainoddfare,
                                    --20
                                    COALESCE (SUM (r.suboddfare), 0) AS suboddfare,
                                    COALESCE (SUM (r.creditoddfare), 0) AS creditoddfare,
                                    COALESCE (r.payver, 0) AS payver,
                                    COALESCE (r.dpid, 0) AS dpid
                                FROM
                                    (
                                        SELECT
                                            a.olid,
                                            a.dpid,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.opcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.opcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.opcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.saveopcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainsaveopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.saveopcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subsaveopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.saveopcount
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditsaveopcount,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.oddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainoddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.oddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS suboddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.oddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditoddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.sumconsumefare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainsumconsumefare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.sumconsumefare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subsumconsumefare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.sumconsumefare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditsumconsumefare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 10 THEN b.sumaddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS mainsumaddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 11 THEN b.sumaddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS subsumaddfare,
                                            COALESCE (
                                                CASE
                                                    b.wallettype
                                                    WHEN 12 THEN b.sumaddfare
                                                    ELSE 0
                                                    END,
                                                0
                                                ) AS creditsumaddfare,
                                            a.payver
                                        FROM
                                            t_ykt_v_online_rec_writeoff_account a
                                                LEFT JOIN t_ykt_v_online_rec_writeoff_child b ON a.olid = b.olid
                                                AND a.dpid = b.dpid
                                    ) r
                                GROUP BY
                                    r.olid,
                                    r.dpid,
                                    r.payver
                            ) t ON s.customerid = t.olid
                                AND s.dpid = t.dpid
                                LEFT JOIN t_ykt_online_base_account_child a ON s.customerid = a.olid
                                AND s.dpid = t.dpid
                                AND a.wallettype = 11
                                LEFT JOIN (
                                SELECT
                                    a.customerid,
                                    a.dpid,
                                    COALESCE (SUM (a.opfare), 0) AS planfare
                                FROM
                                    t_ykt_rec_subsidy_plan a
                                WHERE
                                        a.notecase = 0
                                  AND a.affairstatus = 0
                                GROUP BY
                                    a.customerid,
                                    a.dpid
                            ) p ON s.customerid = p.customerid
                                AND s.dpid = p.dpid
                                LEFT JOIN (
                                SELECT
                                    a.customerid,
                                    a.dpid,
                                    COALESCE (SUM (a.opfare), 0) AS plansubfare
                                FROM
                                    t_ykt_rec_subsidy_plan a
                                WHERE
                                        a.notecase = 1
                                  AND a.affairstatus = 0
                                GROUP BY
                                    a.customerid,
                                    a.dpid
                            ) q ON s.customerid = q.customerid
                                AND s.dpid = q.dpid
                                LEFT JOIN (
                                SELECT
                                    a.customerid,
                                    a.dpid,
                                    COALESCE (SUM (opfare), 0) AS planrepayfare,
                                    COALESCE (SUM (subopfare), 0) AS plansubrepayfare
                                FROM
                                    (
                                        SELECT
                                            a.customerid,
                                            a.dpid,
                                            CASE
                                                SUBSTR (a.repaystatus, 1, 1)
                                                WHEN '0' THEN COALESCE (a.repayfare, 0)
                                                END AS opfare,
                                            CASE
                                                SUBSTR (a.repaystatus, 2, 1)
                                                WHEN '0' THEN COALESCE (a.subrepayfare, 0)
                                                END AS subopfare
                                        FROM
                                            t_ykt_rec_repay_plan a
                                        WHERE
                                                a.affairstatus <> 2
                                    ) a
                                GROUP BY
                                    a.customerid,
                                    a.dpid
                            ) r ON s.customerid = r.customerid
                                AND s.dpid = r.dpid
                                LEFT JOIN t_ykt_rec_card_make g ON s.customerid = g.customerid
                                AND g.cardsn < s.cardsn
                                AND g.cflg = 0
                                AND s.dpid = g.dpid
                                LEFT JOIN t_ykt_rec_temporarily h ON s.customerid = h.customerid
                                AND s.dpid = h.dpid
                                AND h.flag = 0
                        GROUP BY
                            s.customerid,
                            s.name,
                            s.custdept,
                            s.outid,
                            s.dpid,
                            t.payver,
                            a.resetdt,
                            s.oddfare,
                            s.suboddfare,
                            s.oddfareacc,
                            s.suboddfareacc,
                            p.planfare,
                            q.plansubfare,
                            r.planrepayfare,
                            r.plansubrepayfare,
                            t.mainoddfare,
                            t.suboddfare,
                            t.creditoddfare,
                            h.oddfare,
                            h.subsidyoddfare
                    ) t
                        LEFT JOIN t_ykt_base_custdept c ON t.custdept = c.dpcode
                        AND t.dpid = c.dpid --   WHERE t.outid =‘20181024’
                ORDER BY
                    c.dpname,
                    t.customerid
            ) C ON A.CUSTOMERID = C.CUSTOMERID
                left join t_ykt_base_custdept D on SUBSTRING(A.custdept, 1, 9) = D.dpcode
    ) t
