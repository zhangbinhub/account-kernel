package OLink.bpm.init;

import java.util.Date;
import java.util.Timer;


import OLink.bpm.core.dynaform.smsfilldocument.FillDocumentJob;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.util.timer.Job;
import OLink.bpm.util.timer.Schedule;
import pers.acp.tools.common.CommonTools;
import eWAP.core.license.InitLicense;

/**
 * Execute this object to nitialize the system.
 */
public class InitSystem {

    private static boolean USE_SHORTMESSAGE_INTERFACE = false;

    static {
        try {
            String bool = PropertyUtil.getByPropName("shortmessage", "auto.received.job");
            USE_SHORTMESSAGE_INTERFACE = Boolean.parseBoolean(bool);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        init();
    }

    public static void init() throws InitializationException {
        try {
            String bool = PropertyUtil.getByPropName("shortmessage", "auto.received.job");
            USE_SHORTMESSAGE_INTERFACE = Boolean.parseBoolean(bool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PropertyUtil.init();

        InitWorkingCalendar initWrkCld = new InitWorkingCalendar();
        initWrkCld.run();

        InitUserInfo initUser = new InitUserInfo();
        initUser.run();

        InitOperationInfo initOperation = new InitOperationInfo();
        initOperation.run();

        InitInstance initInst = new InitInstance();
        initInst.run();

        try {
            /***** add extend lib begin *****/
            CommonTools.InitTools();
            /***** add extend lib end *****/
            MultiLanguageProperty.init();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (USE_SHORTMESSAGE_INTERFACE) {
            try {//增加 by XGY
                Schedule.registerJob((Job) (InitLicense.initReceiveJob().newInstance()), new Date());
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
            }
            Timer timer = new Timer();
            Date time = new Date();
            timer.schedule(new FillDocumentJob(), new Date(time.getTime() + 60000));
        }
    }
}
