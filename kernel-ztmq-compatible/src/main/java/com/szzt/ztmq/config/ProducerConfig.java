package com.szzt.ztmq.config;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by zhangbin on 2017/4/6.
 * 生产者配置信息
 */
public class ProducerConfig extends Properties {

    private static Logger log = Logger.getLogger(ProducerConfig.class);

    private static ProducerConfig prop = null;

    static {
        try {
            if (prop == null) {
                prop = new ProducerConfig();
            }
            prop.load(ProducerConfig.class.getClassLoader().getResourceAsStream("ztmq_producer.properties"));
            log.info("load ztmq_producer.properties successfull!");
        } catch (Exception e) {
            log.error("load ztmq_producer.properties failed!");
        }
    }

    public static ProducerConfig getInstance() {
        return prop;
    }

}