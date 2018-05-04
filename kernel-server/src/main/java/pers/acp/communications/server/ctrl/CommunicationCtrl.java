package pers.acp.communications.server.ctrl;

import pers.acp.tools.common.CommonTools;
import pers.acp.tools.common.model.RunTimeConfig;
import org.apache.log4j.Logger;
import pers.acp.tools.file.FileTools;

import java.util.HashMap;
import java.util.Map;

public final class CommunicationCtrl {

    private static Logger log = Logger.getLogger(CommunicationCtrl.class);

    /**
     * 通讯通道是否可用
     *
     * @return 是否可用
     */
    public static boolean isEnabled() {
        String value = FileTools.getProperties("communicationctrl", "false");
        if ("true".equals(value)) {
            value = CommonTools.getSysParamValue("CommunicationEnabled");
            return "true".equals(value);
        } else {
            return true;
        }
    }

    /**
     * 设置通讯通道状态
     *
     * @param enabled true-可用，false-禁用
     */
    public static void setEnabled(boolean enabled) {
        Map<String, Object> param = new HashMap<>();
        param.put(RunTimeConfig.class.getCanonicalName() + ".confname", "CommunicationEnabled");
        param.put(RunTimeConfig.class.getCanonicalName() + ".status", 1);
        RunTimeConfig runTimeConfig = (RunTimeConfig) RunTimeConfig.getInstance(param, RunTimeConfig.class, null);
        runTimeConfig.setConfvalue(String.valueOf(enabled));
        runTimeConfig.addUpdateIncludes(new String[]{"confvalue"});
        runTimeConfig.doUpdate();
        log.info("CommunicationEnabled is " + enabled);
    }
}
