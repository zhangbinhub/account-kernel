package OLink.bpm.util;

import java.sql.Connection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.counter.dao.*;
import OLink.bpm.core.dynaform.form.dao.DB2FormTableDAO;
import OLink.bpm.core.dynaform.form.dao.MssqlFormTableDAO;
import OLink.bpm.core.dynaform.form.dao.MysqlFormTableDAO;
import OLink.bpm.core.dynaform.pending.dao.*;
import OLink.bpm.core.report.crossreport.runtime.dao.OracleRuntimeDAO;
import OLink.bpm.core.report.oreport.dao.*;
import OLink.bpm.core.report.standardreport.dao.*;
import OLink.bpm.core.report.wfdashboard.dao.HSQLWFDashBoardDAO;
import OLink.bpm.core.upload.dao.DB2UploadDAO;
import OLink.bpm.core.upload.dao.MssqlUploadDAO;
import OLink.bpm.core.upload.dao.MysqlUploadDAO;
import OLink.bpm.core.upload.dao.OracleUploadDAO;
import OLink.bpm.core.workflow.notification.dao.DB2NotificationDAO;
import OLink.bpm.core.workflow.notification.dao.HsqldbNotificationDAO;
import OLink.bpm.core.workflow.notification.dao.MysqlNotificationDAO;
import OLink.bpm.core.workflow.storage.runtime.intervention.dao.HsqldbFlowInterventionDAO;
import OLink.bpm.core.workflow.storage.runtime.intervention.dao.MssqlFlowInterventionDAO;
import OLink.bpm.core.workflow.storage.runtime.proxy.dao.DB2WorkflowProxyDAO;
import OLink.bpm.core.workflow.storage.runtime.proxy.dao.MssqlWorkflowProxyDAO;
import OLink.bpm.core.counter.dao.HsqldbCounterDAO;
import OLink.bpm.core.counter.dao.MssqlCounterDAO;
import OLink.bpm.core.counter.dao.OracleCounterDAO;
import OLink.bpm.core.deploy.application.dao.AbstractApplicationInitDAO;
import OLink.bpm.core.deploy.application.dao.DB2ApplicationInitDAO;
import OLink.bpm.core.deploy.application.dao.HsqldbApplicationInitDAO;
import OLink.bpm.core.deploy.application.dao.MssqlApplicationInitDAO;
import OLink.bpm.core.dynaform.document.dao.DB2DocStaticTblDAO;
import OLink.bpm.core.dynaform.document.dao.MssqlDocStaticTblDAO;
import OLink.bpm.core.dynaform.document.dao.OracleDocStaticTblDAO;
import OLink.bpm.core.dynaform.form.dao.HsqldbFormTableDAO;
import OLink.bpm.core.dynaform.form.dao.OracleFormTableDAO;
import OLink.bpm.core.dynaform.pending.dao.HsqldbPendingDAO;
import OLink.bpm.core.dynaform.pending.dao.OraclePendingDAO;
import OLink.bpm.core.report.oreport.dao.DB2OReportDAO;
import OLink.bpm.core.report.oreport.dao.HsqldbOReportDAO;
import OLink.bpm.core.report.oreport.dao.MssqlOReportDAO;
import OLink.bpm.core.report.oreport.dao.MysqlOReportDAO;
import OLink.bpm.core.report.standardreport.dao.DB2StandarReportDAO;
import OLink.bpm.core.report.standardreport.dao.HsqldbStandarReportDAO;
import OLink.bpm.core.report.standardreport.dao.MysqlStandarReportDAO;
import OLink.bpm.core.report.standardreport.dao.OracleStandarReportDAO;
import OLink.bpm.core.report.wfdashboard.dao.DB2WFDashBoardDAO;
import OLink.bpm.core.report.wfdashboard.dao.MSSqlWFDashBoardDAO;
import OLink.bpm.core.report.wfdashboard.dao.MySqlWFDashBoardDAO;
import OLink.bpm.core.workflow.notification.dao.MssqlNotificationDAO;
import OLink.bpm.core.workflow.notification.dao.OracleNotificationDAO;
import OLink.bpm.core.counter.dao.DB2CounterDAO;
import OLink.bpm.core.deploy.application.dao.MysqlApplicationInitDAO;
import OLink.bpm.core.deploy.application.dao.OracleApplicationInitDAO;
import OLink.bpm.core.dynaform.document.dao.HsqldbDocStaticTblDAO;
import OLink.bpm.core.dynaform.document.dao.MysqlDocStaticTblDAO;
import OLink.bpm.core.report.wfdashboard.dao.OracleWFDashBoardDAO;
import OLink.bpm.core.upload.dao.HsqldbUploadDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.DB2ActorRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.DB2CirculatorDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.DB2FlowStateRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.DB2NodeRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.DB2RelationHISDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.HsqldbActorRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.HsqldbCirculatorDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.HsqldbFlowStateRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.HsqldbNodeRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.HsqldbRelationHISDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MssqlActorRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MssqlCirculatorDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MssqlFlowStateRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MssqlNodeRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MssqlRelationHISDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MysqlActorRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MysqlCirculatorDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MysqlFlowStateRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MysqlNodeRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.MysqlRelationHISDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.OracleActorRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.OracleCirculatorDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.OracleFlowStateRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.OracleNodeRTDAO;
import OLink.bpm.core.workflow.storage.runtime.dao.OracleRelationHISDAO;
import OLink.bpm.core.workflow.storage.runtime.intervention.dao.DB2FlowInterventionDAO;
import OLink.bpm.core.workflow.storage.runtime.intervention.dao.MysqlFlowInterventionDAO;
import OLink.bpm.core.workflow.storage.runtime.intervention.dao.OracleFlowInterventionDAO;
import OLink.bpm.core.workflow.storage.runtime.proxy.dao.HsqldbWorkflowProxyDAO;
import OLink.bpm.core.workflow.storage.runtime.proxy.dao.MysqlWorkflowProxyDAO;
import OLink.bpm.core.workflow.storage.runtime.proxy.dao.OracleWorkflowProxyDAO;

public class RuntimeDaoManager {

	public IRuntimeDAO getActorRtDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleActorRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlActorRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlActorRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbActorRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2ActorRTDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getCounterDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleCounterDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlCounterDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlCounterDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbCounterDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2CounterDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getDocStaticTblDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleDocStaticTblDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlDocStaticTblDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlDocStaticTblDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbDocStaticTblDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2DocStaticTblDAO(conn, applicationId);
			}
		}
		return null;
	}

	public IRuntimeDAO abstractUploadDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleUploadDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlUploadDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlUploadDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbUploadDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2UploadDAO(conn, applicationId);
			}
		}
		return null;
	}

	public IRuntimeDAO getFlowStateRTDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleFlowStateRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlFlowStateRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlFlowStateRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbFlowStateRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2FlowStateRTDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getFormTableDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			return getFormTableDAOByDbType(conn, dbType);
		}
		return null;
	}

	public IRuntimeDAO getFormTableDAODtId(Connection conn, String datasourceId)
			throws Exception {
		if (datasourceId != null) {
			String dbType = DbTypeUtil.getDBTypeByDtId(datasourceId);
			return getFormTableDAOByDbType(conn, dbType);
		}
		return null;
	}

	public IRuntimeDAO getFormTableDAOByDbType(Connection conn, String dbType)
			throws Exception {
		if (DbTypeUtil.DBTYPE_ORACLE.equals(dbType)) {
			return new OracleFormTableDAO(conn);
		} else if (DbTypeUtil.DBTYPE_MSSQL.equals(dbType)) {
			return new MssqlFormTableDAO(conn);
		} else if (DbTypeUtil.DBTYPE_MYSQL.equals(dbType)) {
			return new MysqlFormTableDAO(conn);
		} else if (DbTypeUtil.DBTYPE_HSQLDB.equals(dbType)) {
			return new HsqldbFormTableDAO(conn);
		} else if (DbTypeUtil.DBTYPE_DB2.equals(dbType)) {
			return new DB2FormTableDAO(conn);
		}
		return null;
	}

	public IRuntimeDAO getNodeRTDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleNodeRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlNodeRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlNodeRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbNodeRTDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2NodeRTDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getRelationHisDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleRelationHISDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlRelationHISDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlRelationHISDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbRelationHISDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2RelationHISDAO(conn);
			}
		}
		return null;
	}

	public AbstractApplicationInitDAO getApplicationInitDAO(Connection conn,
															String applicationId, String dbType) throws Exception {
		if (dbType.trim().equals("")) {
			if (applicationId != null) {
				dbType = DbTypeUtil.getDBType(applicationId);
			}
		}
		if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
			return new OracleApplicationInitDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
			return new MssqlApplicationInitDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
			return new MysqlApplicationInitDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
			return new HsqldbApplicationInitDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
			return new DB2ApplicationInitDAO(conn);
		}
		return null;
	}

	public IRuntimeDAO getPendingDAO(Connection conn, String applicationId)
			throws Exception {
		String dbType = DbTypeUtil.getDBType(applicationId);
		if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
			return new OraclePendingDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
			return new MssqlPendingDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
			return new MysqlPendingDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
			return new HsqldbPendingDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
			return new DB2PendingDAO(conn);
		}
		return null;
	}

	public IRuntimeDAO getFlowInterventionDAO(Connection conn,
			String applicationId) throws Exception {
		String dbType = DbTypeUtil.getDBType(applicationId);
		if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
			return new OracleFlowInterventionDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
			return new MssqlFlowInterventionDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
			return new MysqlFlowInterventionDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
			return new HsqldbFlowInterventionDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
			return new DB2FlowInterventionDAO(conn);
		}
		return null;
	}

	public IRuntimeDAO getWorkflowProxyDAO(Connection conn, String applicationId)
			throws Exception {
		String dbType = DbTypeUtil.getDBType(applicationId);
		if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
			return new OracleWorkflowProxyDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
			return new MssqlWorkflowProxyDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
			return new MysqlWorkflowProxyDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
			return new HsqldbWorkflowProxyDAO(conn);
		} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
			return new DB2WorkflowProxyDAO(conn);
		}
		return null;
	}

	public IRuntimeDAO getOReportDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleOReportDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlOReportDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlOReportDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbOReportDAO(conn, applicationId);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2OReportDAO(conn, applicationId);
			}
		}
		return null;
	}

	public IRuntimeDAO getReportDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2StandarReportDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getCrossReportDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleRuntimeDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbStandarReportDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2StandarReportDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getDashBoardDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleWFDashBoardDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MSSqlWFDashBoardDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MySqlWFDashBoardDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HSQLWFDashBoardDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2WFDashBoardDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getNotificationDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbtype = DbTypeUtil.getDBType(applicationId);
			if (dbtype.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleNotificationDAO(conn);
			} else if (dbtype.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlNotificationDAO(conn);
			} else if (dbtype.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlNotificationDAO(conn);
			} else if (dbtype.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbNotificationDAO(conn);
			} else if (dbtype.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2NotificationDAO(conn);
			}
		}
		return null;
	}

	public IRuntimeDAO getCirculatorDAO(Connection conn, String applicationId)
			throws Exception {
		if (applicationId != null) {
			String dbType = DbTypeUtil.getDBType(applicationId);
			if (dbType.equals(DbTypeUtil.DBTYPE_ORACLE)) {
				return new OracleCirculatorDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MSSQL)) {
				return new MssqlCirculatorDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_MYSQL)) {
				return new MysqlCirculatorDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_HSQLDB)) {
				return new HsqldbCirculatorDAO(conn);
			} else if (dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				return new DB2CirculatorDAO(conn);
			}
		}
		return null;
	}

}
