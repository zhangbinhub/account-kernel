package OLink.bpm.core.report.tablecolumn.action;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import OLink.bpm.core.report.query.ejb.QueryProcess;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.report.query.ejb.Parameter;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumnProcess;
import OLink.bpm.core.report.query.ejb.Query;
import OLink.bpm.util.ProcessFactory;

public class TableColumnHelper {

	private String _reportConfigid;

	private String _type;

	private String _queryid;

	public String get_queryid() {
		return this._queryid;
	}

	public void set_queryid(String _queryid) {
		this._queryid = _queryid;
	}

	public String get_reportConfigid() {
		return _reportConfigid;
	}

	public void set_reportConfigid(String configid) {
		_reportConfigid = configid;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	// 根据queryString从数据库中取得result的column name的集合
	public Collection<String> get_tableColumnFromDateBase(Query query) throws Exception {
		Collection<String> coll = new HashSet<String>();
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet tmprs = null;

		try {
			DataSource dts = query.getDataSource();
			DriverManager.registerDriver((Driver) Class.forName(
					dts.getDriverClass()).newInstance());
			conn = DriverManager.getConnection(dts.getUrl(), dts.getUsername(),
					dts.getPassword());
			Map<String, String> defaultParams = getDefaultParams(query.getParamters());
			String sql = parseJaperSQL(defaultParams, query.getQueryString());

			String tmpsql = "select * from (" + sql + ") where 1<>1";
			ps = conn.prepareStatement(tmpsql);
			tmprs = ps.executeQuery();
			ResultSetMetaData dma = tmprs.getMetaData();
			for (int i = 1; i <= dma.getColumnCount(); i++) {
				coll.add(dma.getColumnName(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceUtils.closeResultSet(tmprs);
			PersistenceUtils.closeStatement(stmt);
			PersistenceUtils.closeStatement(ps);
			
			PersistenceUtils.closeConnection(conn);
		}

		return coll;
	}

	// public Map getParamsSearchForm(Query query)
	// {
	// Map map=new HashMap();
	// Form form=query.getSearchForm();
	// if(form!=null){
	// Collection Fields=form.getFields();
	//		
	// for (Iterator iter = Fields.iterator(); iter.hasNext();) {
	// FormField em = (FormField) iter.next();
	// if(em.getFieldtype().equals(Item.VALUE_TYPE_NUMBER))
	// map.put(em.getName(),Integer.valueOf(8));
	// if(em.getFieldtype().equals(Item.VALUE_TYPE_VARCHAR))
	// map.put(em.getName(),"00");
	// }
	// }
	// return map;
	// }

	public Map<String, String> getDefaultParams(Collection<Parameter> params) {

		Map<String, String> map = new HashMap<String, String>();
		if (params == null || params.size() == 0)
			return map;

		for (Iterator<Parameter> iter = params.iterator(); iter.hasNext();) {
			Parameter em = iter.next();
			map.put(em.getName(), em.getDefaultValue());
		}
		return map;
	}

	private String parseJaperSQL(Map<String, String> params, String jasperSql) {
		if (jasperSql == null || jasperSql.length() == 0)
			return null;
		while (jasperSql.indexOf("$P") >= 0) {
			int i = jasperSql.indexOf("$P");
			int j = jasperSql.indexOf("{", i);
			int k = jasperSql.indexOf("}", j + 1);
			String paramName = jasperSql.substring(j + 1, k);
			String tmpStr = jasperSql.substring(0, i);
			String tmpStr1 = jasperSql.substring(k + 1, jasperSql.length());
			Object obj = params.get(paramName);
			if (obj instanceof Integer) {
				jasperSql = tmpStr + ((Integer) obj).intValue() + tmpStr1;
			}
			// if (obj instanceof Double) {
			// jasperSql=tmpStr+((Double)obj).doubleValue()+tmpStr1;
			// }
			if (obj instanceof String) {
				jasperSql = tmpStr + "'" + obj + "'" + tmpStr1;
			}

		}
		return jasperSql;
	}

	public Collection<TableColumn> get_tableColumnByQuery() throws Exception // 概要queryString从数据库中取得result的column的Name生成TableColumn集合
	{
		Collection<String> coll = null;
		Map<String, TableColumn> rs = new TreeMap<String, TableColumn>();
		if (get_queryid() != null && get_queryid().trim().length() > 0) {
			QueryProcess qp = (QueryProcess) (ProcessFactory
					.createProcess(QueryProcess.class));
			Query query = (Query) qp.doView(get_queryid());
			coll = get_tableColumnFromDateBase(query);
			for (Iterator<String> iter = coll.iterator(); iter.hasNext();) {
				String name = iter.next();
				TableColumn em = new TableColumn();
				em.setName(name);
				rs.put(name, em);
			}
		}
		return rs.values();
	}

	// 根据queryString取到result的column的name和之前已保存的TableColumn生成TableColumn集合
	public Collection<TableColumn> get_tableColumn(String _reportConfigid, String _type,
			String _query, String application) throws Exception {

		TableColumnProcess tp = (TableColumnProcess) (ProcessFactory
				.createProcess(TableColumnProcess.class));
		DataPackage<TableColumn> data = tp.getFieldsByReportConfigAndType(_reportConfigid,
				_type, application);

		QueryProcess qp = (QueryProcess) (ProcessFactory
				.createProcess(QueryProcess.class));
		//ReportConfigProcess rp = (ReportConfigProcess) (ProcessFactory.createProcess(ReportConfigProcess.class));
		Query query = (Query) qp.doView(_query);

		Collection<String> tablecoll = get_tableColumnFromDateBase(query);
		Map<String, TableColumn> rs = new TreeMap<String, TableColumn>();
		if (data != null && data.datas != null && data.datas.size() > 0) {
			for (Iterator<TableColumn> iter = data.datas.iterator(); iter.hasNext();) {
				TableColumn tc = iter.next();
				rs.put(tc.getName(), tc);
			}

		}
		if (tablecoll != null) {
			if (data != null && data.datas != null) {
				for (Iterator<TableColumn> iterator = data.datas.iterator(); iterator
						.hasNext();) {
					TableColumn tc = iterator.next();
					if (tablecoll.contains(tc.getName()))
						tablecoll.remove(tc.getName());
				}
			}
			for (Iterator<String> iter = tablecoll.iterator(); iter.hasNext();) {
				String name = iter.next();
				TableColumn em = new TableColumn();
				em.setName(name);
				rs.put(name, em);
			}
		}
		return rs.values();
	}

	public Collection<String> get_calculateType() {
		Map<String, String> types = new TreeMap<String, String>();
		types.put("", "");
		types.put(TableColumn.CALCULATE_TYPE_SUM,
				TableColumn.CALCULATE_TYPE_SUM);
		types.put(TableColumn.CALCULATE_TYPE_AVG,
				TableColumn.CALCULATE_TYPE_AVG);

		types.put(TableColumn.CALCULATE_TYPE_COUNT,
				TableColumn.CALCULATE_TYPE_COUNT);
		types.put(TableColumn.CALCULATE_TYPE_HIGHEST,
				TableColumn.CALCULATE_TYPE_HIGHEST);

		types.put(TableColumn.CALCULATE_TYPE_LOWEST,
				TableColumn.CALCULATE_TYPE_LOWEST);
		types.put(TableColumn.CALCULATE_TYPE_NOTHING,
				TableColumn.CALCULATE_TYPE_NOTHING);

		types.put(TableColumn.CALCULATE_TYPE_SYSTEM,
				TableColumn.CALCULATE_TYPE_SYSTEM);

		return types.values();
	}

	/*
	 * public String get_reportConfigid() { return _reportConfigid; }
	 * 
	 * public void set_reportConfigid(String configid) { _reportConfigid =
	 * configid; }
	 * 
	 * public String get_type() { return _type; }
	 * 
	 * public void set_type(String _type) { this._type = _type; }
	 */
}
