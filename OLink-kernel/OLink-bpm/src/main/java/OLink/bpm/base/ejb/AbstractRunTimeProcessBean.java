package OLink.bpm.base.ejb;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import org.apache.commons.lang.StringUtils;

import com.jamonapi.proxy.MonProxyFactory;
import eWAP.core.ResourcePool;
import eWAP.core.Tools;

/**
 * The base abstract run time process bean.
 */
public abstract class AbstractRunTimeProcessBean<E> implements IRunTimeProcess<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7120284978893104541L;

	/**
	 * The application id
	 */
	private String applicationId;

	/**
	 * The data sources.
	 */
	private static HashMap<String, DataSource> dataSources = new HashMap<String, DataSource>();

	/**
	 * 单态时存在问题
	 */
	public static final ThreadLocal<Integer> transactionSignal = new ThreadLocal<Integer>();

	/**
	 * The constructor with application id.
	 * 
	 * @param applicationId
	 */
	public AbstractRunTimeProcessBean(String applicationId) {
		this.applicationId = applicationId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#doView(java.lang.String)
	 */
	public ValueObject doView(String id) throws Exception {
		return getDAO().find(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#doCreate(ValueObject)
	 */
	public void doCreate(ValueObject vo) throws Exception {
		try {
			if (vo.getId() == null || vo.getId().trim().length() == 0) {
				vo.setId(Tools.getSequence());
			}

			if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
				vo.setSortId(Tools.getTimeSequence());
			}

			beginTransaction();
			getDAO().create(vo);
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#doRemove(java.lang.String)
	 */
	public void doRemove(String pk) throws Exception {
		try {
			beginTransaction();
			getDAO().remove(pk);
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#doUpdate(ValueObject)
	 */
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			beginTransaction();
			getDAO().update(vo);
			commitTransaction();
		} catch (Exception e) {
			rollbackTransaction();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#doQuery(ParamsTable,
	 *      WebUser)
	 */
	public DataPackage<E> doQuery(ParamsTable params, WebUser user) throws Exception {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#beginTransaction()
	 */
	public void beginTransaction() throws Exception {

		int signal = getTransactionSignal();

		if (signal == 0)
			getConnection().setAutoCommit(false);

		signal++;

		setTransactionSignal(signal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#commitTransaction()
	 */
	public void commitTransaction() throws Exception {
		int signal = getTransactionSignal();
		signal--;

		if (signal == 0) {
			getConnection().commit();
			getConnection().setAutoCommit(true);
		}

		setTransactionSignal(signal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#rollbackTransaction()
	 */
	public void rollbackTransaction() throws Exception {
		int signal = getTransactionSignal();
		signal--;

		if (signal == 0) {
			getConnection().rollback();
			getConnection().setAutoCommit(true);
		}

		setTransactionSignal(signal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunTimeProcess#closeConnection()
	 */
	public void closeConnection() throws Exception {
		if (getConnection() != null) {
			if (!getConnection().isClosed())
				getConnection().close();

			PersistenceUtils.runtimeDBConn.set(null);
		}
	}

	/**
	 * Get the relate Dao
	 * 
	 * @return The relate Dao.
	 * @throws Exception
	 */
	protected abstract IRuntimeDAO getDAO() throws Exception;

	/**
	 * Get the application id.
	 * 
	 * @return
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * @param applicationId
	 *            the applicationId to set
	 */
	protected void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * The data source.
	 * 
	 * @return The data source.
	 * @throws Exception
	 */
	protected DataSource getDataSource() throws Exception {

		DataSource dataSource = (getApplicationId() != null) ? dataSources.get(getApplicationId()) : null;

		if (dataSource == null) {
			ApplicationProcess process = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
			ApplicationVO appvo = (ApplicationVO) process.doView(getApplicationId());
			
			OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource ds = appvo.getDataSourceDefine();
			
			String driver = ds.getDriverClass();
			if (driver!=null ) {
				String username = ds.getUsername();
				String password = ds.getPassword();
				String url = ds.getUrl();
				String poolsize = !StringUtils.isBlank(ds.getPoolsize()) ? ds.getPoolsize() : "10";
				String timeout = !StringUtils.isBlank(ds.getTimeout()) ? ds.getTimeout() : "5000";

//				dataSource = PersistenceUtils.getC3P0DataSource(username, password, driver, url, poolsize, timeout);
				dataSource = PersistenceUtils.getDBCPDataSource(username, password, driver, url, poolsize, timeout);
//				dataSource = PersistenceUtils.getProxoolDataSource(username, password, driver, url, poolsize, timeout);
			}
			else//增加 by XGY
			{
				dataSource=ResourcePool.getDataSource(appvo.getDatasourceid());
			}
			dataSources.put(applicationId, dataSource);
		}

		return dataSource;
	}

	/**
	 * Remove the data source.
	 * 
	 * @param application
	 *            The application name
	 */
	public static synchronized void removeDataSource(String application) {
		dataSources.remove(application);
	}

	/**
	 * Get the connection
	 * 
	 * @return the connection
	 * @throws Exception
	 */
	protected Connection getConnection() throws Exception {
		Map<String, Connection> connMap = PersistenceUtils.runtimeDBConn.get();
		Connection conn = null;
		if (connMap != null) {
			conn = connMap.get(getApplicationId());
		}
		if (conn == null || conn.isClosed()) {
			conn = getDataSource().getConnection();
			Map<String, Connection> map = new HashMap<String, Connection>();
			map.put(getApplicationId(), conn);
			PersistenceUtils.runtimeDBConn.set(map);
		}

		return MonProxyFactory.monitor(conn);
	}

	/**
	 * Get the transaction singal.
	 * 
	 * @return The transcation singal.
	 */
	protected int getTransactionSignal() {
		Integer signal = transactionSignal.get();
		if (signal != null) {
			return signal.intValue();
		} else {
			return 0;
		}
	}

	/**
	 * Set the transaction signal
	 * 
	 * @param signal
	 *            The transaction signal to set.
	 */
	protected void setTransactionSignal(int signal) {
		transactionSignal.set(Integer.valueOf(signal));
	}

}
