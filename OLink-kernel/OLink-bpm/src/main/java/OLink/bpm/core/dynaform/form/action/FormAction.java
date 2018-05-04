package OLink.bpm.core.dynaform.form.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.form.ejb.Confirm;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.mapping.ColumnMapping;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.sysconfig.ejb.CheckoutConfig;
import OLink.bpm.core.table.model.NeedConfirmException;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.page.ejb.PageProcess;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @author Marky
 */
public class FormAction<E> extends BaseAction<E> {

	private static final long serialVersionUID = -728579823137582211L;

	public String get_formid() {
		return _formid;
	}

	public void set_formid(String formid) {
		_formid = formid;
	}

	private String moduleid;

	private String applicationid;

	private Collection<Form> _formSelect;

	private String _formname;

	private String _styleid;

	private String[] _formact;

	private String flowlist;

	private String disField;

	private String workflowname;

	private String _formid;

	private Collection<Confirm> confirms = new ArrayList<Confirm>();
	
	private String checkoutConfig;

	// private DocumentSummaryCfg docSummaryCfg;

	/**
	 * "_FORMACLIST" 表单Form的Activity(按钮) 集合列表.
	 */
	public static Map<String, String> _FORMACLIST = new TreeMap<String, String>();

	/**
	 * "_VIEWACLIST" view的Activity(按钮)集合列表.
	 */
	public static Map<String, String> _VIEWACLIST = new TreeMap<String, String>();

	/**
	 * view打开类型.
	 */
	private static Map<Integer, String> _OPENTYPE = new TreeMap<Integer, String>();

	public static Map<String, String> _WORKFLOW = new TreeMap<String, String>();

	static {
		_OPENTYPE.put(Integer.valueOf(View.OPEN_TYPE_NORMAL), "{*[Normal]*}");
		_OPENTYPE.put(Integer.valueOf(View.OPEN_TYPE_POP),
				"{*[Open_Pop_Window]*}");
	}

	static {
		// _ACLIST.put("0", "Please Choose");
		_VIEWACLIST.put("1", "{*[Query]*}");
		_VIEWACLIST.put("2", "{*[New]*}");
		_VIEWACLIST.put("3", "{*[Delete]*}");
		_VIEWACLIST.put("4", "{*[Modify]*}");
		// _ACLIST.put("6", "脚本处理");
		// _ACLIST.put("7", "DOCUMENT_MODIFY");
		_FORMACLIST.put("8", "{*[Close]*}");
		_FORMACLIST.put("9", "{*[Save&Close]*}");
		_FORMACLIST.put("10", "{*[Back]*}");
		_FORMACLIST.put("11", "{*[Save&Back]*}");
		_FORMACLIST.put("12", "{*[Save&New]*}");

		_WORKFLOW.put("5", "{*[Workflow]*}{*[Process]*}");
	}

	/**
	 * Form类型. 三种类型：
	 * (1:普通(NORMAL),2:子表单(SUBFORM),3:查询表单(SEARCHFORM),4:普通映射(NORMAL_MAPPING)}.
	 */
	private static Map<Integer, String> _FORMTYPE = new TreeMap<Integer, String>();

	static {
		_FORMTYPE.put(Integer.valueOf(Form.FORM_TYPE_NORMAL), "{*[Normal]*}");
		_FORMTYPE.put(Integer.valueOf(Form.FORM_TYPE_NORMAL_MAPPING),
				"{*[Normal]*}" + "({*[Mapping]*})");
		_FORMTYPE.put(Integer.valueOf(Form.FORM_TYPE_SEARCHFORM),
				"{*[SearchForm]*}");
		_FORMTYPE.put(Integer.valueOf(Form.FORM_TYPE_TEMPLATEFORM),
		"{*[core.dynaform.form.type.templateform]*}");

	}

	/**
	 * 返回表单类型集合. 三种表单类型： (1:普通(NORMAL),2:子表单(SUBFORM),3:查询表单(SEARCHFORM)}.
	 * 表单类型常量值为: 1)FORM_TYPE_NORMAL(NORMAL);
	 * 2)FORM_TYPE_SUBFORM(SUBFORM);3)FORM_TYPE_SEARCHFORM(SEARCHFORM).
	 * 普通表单是一般的显示表单. 子表单就是嵌入一个普通表单(这时也称为父表单)里的表单. 查询表单一般用来作为Document一个查询头.
	 * 
	 * @return 表单类型集合. 四种表单类型： (1:普通(NORMAL),2:子表单(SUBFORM),3:查询表单(SEARCHFORM),4:阅读表单(READFORM)}.
	 * @uml.property name="_FORMTYPE"
	 */
	public static Map<Integer, String> get_FORMTYPE() {
		return _FORMTYPE;
	}

	/**
	 * 返回相关样式库主键. 表单可以根据不同的样式来设置不同的个性风格.(这里的样式也就是我们平常所说的CSS.)
	 * 
	 * @return style id
	 * @uml.property name="_styleid"
	 */

	public String get_styleid() {
		if (getContent() != null && ((Form) getContent()).getStyle() != null)
			return ((Form) getContent()).getStyle().getId();
		else
			return null;
	}

	/**
	 * 设置相关样式库主键. 用来设置表单个性风格.
	 * 
	 * @param _styleid
	 *            样式库主键
	 * @uml.property name="_styleid"
	 */
	public void set_styleid(String _styleid) {
		this._styleid = _styleid;
	}

	/**
	 * FormAction 构造函数
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 * @see BaseAction#BaseAction(BaseProcess,
	 *      ValueObject)
	 */
	@SuppressWarnings("unchecked")
	public FormAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(FormProcess.class), new Form());
	}

	/**
	 * FormAction 构造函数
	 * 
	 * @param page
	 *            Page 对象
	 * 
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @see BaseAction#BaseAction(BaseProcess,
	 *      ValueObject)
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public FormAction(Page page) throws ClassNotFoundException {
		super(ProcessFactory.createProcess(PageProcess.class), page);
	}

	/**
	 * FormAction 构造函数
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @param component
	 *            Component对象
	 * @see BaseAction#BaseAction(BaseProcess,
	 *      ValueObject)
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public FormAction(Component component) throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ComponentProcess.class), component);
	}

	/**
	 * 根据表单名称(name)查询,获取Form对象.
	 * 
	 * @param name
	 *            表单Form名称
	 * @return Form 对象
	 * @throws Exception
	 */
	public Form getFormByName(String name) throws Exception {
		FormProcess process = (FormProcess) this.process;
		Form form = process.doViewByFormName(name, getApplication());
		return form;
	}

	/**
	 * 显示表单Form 列表.如果处理成功返回"SUCCESS".
	 * 
	 * @SuppressWarnings webwork不支持泛型
	 * @return result，如果处理成功返回"SUCCESS"
	 * @see BaseAction#doList()
	 * @throws throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doFormList() throws Exception {
		getContext().getParameters().put("xi_type", Integer.valueOf(0));
		return super.doList();
	}

	public String doClearData() {
		try {
			ParamsTable params = this.getParams();
			FormProcess process = (FormProcess) this.process;
			Form form = (Form) process.doView(getContent().getId());
			setContent(form);
			set_formid(form.getId());
			Object deleteType = params.getParameter("deleteType");
			String[] fields = params.getParameterAsArray("afield");
			if (null != deleteType) {
				process.doClearFormData(form);
			} else {
				process.doClearColumnData(form, fields);
			}
			this.addActionMessage("{*[clear.success]*}");
		} catch (Exception e) {
			this.addFieldError("clear failed", "{*[clear.failed]*}");
			LOG.error("doClearData", e);
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 通过表单编号来获得表单信息来生成该表单的视图
	 * 
	 * @return
	 */
	public String doOnekeycreview() {
		try {
			Form form = ((FormProcess) process)
					.oneKeyCreateView(_formid);
			setContent(form);
			this.addActionMessage("{*[Create_View_Success]*}");
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
		}
		return SUCCESS;
	}

	/**
	 * 保存表单Form相关信息. 如果处理成功返回"SUCCESS"，否则返回"INPUT".
	 * 
	 * @see BaseAction#doSave()
	 * @return result.如果处理成功返回"SUCCESS"，否则返回"INPUT".
	 * @see ImpropriateException#ImpropriateException(String)
	 * @throws throws ImpropriateException
	 * @throws throws Exception
	 */
	public String doSave() {
		try {
			Form form = ((Form) getContent());
			
			if(StringUtil.isBlank(form.getId())){
				form.setCheckout(true);
				form.setCheckoutHandler(getUser().getId());
			}
			_formid = form.getId();
			String name = form.getName();
			ParamsTable params = this.getParams();
			params.setParameter("s_name", name);

			doSaveValidate(form);

			ModuleProcess mp = (ModuleProcess) ProcessFactory
					.createProcess(ModuleProcess.class);
			ModuleVO mv = (ModuleVO) mp.doView(moduleid);
			form.setModule(mv);
			StyleRepositoryProcess sp = (StyleRepositoryProcess) ProcessFactory
					.createProcess(StyleRepositoryProcess.class);
			StyleRepositoryVO sr = (StyleRepositoryVO) sp.doView(_styleid);
			form.setStyle(sr);
			form.setLastmodifytime(new Date());
			
			setContent(form);
			return super.doSave();
		} catch (Exception e) {
			LOG.error("doSave", e);
			if (e instanceof NeedConfirmException) {
				Collection<Confirm> confirms = ((NeedConfirmException) e)
						.getConfirms();
				for (Iterator<Confirm> iterator = confirms.iterator(); iterator
						.hasNext();) {
					Confirm confirm = iterator.next();
					addFieldError("Confirm Message", confirm.getMessage());
				}
			} else {
				this.addFieldError("[{*[Errors]*}]:  ", e.getMessage());
			}
			return INPUT;
		}
	}
	
	/**
	 * 签出
	 * @return
	 * @throws Exception
	 */
	public String doCheckout() throws Exception {
		
		try {
			Form form = ((Form) getContent());
			process.doCheckout(form.getId(), getUser());
			form.setCheckout(true);
			form.setCheckoutHandler(getUser().getId());
			setContent(form);
			this.addActionMessage("{*[core.dynaform.form.success.checkout]*}");
			return SUCCESS;
		} catch (Exception e) {
			LOG.error("doCheckout", e);
			if (e instanceof NeedConfirmException) {
				Collection<Confirm> confirms = ((NeedConfirmException) e)
						.getConfirms();
				for (Iterator<Confirm> iterator = confirms.iterator(); iterator
						.hasNext();) {
					Confirm confirm = iterator.next();
					addFieldError("Confirm Message", confirm.getMessage());
				}
			} else {
				this.addFieldError("[{*[Errors]*}]:  ", e.getMessage());
			}
			return INPUT;
		}
		
	}
	
	/**
	 * 签入
	 * @return
	 * @throws Exception
	 */
	public String doCheckin() throws Exception {
		try {
			Form form = ((Form) getContent());
			process.doCheckin(form.getId(), getUser());
			form.setCheckout(false);
			form.setCheckoutHandler("");
			setContent(form);
			this.addActionMessage("{*[core.dynaform.form.success.checkin]*}");
			return SUCCESS;
		} catch (Exception e) {
			LOG.error("doCheckout", e);
			if (e instanceof NeedConfirmException) {
				Collection<Confirm> confirms = ((NeedConfirmException) e)
						.getConfirms();
				for (Iterator<Confirm> iterator = confirms.iterator(); iterator
						.hasNext();) {
					Confirm confirm = iterator.next();
					addFieldError("Confirm Message", confirm.getMessage());
				}
			} else {
				this.addFieldError("[{*[Errors]*}]:  ", e.getMessage());
			}
			return INPUT;
		}
	}

	/**
	 * 保存前的一系列校验
	 * 
	 * @param form
	 * @throws Exception
	 */
	private void doSaveValidate(Form form) throws Exception {
		String name = "";
		ParamsTable params = this.getParams();
		if (form != null) {
			set_formid(form.getId());
			name = form.getName();
			params.setParameter("s_name", name);

			// 名称重复检验

			Form form_Exsited = ((FormProcess) process).doViewByFormName(name,
					form.getApplicationid());
			if (form_Exsited != null && StringUtil.isBlank(form.getId())) {// 判断新建不能重名
				throw new Exception("[" + name + "]{*[core.form.exist]*}");
			} else if (form_Exsited != null
					&& !form_Exsited.getId().trim().equalsIgnoreCase(
							form.getId())) {// 修改不能重名
				throw new Exception("[" + name + "]{*[core.form.exist]*}");
			}
		}

		// 非法字符串检验
		String invalidChars = getInvalidChars(name);
		if (!StringUtil.isBlank(invalidChars)) {
			throw new Exception("{*[core.form.name.exist.invalidchar]*}: "
					+ invalidChars);
		}

		// 关联名称检验
		String relationName = form.getRelationName();
		if (!StringUtil.isBlank(relationName)
				&& !((FormProcess) process).checkRelationName(form.getId(),
						relationName)) {
			throw new Exception(" {*[core.form.relationname.exist]*}: ");
		}

		// 表单映射检验
		String tableName = form.getTableMapping().getTableName();
		int type = form.getType();
		if (StringUtil.isBlank(tableName)
				&& type == Form.FORM_TYPE_NORMAL_MAPPING) {
			throw new Exception(" {*[core.form.type.tableName.select]*} ");
		} else if (!StringUtil.isBlank(tableName)
				&& type == Form.FORM_TYPE_NORMAL_MAPPING) {
			// 映射字段校验
			TableMapping tableMapping = form.getTableMapping();
			if (null != tableMapping.getPrimaryKeyMapping()) {

				Collection<ColumnMapping> columnMappings = tableMapping
						.getColumnMappings();
				if (null != columnMappings && columnMappings.size() > 0) {
					for (Iterator<ColumnMapping> cmsit = columnMappings
							.iterator(); cmsit.hasNext();) {

						ColumnMapping cm = cmsit.next();

						if (!cm.getColumnName().equals("")
								^ !cm.getFieldName().equals("")) {
							throw new Exception(
									"{*[core.mapping.field.select]*}");
						}

						if (!cm.getFieldName().equals("MAPPINGID")) {
							for (Iterator<?> it = DQLASTUtil.SYSTEM_FIELDS
									.iterator(); it.hasNext();) {
								String systemField = (String) it.next();
								if (systemField.equalsIgnoreCase(cm
										.getColumnName())) {
									throw new Exception(
											"{*[core.mapping.field.was.duplicate.with.sys]*}: "
													+ cm.getColumnName());
								}
							}
						}
					}
				}
			} else {
				throw new Exception("{*[core.mapping.primarykey.select]*}");
			}
		}

		// 变更校验
		doChangeValidate(form, params);
	}

	public void doChangeValidate(Form form, ParamsTable params)
			throws Exception {
		FormProcess formPross = (FormProcess) process;
		String tab = params.getParameterAsString("tab");

		formPross.doChangeValidate(form);
		// ServletActionContext.getRequest().setAttribute("tab", tab);
		ServletActionContext.getContext().getValueStack().setValue("tab", tab);
	}

	/**
	 * 删除表单
	 * 
	 * @return
	 */
	public String delete() {
		boolean flag = false;
		try {
			ViewProcess viewPross = (ViewProcess) ProcessFactory
					.createProcess(ViewProcess.class);

			try {
				DataPackage<View> datas = viewPross.doQuery(getParams());
				if (datas.rowCount > 0) {
					for (Iterator<View> ite = datas.datas.iterator(); ite
							.hasNext();) {
						View view = ite.next();
						for (int i = 0; i < this.get_selects().length; i++) {
							if (view.getSearchForm() != null) {
								if (view.getSearchForm().getId().equals(
										this.get_selects()[i])) {
									flag = true;
									this
											.addFieldError("1",
													"{*[View]*}{*[Relation]*}{*[SearchForm]*}");
									break;
								}
							}
						}
						if (flag) {
							break;
						}
					}
				}
			} catch (Exception e) {
				this.addFieldError("Exception", e.getMessage());
				flag = true;
			}
		} catch (ClassNotFoundException e) {
			this.addFieldError("Exception", e.getMessage());
			flag = true;
		}
		if (!flag) {
			return super.doDelete();
		} else {
			return INPUT;
		}
	}

	/**
	 * 编辑
	 * 
	 * @return
	 */
	public String doEdit() {
		
		
		try {
			PropertyUtil.reload("checkout");
			String _checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
			Map params = getContext().getParameters();
			String id = ((String[]) params.get("id"))[0];
			set_formid(id);
			Form form = (Form) process.doView(id);
			if(_checkoutConfig.equals("true") && form.isCheckout() && !form.getCheckoutHandler().equals(getUser().getId())){
				SuperUserProcess sp = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
				SuperUserVO speruser = (SuperUserVO) sp.doView(form.getCheckoutHandler());
				addFieldError("", "此表单已经被"+speruser.getName()+"签出，您目前没有修改的权限！");
//				addFieldError("", "{*[core.dynaform.form.message.warning.be.checkedout.by.other.developers]*}");
			}
			setContent(form);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 返回所属模块主键.
	 * 
	 * @return 模块主键.
	 * @uml.property name="moduleid"
	 */
	public String getModuleid() {
		return moduleid;
	}

	private String getInvalidChars(String name) {
		// String rtn = "";
		// char[] arrchar = new char[name.length()];
		// name.getChars(0, name.length(), arrchar, 0);
		// if (name.indexOf("｜") >= 0 || name.indexOf("︱") >= 0
		// || name.indexOf("︳") >= 0 || name.indexOf("|") >= 0) {
		// rtn = ""|"";
		// }
		String[] p = { "﹉", "＃", "＠", "＆", "＊", "※", "§", "〃", "№", "〓", "○",
				"●", "△", "▲", "◎", "☆", "★", "◇", "◆", "■", "□", "▼", "▽",
				"㊣", "℅", "ˉ", "￣", "＿", "﹍", "﹊", "﹎", "﹋", "﹌", "﹟", "﹠",
				"﹡", "♀", "♂", "?", "⊙", "↑", "↓", "←", "→", "↖", "↗", "↙",
				"↘", "┄", "—", "︴", "﹏", "（", "）", "︵", "︶", "｛", "｝", "︷",
				"︸", "〔", "〕", "︹", "︺", "【", "】", "︻", "︼", "《", "》", "︽",
				"︾", "〈", "〉", "︿", "﹀", "「", "」", "﹁", "﹂", "『", "』", "﹃",
				"﹄", "﹙", "﹚", "﹛", "﹜", "﹝", "﹞", "\"", "〝", "〞", "ˋ",
				"ˊ", "≈", "≡", "≠", "＝", "≤", "≥", "＜", "＞", "≮", "≯", "∷",
				"±", "＋", "－", "×", "÷", "／", "∫", "∮", "∝", "∧", "∨", "∞",
				"∑", "∏", "∪", "∩", "∈", "∵", "∴", "⊥", "∥", "∠", "⌒", "⊙",
				"≌", "∽", "√", "≦", "≧", "≒", "≡", "﹢", "﹣", "﹤", "﹥", "﹦",
				"～", "∟", "⊿", "∥", "㏒", "㏑", "∣", "｜", "︱", "︳", "|", "／",
				"＼", "∕", "﹨", "¥", "€", "￥", "£", "®", "™", "©", "，", "、",
				"。", "．", "；", "：", "？", "！", "︰", "…", "‥", "′", "‵", "々",
				"～", "‖", "ˇ", "ˉ", "﹐", "﹑", "﹒", "·", "﹔", "﹕", "﹖", "﹗",
				"-", "&", "*", "#", "`", "~", "+", "=", "(", ")", "^", "%",
				"$", "@", ";", ",", ":", "'", "\\", "/", ".", ">", "<",
				"?", "!", "[", "]", "{", "}" };
		// String p2 = ""\"";
		// String p3 = "";
		// String p4 = "";
		for (int i = 0; i < p.length; i++) {
			/*
			 * if (p1.indexOf(arrchar[i]) >= 0 || p2.indexOf(arrchar[i]) >= 0 ||
			 * p3.indexOf(arrchar[i]) >= 0 || p4.indexOf(arrchar[i]) >= 0) { if
			 * (rtn == null) { rtn = """ + String.valueOf(arrchar, i, 1) + """;
			 * } else { rtn += " , "" + String.valueOf(arrchar, i, 1) + """; } }
			 */
			if (name != null && name.contains(p[i])) {
				return p[i];
			}
		}
		return "";
	}

	/**
	 * 设置所属模块主键.
	 * 
	 * @param moduleid
	 *            Module对象主键id.
	 * @uml.property name="moduleid"
	 */
	public void setModuleid(String moduleid) {
		this.moduleid = moduleid;
	}

	/**
	 * 查询表单.
	 * 
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	public String doSelect() throws Exception {
		_formSelect = ((FormProcess) process).doSimpleQuery(this.getParams(),
				getApplication());
		return SUCCESS;
	}
	
	public String doSelectFormNameById() throws Exception {
		String id = this.get_formid();
		Form f = (Form) process.doView(id);
		return f.getName();
	}

	/**
	 * 表单全名查询.
	 * 
	 * @return SUCCESS
	 * @throws Exception
	 */
	public String doFullNameSelect() throws Exception {

		return SUCCESS;
	}

	/**
	 * 返回表单查询集合.
	 * 
	 * @return 表单查询集合
	 * @uml.property name="_formSelect"
	 */
	public Collection<Form> get_formSelect() {
		return _formSelect;
	}

	/**
	 * 设置表单查询集合.
	 * 
	 * @param select
	 * @uml.property name="_formSelect"
	 */
	public void set_formSelect(Collection<Form> select) {
		_formSelect = select;
	}

	/**
	 * 根据表单名称(name).获取Form.
	 * 
	 * @return "SUCCESS"为成功处理
	 * @throws Exception
	 */
	public String doSelectField() throws Exception {
		this.setContent(getFormByName(get_formname()));
		return SUCCESS;
	}

	/**
	 * 是否显示LOG
	 * 
	 * @return
	 */
	public String get_isDisplayLog() {
		Form content = (Form) getContent();
		if (content.isShowLog()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 设置V是否显示LOG . true为显示,false 为不显示
	 * 
	 * @param _isDisplayLog
	 *            否显示LOG
	 */
	public void set_isDisplayLog(String _isDisplayLog) {
		Form content = (Form) getContent();
		if (_isDisplayLog != null) {
			if (_isDisplayLog.trim().equalsIgnoreCase("true")) {
				content.setShowLog(true);
				return;
			}
		}
		content.setShowLog(false);
	}

	/**
	 * 保存数据时，是否马上启动流程
	 * 
	 * @return
	 */
	public String get_isOnSaveStartFlow() {
		Form content = (Form) getContent();
		if (content.isOnSaveStartFlow()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 设置保存数据时，是否马上启动流程 . true为启动,false 为不启动
	 * 
	 * @param _isOnSaveStartFlow
	 * 
	 */
	public void set_isOnSaveStartFlow(String _isStartFlow) {
		Form content = (Form) getContent();
		if (_isStartFlow != null) {
			if (_isStartFlow.trim().equalsIgnoreCase("true")) {
				content.setOnSaveStartFlow(true);
				return;
			}
		}
		content.setOnSaveStartFlow(false);
	}

	/**
	 * 返回表单名.
	 * 
	 * @return 表单名.
	 * @uml.property name="_formname"
	 */
	public String get_formname() {
		return _formname;
	}

	/**
	 * 设置表单名.
	 * 
	 * @param _formname
	 *            表单名
	 * @uml.property name="_formname"
	 */
	public void set_formname(String _formname) {
		this._formname = _formname;
	}

	/**
	 * 返回表单(Activity)按钮集合. 如下为Activity值与对应Activity名: ("8" "关闭窗口");
	 * ("9","保存并关闭"); ("10", "返回"); ("11", "保存返回"); ("12", "保存新建");
	 * 
	 * @return map of the acvitity
	 * @uml.property name="_FORMACLIST"
	 */
	public static Map<String, String> get_FORMACLIST() {
		return _FORMACLIST;
	}

	/**
	 * 设置表单按钮列表 . 如下为Activity值与对应Activity名: ("8" "关闭窗口"); ("9", "保存并关闭"); ("10",
	 * "返回"); ("11", "保存返回"); ("12", "保存新建");
	 * 
	 * @param _aclist
	 *            map of the acvitity
	 * @uml.property name="_FORMACLIST"
	 */
	public static void set_FORMACLIST(Map<String, String> _aclist) {
		_FORMACLIST = _aclist;
	}

	/**
	 * @return the flowlist
	 * @uml.property name="flowlist"
	 */
	public String getFlowlist() {
		return flowlist;
	}

	/**
	 * @param flowlist
	 *            the flowlist to set
	 * @uml.property name="flowlist"
	 */
	public void setFlowlist(String flowlist) {
		this.flowlist = flowlist;
	}

	/**
	 * @return the disField
	 * @uml.property name="disField"
	 */
	public String getDisField() {
		return disField;
	}

	/**
	 * @param disField
	 *            the disField to set
	 * @uml.property name="disField"
	 */
	public void setDisField(String disField) {
		this.disField = disField;
	}

	/**
	 * 返回视图打开类型. 类型分别：1:普通(Normal), 2:子窗口打开(Open in pop window) 3:父窗口(Open in
	 * working area). 类型常量对应值为: 1) OPEN_TYPE_NORMAL(Normal);
	 * 2)OPEN_TYPE_POP(Open in pop window) ; 3)OPEN_TYPE_PARENT(Open in working
	 * area).
	 * 
	 * @return 视图打开类型.
	 * @uml.property name="_OPENTYPE"
	 */
	public static Map<Integer, String> get_OPENTYPE() {
		return _OPENTYPE;
	}

	/**
	 * 设置视图打开类型. 类型分别：1:普通(Normal), 2:子窗口打开(Open in pop window) 3:父窗口(Open in
	 * working area). 类型常量对应值为: 1) OPEN_TYPE_NORMAL(Normal);
	 * 2)OPEN_TYPE_POP(Open in pop window) ; 3)OPEN_TYPE_PARENT(Open in working
	 * area).
	 * 
	 * @param _opentype
	 *            视图打开类型
	 * @uml.property name="_OPENTYPE"
	 */
	public static void set_OPENTYPE(Map<Integer, String> _opentype) {
		_OPENTYPE = _opentype;
	}

	/**
	 * 返回视图按钮集合列表. 按钮的列表值为: 1:查询;2: 新建;3: 删除;4: 修改;
	 * 
	 * @return 按钮集合
	 * @uml.property name="_VIEWACLIST"
	 */
	public static Map<String, String> get_VIEWACLIST() {
		return _VIEWACLIST;
	}

	/**
	 * 设置视图按钮集合列表. 按钮的列表值为: 1:查询;2: 新建;3: 删除;4: 修改;
	 * 
	 * @param _viewaclist
	 *            按钮集合
	 * @uml.property name="_VIEWACLIST"
	 */
	public static void set_VIEWACLIST(Map<String, String> _viewaclist) {
		_VIEWACLIST = _viewaclist;
	}

	/**
	 * @return the _formact
	 * @uml.property name="_formact"
	 */
	public String[] get_formact() {
		return _formact;
	}

	/**
	 * @param _formact
	 *            the _formact to set
	 * @uml.property name="_formact"
	 */
	public void set_formact(String[] _formact) {
		this._formact = _formact;
	}

	/**
	 * 设置表单类型集合. 三种类型： (1:普通(NORMAL),2:子表单(SUBFORM),3:查询表单(SEARCHFORM)}.
	 * 表单类型常量值为: 1)FORM_TYPE_NORMAL(NORMAL);
	 * 2)FORM_TYPE_SUBFORM(SUBFORM);3)FORM_TYPE_SEARCHFORM(SEARCHFORM).
	 * 
	 * @uml.property name="_FORMTYPE"
	 */
	public static void set_FORMTYPE(Map<Integer, String> _formtype) {
		_FORMTYPE = _formtype;
	}

	/**
	 * @return the _viewact
	 * @uml.property name="_viewact"
	 */
	public String[] get_viewact() {
		return _viewact;
	}

	/**
	 * @param _viewact
	 *            the _viewact to set
	 * @uml.property name="_viewact"
	 */
	public void set_viewact(String[] _viewact) {
		this._viewact = _viewact;
	}

	// the property of view
	private String _viewact[];

	private String viewname;

	private String description;

	private String superiorid;

	private String opentype;

	/**
	 * 视图描述.
	 * 
	 * @return
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置视图描述
	 * 
	 * @param description
	 * @uml.property name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the opentype
	 * @uml.property name="opentype"
	 */
	public String getOpentype() {
		return opentype;
	}

	/**
	 * @param opentype
	 *            the opentype to set
	 * @uml.property name="opentype"
	 */
	public void setOpentype(String opentype) {
		this.opentype = opentype;
	}

	/**
	 * @return the superiorid
	 * @uml.property name="superiorid"
	 */
	public String getSuperiorid() {
		return superiorid;
	}

	/**
	 * @param superiorid
	 *            the superiorid to set
	 * @uml.property name="superiorid"
	 */
	public void setSuperiorid(String superiorid) {
		this.superiorid = superiorid;
	}

	/**
	 * 返回视图名.
	 * 
	 * @return view name
	 * @uml.property name="viewname"
	 */
	public String getViewname() {
		return viewname;
	}

	/**
	 * 设置视图名.
	 * 
	 * @param viewname
	 *            视图名
	 * @uml.property name="viewname"
	 */
	public void setViewname(String viewname) {
		this.viewname = viewname;
	}

	/**
	 * @return the workflowname
	 * @uml.property name="workflowname"
	 */
	public String getWorkflowname() {
		return workflowname;
	}

	/**
	 * @param workflowname
	 *            the workflowname to set
	 * @uml.property name="workflowname"
	 */
	public void setWorkflowname(String workflowname) {
		this.workflowname = workflowname;
	}

	/**
	 * 返回应用标识
	 * 
	 * @return 应用标识
	 * @uml.property name="applicationid"
	 */
	public String getApplicationid() {
		return applicationid;
	}

	/**
	 * 设置应用标识
	 * 
	 * @param applicationid
	 *            应用标识
	 * @uml.property name="applicationid"
	 */
	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 返回表单对值对象.
	 * 
	 * @return value object
	 */
	public ValueObject getContent() {
		ValueObject content = super.getContent();
		// 清除缓存
		if (content != null && content.getId() != null
				&& content.getId().trim().length() > 0) {
			MemoryCacheUtil.removeFromPublicSpace(content.getId());
		}
		return content;
	}

	/**
	 * @return the confirms
	 * @uml.property name="confirms"
	 */
	public Collection<Confirm> getConfirms() {
		return confirms;
	}

	/**
	 * @param confirms
	 *            the confirms to set
	 * @uml.property name="confirms"
	 */
	public void setConfirms(Collection<Confirm> confirms) {
		this.confirms = confirms;
	}
	
	public String getCheckoutConfig() {
		PropertyUtil.reload("checkout");
		String _checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
		return _checkoutConfig;
	}

	public void setCheckoutConfig(String checkoutConfig) {
		this.checkoutConfig = checkoutConfig;
	}

	public void validate() {
		Form form = (Form) getContent();
		String regex = "^[a-zA-Z]{1}\\w*$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(form.getName());

		try {
			String dbType = DbTypeUtil.getDBType(getApplication());
			if (dbType != null && dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
				if (!matcher.find()) {
					addFieldError("", "{*[form.name.invalid]*}");
				}
			}
		} catch (Exception e) {
			addFieldError("", e.getMessage());
		}
	}

	/**
	 * 同步数据
	 */
	public String synchronouslyData() {
		try {
			Form form = (Form) this.getContent();
			if (StringUtil.isBlank(form.getTableMapping().getPrimaryKeyName())) {
				addFieldError("1", "{*[noPrimaryKeyName]*}");
				return INPUT;
			}
			String reuslt = ((FormProcess) process).doSynchronouslyData(this
					.getParams(), this.getUser(), form);
			//Update by XGY 2011.11.29
			if (reuslt.equals("success")) {
				this.addActionMessage("{*[synchronouslyDataSuccess]*}");
			} else if (reuslt.equals("exist") ) {
				addFieldError("1", "{*[hasSynchronouslyData]*}");
			} else if (reuslt.equals("noexist")) {
				addFieldError("1", "{*[Database]*}{*[Table]*}"
						+ this.getParams().getParameterAsString(
								"content.tableName") + "{*[Data]*} IS NULL");
			}

		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}
	/*
	 * public DocumentSummaryCfg getDocSummaryCfg() { if (docSummaryCfg == null)
	 * { this.docSummaryCfg = ((Form) this.getContent())
	 * .getDocumentSummaryCfg(); if (docSummaryCfg.getType() == null)
	 * docSummaryCfg.setType("00"); } return docSummaryCfg; }
	 * 
	 * public void setDocSummaryCfg(DocumentSummaryCfg docSummaryCfg) {
	 * this.docSummaryCfg = docSummaryCfg; Form form = (Form) this.getContent();
	 * form.setDocumentSummaryCfg(docSummaryCfg); this.setContent(form); }
	 */

}
