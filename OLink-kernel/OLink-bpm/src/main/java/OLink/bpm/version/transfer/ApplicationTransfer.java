package OLink.bpm.version.transfer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.util.StringUtil;

public class ApplicationTransfer extends BaseTransfer {
	
	private final static Logger LOG = Logger.getLogger(ApplicationTransfer.class);

	/**
	 * 升级到2.4版本，Application数据源定制迁移到DataSource模块
	 */
	public void to2_4() {
		Connection conn = getConnection();

		try {
			DataSourceProcess dsProcess = (DataSourceProcess) ProcessFactory.createProcess(DataSourceProcess.class);
			ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);

			QueryRunner qRunner = new QueryRunner();
			String sql = "SELECT id, datasourceid, dbdriver, dbtype, dburl, dbpassword, dbpoolsize, dbtimeout, dbusername FROM T_APPLICATION WHERE datasourceid is null";
			List<?> dataList = (List<?>) qRunner.query(conn, sql, new MapListHandler());
			boolean transfered = true;
			LOG.info("---->begin transfer datasource data to t_datasource in t_application...");
			for (Iterator<?> iterator = dataList.iterator(); iterator.hasNext();) {
				try {
					Map<?, ?> data = (Map<?, ?>) iterator.next();

					String appid = (String) data.get("id");
					String datasourceid = (String) data.get("datasourceid");
					if (StringUtil.isBlank(datasourceid)) {
						transfered = false;
						String dbdriver = (String) data.get("dbdriver");
						String dbtype = (String) data.get("dbtype");
						String dburl = (String) data.get("dburl");
						String dbpassword = (String) data.get("dbpassword");
						String dbpoolsize = (String) data.get("dbpoolsize");
						String dbtimeout = (String) data.get("dbtimeout");
						String dbusername = (String) data.get("dbusername");

						ApplicationVO app = (ApplicationVO) appProcess.doView(appid);

						// 创建数据源
						DataSource ds = new DataSource();
						ds.setName(app.getName() + "_DS");
						ds.setDriverClass(dbdriver);
						ds.setDbType(DataSource.getDbTypeByName(dbtype));
						ds.setUrl(dburl);
						ds.setPassword(dbpassword);
						ds.setPoolsize(dbpoolsize);
						ds.setTimeout(dbtimeout);
						ds.setUsername(dbusername);
						ds.setApplicationid(appid);

						dsProcess.doCreate(ds);
						LOG.info("Create DataSource->" + ds.getName());

						// 设置数据源与软件关联
						app.setDatasourceid(ds.getId());
						appProcess.doUpdate(app);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(transfered)
				LOG.info("---->no data to transfer!");
			else
				LOG.info("---->transfer data successfuly!");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				PersistenceUtils.closeSessionAndConnection();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ApplicationTransfer().to2_4();
	}

}
