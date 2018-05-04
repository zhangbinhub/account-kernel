package OLink.bpm.core.privilege.operation.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.privilege.operation.dao.OperationDAO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.ProcessFactory;

public class OperationProcessBean extends
		AbstractDesignTimeProcessBean<OperationVO> implements OperationProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -987546529351697082L;

	protected IDesignTimeDAO<OperationVO> getDAO() throws Exception {
		return (OperationDAO) DAOFactory.getDefaultDAO(OperationVO.class
				.getName());
	}

	public boolean isEmpty(String applicationId) throws Exception {
		return ((OperationDAO) getDAO()).isEmpty(applicationId);
	}

	OperationVO createOperationByActivity(Activity activity, int resourcetype) {
		if (activity == null)
			return null;

		OperationVO operation = new OperationVO();
		operation.setId(activity.getId());
		operation.setName(activity.getName());
		operation.setResType(resourcetype);
		operation.setApplicationid(activity.getApplicationid());
		operation.setCode(activity.getType());
		return operation;
	}

	public OperationVO doViewByResource(String operationid, String resourceid,
			int resourcetype) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		ViewProcess viewProcess = (ViewProcess) ProcessFactory
				.createProcess(ViewProcess.class);
		OperationProcess operationProcess = (OperationProcess) ProcessFactory
				.createProcess(OperationProcess.class);

		OperationVO operation = null;
		switch (resourcetype) {
		case ResVO.FORM_TYPE:
			Form form = (Form) formProcess.doView(resourceid);
			operation = createOperationByActivity(
					form.findActivity(operationid), resourcetype);
			break;
		case ResVO.VIEW_TYPE:
			View view = (View) viewProcess.doView(resourceid);
			operation = createOperationByActivity(
					view.findActivity(operationid), resourcetype);
			break;
		case ResVO.MENU_TYPE:
		case ResVO.FOLDER_TYPE:
		case ResVO.FORM_FIELD_TYPE:
			operation = (OperationVO) operationProcess.doView(operationid);
		default:
			break;
		}

		if (operation == null) {
			operation = new OperationVO();
			operation.setId(operationid);
			operation.setResType(resourcetype);
			operation.setCode(resourcetype);
		}

		return operation;
	}

	/**
	 * 根据资源ID和资源类型获取操作
	 * 
	 * @param resourceid
	 *            资源ID
	 * @param resourcetype
	 *            资源类型(Form和View中的操作为非固定操作)
	 * @return
	 * @throws Exception
	 */
	public Collection<OperationVO> getOperationByResource(String resourceid,
			int resourcetype, String applicationid) throws Exception {
		FormProcess formProcess = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		ViewProcess viewProcess = (ViewProcess) ProcessFactory
				.createProcess(ViewProcess.class);
		OperationProcess operationProcess = (OperationProcess) ProcessFactory
				.createProcess(OperationProcess.class);
		Collection<OperationVO> rtn = new ArrayList<OperationVO>();

		ParamsTable operationParams = new ParamsTable();
		operationParams.setParameter("t_restype", resourcetype);
		operationParams.setParameter("application", applicationid);
		switch (resourcetype) {
		case ResVO.FORM_TYPE:
			Form form = (Form) formProcess.doView(resourceid);
			Collection<Activity> formActs = form.getActivitys();
			for (Iterator<Activity> iterator = formActs.iterator(); iterator
					.hasNext();) {
				Activity activity = iterator.next();
				rtn.add(createOperationByActivity(activity, resourcetype));
			}
			break;
		case ResVO.VIEW_TYPE:
			View view = (View) viewProcess.doView(resourceid);
			Collection<Activity> viewActs = view.getActivitys();
			for (Iterator<Activity> iterator = viewActs.iterator(); iterator
					.hasNext();) {
				Activity activity = iterator.next();
				rtn.add(createOperationByActivity(activity, resourcetype));
			}
			break;
		case ResVO.FORM_FIELD_TYPE:
		case ResVO.MENU_TYPE:
		case ResVO.FOLDER_TYPE:
			Collection<OperationVO> operactions = operationProcess
					.doSimpleQuery(operationParams);
			rtn.addAll(operactions);
			break;
		default:
			break;
		}

		return rtn;
	}
}
