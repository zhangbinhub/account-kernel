package com.szzt.ztmq.consumer;

import com.szzt.ztmq.config.ConsumerConfig;
import com.szzt.ztmq.interfaces.IMqConsumer;
import com.szzt.ztmq.message.MessageEntity;
import com.szzt.ztmq.tools.ZkTools;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Arrays;

/**
 * Created by zhangbin on 2017/4/6.
 * kafka消息队列 消费者实现
 */
public abstract class KafkaConsumer implements IMqConsumer {

    private ConsumerConfig config;

    private static Thread mainConsumerThread = null;

    private static boolean running = false;

    public KafkaConsumer(ConsumerConfig consumerConfig) {
        this.config = consumerConfig;
    }

    @Override
    public void startConsumer() {
        if (mainConsumerThread == null) {
            running = true;
            mainConsumerThread = new Thread(() -> {
                int message_pool_number = Integer.valueOf(config.getProperty("message.poll.number", "100"));
                ZkTools zkTools = ZkTools.getInstance(config.getProperty("zookeeper.connection"));
                config.setProperty("bootstrap.servers", zkTools.getKafkaBrokerListString());
                zkTools.destroyInstance();
                org.apache.kafka.clients.consumer.KafkaConsumer<String, String> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer<>(config);
                consumer.subscribe(Arrays.asList(config.getProperty("topics").split(",")));
                while (running) {
                    ConsumerRecords<String, String> records = consumer.poll(message_pool_number);
                    for (ConsumerRecord<String, String> record : records) {
                        Thread processThread = new Thread(() -> doProcess(new MessageEntity(record.key(), record.value())));
                        processThread.setDaemon(true);
                        processThread.start();
                    }
                }
            });
            mainConsumerThread.setDaemon(true);
            mainConsumerThread.start();
        }
    }

    @Override
    public void stopConsumer() {
        if (mainConsumerThread != null) {
            running = false;
            mainConsumerThread.interrupt();
            mainConsumerThread = null;
        }
    }

}
