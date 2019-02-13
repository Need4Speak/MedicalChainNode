package com.pancake.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancake.entity.pojo.MongoDBConfig;
import com.pancake.entity.pojo.RabbitmqServer;
import com.pancake.entity.util.Const;
import com.pancake.entity.util.NetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 保存和 json 相关的操作
 * Created by chao on 2017/11/9.
 */
public class JsonUtil {

    private final static ObjectMapper objMapper = new ObjectMapper();
    private final static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * 将字符串转list对象
     *
     * @param <T>
     * @param jsonStr
     * @param cls
     * @return
     */
    public static <T> List<T> str2list(String jsonStr, Class<T> cls) {
        ObjectMapper mapper = new ObjectMapper();
        List<T> objList = null;
        try {
            JavaType t = mapper.getTypeFactory().constructParametricType(
                    List.class, cls);
            objList = mapper.readValue(jsonStr, t);
        } catch (Exception e) {
        }
        return objList;
    }

    public static Map jsonToMap(String jsonStr) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = null;
        JavaType javaType = mapper.getTypeFactory().constructParametricType(HashMap.class, String.class, Object.class);
        try {
            map = mapper.readValue(jsonStr, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 从指定路径读取json文件，解析后返回json字符串
     *
     * @return
     */
    public static String getStrByJsonFile(String jsonFile) {
        String strResult = "";

        try {
            JsonNode rootNode = objMapper.readTree(new File(jsonFile));

            // 获得 json 字符串
            strResult = rootNode.toString();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strResult;
    }

    /**
     * 读取BlockChainConfigFile文件，解析后返回 ValidatorAddress list
     * @return
     */
    public static List<NetAddress> getValidatorAddressList() {
        return getValidatorAddressList(Const.BlockChainConfigFile);
    }

    /**
     * 从指定路径读取json文件，解析后返回 ValidatorAddress list
     *
     * @return
     */
    public static List<NetAddress> getValidatorAddressList(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        //noinspection unchecked
        List<Map> list = (List<Map>) map.get("validators");
        List<NetAddress> addrList = new ArrayList<NetAddress>();
        for(Map tmpMap : list) {
            addrList.add(new NetAddress((String)tmpMap.get("ip"), (Integer) tmpMap.get("port")));
        }
        return addrList;
    }

    /**
     * 读取BlockChainConfigFile文件，解析后返回 BlockerAddress list
     * @return
     */
    public static List<NetAddress> getBlockerAddressList() {
        return getBlockerAddressList(Const.BlockChainConfigFile);
    }

    /**
     * 从指定路径读取json文件，解析后返回 BlockerAddress list
     *
     * @return
     */
    public static List<NetAddress> getBlockerAddressList(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        //noinspection unchecked
        List<Map> list = (List<Map>) map.get("blockers");
        List<NetAddress> addrList = new ArrayList<NetAddress>();
        for(Map tmpMap : list) {
            addrList.add(new NetAddress((String)tmpMap.get("ip"), (Integer) tmpMap.get("port")));
        }
        return addrList;
    }

    /**
     * 从指定路径读取json文件，解析后返回 Validator 的 mongodb 的地址 list
     *
     * @return
     */
    public static List<NetAddress> getValidatorMongoAddr(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        //noinspection unchecked
        List<Map> list = (List<Map>) map.get("validator_mongodb");
        List<NetAddress> addrList = new ArrayList<NetAddress>();
        for(Map tmpMap : list) {
            addrList.add(new NetAddress((String)tmpMap.get("ip"), (Integer) tmpMap.get("port")));
        }
        return addrList;
    }

    /**
     * 获取当前 Validator 的地址
     * @param jsonFile
     * @return
     */
    public static NetAddress getCurrentValidator(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("current_validator");
        return new NetAddress((String)pubMap.get("ip"), (Integer) pubMap.get("port"));
    }

    /**
     * 获取当前 Blocker 的地址
     * @param jsonFile
     * @return
     */
    public static NetAddress getCurrentBlocker(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("current_blocker");
        return new NetAddress((String)pubMap.get("ip"), (Integer) pubMap.get("port"));
    }

    public static RabbitmqServer getRabbitmqServer() {
        return getRabbitmqServer(Const.BlockChainConfigFile);
    }
    /**
     * 从 jsonFile 中获取 Rabbitmq 的配置信息
     * @param jsonFile
     * @return
     */
    public static RabbitmqServer getRabbitmqServer(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("rabbitmq");
        return new RabbitmqServer((String)pubMap.get("userName"), (String)pubMap.get("password"), (String)pubMap.get("ip"),
                (Integer) pubMap.get("port"));
    }

    /**
     * 从 jsonFile 中获取 mongodb 的配置信息
     * @param jsonFile
     * @return
     */
    public static MongoDBConfig getMongoDBConfig(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("mongodb");
        return new MongoDBConfig((String)pubMap.get("ip"), (Integer) pubMap.get("port"),
                (String)pubMap.get("username"), (String)pubMap.get("password"), (String)pubMap.get("database"));
    }

    /**
     * 获取私钥所在文件
     * @param jsonFile
     * @return
     */
    public static String getPvtKeyFile(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("key_pair");
        return (String)pubMap.get("pvt_key_file");
    }

    /**
     * 获取公钥所在文件
     * @param jsonFile
     * @return
     */
    public static String getPubKeyFile(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("key_pair");
        return (String)pubMap.get("pub_key_file");
    }

    /**
     * 从配置文件中获取block的大小
     * @param jsonFile
     * @return
     */
    public static double getBlockSize(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map blockMap = (HashMap) map.get("block");
        return (Double)blockMap.get("size");
    }

    /**
     * 从配置文件中获取生成block的最长时间间隔
     * @param jsonFile
     * @return
     */
    public static long getTimeInterval(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map blockMap = (HashMap) map.get("block");
        return Long.valueOf((Integer)blockMap.get("time_interval"));
    }

    /**
     * 获取 Publisher 的地址
     * @param jsonFile
     * @return
     */
    public static NetAddress getPublisherAddress(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("publisher");
        return new NetAddress((String)pubMap.get("ip"), (Integer) pubMap.get("port"));
    }

    /**
     *  获取 TxIdCollector 的地址
     * @param jsonFile
     * @return
     */
    public static NetAddress getTxIdCollectorAddress(String jsonFile) {
        String jsonStr = getStrByJsonFile(jsonFile);
        Map map = jsonToMap(jsonStr);
        Map pubMap = (HashMap) map.get("tx_id_collector");
        return new NetAddress((String)pubMap.get("ip"), (Integer) pubMap.get("port"));
    }

    /**
     * 判断json字符串是否是一个list
     *
     * @param jsonStr
     * @return
     */
    public static boolean isList(String jsonStr) {
        try {
            if (objMapper.writeValueAsString(jsonStr).substring(1, 2).equals("[")) {
                return true;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取泛型的Collection Type
     * @param collectionClass 泛型的Collection
     * @param elementClasses 元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static String writeValueAsString(Object object) {
        try {
            return objMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        String jsonFile = Const.BlockChainConfigFile;

        // 1. 从指定路径读取json文件，解析后返回json字符串
        logger.info(getStrByJsonFile(jsonFile));

        // 2.
        List<NetAddress> list = getValidatorAddressList(jsonFile);

        for (NetAddress netAddress : list) {
            logger.info(netAddress.toString());
        }
    }

}
