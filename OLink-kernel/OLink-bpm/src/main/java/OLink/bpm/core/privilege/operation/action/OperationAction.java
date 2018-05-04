package OLink.bpm.core.privilege.operation.action;

import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.init.InitOperationInfo;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;

/**
 * @SuppressWarnings 不支持泛型
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class OperationAction extends BaseAction {

	public OperationAction() throws Exception {
		super(ProcessFactory.createProcess(OperationProcess.class),
				new OperationVO());
	}

	private static final long serialVersionUID = 1L;

	/**
	 * 删除全部操作
	 */
	public String doDelete() {
		try{
		this.getParams().removeParameter("_pagelines");
		DataPackage<OperationVO> datas = ((OperationProcess)process).doQuery(this.getParams());
		if(datas.rowCount>0){
			for (Iterator<OperationVO> iter = datas.datas.iterator(); iter.hasNext();) {
				OperationVO operationVO = iter.next();
				process.doRemove(operationVO.getId());
			}
		}
		addActionMessage("{*[delete.successful]*}");
		return  SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	/**
	 * 撤销删除
	 * @return
	 */
	public String doUndo() {
		try{
			InitOperationInfo initOperation = new InitOperationInfo();
			initOperation.run();
			addActionMessage("{*[Undo]*}{*[delete.successful]*}");
			return  SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
}
