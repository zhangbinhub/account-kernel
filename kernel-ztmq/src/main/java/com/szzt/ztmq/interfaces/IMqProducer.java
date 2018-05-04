package com.szzt.ztmq.interfaces;

import com.szzt.ztmq.message.MessageEntity;

/**
 * Created by zhangbin on 2017/4/6.
 * 生产者接口
 */
public interface IMqProducer {

    /**
     * 发送消息
     *
     * @param topic         消息队列主题
     * @param messageEntity 消息实体
     */
    void sendMessage(String topic, MessageEntity messageEntity);

    /**
     * 发送消息
     *
     * @param topic         消息队列主题
     * @param messageEntity 消息实体
     * @param mqCallBack    消息回调
     */
    void sendMessage(String topic, MessageEntity messageEntity, IMqCallBack mqCallBack);

}
