SELECT
    concat(T_GHXT_TABLE_BYICE.NJ, concat(T_GHXT_TABLE_BYICE.ZYDM, concat(T_GHXT_TABLE_BYICE.KCDM, T_GHXT_TABLE_BYICE.PY))) AS ID,
    T_GHXT_TABLE_BYICE.NAME AS NAME,
    T_GHXT_TABLE_BYICE.SEX AS SEX,
    T_GHXT_TABLE_BYICE.CJ AS CJ
FROM
    T_GHXT_TABLE_BYICE
