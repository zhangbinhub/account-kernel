package eWAP.core.license;

import eWAP.core.ResourcePool;
import eWAP.core.Tools;

import javax.servlet.ServletContextEvent;
import java.io.FileInputStream;

public class InitLicense extends ClassLoader {

    static InitLicense cl;
    static LoadLicense process;
    byte[] clsData;

    private InitLicense(ClassLoader paramClassLoader) throws Exception {
        super(paramClassLoader);
    }

    public static void Init(ServletContextEvent paramServletContextEvent,
                            String paramString) throws Exception {
        Class<?> localClass = null;
        ClassLoader localClassLoader = Thread.currentThread()
                .getContextClassLoader();
        cl = new InitLicense(localClassLoader);
        FileInputStream localFileInputStream = null;
        try {
            localFileInputStream = new FileInputStream(
                    ResourcePool.getRootpath() + "/config/eWAP.lic");
            byte[] arrayOfByte1 = new byte[8];
            long l = localFileInputStream.read(arrayOfByte1, 0, 8);
            l = Tools.byteToLong(arrayOfByte1);
            byte[] arrayOfByte2 = new byte[(int) l];
            localFileInputStream.read(arrayOfByte2, 0, (int) l);
            for (int i = 1; i < arrayOfByte2.length; i++) {
                int tmp108_106 = i;
                byte[] tmp108_104 = arrayOfByte2;
                tmp108_104[tmp108_106] = (byte) (tmp108_104[tmp108_106] ^ arrayOfByte2[0]);
            }
            String str = new String(arrayOfByte2);
            String[] arrayOfString = str.split(":", -1);
            l = localFileInputStream.read(arrayOfByte1, 0, 8);
            l = Tools.byteToLong(arrayOfByte1);
            arrayOfByte2 = new byte[(int) l];
            localFileInputStream.read(arrayOfByte2, 0, (int) l);
            l = localFileInputStream.read(arrayOfByte1, 0, 8);
            l = Tools.byteToLong(arrayOfByte1);
            arrayOfByte2 = new byte[(int) l];
            localFileInputStream.read(arrayOfByte2, 0, (int) l);
            str = arrayOfString[0] + arrayOfString[1];
            byte[] arrayOfByte3 = str.getBytes();
            int j = 0;
            for (int k = 0; k < arrayOfByte2.length; k++) {
                int tmp273_271 = k;
                byte[] tmp273_269 = arrayOfByte2;
                tmp273_269[tmp273_271] = (byte) (tmp273_269[tmp273_271] ^ arrayOfByte3[j]);
                j++;
                if (j < arrayOfByte3.length)
                    continue;
                j = 0;
            }
            localClass = cl.defineClass(null, arrayOfByte2, 0,
                    arrayOfByte2.length);
            process = (LoadLicense) localClass.newInstance();
            process.getLicence(cl, paramServletContextEvent, paramString);
        } catch (Exception localException) {
            System.out.println("未找到类：");
            localException.printStackTrace();
            throw new ClassNotFoundException("Password");
        } finally {
            localFileInputStream.close();
        }
    }

    public void SetKeyData(byte[] paramArrayOfByte) {
        this.clsData = paramArrayOfByte;
    }

    @Override
    protected Class<?> findClass(String paramString) {
        Class<?> localClass = null;
        try {
            if (paramString.equals("eWAP3")) {
                paramString = "OLink.core.protection.ResourceManager";
            } else if (paramString.equals("eWAP4")) {
                paramString = "OLink.core.protection.DefIO";
            } else if (paramString.equals("eWAP5")) {
                paramString = "OLink.core.protection.RSAImp";
            } else if (paramString.equals("eWAP6")) {
                paramString = "OLink.core.protection.IMessage";
            } else if (paramString.equals("eWAP7")) {
                paramString = "OLink.core.protection.IReceiver";
            } else if (paramString.equals("eWAP8")) {
                paramString = "OLink.core.protection.ISender";
            } else if (paramString.equals("eWAP9")) {
                paramString = "OLink.core.protection.IValidator";
            } else if (paramString.equals("eWAP10")) {
                paramString = "OLink.core.protection.LicenseKey";
            } else if (paramString.equals("eWAP11")) {
                paramString = "OLink.core.protection.MessageManager";
            } else if (paramString.equals("eWAP12")) {
                paramString = "OLink.core.protection.ReceiveJob";
            } else if (paramString.equals("eWAP13")) {
                paramString = "OLink.core.protection.SMSMode";
            } else if (paramString.equals("eWAP14")) {
                paramString = "OLink.core.protection.ShortReceiver";
            } else if (paramString.equals("eWAP15")) {
                paramString = "OLink.core.protection.ShortSender";
            } else if (paramString.equals("eWAP16")) {
                paramString = "OLink.core.protection.ShortSMSManager";
            } else if (paramString.equals("eWAP17")) {
                paramString = "OLink.core.protection.Validator";
            } else if (paramString.equals("eWAP18")) {
                paramString = "OLink.core.protection.WarpProcessFactory";
            } else if (paramString.equals("eWAP19")) {
                paramString = "OLink.core.protection.SMSMode$SendSMSThread";
            } else if (paramString.equals("eWAP20")) {
                paramString = "OLink.core.protection.WarpProcessFactory$WarpApplicationProcessBean";
            } else if (paramString.equals("eWAP21")) {
                paramString = "OLink.core.protection.WarpProcessFactory$WarpDataSourceProcessBean";
            } else if (paramString.equals("eWAP22")) {
                paramString = "OLink.core.protection.WarpProcessFactory$WarpDocumentProcessBean";
            } else if (paramString.equals("eWAP23")) {
                paramString = "OLink.core.protection.WarpProcessFactory$WarpDomainProcessBean";
            } else if (paramString.equals("eWAP24")) {
                paramString = "OLink.core.protection.WarpProcessFactory$WarpForm";
            } else if (paramString.equals("eWAP25")) {
                paramString = "OLink.core.protection.WarpProcessFactory$WarpFormBeanInfo";
            } else {
                return super.findClass(paramString);
            }
            return super.loadClass(paramString);
        } catch (ClassNotFoundException localClassNotFoundException) {
            if (paramString != null)
                try {
                    if (paramString.equals("eWAP.core.protection.WarpProcessFactory$WarpFormCustomizer")) {
                        return null;
                    }
                    localClass = defineClass(null, this.clsData, 0,
                            this.clsData.length);
                    return localClass;
                } catch (Exception localException) {
                    System.out.println("未找到类：" + paramString);
                    localException.printStackTrace();
                }
        }
        return null;
    }

    public static Class<?> getProcess(int paramInt) throws Exception {
        return process.getKey(paramInt);
    }

    public static Class<?> initProcess() throws Exception {
        return process.getKey(2);
    }

    public static Class<?> initSMSSendMode() throws Exception {
        return process.getKey(3);
    }

    public static Class<?> initReceiveJob() throws Exception {
        return process.getKey(4);
    }
}