package com.szzt.ztmq.factory;

import com.szzt.ztmq.config.ConsumerConfig;
import com.szzt.ztmq.config.ProducerConfig;
import com.szzt.ztmq.interfaces.IMqConsumer;
import com.szzt.ztmq.interfaces.IMqProducer;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;

/**
 * Created by zhangbin on 2017/4/7.
 * 消息队列工厂类
 */
public class MqFactory {

    private static Logger LOG = Logger.getLogger(MqFactory.class);

    /**
     * 创建生产者实例
     *
     * @param cls 生产者类
     * @return 生产者实例
     */
    public static <T> T createProducerInstance(Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getConstructor(ProducerConfig.class);
            T instance = constructor.newInstance(ProducerConfig.getInstance());
            if (instance instanceof IMqProducer) {
                return instance;
            } else {
                LOG.error("class " + cls.getCanonicalName() + " is not implements " + IMqProducer.class.getCanonicalName());
                return null;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 创建消费者实例
     *
     * @param cls 消费者类
     * @return 消费者实例
     */
    public static <T> T createConsumerInstance(Class<T> cls) {
        try {
            Constructor<T> constructor = cls.getConstructor(ConsumerConfig.class);
            T instance = constructor.newInstance(ConsumerConfig.getInstance());
            if (instance instanceof IMqConsumer) {
                return instance;
            } else {
                LOG.error("class " + cls.getCanonicalName() + " is not implements " + IMqConsumer.class.getCanonicalName());
                return null;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
