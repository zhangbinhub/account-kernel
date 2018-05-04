package OLink.bpm.core.privilege.res.ejb;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.privilege.res.dao.ResDAO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.tree.Node;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.file.FileOperate;
import OLink.bpm.util.sequence.Sequence;


public class ResProcessBean extends AbstractDesignTimeProcessBean<ResVO> implements ResProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 724372177764903304L;
	private Environment env = Environment.getInstance();

	public ValueObject doViewByName(String name, String application) throws Exception {
		return getDAO().findByName(name, application);
	}

	/**
	 * 获得显示资源树形结构 parenttype 上级类型(16-模块资源,32-菜单资源,64-文件资源)
	 * 
	 * @param params
	 * @param rprocess
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getResTree(ParamsTable params) throws Exception {
		Collection<Node> allNodes = new ArrayList<Node>();
		String roottype = params.getParameterAsString("roottype"); // 根类型
		if (roottype != null) {
			int type = Integer.parseInt(roottype);
			switch (type) {
			case 16:
				allNodes.addAll(getModuleTree(params));
				break;
			case 32:
				allNodes.addAll(getMenuTree(params));
				break;
			case 64:
				allNodes.addAll(getFolderTree(params));
			default:
				break;
			}
		} else {
			// 初始化各种资源根节点
			Node resourceRoot = new Node();
			resourceRoot.setId("32");
			resourceRoot.setData("菜单资源");
			resourceRoot.addAttr("roottype", "32");
			resourceRoot.setState(Node.STATE_CLOSED);
			allNodes.add(resourceRoot);

			Node moduleRoot = new Node();
			moduleRoot.setId("16");
			moduleRoot.setData("模块资源");
			moduleRoot.addAttr("roottype", "16");
			moduleRoot.setState(Node.STATE_CLOSED);
			allNodes.add(moduleRoot);

			Node folderRoot = new Node();
			folderRoot.setId("64");
			folderRoot.setData("文件夹资源");
			folderRoot.addAttr("roottype", "64");
			folderRoot.setState(Node.STATE_CLOSED);
			allNodes.add(folderRoot);

		}

		return allNodes;
	}

	/**
	 * 文件夹树形结构
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getFolderTree(ParamsTable params) throws Exception {
		Collection<Node> childNodes = new ArrayList<Node>();// 树的孩子节点
		String roottype = params.getParameterAsString("roottype"); // 根类型
		String rootRealPath = env.getRealPath("uploads");
		File file = new File(rootRealPath);

		Collection<File> folders = FileOperate.deepSearchDirectory(file);
		for (Iterator<File> iterator = folders.iterator(); iterator.hasNext();) {
			File folder = iterator.next();

			Node node = new Node();
			String id = Sequence.getFileUUID(folder, "uploads");
			String parentid = Sequence.getFileUUID(folder.getParentFile(), "uploads");

			node.setId(id);
			node.setData("文件夹->" + folder.getName());
			node.addAttr("parentid", parentid);
			node.addAttr("roottype", roottype);
			node.addAttr("resourcetype", ResVO.FOLDER_TYPE);
			node.addAttr("resourcename", folder.getName());
			childNodes.add(node);
		}
		return childNodes;
	}

	/**
	 * 获取模块树
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getModuleTree(ParamsTable params) throws Exception {
		Collection<Node> childNodes = new ArrayList<Node>();// 树的孩子节点
		String roottype = params.getParameterAsString("roottype"); // 根类型
		ModuleProcess moduleprocess = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);

		Collection<ModuleVO> cols = moduleprocess.doSimpleQuery(params);
		for (Iterator<ModuleVO> iterator = cols.iterator(); iterator.hasNext();) {
			ModuleVO moduleVO = iterator.next();
			// 将子Module转化为Node
			Node node = new Node();
			node.setId(moduleVO.getId());
			node.setData("模块->" + moduleVO.getName());
			node.addAttr("roottype", roottype);
			node.addAttr("parentid", moduleVO.getSuperior() != null ? moduleVO.getSuperior().getId() : "");
			node.addAttr("resourcename", moduleVO.getName());
			childNodes.add(node);
		}

		return childNodes;
	}

	/**
	 * 菜单树形结构
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getMenuTree(ParamsTable params) throws Exception {
		Collection<Node> childNodes = new ArrayList<Node>();// 树的孩子节点
		String parentid = params.getParameterAsString("parentid");
		String roottype = params.getParameterAsString("roottype"); // 根类型
		if (parentid != null && !parentid.equals("")) {
			// params.setParameter("s_superior", parentid);
		}
		ResourceProcess rprocess = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		Collection<ResourceVO> dc = rprocess.doSimpleQuery(params);
		for (Iterator<ResourceVO> ite = dc.iterator(); ite.hasNext();) {
			ResourceVO rv = ite.next();
			Node node = new Node();
			node.setId(rv.getId());
			node.setData("菜单->" + rv.getDescription());
			node.addAttr("parentid", rv.getSuperior() != null ? rv.getSuperior().getId() : "");
			node.addAttr("resourcetype", ResVO.MENU_TYPE);
			node.addAttr("resourcename", rv.getDescription());
			node.addAttr("roottype", roottype);
			childNodes.add(node);
		}
		return childNodes;
	}

	/**
	 * 表单树形结构
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Collection<Node> getFormTree(ParamsTable params) throws Exception {
		Collection<Node> childNodes = new ArrayList<Node>();// 树的孩子节点
		String parentid = params.getParameterAsString("parentid");
		String temp = params.getParameterAsString("temp");
		FormProcess formprocess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		if (parentid != null && !parentid.equals("")) {
			params.setParameter("s_superior", parentid);
		}
		ModuleProcess moduleprocess = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
		Collection<ModuleVO> moduledc = moduleprocess.doSimpleQuery(params);

		for (Iterator<ModuleVO> ite = moduledc.iterator(); ite.hasNext();) {
			ModuleVO module = ite.next();
			ApplicationVO applicationVO = module.getApplication();
			if (module.getSuperior() == null) {
				Node node = new Node();
				node.setState(Node.STATE_CLOSED);
				childNodes.add(node);
				node.setId(module.getId());
				node.setData(applicationVO.getName() + "/" + module.getName());
				node.addAttr("temp", applicationVO.getName() + "/" + module.getName());
				node.addAttr("parentid", module.getId());
			} else {
				if (parentid != null && !parentid.equals("")) {
					Node node = new Node();
					node.setId(module.getId());
					node.setData(module.getName());
					node.addAttr("temp", URLDecoder.decode(temp, "UTF-8") + "/" + module.getName());
					node.setState(Node.STATE_CLOSED);
					node.addAttr("parentid", module.getId());
					childNodes.add(node);
				}
			}

		}

		if (moduledc.size() == 0
				|| formprocess.doGetTotalLines("from Form where module='" + parentid
						+ "'") > 0) {
			params.removeParameter("s_superior");
			params.setParameter("s_module", parentid);
			Collection<Form> formdc = formprocess.doSimpleQuery(params);
			for (Iterator<Form> ite1 = formdc.iterator(); ite1.hasNext();) {
				Form form = ite1.next();
				Node node1 = new Node();
				node1.setId(form.getId());
				node1.setData(form.getName());
				node1.addAttr("result", URLDecoder.decode(temp, "UTF-8") + "/" + form.getName());
				childNodes.add(node1);
			}
		}
		return childNodes;
	}

	@Override
	protected IDesignTimeDAO<ResVO> getDAO() throws Exception {
		return (ResDAO) DAOFactory.getDefaultDAO(ResVO.class.getName());
	}

}