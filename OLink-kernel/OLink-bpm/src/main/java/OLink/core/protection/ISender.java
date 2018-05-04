package OLink.core.protection;

public abstract interface ISender extends IMessage
{
    public abstract int sendMessage(String paramString1, String paramString2)
            throws Exception;

    public abstract int sendMessage(String paramString1, String paramString2, String paramString3)
            throws Exception;

    public abstract int battchSendMessage(String[] paramArrayOfString, String paramString)
            throws Exception;

    public abstract int battchSendMessage(String[] paramArrayOfString, String paramString1, String paramString2)
            throws Exception;
}