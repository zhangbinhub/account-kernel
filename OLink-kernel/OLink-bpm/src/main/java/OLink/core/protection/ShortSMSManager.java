package OLink.core.protection;

import eWAP.core.SMSService;

public final class ShortSMSManager
{
    private static SMSService instance = null;

    public static SMSService getInstance()
    {
        synchronized (ShortSMSManager.class) {
            if (instance == null) {
                try
                {
                    Object obj = Class.forName("services.SMSService").newInstance();
                    instance = (SMSService)obj;
                }
                catch (Exception localException)
                {
                }
            }
        }
        return instance;
    }
}