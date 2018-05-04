package OLink.bpm.core.resource.action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.core.permission.action.PermissionHelper;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.util.ProcessFactory;

import com.opensymphony.webwork.ServletActionContext;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.user.action.WebUser;
import eWAP.core.Tools;

public class ResourceHelper extends BaseHelper<ResourceVO> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ResourceHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ResourceProcess.class));
	}

	public String getName(String type) {
		String name = ResourceType.getName(type);
		return name;
	}

	public Collection<ResourceVO> deepSearchResouece(String resourceid) throws Exception {
		Collection<ResourceVO> rtn = new ArrayList<ResourceVO>();
		ResourceProcess rp = ((ResourceProcess) process);
		ResourceVO start = (ResourceVO) rp.doView(resourceid);
		if (start != null) {
			Collection<ResourceVO> allResource = rp.doSimpleQuery(null, getApplicationid());
			Collection<ResourceVO> underlist = rp.deepSearchResouece(allResource, start, null, 0);
			rtn.add(start); // 包含当前菜单
			for (Iterator<ResourceVO> iter = underlist.iterator(); iter.hasNext();) {
				ResourceVO res = iter.next();
				if (res.isIsprotected()) {
					rtn.add(res);
				}
			}
		}
		return rtn;
	}

	/**
	 * 获取当前菜单的直属下级菜单
	 * 
	 * @param resourceid
	 * @param deep
	 * @return
	 * @throws Exception
	 */
	public Collection<ResourceVO> searchSubResource(String resourceid, int deep,String domain) throws Exception {
		Collection<ResourceVO> rtn = new ArrayList<ResourceVO>();
		ResourceProcess rp = ((ResourceProcess) process);
		ParamsTable params = new ParamsTable();
		params.setParameter("_orderby", "orderno");
		ResourceVO start = (ResourceVO) rp.doView(resourceid);
		if (start != null) {
			Collection<ResourceVO> allResource = rp.doSimpleQuery(params, getApplicationid());
			Collection<ResourceVO> underlist = rp.deepSearchResouece(allResource, start, null, deep);

			for (Iterator<ResourceVO> iter = underlist.iterator(); iter.hasNext();) {
				ResourceVO res = iter.next();
				if(res.getIsview() == null
						|| res.getIsview().equals("public")
						|| (res.getIsview().equals("private") && res.getColids() != null && res.getColids().indexOf(domain) >= 0)){
					rtn.add(res);
				}
			}
		}
		return rtn;
	}
	
	
	public String getHtml(Collection<ResourceVO> cols) throws Exception{
		Environment ev=Environment.getInstance();
		HttpSession session = ServletActionContext.getRequest().getSession();
		WebUser user = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		String skinType = (String)session.getAttribute("SKINTYPE");
		StringBuffer html=new StringBuffer();
		PermissionHelper ph=new PermissionHelper();
		Boolean isTop=true;
		html.append(getHtml(cols,ev,user,skinType,ph,isTop));
		return html.toString();
	}
	
	public String getHtml(Collection<ResourceVO> cols,Environment ev,WebUser user,String skinType,PermissionHelper ph,boolean isTop) throws Exception{
		StringBuffer html=new StringBuffer();
		html.append("<ul>");
		for(Iterator<ResourceVO> iter=cols.iterator();iter.hasNext();){
			ResourceVO res= iter.next();
			if(ph.checkPermission(res, res.getApplicationid(), user)){
				Collection<ResourceVO> submenus=this.searchSubResource(res.getId(),1,res.getDomainid());
				if(isTop){
					if(submenus.size()>0){
						html.append("<li class='hassubmenus' id='"+res.getId()+"'><img src='"+ev.getContextPath()+"/portal/"+skinType+"/resource/images/down.gif'/>");
					}else{
						html.append("<li class='nosubmenus' id='"+res.getId()+"'>");
					}
				}else{
					if(submenus.size()>0){
						html.append("<li class='subhassubmenus' id='"+res.getId()+"'><img src='"+ev.getContextPath()+"/portal/"+skinType+"/resource/images/down.gif'/>");
					}else{
						html.append("<li id='"+res.getId()+"'>");
					}
				}
				html.append("<a title='"+res.getDescription()+"' href='"+res.toUrlString(user,ServletActionContext.getRequest())+"' class='first'  target='detail'>"+res.getDescription()+"</a>");
				if(submenus.size()>0){
					html.append(getHtml(submenus,ev,user,skinType,ph,false));
				}
				html.append("</li>");
			}
		}
		html.append("</ul>");
		return html.toString();
	}

	public Collection<ResourceVO> searchResourceForMb(ResourceVO startNode, String domain) throws Exception {
		Collection<ResourceVO> rtn = new ArrayList<ResourceVO>();
		ResourceProcess rp = ((ResourceProcess) process);
		if (startNode != null) {
			ParamsTable params = new ParamsTable();
			params.setParameter("s_type", ResourceType.RESOURCE_TYPE_MOBILE);
			Collection<ResourceVO> allResource = rp.doSimpleQuery(params, getApplicationid());
			Collection<ResourceVO> underlist = rp.deepSearchResouece(allResource, startNode, null, 1);

			for (Iterator<ResourceVO> iter = underlist.iterator(); iter.hasNext();) {
				ResourceVO vo = iter.next();
//				if (startNode == null) {
//					if (vo.getSuperior() == null
//							&& (vo.getIsview() == null || vo.getIsview().equals("public") || (vo.getIsview().equals(
//									"private")
//									&& vo.getColids() != null && vo.getColids().indexOf(domain) >= 0))) {
//						rtn.add(vo);
//					}
//				} else 
					
					if (vo.getIsview() == null
						|| vo.getIsview().equals("public")
						|| (vo.getIsview().equals("private") && vo.getColids() != null && vo.getColids()
								.indexOf(domain) >= 0)) {
					if (vo.getSuperior() != null) {
						ResourceVO superior = vo.getSuperior();
						while (superior != null) {
							if (superior.getId().equals(startNode.getId())) {
								rtn.add(vo);
								break;
							}
							superior = superior.getSuperior();
						}
					}
				}
			}
		}
		return rtn;
	}

	public ResourceVO getResourcById(String resourceid) throws Exception {
		ResourceProcess rp = ((ResourceProcess) process);
		return (ResourceVO) rp.doView(resourceid);

	}

	public ResourceVO getTopResourceByName(String name) throws Exception {
		ResourceProcess rp = ((ResourceProcess) process);
		return rp.getTopResourceByName(name, getApplicationid());
	}

	public Map<String, String> getMobileIcons() {
		Map<String, String> map = new HashMap<String, String>();
		String[] names = ResourceType.MOBILEICOS;
		String[] types = ResourceType.ICOTYPES;
		for (int i = 0; i < names.length; i++) {
			map.put(types[i], names[i]);
		}
		return map;
	}

	public Map<String, String> getAllDomain() throws Exception {
		DomainProcess process = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		Collection<DomainVO> domains = process.getAllDomain();
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (domains.size() <= 0) {
			return map;
		}
		for (Iterator<DomainVO> iter = domains.iterator(); iter.hasNext();) {
			DomainVO domain = iter.next();
			map.put(domain.getId(), domain.getName());
		}
		return map;

	}

	public void addReportResource(String applicationid) throws Exception {
			ResourceProcess process = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("s_applicationid", applicationid);
			Collection<ResourceVO> colls = process.doSimpleQuery(params, null);

			if (colls.isEmpty()) {

				// 时效报表的上级菜单
				ResourceVO statReport = new ResourceVO();
				statReport.setId(Tools.getSequence());
				statReport.setActionclass("none");
				statReport.setActionmethod("none");
				statReport.setActionurl("");
				statReport.setDescription("报表");
				statReport.setIsprotected(false);
				statReport.setOrderno("2");
				statReport.setSuperior(null);
				statReport.setType("00");
				statReport.setOtherurl("");
				statReport.setApplication("");
				statReport.setDisplayView("");
				statReport.setModule("");
				statReport.setResourceAction(ResourceType.ACTION_TYPE_NONE);
				statReport.setReport("");
				statReport.setReportAppliction("");
				statReport.setReportModule("");
				statReport.setSortId(Tools.getTimeSequence());
				statReport.setApplicationid(applicationid);
				process.doUpdate(statReport);

				// 时效报表菜单
				ResourceVO resource = new ResourceVO();
				resource.setId(Tools.getSequence());
				resource.setActionclass("none");
				resource.setActionmethod("none");
				resource.setActionurl("");
				resource.setDescription("时效报表");
				resource.setIsprotected(false);
				resource.setOrderno("2");
				resource.setSuperior(statReport);
				resource.setType("00");
				resource.setOtherurl("/portal/share/report/standardreport/query.jsp");
				resource.setApplication("");
				resource.setDisplayView("");
				resource.setModule("");
				resource.setResourceAction(ResourceType.ACTION_TYPE_OTHERURL);
				resource.setReport("");
				resource.setReportAppliction("");
				resource.setReportModule("");
				resource.setSortId(Tools.getTimeSequence());
				resource.setApplicationid(applicationid);
				process.doUpdate(resource);

				// 流程仪表盘
				ResourceVO resource2 = new ResourceVO();
				resource2.setId(Tools.getSequence());
				resource2.setActionclass("none");
				resource2.setActionmethod("none");
				resource2.setActionurl("");
				resource2.setDescription("流程仪表盘");
				resource2.setIsprotected(false);
				resource2.setOrderno("3");
				resource2.setSuperior(statReport);
				resource2.setType("00");
				resource2.setOtherurl("/portal/share/report/wfdashboard/sumframe.jsp");
				resource2.setApplication("");
				resource2.setDisplayView("");
				resource2.setModule("");
				resource2.setResourceAction(ResourceType.ACTION_TYPE_OTHERURL);
				resource2.setReport("");
				resource2.setReportAppliction("");
				resource2.setReportModule("");
				resource2.setSortId(Tools.getTimeSequence());
				resource2.setApplicationid(applicationid);
				process.doUpdate(resource2);
			}
	}
	
	
	/**
	 * 获取图标库的所有图标
	 * @return
	 * @throws Exception
	 */
	public Collection<Icon> getIcons() throws Exception {
		Collection<Icon> rtn = new ArrayList<Icon>();
		Environment ev = Environment.getInstance();
		String contentPath = "";
		if (ev != null) {
			contentPath = ev.getRealPath("") + "lib/icon";
			File dir = new File(contentPath);
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (!files[i].isDirectory() && isImageFile(files[i].getName())) {// 判断是否是目录文件
						Icon icon = new Icon();
						BufferedImage bufferedImage = ImageIO.read(files[i]);
						icon.setSize(bufferedImage.getWidth()+" x "+bufferedImage.getHeight());
						icon.setLength( new DecimalFormat("#.##").format(files[i].length()/1024.0)+" KB");
						icon.setName(files[i].getName());
						icon.setWidth(bufferedImage.getWidth());
						rtn.add(icon);
					}
				}
			}
		}
		return rtn;
	}
	
	/**
	 * 判断文件名是否为合法的图片文件格式
	 * @param name
	 * @return
	 */
	private boolean isImageFile(String name){
		name = name.toLowerCase();
		return name.indexOf("png") >= 0 || name.indexOf("ico") >= 0
		|| name.indexOf(".gif") >= 0
		|| name.indexOf(".jpg") >= 0
		|| name.indexOf(".jpge") >= 0
		|| name.indexOf(".bmp") >= 0;
	}
	
	/**
	 * 图标
	 * @author Happy
	 *
	 */
	public class Icon {
		private String name;
		private String size;
		private String length;
		private int width;
		
		
		public Icon() {
			super();
		}


		public int getWidth() {
			return width;
		}


		public void setWidth(int width) {
			this.width = width;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public String getSize() {
			return size;
		}


		public void setSize(String size) {
			this.size = size;
		}


		public String getLength() {
			return length;
		}


		public void setLength(String length) {
			this.length = length;
		}
		
		
		
	}
	
}
