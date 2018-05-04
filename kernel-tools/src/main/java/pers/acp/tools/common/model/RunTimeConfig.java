package pers.acp.tools.common.model;

import pers.acp.tools.dbconnection.annotation.ADBTable;
import pers.acp.tools.dbconnection.annotation.ADBTableField;
import pers.acp.tools.dbconnection.annotation.ADBTablePrimaryKey;
import pers.acp.tools.dbconnection.entity.DBTable;
import pers.acp.tools.dbconnection.entity.DBTableFieldType;

/**
 * Created by zhangbin on 2016/9/8.
 * 系统参数
 */
@ADBTable(tablename = "T_RuntimeConfig")
public class RunTimeConfig extends DBTable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConfname() {
        return confname;
    }

    public void setConfname(String confname) {
        this.confname = confname;
    }

    public String getConfvalue() {
        return confvalue;
    }

    public void setConfvalue(String confvalue) {
        this.confvalue = confvalue;
    }

    public String getConfdes() {
        return confdes;
    }

    public void setConfdes(String confdes) {
        this.confdes = confdes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @ADBTablePrimaryKey(name = "id")
    private String id;

    @ADBTableField(name = "confname", fieldType = DBTableFieldType.String, allowNull = false)
    private String confname;

    @ADBTableField(name = "confvalue", fieldType = DBTableFieldType.String)
    private String confvalue;

    @ADBTableField(name = "confdes", fieldType = DBTableFieldType.String)
    private String confdes;

    @ADBTableField(name = "status", fieldType = DBTableFieldType.Integer, allowNull = false)
    private int status;

    @ADBTableField(name = "type", fieldType = DBTableFieldType.Integer, allowNull = false)
    private int type;
}
