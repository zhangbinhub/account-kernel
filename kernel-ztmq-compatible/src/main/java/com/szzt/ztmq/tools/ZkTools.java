package com.szzt.ztmq.tools;

import kafka.utils.ZKStringSerializer;
import net.sf.json.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.data.Stat;
import pers.acp.tools.common.CommonTools;

import java.util.List;

/**
 * Created by zhangbin on 2017/4/8.
 * zookeeper 工具类
 */
public class ZkTools {

    private static String KAFKA_BROKER_PATH = "/brokers/ids";

    private static int SESSION_TIMEOUT = 10000;

    private static int CONNECTION_TIMEOUT = 10000;

    private static ZkTools instance = null;

    private ZkClient zkClient = null;

    /**
     * 构造函数
     *
     * @param zkServers         zookeeper 连接字符串
     * @param sessionTimeout    session 超时时间
     * @param connectionTimeout 连接超时时间
     */
    private ZkTools(String zkServers, int sessionTimeout, int connectionTimeout) {
        zkClient = new ZkClient(zkServers, sessionTimeout, connectionTimeout, new ZkSerializer() {
            @Override
            public byte[] serialize(Object data) {
                return ZKStringSerializer.serialize(data);
            }

            @Override
            public Object deserialize(byte[] bytes) {
                return ZKStringSerializer.deserialize(bytes);
            }
        });
    }

    /**
     * 初始化zk工具类
     *
     * @param zkServers zookeeper 连接字符串
     * @return ZkTools zookeeper 客户端
     */
    public static ZkTools getInstance(String zkServers) {
        return getInstance(zkServers, SESSION_TIMEOUT, CONNECTION_TIMEOUT);
    }

    /**
     * 初始化zk工具类
     *
     * @param zkServers         zookeeper 连接字符串
     * @param sessionTimeout    session 超时时间
     * @param connectionTimeout 连接超时时间
     * @return ZkTools zookeeper 客户端
     */
    public static ZkTools getInstance(String zkServers, int sessionTimeout, int connectionTimeout) {
        if (instance == null) {
            instance = new ZkTools(zkServers, sessionTimeout, connectionTimeout);
        }
        return instance;
    }

    /**
     * 销毁zk客户端
     */
    public void destroyInstance() {
        if (zkClient != null) {
            zkClient.close();
            zkClient = null;
        }
    }

    /**
     * 获取kafka集群服务字符串
     *
     * @return kafka集群服务字符串
     */
    public String getKafkaBrokerListString() {
        boolean exists = zkClient.exists(KAFKA_BROKER_PATH);
        if (exists) {
            String brokerlist = "";
            List<String> children = zkClient.getChildren(KAFKA_BROKER_PATH);
            for (String child : children) {
                JSONObject jsonObject = getBrokerInfo(KAFKA_BROKER_PATH + "/" + child);
                if (!CommonTools.isNullStr(brokerlist)) {
                    brokerlist += ",";
                }
                brokerlist += jsonObject.getString("host") + ":" + jsonObject.getString("port");
            }
            return brokerlist;
        } else {
            return "";
        }
    }

    /**
     * 获取节点信息
     *
     * @param path 路径
     * @return 节点信息
     */
    public JSONObject getBrokerInfo(String path) {
        return CommonTools.getJsonObjectFromStr(zkClient.readData(path, new Stat()));
    }

}
