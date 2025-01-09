package com.itangsoft.datasource.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MongodbDataSource {

    MongoClient mongoClient;

    /**
     * 获取连接
     */
    public void create() {
        this.mongoClient = MongoClients.create("mongodb://test:123456@localhost:3306/data_solution");
    }

    /**
     * 获取数据库
     */
    public MongoDatabase getMongoDatabase() {
        return this.mongoClient.getDatabase("data_solution");
    }

    /**
     * 获取指定集合
     */
    public MongoCollection<Document> getCollection(MongoDatabase database, String collectionName) {
        return database.getCollection(collectionName);
    }

    /**
     * 获取集合所有字段
     */
    public Set<String> getDocuments(MongoDatabase database, MongoCollection<Document> collection) {
        // 用于存储字段信息
        Set<String> fieldNames = new LinkedHashSet<>();

        // 遍历集合中的文档
        for (Document document : collection.find().limit(10)) {
            fieldNames.addAll(document.keySet());
        }

        return fieldNames;
    }

    /**
     * 一次性获取数据库中的所有字段
     */
    public void queryAllDocuments(MongoDatabase database) {
        // 获取所有集合的名称
        MongoIterable<String> collectionNames = database.listCollectionNames();
        for (String collectionName : collectionNames) {
            if (!"user".equals(collectionName)) {
                // continue;
            }

            // 获取集合
            MongoCollection<Document> collection = getCollection(database, collectionName);

            // 用于存储字段信息
            Map<String, Map<String, Object>> fieldMap = new LinkedHashMap<>();

            // 只查询前 10 条文档
            try (MongoCursor<Document> cursor = collection.find().limit(10).iterator()) {
                while (cursor.hasNext()) {
                    for (Map.Entry<String, Object> entry : cursor.next().entrySet()) {
                        String fieldName = entry.getKey();
                        String fieldType = entry.getValue() == null ? null : entry.getValue().getClass().getSimpleName();

                        Map<String, Object> fieldAttributeMap = new LinkedHashMap<>();
                        fieldAttributeMap.put("fieldType", fieldType);
                        fieldAttributeMap.put("primaryKey", "_id".equals(fieldName));

                        fieldMap.putIfAbsent(fieldName, fieldAttributeMap);
                    }
                }
            }

            // 打印字段信息
            int order = 1;
            for (Map.Entry<String, Map<String, Object>> entry : fieldMap.entrySet()) {
                String fieldName = entry.getKey();
                Map<String, Object> attributeMap = entry.getValue();

                System.out.println("  字段: " + fieldName);
                System.out.println("    - 类型: " + attributeMap.get("fieldType"));
                System.out.println("    - 是否主键: " + attributeMap.get("primaryKey"));
                System.out.println("    - 顺序: " + order++);
            }
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        this.mongoClient.close();
    }
}
