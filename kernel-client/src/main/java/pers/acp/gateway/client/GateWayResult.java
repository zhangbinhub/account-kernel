package pers.acp.gateway.client;

import java.util.Map;

public class GateWayResult {

    /**
     * -2-执行请求前操作失败，-1-请求失败，0-请求成功，-3-异常错误
     */
    private int status;

    private String errorMessage = null;

    private Map<String, String> info = null;

    /**
     * 请求结果：-2-执行请求前操作失败，-1-请求失败，0-请求成功，-3-异常错误
     *
     * @return -2-执行请求前操作失败，-1-请求失败，0-请求成功，-3-异常错误
     */
    public int getStatus() {
        return status;
    }

    /**
     * 请求结果：-2-执行请求前操作失败，-1-请求失败，0-请求成功，-3-异常错误
     *
     * @param status -2-执行请求前操作失败，-1-请求失败，0-请求成功，-3-异常错误
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 获取返回数据集
     *
     * @return 数据集
     */
    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

}
