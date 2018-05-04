package OLink.core.protection;

class LicenseKey {
    private String type;
    private String organization;
    private String version;

    String getVersion() {
        return this.version;
    }

    String getType() {
        if (!KeyPool.getKey(3).equalsIgnoreCase("")) return "A.试用版";
        return this.type;
    }

    String getOrganization() {
        return this.organization;
    }

    LicenseKey() {
        this.type = "G.企业版";
        this.organization = "正版用户";
        this.version = "OLink2.0";
    }

    String toCopyright() {
        String v = KeyPool.getKey(3);
        if (!v.equalsIgnoreCase(""))
            return "<font color='red'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请使用正版软件，有效期至" + v + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + KeyPool.getKey(6) + "</font>";
        return "";
    }
}