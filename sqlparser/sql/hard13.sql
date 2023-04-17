select
    "XXTSYYID",
    "XXJGDM",
    "XXJGMC",
    "XYDTXXBT",
    "XYDTXXFBFWLJ",
    "DTFBSJ"
from
    (
        SELECT
            '12794197362620' | | ROWID AS XXTSYYID,
      /*主键数据唯一性标识*/
      '12410000419043018D' AS XXJGDM,
      /*学校机构代码*/
      '河南工业职业技术学院' AS XXJGMC,
      /*学校机构名称*/
      WBTITLE AS XYDTXXBT,
      /*校园动态信息标题*/
      CASE
        WHEN WBLINKURL IS NOT NULL THEN WBLINKURL
        ELSE 'http://www.hnpi.edu.cn/info/' | | WBTREEID | | '/' | | WBNEWSID | | '.htm'
      END XYDTXXFBFWLJ,
      /*校园动态信息外部访问链接*/
      TO_CHAR(SUBSTR(WBDATE, 1, 10)) AS DTFBSJ
      /*动态发布时间*/
        FROM
            USER_ODS.T_WZQ_WBNEWS
        WHERE
            OWNER = '1451295937'
          AND WBTREEID IN ('1117', '1118')
          AND WBAUDITING = 1
          AND TO_CHAR(SUBSTR(WBDATE, 1, 4)) >= '2022'
          AND TO_CHAR(TO_DATE(SUBSTR(WBDATE, 1, 10), 'YYYY-MM-DD'), 'IW') = TO_CHAR(SYSDATE, 'IW') -1
    ) t
