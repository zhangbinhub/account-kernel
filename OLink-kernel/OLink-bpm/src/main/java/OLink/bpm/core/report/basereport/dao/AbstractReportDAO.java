package OLink.bpm.core.report.basereport.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.ValueObject;

public abstract class AbstractReportDAO {
	
	protected String dbTag = "Oracle: ";

	protected String schema = "";

	protected Connection connection;
	
	protected String dbType = ""; 

	public AbstractReportDAO(Connection conn) throws Exception {
		this.connection = conn;
	}

	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "."
					+ tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}

	
	public void create(ValueObject vo) throws Exception {

	}

	public ValueObject find(String id) throws Exception {
		return null;
	}

	public void remove(String pk) throws Exception {

	}

	public void update(ValueObject vo) throws Exception {

	}

}
