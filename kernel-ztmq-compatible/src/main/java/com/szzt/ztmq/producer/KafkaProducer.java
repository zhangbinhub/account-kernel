package com.szzt.ztmq.producer;

import com.szzt.ztmq.config.ProducerConfig;
import com.szzt.ztmq.interfaces.IMqCallBack;
import com.szzt.ztmq.interfaces.IMqProducer;
import com.szzt.ztmq.message.MessageEntity;
import com.szzt.ztmq.tools.ZkTools;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import pers.acp.tools.task.threadpool.ThreadPoolService;
import pers.acp.tools.task.threadpool.basetask.BaseThreadTask;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhangbin on 2017/4/6.
 * kafka消息队列 生产者实现
 */
public class KafkaProducer implements IMqProducer {

    private Logger log = Logger.getLogger(this.getClass());

    /**
     * 生产者配置
     */
    private ProducerConfig config;

    /**
     * 消息发送线程池
     */
    private static ThreadPoolService messagePool = null;

    /**
     * kafka 生产者
     */
    private static Producer<String, String> producer = null;

    /**
     * 锁对象
     */
    private static Lock lock = new ReentrantLock();

    /**
     * 构造函数
     *
     * @param producerConfig 生产者配置
     */
    public KafkaProducer(ProducerConfig producerConfig) {
        this.config = producerConfig;
    }

    @Override
    public void sendMessage(String topic, MessageEntity messageEntity) {
        sendMessage(topic, messageEntity, null);
    }

    @Override
    public void sendMessage(String topic, MessageEntity messageEntity, IMqCallBack mqCallBack) {
        if (messagePool == null) {
            messagePool = ThreadPoolService.getInstance("message_pool_queue", 3000, Integer.valueOf(config.getProperty("threadnumber")));
        }
        messagePool.addTask(new BaseThreadTask("send message") {
            @Override
            public boolean beforeExcuteFun() {
                return true;
            }

            @Override
            public Object excuteFun() {
                ZkTools zkTools = null;
                try {
                    lock.lock();
                    if (producer == null) {
                        zkTools = ZkTools.getInstance(config.getProperty("zookeeper.connection"));
                        config.setProperty("bootstrap.servers", zkTools.getKafkaBrokerListString());
                        producer = new org.apache.kafka.clients.producer.KafkaProducer<>(config);
                    }
                    lock.unlock();
                    producer.send(new ProducerRecord<>(topic, messageEntity.getMessageKey(), messageEntity.getMessageContent()), (recordMetadata, e) -> {
                        if (mqCallBack != null) {
                            if (e != null) {
                                mqCallBack.onFailed(e);
                            } else {
                                mqCallBack.onSuccessfull();
                            }
                        }
                    });
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    mqCallBack.onFailed(e);
                } finally {
                    if (zkTools != null) {
                        zkTools.destroyInstance();
                    }
                }
                return true;
            }

            @Override
            public void afterExcuteFun(Object result) {
                lock.lock();
                if (messagePool.isWaitingOther(this.getThreadindex())) {
                    producer.close();
                    producer = null;
                }
                lock.unlock();
            }
        });
    }

}
