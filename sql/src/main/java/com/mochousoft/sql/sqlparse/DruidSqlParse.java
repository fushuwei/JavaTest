package com.mochousoft.sql.sqlparse;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleSysdateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectSubqueryTableSource;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DruidSqlParse {

    public static void main(String[] args) {
        JSONObject jResult = new JSONObject(true);

        if (args.length != 2) {
            jResult.put("status", false);
            jResult.put("message", "参数个数有误");
        } else {
            try {
                jResult = parseSQL(args[0], args[1]);
            } catch (Exception e) {
                e.printStackTrace();
                jResult.put("status", false);
                jResult.put("message", e.getMessage());
            }
        }

        System.out.println(formatJson(jResult));
    }

    /**
     * 解析SQL语句，只针对select语句，不包含insert、update和delete等语句解析
     *
     * @param sql          一条完整的SQL语句
     * @param databaseType 数据类型: oracle, mysql, sqlserver, postgresql等
     */
    private static JSONObject parseSQL(String sql, String databaseType) {
        JSONObject jResult = new JSONObject(true);

        if (StringUtils.isBlank(sql)) {
            jResult.put("status", false);
            jResult.put("message", "SQL不允许为空");
            return jResult;
        }
        if (StringUtils.isBlank(databaseType)) {
            jResult.put("status", false);
            jResult.put("message", "数据库类型不允许为空");
            return jResult;
        }

        String[] supportTypeList = new String[]{JdbcConstants.ORACLE, JdbcConstants.MYSQL, JdbcConstants.SQL_SERVER, JdbcConstants.POSTGRESQL};
        if (!Arrays.asList(supportTypeList).contains(databaseType)) {
            jResult.put("status", false);
            jResult.put("message", String.format("参数错误: 不支持[%s]类型数据库, 可选项: [%s, %s, %s, %s]", databaseType,
                    JdbcConstants.ORACLE, JdbcConstants.MYSQL, JdbcConstants.SQL_SERVER, JdbcConstants.POSTGRESQL));
            return jResult;
        }

        // 替换SQL语句中的中文括号
        sql = sql.replaceAll("（", "(").replaceAll("）", ")");

        // 处理Oracle数据库的XMLAGG函数，例如：XMLAGG(XMLPARSE(CONTENT A || ',' WELLFORMED) ORDER BY B).GETCLOBVAL()
        if (sql.toUpperCase().contains("XMLAGG(XMLPARSE(CONTENT ")) {
            sql = sql.replaceAll("(?i)XMLAGG\\(XMLPARSE\\(CONTENT ", "XMLAGG(XMLPARSE(");
            sql = sql.replaceAll("(?i)' WELLFORMED\\) ", "') ");
        }

        // 解析用户输入的SQL, 生成AST
        SQLStatement statement = SQLUtils.parseStatements(sql, databaseType).get(0);
        // 只解析查询语句，即SQL以select声明的语句
        if (statement instanceof SQLSelectStatement) {
            SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
            // 获取转成AST后的当前SQL语句
            SQLSelectQuery selectQuery = selectStatement.getSelect().getQuery();

            // 开始解析
            JSONObject jQueryTree = new JSONObject();
            buildQueryTree(jQueryTree, selectQuery, databaseType);

            // 对解析结果进行二次解析
            JSONObject jParseResult = new JSONObject(true);
            parseQueryTree(jParseResult, jQueryTree);


            jResult.put("status", true);
            jResult.put("message", jParseResult);
            return jResult;
        } else {
            jResult.put("status", false);
            jResult.put("message", "只支持查询语句解析");
            return jResult;
        }
    }

    // 构建查询语法树
    private static void buildQueryTree(JSONObject jQueryTree, SQLSelectQuery selectQuery, String databaseType) {
        if (selectQuery instanceof SQLSelectQueryBlock) { // 不带union的SQL语句
            SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;

            // 获取当前SQL最外层查询的表
            SQLTableSource tableSource = selectQueryBlock.getFrom();

            // 获取当前SQL最外层查询的字段
            List<SQLSelectItem> selectItemList = selectQueryBlock.getSelectList();

            // 封装当前SQL最外层查询的表来源信息
            buildTableSource(jQueryTree, tableSource, selectItemList, databaseType);

        } else if (selectQuery instanceof SQLUnionQuery) { // 带union的SQL语句
            SQLUnionQuery unionQuery = (SQLUnionQuery) selectQuery;
            for (SQLSelectQuery query : unionQuery.getRelations()) {
                buildQueryTree(jQueryTree, query, databaseType);
            }
        }
    }

    // 构建当前查询语法树的表信息
    private static void buildTableSource(JSONObject jQueryTree, SQLTableSource tableSource, List<SQLSelectItem> selectItemList, String databaseType) {
        if (tableSource instanceof SQLExprTableSource) { // 单表
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) { // 表名不带schema
                JSONObject jTableSource = getTableSource("", ((SQLIdentifierExpr) expr).getName(), tableSource.getAlias(), false);

                // 封装当前SQL最外层查询字段
                addSelectItem(jTableSource.getJSONArray("select_items"), selectItemList);

                addTableSource(jQueryTree, jTableSource, tableSource.getAlias());
            } else if (expr instanceof SQLPropertyExpr) { // 表名带有schema
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                JSONObject jTableSource = getTableSource(propertyExpr.getOwnernName(), propertyExpr.getName(), tableSource.getAlias(), false);

                // 封装当前SQL最外层查询字段
                addSelectItem(jTableSource.getJSONArray("select_items"), selectItemList);

                addTableSource(jQueryTree, jTableSource, tableSource.getAlias());
            } else if (expr instanceof OracleDbLinkExpr) {
                JSONObject jTableSource = getTableSource("", ((SQLIdentifierExpr) ((OracleDbLinkExpr) expr).getExpr()).getName(), tableSource.getAlias(), false);

                // 封装当前SQL最外层查询字段
                addSelectItem(jTableSource.getJSONArray("select_items"), selectItemList);

                addTableSource(jQueryTree, jTableSource, tableSource.getAlias());
            }
        } else if (tableSource instanceof SQLJoinTableSource) { // 关联查询
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) tableSource;
            buildTableSource(jQueryTree, joinTableSource.getLeft(), selectItemList, databaseType);
            buildTableSource(jQueryTree, joinTableSource.getRight(), selectItemList, databaseType);
        } else if (tableSource instanceof SQLSubqueryTableSource) { // 子查询
            // 获取当前查询的源信息
            JSONObject jTableSource = getTableSource("", "", tableSource.getAlias(), true);

            // 封装当前SQL最外层查询字段
            addSelectItem(jTableSource.getJSONArray("select_items"), selectItemList);

            // 获取当前子查询语句中最外层查询的表
            Object subTableSource = getSubQueryTableSource(tableSource, databaseType);

            // 判断最外层的表是单表还是union查询
            if (subTableSource instanceof SQLTableSource || subTableSource == null) { // 单表（注意：此处null也表示是单表）
                // 判断获取到的最外层的表是否为空
                if (subTableSource != null) {
                    // 获取当前子查询语句中最外层查询的字段
                    List<SQLSelectItem> subSelectItemList = getSubQuerySelectItemList(tableSource, databaseType);
                    // 递归继续封装子查询的源信息
                    buildTableSource(jTableSource.getJSONObject("sub_query"), (SQLTableSource) subTableSource, subSelectItemList, databaseType);
                }
                // 将获取的和下钻封装后的源信息加入解析结果树中
                addTableSource(jQueryTree, jTableSource, tableSource.getAlias());
            } else if (subTableSource instanceof SQLUnionQuery) { // union查询
                // 先将当前子查询的别名作为key加入到查询树中
                addTableSource(jQueryTree, jTableSource, tableSource.getAlias());
                // 递归封装每个union子句的源信息
                buildQueryTree(jTableSource.getJSONObject("sub_query"), (SQLUnionQuery) subTableSource, databaseType);
            }
        } else if (tableSource instanceof SQLUnionQueryTableSource) { // union查询
            // 获取当前查询的源信息
            JSONObject jTableSource = getTableSource("", "", tableSource.getAlias(), true);
            // 封装当前SQL最外层查询字段
            addSelectItem(jTableSource.getJSONArray("select_items"), selectItemList);
            // 先将当前子查询的别名作为key加入到查询树中
            addTableSource(jQueryTree, jTableSource, tableSource.getAlias());
            // 递归封装每个union子句的源信息
            buildQueryTree(jTableSource.getJSONObject("sub_query"), ((SQLUnionQueryTableSource) tableSource).getUnion(), databaseType);
        }
    }

    // 获取子查询SQL语句中最外层查询的表
    private static Object getSubQueryTableSource(SQLTableSource tableSource, String databaseType) {
        switch (databaseType) {
            case JdbcConstants.ORACLE: {
                SQLSelectQuery subSelectQuery = ((OracleSelectSubqueryTableSource) tableSource).getSelect().getQuery();
                if (subSelectQuery instanceof OracleSelectQueryBlock) {
                    OracleSelectQueryBlock selectQuery = (OracleSelectQueryBlock) subSelectQuery;
                    return selectQuery.getFrom();
                } else if (subSelectQuery instanceof SQLUnionQuery) {
                    return subSelectQuery;
                }
            }
            case JdbcConstants.MYSQL: {
                SQLSelectQuery subSelectQuery = ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
                if (subSelectQuery instanceof MySqlSelectQueryBlock) {
                    MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) subSelectQuery;
                    return selectQuery.getFrom();
                } else if (subSelectQuery instanceof SQLUnionQuery) {
                    return subSelectQuery;
                }
            }
            case JdbcConstants.SQL_SERVER: {
                SQLSelectQuery subSelectQuery = ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
                if (subSelectQuery instanceof SQLServerSelectQueryBlock) {
                    SQLServerSelectQueryBlock selectQuery = (SQLServerSelectQueryBlock) subSelectQuery;
                    return selectQuery.getFrom();
                } else if (subSelectQuery instanceof SQLUnionQuery) {
                    return subSelectQuery;
                }
            }
            case JdbcConstants.POSTGRESQL: {
                SQLSelectQuery subSelectQuery = ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
                if (subSelectQuery instanceof PGSelectQueryBlock) {
                    PGSelectQueryBlock selectQuery = (PGSelectQueryBlock) subSelectQuery;
                    return selectQuery.getFrom();
                } else if (subSelectQuery instanceof SQLUnionQuery) {
                    return subSelectQuery;
                }
            }
        }
        return null;
    }

    // 获取子查询SQL语句中最外层查询的字段
    private static List<SQLSelectItem> getSubQuerySelectItemList(SQLTableSource tableSource, String databaseType) {
        List<SQLSelectItem> subSelectItemList = new ArrayList<>();
        switch (databaseType) {
            case JdbcConstants.ORACLE: {
                OracleSelectQueryBlock selectQuery = (OracleSelectQueryBlock) ((OracleSelectSubqueryTableSource) tableSource).getSelect().getQuery();
                subSelectItemList = selectQuery.getSelectList();
                break;
            }
            case JdbcConstants.MYSQL: {
                MySqlSelectQueryBlock selectQuery = (MySqlSelectQueryBlock) ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
                subSelectItemList = selectQuery.getSelectList();
                break;
            }
            case JdbcConstants.SQL_SERVER: {
                SQLServerSelectQueryBlock selectQuery = (SQLServerSelectQueryBlock) ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
                subSelectItemList = selectQuery.getSelectList();
                break;
            }
            case JdbcConstants.POSTGRESQL: {
                PGSelectQueryBlock selectQuery = (PGSelectQueryBlock) ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
                subSelectItemList = selectQuery.getSelectList();
                break;
            }
        }
        return subSelectItemList;
    }

    // 封装当前最外层查询的表信息结构并返回
    private static JSONObject getTableSource(String schema, String table, String alias, boolean isSubQuery) {
        JSONObject jTableSource = new JSONObject(true);
        jTableSource.put("schema", removeDoubleQuote(schema));
        jTableSource.put("table", removeDoubleQuote(table));
        jTableSource.put("alias", StringUtils.isBlank(alias) ? "" : removeDoubleQuote(alias));
        jTableSource.put("select_items", new JSONArray());
        if (isSubQuery) {
            jTableSource.put("sub_query", new JSONObject());
        }
        return jTableSource;
    }

    // 将当前最外层查询的字段封装后添加至表信息中
    private static void addSelectItem(JSONArray jSelectItemArray, List<SQLSelectItem> selectItemList) {
        for (SQLSelectItem selectItem : selectItemList) {
            SQLExpr expr = selectItem.getExpr();

            // 过滤一些无意义的查询项, 比如: SYSDATE
            if (expr instanceof OracleSysdateExpr) {
                continue;
            }

            // 获取查询项的别名 (注意: 没有显示指定别名的情况，此处得到的就是查询的字段名)
            String itemAlias = selectItem.getAlias();

            // 如果没有指定别名，则通过如下方式获取别名
            if (itemAlias == null) {
                if (selectItem.getExpr() instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) selectItem.getExpr();
                    itemAlias = identifierExpr.getName();
                } else if (selectItem.getExpr() instanceof SQLPropertyExpr) {
                    SQLPropertyExpr propertyExpr = (SQLPropertyExpr) selectItem.getExpr();
                    itemAlias = propertyExpr.getName();
                } else {
                    itemAlias = selectItem.getExpr().toString();
                }

                // 通过以上方式获取的别名必须去除空格、换行符和Tab符等
                if (itemAlias != null) {
                    itemAlias = StringUtils.deleteWhitespace(itemAlias);
                }
            }

            // 构造查询项数据结构
            JSONObject jSelectItem = new JSONObject();
            jSelectItem.put("alias", removeDoubleQuote(itemAlias));

            // 封装查询项的来源信息
            JSONArray jSelectItemSourceArray = new JSONArray();
            buildSelectItemSource(jSelectItemSourceArray, expr);

            jSelectItem.put("fields", jSelectItemSourceArray);

            jSelectItemArray.add(jSelectItem);
        }
    }

    // 构建查询字段的源信息
    private static void buildSelectItemSource(JSONArray jSelectItemSourceArray, SQLExpr expr) {
        if (expr != null) {
            JSONObject jSelectItemSource = new JSONObject(true);

            if (expr instanceof SQLAllColumnExpr) { // "*"号 (示例: select * from table)
                jSelectItemSource.put("table", "");
                jSelectItemSource.put("field", "*");
                jSelectItemSourceArray.add(jSelectItemSource);

            } else if (expr instanceof SQLIdentifierExpr) { // 单字段 (示例: select id from table)
                SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
                jSelectItemSource.put("table", "");
                jSelectItemSource.put("field", removeDoubleQuote(identifierExpr.getName()));
                jSelectItemSourceArray.add(jSelectItemSource);

            } else if (expr instanceof SQLPropertyExpr) { // 别名.字段 (示例: select t.id from table t)
                SQLPropertyExpr propertyExpr = (SQLPropertyExpr) expr;
                jSelectItemSource.put("table", StringUtils.isBlank(propertyExpr.getOwnernName()) ? "" : removeDoubleQuote(propertyExpr.getOwnernName()));
                jSelectItemSource.put("field", removeDoubleQuote(propertyExpr.getName()));
                jSelectItemSourceArray.add(jSelectItemSource);

            } else if (expr instanceof SQLMethodInvokeExpr) { // 参数 (示例: select to_char(id) from table)
                SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) expr;
                List<SQLExpr> argumentList = methodInvokeExpr.getArguments();
                for (SQLExpr subExpr : argumentList) {
                    buildSelectItemSource(jSelectItemSourceArray, subExpr);
                }

            } else if (expr instanceof SQLCaseExpr) { // 条件判断, CASE WHEN THEN
                SQLCaseExpr caseExpr = (SQLCaseExpr) expr;
                for (SQLCaseExpr.Item item : caseExpr.getItems()) {
                    buildSelectItemSource(jSelectItemSourceArray, item.getValueExpr());
                }
                buildSelectItemSource(jSelectItemSourceArray, caseExpr.getValueExpr());
                buildSelectItemSource(jSelectItemSourceArray, caseExpr.getElseExpr());
            }
        }
    }

    // 将表信息添加至查询树结构中
    private static void addTableSource(JSONObject jQueryTree, JSONObject jTableSource, String alias) {
        if (StringUtils.isBlank(alias)) {
            alias = "Unnamed";
        }

        JSONArray jArray = jQueryTree.getJSONArray(alias);
        if (jArray == null) {
            jArray = new JSONArray();
        }

        if (jTableSource != null) {
            jArray.add(jTableSource);
        }

        jQueryTree.put(removeDoubleQuote(alias), jArray);
    }

    // 二次解析，封装标准的数据结构，此方法只获取用户最外层查询的字段，如果最外层查询的是"*"号，并且from的是子查询，则跳过最外层*号，从而获取子查询中的字段
    private static void parseQueryTree(JSONObject jParseResult, JSONObject jQueryTree) {
        // 获取当前SQL查询项
        String firstTableAlias = "";
        for (String tableAlias : jQueryTree.keySet()) {
            // 获取第一个表名称信息，union查询场景使用，用于确保union前后的查询字段名一致
            if (StringUtils.isBlank(firstTableAlias)) {
                firstTableAlias = tableAlias;
            }
            // 开始层层解析
            JSONArray jTableSourceArray = jQueryTree.getJSONArray(tableAlias);
            for (int i = 0; i < jTableSourceArray.size(); i++) {
                JSONObject jTableSource = jTableSourceArray.getJSONObject(i);
                JSONArray jSelectItemArray = jTableSource.getJSONArray("select_items");
                for (int j = 0; j < jSelectItemArray.size(); j++) {
                    JSONObject jSelectItem = jSelectItemArray.getJSONObject(j);

                    // 获取查询项的名称, 不能直接 jSelectItem.getString("alias") 获取，因为union的时候，查询的字段别名不一样时可能导致解析结果不正确
                    String selectItemName = jQueryTree.getJSONArray(firstTableAlias).getJSONObject(0).getJSONArray("select_items").getJSONObject(j).getString("alias");

                    // 真实来源字段数组，一个查询项可能来自多个字段的情况
                    JSONArray jSelectItemSourceArray = jParseResult.getJSONArray(selectItemName);
                    if (jSelectItemSourceArray == null) {
                        jSelectItemSourceArray = new JSONArray();
                    }

                    /*
                     * 二次解析，获取当前查询项的真实来源字段
                     * 如果当前SQL最外层查询项是"*"号，并且最外层查询项只有一个，并且最外层查询的表是子查询对应的结果集，则忽略最外层，从而获取子查询中的查询项
                     */
                    if ("*".equals(jSelectItem.getString("alias")) && jSelectItemArray.size() == 1 && jTableSource.getJSONObject("sub_query") != null) {
                        parseQueryTree(jParseResult, jTableSource.getJSONObject("sub_query"));
                    } else {
                        // 获取当前查询项的最终来源
                        getFinalSelectItemSource(jSelectItemSourceArray, jTableSource, jSelectItem);
                        // 封装最终结果
                        jParseResult.put(selectItemName, jSelectItemSourceArray);
                    }
                }
            }
        }
    }

    // 获取查询项最终来源的真实字段
    private static void getFinalSelectItemSource(JSONArray jSelectItemSourceArray, JSONObject jTableSource, JSONObject jSelectItem) {
        // 获取当前SQL来自的子查询结果集表
        JSONObject jSubTable = jTableSource.getJSONObject("sub_query");

        // 如果当前表来源信息下没有子查询语句，说明当前表来源信息已经是最终查询的表
        if (jSubTable == null) {
            jSelectItem.getJSONArray("fields").forEach(jSelectItemSource -> {
                JSONObject jField = (JSONObject) jSelectItemSource;
                String currTableSource = jTableSource.getString("alias");
                if (StringUtils.isBlank(currTableSource) || "".equals(jField.getString("table")) ||
                        currTableSource.equalsIgnoreCase(jField.getString("table"))) {
                    JSONObject jObject = new JSONObject(true);
                    jObject.put("schema", jTableSource.getString("schema"));
                    jObject.put("table", jTableSource.getString("table"));
                    jObject.put("field", jField.getString("field"));
                    if (!jSelectItemSourceArray.contains(jObject)) {
                        jSelectItemSourceArray.add(jObject);
                    }
                }
            });
        } else {
            // 如果有子查询，则将当前查询项的来源信息循环下钻溯源
            jSelectItem.getJSONArray("fields").forEach(jSelectItemSource -> {
                // 判断当前查询项是否来自当前来源表，如果是则继续下钻溯源，如果否，则不在当前来源表继续查找当前字段的来源
                if (!"".equals(((JSONObject) jSelectItemSource).getString("table")) && !jTableSource.getString("alias").equalsIgnoreCase(((JSONObject) jSelectItemSource).getString("table"))) {
                    return;
                }
                // 遍历子查询中所有的表
                String subFirstTableAlias = "";
                for (String subTableAlias : jSubTable.keySet()) {
                    // 获取第一个表名称信息，union查询场景使用，用于确保union前后的查询字段名一致
                    if (StringUtils.isBlank(subFirstTableAlias)) {
                        subFirstTableAlias = subTableAlias;
                    }
                    // 开始层层解析
                    JSONArray jSubTableSourceArray = jSubTable.getJSONArray(subTableAlias);
                    for (int i = 0; i < jSubTableSourceArray.size(); i++) {
                        JSONObject jSubTableSource = jSubTableSourceArray.getJSONObject(i);
                        JSONArray jSubSelectItemArray = jSubTableSource.getJSONArray("select_items");
                        for (int j = 0; j < jSubSelectItemArray.size(); j++) {
                            JSONObject jSubSelectItem = jSubSelectItemArray.getJSONObject(j);

                            // 获取查询项的名称, 不能直接 jSelectItem.getString("alias") 获取，因为union的时候，查询的字段别名不一样时可能导致解析结果不正确
                            String subSelectItemName = jSubTable.getJSONArray(subFirstTableAlias).getJSONObject(0).getJSONArray("select_items").getJSONObject(j).getString("alias");

                            /*
                             * 二次解析，获取当前查询项的真实来源字段
                             * 如果当前SQL最外层查询项是"*"号，并且最外层查询项只有一个，并且最外层查询的表是子查询对应的结果集，则忽略最外层，从而获取子查询中的查询项
                             */
                            if ("*".equals(subSelectItemName) && jSubSelectItemArray.size() == 1 && jTableSource.getJSONObject("sub_query") != null) {
                                getFinalSelectItemSource(jSelectItemSourceArray, jSubTableSource, jSelectItem);
                            } else {
                                if (subSelectItemName.equalsIgnoreCase(((JSONObject) jSelectItemSource).getString("field"))) {
                                    // 递归下钻，直到获取到真实来源字段为止
                                    getFinalSelectItemSource(jSelectItemSourceArray, jSubTableSource, jSubSelectItem);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    // 去除字符串中的双引号
    private static String removeDoubleQuote(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("\"", "");
    }

    // json格式化
    private static String formatJson(Object object) {
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
    }

}
