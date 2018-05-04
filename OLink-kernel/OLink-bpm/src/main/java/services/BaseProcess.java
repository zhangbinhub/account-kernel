/*
/*
 * 创建日期: 2011-05-30
 * 项目名称: OLink平台
 * 功能说明: Olink缺省交易接口实现
 */
package services;

/**
 * @author xgy
 * OLink平台接口处理代码
 */

import eWAP.core.dbaccess.*;
import OLink.bpm.constans.Web;
import eWAP.core.IBaseProcess;
import eWAP.core.IDefIO;
import eWAP.core.InitResource;
import eWAP.core.LoginSession;
import eWAP.core.ResourceManager;
import eWAP.core.ResourcePool;
import eWAP.core.Tools;

import org.apache.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseProcess implements IBaseProcess {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(BaseProcess.class);

    /**
     * 建立公共信息，不必修改
     */

    public BaseProcess() {
        logger.info("Init BaseProcess file.encoding:"
                + System.getProperty("file.encoding"));
    }

    @SuppressWarnings("unchecked")
    public Boolean loginTransaction(Object request, String DomainNo,
                                    String UserNo) {
        if (request instanceof HttpServletRequest) // eWAP2.0
        {
            try {
                ConnectionFactory dbconn = new ResourcePool()
                        .getConnectionFactory();

                String sqlStr = "select u.name,d.DESCRIPTION,t.id,u.id,d.id,a.id "
                        + "from t_user u inner join t_domain d on u.domainid=d.id left join "
                        + " t_department t on u.DEFAULTDEPARTMENT=t.ID left join t_domain_application_set w "
                        + " on d.ID=w.DomainID left join t_application a on w.applicationID=a.ID"
                        + " where d.name='" + DomainNo + "' and u.loginno='"
                        + UserNo + "'";
                ArrayList<Object[]> l = dbconn
                        .doQuery(sqlStr, null, 0, 0, true);
                if (l == null || l.size() < 1)
                    return null;
                Object[] col = l.get(0);
                Map<String, Object> var = new HashMap<String, Object>();
                var.put("UserID", col[3]);// 唯一ID
                var.put("UserName", col[0]);
                var.put("DomainName", col[1]);
                var.put("DepartmentID", col[2]);
                var.put("UserLoginno", UserNo);
                var.put("DomainID", col[4]);
                var.put("ApplicationID", col[5]);
                var.put("LoginNO", UserNo);
                return ResourcePool.loginProcess((HttpServletRequest) request,
                        (String) col[3], var);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            ResourcePool req = (ResourcePool) request;
            try {
                String r_ip_addr = req.getRequestIP();

                String userName = "";
                String userPwd = "";
                String userInst = "";
                String status = "";
                String userlevel = "";

                String returnStr = "";
                String sqlStr = "";

                String InstID = req.getParameter("InstID");
                String UserID = req.getParameter("UserID");
                String password = req.getParameter("password");
                password = Tools.getRSAUtil().decrypt(
                        ResourcePool.getPrivateKey(), password, true);
                // password = Tools.cryptMd5Str(password);
                ConnectionFactory dbclass = req.getConnectionFactory(0);
                sqlStr = "select LInst_Name||USER_NAME UserName, USER_PASSWORD, user_status,user_level, SysDate-Passwd_Date days,FIRSTDATE begin_date,WorkDate end_date,ProcessNo,User_IP,Inst_Flag from T_SYS_PARA ,T_MGT_OPERATOR_Y u,T_INST_INFO i "
                        + "where u.inst_No=i.Inst_No and u.inst_no = '"
                        + InstID + "' and user_code='" + UserID + "' ";
                ArrayList<Object[]> mainVec = dbclass.doQuery(sqlStr, null, -1,
                        -1, true);
                if (mainVec == null) {
                    req.setTransFlag(-1);
                    req.setTransMsg("数据库连接错误");
                    return false;
                } else if (mainVec.size() == 0) {
                    req.setTransFlag(-1);
                    req.setTransMsg("不是合法用户");
                    return false;
                } else {
                    Object[] o = mainVec.get(0);
                    userName = Tools.trimNull((String) o[0]);
                    userPwd = Tools.trimNull((String) o[1]);
                    status = Tools.trimNull((String) o[2]);
                    userlevel = Tools.trimNull((String) o[3]);
                    String tmpStr = Tools.trimNull(o[4].toString());
                    String begindate = Tools.trimNull((String) o[5]);
                    String enddate = Tools.trimNull((String) o[6]);
                    String User_IP = Tools.trimNull((String) o[8]).trim();
                    String ProcessNo = (o[7].toString()).trim();
                    String Cancel_Inst = Tools.trimNull((String) o[9]);
                    if (ProcessNo.equals("0"))
                        enddate = Pub.DateSub(enddate, 1);
                    if (userPwd != null) {
                        userPwd = userPwd.trim();
                    }
                    if (User_IP != null) {
                        User_IP = User_IP.trim();
                    }
                    // 请求上传密码加密

                    if (password != null) {
                        password = password.trim();
                    }
                    // System.out.println("密码名文：["+userPwd + "]");
                    // System.out.println("密码密文：["+codePass + "]");

                    userInst = InstID;
                    if (((userPwd == null || userPwd.equals("")) && (password == null || password
                            .equals("")))
                            || userPwd.equals(password)) {
                        if (User_IP != "" && !User_IP.equals(",")
                                && User_IP.equals("")) {
                            int i = User_IP.indexOf(',');
                            String eIP = User_IP.substring(i + 1);
                            String sIP = User_IP.substring(0, i);

                            int ip1 = 0;
                            int ip2 = 0;
                            int ip3 = 0;

                            String[] list1 = sIP.split("\\.");
                            String[] list2 = eIP.split("\\.");
                            String[] list3 = r_ip_addr.split("\\.");
                            for (i = 0; i < list1.length; i++) {
                                ip1 = Integer.parseInt(list1[i].toString());
                                ip2 = Integer.parseInt(list2[i].toString());
                                ip3 = Integer.parseInt(list3[i].toString());
                                if (ip3 < ip1 || ip3 > ip2) {
                                    req.setTransFlag(-1);
                                    req.setTransMsg("地址不匹配");
                                    return false;
                                }

                            }
                        }
                        if (!status.equalsIgnoreCase("1")) {
                            req.setTransFlag(-1);
                            req.setTransMsg("用户状态为已注销");
                            return false;
                        }
                        // 登记登陆用户信息
                        req.setSysVariable("FirstDate", begindate);
                        req.setSysVariable("WorkDate", enddate);
                        HashMap<String, Object> var = new HashMap<String, Object>();
                        var.put("UserID", UserID);
                        var.put("UserName", userName);
                        var.put("InstID", userInst);
                        var.put("UserLevel", userlevel);
                        var.put("Cancel_Inst", Cancel_Inst);
                        var.put("EXCHANGE1", "");
                        var.put("EXCHANGE2", "");
                        var.put("EXCHANGE3", "");
                        if (!ResourcePool.loginProcess(req
                                .getHttpServletRequest(), userInst + ","
                                + UserID, var)) {
                            req.setTransFlag(-1);
                            req.setTransMsg("用户已登陆");
                            return false;
                        }
                        if (Double.parseDouble(tmpStr) > 30) {
                            sqlStr = "update t_mgt_operator_y set User_Password='密码过期' where  Inst_No='"
                                    + InstID
                                    + "' and User_Code ='"
                                    + UserID
                                    + "'";
                            dbclass.executeUpdate(sqlStr);
                            returnStr = returnStr + "ModifyPasswd";
                        }

                        returnStr = "Ret|=|" + returnStr;
                        req.setTransFlag(0);
                        req.setResultObj(returnStr);
                        return true;
                    } else {
                        req.setTransFlag(-1);
                        req.setTransMsg("用户密码错误");
                        if (userPwd.equals("密码过期")) {
                            req.setTransFlag(-1);
                            req.setTransMsg("用户密码已过期");
                        }
                        return false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (request instanceof ResourcePool) {
                    req.setTransFlag(-1);
                    req.setResultObj(e.toString());
                }
                return false;
            }
        }
    }

    /**
     * 　 Method：public void SqlExecute() Function: 输入参数： String sqlStr：SQL语句
     * 输出参数:是否成功
     */
    public void SqlExecute(ResourcePool req, int dbsNo, String sqlStr,
                           String sqlPara, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }

        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);

        sqlStr = Tools.getDefIO().GetSqlStr(sqlStr);
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();

        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);
        if (!dbclass.executeUpdate(sqlStr, callPara)) {
            logger.info("执行出错：" + dbclass.getMsg() + "sql=" + sqlStr);
            req.setTransFlag(-1);
            req.setTransMsg(dbclass.getMsg());
            return;
        } else {
            req.setTransFlag(0);
            req.setTransMsg("成功处理" + dbclass.getRow() + "条记录");
            WriteOperLog(req, "成功", funcCode);
            return;
        }
    }

    /**
     * 　 Method：public void commSqlQuery() Function: 输入参数： String sqlStr：SQL语句
     * String goPage：查询页号 输出参数:查询结果
     */
    public String SqlQuery(ResourcePool req, int dbsNo, String sqlStr,
                           String sqlPara, String fldField, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            return "-1|=|没有执行权限";
        }
        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            return "-1获取数据连接失败";
        }

        sqlStr = Tools.getDefIO().GetSqlStr(sqlStr);
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();

        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);
        try {
            String rString = dbclass.doQuery(sqlStr, callPara, 0, 0, fldField,
                    true);
            if (rString == null) {
                logger.debug(sqlStr);
                logger.debug("查询记录异常: " + dbclass.getMsg());
                return "-1|=|" + dbclass.getMsg();
            }
            if (rString.equals("")) {
                return "-1|=|" + "没有满足条件的记录";
            }
            WriteOperLog(req, "成功", funcCode);
            return rString;
        } catch (Exception e) {
            return "-1|=|查询记录出错" + e.toString();
        }
    }

    /**
     * 　 Method：public void SqlQueryForPage() Function: 输入参数： String
     * sqlStr：SQL语句 String goPage：查询页号 输出参数:查询结果
     */
    public void SqlQueryForPage(ResourcePool req, int dbsNo, String sqlStr,
                                String sqlPara, int linesCount, int goPage,
                                boolean getTotalPageFlag, String headCtrl, String bodyCtrl,
                                String fldStr, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }

        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            req.setTransFlag(-1);
            req.setTransMsg("获取数据连接失败");
            return;
        }

        sqlStr = Tools.getDefIO().GetSqlStr(sqlStr);
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();

        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);

        try {
            StringBuffer returnStrBuffer = new StringBuffer("|=|");

            boolean getLastCol = true;
            int iPage = goPage;
            int iLinesCount = linesCount;
            String drive = dbclass.getDriverType();
            int totalRecord = 0;

            if (goPage >= 1 && linesCount >= 1 && drive.indexOf("oracle") >= 0) // oracle翻页
            {
                int intbegin = (goPage - 1) * linesCount + 1;
                if (goPage == 1 || getTotalPageFlag) {
                    String cntStr = "select count(*) cnt from (" + sqlStr + ")";
                    try {
                        cntStr = dbclass.doQueryString(cntStr, callPara);
                        if (cntStr == null) {
                            req.setTransFlag(-1);
                            req.setTransMsg(dbclass.getMsg());
                            return;
                        }
                        totalRecord = Integer.parseInt(cntStr);
                    } catch (Exception e) {
                        req.setTransFlag(-1);
                        req.setTransMsg("" + e);
                        return;
                    }
                    if (totalRecord <= 0) {
                        req.setTransFlag(-1);
                        req.setTransMsg("没有满足条件的记录");
                        return;
                    }
                }
                sqlStr = "select * from (select a.*,rownum rownumofineed from ("
                        + sqlStr
                        + ") a where rownum<="
                        + goPage
                        * linesCount
                        + " order by rownumofineed) where rownumofineed>= "
                        + intbegin;
                getLastCol = false;
                iLinesCount = 0;
                iPage = 0;
            }
            // 如有需要,增加其他数据库翻页
            String rString = dbclass.doQuery(sqlStr, callPara, iPage,
                    iLinesCount, fldStr, getLastCol);
            if (rString == null) {
                logger.debug(sqlStr);
                logger.debug("查询记录异常: " + dbclass.getMsg());
                req.setTransFlag(-1);
                req.setTransMsg(dbclass.getMsg());
                return;
            }
            if (rString.equals("")) {
                req.setTransFlag(-1);
                req.setTransMsg("没有满足条件的记录");
                return;
            }
            if (bodyCtrl == null || bodyCtrl.equals("")) {
                String[] colName = dbclass.getColName();
                for (int i = 0; i < colName.length; i++) {
                    if (i == 0)
                        bodyCtrl = colName[i];
                    else
                        bodyCtrl += "^" + colName[i];
                }
            }
            String outputBodyCtrl = bodyCtrl;
            if (headCtrl != null)
                outputBodyCtrl += "|^|" + headCtrl;
            returnStrBuffer.append(rString);
            rString = null;
            if (goPage == 1 || getTotalPageFlag) {
                if (totalRecord != 0)// 特定翻页
                    returnStrBuffer
                            .append("^"
                                    + (totalRecord / linesCount + ((totalRecord % linesCount) > 0 ? 1
                                    : 0)) + "^" + totalRecord);
                else
                    // 通用查询
                    returnStrBuffer.append("^" + dbclass.getPageCount() + "^"
                            + dbclass.getRow());
            }
            String returnStr = "";
            if (returnStrBuffer.length() > 0) {
                returnStr = outputBodyCtrl + returnStrBuffer.toString();
            }
            req.setTransFlag(0);
            req.setResultObj(returnStr);
            if (goPage == 1)
                WriteOperLog(req, "成功", funcCode);
            return;
        } catch (Exception e) {
            req.setTransFlag(-1);
            req.setTransMsg("查询记录出错" + e.toString());
            return;
        }
    }

    /**
     * 　 Method：public void CreateExcelBySql() Function:Excel 输入参数： String
     * sqlStr：SQL语句 String goPage：查询页号 输出参数:查询结果
     */
    public void CreateExcelBySql(ResourcePool req, int dbsNo, String sqlStr,
                                 String sqlPara, String headCtrl, String bodyCtrl, String tailCtrl,
                                 String reportName, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }

        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            req.setTransFlag(-1);
            req.setTransMsg("获取数据连接失败");
            return;
        }

        sqlStr = Tools.getDefIO().GetSqlStr(sqlStr);
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();

        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);

        try {
            ByteArrayOutputStream baos;
            ArrayList<?> l = dbclass.doQuery(sqlStr, callPara, 0, 0, true);
            if (l == null) {
                logger.debug("查询记录异常: " + dbclass.getMsg());
                req.setTransFlag(-1);
                req.setTransMsg(dbclass.getMsg());
                return;
            }
            if (l.size() == 0) {
                req.setTransFlag(-1);
                req.setTransMsg("没有满足条件的记录");
                return;
            }
            if (bodyCtrl == null || bodyCtrl.equals("")) {
                String[] colName = dbclass.getColName();
                for (int i = 0; i < colName.length; i++) {
                    if (i == 0)
                        bodyCtrl = colName[i];
                    else
                        bodyCtrl += "^" + colName[i];
                }
            }
            baos = (ByteArrayOutputStream) Tools.getDefIO().CreateExcelByCtrl(
                    headCtrl, bodyCtrl, tailCtrl, l);
            if (baos == null) {
                req.setTransFlag(-1);
                req.setTransMsg("生成文件失败");
                return;
            }
            HttpServletResponse resp = req.getHttpServletResponse();
            resp.setContentType("application/x-msdownload");
            if (reportName == null || reportName.equals(""))
                reportName = "" + System.currentTimeMillis();
            reportName = new String(reportName.getBytes(), "ISO8859-1");
            resp.setHeader("Content-disposition", "attachment; filename="
                    + reportName + ".xls");
            resp.setContentLength(baos.size());
            baos.writeTo(resp.getOutputStream());
            baos.flush();
            baos.close();
            WriteOperLog(req, "成功", funcCode);
            return;
        } catch (Exception e) {
            req.setTransFlag(-1);
            req.setTransMsg("查询记录出错" + e.toString());
            return;
        }
    }

    /**
     * 　 Method：public void CreateTextBySql() Function: 输入参数： String
     * sqlStr：SQL语句 String goPage：查询页号 输出参数:查询结果
     */
    public void CreateTextBySql(ResourcePool req, int dbsNo, String sqlStr,
                                String sqlPara, String headCtrl, String bodyCtrl, String tailCtrl,
                                String fldStr, String fldRow, boolean useTitle, String reportName,
                                String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }

        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            req.setTransFlag(-1);
            req.setTransMsg("获取数据连接失败");
            return;
        }

        sqlStr = Tools.getDefIO().GetSqlStr(sqlStr);
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();

        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);
        if (fldStr == null || fldStr.equals(""))
            fldStr = ",";
        if (fldRow == null || fldRow.equals(""))
            fldRow = "\r\n";

        try {
            ByteArrayOutputStream baos;
            ArrayList<?> l = dbclass.doQuery(sqlStr, callPara, 0, 0, true);
            if (l == null) {
                logger.debug("查询记录异常: " + dbclass.getMsg());
                req.setTransFlag(-1);
                req.setTransMsg(dbclass.getMsg());
                return;
            }
            if (l.size() == 0) {
                req.setTransFlag(-1);
                req.setTransMsg("没有满足条件的记录");
                return;
            }
            if (bodyCtrl == null || bodyCtrl.equals("")) {
                String[] colName = dbclass.getColName();
                for (int i = 0; i < colName.length; i++) {
                    if (i == 0)
                        bodyCtrl = colName[i];
                    else
                        bodyCtrl += "^" + colName[i];
                }
            }
            if (useTitle)
                baos = (ByteArrayOutputStream) Tools.getDefIO().CreateText(
                        headCtrl, bodyCtrl, tailCtrl, fldStr, fldRow, true, l);
            else
                baos = (ByteArrayOutputStream) Tools.getDefIO().CreateText(
                        headCtrl, bodyCtrl, tailCtrl, fldStr, fldRow, false, l);
            if (baos == null) {
                req.setTransFlag(-1);
                req.setTransMsg("生成文件失败");
                return;
            }
            HttpServletResponse resp = req.getHttpServletResponse();
            resp.setContentType("application/x-msdownload");
            if (reportName == null || reportName.equals(""))
                reportName = "" + System.currentTimeMillis();
            reportName = new String(reportName.getBytes(), "ISO8859-1");
            resp.setHeader("Content-disposition", "attachment; filename="
                    + reportName + ".txt");
            resp.setContentLength(baos.size());
            baos.writeTo(resp.getOutputStream());
            baos.flush();
            baos.close();
            WriteOperLog(req, "成功", funcCode);
            return;
        } catch (Exception e) {
            req.setTransFlag(-1);
            req.setTransMsg("查询记录出错" + e.toString());
            return;
        }
    }

    /**
     * 　 Method：public void CallProc() Function: 输出参数:查询结果 ResourcePool
     */
    public String CallProc(ResourcePool req, int dbsNo, String sqlStr,
                           String sqlPara, String fldStr, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            return "-1|=|没有执行权限";
        }
        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            return "-1:获取数据连接失败";
        }
        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);

        try {
            ArrayList<?> l = dbclass.doCall(sqlStr, callPara, 0, 0);
            if (l == null) {
                logger.debug(sqlStr);
                logger.debug("查询记录异常: " + dbclass.getMsg());
                return "-1:" + dbclass.getMsg();
            }
            Object[] oo = (Object[]) l.get(1);
            String valueStr = null;
            StringBuffer tbuf = null;
            String[] oName = (String[]) l.get(0);
            for (int i = 0; i < oo.length; i++) {
                if (oName[i] == null || oName[i].equals(""))
                    continue;
                if (valueStr == null) {
                    valueStr = Tools.toString(oo[i], "");
                } else {
                    valueStr += "^" + Tools.toString(oo[i], "");
                }
            }
            if (l.size() > 2) {
                for (int i = 2; i < l.size(); i++) {
                    Object[] row = (Object[]) l.get(i);
                    for (int j = 0; j < row.length; j++) {
                        if (tbuf == null)
                            tbuf = new StringBuffer(Tools.toString(row[j]));
                        else
                            tbuf.append(fldStr + Tools.toString(row[j]));
                    }
                }
            }
            valueStr = (valueStr != null) ? valueStr + "^" + tbuf : tbuf
                    .toString();
            WriteOperLog(req, "成功", funcCode);
            return valueStr;

        } catch (Exception e) {
            return "-1:查询记录出错" + e.toString();
        }
    }

    /**
     * 　 Method：public String CallProc() Function: 输出参数:查询结果
     */
    public void CallProcForPage(ResourcePool req, int dbsNo, String sqlStr,
                                String sqlPara, int linesCount, int goPage,
                                boolean getTotalPageFlag, String headCtrl, String bodyCtrl,
                                String fldStr, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }
        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            req.setTransFlag(-1);
            req.setTransMsg("获取数据连接失败");
            return;
        }
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();
        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);
        if (fldStr == null || fldStr.equals(""))
            fldStr = ",";

        StringBuffer tbuf = null;
        try {
            StringBuffer returnStrBuffer = new StringBuffer("|=|");

            ArrayList<?> l = dbclass.doCall(sqlStr, callPara, goPage,
                    linesCount);
            if (l == null) {
                logger.debug(sqlStr);
                logger.debug("查询记录异常: " + dbclass.getMsg());
                req.setTransFlag(-1);
                req.setTransMsg(dbclass.getMsg());
                return;
            }
            String returnStr = "";
            String valueStr = null;
            String nameStr = null;
            String[] oName = (String[]) l.get(0);
            Object[] oo = (Object[]) l.get(1);
            for (int i = 0; i < oName.length; i++) {
                if (oName[i] == null || oName[i].equals(""))
                    continue;
                if (valueStr == null) {
                    nameStr = Tools.toString(oName[i], "");
                    valueStr = Tools.toString(oo[i], "");
                } else {
                    nameStr += "^" + Tools.toString(oName[i], "");
                    valueStr += "^" + Tools.toString(oo[i], "");
                }
            }
            if (valueStr != null)
                valueStr = nameStr + "|=|" + valueStr;
            if (l.size() > 2) {
                if (bodyCtrl == null || bodyCtrl.equals("")) {
                    String[] colName = dbclass.getColName();
                    for (int i = 0; i < colName.length; i++) {
                        if (i == 0)
                            bodyCtrl = colName[i];
                        else
                            bodyCtrl += "^" + colName[i];
                    }
                }
                for (int i = 2; i < l.size(); i++) {
                    Object[] row = (Object[]) l.get(i);
                    for (int j = 0; j < row.length; j++) {
                        if (tbuf == null)
                            tbuf = new StringBuffer(Tools.toString(row[j]));
                        else
                            tbuf.append(fldStr + Tools.toString(row[j]));
                    }
                }
                returnStrBuffer.append(tbuf);
                if (goPage == 1 || getTotalPageFlag) {
                    returnStrBuffer.append("^" + dbclass.getPageCount() + "^"
                            + dbclass.getRow());
                }
                if (returnStrBuffer.length() > 0) {
                    String outputHeadCtrl = headCtrl;
                    String outputBodyCtrl = bodyCtrl;
                    if (outputHeadCtrl != null)
                        outputBodyCtrl += "|^|" + outputHeadCtrl;
                    returnStr = outputBodyCtrl + returnStrBuffer.toString();
                }
            }
            if (valueStr != null) {
                returnStr = (returnStr == "") ? valueStr : returnStr + "|@|"
                        + valueStr;
            }
            req.setTransFlag(0);
            req.setResultObj(returnStr);
            if (goPage == 1)
                WriteOperLog(req, "成功", funcCode);
            return;
        } catch (Exception e) {
            req.setTransFlag(-1);
            req.setTransMsg("查询记录出错" + e.toString());
            return;
        }
    }

    /**
     * 　 Method：public String CreateExcelByProc() Function: 输出参数:查询结果
     */
    public void CreateExcelByProc(ResourcePool req, int dbsNo, String sqlStr,
                                  String sqlPara, String headCtrl, String bodyCtrl, String tailCtrl,
                                  String reportName, String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }
        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            req.setTransFlag(-1);
            req.setTransMsg("获取数据连接失败");
            return;
        }
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();
        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);

        try {
            ByteArrayOutputStream baos;
            ArrayList<?> l = dbclass.doCall(sqlStr, callPara, 0, 0);
            if (l == null) {
                logger.debug("查询记录异常: " + dbclass.getMsg());
                req.setTransFlag(-1);
                req.setTransMsg(dbclass.getMsg());
                return;
            }
            if (l.size() == 0) {
                req.setTransFlag(-1);
                req.setTransMsg("没有满足条件的记录");
                return;
            }
            if (bodyCtrl == null || bodyCtrl.equals("")) {
                String[] colName = dbclass.getColName();
                for (int i = 0; i < colName.length; i++) {
                    if (i == 0)
                        bodyCtrl = colName[i];
                    else
                        bodyCtrl += "^" + colName[i];
                }
            }
            baos = (ByteArrayOutputStream) Tools.getDefIO().CreateExcelByCtrl(
                    headCtrl, bodyCtrl, tailCtrl, l);
            if (baos == null) {
                req.setTransFlag(-1);
                req.setTransMsg("生成文件失败");
                return;
            }
            HttpServletResponse resp = req.getHttpServletResponse();
            resp.setContentType("application/x-msdownload");
            if (reportName == null || reportName.equals(""))
                reportName = "" + System.currentTimeMillis();
            // reportName=java.net.URLEncoder.encode(reportName, "UTF-8");
            // reportName=new String(reportName.getBytes("GBK"), "ISO8859-1");
            reportName = new String(reportName.getBytes(), "ISO8859-1");
            resp.setHeader("Content-disposition", "attachment; filename="
                    + reportName + ".xls");
            resp.setContentLength(baos.size());
            baos.writeTo(resp.getOutputStream());
            baos.flush();
            baos.close();
            WriteOperLog(req, "成功", funcCode);
            return;
        } catch (Exception e) {
            req.setTransFlag(-1);
            req.setTransMsg("查询记录出错" + e.toString());
            return;
        }
    }

    /**
     * 　 Method：public String CreateTextByProc() Function: 输出参数:查询结果
     */
    public void CreateTextByProc(ResourcePool req, int dbsNo, String sqlStr,
                                 String sqlPara, String headCtrl, String bodyCtrl, String tailCtrl,
                                 String fldStr, String fldRow, boolean useTitle, String reportName,
                                 String funcCode) {
        if (!HasPrivate(req, funcCode)) {
            req.setTransMsg("没有执行权限!!!");
            req.setTransFlag(-1);
            return;
        }
        ConnectionFactory dbclass = req.getConnectionFactory(dbsNo);
        if (dbclass == null) {
            req.setTransFlag(-1);
            req.setTransMsg("获取数据连接失败");
            return;
        }
        if (sqlStr == null || sqlStr.equals(""))
            sqlStr = req.getSqlStr();
        String[] callPara = (sqlPara == null) ? null : sqlPara.split(",", -1);

        try {
            ByteArrayOutputStream baos;
            ArrayList<?> l = dbclass.doCall(sqlStr, callPara, 0, 0);
            if (l == null) {
                logger.debug("查询记录异常: " + dbclass.getMsg());
                req.setTransFlag(-1);
                req.setTransMsg(dbclass.getMsg());
                return;
            }
            if (l.size() == 0) {
                req.setTransFlag(-1);
                req.setTransMsg("没有满足条件的记录");
                return;
            }
            if (bodyCtrl == null || bodyCtrl.equals("")) {
                String[] colName = dbclass.getColName();
                for (int i = 0; i < colName.length; i++) {
                    if (i == 0)
                        bodyCtrl = colName[i];
                    else
                        bodyCtrl += "^" + colName[i];
                }
            }
            if (useTitle)
                baos = (ByteArrayOutputStream) Tools.getDefIO().CreateText(
                        headCtrl, bodyCtrl, tailCtrl, fldStr, fldRow, true, l);
            else
                baos = (ByteArrayOutputStream) Tools.getDefIO().CreateText(
                        headCtrl, bodyCtrl, tailCtrl, fldStr, fldRow, false, l);
            if (baos == null) {
                req.setTransFlag(-1);
                req.setTransMsg("生成文件失败");
                return;
            }
            HttpServletResponse resp = req.getHttpServletResponse();
            resp.setContentType("application/x-msdownload");
            if (reportName == null || reportName.equals(""))
                reportName = "" + System.currentTimeMillis();
            // reportName=java.net.URLEncoder.encode(reportName, "UTF-8");
            // reportName=new String(reportName.getBytes("GBK"), "ISO8859-1");
            reportName = new String(reportName.getBytes(), "ISO8859-1");
            resp.setHeader("Content-disposition", "attachment; filename="
                    + reportName + ".txt");
            resp.setContentLength(baos.size());
            baos.writeTo(resp.getOutputStream());
            baos.flush();
            baos.close();
            WriteOperLog(req, "成功", funcCode);
            return;
        } catch (Exception e) {
            req.setTransFlag(-1);
            req.setTransMsg("查询记录出错" + e.toString());
            return;
        }
    }

    public String CreateExcelByTemplate(ResourcePool req, String xmlCfg,
                                        String xlsTemplate, String outName, String sqlPara) {
        Map<String, String> varMap = new HashMap<String, String>();
        String[] callPara = sqlPara.split(",", -1);
        for (String v : callPara) {
            int idx = v.indexOf(":");
            if (idx < 0)
                continue;
            String value = v.substring(idx + 1);
            value = value.replaceAll("char\\(44\\)", ",");
            varMap.put(v.substring(0, idx), value);
        }
        try {
            IDefIO cf = Tools.getDefIO();
            // DefIO cf =new DefIO();
            String path = ResourcePool.getRootpath() + "/config/reportCfg/";
            ByteArrayOutputStream out = cf.CreateExcelByTemplate(req, path
                    + xlsTemplate, path + xmlCfg, varMap);

            if (out != null) {
                if (!cf.hasRecord()) {
                    return "生成失败";
                }
                String reportName = new String(outName.getBytes(), "ISO8859-1");
                HttpServletResponse response = req.getHttpServletResponse();
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition:inline;",
                        "attachment; filename=" + reportName + ".xls");
                response.setContentLength(out.size());
                ServletOutputStream out1 = response.getOutputStream();
                out.writeTo(out1);
                out.flush();
                out.close();
                out1.flush();
                out1.close();
                out1 = null;
                response.flushBuffer();
                return null;
            }
        } catch (Exception e) {
        }
        return "生成失败";
    }

    public static void setExchangeVar(HttpServletRequest request,
                                      String varName, Object value) {
        try {
            ResourcePool.getLoginSession(request).setSessionVar(varName, value);
        } catch (Exception e) {
        }
    }

    public static Object getExchangeVar(HttpServletRequest request,
                                        String varName) {
        try {
            return ResourcePool.getLoginSession(request).getSessionVar(varName);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 　 检查权限 Method：public boolean HasPrivate() 输出参数:是否成功
     */

    public boolean HasPrivate(ResourcePool req, String checkFunc) {
        String value = "";
        if (checkFunc == null || checkFunc.equals(""))
            return true;
        if (ResourcePool.getRunAs() < 2.0) {
            String InstID;
            String UserID;
            ConnectionFactory dbconn = req.getConnectionFactory();

            try {
                InstID = req.getUserVariable("InstID").toString();
                UserID = req.getUserVariable("UserID").toString();
            } catch (Exception e) {
                return true;
            }

            if (checkFunc == null || checkFunc.equals(""))
                return true;
            if (UserID == null || UserID.equals(""))
                return true;
            if (InstID.equals("0000") && UserID.equals("Admin"))
                return true;
            String sqlStr = "select min(Func_Code) Func_Code from ("
                    + "select 0 Func_Code from T_USER_PRIVATE where Private_Type='2' and Inst_No='"
                    + InstID
                    + "' and User_Code='"
                    + UserID
                    + "' and Private_Code='"
                    + checkFunc
                    + "' "
                    + "union "
                    + "select 0 Func_Cod from T_ROLE_PRIVATE a,T_USER_PRIVATE b where a.Role_Code=to_number(b.Private_Code) and b.Private_Type='1' and a.Private_Type='2' and b.Inst_No='"
                    + InstID
                    + "' and b.User_Code='"
                    + UserID
                    + "' and a.Private_Code='"
                    + checkFunc
                    + "' "
                    + "union "
                    + "select count(*) Func_Cod from T_SYS_FUNC where Func_code='"
                    + checkFunc + "' and IsCheck='1'" + ")";
            value = dbconn.doQueryString(sqlStr, null);
        } else {
            String UserID;
            String DomainID;
            ConnectionFactory dbconn = req.getConnectionFactory();
            try {
                UserID = req.getUserVariable("UserID").toString();
                DomainID = req.getUserVariable("DomainID").toString();
            } catch (Exception e) {
                return true;
            }

            if (checkFunc == null || checkFunc.equals(""))
                return true;
            if (UserID == null || UserID.equals("")
                    || UserID.equalsIgnoreCase("admin"))
                return true;
            String sqlStr = "select min(cnt) from (select 0 cnt "
                    + "from t_role_func f,t_user_role_set r,t_user u,t_domain d "
                    + "where f.roleid=r.roleid and r.userid=u.id and u.domainid=d.id and d.name='"
                    + DomainID + "' and u.loginno='" + UserID
                    + "' and f.funcid='" + checkFunc + "' "
                    + "union select count(*) cnt from T_FUNC where funcid='"
                    + checkFunc + "')";
            value = dbconn.doQueryString(sqlStr, null);
        }

        if (value == null || (value.equals("") || value.equals("0")))
            return true;
        else
            return false;
    }

    /**
     * 　 记录日志 Method：public boolean writeOperLog() 输出参数:是否成功
     */
    public void WriteOperLog(ResourcePool req, String infoStr, String LogFunc) {
        if (LogFunc == null || LogFunc.equals(""))
            return;
        if (ResourcePool.getRunAs() < 2.0) {
            String InstID;
            String UserID;
            ConnectionFactory dbconn = req.getConnectionFactory();
            try {
                InstID = req.getUserVariable("InstID").toString();
                UserID = req.getUserVariable("UserID").toString();
            } catch (Exception e) {
                return;
            }
            if (UserID == null || UserID.equals(""))
                return;
            if (LogFunc == null || LogFunc.equals(""))
                return;

            if (dbconn == null)
                dbconn = req.getConnectionFactory();
            try {
                String writeLogStr = "insert into T_MGT_TRANS_JOUR_Y "
                        + "select to_char(sysdate,'yyyymmdd hh24:mi:ss'),'"
                        + InstID + "','" + UserID + "','" + LogFunc + "','"
                        + infoStr + "' from T_SYS_FUNC where Func_Code='"
                        + LogFunc + "'";

                boolean flag = dbconn.executeUpdate(writeLogStr);

                if (flag == false) {
                    System.out.println("插入日志失败：" + dbconn.getMsg());
                    return;
                }

            } catch (Exception e) {
                System.out.println("写日志异常!");
                return;
            }
        }
        return;
    }

    /**
     * 销毁已登录的session
     *
     * @param Request
     * @param UserID
     * @param UserNo
     * @param version 1-eWAP1.0；2-eWAP2.0
     */
    public static void destroySession(Object Request, String UserID,
                                      String UserNo, int version) {
        if (version == 2) {
            ResourceManager localResourceManager = InitResource
                    .getResourceManager();
            LoginSession localLoginSession = localResourceManager
                    .getLogin(UserID);
            if ((localLoginSession != null) && (localLoginSession.se != null)) {
                try {
                    if ((localLoginSession.se.getAttribute("loginSession") != null)) {
                        logger.info("单点登录限制：" + UserNo + " version：" + version);
                        logger.info("旧Session：" + localLoginSession.se.getId());
                        localLoginSession.se
                                .removeAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
                        localLoginSession.se
                                .removeAttribute(Web.SESSION_ATTRIBUTE_DEBUG);
                        localLoginSession.se.removeAttribute(Web.SKIN_TYPE);
                        localLoginSession.se
                                .removeAttribute(Web.SESSION_ATTRIBUTE_DOMAIN);
                        localLoginSession.se
                                .removeAttribute(Web.SESSION_ATTRIBUTE_APPLICATION);
                        localLoginSession.se
                                .removeAttribute(Web.SESSION_ATTRIBUTE_ONLINEUSER);
                        if (Request instanceof HttpServletRequest) {
                            logger.info("新Session："
                                    + ((HttpServletRequest) Request)
                                    .getSession().getId());
                            if (!localLoginSession.se.getId().equals(
                                    ((HttpServletRequest) Request).getSession()
                                            .getId())) {
                                localLoginSession.se.invalidate();
                            }
                        } else {
                            localLoginSession.se.invalidate();
                        }
                    }
                } catch (IllegalStateException ie) {
                    logger.info("单点登录限制：" + UserNo + " version：" + version);
                    logger.info("旧Session：" + localLoginSession.se.getId());
                    if (Request instanceof HttpServletRequest) {
                        logger.info("新Session："
                                + ((HttpServletRequest) Request).getSession()
                                .getId());
                    }
                }
            }
        }
    }
}
