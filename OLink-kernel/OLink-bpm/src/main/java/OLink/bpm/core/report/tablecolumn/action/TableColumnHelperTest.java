package OLink.bpm.core.report.tablecolumn.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.report.query.ejb.ParameterProcess;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.report.query.ejb.Parameter;
import OLink.bpm.core.report.query.ejb.Query;
import OLink.bpm.core.report.query.ejb.QueryProcess;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfigProcess;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumnProcess;
import OLink.bpm.util.ProcessFactory;
import junit.framework.TestCase;

public class TableColumnHelperTest extends TestCase {

	TableColumnHelper helper;

	Query query;

	Parameter p1;

	Parameter p2;

	HashSet<Parameter> pars;

	DataSource ds;

	ParameterProcess pp;

	DataSourceProcess dp;

	ReportConfigProcess rp;

	TableColumnProcess tp;

	QueryProcess qp;

	ReportConfig rc;

	protected void setUp() throws Exception {
		super.setUp();
		helper = new TableColumnHelper();
		query = new Query();

		ds = new DataSource();
		ds.setName("MyDataSource");
		ds.setDriverClass("oracle.jdbc.driver.OracleDriver");
		ds.setUrl("jdbc:oracle:thin:@192.168.0.100:1521:XE");
		ds.setUsername("wpigb4");
		ds.setPassword("helloworld");

		query
				.setQueryString("select * from t_user where  loginno like $P{loginno} and name like $P{name}");
		// query.setId(Tools.getSequence());
		query.setDataSource(ds);
		p1 = new Parameter();
		p2 = new Parameter();
		p1.setName("name");
		p1.setDefaultValue("peng");
		p2.setName("loginno");
		p2.setDefaultValue("dlp");
		p1.setQuery(query);
		p2.setQuery(query);
		pars = new HashSet<Parameter>();
		pars.add(p1);
		pars.add(p2);
		query.setParamters(pars);

		pp = (ParameterProcess) (ProcessFactory
				.createProcess(ParameterProcess.class));
		dp = (DataSourceProcess) (ProcessFactory
				.createProcess(DataSourceProcess.class));

		rp = (ReportConfigProcess) (ProcessFactory
				.createProcess(ReportConfigProcess.class));
		tp = (TableColumnProcess) (ProcessFactory
				.createProcess(TableColumnProcess.class));
		qp = (QueryProcess) (ProcessFactory.createProcess(QueryProcess.class));
		rc = new ReportConfig();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test_all(String application) throws Exception {
		Get_tableColumnFromDateBase();
		getDefaultParams();

		Get_tableColumnByQuery();
		Get_tableColumn(application);
		deleteRubbishData();
	}

	/*
	 * Test method for
	 * 'TableColumnHelper.get_tableColumnFromDateBase(Query)'
	 */
	public void Get_tableColumnFromDateBase() throws Exception {

		Collection<String> cols = helper.get_tableColumnFromDateBase(query);

		assertTrue(cols.contains("NAME") && cols.contains("LOGINNO"));

	}

	/*
	 * Test method for
	 * 'TableColumnHelper.getDefaultParams(Collection)'
	 */
	public void getDefaultParams() {

		Map<String, String> map = helper.getDefaultParams(query.getParamters());
		assertEquals(map.size(), query.getParamters().size());

	}

	/*
	 * Test method for
	 * 'TableColumnHelper.get_tableColumnByQuery()'
	 */
	public void Get_tableColumnByQuery() throws Exception {

		p1.setQuery(null);
		p2.setQuery(null);
		pp.doCreate(p1);
		pp.doCreate(p2);

		dp.doCreate(ds);

		query.setParamters(null);

		qp.doCreate(query);

		p1.setQuery(query);
		p2.setQuery(query);
		query.setParamters(pars);
		pp.doUpdate(p1);
		pp.doUpdate(p2);
		qp.doUpdate(query);

		helper.set_queryid(query.getId());
		Collection<TableColumn> cols = helper.get_tableColumnByQuery();

		int i = 0;
		for (Iterator<TableColumn> iter = cols.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (em.getName().equals("NAME") || em.getName().equals("LOGINNO"))
				i++;
		}
		assertEquals(i, 2);

	}

	/*
	 * Test method for
	 * 'TableColumnHelper.get_tableColumn(String,
	 * String, String)'
	 */
	public void Get_tableColumn(String application) throws Exception {

		rc.setQuery(query);
		rp.doCreate(rc);
		TableColumn c1 = new TableColumn();
		c1.setName("NAME");
		c1.setDescription("username");
		c1.setReportConfig(rc);
		c1.setType(ReportConfig.Column_Type_Detail);

		TableColumn c2 = new TableColumn();
		c2.setName("LOGINNO");
		c2.setDescription("LoginNo");
		c2.setReportConfig(rc);
		c2.setType(ReportConfig.Column_Type_Detail);

		tp.doCreate(c1);
		tp.doCreate(c2);

		int i = 0;
		Collection<TableColumn> cols = helper.get_tableColumn(rc.getId(),
				ReportConfig.Column_Type_Detail, query.getId(), application);
		for (Iterator<TableColumn> iter = cols.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (em.getName().equals("NAME")
					&& em.getDescription().equals("username"))
				i++;
			if (em.getName().equals("LOGINNO")
					&& em.getDescription().equals("LoginNo"))
				i++;

		}
		assertEquals(i, 2);
	}

	public void deleteRubbishData() throws Exception {
		rp.doRemove(rc.getId());
		qp.doRemove(query.getId());
		pp.doRemove(p1.getId());
		pp.doRemove(p2.getId());
		dp.doRemove(ds.getId());

	}

}
