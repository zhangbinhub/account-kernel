package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

public class DepartmentTransfer extends BaseTransfer {
	
	private final static Logger LOG = Logger.getLogger(DepartmentTransfer.class);
	
	public void to2_5SP4() {
		
		try {
			updateDepartment4IndexCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 部门增加IndexCode字段需要更新旧数据
	 * @throws Exception
	 */
	private void updateDepartment4IndexCode() throws Exception {
		LOG.info("DepartmentTransfer Start!");
		try {
			DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("n_level",0);
			Collection<DepartmentVO> roots = process.doSimpleQuery(params);
			for(Iterator<DepartmentVO> iter = roots.iterator();iter.hasNext();){
				DepartmentVO root = iter.next();
				root.setIndexCode(root.getId());
				process.doUpdate(root);
				LOG.info("Department name:["+root.getName()+"] update success!");
				Collection<DepartmentVO> childs = process.getUnderDeptList(root.getId(), 1, true);
				updateChildDeps(process, childs, root.getId());
				
			}
			LOG.info("DepartmentTransfer End!");
		} catch (ClassNotFoundException e) {
			LOG.error("DepartmentTransfer Failed!");
			e.printStackTrace();
		} catch (Exception e) {
			LOG.error("DepartmentTransfer Failed!");
			e.printStackTrace();
		}finally{
			try {
				PersistenceUtils.closeSessionAndConnection();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	private void updateChildDeps(DepartmentProcess process,Collection<DepartmentVO> childs,String superIndexCode) throws Exception {
		
		for(Iterator<DepartmentVO> iter = childs.iterator();iter.hasNext();){
			DepartmentVO child = iter.next();
			String indexCode = superIndexCode+"_"+child.getId();
			child.setIndexCode(indexCode);
			process.doUpdate(child);
			LOG.info("Department name:["+child.getName()+"] update success!");
			Collection<DepartmentVO> _childs = process.getUnderDeptList(child.getId(), 1, true);
			
			updateChildDeps(process, _childs, indexCode);
			
		}
	}
	
	public static void main(String[] args) {
		new DepartmentTransfer().to2_5SP4();// just for test
	}


}
