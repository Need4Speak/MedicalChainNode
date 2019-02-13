package com.pancake.util;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.pancake.entity.pojo.MongoDBConfig;
import com.pancake.entity.util.Const;
import com.pancake.entity.util.NetAddress;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

/**
 * Created by chao on 2018/6/1.
 */
public class MongoDB {
    private final static Logger logger = LoggerFactory.getLogger(MongoDB.class);
    private MongoDatabase mongoDatabase;
    private MongoDBConfig mongoDBConfig = JsonUtil.getMongoDBConfig(Const.BlockChainConfigFile);
    private MongoClient mongoClient = new MongoClient(mongoDBConfig.getIp(), mongoDBConfig.getPort());
    private String database = mongoDBConfig.getDatabase();

    public MongoDB() {
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(15000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.ACKNOWLEDGED);//
        options.build();
        mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoDB(String database) {
        this.database = database;
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(15000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.ACKNOWLEDGED);//
        options.build();
        mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoDB(NetAddress mongoAddr, String database) {
        this.database = database;
        mongoClient = new MongoClient(mongoAddr.getIp(), mongoAddr.getPort());
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(15000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.ACKNOWLEDGED);//
        options.build();
        mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoDB(MongoDBConfig mongoDBConfig) {
        this.database = mongoDBConfig.getDatabase();
        ServerAddress serverAddress = new ServerAddress(mongoDBConfig.getIp(), mongoDBConfig.getPort());
        MongoCredential credential = MongoCredential.createScramSha1Credential(mongoDBConfig.getUsername(),
                mongoDBConfig.getDatabase(), mongoDBConfig.getPassword().toCharArray());
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();
        credentials.add(credential);
        mongoClient = new MongoClient(serverAddress, credentials);
        MongoClientOptions.Builder options = new MongoClientOptions.Builder();
        // options.autoConnectRetry(true);// 自动重连true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
        options.connectTimeout(15000);// 连接超时，推荐>3000毫秒
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// 套接字超时时间，0无限制
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
        options.writeConcern(WriteConcern.ACKNOWLEDGED);//
        options.build();
        mongoDatabase = mongoClient.getDatabase(database);
    }

    public MongoDatabase getMongoDatabase() {
        return this.mongoDatabase;
    }

    public List<String> findByKVs(Map<String, Object> kvMap, String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        List<String> result = new ArrayList<String>();
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : kvMap.entrySet()) {
            doc.put(entry.getKey(), entry.getValue());
        }

        for (Document document : collection.find(doc)) {
            result.add(document.toJson());
        }
        return result;

    }

    @SuppressWarnings("Duplicates")
    public List<String> findAllSort(String collectionName, String sortKey, String sortForm) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        FindIterable<Document> findIterable = null;
        if (sortForm.equals(Const.DESC)) {
            findIterable = collection.find().sort(descending(sortKey));
        } else if (sortForm.equals(Const.ASC)) {
            findIterable = collection.find().sort(ascending(sortKey));
        }
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        List<String> list = new ArrayList<String>();
        String record;
        while (mongoCursor.hasNext()) {
            record = mongoCursor.next().toJson();
            list.add(record);
            logger.debug("record: " + record);
        }
        return list;
    }

    /**
     * 获取所有满足 key = value 的文档
     *
     * @param key
     * @param value
     * @param collectionName
     * @return
     */
    public List<String> find(String key, String value, String collectionName) {
        List<String> result = new ArrayList<String>();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        for (Document document : collection.find(eq(key, value))) {
            result.add(document.toJson());
        }
        return result;
    }


    /**
     * 获取集合 collectionName 中的记录数
     *
     * @param collectionName
     * @return
     */
    public long countRecords(String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        return collection.count();
    }

    /**
     * 根据 key 获取所有对应的value
     *
     * @param key
     * @param collectionName
     * @return
     */
    @SuppressWarnings("Duplicates")
    public Set<String> findValuesByKey(String key, String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        MongoCursor<String> mongoCursor = collection.distinct(key, String.class).iterator();
        Set<String> blockIdSet = new HashSet<String>();
        while (mongoCursor.hasNext()) {
            blockIdSet.add(mongoCursor.next());
        }
        return blockIdSet;
    }

    /**
     * 根据 key 统计去重统计所有value的个数
     *
     * @param key
     * @param collectionName
     * @return
     */
    public int countValuesByKey(String key, String collectionName) {
        return this.findValuesByKey(key, collectionName).size();
    }

    /**
     * 判断 collectionName 是否存在
     *
     * @param collectionName
     * @return
     */
    public boolean collectionExists(String collectionName) {
        return mongoDatabase.listCollectionNames()
                .into(new ArrayList<String>()).contains(collectionName);
    }

    /**
     * 查找集合 collectionName 中的第一条记录，以 json 形式返回
     *
     * @param collectionName
     * @return
     */
    public String findFirstDoc(String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        Document document = collection.find().first();
        return document.toJson();
    }

    /**
     * 插入key-value
     *
     * @param key
     * @param value
     * @param collectionName
     */
    public void insertKV(String key, String value, String collectionName) {
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        Document document = new Document(key, value);
        collection.insertOne(document);
    }

}
