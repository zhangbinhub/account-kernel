package OLink.bpm.core.privilege.operation.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.permission.ejb.PermissionProcess;
import org.apache.log4j.Logger;

public class OperationHelper extends BaseHelper<OperationVO> {
	static Logger logger = Logger.getLogger(OperationHelper.class);

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public OperationHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(OperationProcess.class));
	}

	public Collection<OperationVO> getOperation() {
		Collection<OperationVO> rtn = new ArrayList<OperationVO>();
		try {
			Collection<OperationVO> colls = process.doSimpleQuery(null, getApplicationid());
			if (colls != null && colls.size() > 0) {
				rtn = colls;
			}
		} catch (Exception e) {
			logger.error("Create instance select error");
			e.printStackTrace();
		}
		return rtn;
	}

	/**
	 * 根据类型获得操作集合
	 * 
	 * @param resourcesId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getOperations(String resourcesid, int type) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		OperationProcess op = (OperationProcess) ProcessFactory.createProcess(OperationProcess.class);
		ParamsTable params = new ParamsTable();
		params.setParameter("i_resType", type);
		Collection<OperationVO> datas = op.doSimpleQuery(params, getApplicationid());
		if (datas != null && datas.size() > 0) {
			for (Iterator<OperationVO> ite = datas.iterator(); ite.hasNext();) {
				OperationVO operation = ite.next();
				map.put(operation.getId(), "{*[" + operation.getName() + "]*}");
			}
		}

		return map;
	}

	/**
	 * 获取操作集合
	 * 
	 * @param resourcesid
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getOperationMap(String resourcesid, String type, String applicationid) throws Exception {
		Map<String, String> map = new HashMap<String, String>();

		OperationProcess op = (OperationProcess) ProcessFactory.createProcess(OperationProcess.class);
		Collection<OperationVO> datas = op.getOperationByResource(resourcesid, Integer.parseInt(type), applicationid);
		if (datas != null && datas.size() > 0) {
			for (Iterator<OperationVO> ite = datas.iterator(); ite.hasNext();) {
				OperationVO operation = ite.next();
				map.put(operation.getId(), "{*[" + operation.getName() + "]*}");
			}
		}

		return map;
	}

	public String getOperationIdByCode(String resourcesid, String type, int operationCode, String applicationid)
			throws Exception {
		OperationProcess op = (OperationProcess) ProcessFactory.createProcess(OperationProcess.class);
		Collection<OperationVO> datas = op.getOperationByResource(resourcesid, Integer.parseInt(type), applicationid);
		if (datas != null && datas.size() > 0) {
			for (Iterator<OperationVO> ite = datas.iterator(); ite.hasNext();) {
				OperationVO operation = ite.next();
				if (operationCode == operation.getCode()) {
					return operation.getId();
				}
			}
		}

		return "";
	}

	public boolean isMenuType(String resourceType) {
		return ResVO.MENU_TYPE == Integer.parseInt(resourceType);
	}

	public Collection<OperationVO> getOperationList(String resourcesid, String type) throws Exception {
		try {
			OperationProcess op = (OperationProcess) ProcessFactory.createProcess(OperationProcess.class);
			Collection<OperationVO> datas = op.getOperationByResource(resourcesid, Integer.parseInt(type),
					getApplicationid());

			return datas;
		} catch (NumberFormatException e) {
			logger.warn(e.getMessage());
		}

		return new ArrayList<OperationVO>();
	}

	/**
	 * 获得类型
	 * 
	 * @param applicationid
	 * @param roleid
	 * @param resid
	 * @return
	 * @throws Exception
	 */
	public Collection<String> getOperateionValues(String applicationid, String roleid, String resid) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("application", applicationid);
		params.setParameter("s_role_id", roleid);
		params.setParameter("s_res_id", resid);
		PermissionProcess permissionProcess = (PermissionProcess) ProcessFactory.createProcess(PermissionProcess.class);
		DataPackage<PermissionVO> datas = permissionProcess.doQuery(params);
		Collection<String> rtn = new ArrayList<String>();

		if (datas.rowCount > 0) {
			for (Iterator<PermissionVO> iter = datas.datas.iterator(); iter.hasNext();) {
				PermissionVO permissionVO = iter.next();
				if (permissionVO != null) {
					rtn.add(permissionVO.getOperationId());
				}
			}
		}
		return rtn;
	}

}
