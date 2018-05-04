package OLink.bpm.util.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class JDBCUtil {
	
	public static Double queryColumnDoubleValue(Connection conn, String sql,
			String columnName) throws Exception {
		Double rtn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rtn = new Double(rs.getDouble(columnName));
			}
		}
		finally {
			rs.close();
			stmt.close();
		}
		return rtn;
	}
	
	public static Integer queryTotalLine(Connection conn,String sql)throws Exception{
		
		int totalline=0;
		//int from = sql.toLowerCase().indexOf("from");
		//int order = sql.toLowerCase().indexOf("order by");

		//String newsql = (order > 0) ? "select count(*) totalline"
				//+ sql.substring(from, order) : "select count(*) totalline "
				//+ sql.substring(from);
		
				Statement stmt = null;
				ResultSet rs = null;
				try {
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						totalline =rs.getInt("totalline");
					}
				}
				finally {
					rs.close();
					stmt.close();
				}
				
		return  Integer.valueOf(totalline);
	}
}
