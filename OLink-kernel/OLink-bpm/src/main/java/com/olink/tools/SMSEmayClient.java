package com.olink.tools;

import cn.emay.sdk.client.api.Client;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;

/***
 * 亿美软通短信平台
 *
 * @author Lin Rui
 */
public class SMSEmayClient {
    private int iResult = 0;

    /**
     * 供应商账号、密码、下行对象
     *
     * @param acc
     * @param pwd
     * @param vo
     * @return
     */

    public int SMSSend(String acc, String pwd, SubmitMessageVO vo) {
        try {
            Client myClient = new Client(acc, pwd);
            int myReg = myClient.registEx(pwd);
            int iRet = myClient.sendSMS(new String[]{vo.getReceiver()}, vo.getTitle() + ":" + vo.getContent(), 3);
            iResult = iRet;
            return iRet;
        } catch (Exception e) {
            iResult = -1;
            e.printStackTrace();
        }
        return iResult;


    }

    /**
     * @param acc
     * @param pwd
     * @param recv
     * @param strTitle
     * @param strContent
     * @return
     */

    public int SMSSend(String acc, String pwd, String recv, String strTitle, String strContent) {
        try {

            Client myClient = new Client(acc, pwd);
            int myReg = myClient.registEx(pwd);
            int iRet = myClient.sendSMS(new String[]{recv}, strTitle + ":" + strContent, 3);
            iResult = iRet;
            return iRet;
        } catch (Exception e) {
            iResult = -1;
            e.printStackTrace();
        }
        return iResult;

    }

    /***
     * 获取系统剩余短信条数
     *
     * @param acc
     * @param pwd
     * @return
     */

    public String getSystemLeftSMSNumber(String acc, String pwd) {
        try {
            Client myClient = new Client(acc, pwd);
            int myReg = myClient.registEx(pwd);
            double dBalance = myClient.getBalance();
            double perMoney = myClient.getEachFee();
            double dNumber = dBalance / perMoney;
            System.out.println("短信发送结果:剩余短信-" + dNumber + "条；" + "当前单价：" + perMoney);
            return Integer.toString((int) dNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";

    }

}
