package com.itangsoft.datasource.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;

class MongodbDataSourceTest {

    @Test
    void testSimple() {
        MongodbDataSource ds = new MongodbDataSource();

        ds.create();

        MongoDatabase database = ds.getMongoDatabase();

        MongoCollection<Document> collection = ds.getCollection(database, "user");

        for (String field : ds.getDocuments(database, collection)) {
            System.out.println(field);
        }

        ds.close();
    }

    @Test
    void testQueryAllDocuments() {
        MongodbDataSource ds = new MongodbDataSource();

        ds.create();

        MongoDatabase database = ds.getMongoDatabase();

        ds.queryAllDocuments(database);

        ds.close();
    }
}
