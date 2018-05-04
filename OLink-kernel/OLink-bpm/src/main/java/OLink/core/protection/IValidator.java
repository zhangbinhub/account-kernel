package OLink.core.protection;

public abstract interface IValidator
{
    public abstract boolean validate()
            throws Exception;

    public abstract String getDomainid();

    public abstract String getApplicationid();
}