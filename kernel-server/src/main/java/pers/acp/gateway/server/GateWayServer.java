package pers.acp.gateway.server;

import pers.acp.communications.server.http.servlet.base.BaseServletHandle;
import pers.acp.communications.server.http.servlet.handle.HttpServletRequestAcp;
import pers.acp.communications.server.http.servlet.handle.HttpServletResponseAcp;
import pers.acp.communications.server.http.servlet.tools.ServletTools;
import pers.acp.communications.tools.CommunicationTools;
import pers.acp.gateway.common.GateWayException;
import pers.acp.gateway.common.GateWaySignType;
import pers.acp.gateway.common.GateWayTools;
import pers.acp.gateway.server.GateWayServerConfig.Server;
import pers.acp.gateway.server.base.IBaseServer;
import pers.acp.gateway.tradeorder.annotation.ATradeOrder;
import pers.acp.gateway.tradeorder.annotation.ATradeOrderField;
import pers.acp.gateway.tradeorder.interfaces.ITradeOrder;
import pers.acp.tools.common.CommonTools;
import pers.acp.tools.exceptions.ConfigException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 网关服务端入口类
 *
 * @author zhangbin
 */
public class GateWayServer extends BaseServletHandle {

    /**
     * 日志对象
     */
    private Logger log = Logger.getLogger(this.getClass());

    /**
     * 默认签名算法
     */
    private GateWaySignType defaultSignType = GateWaySignType.MD5;

    public GateWayServer(HttpServletRequestAcp request, HttpServletResponseAcp response) throws ConfigException {
        super(request, response);
    }

    /**
     * 网关服务端统一入口
     */
    public void CallGateWay() {
        try {
            String content = ServletTools.getRequestContent(request);
            Map<String, String> param = CommunicationTools.parseXML(content);
            GateWayServerConfig serversConfig = GateWayServerConfig.getInstance();
            String serverName = param.get("servername");
            if (!CommonTools.isNullStr(serverName)) {
                Map<String, String> returnMessage = null;
                String errorMessage = null;
                String tradeKey = null;
                List<Server> servers = serversConfig.getServers();
                for (Server server : servers) {
                    if (server.getServerName().equals(serverName)) {
                        String classname = server.getServerClass();
                        String orderClassName = server.getOrderClass();
                        Class<?> cls = Class.forName(classname);
                        IBaseServer serverObj = (IBaseServer) cls.newInstance();
                        Class<?> ocls = Class.forName(orderClassName);
                        ITradeOrder tradeorder = getOrder(param, ocls);
                        tradeKey = serverObj.getTradeKey(tradeorder);
                        if (CommonTools.isNullStr(tradeKey)) {
                            throw new GateWayException("don't find trade key");
                        }
                        if (GateWayTools.validateSign(param, tradeKey)) {
                            returnMessage = serverObj.doServer(tradeorder, server);
                        } else {
                            errorMessage = "validate sign is faild!";
                            log.error("validate sign is faild!");
                        }
                        break;
                    }
                }
                if (returnMessage != null) {
                    doReturn(returnMessage, defaultSignType, tradeKey);
                } else {
                    if (CommonTools.isNullStr(errorMessage)) {
                        errorMessage = "no such server:" + serverName;
                    }
                    doReturnError("servername:" + serverName + " is faild:" + errorMessage, defaultSignType, tradeKey);
                }
            } else {
                doReturnError("servername is null!", defaultSignType, null);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            doReturnError("gateway server exception:" + e.getMessage(), defaultSignType, null);
        }
    }

    /**
     * 将参数实例化成订单对象
     *
     * @param param 参数
     * @param ocls  类
     * @return 交易订单
     */
    private ITradeOrder getOrder(Map<String, String> param, Class<?> ocls) throws GateWayException, InstantiationException, IllegalAccessException {
        List<Field> fields = getOrderClassFields(ocls);
        ITradeOrder tradeOrder = (ITradeOrder) ocls.newInstance();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ATradeOrderField.class)) {
                ATradeOrderField aTradeOrderField = field.getAnnotation(ATradeOrderField.class);
                for (Entry<String, String> entry : param.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.equals(aTradeOrderField.name())) {
                        field.set(tradeOrder, value);
                    }
                }
            }
        }
        return tradeOrder;
    }

    /**
     * 获取订单类全部字段（包括继承）
     *
     * @param clas 订单类
     * @return 字段List
     */
    private List<Field> getOrderClassFields(Class<?> clas) throws GateWayException {
        if (clas.isAnnotationPresent(ATradeOrder.class)) {
            List<Field> result = new ArrayList<>(Arrays.asList(clas.getDeclaredFields()));
            Class<?> cls = clas.getSuperclass();
            while (cls != null && cls.isAnnotationPresent(ATradeOrder.class)) {
                result.addAll(new ArrayList<>(Arrays.asList(cls.getDeclaredFields())));
                cls = cls.getSuperclass();
            }
            return result;
        } else {
            throw new GateWayException("order class need Annotation ATradeOrder");
        }
    }

    /**
     * 返回信息
     *
     * @param returnMessage 返回信息
     * @param signType      签名类型
     * @param tradeKey      交易密钥
     */
    private void doReturn(Map<String, String> returnMessage, GateWaySignType signType, String tradeKey) {
        String content = GateWayTools.buildReturnMessage(returnMessage, signType, tradeKey);
        response.doReturn(content);
    }

    /**
     * 返回失败信息
     *
     * @param errorMessage 错误信息
     * @param signType     签名类型
     * @param tradeKey     交易密钥
     */
    private void doReturnError(String errorMessage, GateWaySignType signType, String tradeKey) {
        String content = GateWayTools.buildErrorMessage(errorMessage, signType, tradeKey);
        response.doReturn(content);
    }

}
