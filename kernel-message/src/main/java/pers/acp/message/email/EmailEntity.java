package pers.acp.message.email;

import pers.acp.tools.common.CommonTools;

import java.util.List;
import java.util.Map;

public class EmailEntity {

    private String senderAddress;

    private String userName;

    private String password;

    private String mailHost;

    private int mailPort = 465;

    private String mailTransportProtocol = "smtp";

    private boolean mailSmtpAuth = true;

    private boolean deBug = false;

    private boolean isSSL = true;

    private List<String> RecipientAddresses = null;

    private List<String> RecipientCCAddresses = null;

    private List<String> RecipientBCCAddresses = null;

    private String mailSubject;

    private String content = "";

    private Map<String, String> images;

    private List<String> attaches;

    public String getSenderAddress() {
        return senderAddress;
    }

    /**
     * 设置发送者邮箱地址
     *
     * @param senderAddress 发送者地址
     */
    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    /**
     * 设置邮件服务器登录名，默认从发送者邮箱地址截取
     *
     * @param userName 登录名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取邮件服务器登录名，默认从发送者邮箱地址截取
     *
     * @return 获取登录名
     */
    public String getUserName() {
        if (CommonTools.isNullStr(userName)) {
            if (!CommonTools.isNullStr(senderAddress)) {
                String[] senderinfo = senderAddress.split("@");
                if (senderinfo.length == 2) {
                    userName = senderinfo[0];
                } else {
                    userName = "";
                }
            } else {
                userName = "";
            }
        }
        return userName;
    }

    public String getPassword() {
        return password;
    }

    /**
     * 设置邮件服务器登录密码
     *
     * @param password 登录密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 设置邮件服务器主机名，默认从发送者邮箱地址截取
     *
     * @param mailHost 邮件服务器主机名
     */
    public void setMailHost(String mailHost) {
        this.mailHost = mailHost;
    }

    /**
     * 获取邮件服务器主机名，默认从发送者邮箱地址截取
     *
     * @return 邮件服务器主机名
     */
    public String getMailHost() {
        if (CommonTools.isNullStr(mailHost)) {
            if (!CommonTools.isNullStr(senderAddress)) {
                String[] senderinfo = senderAddress.split("@");
                if (senderinfo.length == 2) {
                    mailHost = "smtp." + senderinfo[1];
                } else {
                    mailHost = "";
                }
            } else {
                mailHost = "";
            }
        }
        return mailHost;
    }

    public int getMailPort() {
        return mailPort;
    }

    /**
     * 设置邮件服务器端口
     *
     * @param mailPort 邮件服务器端口
     */
    public void setMailPort(int mailPort) {
        this.mailPort = mailPort;
    }

    public String getMailTransportProtocol() {
        return mailTransportProtocol;
    }

    /**
     * 设置邮件协议，默认"smpt"
     *
     * @param mailTransportProtocol 邮件协议
     */
    public void setMailTransportProtocol(String mailTransportProtocol) {
        this.mailTransportProtocol = mailTransportProtocol;
    }

    public boolean isMailSmtpAuth() {
        return mailSmtpAuth;
    }

    /**
     * 是否进行smtp协议认证，默认true
     *
     * @param mailSmtpAuth 是否进行smtp协议认证
     */
    public void setMailSmtpAuth(boolean mailSmtpAuth) {
        this.mailSmtpAuth = mailSmtpAuth;
    }

    public boolean isSSL() {
        return isSSL;
    }

    /**
     * 是否采用https安全链接
     *
     * @param isSSL 是否采用https链接
     */
    public void setSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }

    public boolean isDeBug() {
        return deBug;
    }

    /**
     * 是否输出调试信息
     *
     * @param deBug 是否输出调试信息
     */
    public void setDeBug(boolean deBug) {
        this.deBug = deBug;
    }

    public List<String> getRecipientAddresses() {
        return RecipientAddresses;
    }

    /**
     * 设置邮件接收者地址
     *
     * @param recipientAddresses 邮件接收者地址
     */
    public void setRecipientAddresses(List<String> recipientAddresses) {
        RecipientAddresses = recipientAddresses;
    }

    public List<String> getRecipientCCAddresses() {
        return RecipientCCAddresses;
    }

    /**
     * 设置邮件抄送人地址，为null则不抄送，默认为null
     *
     * @param recipientCCAddresses 抄送人地址
     */
    public void setRecipientCCAddresses(List<String> recipientCCAddresses) {
        RecipientCCAddresses = recipientCCAddresses;
    }

    public List<String> getRecipientBCCAddresses() {
        return RecipientBCCAddresses;
    }

    /**
     * 设置邮件密送人地址，为null则不密送，默认为null
     *
     * @param recipientBCCAddresses 密送人地址
     */
    public void setRecipientBCCAddresses(List<String> recipientBCCAddresses) {
        RecipientBCCAddresses = recipientBCCAddresses;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    /**
     * 设置邮件标题
     *
     * @param mailSubject 标题
     */
    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getContent() {
        return content;
    }

    /**
     * 设置邮件正文文本
     *
     * @param content 正文
     */
    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getImages() {
        return images;
    }

    /**
     * 设置邮件图片 Map:ContentID=>图片路径（绝对路径或相对于webroot以"/"开头的路径）
     *
     * @param images 图片
     */
    public void setImages(Map<String, String> images) {
        this.images = images;
    }

    public List<String> getAttaches() {
        return attaches;
    }

    /**
     * 附件文件路径：绝对路径或相对于webroot以"/"开头的路径
     *
     * @param attaches 附件
     */
    public void setAttaches(List<String> attaches) {
        this.attaches = attaches;
    }

}
