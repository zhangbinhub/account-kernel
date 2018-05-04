package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import OLink.bpm.util.ProcessFactory;

public class OperationTransfer extends BaseTransfer {

	public void to2_4() {
	}

	public void to2_5() {
		try {
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			Collection<ApplicationVO> appList = applicationProcess.doSimpleQuery(null);
			for (Iterator<ApplicationVO> iterator = appList.iterator(); iterator.hasNext();) {
				ApplicationVO app = iterator.next();
				System.out.println("当前软件： " + app.getName());
				// 删除按钮
				OperationProcess operationProcess = (OperationProcess) ProcessFactory
						.createProcess(OperationProcess.class);
				String operationId1 = app.getId() + OperationVO.FORMFIELD_HIDDEN + "" + ResVO.FORM_FIELD_TYPE;
				System.out.println("Delete Operation: " + operationId1);
				operationProcess.doRemove(operationId1);
				String operationId2 = app.getId() + OperationVO.FORMFIELD_READONLY + "" + ResVO.FORM_FIELD_TYPE;
				System.out.println("Delete Operation: " + operationId2);
				operationProcess.doRemove(operationId2);

				// 新建修改按钮
				int code = OperationVO.FORMFIELD_READONLY;
				OperationVO operation = new OperationVO();
				operation.setId(app.getId() + code + "" + ResVO.FORM_FIELD_TYPE);
				operation.setName("Modify");
				operation.setCode(code);
				operation.setResType(ResVO.FORM_FIELD_TYPE);
				operation.setApplicationid(app.getId());

				System.out.println("Create Operation: " + operation.getName());
				operationProcess.doCreate(operation);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OperationTransfer transfer = new OperationTransfer();
		transfer.to2_5();
	}

}
