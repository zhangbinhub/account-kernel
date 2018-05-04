package OLink.bpm.core.deploy.application.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.tree.Node;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import org.apache.log4j.Logger;

public class ApplicationHelper extends BaseHelper<ApplicationVO> {
	static Logger logger = Logger.getLogger(ApplicationHelper.class);

	/**
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public ApplicationHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ApplicationProcess.class));
	}

	/**
	 * getModuleTree
	 * 
	 * @throws ClassNotFoundException
	 */
	public Collection<Node> getModuleTree(String applicationid, String parentid) {
		Collection<Node> modTree = new ArrayList<Node>();
		if (parentid == null || "".equals(parentid)) {
			try {
				ApplicationVO av = this.getApplicationById(applicationid);
				Collection<ModuleVO> cols = getSubModuleTree(av.getModules(), "");
				modTree = getModuleTree(cols, av);
			} catch (Exception e) {
				logger.error("Create instance select error");
				e.printStackTrace();
			}
		} else {
			try {
				ApplicationVO av = this.getApplicationById(applicationid);
				Collection<ModuleVO> cols = getSubModuleTree(av.getModules(), parentid);
				modTree = getModuleTree(cols, av);
			} catch (Exception e) {
				logger.error("Create instance select error");
				e.printStackTrace();
			}
		}
		return modTree;
	}

	public Collection<Node> getModuleTree(Collection<ModuleVO> cols, ApplicationVO av) {
		Collection<Node> modTree = new ArrayList<Node>();
		for (Iterator<ModuleVO> iter = cols.iterator(); iter.hasNext();) {
			ModuleVO mv = iter.next();
			Node node = new Node();
			node.setId(mv.getId());
			node.setData(mv.getName());
			Collection<ModuleVO> sub = getSubModuleTree(av.getModules(), mv.getId());
			if (sub.size() > 0) {
				node.setState(Node.STATE_CLOSED);
			}
			modTree.add(node);
		}
		return modTree;
	}

	public Collection<ModuleVO> getSubModuleTree(Collection<ModuleVO> cols, String parentid) {
		Collection<ModuleVO> subModTree = new ArrayList<ModuleVO>();
		if (parentid != null && !"".equals(parentid)) {
			for (Iterator<ModuleVO> iter = cols.iterator(); iter.hasNext();) {
				ModuleVO mv = iter.next();
				if (mv.getSuperior() != null && mv.getSuperior().getId() != null
						&& mv.getSuperior().getId().trim().length() > 0 && mv.getSuperior().getId().equals(parentid)) {
					subModTree.add(mv);
				}
			}
		} else {
			for (Iterator<ModuleVO> iter = cols.iterator(); iter.hasNext();) {
				ModuleVO mv = iter.next();
				if (mv.getSuperior() == null || mv.getSuperior().getId() == null
						&& mv.getSuperior().getId().trim().length() <= 0) {
					subModTree.add(mv);
				}
			}
		}
		return subModTree;
	}

	public Collection<ApplicationVO> getAppList() {
		Collection<ApplicationVO> rtn = new ArrayList<ApplicationVO>();
		try {
			Collection<ApplicationVO> colls = process.doSimpleQuery(null, null);
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
	 * init application type
	 * 
	 * @return
	 */
	public Map<String, String> getApplicationType() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "");
		map.put("00", "{*[Mobile_Applications]*}");
		map.put("01", "{*[Financial_Management]*}");
		map.put("02", "{*[Market_Management]*}");
		map.put("03", "{*[Human_Resources]*}");
		map.put("04", "{*[Customer_Management]*}");
		map.put("05", "{*[Software_Development]*}");
		return map;
	}

	public String getDesc(String application) {
		String rtn = "";
		try {
			ApplicationVO vo = (ApplicationVO) process.doView(application);
			if (vo != null) {
				rtn = vo.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rtn;
	}

	public ApplicationVO getApplicationById(String id) throws Exception {
		ApplicationVO vo = (ApplicationVO) process.doView(id);
		return vo;
	}

	public String getApplicationNameById(String id) throws Exception {
		return getApplicationById(id).getName();
	}

	public Collection<ApplicationVO> queryApplications(WebUser user, int page, int line) throws Exception {
		ApplicationProcess process = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);

		return process.queryApplications(user.getId(), page, line);
	}

	/**
	 * 根据用户角色和企业域进行过滤
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Collection<ApplicationVO> getListByWebUser(WebUser user) throws Exception {
		Collection<ApplicationVO> applications = ((ApplicationProcess) process).queryByDomain(user.getDomainid());
		Collection<ApplicationVO> rtn = new ArrayList<ApplicationVO>();

		if (applications != null && !applications.isEmpty()) {
			for (Iterator<ApplicationVO> iterator = applications.iterator(); iterator.hasNext();) {
				ApplicationVO application = iterator.next();
				Collection<RoleVO> roles = getRolesByApplication(user, application.getId());
				if (roles != null && !roles.isEmpty() && application.isActivated()) {
					rtn.add(application);
				}
			}
		}

		return rtn;
	}
	
	private Collection<RoleVO> getRolesByApplication(WebUser user, String application) throws Exception {
		UserProcess up = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		UserVO currUser = (UserVO) up.doView(user.getId());
		Collection<RoleVO> roles = currUser.getRoles();
		Collection<RoleVO> rtn = new ArrayList<RoleVO>();
		for (Iterator<RoleVO> iterator = roles.iterator(); iterator.hasNext();) {
			RoleVO roleVO = iterator.next();
			if (roleVO.getApplicationid().equals(application)) {
				rtn.add(roleVO);
			}
		}

		return rtn;
	}

	public Map<String, String> getMapByDomain(String domainId) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("", "");
		Collection<ApplicationVO> list = ((ApplicationProcess) process).queryByDomain(domainId);
		for (Iterator<ApplicationVO> it = list.iterator(); it.hasNext();) {
			ApplicationVO vo = it.next();
			map.put(vo.getId(), vo.getName());
		}
		return map;
	}

	public Map<String, String> getMobileAppIcons() {
		Map<String, String> map = new HashMap<String, String>();
		String[] names = MOBILEAPPICOS;
		String[] types = APPICOTYPES;
		for (int i = 0; i < names.length; i++) {
			map.put(types[i], names[i]);
		}
		return map;
	}

	public static final String[] MOBILEAPPICOS = { "01", "02", "03", "04", "05" };

	public static final String[] APPICOTYPES = { "001", "010", "011", "100", "101" };

}
