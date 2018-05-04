package OLink.bpm.core.dynaform.form.ejb;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.VersionSupport;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityParent;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.engine.StateMachineHelper;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.HtmlEncoder;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.util.xml.XmlUtil;
import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import eWAP.core.IDefIO;
import eWAP.core.Tools;

/**
 * @hibernate.class table="T_DYNAFORM_FORM"
 * @author Marky
 */
public class Form extends VersionSupport implements Cloneable, ActivityParent {
	private transient final Logger log = Logger.getLogger(this.getClass());

	private static final long serialVersionUID = -6290080974202084245L;

	/**
	 * 普通表单类型常量
	 */
	public static final int FORM_TYPE_NORMAL = 0x0000001;

	/**
	 * 普通(含映射)表单类型常量
	 */
	public static final int FORM_TYPE_NORMAL_MAPPING = 0x0010000;

	/**
	 * 子表单类型常量
	 */
	public static final int FORM_TYPE_SUBFORM = 0x0000010;

	/**
	 * 查询表单类型常量
	 */

	public static final int FORM_TYPE_SEARCHFORM = 0x0000100;
	
	/**
	 * 首页
	 */
	public static final int FORM_TYPE_HOMEPAGE = 0x0001000;
	
	/**
	 * 阅读表单
	 */
	public static final int FORM_TYPE_TEMPLATEFORM = 0x0100000;
	
	private static final int[] formType = new int[] { FORM_TYPE_NORMAL,
			FORM_TYPE_SEARCHFORM, FORM_TYPE_HOMEPAGE, FORM_TYPE_NORMAL_MAPPING, FORM_TYPE_TEMPLATEFORM};

	private static final String[] formTypeName = new String[] { "NORMAL",
			"SEARCHFORM", "HOMEPAGE", "NORMAL_MAPPING","core.dynaform.form.type.templateform" };
	/**
	 * 
	 */
	private Map<String, FormField> _fields;

	private Collection<Textpart> _textparts;

	protected Collection<FormElement> _elements;

	protected Map<String, Component> _components = new LinkedHashMap<String, Component>();

	// private Collection<?> _compiledElements;
	/**
	 * 应用标识
	 */
	private String applicationid;
	/**
	 * 排序
	 */
	private String sortId;

	private Map<String, Form> subFormMap = new LinkedHashMap<String, Form>();

	/**
	 * Form在模版内容
	 * 
	 * @uml.property name="templatecontext"
	 */
	private String templatecontext;

	/**
	 * 表单描述
	 * 
	 * @uml.property name="discription"
	 */
	private String discription;

	/**
	 * Form类型
	 * 
	 * @uml.property name="type"
	 */
	private int type;

	/**
	 * 主键
	 * 
	 * @uml.property name="id"
	 */
	private String id;

	/**
	 * 所属模块
	 */
	private ModuleVO module;

	/**
	 * 是否记录日志
	 */
	private boolean showLog;

	/**
	 * 最后修改用户
	 */
	private UserVO lastmodifier;

	/**
	 * 最后修改日期
	 * 
	 * @uml.property name="lastmodifytime"
	 */
	private Date lastmodifytime;

	/**
	 * Form名称
	 * 
	 * @uml.property name="name"
	 */
	private String name;

	/**
	 * 样式库
	 */
	public StyleRepositoryVO style;

	/**
	 * 按钮
	 * 
	 * @uml.property name="activitys"
	 */
	private Set<Activity> activitys;

	/**
	 * 表单版本
	 * 
	 * @uml.property name="version"
	 */

	private int version;

	/**
	 * 打开表单前执行的脚本
	 * 
	 * @uml.property name="beforopenscript"
	 */
	private String isopenablescript;

	/**
	 * 是否可编辑脚本
	 * 
	 * @uml.property name="iseditablescript"
	 */
	private String iseditablescript;

	private String activityXML;

	/**
	 * 短信关联名,全局唯一性
	 */
	private String relationName;

	/**
	 * 短信关联字段列表
	 */
	private String relationText;

	/**
	 * 短信填单时，保存后是否马上启动流程
	 */
	private boolean onSaveStartFlow;

	/**
	 * 字段映射字符串
	 */
	private String mappingStr;

	/**
	 * 字段映射
	 */
	private TableMapping mapping;

	/**
	 * 文档摘要配置
	 */
	private String documentSummaryXML;

	private Collection<SummaryCfgVO> summaryCfg;

	/**
	 * 是否签出
	 */
	private boolean checkout = false;

	/**
	 * 签出者
	 */
	private String checkoutHandler;

	/**
	 * 是否被签出
	 * 
	 * @return
	 */
	public boolean isCheckout() {
		return checkout;
	}

	/**
	 * 设置是否签出
	 * 
	 * @param checkout
	 */
	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	/**
	 * 获取签出者
	 * 
	 * @return
	 */
	public String getCheckoutHandler() {
		return checkoutHandler;
	}

	/**
	 * 设置签出者
	 * 
	 * @param checkoutHandler
	 */
	public void setCheckoutHandler(String checkoutHandler) {
		this.checkoutHandler = checkoutHandler;
	}

	/**
	 * 获取文档的摘要配置
	 * 
	 * @return
	 */
	public String getDocumentSummaryXML() {
		return documentSummaryXML;
	}

	/**
	 * 设置文档的摘要配置
	 * 
	 * @param documentSummaryXML
	 */
	public void setDocumentSummaryXML(String documentSummaryXML) {
		this.documentSummaryXML = documentSummaryXML;
	}

	/**
	 * 获取文档摘要配置对象
	 * 
	 * @return 文档摘要配置对象
	 * 
	 *         public DocumentSummaryCfg getDocumentSummaryCfg() { if
	 *         (this.getDocumentSummaryXML() != null &&
	 *         this.getDocumentSummaryXML().length() > 0) { return
	 *         (DocumentSummaryCfg)
	 *         XmlUtil.toOjbect(this.getDocumentSummaryXML()); } return new
	 *         DocumentSummaryCfg(); }
	 */

	/**
	 * 设置文档摘要配置对象
	 * 
	 * @param cfg
	 * 
	 *            public void setDocumentSummaryCfg(DocumentSummaryCfg cfg) {
	 *            this.setDocumentSummaryXML(XmlUtil.toXml(cfg)); }
	 */

	/**
	 * 获取应用标识
	 * 
	 * @return 应用标识
	 * @hibernate.property column="APPLICATIONID"
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * 获取摘要集合
	 * 
	 * @return
	 */
	public Collection<SummaryCfgVO> getSummaryCfg() {
		return summaryCfg;
	}

	/**
	 * 设置摘要集合
	 * 
	 * @param summaryCfgs
	 */
	public void setSummaryCfg(Collection<SummaryCfgVO> summaryCfg) {
		this.summaryCfg = summaryCfg;
	}

	/**
	 * 设置应用标识
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 获取排序标识
	 * 
	 * @return 排序标识
	 * 
	 * @hibernate.property column="SORTID"
	 */
	public String getSortId() {
		return sortId;
	}

	/**
	 * 设置排序标识
	 */
	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	/**
	 * 获取按钮对象的Xml
	 * 
	 * @hibernate.property column="ACTIVITYXML" type="text"
	 * @return 按钮对象的Xml
	 */
	public String getActivityXML() {
		if (!getActivitys().isEmpty()) {
			return XmlUtil.toXml(getActivitys());
		}
		return activityXML;
	}

	/**
	 * @SuppressWarnings XmlUtil.toOjbect方法返回类型为Object,类型不确定 设置已转换按钮对象的Xml
	 */
	@SuppressWarnings("unchecked")
	public void setActivityXML(String activityXML) {
		getActivitys().clear();
		if (!StringUtil.isBlank(activityXML)) {
			try {
				getActivitys().addAll(
						(Set<Activity>) XmlUtil.toOjbect(activityXML));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.activityXML = activityXML;
	}

	/**
	 * 获取表单版本.
	 * 
	 * @hibernate.property column="VERSIONS"
	 * @return 表单版本
	 * @uml.property name="version"
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * 设置表单版本
	 * 
	 * @param version
	 *            表单版本
	 * @uml.property name="version"
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * 设置关联按钮集合
	 * 
	 * @return 关联按钮集合
	 */
	public Set<Activity> getActivitys() {
		if (this.activitys == null)
			this.activitys = new TreeSet<Activity>();
		return activitys;
	}

	/**
	 * 设置表单按钮.
	 * 
	 * @param activitys
	 *            activity集合
	 * @uml.property name="activitys"
	 */
	public void setActivitys(Set<Activity> activitys) {
		this.activitys = activitys;
	}

	/**
	 * Form构造函数
	 */
	public Form() {
		_fields = new LinkedHashMap<String, FormField>(20);
		_textparts = new ArrayList<Textpart>(20);
		_elements = new ArrayList<FormElement>(40);
		// _compiledElements = new ArrayList<Object>(50);
	}

	public String toCalctext(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return toCalctext(doc, runner, webUser, false, true);
	}

	public String toCalctext(Document doc, IRunner runner, WebUser webUser,
			boolean hiddenAll, boolean printCopyright) throws Exception {
		StringBuffer template = new StringBuffer();
		template.append("<table cellspacing='5' width='100%'><tr><td>");

		// show element html tags.
		Iterator<FormElement> iter = _elements.iterator();
		DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,this
				.getApplicationid());
		Collection<Document> logdocs = null;
		if (doc != null && doc.getFormname() != null
				&& doc.getFormname().length() > 0) {
			if (this.getType() == FORM_TYPE_NORMAL
					|| this.getType() == FORM_TYPE_SUBFORM) {
				logdocs = process.queryModifiedDocuments(doc);// 查找修改过的字段记录
			}
		}
		while (iter.hasNext()) {
			FormElement fld = iter.next();
			if (fld != null) {
				if (fld instanceof FormField) {
					FormField ff = (FormField) fld;
					try {
						Object object = null;
						Item item = doc.findItem(ff.getName());
						if (item != null) {
							object = item.getValue();
						}
						boolean isModified = ff.isModified(logdocs, object);

						if(fld instanceof IncludeJsFile);
						else {
							template.append("<span id='").append(ff.getName())
									.append("_divid'").append(">");
						}

						String content = fld.toHtmlTxt(doc, runner, webUser);

						// if (content != null && content.trim().length() > 0) {
						// template.append(content);
						// }
						if (isModified && ff != null) {
							if (ff instanceof TextareaField) {
								template
										.append("<table style=\"border:1px solid red;\" id='"
												+ ff.getName()
												+ "_showHisDiv'>");
								template.append("<tr><td nowrap='true'>");
								template.append(content);
								template.append("</td></tr></table>");
							} else if ((ff.getTextType() != null && !ff
									.getTextType().equalsIgnoreCase("hidden"))) {
								template
										.append("<table style=\"border:1px solid red;\" id='"
												+ ff.getName()
												+ "_showHisDiv' >");
								template.append("<tr><td nowrap='true'>");
								template.append(content);
								template.append("</td></tr></table>");
							}
							template
									.append("<script>jQuery(function(){var tooltip = jQuery('#tipsMsgDiv');jQuery('#"
											+ ff.getName()
											+ "_showHisDiv').bind('mouseover',function(e){Mouse(e);");
							template.append("getDatas('" + doc.getId() + "','"
									+ doc.getApplicationid() + "','"
									+ ff.getName() + "','" + ff.getShowName()
									+ "');");
							template
									.append("if((toppos+tooltip.height()>jQuery(document).height())){toppos=toppos-tooltip.height();}");
							template
									.append("if((leftpos+tooltip.width()>jQuery(document).width())){leftpos=leftpos-tooltip.width();}");
							template
									.append("tooltip.css({ top: toppos ,left: leftpos});tooltip.stop().fadeTo(200,1);}).bind('mousemove',function(e){Mouse(e);");
							template
									.append("if((toppos+tooltip.height()>jQuery(document).height())){toppos=toppos-tooltip.height();}");
							template
									.append("if((leftpos+tooltip.width()>jQuery(document).width())){leftpos=leftpos-tooltip.width();}");
							template
									.append("tooltip.css({ top: toppos ,left: leftpos  });}).bind('mouseout',function(){tooltip.stop().fadeOut(200,0, function(){$(this).hide();});});});</script>");
						} else {
							if(fld instanceof IncludeJsFile)
							{
						 		IDefIO cf = Tools.getDefIO();
						 		String tmpStr=cf.getFileContent(content);
								template.append(tmpStr);	
							}
							else
							   template.append(content);
						}

						if(fld instanceof IncludeJsFile);
						else    template.append("</span>");
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				} else {
					template.append(fld.toHtmlTxt(doc, runner, webUser));
				}
			}
		}
		template.append("</td></tr>");
		template.append("</table>");
		return template.toString();
	}

	/**
	 * 获取表单模板的文本内容,返回字符串为重定义的html文本. 若参数bRefreshScript为true时,将添加刷新功能的Script.
	 * 
	 * @param doc
	 *            Document对象
	 * @param runner
	 *            宏脚本类
	 * @param errors
	 *            信息错误集合
	 * @see AbstractRunner#run(String, String)
	 * @return 返回字符串为重定义的html文本
	 * @throws Exception
	 */
	public String toCalctext(Document doc, IRunner runner, WebUser webUser,
			boolean hiddenAll) throws Exception {
		return toCalctext(doc, runner, webUser, hiddenAll, false);
	}

	/**
	 * 获取表单模板的文本内容,返回字符串为重定义的xml文本. 若参数bRefreshScript为true时,将添加刷新功能的Script.
	 * 
	 * @param doc
	 *            Document对象
	 * @param runner
	 *            AbstractRunner
	 * @param errors
	 *            信息错误集合
	 * @see AbstractRunner#run(String, String)
	 * @return 返回字符串为重定义的xml文本
	 * @throws Exception
	 */
	public String toXMLCalctext(Document doc, IRunner runner, WebUser user,
			boolean hiddenAll) throws Exception {
		StringBuffer template = new StringBuffer();

		// show element html tags.
		Iterator<FormElement> iter = _elements.iterator();

		while (iter.hasNext()) {
			FormElement fld = iter.next();
			if (fld != null) {
				if (fld instanceof FormField) {
					FormField ff = (FormField) fld;
					try {
						if (!hiddenAll && ff.isMobile()) {
							String content = ff.toMbXMLText(doc, runner, user);

							if (content != null && content.trim().length() > 0) {
								template.append(content);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				} else {
					// template.append(fld.toMbXMLText(doc, runner));
				}
			}
		}

		return template.toString();
	}

	/**
	 * 获取错误的提示信息
	 * 
	 * @param errors
	 * @return 错误的提示信息
	 */
	public String getErrorMessages(Collection<ValidateMessage> errors) {
		StringBuffer template = new StringBuffer();
		// show error info.
		if (errors != null && errors.size() > 0) {
			template.append("<table class=dybody width='100%'><tr>");
			template
					.append("<td align='right' width='1%' valign='top' nowrap><font color='red'><b>提示信息：</b></font></td>");
			template
					.append("<td align='left' width='99%'><font color='red'><b>");
			Iterator<ValidateMessage> iter = errors.iterator();
			while (iter.hasNext()) {
				ValidateMessage error = iter.next();
				template.append(error.getErrmessage());
			}
			template.append("</font><b></td></tr></table>");
		}
		return template.toString();
	}

	/**
	 * 添加script. 此Script包括刷新字段值的script函数等.
	 * 
	 * @param doc
	 *            Document对象
	 * @param user
	 *            Web用户
	 * @param flowVO
	 *            流程定义对象
	 * @return 返回字符串的内容重定义后的script
	 * @throws Exception
	 */
	public String addScript(Document doc, WebUser user) throws Exception {
		StringBuffer template = new StringBuffer();

		template.append("<script>");

		template.append("var dy_token = true;");

		template.append("function dy_lock(){");
		template
				.append("window.top.document.getElementById('loadingDiv').style.display='';\n");
		template
				.append("window.top.document.getElementById('loadingDivBack').style.display='';\n");
		template.append("}");

		template
				.append("function dy_unlock(){window.top.document.getElementById('loadingDiv').style.display='none';window.top.document.getElementById('loadingDivBack').style.display='none';}");

		template.append("function dy_getFormid(){").append("return '").append(
				getId()).append("';}");

		template.append("function dy_getValuesMap(withParentid){");
		// template.append("var start = new Date().getMilliseconds();");
		template.append("var valuesMap = {};");
		template
				.append("if(document.getElementsByName('_selects')) {valuesMap['_selectsText'] = getCheckedListStr('_selects');}\n");
		for (Iterator<FormField> iter2 = getAllFields().iterator(); iter2
				.hasNext();) {
			Object obj = iter2.next();

			if (obj instanceof ValueStoreField) {
				FormField field = (FormField) obj;
				template.append(field.getValueMapScript() + "\n");
			}
		}
		template
				.append("if(document.getElementsByName('parentid') && document.getElementsByName('parentid').length > 0 && withParentid){valuesMap['parentid']=document.getElementsByName('parentid')[0].value;}\n");
		template
				.append("if(document.getElementsByName('application') && document.getElementsByName('application').length > 0){valuesMap['application']=document.getElementsByName('application')[0].value;}\n");
		template.append("return valuesMap;");
		template.append("}");

		template.append("function dy_refresh(actfield){");
		template.append("try{");
		template.append("if (dy_token){");
		template.append("dy_token=false;");
		template.append("dy_lock();");
		template
				.append("FormHelper.refresh('"
						+ this.getId()
						+ "',actfield,'"
						+ doc.getId()
						+ "','"
						+ user.getId()
						+ "' , dy_getValuesMap(true) , '"
						+ doc.getFlowid()
						// + "' , {}, '" + doc.getFlowid()
						+ "' , function(str){try{eval(str)}catch(ex){alert('form: ' + ex.message)};dy_unlock();dy_token=true;});");
		template.append("}");
		template
				.append("}catch(ex){dy_unlock();alert('form: ' + ex.message);}");
		template.append("}");
		template.append("</script>");

		return template.toString();
	}

	/**
	 * 所有表单的Field 排序
	 * 
	 * @return 所有表单的Field
	 */
	public Collection<FormField> sortFields() {
		// Field 排序
		ArrayList<FormField> tFields = new ArrayList<FormField>();
		Collection<FormField> fields = this.getFields();
		Iterator<FormField> iter3 = fields.iterator();
		while (iter3.hasNext()) {
			FormField field = iter3.next();
			if (field.getName().lastIndexOf("$") > 0) {
				tFields.add(field);
			}
		}
		for (int i = 0; i < tFields.size(); i++) {
			for (int j = i + 1; j < tFields.size(); j++) {
				FormField f1 = tFields.get(i);
				FormField f2 = tFields.get(j);
				if (f1.getSeq() > f2.getSeq()) {
					FormField tmp = tFields.get(i);
					tFields.set(i, tFields.get(j));
					tFields.set(j, tmp);
				}
			}
		}
		return tFields;
	}

	/**
	 * 编译重定义的html文本.
	 * 如果文本内容存在以"<%"开始,"%>"结尾,将"<%"开始,"%>"结尾之间的文本内容,此内容为js(javascript
	 * )内容存储在CalculatePart对象类,
	 * 并将CalculatePart对象添加到ArrayList集合;否则直接将文本内容添加到ArrayList集合.
	 * 不断的循环以上操作.返回一个Collection
	 * 
	 * @param content
	 *            表单文本内容,返回字符串为重定义的html文本
	 * @see OLink.bpm.core.dynaform.form.ejb.CalculatePart#CalculatePart(String)
	 */
	public Collection<?> compileCalctext(String content) {
		// _compiledElements.clear();

		// StringBuffer ready = new StringBuffer();
		ArrayList<Object> ready = new ArrayList<Object>();
		String str;
		int pos1, pos2;
		do {
			pos1 = content.indexOf("<%");
			pos2 = content.indexOf("%>");

			if (pos1 < pos2) {
				String tmp = content.substring(0, pos1);
				// tmp = StringUtil.replace(tmp, "\"", "\\\"");//
				// tmp.replaceAll("\"","\\\"");
				// tmp = StringUtil.replace(tmp, "\r\n", "\\r\\n");

				// ready.append("$HTML.append(\"");
				ready.add(tmp);
				// ready.append("\");\r\n");

				str = content.substring(pos1 + 2, pos2);
				ready.add(new CalculatePart(str));
				// ready.append("\r\n");

				content = content.substring(pos2 + 2, content.length());
			} else {
				if (pos2 > 0) {
					String tmp = content.substring(0, pos2);
					// tmp = StringUtil.replace(tmp, "\"", "\\\"");//
					// tmp.replaceAll("\"","\\\"");
					// tmp = StringUtil.replace(tmp, "\r\n", "\\r\\n");

					// ready.append("$HTML.append(\"");
					ready.add(tmp);
					// ready.append("\");\r\n");

					content = content.substring(pos2, content.length());
				}
			}
		} while (pos1 > 0 && pos2 > 0);
		// content = StringUtil.replace(content, "\"", "\\\"");//
		// tmp.replaceAll("\"","\\\"");
		// content = StringUtil.replace(content, "\r\n", "\\r\\n");

		// while (content.length() > 0) {
		// ready.append("$HTML.append(\"");

		// int posSplit = 20480;
		// posSplit = content.indexOf(" ", posSplit);
		//
		// if (content.length() > posSplit && posSplit > 0) {
		// ready.append(content.substring(0, posSplit));
		// content = content.substring(posSplit);
		// } else {
		// ready.append(content);
		// content = "";
		// }
		//
		// ready.append("\");\r\n");
		//
		// }
		ready.add(content);

		// return ready.toString();
		return ready;
	}

	/**
	 * 根据输入的String返回Form实例
	 * 
	 * @param formStr
	 * @return Form
	 * 
	 */
	public Form newFormInstance(String formStr) {
		return null;
	}

	/**
	 * 添加表单的Field
	 * 
	 * @param field
	 *            FormField
	 * 
	 */
	public void addField(FormField field) throws Exception{//增加 by XGY throws Exception
		if (field != null) {
			// 自动生成Field Name
			if (field.getName() == null || field.getName().length() <= 0) {
				try {
					field.setName(field.getId() + "");
				} catch (Exception e) {
					throw e;
				}
			}

			field.setFormid(this.getId());
			field.set_form(this);
			addSubForms(field);

			_fields.put(field.getId(), field);
			_elements.add(field);
		}
	}

	/**
	 * 添加表单所有的Field
	 * 
	 * @param fields
	 *            Field集合
	 */
	public void addAllField(Collection<FormField> fields)throws Exception{//增加 by XGY throws Exception
		for (Iterator<FormField> iterator = fields.iterator(); iterator
				.hasNext();) {
			FormField field = iterator.next();
			addField(field);
		}
	}

	/**
	 * 添加文本部分
	 * 
	 * @param textpart
	 *            Textpart
	 * @see Textpart#text
	 * 
	 * 
	 */
	public void addTextpart(Textpart textpart) {
		_textparts.add(textpart);
		_elements.add(textpart);
	}

	// public void removeTextpart(Textpart textpart) {
	// _textparts.remove(textpart);
	// _elements.remove(textpart);
	// }

	/**
	 * 根据表单字段名称(fieldId)删除对应的字段(field)
	 * 
	 * param field标识
	 */
	public void removeField(String fieldId) {
		if (fieldId != null) {
			_elements.remove(_fields.get(fieldId));
			_fields.remove(fieldId);

			for (Iterator<Form> iter = subFormMap.values().iterator(); iter
					.hasNext();) {
				Form form = iter.next();
				form.removeField(fieldId);
			}
		}
	}

	/**
	 * 移除所有的Field
	 * 
	 * @param fields
	 *            Field集合
	 */
	public void removeAllField(Collection<FormField> fields) {
		for (Iterator<FormField> iterator = fields.iterator(); iterator
				.hasNext();) {
			FormField field = iterator.next();
			removeField(field.getId());
		}
	}

	/**
	 * 获取表单所有字段集合
	 * 
	 * @return 表单字段集合
	 */
	public Collection<FormField> getFields() {
		return _fields.values();
	}

	/**
	 * 判断是否包含表单
	 * 
	 * @param form
	 * @see OLink.bpm.core.dynaform.form.ejb.form
	 * @return
	 */
	public boolean isContain(Form form) {
		Collection<Form> subForms = getSubForms();
		return subForms.contains(form);
	}

	public boolean equals(Object obj) {
		if (this == null)
			return false;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Form form = (Form) obj;
		if (this.id == null) {
			if (form.id != null)
				return false;
		} else if (!this.id.equals(form.id))
			return false;
		return true;
	}// applicationid != null ? this.applicationid.hashCode():0

	@Override
	public int hashCode() {
		return super.hashCode();
		/*
		 * this.name.hashCode() + this.applicationid.hashCode() +
		 * this.beforopenscript.hashCode() + this.discription.hashCode() +
		 * this.domainid.hashCode() + this.templatecontext.hashCode() 31;
		 */
	}

	/**
	 * 根据表单字段名称（fieldname）,返回表单的字段(Field)
	 * 
	 * @param fieldname
	 *            字段名
	 * @return 表单的Field
	 * @throws Exception
	 * 
	 */
	public FormField findField(String fieldId) {
		return getAllFieldMap().get(fieldId);
	}

	/**
	 * 获得FormField对象
	 * 
	 * @param fieldName
	 *            Field的名
	 * @return
	 */
	public FormField findFieldByName(String fieldName) {
		Collection<FormField> allFields = getAllFields();
		for (Iterator<FormField> iter = allFields.iterator(); iter.hasNext();) {
			FormField field = iter.next();
			if (field.getName().equals(fieldName)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * 创建当前用户的Document. 此根据不同表单模板内容、风格，创建用户相应的Document。
	 * 
	 * @param params
	 *            ParamsTable对象
	 * @see DocumentProcessBean#createNewDoc(Form,
	 *      ParamsTable, WebUser)
	 * @param user
	 *            Web用户
	 * @see Form#createDocument(Document,
	 *      ParamsTable, WebUser)
	 * @return Document对象
	 * @throws Exception
	 */
	public Document createDocument(ParamsTable params, WebUser user)
			throws Exception {
		return createDocument(params, user, true);
	}

	public Document createDocument(ParamsTable params, WebUser user,
			boolean calcAll) throws Exception {
		Document newDoc = new Document();
		newDoc.setId(Tools.getSequence());
		newDoc.setAuthor(user);
		if(!StringUtil.isBlank(user.getDefaultDepartment())){
			newDoc.setAuthorDeptIndex(user.getDepartmentById(user.getDefaultDepartment()).getIndexCode());
		}
		newDoc.setCreated(new Date());
		newDoc.setIstmp(true);

		newDoc.setLastmodifier(user.getId());

		if (this.type == Form.FORM_TYPE_NORMAL) {
			newDoc.setMappingId(newDoc.getId());
		} else if (this.type == Form.FORM_TYPE_NORMAL_MAPPING) {
			newDoc.setMappingId(Tools.getUUID());
		}

		return createDocument(newDoc, params, calcAll, user);
	}

	/**
	 * 创建当前用户的Document. 此根据不同表单模板内容、风格，创建用户相应的Document。
	 * 
	 * @param params
	 *            ParamsTable对象
	 * @param doc
	 *            Document对象
	 * @param user
	 *            Web用户对象
	 * @see Form#createDocument(Document,
	 *      ParamsTable, boolean, WebUser)
	 * @return Document对象
	 * @throws Exception
	 */
	public Document createDocument(Document doc, ParamsTable params,
			WebUser user) throws Exception {
		return createDocument(doc, params, true, user);
	}

	/**
	 * 创建当前用户的Document. 此根据不同表单模板内容、风格，创建用户相应的Document.
	 * 若calcAll参数值为true时,将重新计算Document的ITEM的值.
	 * 
	 * 
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            ParamsTable对象
	 * @param calcAll
	 *            是否重新计算Document的ITEM的值
	 * @param user
	 *            web用户对象
	 * @see Form#createDocument(Document,
	 *      ParamsTable, boolean, WebUser, BillDefiVO) ParamsTable, WebUser)
	 * @return Document对象
	 * @throws Exception
	 */
	public Document createDocument(Document doc, ParamsTable params,
			boolean calcAll, WebUser user) throws Exception {
		if (doc == null) {
			//there is a dead loop
			return createDocument(params, user);
		}

		doc.setDomainid(user.getDomainid());
		doc.setApplicationid(this.getApplicationid());
		/*
		 * since 2.6 Documemt remove flowid if
		 * (!StringUtil.isBlank(params.getParameterAsString("_flowid"))) {
		 * doc.setFlowid(params.getParameterAsString("_flowid")); }
		 */
		if (params.getParameterAsBoolean("isRelate")) {
			if (!StringUtil.isBlank(params.getParameterAsString("parentid"))) {
				String parentid = params.getParameterAsString("parentid");

				doc.setParent(parentid);
				doc.setParent((Document) user.getFromTmpspace(parentid));
			}
		}

		doc.setFormid(this.id);
		doc.setFormname(getFullName());

		addItems(doc, params);

		Monitor monitor = MonitorFactory
				.start("Form.createDocument: (recalculateDocument)");

		recalculateDocument(doc, params, calcAll, user);

		monitor.stop();

		return doc;
	}

	/**
	 * 为Document添加Filed所对应的Item
	 * 
	 * @param doc
	 *            文档
	 * @param params
	 *            页面参数值
	 */
	public void addItems(Document doc, ParamsTable params) {
		Monitor monitor = MonitorFactory
				.start("Form.createDocument: (add all Items)");
		Collection<FormField> fields = getAllFields();
		// 创建Field
		Iterator<FormField> fieldIter = fields.iterator();
		// Collection itemsColls = new ArrayList();
		while (fieldIter.hasNext()) {
			FormField field = fieldIter.next();
			// field.setIssubformvalue(this.getIssubform());
			if (field == null || field.getFieldtype() == null) {
				continue;
			}

			if (!field.getFieldtype().equals(Item.VALUE_TYPE_INCLUDE)) {
				Item item = null;
				//change by lr 2013-11-28 
				//for excel params were changed to uppcase, but form's defination had all kinds
				String tmpFieldName=field.getName().toUpperCase();
				
				
				if (field.getFieldtype().equals(Item.VALUE_TYPE_VARCHAR)){
					String tmpFieldValue=params.getParameterAsString(tmpFieldName);
					if(tmpFieldValue==null||tmpFieldValue=="")
						item = field.createItem(doc, params
								.getParameterAsString(field.getName()));
					else
						item = field.createItem(doc, tmpFieldValue);
				}
				else if (field.getFieldtype().equals(Item.VALUE_TYPE_NUMBER)) {
					String val = params.getParameterAsString(field.getName());
					if(StringUtil.isBlank(val))
						val=params.getParameterAsString(field.getName().toUpperCase());
					if (!StringUtil.isBlank(val)) {
						StringBuffer itemValue = new StringBuffer();
						InputField infield = (InputField) field;
						String numberpatern = infield.getNumberPattern();
						if(!StringUtil.isBlank(numberpatern)){
							DecimalFormat format = new DecimalFormat(numberpatern);
							try {
								itemValue.append(format.parse(val));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						} else {
							itemValue.append(val);
						}
						item = field.createItem(doc, itemValue.toString());
					}else {
						item = field.createItem(doc, params
								.getParameterAsString(field.getName()));
					}
				} else if (field.getFieldtype().equals(Item.VALUE_TYPE_DATE)){
					String tmpFieldValue=params.getParameterAsString(tmpFieldName);
					if(tmpFieldValue==null||tmpFieldValue=="")
						item = field.createItem(doc, params
								.getParameterAsString(field.getName()));
					else
						item = field.createItem(doc, tmpFieldValue);
				}
					
				else if (field.getFieldtype().equals(Item.VALUE_TYPE_TEXT)){
					String tmpFieldValue=params.getParameterAsText(tmpFieldName);
					if(tmpFieldValue==null||tmpFieldValue=="")
						item = field.createItem(doc, params
								.getParameterAsText(field.getName()));
					else
						item = field.createItem(doc, tmpFieldValue);
				}
					
				else
					item = field.createItem(doc, params
							.getParameterAsString(field.getName()));
				// item.setDocid(doc.getId()); //设置Doc id使其与doc相对应
				item.setDocument(doc);
				item.setType(field.getFieldtype());
				item.setFormname(this.getName()); // 设置item所属的form name

				String itemName = item.getName();
				Item tmp = doc.findItem(itemName);
				if (tmp != null) {
					item.setId(tmp.getId());
				} else {
					if (item.getId() == null || item.getId().length() <= 0) {
						try {
							item.setId(Tools.getSequence());
						} catch (Exception e) {
						}
					}
				}

				doc.addItem(item); // 在doc中增加此item
			}
		}
		monitor.stop();
	}

	/**
	 * 获取完整的表名(模块名/表名)
	 * 
	 * @return
	 */
	public String getFullName() {
		String formname = this.name;

		ModuleVO mv = this.getModule();
		if (mv != null) {
			formname = mv.getName() + "/" + formname;
			while (mv.getSuperior() != null) {
				mv = mv.getSuperior();
				formname = mv.getName() + "/" + formname;
			}
			formname = mv.getApplication().getName() + "/" + formname;
		}
		return formname;
	}

	// protected String processListFieldValueText(ParamsTable params,
	// Document doc, String listFieldName) throws Exception {
	// //
	// StringList sl = new StringList();
	//
	// String command = params.getParameterAsString("COMMAND");
	// int rowno = StringUtil.isNumber(params.getParameterAsString("ROWNO")) ?
	// Integer
	// .parseInt(params.getParameterAsString("ROWNO"))
	// : -1;
	//
	// String value_item = doc.getItemValueAsString(listFieldName.substring(0,
	// listFieldName.length() - 1));// item.getVarcharvalue();
	//
	// String value_list = params.getParameterAsString(listFieldName);//
	// item_list.getVarcharvalue();
	//
	// if (value_list != null) {
	// if (value_list.trim().length() > 0) {
	// sl.add(value_list);
	// }
	// }
	//
	// if (command != null && command.equals("APPEND")) {
	// sl.add(value_item);
	// } else if (command != null && command.equals("MODIFY") && rowno >= 0) {
	// sl.replace(rowno, value_item);
	// } else if (command != null && command.equals("REMOVE") && rowno >= 0) {
	// if (rowno >= 0) {
	// sl.remove(rowno);
	// }
	// }
	// // }
	//
	// return sl.toString();
	// }

	/**
	 * 设置Form的模版内容，模版内容为重定义后的html. 通过强化HTML标签及语法，表达动态表单的布局、元素、校验方法、值脚本、选项脚本等。
	 * 
	 * @param template
	 *            Form的模版内容.
	 * @uml.property name="templatecontext"
	 */
	public void setTemplatecontext(String template) throws Exception{//增加 by  XGY
		_fields.clear();
		_textparts.clear();
		_elements.clear();

		this.templatecontext = template;

		try {
			// TemplateProcessVisitor.parseTemplate(this, template);
			// TestHelp.doPrintTest(TemplateProcessVisitor.class.getName());
			TemplateParser.parseTemplate(this, template);

		} catch (Exception e) {
			throw e;
		}
	}

	// public boolean isChanged() {
	// return changed;
	// }

	// public void setChanged(boolean changed) {
	// this.changed = changed;
	// }

	/**
	 * 获取表单主键,主键为UUID,用来标识表单的唯一性
	 * 
	 * @return 表单主键
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @see Tools#getSequence()
	 * @uml.property name="id"
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 设置表单主键.主键为UUID,用来标识表单的唯一性.
	 * 
	 * @param id
	 *            表单主键
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取表单所属的模块(module)
	 * 
	 * @return 与表单相关的模块
	 * @return ModuleVO
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        column="MODULE"
	 * @uml.property name="module"
	 */
	public ModuleVO getModule() {
		return module;
	}

	/**
	 * 设置与表单相关的模块
	 * 
	 * @param module
	 * @uml.property name="module"
	 */
	public void setModule(ModuleVO module) {
		this.module = module;
	}

	/**
	 * 获取表单名称
	 * 
	 * @hibernate.property column="NAME"
	 * @return 表单名字
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置表单名字
	 * 
	 * @param name
	 *            表单名称
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取表单类型 . 四种类型：分别四个常量：
	 * <p>
	 * <code>FORM_TYPE_NORMAL</code> 1：普通(Normal);
	 * <p>
	 * FORM_TYPE_SEARCHFORM 2:查询（searchForm）;
	 * <p>
	 * FORM_TYPE_SUBFORM 3:子表单(subForm);
	 * <p>
	 * FORM_TYPE_HOMEPAGE 4：HOMEPAGE
	 * 
	 * @hibernate.property column="TYPE"
	 * @return 表单类型 四种类型： 分别四个常量：
	 *         <p>
	 *         FORM_TYPE_NORMAL 1：普通(Normal)；
	 *         <p>
	 *         FORM_TYPE_SEARCHFORM 2:查询（searchForm）;
	 *         <p>
	 *         FORM_TYPE_SUBFORM 3:子表单(subForm);
	 *         <p>
	 *         FORM_TYPE_HOMEPAGE 4：HOMEPAGE
	 * @uml.property name="type"
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置表单类型 . 四种类型： 分别四个常量： FORM_TYPE_NORMAL 1：普通(Normal)
	 * FORM_TYPE_SEARCHFORM.
	 * 
	 * @param type
	 * @uml.property name="type"
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取Form在模版内容，模版内容为重定义后的html. 通过强化HTML标签及语法，表达动态表单的布局、元素、校验方法、值脚本、选项脚本等。
	 * 
	 * @hibernate.property column="TEMPLATECONTEXT" type="text"
	 * @return Form在模版内容，模版内容为重定义后的html
	 * @uml.property name="templatecontext"
	 */
	public String getTemplatecontext() {
		return templatecontext;
	}

	/**
	 * Form模版内容结合Document中的ITEM存放的值,返回字符串为重定义后的html。
	 * 返回重定义后的html通过强化HTML标签及语法，表达Document的布局、元素、item值等。 此还设置Document的Field
	 * 的读写权限.
	 * 
	 * @param doc
	 *            文档对象
	 * @param params
	 *            参数
	 * @param isEditMode
	 *            标识文档是否为可编辑(true为可编辑,false为不可编辑)
	 * @param user
	 *            用户
	 * @param errors
	 *            错误信息
	 * @param flowVO
	 *            BillDefiVO(流程定义)对象
	 * @param bRefreshScript
	 * 
	 * @return Form模版内容结合Document的ITEM存放的值为重定义后的html
	 * @throws Exception
	 */
	public String toHtml(Document doc, ParamsTable params, WebUser user,
			Collection<ValidateMessage> errors) throws Exception {
		StringBuffer buffer = new StringBuffer();

		// String application =
		// params.getParameterAsString(Web.REQUEST_ATTRIBUTE_APPLICATION);
		doc.setAudituserid(user.getId());

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(),
				doc.getApplicationid());
		runner.initBSFManager(doc, params, user, errors);

		buffer.append(getErrorMessages(errors));

		String calctext = toCalctext(doc, runner, user);
		Collection<?> js = compileCalctext(calctext);

		for (Iterator<?> iter = js.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof String) {
				buffer.append(element);
			} else {
				CalculatePart part = (CalculatePart) element;

				StringBuffer label = new StringBuffer();
				label.append("Form(").append(getId()).append(")." + getName())
						.append(".CalculatePart");
				Object result = runner.run(label.toString(), part.text);
				if (result != null) {
					buffer.append(result);
				}
			}
		}

		buffer.append(addScript(doc, user));
		return buffer.toString();
	}

	/**
	 * 获取表单在手机显示的XML
	 * 
	 * @param doc
	 * @param params
	 * @param user
	 * @param errors
	 * @param evt
	 * @return
	 * @throws Exception
	 */
	public String toMbXML(Document doc, ParamsTable params, WebUser user,
			Collection<ValidateMessage> errors, Environment evt)
			throws Exception {

		return toMbXML(doc, params, new ArrayList<Object>(), user, errors, evt,
				false);
	}

	/**
	 * 获取表单在手机显示的XML
	 * 
	 * @param doc
	 * @param params
	 * @param columnNames
	 * @param user
	 * @param errors
	 * @param evt
	 * @param refresh
	 *            是否是刷新表单
	 * @return
	 * @throws Exception
	 */
	public String toMbXML(Document doc, ParamsTable params,
			Collection<?> columnNames, WebUser user,
			Collection<ValidateMessage> errors, Environment evt, boolean refresh)
			throws Exception {
		doc.setAudituserid(user.getId());
		String viewid = (String) params.getParameter("_viewid");
		String parentid = (String) params.getParameter("parentid");
		String mapStr = params.getParameterAsString("_mapStr");
		String application = params
				.getParameterAsString(Web.REQUEST_ATTRIBUTE_APPLICATION);

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(),
				application);
		runner.initBSFManager(doc, params, user, errors);

		StringBuffer template = new StringBuffer();
		String name = getDiscription();
		if (name == null || name.trim().length() <= 0
				|| name.trim().equals("null"))
			name = getName();
		template.append("<").append(MobileConstant.TAG_FORM).append(" ")
				.append(MobileConstant.ATT_TITLE).append("='").append(
						HtmlEncoder.encode(name)).append("'>");
		template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ")
				.append(MobileConstant.ATT_NAME).append(
						"='application'>" + application + "</").append(
						MobileConstant.TAG_HIDDENFIELD).append(">");
		if (!StringUtil.isBlank(doc.getFlowid())) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_flowid'>" + doc.getFlowid() + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		} else if (!StringUtil.isBlank(getOnActionFlow())) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_flowid'>" + getOnActionFlow() + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (!StringUtil.isBlank(doc.getStateid())) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_stateid'>" + doc.getStateid() + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (refresh) {
			Iterator<FormElement> iter = _elements.iterator();
			if (columnNames != null && columnNames.size() > 0) {
				String[] names = new String[columnNames.size()];
				int i = 0;
				for (Iterator<?> iterator = columnNames.iterator(); iterator
						.hasNext();) {
					names[i] = (String) iterator.next();
					i++;
				}
				while (iter.hasNext()) {
					try {
						FormElement fld = iter.next();
						if (fld instanceof FormField) {
							FormField ff = (FormField) fld;
							if (!ff.isMobile()) {
								continue;
							}
							if (ff.isCalculateOnRefresh()
									|| ff.isRefreshOnChanged()) {
								String content = ff.toMbXMLText(doc, runner,
										user);
								if (content != null
										&& content.trim().length() > 0) {
									template.append(content);
								}
							} else {
								for (i = 0; i < names.length; i++) {
									if (ff.getName().equalsIgnoreCase(names[i])) {
										String content = ff.toMbXMLText(doc,
												runner, user);
										if (content != null
												&& content.trim().length() > 0) {
											template.append(content);
										}
										break;
									}
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				}
			}
			template.append("</").append(MobileConstant.TAG_FORM).append(">");
			return template.toString();
		}
		// add activity xml text.
		Collection<Activity> acts = this.getActivitys();
		if (acts != null) {
			StateMachineHelper helper = new StateMachineHelper(doc);
			for (Iterator<Activity> iter = acts.iterator(); iter.hasNext();) {
				Activity act = iter.next();
				if (act.isHidden(runner, this, doc, user, ResVO.FORM_TYPE)) {
					continue;
				}
				// 打印Activity不生成XML
				if (act.getType() == ActivityType.PRINT
						|| act.getType() == ActivityType.PRINT_WITHFLOWHIS) {
					continue;
				}
				template.append("<").append(MobileConstant.TAG_ACTION).append(
						" ").append(MobileConstant.ATT_ID).append("='");
				template.append(act.getId());
				template.append("' ").append(MobileConstant.ATT_NAME).append(
						"='");
				String ntemp = act.getName();
				String actname = MultiLanguageProperty.getProperty(
						MultiLanguageProperty.getName(2), ntemp, ntemp);
				actname = actname.replaceAll("&", "&amp;");
				template.append(actname);
				template.append("' ").append(MobileConstant.ATT_TYPE).append(
						"='");
				template.append(act.getType() + "'");

				StringBuffer label = new StringBuffer();
				label.append("Form(").append(getId()).append(")." + getName())
						.append(".Activity.HiddenScript");
				Object result = runner.run(label.toString(), act
						.getHiddenScript());
				if (result != null && result instanceof Boolean) {
					if (((Boolean) result).booleanValue()) {
						template.append(" ").append(MobileConstant.ATT_HIDDEN)
								.append(" = 'true' ");
					}
				}
				template.append(">");
				if (act.getType() == ActivityType.WORKFLOW_PROCESS) {

					template.append(helper.toFlowXMLText(doc, user));

				}
				template.append("</").append(MobileConstant.TAG_ACTION).append(
						">");
				if (act.getType() == ActivityType.WORKFLOW_PROCESS) {
					template.append("<").append(MobileConstant.TAG_HIDDENFIELD)
							.append(" ").append(MobileConstant.ATT_NAME)
							.append("='_flowid'>");
					template.append(act.getOnActionFlow());
					template.append("</")
							.append(MobileConstant.TAG_HIDDENFIELD).append(">");

					if (doc.getFlowid() != null) {
						template.append("<").append(MobileConstant.TAG_ACTION)
								.append(" ").append(MobileConstant.ATT_ID)
								.append(" = '");
						template.append(Tools.getSequence());
						template.append("' ").append(MobileConstant.ATT_NAME)
								.append("='{*[Flow]*}{*[Diagram]*}' ").append(
										MobileConstant.ATT_TYPE).append(" = '");
						template.append(ActivityType.DOCUEMNT_VIEWFLOWIMAGE);
						template.append("'>");
						template.append("</").append(MobileConstant.TAG_ACTION)
								.append(">");
						if (helper.isShowHis(doc.getFlowid(), doc.getId(), doc
								.getApplicationid())) {
							template.append("<").append(
									MobileConstant.TAG_ACTION).append(" ")
									.append(MobileConstant.ATT_ID).append(
											" = '");
							template.append(Tools.getSequence());
							template.append("' ").append(
									MobileConstant.ATT_NAME).append(
									"='{*[Flow]*}{*[History]*}' ").append(
									MobileConstant.ATT_TYPE).append(" = '");
							template.append("824");
							template.append("'>");
							template.append("<").append(
									MobileConstant.TAG_PARAMETER).append(" ")
									.append(MobileConstant.ATT_NAME).append(
											"='_docid'>" + doc.getId() + "</")
									.append(MobileConstant.TAG_PARAMETER)
									.append(">");
							template.append("</").append(
									MobileConstant.TAG_ACTION).append(">");
						}
					}
				}else{
					if (acts != null && acts.size() > 0) {
						for (Iterator<Activity> iter1 = acts.iterator(); iter1.hasNext();) {
							Activity act1 = iter1.next();
							if (!StringUtil.isBlank(act1.getOnActionFlow())) {
								template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ").append(
										MobileConstant.ATT_NAME).append("='_flowid'>");
								template.append(act1.getOnActionFlow());
								template.append("</").append(MobileConstant.TAG_HIDDENFIELD).append(">");
								break;
							}
						}
					}
				}
			}
		}

		String isRefresh = params.getParameterAsString("refresh");
		if (isRefresh != null && isRefresh.trim().equals("true")) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='refresh'>true</").append(MobileConstant.TAG_HIDDENFIELD)
					.append(">");
		}
		String isRelate = params.getParameterAsString("isRelate");
		if (isRelate != null && isRelate.trim().equals("true")) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='isRelate'>true</")
					.append(MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (doc.getStateLabel() != null && !doc.getStateLabel().equals("null")
				&& !doc.getStateLabel().equals("none")) {
			template.append("<").append(MobileConstant.TAG_LABELFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='' >{*[Flow]*}{*[State]*}:" + doc.getStateLabel() + "</")
					.append(MobileConstant.TAG_LABELFIELD).append(">");
		}
		String relateid = params.getParameterAsString("relateid");
		if (relateid != null) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='relateid' >" + relateid + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (viewid != null) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_viewid' >" + viewid + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (mapStr != null) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='_mapStr'>" + mapStr + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		if (parentid != null) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append(
					"='parentid' >" + parentid + "</").append(
					MobileConstant.TAG_HIDDENFIELD).append(">");
		}
		// 加入DOCID
		if (doc != null && doc.getId() != null) {
			template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(
					" ").append(MobileConstant.ATT_NAME).append("='_docid'>");
			template.append(doc.getId());
			template.append("</").append(MobileConstant.TAG_HIDDENFIELD)
					.append(">");
		}

		// Formid
		template.append("<").append(MobileConstant.TAG_HIDDENFIELD).append(" ")
				.append(MobileConstant.ATT_NAME).append("='_formid'>");
		template.append(this.getId());
		template.append("</").append(MobileConstant.TAG_HIDDENFIELD)
				.append(">");

		Iterator<FormElement> iter = _elements.iterator();
		while (iter.hasNext()) {
			try {
				FormElement fld = iter.next();
				if (fld instanceof FormField) {
					FormField ff = (FormField) fld;
					if (ff.isMobile()) {
						String content = ff.toMbXMLText(doc, runner, user);

						if (content != null && content.trim().length() > 0) {
							template.append(content);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}

		template.append("</").append(MobileConstant.TAG_FORM).append(">");
		return template.toString();
	}

	/**
	 * 
	 * Form模版内容结合Document的ITEM存放的值,返回重定义后的打印html.
	 * 若有错误信息,也将组合Form模版内容结合Document的ITEM存放的值,返回重定义后为打html.
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @param errors
	 *            错误信息集合
	 * @return Form模版内容结合Document的ITEM存放的值,返回重定义后的打印html
	 * @throws Exception
	 */

	public String toPrintHtml(Document doc, ParamsTable params, WebUser user,
			Collection<ValidateMessage> errors) throws Exception {
		StringBuffer htmlBuffer = new StringBuffer();

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(),
				doc.getApplicationid());
		params.setParameter("parentid", doc.getId());
		runner.initBSFManager(doc, params, user, errors);

		String calctext = toPrintCalctext(doc, runner, user);
		htmlBuffer.append(toFinalHtml(calctext, runner));

		return htmlBuffer.toString();
	}

	/**
	 * 解析Form模版内容结合Document的ITEM存放的值,返回重定义后的PDF所支持的html
	 * 
	 * @param doc
	 *            文档
	 * @param params
	 *            参数
	 * @param user
	 *            用户
	 * @param errors
	 *            错误信息
	 * @return PDF支持的HTML
	 * @throws Exception
	 */
	public String toPdfHtml(Document doc, ParamsTable params, WebUser user,
			Collection<ValidateMessage> errors) throws Exception {
		StringBuffer htmlBuffer = new StringBuffer();

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(),
				doc.getApplicationid());
		runner.initBSFManager(doc, params, user, errors);

		String calctext = toPdfCalctext(doc, runner, user);

		// htmlBuffer.append("<!DOCTYPE html[<!ENTITY nbsp \" \">]>\n");
		htmlBuffer.append("<html>");
		htmlBuffer.append("<head>");
		String cssRealPath = Environment.getInstance().getRealPath(
				"resource/css/main-front.css");
		cssRealPath = "file://"
				+ cssRealPath.replaceFirst(":", "|").replaceAll("//", "/");
		htmlBuffer.append("<link rel='stylesheet' href='" + cssRealPath
				+ "' type=\"text/css\"/>");
		htmlBuffer.append("</head>");

		htmlBuffer.append("<body style=\"font-family:'SimSun'\">");
		htmlBuffer.append(toFinalHtml(calctext, runner));
		htmlBuffer.append("</body>");
		htmlBuffer.append("</html>");

		return htmlBuffer.toString();
	}

	/**
	 * 输出重写义后的HTML,详细请参考compileCalctext方法
	 * 
	 * @param calctext
	 * @param runner
	 * @return 表单文本内容,返回字符串为重定义的html文本
	 * @throws Exception
	 */
	protected String toFinalHtml(String calctext, IRunner runner)
			throws Exception {
		StringBuffer htmlBuffer = new StringBuffer();

		Collection<?> js = compileCalctext(calctext);
		for (Iterator<?> iter = js.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof String) {
				htmlBuffer.append(element);
			} else {
				CalculatePart part = (CalculatePart) element;

				StringBuffer label = new StringBuffer();
				label.append("Form(").append(getId()).append(")." + getName())
						.append(".CalculatePart");
				Object result = runner.run(label.toString(), part.text);
				if (result != null) {
					htmlBuffer.append(result);
				}
			}
		}
		return htmlBuffer.toString();
	}

	/**
	 * 输出重写义后的PDF所支持HTML标记,
	 * 
	 * @param doc
	 *            文档
	 * @param runner
	 *            宏脚本类
	 * @param webUser
	 *            用户
	 * @return 重写义后的PDF所支持HTML标记
	 * @throws Exception
	 */
	public String toPdfCalctext(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer template = new StringBuffer();
		// show element html tags.
		Iterator<FormElement> iter = _elements.iterator();

		while (iter.hasNext()) {
			FormElement fld = iter.next();
			if (fld != null) {
				if (fld instanceof FormField) {
					try {
						String content = fld.toPdfHtmlTxt(doc, runner, webUser);

						if (content != null && content.trim().length() > 0) {
							// if (content.toUpperCase().indexOf("<IMG ") > -1)
							// {
							// if (content.toLowerCase().contains(" src=")) {
							// if (!content.toLowerCase().startsWith(
							// "http:")) {
							// int index = content.toLowerCase()
							// .indexOf(" src=");
							// if (index > -1) {
							// String end = content
							// .substring(index + 6);
							// String baseUrl = Environment
							// .getInstance().getBaseUrl();
							// if (baseUrl != null
							// && end.startsWith("/")
							// && baseUrl.endsWith("/")) {
							// baseUrl = baseUrl.substring(0,
							// baseUrl.length() - 1);
							// }
							// content = content.substring(0,
							// index + 6)
							// + baseUrl + end;
							// }
							// }
							// }
							// }
							template.append(content);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					template.append(fld.toPdfHtmlTxt(doc, runner, webUser));
				}
			}
		}
		return template.toString();
	}

	/**
	 * Form模版内容结合Document的ITEM存放的值,返回重定义后的打印html.
	 * 
	 * @param doc
	 *            文档对象
	 * @param runner
	 *            宏脚本类
	 * @param webUser
	 *            web用户
	 * @return 重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintCalctext(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer template = new StringBuffer();
		// show element html tags.
		Iterator<FormElement> iter = _elements.iterator();

		while (iter.hasNext()) {
			FormElement fld = iter.next();
			if (fld != null) {
				if (fld instanceof FormField) {
					try {
						String content = fld.toPrintHtmlTxt(doc, runner,
								webUser);

						if (content != null && content.trim().length() > 0) {
							template.append(content);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					template.append(fld.toPrintHtmlTxt(doc, runner, webUser));
				}
			}
		}
		return template.toString();
	}

	/**
	 * 重新计算Document的item的值,返回Document对象. 如果有错误,抛出异常并有相应脚本错误信息.
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            参数对象
	 * @param user
	 *            web用户
	 * @return Document对象
	 * @throws Exception
	 */
	public Document recalculateDocument(Document doc, ParamsTable params,
			WebUser user) throws Exception {
		return recalculateDocument(doc, params, true, user);
	}

	/**
	 * 执行Field脚本,重新计算Document的item的值,返回Document对象. 若Field脚本输入不合法时返回异常错误信息。
	 * 
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            参数对象
	 * @param calcAll
	 *            是否重新计算Document的ITEM的值
	 * @param user
	 *            web用户
	 * @return Document对象
	 * @throws Exception
	 */
	public Document recalculateDocument(Document doc, ParamsTable params,
			boolean calcAll, WebUser user) throws Exception {

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(),
				this.getApplicationid());
		runner.initBSFManager(doc, params, user, null);

		Iterator<FormField> iter = getAllFields().iterator();
		FormField field = null;
		try {
			while (iter.hasNext()) {
				field = iter.next();
				if (field != null && field.getFieldtype() != null
						&& (field.isCalculateOnRefresh() || calcAll)) {
					field.recalculate(runner, doc, user);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			log.info(StringUtil.dencodeHTML(field.getValueScript()));
			log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
			throw new Exception("Field:" + field.getName() + "-值脚本出错 <br>"
					+ ex.getMessage());
		}

		return doc;
	}

	/**
	 * 执行表单Field校验脚本, 测试Field输入合法性，如果不合法返回相应脚本错误信息，否则返回为空.
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @param isSubform
	 * 
	 * @return 错误信息
	 * @throws Exception
	 */
	public Collection<ValidateMessage> validate(Document doc,
			ParamsTable params, WebUser user, boolean isSubform)
			throws Exception {
		Collection<ValidateMessage> errs = new ArrayList<ValidateMessage>();

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(),
				user.getDefaultApplication());
		// 测试Field输入合法性
		runner.initBSFManager(doc, params, user, errs);

		Iterator<FormField> iter = getAllFields().iterator();
		while (iter.hasNext()) {
			FormField field = iter.next();
			if (field != null) {
				try {
					ValidateMessage msg = field.validate(runner, doc);
					if (msg != null) {
						errs.add(msg);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					throw new Exception("Field:" + field.getName()
							+ "-校验脚本出错！ <br>" + ex.getMessage());
				}
			}
		}

		return errs;
	}

	/**
	 * 执行表单字段校验脚本，测试Field输入合法性，如果不合法返回相应脚本错误信息，否则返回为空.
	 * 
	 * @param doc
	 *            对象
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * @return 错误信息
	 * @throws Exception
	 */
	public Collection<ValidateMessage> validate(Document doc,
			ParamsTable params, WebUser user) throws Exception {
		return validate(doc, params, user, false);
	}

	// public String compile(Document currdoc, ParamsTable params, WebUser user,
	// String js) {
	// try {
	// AbstractRunner runner = JavaScriptFactory.getInstance(params
	// .getSessionid(), user.getApplicationid());
	// runner.initBSFManager(currdoc, params, user, null);
	//
	// StringBuffer label = new StringBuffer();
	// label.append(getName()).append(".").append("COMPILE");
	// runner.run(label.toString(), js);
	// } catch (Exception ex) {
	// return ex.getMessage();
	// }
	// return "success";
	// }

	/**
	 * 表单描述.
	 * 
	 * @hibernate.property column="DISCRIPTION" type = "text"
	 * @return 表单描述
	 * @uml.property name="discription"
	 */
	public String getDiscription() {
		return HtmlEncoder.encode(discription);
	}

	/**
	 * 设置表单描述.
	 * 
	 * @param discription
	 *            表单描述
	 * @uml.property name="discription"
	 */
	public void setDiscription(String discription) {
		this.discription = discription;
	}

	/**
	 * 获取最后的修改用户. 此为修改表单的最后的用户
	 * 
	 * @hibernate.many-to-one class="UserVO"
	 *                        column="LASTMODIFIER"
	 * @uml.property name="lastmodifier"
	 */
	public UserVO getLastmodifier() {
		return lastmodifier;
	}

	/**
	 * 设置最后修改用户. 此为修改表单的最后的用户.
	 * 
	 * @param lastmodifier
	 * @uml.property name="lastmodifier"
	 */
	public void setLastmodifier(UserVO lastmodifier) {
		this.lastmodifier = lastmodifier;
	}

	/**
	 * 最后修改日期. 此为记录表单修改的最后日期.
	 * 
	 * @hibernate.property column="LASTMODIFYTIME"
	 * @return 最后的修改日期
	 * @uml.property name="lastmodifytime"
	 */
	public Date getLastmodifytime() {
		return lastmodifytime;
	}

	/**
	 * 设置最后的修改日期. 此为记录表单修改的最后日期.
	 * 
	 * @param lastmodifytime
	 *            最后的修改日期
	 * @uml.property name="lastmodifytime"
	 */
	public void setLastmodifytime(Date lastmodifytime) {
		this.lastmodifytime = lastmodifytime;
	}

	/**
	 * 返回引用的相关样式库(StyleRepository) 此样式是设置表单个性的风格.
	 * 
	 * @return 相关样式库对象(StyleRepositoryVO)
	 * @hibernate.many-to-one 
	 *                        class="StyleRepositoryVO"
	 *                        column="STYLE"
	 * @uml.property name="style"
	 */
	public StyleRepositoryVO getStyle() {
		return style;
	}

	/**
	 * 是否显示日志
	 * 
	 * @hibernate.property column="SHOWLOG"
	 * @return 是否显示日志
	 */
	public boolean isShowLog() {
		return showLog;
	}

	/**
	 * 设置是否显示日志
	 * 
	 * @param displayLog
	 */
	public void setShowLog(boolean showLog) {
		this.showLog = showLog;
	}

	/**
	 * 设置引用的相关样式库(StyleRepository). 此样式是设置表单的风格(CSS).
	 * 
	 * @param style
	 *            样式库对象
	 * @uml.property name="style"
	 */
	public void setStyle(StyleRepositoryVO style) {
		this.style = style;
	}

	/**
	 * 返回表单类型名 表单四种类型名{1:普通(NORMAL),2:子表单(SUBFORM),3:查询表单(SEARCHFORM),4:
	 * 主页(HOMEPAGE) }
	 * 
	 * @return 表单类型名
	 */
	public String getTypeName() {
		for (int i = 0; i < formType.length; i++) {
			if (this.type == formType[i]) {
				return Form.formTypeName[i];
			}
		}
		return "";
	}

	/**
	 * 取出所有的表单字段,并保存到一个Map中
	 * 
	 * @return
	 */
	protected Map<String, FormField> getAllFieldMap() {
		Map<String, FormField> map = new LinkedHashMap<String, FormField>();
		map.putAll(_fields);
		for (Iterator<Form> iter = subFormMap.values().iterator(); iter
				.hasNext();) {
			Form form = iter.next();
			map.putAll(form.getAllFieldMap());
		}

		return map;
	}

	/**
	 * 获取查询表单的字段
	 * 
	 * @return 字段集合<java.util.Map>
	 */
	protected Map<String, FormField> getAllSerachFormField() {
		Map<String, FormField> map = new LinkedHashMap<String, FormField>();
		map.putAll(_fields);
		for (Iterator<Form> iter = subFormMap.values().iterator(); iter
				.hasNext();) {
			Form serachform = iter.next();
			if (serachform.getType() == FORM_TYPE_SEARCHFORM)
				map.putAll(serachform.getAllFieldMap());
		}
		return map;
	}

	/**
	 * 获取所有表单域(Field)包括子表单
	 * 
	 * @return Field列表
	 */
	public Collection<FormField> getAllFields() {
		return getAllFieldMap().values();
	}

	/**
	 * 获取表单中所有所属为手机的Field
	 * 
	 * @return <java.util.Collection>
	 */
	public Collection<FormField> getAllMobileFields() {
		Collection<FormField> fields = new ArrayList<FormField>();
		Collection<FormField> allFields = getAllFields();
		for (Iterator<FormField> iterator = allFields.iterator(); iterator
				.hasNext();) {
			FormField f = iterator.next();
			if (f.isMobile()) {
				fields.add(f);
			}
		}
		return fields;
	}

	/**
	 * 获取表单中所有的Field的名字
	 * 
	 * @return <java.util.Collection>
	 */
	public Collection<String> getAllFieldNames() {
		Collection<String> nameList = new ArrayList<String>();
		Collection<FormField> allFieldList = getAllFields();
		for (Iterator<FormField> iter = allFieldList.iterator(); iter.hasNext();) {
			FormField field = iter.next();
			nameList.add(field.getName());
		}
		return nameList;
	}

	/**
	 * 取readonly的属性字段并为subform,如果field的字段类型为隐藏，此时查询和重置按钮不显示
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	public boolean checkDisplayType() throws Exception {
		StringBuffer buffer = new StringBuffer();
		Collection<FormField> allFieldList = getAllSerachFormField().values();
		for (Iterator<FormField> iter = allFieldList.iterator(); iter.hasNext();) {
			FormField field = iter.next();
			if (field.getTextType() != null
					&& field.getTextType().equalsIgnoreCase("hidden")) {
				buffer.append("0,");
			} else {
				buffer.append("1,");
			}
		}
		return buffer.indexOf("1") != -1;
	}

	/**
	 * 获取所有要保存值到数据库的表单域(ValueStoreField)
	 * 
	 * @return 获取所有要保存值到数据库的表单域(ValueStoreField)
	 */
	public Collection<FormField> getValueStoreFields() {
		Collection<FormField> rtn = new ArrayList<FormField>();

		for (Iterator<FormField> iter = getAllFields().iterator(); iter
				.hasNext();) {
			FormField field = iter.next();
			if (field instanceof ValueStoreField) {
				rtn.add(field);
			}
		}
		return rtn;
	}

	/**
	 * 深度克隆对象(clone object)
	 * 
	 * @return Object
	 */
	public Object clone() {
		Object obj = null;
		try {
			if (ObjectUtil.clone(this) != null) {
				obj = ObjectUtil.clone(this);
			} else
				obj = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 增加子表单
	 * 
	 * @param field
	 *            FormField
	 */
	protected void addSubForms(FormField field) throws Exception{
		if (field instanceof TabField) {
			try {
				Collection<Form> forms = ((TabField) field).getForms();
				for (Iterator<Form> iter = forms.iterator(); iter.hasNext();) {
					Form form = iter.next();
					if (!this.equals(form)) {
						addSubForm(form);
					}
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

	/**
	 * 添加Tab组件所包含的表单
	 * 
	 * @param form
	 */
	public void addSubForm(Form form) {
		subFormMap.put(form.getId(), form);
	}

	/**
	 * 返回Tab组件所包含的表单
	 * 
	 * @return 包含的表单集合
	 */
	public Collection<Form> getSubForms() {
		return subFormMap.values();
	}

	public static class CalculatePart {
		public String text;

		CalculatePart(String t) {
			text = t;
		}
	}

	/**
	 * 获取Form所关联的流程
	 * 
	 * @return 流程
	 */
	public String getOnActionFlow() {
		if (activitys != null && activitys.size() > 0) {
			for (Iterator<Activity> iter = activitys.iterator(); iter.hasNext();) {
				Activity act = iter.next();
				if (!StringUtil.isBlank(act.getOnActionFlow())) {
					return act.getOnActionFlow();
				}
			}
		}
		return "";
	}

	/**
	 * 根据按钮类型获取按钮
	 * 
	 * @param type
	 *            按钮类型
	 * @return 按鈕对象
	 */
	public Activity getActivityByType(int type) {
		if (activitys != null && activitys.size() > 0) {
			for (Iterator<Activity> iter = activitys.iterator(); iter.hasNext();) {
				Activity act = iter.next();
				if (act.getType() == type) {
					return act;
				}
			}
		}
		return null;
	}

	/**
	 * 根据按钮标识获取按钮
	 * 
	 * @param type
	 *            按钮类型
	 * @return 按鈕对象
	 */
	public Activity findActivity(String id) {
		Set<Activity> activitySet = getActivitys();
		for (Iterator<Activity> iterator = activitySet.iterator(); iterator
				.hasNext();) {
			Activity activity = iterator.next();
			if (activity.getId().equals(id)) {
				activity.setParentForm(this.getId());
				activity.setApplicationid(this.getApplicationid());
				return activity;
			}
		}

		FormField buttonField = this.findField(id);
		if (buttonField instanceof ButtonField) {
			return ((ButtonField) buttonField).getActivity();
		}

		return null;
	}

	/**
	 * 短信关联名
	 * 
	 * @return 短信关联名
	 */
	public String getRelationName() {
		return relationName;
	}

	/**
	 * 
	 * 短信关联字段列表
	 * 
	 * @return 短信关联字段列表
	 */
	public String getRelationText() {
		return relationText;
	}

	/**
	 * 短信填单时，保存后是否马上启动流程
	 * 
	 * @return 短信填单时，保存后是否马上启动流程
	 */
	public boolean isOnSaveStartFlow() {
		return onSaveStartFlow;
	}

	/**
	 * 设置短信关联名,全局唯一性
	 * 
	 * @param relationName
	 *            关联名
	 */
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	/**
	 * 设置关联字段列表,全局唯一性
	 * 
	 * @param relationText
	 */
	public void setRelationText(String relationText) {
		this.relationText = relationText;
	}

	/**
	 * 设置短信填单时，保存后是否马上启动流程
	 * 
	 * @param onSaveStartFlow
	 *            boolean
	 */
	public void setOnSaveStartFlow(boolean onSaveStartFlow) {
		this.onSaveStartFlow = onSaveStartFlow;
	}

	/**
	 * 增加组件
	 * 
	 * @param component
	 */
	public void addComponent(Component component) {
		_components.put(component.getId(), component);
	}

	/**
	 * 查询组件
	 * 
	 * @param id
	 *            组件标识
	 * @return 组件对象
	 */
	public Component findComponent(String id) {
		return _components.get(id);
	}

	/**
	 * 查询子表单
	 * 
	 * @return 子表单集合
	 */
	public Map<String, Form> getSubFormMap() {
		return subFormMap;
	}

	/**
	 * 设置子表单
	 * 
	 * @param subFormMap
	 */
	public void setSubFormMap(Map<String, Form> subFormMap) {
		this.subFormMap = subFormMap;
	}

	public String getMappingStr() {
		return mappingStr;
	}

	public void setMappingStr(String mappingStr) {
		this.mappingStr = mappingStr;
	}

	/**
	 * 获取包含的视图(即子表单视图)列表
	 * 
	 * @return
	 */
	public Collection<View> getIncludeViewList() {
		Collection<View> rtn = new ArrayList<View>();
		try {
			Collection<FormField> fields = this.getAllFields();
			for (Iterator<FormField> iterator = fields.iterator(); iterator
					.hasNext();) {
				FormField field = iterator.next();
				if (field instanceof IncludeField) {
					View view = ((IncludeField) field).getIncludeView();
					if (view != null) {
						rtn.add(view);
					}
				}
			}
		} catch (Exception e) {
			log.error("getIncludeViewList", e);
		}

		return rtn;
	}

	public TableMapping getTableMapping() {
		if (mapping == null) {
			// 数据库映射
			mapping = new TableMapping(this);
		}

		return mapping;
	}

	public String getSimpleClassName() {
		return this.getClass().getSimpleName();
	}

	public String getIsopenablescript() {
		return isopenablescript;
	}

	public void setIsopenablescript(String isopenablescript) {
		this.isopenablescript = isopenablescript;
	}

	public String getIseditablescript() {
		return iseditablescript;
	}

	public void setIseditablescript(String iseditablescript) {
		this.iseditablescript = iseditablescript;
	}
}

