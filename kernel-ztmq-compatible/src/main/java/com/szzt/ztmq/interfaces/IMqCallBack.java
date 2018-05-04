package com.szzt.ztmq.interfaces;

/**
 * Created by zhangbin on 2017/4/8.
 * 消息发送回调
 */
public interface IMqCallBack {

    /**
     * 发送成功
     */
    void onSuccessfull();

    /**
     * 发送失败
     *
     * @param e 异常信息
     */
    void onFailed(Exception e);

}
