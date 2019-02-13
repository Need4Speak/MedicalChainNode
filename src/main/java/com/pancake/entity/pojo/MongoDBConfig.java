package com.pancake.entity.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancake.entity.util.NetAddress;

/**
 * Created by chao on 2018/6/2.
 */
public class MongoDBConfig {
    private String ip;
    private int port;
    private String username;
    private String password;
    private String database;

    public MongoDBConfig() {
    }

    public MongoDBConfig(NetAddress netAddress, String database) {
        this.ip = netAddress.getIp();
        this.port = netAddress.getPort();
        this.database = database;
    }

    public MongoDBConfig(String ip, int port, String database) {
        this.ip = ip;
        this.port = port;
        this.database = database;
    }

    public MongoDBConfig(String ip, int port, String username, String password, String database) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    @Override
    public String toString() {
        String rtn = null;
        try {
            rtn = (new ObjectMapper()).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
