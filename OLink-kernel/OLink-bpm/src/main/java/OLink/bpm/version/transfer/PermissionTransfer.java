package OLink.bpm.version.transfer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.base.dao.DAOException;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import OLink.bpm.core.privilege.res.ejb.ResProcess;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;
import org.hibernate.cfg.Configuration;

import OLink.bpm.base.action.ParamsTable;
import eWAP.core.Tools;


public class PermissionTransfer extends BaseTransfer {

	private final static Logger LOG = Logger.getLogger(PermissionTransfer.class);

	public void to2_4() {
		try {
			ResourceProcess resourceProcess = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class); // 菜单
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			RoleProcess roleProcess = (RoleProcess) ProcessFactory.createProcess(RoleProcess.class); // 角色

			Collection<?> apps = applicationProcess.doSimpleQuery(null);

			for (Iterator<?> iterator = apps.iterator(); iterator.hasNext();) {
				ApplicationVO app = (ApplicationVO) iterator.next();
				// 菜单列表
				Collection<?> resources = resourceProcess.doSimpleQuery(null, app.getId());
				// 角色列表
				Collection<?> roles = roleProcess.doSimpleQuery(null, app.getId());
				// 可访问的(菜单+角色)集合
				Map<?, ?> viewAbleMap = getMenuViewAbleMap(app.getId());

				for (Iterator<?> iterator2 = resources.iterator(); iterator2.hasNext();) {
					ResourceVO resourceVO = (ResourceVO) iterator2.next();
					if (resourceVO.isIsprotected()) {
						LOG.info("Protected Resource: " + resourceVO.getDescription());
						// 获取资源
						ResVO resVO = createResWhenNotExist(resourceVO);

						// 赋禁止权限
						for (Iterator<?> iterator3 = roles.iterator(); iterator3.hasNext();) {
							RoleVO roleVO = (RoleVO) iterator3.next();
							if (!viewAbleMap.containsKey((resourceVO.getId() + "_" + roleVO.getId()))) {
								gtantAuth(roleVO, resVO);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void to2_5() {
		Statement statement=null;
		try {
			Connection conn = getConnection();
			Configuration cfg = new Configuration().configure();
			DatabaseMetaData metaData = conn.getMetaData();

			// 获取hibernate方言
			String dialect = cfg.getProperty("hibernate.dialect");
			LOG.info("dialect: " + dialect);

			String catalog = null;
			String schemaPattern = null;

			// 根据hibernate配置获取数据库类型
			String schema = getSchema(conn, dialect);
			if (dialect.equals("org.hibernate.dialect.OracleDialect")
					|| dialect.equals("org.hibernate.dialect.HSQLDialect")
					|| dialect.equals("DB2V9Dialect")) {
				schemaPattern = schema;
			} else if (dialect.equals("SQLServerUnicodeDialect")) {
				schemaPattern = "DBO";
			} else if (dialect.equals("org.hibernate.dialect.MySQL5Dialect")) {
				catalog = schema;
			}

			ResultSet rs = metaData.getImportedKeys(catalog, schemaPattern, "T_PERMISSION");
			statement = conn.createStatement();
			while (rs.next()) {
				String fk_name = rs.getString("FK_NAME");
				LOG.info("删除外键：" + fk_name);
				String sql = "";
				if (dialect.equals("SQLServerUnicodeDialect")){
					sql = "alter table T_PERMISSION drop CONSTRAINT " + fk_name;
				} else {
					sql = "alter table T_PERMISSION drop foreign key " + fk_name;
				}
				statement.execute(sql);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {//Add By XGY 20130228
			try {
				PersistenceUtils.closeStatement(statement);
			} catch (DAOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getSchema(Connection conn, String dialect) {
		if (dialect.equals("org.hibernate.dialect.OracleDialect")) {
			return DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
		} else if (dialect.equals("SQLServerUnicodeDialect")) {
			return DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MSSQL);
		} else if (dialect.equals("org.hibernate.dialect.MySQL5Dialect")) {
			return DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
		} else if (dialect.equals("org.hibernate.dialect.HSQLDialect")) {
			return DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
		} else if (dialect.equals("DB2V9Dialect")) {
			return DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
		}

		return "";
	}

	/**
	 * 为角色赋禁止权限
	 * 
	 * @param roleVO
	 *            角色
	 * @param resVO
	 *            资源
	 * @throws Exception
	 */
	private void gtantAuth(RoleVO roleVO, ResVO resVO) throws Exception {
		if (roleVO != null && resVO != null) {
			PermissionProcess process = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class); // 菜单
			// 查看角色是否已赋权
			Collection<PermissionVO> permissions = process.doQueryByRoleIdAndResName(roleVO.getId(), resVO.getName());
			if (permissions != null && permissions.size() > 0) {
				return;
			}

			// 初始化参数
			ParamsTable params = new ParamsTable();
			params.setParameter("roleid", roleVO.getId());
			params.setParameter("applicationid", roleVO.getApplicationid());

			OperationVO operation = getMenuOperation(roleVO.getApplicationid());
			if (operation != null) {
				params.setParameter(resVO.getId() + "_selects", operation.getId());
				params.setParameter(resVO.getId() + "_resourcesType", Integer.valueOf(PermissionVO.TYPE_ALLOW));

				process.grantAuth(new String[] { resVO.getId() }, params);
			}
		}

	}

	/**
	 * 获取可以访问的(菜单+角色)集合
	 * 
	 * @return
	 * @throws SQLException
	 */
	private Map<Object, Object> getMenuViewAbleMap(String applicationid) throws SQLException {
		Connection conn = getConnection();

		Map<Object, Object> rtn = new HashMap<Object, Object>();
		QueryRunner qRunner = new QueryRunner();
		String sql = "SELECT resource_id,role_id FROM t_permission";
		sql += " WHERE resource_id IS NOT NULL AND role_id IS NOT NULL AND APPLICATIONID='" + applicationid + "'";
		sql += " GROUP BY resource_id, role_id";

		List<?> dataList = (List<?>) qRunner.query(conn, sql, new MapListHandler());
		for (Iterator<?> iterator = dataList.iterator(); iterator.hasNext();) {
			Map<?, ?> data = (Map<?, ?>) iterator.next();
			String resourceid = (String) data.get("resource_id");
			String roleid = (String) data.get("role_id");
			rtn.put(resourceid + "_" + roleid, roleid);
		}

		return rtn;
	}

	/**
	 * 如果不存在则创建资源，否则返回指定资源
	 * 
	 * @param resourceVO
	 * @return
	 * @throws Exception
	 */
	private ResVO createResWhenNotExist(ResourceVO resourceVO) throws Exception {
		ResProcess resProcess = (ResProcess) ProcessFactory.createProcess(ResProcess.class); // 资源
		ResVO resVO = null;
		// 菜单对应的资源不存在，则创建
		if (resourceVO != null && !resProcess.checkExitName(resourceVO.getFullName(), resourceVO.getApplicationid())) {
			resVO = toRes(resourceVO);
			resProcess.doCreate(resVO);
		} else {
			if (resourceVO != null)
				resVO = (ResVO) resProcess.doViewByName(resourceVO.getFullName(), resourceVO.getApplicationid());
		}

		return resVO;
	}

	/**
	 * 获取菜单操作
	 * 
	 * @param application
	 *            软件
	 * @return
	 * @throws Exception
	 */
	private OperationVO getMenuOperation(String application) throws Exception {
		OperationProcess operationProcess = (OperationProcess) ProcessFactory.createProcess(OperationProcess.class);
		return (OperationVO) operationProcess.doViewByName("Isview", application);
	}

	/**
	 * 把菜单转换为资源
	 * 
	 * @param resourceVO
	 * @return
	 * @throws Exception
	 */
	private ResVO toRes(ResourceVO resourceVO) throws Exception {
		ResVO resVO = new ResVO();

		resVO.setId(Tools.getSequence());
		resVO.setName(resourceVO.getFullName());
		resVO.setType(ResVO.MENU_TYPE);
		resVO.setApplicationid(resourceVO.getApplicationid());
		resVO.setCaption(resourceVO.getDescription());

		return resVO;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PermissionTransfer permissionTransfer = new PermissionTransfer();
		// permissionTransfer.to2_4();
		permissionTransfer.to2_5();
	}

}
