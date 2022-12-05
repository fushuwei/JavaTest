SELECT
    SQND	SQND	,/*	申请年度	*/
    decode(SFLX,'是','1','否','0','9')	SFLX	,/*	是否立项	*/
    XMBH	XMBH	,/*	项目编号	*/
    JXMC ,/*	奖项名称	*/
    JXLB ,/*	奖项类别	*/
    ZFTJR	ZFTJR	,/*	中方推荐人	*/
    BTJWGZJ	BTJWGZJ	,/*	被推荐外国专家	*/
    LXR	LXR	,/*	联系人	*/
    to_char(TBRQ,'yyyymmdd') 	TBRQ	,/*	填报日期	*/
    WYKXJZWXM	WYKXJZWXM	,/*	我院科学家中文姓名	*/
    WYKXJGH	WYKXJGH	,/*	我院科学家工号	*/
    /*(SELECT  decode(dwsscj,'2',(SELECT dwmc FROM T_GHB_YXSDWXX where dwbm=c.LSDWH )  ,'1',DWMC,DWMC)  FROM  T_GHB_YXSDWXX c  where   c.DWMC=b.DEPT_NAME	) BMMC ,部门名称*/
    BMMC BMMC ,/*部门名称*/
    decode(WGKXJXB,'男','1','女','2','9')	WGKXJXB	,/*	外国科学家性别	*/
    WGKXJZWXM	WGKXJZWXM	,/*	外国科学家中文姓名	*/
    WGKXJYWXM	WGKXJYWXM	,/*	外国科学家英文姓名	*/
    WGKXJGJ	WGKXJGJ	,/*	外国科学家国籍	*/
    to_char(WGKXJCSRQ,'yyyymmdd')  	WGKXJCSRQ	,/*	外国科学家出生日期	*/
    WGKXJZY	WGKXJZY	,/*	外国科学家专业	*/
    WGKXJZW	WGKXJZW	,/*	外国科学家职务	*/
    WGKXJXLXW	WGKXJXLXW	,/*	外国科学家学历学位	*/
    WGKXJGWZWGZDW	WGKXJGWZWGZDW	,/*	外国科学家国外中文工作单位	*/
    WGKXJGNGZDW	WGKXJGNGZDW	,/*	外国科学家国内工作单位	*/
    to_char(sysdate,'yyyymmddhh24miss') 	TSTAMP	/*	时间戳	*/
FROM   T_ZGKXYARPGL_QNKXJGJHZJ
