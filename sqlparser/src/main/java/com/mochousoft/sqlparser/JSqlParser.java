package com.mochousoft.sqlparser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

// https://github.com/JSQLParser/JSqlParser/wiki#examples
public class JSqlParser {

    public static void main(String[] args) {

        String sql1 = "select id, xm name, nl as age from t_user";
        String sql2 = "select id, xm name, nl as age from (select * from t_user)";
        String sql3 = "select id, xm name, nl as age from t_user1 union select id, xm name, nl as age from t_user2";

        JSONObject jResult = parse(sql2);

        System.out.println(formatJSON(jResult));
    }

    /**
     * 解析SQL
     *
     * @param sql 待解析的SQL语句
     * @return
     */
    public static JSONObject parse(String sql) {

        JSONObject jResult = new JSONObject(true);

        if (StringUtils.isBlank(sql)) {
            jResult.put("status", "FAILURE");
            jResult.put("result", "SQL不允许为空");
            return jResult;
        }

        // 将 SQL 转成 AST
        Statement stmt = null;
        try {
            stmt = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            jResult.put("status", "FAILURE");
            jResult.put("result", "SQL解析失败, 详细原因: " + e);
            return jResult;
        }

        // 解析AST
        if (stmt instanceof Select) {
            // 定义变量, 用于存储解析结果
            JSONObject jParseResult = new JSONObject();
            // 开始解析select语句
            parseSelect((Select) stmt, jParseResult);
        } else if (stmt instanceof CreateView) {
            // todo
        } else {
            // todo
        }

        return jResult;
    }

    public static void parseSelect(Select select, JSONObject jParseResult) {
        // 获取当前 SQL 转换成 AST 后的查询体
        SelectBody selectBody = select.getSelectBody();

        // 判断查询形式
        if (selectBody instanceof PlainSelect) { // 单表查询
            parsePlainSelect((PlainSelect) selectBody, jParseResult);
        } else if (selectBody instanceof SetOperationList) { // union 或 union all
            for (int i = 0; i < ((SetOperationList) selectBody).getSelects().size(); i++) {
                PlainSelect plainSelect = (PlainSelect) ((SetOperationList) selectBody).getSelects().get(i);
                parsePlainSelect(plainSelect, jParseResult);
            }
        } else {
            // todo
        }
    }

    public static void parsePlainSelect(PlainSelect plainSelect, JSONObject jParseResult) {

        // 获取当前SQL最外层查询的表
        FromItem fromItem = plainSelect.getFromItem();

        // 获取当前SQL最外层查询的字段
        List<SelectItem> selectItemList = plainSelect.getSelectItems();

        // 判断当前SQL最外层查询的表的类型
        if (fromItem instanceof Table) { // 单表

        } else if (fromItem instanceof SubSelect) { // 子查询

        } else if (fromItem instanceof SubJoin) {// join关联查询

        } else {
            // todo
        }
    }


    /**
     * json格式化
     *
     * @param object
     * @return
     */
    private static String formatJSON(Object object) {
        return JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteDateUseDateFormat);
    }
}
