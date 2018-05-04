package OLink.bpm.core.report.query.action;

import java.util.Collection;
import java.util.HashSet;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.report.query.ejb.Query;
import OLink.bpm.core.report.query.ejb.QueryProcess;
import OLink.bpm.util.ProcessFactory;

public class QueryHelper extends BaseHelper<Query> {
	
	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public QueryHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(QueryProcess.class));
	}

	/*public String moduleid;
	
	public String applicationid;
  
	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String appid) {
		this.applicationid = appid;
	}

	public String getModuleid() {
		return moduleid;
	}

	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}*/

	public Collection<Query> get_queryStringList()throws Exception
	{
		QueryProcess process = (QueryProcess) ProcessFactory
		.createProcess(QueryProcess.class);
		Collection<Query> rs = process.get_queryStringList(getModuleid(),getApplicationid());
		return rs;
	}
	
	public  Collection<String> getParameters(String queryid) throws Exception {

		QueryProcess process = (QueryProcess) ProcessFactory
		.createProcess(QueryProcess.class);
		Query vo=(Query)process.doView(queryid);
		if (vo==null||vo.getQueryString() == null || vo.getQueryString().length() == 0)
			return null;
		String sql=vo.getQueryString();
		Collection<String> list = new HashSet<String>();
		while (sql.indexOf("{") >= 0) {
			int i = sql.indexOf("{", 0);
			int j = sql.indexOf("}", i);
			String paramName = sql.substring(i + 1, j);
			if(!list.contains(paramName))
			list.add(paramName);
			sql = sql.substring(j + 1, sql.length());
		}
		return list;
	}
	
	public  Collection<String> getParametersBySQL(String  sql) throws Exception {

		Collection<String> list = new HashSet<String>();
		while (sql.indexOf("{") >= 0) {
			int i = sql.indexOf("{", 0);
			int j = sql.indexOf("}", i);
			String paramName = sql.substring(i + 1, j);
			if(!list.contains(paramName))
			list.add(paramName);
			sql = sql.substring(j + 1, sql.length());
		}
		return list;
	}
	
}
