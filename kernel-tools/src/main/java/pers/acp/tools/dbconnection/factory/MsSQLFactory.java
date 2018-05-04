package pers.acp.tools.dbconnection.factory;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zhang on 2016/7/21.
 * MicroSoftSQL操作工厂
 */
public class MsSQLFactory extends BaseDBFactory {

    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public ResultSet doQueryPage(Connection connection, Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray, long totlerecord) throws SQLException {
        String sql = "" + "select " + sqlArray[0] + " from " + sqlArray[1] + " " + sqlArray[2] + " " + sqlArray[3];
        String strSQL = "select top " + maxCount + " * from (" + sql + ") q_table where q_table." + pKey + " not in (select top " + ((currPage - 1) * maxCount) + " q_table_t." + pKey + " from (" + sql + ") q_table_t)";
        log.debug("sql=" + strSQL);
        PreparedStatement pstmt = connection.prepareStatement(strSQL);
        if (param != null) {
            for (int i = 0; i < param.length * 2; i++) {
                pstmt.setObject(i + 1, param[i % param.length]);
            }
        }
        return pstmt.executeQuery();
    }

}
