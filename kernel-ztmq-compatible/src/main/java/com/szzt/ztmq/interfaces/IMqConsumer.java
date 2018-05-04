package com.szzt.ztmq.interfaces;

import com.szzt.ztmq.message.MessageEntity;

/**
 * Created by zhangbin on 2017/4/6.
 * 消费者接口
 */
public interface IMqConsumer {

    /**
     * 启动消费者消费消息
     */
    void startConsumer();

    /**
     * 停止消费者消费消息
     */
    void stopConsumer();

    /**
     * 消息处理
     *
     * @param messageEntity 消息实体
     */
    void doProcess(MessageEntity messageEntity);

}
