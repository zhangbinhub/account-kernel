package OLink.core.protection;

import eWAP.core.*;
import eWAP.core.dbaccess.ConnectionFactory;
import eWAP.core.dbaccess.DataSourceDef;
import eWAP.core.request.RequestManager;
import eWAP.core.request.RequestWrapper;
import eWAP.core.screen.ScreenManager;
import eWAP.core.trans.TransManager;
import eWAP.core.variable.VariableManager;
import eWAP.core.xmlhttp.XMLSendManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ResourceManager
        implements eWAP.core.ResourceManager
{
    private static RequestManager reqManager = null;
    private static Hashtable<String, RequestProcessor> processorHash = new Hashtable();
    private static Hashtable<String, LoginSession> userLogin = new Hashtable();

    static {
        String path = InitResource.getRootPath();
        System.out.println("Start initSource");
        initSource(path);
        System.out.println("Start RunAppServer");
        RunAppServer(path);
        System.out.println("Start initTrans");
        initDefine(path);
    }

    public LoginSession getLogin(String userUniqueID)
    {
        if (containsLogin(userUniqueID))
            return (LoginSession)userLogin.get(userUniqueID);
        return null;
    }

    public void addLogin(String userUniqueID, LoginSession login)
    {
        userLogin.put(userUniqueID, login);
    }

    public void removeLogin(String userUniqueID)
    {
        if ((userUniqueID == null) || (userUniqueID.equals(""))) return;
        if (userLogin.containsKey(userUniqueID)) userLogin.remove(userUniqueID);
        for (String key : userLogin.keySet())
        {
            LoginSession login = (LoginSession)userLogin.get(key);
            try
            {
                if (login.se.getAttribute("loginSession") != null) continue; userLogin.remove(key);
            }
            catch (Exception e)
            {
                userLogin.remove(key);
            }
        }
    }

    public boolean containsLogin(String userUniqueID)
    {
        if ((userUniqueID == null) || (userUniqueID.equals(""))) {
            return false;
        }
        return userLogin.containsKey(userUniqueID);
    }

    public static void initDefine(String path)
    {
        RequestProcessor processor = null;
        try
        {
            reqManager = new RequestManager();
            reqManager.init();
            processor = new ScreenManager();
            processor.init();
            processorHash.put("SCREEN", processor);
            processor = new VariableManager();
            processor.init();
            processorHash.put("VARIABLE", processor);
            processor = new TransManager();
            processor.init();
            processorHash.put("TRANS", processor);
            processor = new XMLSendManager();
            processorHash.put("XMLSEND", processor);
        }
        catch (Exception e)
        {
            System.out.println("资源管理器初始化异常" + e);
        }
    }

    public RequestManager getReqManager()
    {
        return reqManager;
    }

    public RequestProcessor getRequestProcessor(String type)
    {
        if (type == null) {
            return null;
        }
        return (RequestProcessor)processorHash.get(type.toUpperCase());
    }

    public static boolean verifyLicense()
    {
        return true;
    }

    public boolean inRunVerify(RequestWrapper rw)
    {
        return true;
    }

    public String setInstanceVar(Object transob, ResourcePool eM, RequestWrapper req, ConnectionFactory ds)
    {
        Field[] fieldList = transob.getClass().getDeclaredFields();
        Field field = null;
        String name = null;
        for (int i = 0; i < fieldList.length; i++) {
            try
            {
                Object ot = null;
                field = fieldList[i];
                String type = field.getType().toString();
                name = field.getName();
                if (type.endsWith(".ConnectionFactory"))
                {
                    field.setAccessible(true);
                    field.set(transob, ds);
                }
                else if (type.endsWith(".ResourcePool"))
                {
                    field.setAccessible(true);
                    field.set(transob, eM);
                }
                else if (type.endsWith(".HttpServletRequest"))
                {
                    field.setAccessible(true);
                    field.set(transob, req.getHttpServletRequest());
                }
                else if (type.endsWith(".HttpServletResponse"))
                {
                    field.setAccessible(true);
                    field.set(transob, req.getHttpServletResponse());
                }
                else
                {
                    String value = eM.getParameter(name);
                    if (value == null)
                    {
                        ot = eM.getUserVariable(name);
                        if (ot != null) value = ot.toString();
                    }
                    if (value == null)
                    {
                        ot = eM.getSysVariable(name);
                        if (ot != null) value = ot.toString();
                    }
                    if (value != null) {
                        field.setAccessible(true);
                        if ((type.endsWith("boolean")) || (type.endsWith(".Boolean")))
                        {
                            if (value.equals("true")) {
                                field.set(transob, Boolean.valueOf(true));
                            }
                        }
                        else if (type.endsWith(".String"))
                        {
                            field.set(transob, value);
                        }
                        else if ((type.endsWith("int")) || (type.endsWith(".Integer")))
                        {
                            value = value.replaceAll(",", "");
                            if (value.equals("")) value = "0";
                            field.set(transob, Integer.valueOf(Integer.parseInt(value)));
                        }
                        else if ((type.endsWith(".Double")) || (type.endsWith("double")))
                        {
                            value = value.replaceAll(",", "");
                            if (value.equals("")) value = "0";
                            field.set(transob, Double.valueOf(value));
                        }
                        else if ((type.endsWith(".Float")) || (type.endsWith("float")))
                        {
                            value = value.replaceAll(",", "");
                            if (value.equals("")) value = "0";
                            field.set(transob, Float.valueOf(value));
                        }
                        else {
                            if ((!type.endsWith(".Long")) && (!type.endsWith("long")))
                                continue;
                            value = value.replaceAll(",", "");
                            if (value.equals("")) value = "0";
                            field.set(transob, Long.valueOf(value));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("setVarValue(" + name + ") Exception:" + e);
                return "setVarValue(" + name + ") Exception:" + e;
            }
        }
        return null;
    }

    public static void RunAppServer(String path)
    {
        String[] name = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "ApplServer", "Server", "name");
        String[] server = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "ApplServer", "Server", "class");
        String[] argv = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "ApplServer", "Server", "argv");
        if (server != null) for (int i = 0; i < server.length; i++)
            try
            {
                Class cl = Class.forName(server[i]);
                Method transMethod = cl.getMethod("Start", new Class[] { String.class });
                transMethod.invoke(cl.newInstance(), new Object[] { argv[i] });
                System.out.println("启动服务[" + name[i] + "]成功");
            }
            catch (Exception e) {
                System.out.println("启动服务[" + name[i] + "]失败:" + e);
            }
    }

    public static void initSource(String path)
    {
        String tmpStr = "";
        String[] dsName = (String[])null;
        String[] dsNo = (String[])null;
        String[] nameSpace = (String[])null;
        String[] driverType = (String[])null;
        String[] runAs = (String[])null;
        String[] dialect = (String[])null;
        Map dsDef = new HashMap();
        dsName = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "RESOURCE", "DS", "name");
        dsNo = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "RESOURCE", "DS", "dsNo");
        nameSpace = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "RESOURCE", "DS", "jndi");
        driverType = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "RESOURCE", "DS", "driverType");
        runAs = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "RESOURCE", "DS", "runAs");
        dialect = ResourcePool.GetConfigInfoAttr(path + "config/system.xml", "RESOURCE", "DS", "hibernate.dialect");
        int sysNo = -1;
        for (int i = 0; i < dsNo.length; i++)
        {
            int no = Tools.String2Int(dsNo[i], 0);
            if ((runAs != null) && (runAs[i] != null) && (!runAs[i].equals("")))
            {
                ResourcePool.setSysDsNo(no);
                sysNo = i;
            }
            System.out.println("dsNo:" + no + " name:" + dsName[i] + " jndi:" + nameSpace[i] + " driver:" + driverType[i]);
            DataSourceDef def = new DataSourceDef();
            DataSource ds = null;
            try
            {
                Context ctx = new InitialContext();
                ds = (DataSource)ctx.lookup(nameSpace[i]);
            }
            catch (NamingException e)
            {
                System.out.println("Could not initalize DataSource(NamingException): " + dsName[i]);
            }
            catch (Exception e)
            {
                System.out.println("path=[" + path + "]  Could not initalize DataSource(Other):" + dsName[i]);
            }
            def.setDs(ds);
            def.setDsName(dsName[i]);
            def.setDrirverType(driverType[i]);
            dsDef.put(new Integer(no), def);
        }
        ResourcePool.setDataSource(dsDef);

        if (sysNo != -1)
        {
            double version = Tools.String2Double(runAs[sysNo], 1.0D);
            char[] cbuf = new char[1024];
            try
            {
                if (version >= 2.0D)
                {
                    String sName = path + "config/hibernate.cfg.xml";
                    String dName = path + "WEB-INF/classes/hibernate.cfg.xml";
                    FileReader fr = new FileReader(sName);
                    tmpStr = "";
                    while (true)
                    {
                        int len = fr.read(cbuf);
                        if (len < 0) break;
                        tmpStr = tmpStr + new String(cbuf, 0, len);
                    }
                    fr.close();
                    tmpStr = tmpStr.replace("${EWAPCFG.hibernate.dialect}", dialect[sysNo]);
                    FileWriter fw = new FileWriter(dName);
                    fw.write(tmpStr);
                    fw.close();
                    ResourcePool.setRunAs(version);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String dName = path + "WEB-INF/classes/jbpm.cfg.xml";
        File myFilePath = new File(dName);
        myFilePath.delete();
        dName = path + "WEB-INF/classes/jbpm.hibernate.cfg.xml";
        myFilePath = new File(dName);
        myFilePath.delete();
        if (ResourcePool.getRunAs() < 2.0D)
        {
            dName = path + "WEB-INF/classes/hibernate.cfg.xml";
            myFilePath = new File(dName);
            myFilePath.delete();
        }
    }
}