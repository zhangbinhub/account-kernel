package OLink.bpm.webservice;

import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.webservice.fault.DepartmentServiceFault;
import OLink.bpm.webservice.model.SimpleDepartment;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class DepartmentService {

	public SimpleDepartment createDepartment(SimpleDepartment dep)
			throws DepartmentServiceFault {
		try {
			DepartmentProcess process = (DepartmentProcess) ProcessFactory
					.createProcess(DepartmentProcess.class);

			if (dep == null || StringUtil.isBlank(dep.getDomainName())
					|| StringUtil.isBlank(dep.getName())) {
				throw new NullPointerException("对象为空或对象的ID为空.");
			}

			DomainVO domain = WebServiceUtil
					.validateDomain(dep.getDomainName());

			DepartmentVO vo = new DepartmentVO();

			ObjectUtil.copyProperties(vo, dep);
			DepartmentVO superDep = process.getRootDepartmentByApplication(
					null, domain.getId());
			vo.setSuperior(superDep);
			vo.setDomain(domain);
			vo.setDomainid(domain.getId());
			DepartmentVO temp = (DepartmentVO) process.doViewByName(dep.getName(), domain
					.getId());

			if (temp != null) {
				if (dep.getName().equalsIgnoreCase(temp.getName())
						&& vo.getSuperior().getId().equals(
								temp.getSuperior().getId())) {
					throw new Exception("该部门" + temp.getName() + "已经存在.");
				}
			}
			process.doCreate(vo);
		} catch (Exception e) {
			throw new DepartmentServiceFault(e.getMessage());
		}
		return dep;
	}

	public void updateDepartment(SimpleDepartment dep)
			throws DepartmentServiceFault {
		try {
			DepartmentProcess process = (DepartmentProcess) ProcessFactory
					.createProcess(DepartmentProcess.class);
			if (dep == null || StringUtil.isBlank(dep.getId())
					|| StringUtil.isBlank(dep.getName())) {
				throw new NullPointerException("对象为空或对象的ID为空.");
			}

			DomainVO domain = WebServiceUtil
					.validateDomain(dep.getDomainName());

			DepartmentVO vo = (DepartmentVO) process.doView(dep.getId());
			if (vo == null)
				throw new Exception("该部门" + dep.getId() + "不存在.");

			if (!dep.getName().equals(vo.getName())) {
				DepartmentVO temp = (DepartmentVO) process.doViewByName(dep.getName(), domain
						.getId());
				if (temp != null) {
					throw new Exception("该部门名称" + temp.getName() + "已存在！");
				}
			}

			ObjectUtil.copyProperties(vo, dep);
			process.doUpdate(vo);

		} catch (Exception e) {
			throw new DepartmentServiceFault(e.getMessage());
		}
	}

	public SimpleDepartment getDepartment(String pk)
			throws DepartmentServiceFault {
		SimpleDepartment dep = null;
		try {
			DepartmentProcess process = (DepartmentProcess) ProcessFactory
					.createProcess(DepartmentProcess.class);
			if (StringUtil.isBlank(pk)) {
				throw new NullPointerException("主键为空.");
			}
			DepartmentVO vo = (DepartmentVO) process.doView(pk);
			if (vo != null) {
				dep = new SimpleDepartment();
				ObjectUtil.copyProperties(dep, vo);
			}
		} catch (Exception e) {
			throw new DepartmentServiceFault(e.getMessage());
		}
		return dep;
	}

	public void deleteDepartment(String pk) throws DepartmentServiceFault {
		try {
			DepartmentProcess process = (DepartmentProcess) ProcessFactory
					.createProcess(DepartmentProcess.class);
			if (StringUtil.isBlank(pk)) {
				throw new NullPointerException("主键为空.");
			}
			process.doRemove(pk);
		} catch (Exception e) {
			throw new DepartmentServiceFault(e.getMessage());
		}
	}

	/**
	 * 更新当前部门的上级部门
	 * 
	 * @param dep
	 *            -当前部门
	 * @param superDep
	 *            -上级部门
	 * @throws DepartmentServiceFault
	 */
	public void upateSuperior(SimpleDepartment dep, SimpleDepartment superDep)
			throws DepartmentServiceFault {
		try {

			DepartmentProcess process = (DepartmentProcess) ProcessFactory
					.createProcess(DepartmentProcess.class);
			if (dep == null || StringUtil.isBlank(dep.getId())
					|| superDep == null || StringUtil.isBlank(superDep.getId())) {
				throw new NullPointerException("对象为空或对象的ID为空.");
			}

			DomainVO domain = WebServiceUtil
					.validateDomain(dep.getDomainName());
			DomainVO superiorDomain = WebServiceUtil.validateDomain(superDep
					.getDomainName());

			if (!domain.getId().equals(superiorDomain.getId()))
				throw new Exception("两个部门不在同一个域.");

			DepartmentVO vo = (DepartmentVO) process.doViewByName(
					dep.getName(), domain.getId());
			if (vo == null)
				throw new Exception("当前部门" + dep.getName() + "不存在.");
			DepartmentVO sp = (DepartmentVO) process.doViewByName(superDep
					.getName(), superiorDomain.getId());
			if (sp == null)
				throw new Exception("上级部门" + superDep.getName() + "不存在.");

			vo.setSuperior(sp);
			process.doUpdate(vo);

		} catch (Exception e) {
			throw new DepartmentServiceFault(e.getMessage());
		}
	}

}
