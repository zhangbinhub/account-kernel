package OLink.bpm.core.resource.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.links.ejb.LinkProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.util.web.DWRHtmlUtils;

public class ResourceUtil {

	/**
	 * 获取菜单生成树
	 * 
	 * @param appid
	 *            软件id
	 * @param domainid
	 *            企业域ID
	 * @param basePath
	 *            应用相对路径
	 * @param userid
	 *            在线用户ID
	 * @return
	 * @throws Exception
	 */
	public String getMenuTree(String appid, String domainid, String basePath,
			String userid) throws Exception {
		ResourceAction action = new ResourceAction();
		StringBuffer menuTree = new StringBuffer();
		ParamsTable params = new ParamsTable();
		params.setParameter("xi_type", ResourceType.RESOURCE_TYPE_MOBILE);
		Collection<ResourceVO> topMenus = action.get_topmenus(appid, domainid, params);
		String panelName = "MainMenu";
		menuTree.append("new Function(\"");

		UserProcess userProcess = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);

		WebUser user = userProcess.getWebUserInstance(userid);

		// 1，创建一个面板，用来存放树
		menuTree.append(createPanel(panelName));

		int i = 0;

		for (Iterator<ResourceVO> iter = topMenus.iterator(); iter.hasNext(); i++) {
			ResourceVO resource = iter.next();

			Collection<ResourceVO> subMenus = action.getSubMenus(resource, appid, domainid,
					new ParamsTable());
			menuTree.append(createATree(panelName, resource, subMenus,
					basePath, user));

		}
		menuTree.append(renderPanel(panelName));
		menuTree.append("\")");

		return menuTree.toString();
	}

	/**
	 * 渲染在页面上的元素
	 * 
	 * @param panelName
	 *            EXT面板的名字
	 * @return
	 */
	private String renderPanel(String panelName) {
		return panelName + ".render('ct');";
	}

	/**
	 * 将树添加到面板中
	 * 
	 * @param panelName
	 *            EXT面板的名字
	 * @param treeName
	 *            树的名字
	 * @return
	 */
	private String addToPanel(String panelName, String treeName) {
		return panelName + ".add(" + treeName + ");";
	}

	/**
	 * 生成一棵树
	 * 
	 * @param panelName
	 *            面板名字
	 * @param res
	 *            顶层的菜单名
	 * @param subMenus
	 *            子节点的集合
	 * @param basePath
	 *            应用的相对路径
	 * @param user
	 *            在线用户
	 * @return
	 * @throws Exception
	 */
	private String createATree(String panelName, ResourceVO res,
			Collection<ResourceVO> subMenus, String basePath, WebUser user)
			throws Exception {
		StringBuffer tree = new StringBuffer();

		if (PermissionPackage.checkPermission(res, user)) {

			String treeName = "t" + res.getId().replaceAll("-", "_");
			String treeTitle = res.getDescription();
			String rootName = "n" + res.getId().replaceAll("-", "_");

			// 新建根
			tree.append("var " + rootName + " = new Ext.tree.TreeNode({text:'"
					+ rootName + "'});");

			// 新建树
			tree.append("var " + treeName + " = new Ext.tree.TreePanel({");
			tree.append("rootVisible:false,");
			tree.append("title:'" + treeTitle + "',");
			tree.append("root:" + rootName);
			tree.append(",bodyStyle:'background-color:#EDF7FF'");
			tree.append("});");

			// 节点
			int i = 0;
			for (Iterator<ResourceVO> iter = subMenus.iterator(); iter.hasNext(); i++) {
				ResourceVO resource = iter.next();
				String nodeName = "n" + resource.getId().replaceAll("-", "_");
				if (PermissionPackage.checkPermission(resource, user)) {
					tree.append(createANode(resource, nodeName, basePath));
				}
			}
			i = 0;
			for (Iterator<ResourceVO> iter = subMenus.iterator(); iter.hasNext(); i++) {
				ResourceVO resource = iter.next();
				String nodeName = "n" + resource.getId().replaceAll("-", "_");
				if (PermissionPackage.checkPermission(resource, user)) {
					if (resource.getSuperior() != null) {
						tree.append(appendChild("n"
								+ resource.getSuperior().getId().replaceAll("-",
										"_"), nodeName));
					} else {
						tree.append(appendChild(rootName, nodeName));
					}
				}
			}

			tree.append(addToPanel(panelName, treeName));

			return tree.toString();
		}

		return "";
	}

	/**
	 * 给资源添加子节点
	 * 
	 * @param superiorName
	 *            需要添加的节点名
	 * @param childName
	 *            子节点名字
	 * @return
	 */
	private String appendChild(String superiorName, String childName) {

		String rtn = "if (typeof " + superiorName + " !=  'undefined')";
		return rtn += superiorName + ".appendChild(" + childName + ");";
	}

	/**
	 * 生成节点
	 * 
	 * @param resource
	 *            需要生成节点的资源
	 * @param nodeName
	 *            节点的名字
	 * @param urlBasePath
	 *            节点所需要的链接
	 * @return
	 * @throws Exception
	 */
	private String createANode(ResourceVO resource, String nodeName,
			String urlBasePath) throws Exception {

		StringBuffer node = new StringBuffer();

		String resourceAction = resource.getResourceAction();
		node.append("var ").append(nodeName).append(
				" = new Ext.tree.TreeNode({text:'");
		node.append(resource.getDescription()).append("'");
		node.append(",id:'" + resource.getId() + "'");
		if (resourceAction.equals(ResourceType.ACTION_TYPE_NONE)) { // '00'
			node.append(",hrefTarget:'detail'");
		} else if (resourceAction.equals(ResourceType.ACTION_TYPE_VIEW)) { // '01'
			node.append(",href:'").append(urlBasePath).append(
					"portal/dynaform/view/displayView.action?_viewid=").append(
					resource.getDisplayView()).append("'");
			node.append(",hrefTarget:'detail'");
		} else if (resourceAction.equals(ResourceType.ACTION_TYPE_OTHERURL)) { // '03'
			node.append(",href:'").append(urlBasePath).append(
					resource.getOtherurl()).append("'");
			node.append(",hrefTarget:'detail'");
		}
		node.append(",iconCls:'xx-icon'");
		node.append("});");
		return node.toString();
	}

	/**
	 * 生成面板
	 * 
	 * @param panelName
	 *            面板的名字
	 * @return
	 * @throws Exception
	 */
	private String createPanel(String panelName) throws Exception {
		StringBuffer builder = new StringBuffer();
		builder
				.append("Ext.BLANK_IMAGE_URL = '../ext2.3/resources/images/default/s.gif';");
		builder.append("var clientHeight = document.body.clientHeight - 2;");
		builder.append("var clientWidth = document.body.clientWidth - 2;");
		builder.append("var " + panelName + " = new Ext.Panel({");
		builder.append("region : 'west',");
		builder.append("layout : 'accordion',");
		builder.append("autoScroll : false,");
		builder.append("width : clientWidth");
		builder.append(",height : clientHeight");
		builder
				.append(",bodyStyle:'background-color:#EDF7FF; border:1px solid #cbd6dc;'");
		builder.append("});");

		return builder.toString();
	}

	/**
	 * 获取菜单的集合，并显示为下拉框模式
	 * 
	 * @param excludeNodeId
	 *            需要排除在外的元素ID
	 * @param type
	 *            菜单类型
	 * @param application
	 *            应用ID
	 * @param selectFieldName
	 *            字段名字
	 * @param def
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String get_menus(String excludeNodeId, String type,
			String application, String selectFieldName, String def)
			throws Exception {
		Map<String, String> dm = new HashMap<String, String>();
		if (type != null) {
			ParamsTable params = new ParamsTable();
			if (type.equals(ResourceType.RESOURCE_TYPE_MOBILE)) {
				params
						.setParameter("s_type",
								ResourceType.RESOURCE_TYPE_MOBILE);
			} else {
				params.setParameter("xi_type",
						ResourceType.RESOURCE_TYPE_MOBILE);
			}
			IDesignTimeProcess<ResourceVO> proxy = ProcessFactory
					.createProcess(ResourceProcess.class);
			Collection<ResourceVO> dc = proxy.doSimpleQuery(params, application);
			dm = ((ResourceProcess) proxy).deepSearchMenuTree(dc, null,
					excludeNodeId, 0);
			if (dm.size() > 1 && type.equals(ResourceType.RESOURCE_TYPE_MOBILE)) {
				dm.remove("");
			}
		} else {
			dm.put("", "{*[NO]*}");
		}
		return DWRHtmlUtils.createOptions(dm, selectFieldName, def);
	}
	
	public boolean isLinkToView(String linkid) throws Exception{
		LinkProcess linkprocess =(LinkProcess) ProcessFactory.createProcess(LinkProcess.class);
		LinkVO link =(LinkVO) linkprocess.doView(linkid);
		if(link!=null){
			if(link.getType()!=null && link.getType().equals(LinkVO.LinkType.VIEW.getCode())){
				return true;
			}
		}
		return false;
	}
	
	public boolean isShowTotalRow(ResourceVO resource) throws Exception{
		//ResourceProcess rosourceprocess =(ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		//ResourceVO resource =(ResourceVO) rosourceprocess.doView(resourceid);
		if(resource!=null && resource.getLink()!=null){
			if(isLinkToView(resource.getLink().getId())){
				if(resource.getShowtotalrow()!=null && resource.getShowtotalrow().equals("true")){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public String getTotalRowByResourceid(String resourceid,HttpServletRequest request) throws Exception{
		if(resourceid.length()<=0){
			return "0";
		}
		StringBuffer html =new StringBuffer();
		ViewProcess viewprocess =(ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		ResourceProcess resourceprocess =(ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
		ResourceVO resource =(ResourceVO) resourceprocess.doView(resourceid);
		if(resource==null){
			return "0";
		}
		HttpSession session = request.getSession();
		WebUser user=(WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		
		LinkVO link =resource.getLink();
		if(link!=null && link.getType()!=null){
			if(link.getType().equals(LinkVO.LinkType.VIEW.getCode())){
				if(link.getActionContent()==null || link.getActionContent().equals("")){
					return "0";
				}
				View view = (View) viewprocess.doView(link.getActionContent());
				if (view != null) {
					DataPackage<Document> datas = view.getViewTypeImpl().getViewDatas(new ParamsTable(), user, null);
					html.append(String.valueOf(datas.rowCount));
					return html.toString();
				}
			}
		}
		return "0";
	}
	
	public Collection<ResourceVO> getResourcesByApp(String application) throws Exception{
		ResourceProcess resourceProcess=(ResourceProcess)ProcessFactory.createProcess(ResourceProcess.class);
		ParamsTable params=new ParamsTable();
		params.setParameter("application",application.toString());
		Collection<ResourceVO> cols= resourceProcess.doQuery(params).datas;
		return cols;
	}
}