package OLink.core.protection;

public abstract interface IReceiver extends IMessage
{
    public abstract String receiveMessage()
            throws Exception;
}