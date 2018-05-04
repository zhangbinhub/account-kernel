package OLink.bpm.version.transfer;

import java.sql.Connection;

import OLink.bpm.base.dao.PersistenceUtils;
import org.hibernate.Session;

public abstract class BaseTransfer implements ITransfer {
	private Connection conn;

	public Connection getConnection() {
		try {
			Session session = PersistenceUtils.currentSession();
			if (conn == null || conn.isClosed()) {
				conn = session.connection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public void to2_4() {}
	
	public void to2_5() {}
	
	public void to2_5SP4() {}

	public void to2_6() throws Exception{}
	
}
