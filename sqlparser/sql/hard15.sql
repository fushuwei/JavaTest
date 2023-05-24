SELECT Trim(a.EMP_JBN)                                                                         GH,/*	工号	*/
       DECODE(a.EMP_SCN_COD, '00', NULL, EMP_SCN_COD)                                          SZDWBM,/*	所在单位编码	*/
       Trim(a.EMP_NAM)                                                                         XM,/*	姓名	*/
       Trim(a.EMP_BFR)                                                                         CYM,/*	曾用名	*/
       DECODE(a.EMP_SEX, '男', '1', '女', '2')                                                   XBM,/*	性别码	*/
       replace(replace(replace(a.EMP_BRT, '-', ''), ',', ''), '.', '')                         CSRQ,/*	出生日期	*/
       M.DM                                                                                    CSDM,/*	出生地码	*/
       N.DM                                                                                    JGM,/*	籍贯码	*/
       B.DM                                                                                    MZM, /*民族码*/
       O.DM                                                                                    GJDQM,/*	国籍/地区码	*/
       C.DM                                                                                    SFZJLXM,/*	身份证件类型码	*/
       replace(Trim(EMP_DCR_NUM), ',', '')                                                     SFZJH,/*	身份证件号	*/
       D.DM                                                                                    HYZKM, /*婚姻状况码*/
       E.DM                                                                                    ZZMMM,/*	政治面貌码	*/
       F.DM                                                                                    JKZKM,/*	健康状况码	*/
       replace(replace(replace(a.EMP_JIW_YYM, '-', ''), ',', ''), '.', '')                     CJGZNY,/*	参加工作年月	*/
       replace(replace(replace(a.EMP_TRS_YYM, '-', ''), ',', ''), '.', '')                     LXRQ,/*	来校日期	*/
       G.DM                                                                                    JZGLXM,/*	教职工类型码	*/
       H.DM                                                                                    JZGLBM,/*	教职工类别码	*/
       I.DM                                                                                    JZGLYM,/*	教职工来源码	*/
       DECODE(a.EMP_ESL_TYP, '在编', '11', '人代', '11', '临时', '11', '离职', '07', '退休', '01', '99') JZGDQZTM,/*	教职工当前状态码	*/
       K.DM                                                                                    ZGXLM,/*	最高学历码	*/
       L.DM                                                                                    ZGXWM,/*	最高学位码	*/
       a.PST_ADD                                                                               TXDZ,/*	通讯地址	*/
       a.MBL_NUM                                                                               YDDH,/*	移动电话	*/
       a.EML                                                                                   DZYX,/*	电子邮箱	*/
       a.TEL_NUM                                                                               JSTXH,/*	即时通讯号	*/
       DECODE(a.PST_NAM, '无', null, 'null', null, a.PST_NAM)                                   GWMC,/*	岗位名称	*/
       DECODE(a.IS_PRESIDE, '是', '1', '否', '0', null)                                          SFZCGZ,/*	是否主持工作	*/
       p.DM                                                                                    ZYJSZWM,/*	专业技术职务码	*/
       decode(q.DM, null, '499', q.DM)                                                         ZYJSZWJBM,/*	专业技术职务级别码	*/
       r.DM                                                                                    ZJGWDJM,/*	专技岗位等级码	*/
       DECODE(s.DM, null, '299', s.DM)                                                         ZYJBM,/*	职员级别码	*/
       DECODE(a.EMP_DBL_TCH, '是', '1', '否', '0', null)                                         SFSSXJS,/*是否双师型教师*/
       decode(t1.DBL_FLG, '是', '1', '否', '0', null)                                            SFSJT, /*是否双肩挑*/
       DECODE(t1.NST_FLG, '是', '1', '否', '0', null)                                            SFFDY,/*是否辅导员*/
       to_char(sysdate, 'yyyymmddhh24miss')                                                    TSTAMP /*时间戳*/
FROM (SELECT a.*
      FROM (SELECT a.*,
                   ROW_NUMBER() OVER (PARTITION BY EMP_DCR_NUM ORDER BY UPT_TIM DESC)   row_num
            FROM T_RS_EMP_ESL a
            where EMP_DCR_NUM is not null) a
      WHERE a.row_num = 1
      UNION ALL
      SELECT a.*, null row_num
      FROM T_RS_EMP_ESL a
      where EMP_DCR_NUM is null) A
         LEFT JOIN DM_XB_MZM B ON A.EMP_VLK = B.MS
         LEFT JOIN DM_XB_SFZJLXM C ON A.EMP_DCR_TYP = C.MS
         LEFT JOIN DM_GB_HYZKM D ON A.EMP_MRT = D.MS
         LEFT JOIN DM_XB_ZZMMM E ON A.EMP_PLT = E.MS
         LEFT JOIN DM_GB_JKZKM F ON A.EMP_HLT = F.MS
         LEFT JOIN DM_XB_JZGLXM G ON A.EMP_ESL_TYP = G.MS
         LEFT JOIN DM_XB_JZGLBM H ON A.EMP_TYP = H.MS
         LEFT JOIN DM_XB_JZGLYM I ON A.EMP_SRC = I.MS
         LEFT JOIN DM_XB_JZGDQZTM J ON A.EMP_STT = J.MS
         LEFT JOIN DM_XB_XLM K ON A.GET_DCT = K.MS
         LEFT JOIN DM_XB_XWM L ON A.DGR_NAM = L.MS
         LEFT JOIN DM_XB_XZQHM M ON Trim(A.EMP_HMP) = M.MS
         LEFT JOIN DM_XB_XZQHM N ON Trim(A.EMP_NTV) = N.MS
         LEFT JOIN DM_XB_GJDQM O ON Trim(A.EMP_NTN) = O.MS
         LEFT JOIN DM_GB_ZYJSZWMZHB p on a.PRF_TTL = p.MS
         LEFT JOIN DM_XB_ZYJSZWJBM q on a.TEN_PST = q.MS
         LEFT JOIN DM_XB_ZJGWDJM r on a.TEN_LVL = r.MS
         LEFT JOIN DM_XB_ZYJBM S on a.MAN_LVL = s.MS
         LEFT JOIN (SELECT a.*
                    FROM (SELECT a.*, ROW_NUMBER() OVER (PARTITION BY MAIN_ID ORDER BY UPT_TIM DESC)   row_num
                          FROM T_RS_EMP_ESL_PST_NGG a) a
                    WHERE a.row_num = 1) t1 on a.PKID = t1.MAIN_ID
WHERE a.EMP_ESL_TYP <> '学生'
  and a.UPT_ACT !='D'
AND a.EMP_JBN IS NOT NULL
