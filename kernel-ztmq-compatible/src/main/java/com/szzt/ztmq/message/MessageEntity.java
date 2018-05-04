package com.szzt.ztmq.message;

import pers.acp.tools.security.MD5Utils;

/**
 * Created by zhangbin on 2017/4/6.
 * 消息实体
 */
public class MessageEntity {

    public MessageEntity(String key, String value) {
        this.MessageKey = key;
        this.MessageContent = value;
    }

    public MessageEntity(String value) {
        this.MessageKey = MD5Utils.encrypt(value);
        this.MessageContent = value;
    }

    public String getMessageKey() {
        return MessageKey;
    }

    public void setMessageKey(String messageKey) {
        MessageKey = messageKey;
    }

    public String getMessageContent() {
        return MessageContent;
    }

    public void setMessageContent(String messageContent) {
        MessageContent = messageContent;
    }

    private String MessageKey;

    private String MessageContent;

}
