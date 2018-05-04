package OLink.bpm.core.dynaform.dts.el2xml;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import OLink.bpm.base.dao.PersistenceUtils;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.hibernate.Session;

public abstract class ElementToXml {
	Connection conn;

	public ElementToXml() throws Exception {
		Session session = PersistenceUtils.currentSession();
		conn = session.connection();
	}

	protected Collection<?> getElementsBySQL(String sql, Class<?> type) {
		QueryRunner qRunner = new QueryRunner();

		try {
			BeanListHandler bh = new BeanListHandler(type,
					new BasicRowProcessor(new BeanProcessor() {
						protected int[] mapColumnsToProperties(
								ResultSetMetaData rsmd,
								PropertyDescriptor[] props) throws SQLException {

							int cols = rsmd.getColumnCount();
							int columnToProperty[] = new int[cols + 1];
							Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

							for (int col = 1; col <= cols; col++) {
								String columnName = rsmd.getColumnLabel(col);
								for (int i = 0; i < props.length; i++) {

									if (columnName.equalsIgnoreCase(props[i]
											.getName())) {
										columnToProperty[col] = i;
										break;
									}
								}
							}
							return columnToProperty;
						}
					}));
			Collection<?> list = (Collection<?>) qRunner.query(conn, sql, bh);
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
