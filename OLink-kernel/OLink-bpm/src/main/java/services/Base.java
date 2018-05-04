// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   Base.java

package services;


public class Base
{

    private String msgStr;

    public Base()
    {
    }

    protected void setMsg(String msgStr, boolean logSign)
    {
        this.msgStr = msgStr;
        if(logSign)
            ShowMessage(msgStr);
    }

    protected void setMsg(String msgStr)
    {
        this.msgStr = msgStr;
    }

    public String getMsg()
    {
        return msgStr;
    }
    
    protected void  DbSetMsg(String dberrorStr)
    {
		String esub=dberrorStr.substring(dberrorStr.indexOf("ORA-"));
		this.msgStr="数据库操作错，错误码:"+esub.substring(0,9);
    }

    protected void ShowMessage(String msgStr)
    {
        System.out.println(msgStr);
    }
}
