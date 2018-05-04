package OLink.core.protection;

public class Validator
        implements IValidator
{
    private String domainid;
    private String applicationid;

    public Validator(String domainid, String applicationid)
    {
        this.domainid = domainid;
        this.applicationid = applicationid;
    }

    public void setDomainid(String domainid) {
        this.domainid = domainid;
    }

    public void setApplicationid(String applicationid) {
        this.applicationid = applicationid;
    }

    public boolean validate() throws Exception {
        return true;
    }

    public String getDomainid() {
        return this.domainid;
    }

    public String getApplicationid() {
        return this.applicationid;
    }
}