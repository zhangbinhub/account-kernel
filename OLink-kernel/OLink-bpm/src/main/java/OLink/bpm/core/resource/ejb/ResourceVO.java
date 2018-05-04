
package OLink.bpm.core.resource.ejb;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.user.action.WebUser;

/**
 * @hibernate.class table="T_RESOURCE" lazy="false"
 */
public class ResourceVO extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7671765820640298103L;

	private static final Logger LOG = Logger.getLogger(ResourceVO.class);

	/**
	 * 主键
	 */
	private String id;

	/**
	 * 资源描述
	 */
	private String description;

	/**
	 * 英文描述
	 */
	private String engdescription;

	/**
	 * 资源URL
	 */
	private String actionurl;

	/**
	 * 资源类URL = Namespace + action
	 */
	private String actionclass;

	/*
	 * 其他url 当actionurl未被填写是方能有效
	 */
	private String otherurl;

	/**
	 * Action 方法
	 */
	private String actionmethod;

	/**
	 * 资源类型： 00=菜单 01=页面 10=Mobile 菜单
	 */
	private String type;

	/**
	 * 打开页面位置
	 */
	private String opentarget;

	public String getOpentarget() {
		return opentarget;
	}

	public void setOpentarget(String opentarget) {
		this.opentarget = opentarget;
	}

	/**
	 * 排序字段
	 */
	private String orderno;

	/**
	 * 是否受保护
	 */
	private boolean isprotected;

	/**
	 * 是否显示记录总数
	 */
	private String showtotalrow;

	public String getShowtotalrow() {
		return showtotalrow;
	}

	public void setShowtotalrow(String showtotalrow) {
		this.showtotalrow = showtotalrow;
	}

	/**
	 * 菜单图标
	 */
	private String ico;

	/**
	 * 菜单图标类型 001=目录菜单 010=待办列表 011=超期列表 100=常规列表
	 */
	private String mobileIco;

	private ResourceVO superior;

	private String application;

	private String module;

	private String isview;

	private String colids;

	private String displayView;

	private String resourceAction;

	private String reportAppliction;

	private String reportModule;

	private String report;

	private Set<PermissionVO> relatedPermissions;

	private String impMappingConfig;

	private LinkVO link;

	public static final String ACTIONTYPE_NONE = "00";

	public static final String ACTIONTYPE_VIEW = "01";

	public static final String ACTIONTYPE_ACTIONClASS = "02";

	public static final String ACTIONTYPE_OTHEROUR = "03";

	public static final String ACTIONTYPE_REPORT = "04";

	public static final String ACCESS_CONTRAL_PRIVATE = "private";

	public static final String ACCESS_CONTRAL_PUGLIC = "public";

	/**
	 * @return java.lang.String
	 * @hibernate.property column="ACTIONCLASS "
	 * @roseuid 44C5FC6C00E5
	 */
	public String getActionclass() {
		return actionclass;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "ACTIONMETHOD"
	 * @roseuid 44C5FC6C012B
	 */
	public String getActionmethod() {
		return actionmethod;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column="ACTIONURL"
	 * @roseuid 44C5FC6C00A9
	 */
	public String getActionurl() {
		return actionurl;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column="DESCRIPTION"
	 * @roseuid 44C5FC6C0026
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column="ENGDESCRIPTION"
	 * @roseuid 44C5FC6C006D
	 */
	public String getEngdescription() {
		return engdescription;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "ICO"
	 * @roseuid 44C5FC6C0289
	 */
	public String getIco() {
		return ico;
	}

	/**
	 * @return the current value of the id property
	 * @hibernate.id column="ID" generator-class = "assigned"
	 * @roseuid 44C5FC6B03D2
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "ORDERNO"
	 * @roseuid 44C5FC6C01FD
	 */
	public String getOrderno() {
		return orderno;
	}

	/**
	 * @hibernate.many-to-one class = "ResourceVO"
	 *                        column = "SUPERIOR" outer-join = "true"
	 * @return Returns the superior.
	 * @roseuid 44C7A18A029F
	 */
	public ResourceVO getSuperior() {
		return superior;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "TYPE"
	 * @roseuid 44C5FC6C017B
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param aActionclass
	 * @roseuid 44C5FC6C00F9
	 */
	public void setActionclass(String aActionclass) {
		actionclass = aActionclass;
	}

	/**
	 * @param aActionmethod
	 * @roseuid 44C5FC6C0149
	 */
	public void setActionmethod(String aActionmethod) {
		actionmethod = aActionmethod;
	}

	/**
	 * @param aActionurl
	 * @roseuid 44C5FC6C00BD
	 */
	public void setActionurl(String aActionurl) {
		actionurl = aActionurl;
	}

	/**
	 * @param aDescription
	 * @roseuid 44C5FC6C003B
	 */
	public void setDescription(String aDescription) {
		description = aDescription;
	}

	/**
	 * @param aEngdescription
	 * @roseuid 44C5FC6C0077
	 */
	public void setEngdescription(String aEngdescription) {
		engdescription = aEngdescription;
	}

	/**
	 * @param aIco
	 * @roseuid 44C5FC6C029D
	 */
	public void setIco(String aIco) {
		ico = aIco;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param aId
	 *            the new value of the id property
	 * 
	 * @param aId
	 * @roseuid 44C5FC6B03DC
	 */
	public void setId(String aId) {
		id = aId;
	}

	/**
	 * @param aOrderno
	 * @roseuid 44C5FC6C0211
	 */
	public void setOrderno(String aOrderno) {
		orderno = aOrderno;
	}

	/**
	 * @param superior
	 * @roseuid 44C7A18A02A8
	 */
	public void setSuperior(ResourceVO superior) {
		this.superior = superior;
	}

	/**
	 * @param aType
	 * @roseuid 44C5FC6C018F
	 */
	public void setType(String aType) {
		type = aType;
	}

	/**
	 * @hibernate.property column="OTHERURL"
	 */
	public String getOtherurl() {
		return otherurl;
	}

	public void setOtherurl(String otherurl) {
		this.otherurl = otherurl;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "APPLICATION"
	 * @roseuid 44C5FC6C012B
	 */
	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "DISPLAYVIEW"
	 * @roseuid 44C5FC6C012B
	 */
	public String getDisplayView() {
		return displayView;
	}

	public void setDisplayView(String displayView) {
		this.displayView = displayView;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "MODULE"
	 * @roseuid 44C5FC6C012B
	 */
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return java.lang.String
	 * @hibernate.property column = "RESOURCEACTION"
	 * @roseuid 44C5FC6C012B
	 */
	public String getResourceAction() {
		return resourceAction;
	}

	public void setResourceAction(String resourceAction) {
		this.resourceAction = resourceAction;
	}

	/**
	 * @hibernate.set name="permission" table="T_PERMISSION" cascade="delete"
	 *                lazy="false" inverse="true"
	 * @hibernate.collection-key column="RESOURCE_ID"
	 * @hibernate.collection-one-to-many 
	 *                                   class="PermissionVO"
	 * @return
	 */
	public Set<PermissionVO> getRelatedPermissions() {
		return relatedPermissions;
	}

	public void setRelatedPermissions(Set<PermissionVO> relatedPermissions) {
		this.relatedPermissions = relatedPermissions;
	}

	/**
	 * @hibernate.property column = "ISPROTECTEDS"
	 */
	public boolean isIsprotected() {
		return isprotected;
	}

	public void setIsprotected(boolean isprotected) {
		this.isprotected = isprotected;
	}

	/**
	 * @hibernate.property column="REPORT"
	 */
	public String getReport() {
		return report;
	}

	/**
	 * @param report
	 *            The report to set.
	 */
	public void setReport(String report) {
		this.report = report;
	}

	/**
	 * @hibernate.property column="COLIDS"
	 */
	public String getColids() {
		return colids;
	}

	public void setColids(String colids) {
		this.colids = colids;
	}

	/**
	 * @hibernate.property column="ISVIEW"
	 */
	public String getIsview() {
		return isview;
	}

	public void setIsview(String isview) {
		this.isview = isview;
	}

	/**
	 * @hibernate.property column="REPORTAPPLICTION"
	 */
	public String getReportAppliction() {
		return reportAppliction;
	}

	/**
	 * @param reportAppliction
	 *            The reportAppliction to set.
	 */
	public void setReportAppliction(String reportAppliction) {
		this.reportAppliction = reportAppliction;
	}

	/**
	 * @hibernate.property column="REPORTMODULE"
	 */
	public String getReportModule() {
		return reportModule;
	}

	/**
	 * @param reportModule
	 *            The reportModule to set.
	 */
	public void setReportModule(String reportModule) {
		this.reportModule = reportModule;
	}

	/**
	 * @hibernate.property column="MOBILEICO"
	 */
	public String getMobileIco() {
		return mobileIco;
	}

	/**
	 * @param mobileIco
	 *            The mobileIco to set.
	 */
	public void setMobileIco(String mobileIco) {
		this.mobileIco = mobileIco;
	}

	/**
	 * @hibernate.property column = "IMPMAPPINGCONFIG";
	 * @return
	 */
	public String getImpMappingConfig() {
		return impMappingConfig;
	}

	public void setImpMappingConfig(String impMappingConfig) {
		this.impMappingConfig = impMappingConfig;
	}

	public LinkVO getLink() {
		return link;
	}

	public void setLink(LinkVO link) {
		this.link = link;
	}

	public String toUrlString() {
		return toUrlString(null, new ParamsTable());
	}

	/**
	 * 返回此菜单的超链接
	 * 
	 * @return
	 */
	public String toUrlString(WebUser user, ParamsTable params) {
		Environment env = Environment.getInstance();
		StringBuffer html = new StringBuffer();
		if (this.getLink() != null) {
			String linkUrl = this.getLink().toLinkUrl(user, params);
			if ("01".equals(this.getLink().getType())) {
				html.append(linkUrl);
				html.insert(linkUrl.indexOf("?") + 1, "_resourceid=" + this.getId() + "&");
				linkUrl = html.toString();
			}
			if (linkUrl.toLowerCase().startsWith("http")
					|| linkUrl.toLowerCase().startsWith(env.getContextPath().toLowerCase())) {
				return linkUrl;
			} else {
				if (linkUrl.startsWith("/")) {
					return env.getContextPath() + linkUrl;
				}
				return env.getContextPath() + "/" + linkUrl;
			}

		} else {
			return toOldUrlString();
		}
	}

	/**
	 * 返回此菜单的超链接
	 * 
	 * @return
	 */
	public String toUrlString(WebUser user, HttpServletRequest request) {
		ParamsTable params = ParamsTable.convertHTTP(request);
		return toUrlString(user, params);
	}

	/**
	 * 2.3版本之前的兼容模式
	 * 
	 * @deprecated 旧版本方法，已掉弃
	 * @return
	 */
	public String toOldUrlString() {
		if (getResourceAction() == null) {
			return "javascript:void(0)";
		}
		Environment env = Environment.getInstance();

		if (String.valueOf(ResourceType.ACTION_TYPE_NONE).equals(this.getResourceAction())) {
			return "javascript:void(0)";
		} else if (String.valueOf(ResourceType.ACTION_TYPE_VIEW).equals(this.getResourceAction())) {
			return env.getContextPath() + "/portal/dynaform/view/displayView.action?_viewid=" + this.getDisplayView()
					+ "&clearTemp=true";
		} else if (String.valueOf(ResourceType.ACTION_TYPE_ACTIONCLASS).equals(this.getResourceAction())) {
			return env.getContextPath() + this.getActionurl();
		} else if (String.valueOf(ResourceType.ACTION_TYPE_OTHERURL).equals(this.getResourceAction())) {
			return env.getContextPath() + this.getOtherurl();
		} else if (String.valueOf(ResourceType.ACTION_TYPE_REPORT).equals(this.getResourceAction())) {
			return env.getContextPath() + "/portal/report/crossreport/runtime/runreport.action?reportId="
					+ this.getReport();
		} else if (String.valueOf(ResourceType.RESOURCE_TYPE_EXCIMP).equals(this.getResourceAction())) {
			return env.getContextPath() + "/portal/share/dynaform/dts/excelimport/importbyid.jsp?id="
					+ this.getImpMappingConfig() + "&applicationid=" + this.getApplicationid();
		} else {
			return "javascript:void(0)";
		}
	}

	/**
	 * 获取菜单全名
	 * 
	 * @return String
	 */
	public String getFullName() {
		ResourceVO resVO = this;

		String resName = resVO.getDescription();
		while (resVO.getSuperior() != null) {
			resVO = resVO.getSuperior();
			resName = resVO.getDescription() + "/" + resName;
		}

		if (!StringUtil.isBlank(resVO.getApplicationid())) {
			try {
				ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
						.createProcess(ApplicationProcess.class);
				ApplicationVO applicationVO = (ApplicationVO) applicationProcess.doView(resVO.getApplicationid());
				resName = applicationVO.getName() + "/" + resName;
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}

		return resName;
	}
}
