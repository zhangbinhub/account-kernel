package com.szzt.ztmq.config;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created by zhangbin on 2017/4/6.
 * 消费者配置信息
 */
public class ConsumerConfig extends Properties {

    private static Logger log = Logger.getLogger(ConsumerConfig.class);

    private static ConsumerConfig prop = null;

    static {
        try {
            if (prop == null) {
                prop = new ConsumerConfig();
            }
            prop.load(ConsumerConfig.class.getClassLoader().getResourceAsStream("ztmq_consumer.properties"));
            log.info("load ztmq_consumer.properties successfull!");
        } catch (Exception e) {
            log.error("load ztmq_consumer.properties failed!");
        }
    }

    public static ConsumerConfig getInstance() {
        return prop;
    }

}
