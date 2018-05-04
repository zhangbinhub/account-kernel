package OLink.bpm.core.dynaform.view.action;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.type.GanttType;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryProcess;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.constans.Web;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.dynaform.view.ejb.type.CalendarType;
import OLink.bpm.core.dynaform.view.ejb.type.MapType;
import OLink.bpm.core.dynaform.view.ejb.type.TreeType;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.sysconfig.ejb.CheckoutConfig;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.util.CreateProcessException;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.PropertyUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import eWAP.core.Tools;

import com.opensymphony.webwork.ServletActionContext;

/**
 * 
 * @author nicholas
 * 
 */
public class ViewAction extends BaseAction<View> {
	private static final Logger LOG = Logger.getLogger(ViewAction.class);

	public static final int DO_DISPLAY_VIEW = 1;

	public static final int DO_DIALOG_VIEW = 2;

	protected String _viewid;

	protected String _superiorid;

	protected String _resourceid;

	protected String _resourcedesc;

	protected String totalRowText;

	protected String _sortCol;

	protected String _sortStatus;

	protected String _orderby;

	protected String domain;

	protected Document parent;

	protected Document currentDocument;

	protected int year;

	protected int month;

	protected int week;

	protected int day;

	protected String viewMode;

	protected String linkName;

	protected boolean isPreview = false;
	
	private String checkoutConfig;
	
	public String getCheckoutConfig() {
		PropertyUtil.reload("checkout");
		String checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
		return checkoutConfig;
	}

	public void setCheckoutConfig(String checkoutConfig) {
		this.checkoutConfig = checkoutConfig;
	}

	public boolean isPreview() {
		return isPreview;
	}

	public void setPreview(boolean isPreview) {
		this.isPreview = isPreview;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public int getWeek() {
		return week;
	}

	public int getDay() {
		return day;
	}

	public void setWeek(int week) {
		this.week = week;
	}

	public void setDay(int day) {
		this.day = day;
	}

	private static final long serialVersionUID = 1L;

	private static Map<Integer, String> _OPENTYPE = new TreeMap<Integer, String>();

	protected View view;

	/**
	 * View类型. 两种类型： (1:普通(NORMAL),2:日历视图(CALENDARVIEW), 3:树形视图(TREEVIEW),
	 * 4:地图视图(TREEVIEW)}.
	 */
	private static Map<Integer, String> _VIEWTYPE = new TreeMap<Integer, String>();

	public static Map<Integer, String> get_VIEWTYPE() {
		return _VIEWTYPE;
	}

	static {
		_VIEWTYPE.put(View.VIEW_TYPE_NORMAL, "{*[Normal]*}");
		_VIEWTYPE.put(View.VIEW_TYPE_CALENDAR, "{*[Calendar]*}{*[View]*}");
		_VIEWTYPE.put(View.VIEW_TYPE_TREE, "{*[Tree]*}{*[View]*}");
		_VIEWTYPE.put(View.VIEW_TYPE_MAP, "{*[map]*}{*[View]*}");
		_VIEWTYPE.put(View.VIEW_TYPE_GANTT, "{*[Gantt]*}{*[View]*}");
	}
	static {
		_OPENTYPE.put(View.OPEN_TYPE_NORMAL, "{*[Normal]*}");
		_OPENTYPE.put(View.OPEN_TYPE_OWN, "{*[Open.in.working.own]*}");
		_OPENTYPE.put(View.OPEN_TYPE_DIV, "{*[Open.in.working.div]*}");
		_OPENTYPE.put(View.OPEN_TYPE_GRID, "{*[Open.in.grid]*}");
	}

	/**
	 * 获取所引用的查询Form主键
	 * 
	 * @return 查询Form主键
	 */
	public String get_searchformid() {
		View view = (View) getContent();
		if (view.getSearchForm() != null) {
			return view.getSearchForm().getId();
		}
		return null;
	}

	/**
	 * Set所引用的查询Form主键
	 * 
	 * @param _formid
	 *            查询Form主键
	 */
	public void set_searchformid(String _formid) {
		View view = (View) getContent();
		if (!StringUtil.isBlank(_formid)) {
			Form form = new Form();
			form.setId(_formid);
			view.setSearchForm(form);
		}
	}

	/**
	 * ViewAction 构造函数
	 * 
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @see BaseAction#BaseAction(BaseProcess,
	 *      ValueObject)
	 * @see ProcessFactory#createProcess(Class)
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ViewAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ViewProcess.class), new View());
	}

	/**
	 * 显示视图view数据列表(前台调用)
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doDisplayView() throws Exception {
		try {
			return getSuccessResult((View) getContent(), DO_DISPLAY_VIEW);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				addFieldError("", e.getMessage());
			} else {
				addFieldError("errorMessage", e.toString());
			}
			return getInputResult((View) getContent());
		}
	}

	/**
	 * 显示视图view数据列表(前台视图控件调用)
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doDialogView() throws Exception {
		try {
			return getSuccessResult((View) getContent(), DO_DIALOG_VIEW);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage() != null ? e.getMessage() : "");
			return getInputResult((View) getContent());
		}
	}
	
	public String doEdit() {
		try {
			Map<?, ?> params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			View view = (View) process.doView(id);
			if(view.isCheckout() && !view.getCheckoutHandler().equals(getUser().getId())){
				SuperUserProcess sp = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
				SuperUserVO speruser = (SuperUserVO) sp.doView(view.getCheckoutHandler());
				addFieldError("", "此视图已经被"+speruser.getName()+"签出，您目前没有修改的权限！");
			}
			setContent(view);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	protected String getSuccessResult(View view, int cldType) throws Exception {
		String clearTemp = getParams().getParameterAsString("clearTemp");
		String parentid = getParams().getParameterAsString("parentid");
		String isinner = getParams().getParameterAsString("isinner");

		if (clearTemp != null && clearTemp.equals("true")) {
			getUser().clearTmpspace(); // 清空用户缓存
		}

		if (StringUtil.isBlank(isinner) || !isinner.equals("true")) {
			switch (view.getOpenType()) {
			case View.OPEN_TYPE_GRID:
				displayByView(view);
				return "successGrid";
			default:
				break;
			}

			if (cldType == DO_DIALOG_VIEW) {
				switch (view.getViewType()) {
				case View.VIEW_TYPE_NORMAL:
					displayByView(view);
					break;
				case View.VIEW_TYPE_TREE:
					setContent(view);
					return "successTree";
				case View.VIEW_TYPE_CALENDAR:
					doShowCldView(cldType);
					return "successCld";
				case View.VIEW_TYPE_GANTT:
					setContent(view);
					return "successGantt";
				case View.VIEW_TYPE_MAP:
					displayByView(view);
					return "successMap";
				default:
					displayByView(view);
					break;
				}
			} else {
				switch (view.getViewType()) {
				case View.VIEW_TYPE_CALENDAR:
					doShowCldView(cldType);
					return "successCld";
				case View.VIEW_TYPE_NORMAL:
					displayByView(view);
					if (!StringUtil.isBlank(parentid)) {
						return "successSub";
					}
					break;
				case View.VIEW_TYPE_TREE:
					setContent(view);
					return "successTree";
				case View.VIEW_TYPE_GANTT:
					setContent(view);
					return "successGantt";
				case View.VIEW_TYPE_MAP:
					displayByView(view);
					return "successMap";
				default:
					break;
				}
			}

		} else {
			displayByView((View) getContent());
		}

		return SUCCESS; // 普通且不为子表单
	}

	protected String getInputResult(View view) throws Exception {
		String parentid = getParams().getParameterAsString("parentid");
		String isinner = getParams().getParameterAsString("isinner");

		if (StringUtil.isBlank(isinner) || !isinner.equals("true")) {
			switch (view.getOpenType()) {
			case View.OPEN_TYPE_GRID:
				return "inputGrid";
			default:
				break;
			}

			switch (view.getViewType()) {
			case View.VIEW_TYPE_NORMAL:
				if (!StringUtil.isBlank(parentid)) {
					return "inputSub";
				}
				break;
			case View.VIEW_TYPE_TREE:
				return "inputTree";
			case View.VIEW_TYPE_CALENDAR:
				return "inputCld";
			case View.VIEW_TYPE_GANTT:
				return "inputGantt";
			case View.VIEW_TYPE_MAP:
				return "inputMap";
			default:
				break;
			}
		}

		return INPUT; // 普通且不为子表单
	}

	/**
	 * 显示子表单视图view数据列表(前台调用)
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doSubFormView() throws Exception {
		try {
			displayByView((View) getContent());
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 显示视图view数据列表(后台调用)
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doPreView() throws Exception {
		try {
			displayByView((View) getContent());
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 预览
	 * 
	 * @return
	 */
	public String doPreDialogView() {
		try {
			return getSuccessResult((View) getContent(), DO_DIALOG_VIEW);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 预览
	 */
	public String doPreDisplayView() throws Exception {
		try {
			return getSuccessResult(view, DO_DISPLAY_VIEW);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				addFieldError("", e.getMessage());
			} else {
				addFieldError("errorMessage", e.toString());
			}
			return INPUT;
		}
	}

	/**
	 * 显示日历视图
	 * 
	 * @param displayType
	 *            显示类型（普通、弹出）
	 * @return
	 * @throws Exception
	 */
	protected boolean doShowCldView(int displayType) throws Exception {
		ParamsTable params = getParams();
		if (view.getViewTypeImpl().intValue() != View.VIEW_TYPE_CALENDAR) {
			return false;
		}

		// part.1
		String viewMode = params.getParameterAsString("viewMode");
		addByType(params);
		ViewHelper hp = new ViewHelper();
		hp.setDisplayType(displayType);

		String toHtml = "";
		if (getDomain() != null)
			params.setParameter("domainid", getDomain());
		changeOrderBy(params);

		// part.2
		if ("WEEKVIEW".equals(viewMode)) {
			toHtml = hp.toWeekHtml(view, params, getUser(), getApplication(), year, month, day, isPreview);
		} else if ("DAYVIEW".equals(viewMode)) {
			toHtml = hp.toDayHtml(view, params, getUser(), getApplication(), year, month, day, isPreview);
		} else {
			viewMode = viewMode != null && viewMode.length() > 0 ? viewMode : "MONTHVIEW";
			params.setParameter("viewMode", viewMode);
			toHtml = hp.toMonthHtml(view, params, getUser(), view.getApplicationid(), year, month, isPreview);
		}

		// part.3
		HttpSession session = ServletActionContext.getRequest().getSession();
		session.setAttribute("toHtml", toHtml);
		setViewMode(viewMode);
		setCurrentDocument(getSearchDocument(view));
		setContent(view);

		return true;
	}

	private void addByType(ParamsTable params) {
		String year = params.getParameterAsString("year");
		String month = params.getParameterAsString("month");
		String addType = params.getParameterAsString("addType");
		Calendar dfCld = Calendar.getInstance();
		int yearIndex = year != null && year.trim().length() > 0 ? Integer.parseInt(year) : dfCld.get(Calendar.YEAR);
		int monthIndex = month != null && month.trim().length() > 0 ? Integer.parseInt(month) : dfCld
				.get(Calendar.MONTH) + 1;
		String week = params.getParameterAsString("week");
		int weekIndex = week != null && week.trim().length() > 0 ? Integer.parseInt(week) : 0;
		String day = params.getParameterAsString("day");
		int dayIndex = day != null && day.trim().length() > 0 ? Integer.parseInt(day) : dfCld
				.get(Calendar.DAY_OF_MONTH);
		Calendar cld = CalendarVO.getThisMonth(yearIndex, monthIndex);
		cld.set(Calendar.YEAR, yearIndex);
		cld.set(Calendar.MONTH, monthIndex - 1);
		cld.set(Calendar.DAY_OF_MONTH, dayIndex);
		if ("previousYear".equals(addType)) {
			yearIndex--;
		} else if ("previousMonth".equals(addType)) {
			cld.add(Calendar.MONTH, -1);
			yearIndex = cld.get(Calendar.YEAR);
			monthIndex = cld.get(Calendar.MONTH) + 1;
		} else if ("previousWeek".equals(addType)) {
			weekIndex--;
			cld.add(Calendar.DAY_OF_MONTH, -7);
			yearIndex = cld.get(Calendar.YEAR);
			monthIndex = cld.get(Calendar.MONTH) + 1;
			dayIndex = cld.get(Calendar.DAY_OF_MONTH);
		} else if ("previousDay".equals(addType)) {
			cld.add(Calendar.DAY_OF_MONTH, -1);
			yearIndex = cld.get(Calendar.YEAR);
			monthIndex = cld.get(Calendar.MONTH) + 1;
			dayIndex = cld.get(Calendar.DAY_OF_MONTH);
		} else if ("nextYear".equals(addType)) {
			yearIndex++;
		} else if ("nextMonth".equals(addType)) {
			cld.add(Calendar.MONTH, 1);
			yearIndex = cld.get(Calendar.YEAR);
			monthIndex = cld.get(Calendar.MONTH) + 1;
		} else if ("nextWeek".equals(addType)) {
			weekIndex++;
			cld.add(Calendar.DAY_OF_MONTH, 7);
			yearIndex = cld.get(Calendar.YEAR);
			monthIndex = cld.get(Calendar.MONTH) + 1;
			dayIndex = cld.get(Calendar.DAY_OF_MONTH);
		} else if ("nextDay".equals(addType)) {
			cld.add(Calendar.DAY_OF_MONTH, 1);
			yearIndex = cld.get(Calendar.YEAR);
			monthIndex = cld.get(Calendar.MONTH) + 1;
			dayIndex = cld.get(Calendar.DAY_OF_MONTH);
		}
		setYear(yearIndex);
		setMonth(monthIndex);
		setWeek(weekIndex);
		setDay(dayIndex);
	}

	/**
	 * @SuppressWarnings setDatas方法设置了View以外的对象，存在类型转换风险
	 */
	@SuppressWarnings("unchecked")
	protected void displayByView(View view) throws Exception {
		ParamsTable params = getParams(); // 获取并设置参数
		if (view != null) {
			Document searchDocument = getSearchDocument(view);
			// 设置Action属性
			setCurrentDocument(searchDocument);
			setContent(view);
			setParent(null);

			// 改变排序参数
			changeOrderBy(params);

			this.validateDocumentValue(searchDocument);

			DataPackage datas = view.getViewTypeImpl().getViewDatas(params, getUser(), searchDocument);

			setDatas(datas);
			setTotalRowText(view.getTotalRowText(datas));
		}
	}

	/**
	 * 查询表单
	 * 
	 * @return
	 */
	public String displaySearchForm() {
		try {
			Document searchDocument = getSearchDocument((View) getContent());
			// 设置Action属性
			setCurrentDocument(searchDocument);
			setParent(null);
		} catch (Exception e) {
			e.printStackTrace();
			return INPUT;
		}
		return SUCCESS;
	}

	public void changeOrderBy(ParamsTable params) {
		if (params.getParameter("_sortCol") == null || params.getParameter("_sortCol").equals("")) {
			setOrder(params, view.getOrderFieldAndOrderTypeArr());
			// params.setParameter("_sortStatus",
			// view.getOrderFieldAndOrderTypeArr());
		} else {
			String[] colFields = new String[1];
			String fieldName = view.getFormFieldNameByColsName(params.getParameterAsString("_sortCol"));
			String sortStatus = params.getParameterAsString("_sortStatus");
			colFields[0] = fieldName + " " + sortStatus;
			setOrder(params, colFields);
		}
	}

	public void setOrder(ParamsTable params, String[] orderFields) {
		params.setParameter("_sortCol", orderFields);
		set_sortCol(this.get_sortCol());
	}

	/**
	 * 返回 view主键
	 * 
	 * @return view主键
	 */
	public String get_viewid() {
		return _viewid;
	}

	/**
	 * Set view主键
	 * 
	 * @param _viewid
	 *            view主键
	 */
	public void set_viewid(String _viewid) {
		try {
			if (!StringUtil.isBlank(_viewid)) {
				String[] viewids = _viewid.split(",");
				View view = (View) process.doView(viewids[0].trim());
				if (view == null) {
					throw new Exception("视图不存在");
				}
				setContent(view);
				this.view = view;
				this._viewid = viewids[0].trim();
			}

		} catch (Exception e) {
			LOG.warn("set_viewid", e);
		}

	}

	/**
	 * 根据模块主键,获取view数据集合.
	 * 
	 * @return result ."SUCCESS"表示成功处理.
	 * @throws Exception
	 */
	public String getViewsByModule() throws Exception {
		ViewProcess process = (ViewProcess) this.process;
		this.setDatas(process.getViewsByModuleId(this.get_moduleid(), getApplication()));
		return SUCCESS;
	}

	/**
	 * 返回所属模块主键(module id)
	 * 
	 * @return 所属模块主键(module id)
	 */
	public String get_moduleid() {
		View view = (View) getContent();
		if (view.getModule() != null) {
			return view.getModule().getId();
		}
		return null;
	}

	/**
	 * Set模块主键(module id)
	 * 
	 * @param _moduleid
	 *            模块主键
	 */
	public void set_moduleid(String _moduleid) {
		View view = (View) getContent();
		if (_moduleid != null) {
			try {
				ModuleProcess mp = (ModuleProcess) ProcessFactory.createProcess((ModuleProcess.class));
				ModuleVO module = (ModuleVO) mp.doView(_moduleid);
				view.setModule(module);

			} catch (Exception e) {
			}
		}
	}

	/**
	 * 返回相关的样式主键(Style id)
	 * 
	 * @return 样式主键(Style id)
	 */
	public String get_styleid() {
		if (((View) getContent()).getStyle() != null)
			return ((View) getContent()).getStyle().getId();
		else
			return null;

	}

	/**
	 * Set相关样式主键(Style id)
	 * 
	 * @param _styleid
	 *            视图风格主键
	 * @throws Exception
	 */
	public void set_styleid(String _styleid) throws Exception {
		View view = (View) getContent();
		if (_styleid != null) {
			StyleRepositoryProcess sp = (StyleRepositoryProcess) ProcessFactory
					.createProcess(StyleRepositoryProcess.class);
			StyleRepositoryVO sty = (StyleRepositoryVO) sp.doView(_styleid);
			view.setStyle(sty);
		}
	}

	/**
	 * Gets view相关resource主键
	 * 
	 * @return
	 */
	public String get_resourceid() {
		View view = (View) getContent();
		return view.getRelatedResourceid();
	}

	/**
	 * Set view相关resource主键
	 * 
	 * @param _resourceid
	 *            resource主键
	 * @throws Exception
	 */
	public void set_resourceid(String _resourceid) throws Exception {
		this._resourceid = _resourceid;
	}

	/**
	 * 获取上级resource主键
	 * 
	 * @return 上级resource主键
	 * @throws Exception
	 */
	public String get_superiorid() throws Exception {
		View view = (View) getContent();
		String rid = view.getRelatedResourceid();
		if (rid != null) {
			ResourceProcess rp = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
			ResourceVO rvo = (ResourceVO) rp.doView(rid);
			if (rvo != null && rvo.getSuperior() != null) {
				return rvo.getSuperior().getId();
			}
		}
		return null;
	}

	/**
	 * Set 上级resource主键
	 * 
	 * @param _superiorid
	 *            上级resource主键
	 */
	public void set_superiorid(String _superiorid) {
		this._superiorid = _superiorid;
	}

	/**
	 * 保存view对象
	 * 
	 * @return 返回一个字符串,如果处理成功返回"SUCCESS",
	 */
	public String doSave() {
		try {
			View view = (View) (this.getContent());
			if(StringUtil.isBlank(view.getId())){
				view.setCheckout(true);
				view.setCheckoutHandler(getUser().getId());
			}
			set_viewid(view.getId());
			String name = view.getName();
			ParamsTable params = this.getParams();
			params.setParameter("s_name", name);

			doValidate(view); // 校验

			if (StringUtil.isBlank(view.getId())) {
				view.setId(Tools.getSequence());
				view.setSortId(Tools.getTimeSequence());
				ModuleProcess mp = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
				ModuleVO module = (ModuleVO) mp.doView(getParams().getParameterAsString("s_module"));
				view.setModule(module);
			}

			view.setLastmodifytime(new Date());
			view.setDescription(_resourcedesc);

			setContent(view);
			return super.doSave();
		} catch (Exception e) {
			LOG.error("doSave", e);
			addFieldError("[{*[Errors]*}]:  ", e.getMessage());
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
			View view = (View) (this.getContent());
			view.setDescription(_resourcedesc);
			process.doCheckout(view.getId(), getUser());
			view.setCheckout(true);
			view.setCheckoutHandler(getUser().getId());
			setContent(view);
			this.addActionMessage("{*[core.dynaform.form.success.checkout]*}");
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
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
			View view = (View) (this.getContent());
			view.setDescription(_resourcedesc);
			process.doCheckin(view.getId(), getUser());
			view.setCheckout(false);
			view.setCheckoutHandler("");
			setContent(view);
			this.addActionMessage("{*[core.dynaform.form.success.checkin]*}");
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 对视图进行校验
	 * 
	 * @param view
	 * @throws Exception
	 */
	protected void doValidate(View view) throws Exception {
		String name = "";
		ParamsTable params = this.getParams();
		if (view != null) {
			set_viewid(view.getId());
			name = view.getName();
			params.setParameter("s_name", name);
		
			// 检验重名
			View tempView = ((ViewProcess) process).getViewByName(name, application);
			if (tempView != null) {
				if (view.getId() == null || view.getId().trim().length() <= 0) {// 判断新建不能重名
					throw new Exception("{*[viewExist]*}");
				} else if (view.getViewType() == tempView.getViewType()
						&& !view.getId().trim().equalsIgnoreCase(tempView.getId())) {// 修改不能重名
					throw new Exception("{*[viewExist]*}");
				}
			}
			
			//当视图打开类型为网格视图时,不应有操作列
			Set<Column> columns = view.getColumns();
			Iterator<Column> colIterator = columns.iterator();
			while(colIterator.hasNext()){
				int tempOpenType = view.getOpenType();
				Column col = colIterator.next();
				String tempType = col.getType();
				if(Column.COLUMN_TYPE_OPERATE.equals(tempType) || Column.COLUMN_TYPE_LOGO.equals(tempType)){
					if(tempOpenType == View.OPEN_TYPE_GRID){
						throw new Exception("{*[core.view.grid.operate]*}");
					}
				}
			}
		
		
		}
		
		// 非法字符串检验
		String invalidChars = getInvalidChars(name);
		if (!StringUtil.isBlank(invalidChars)) {
			throw new Exception("{*[core.view.name.exist.invalidchar]*}: "
					+ invalidChars);
		}

		Map<String, Column> columnMapping = view.getViewTypeImpl().getColumnMapping();
		StringBuffer errors = new StringBuffer();
		if (view.getViewType() == View.VIEW_TYPE_GANTT) {// 检验甘特视图映射
			for (int i = 0; i < GanttType.DEFAULT_KEY_FIELDS.length; i++) {
				String fieldCode = GanttType.DEFAULT_KEY_FIELDS[i];
				if (!columnMapping.containsKey(fieldCode)) {
					errors.append(GanttType.DEFAULT_FIELDS.get(fieldCode)).append(",");
				}
			}
		} else if (view.getViewType() == View.VIEW_TYPE_TREE) {// 检验树形视图映射
			for (int i = 0; i < TreeType.DEFAULT_KEY_FIELDS.length; i++) {
				String fieldCode = TreeType.DEFAULT_KEY_FIELDS[i];
				if (!columnMapping.containsKey(fieldCode)) {
					errors.append(TreeType.DEFAULT_FIELDS.get(fieldCode)).append(",");
				}
			}
		} else if (view.getViewType() == View.VIEW_TYPE_MAP) {// 校验地图视图映射
			for (int i = 0; i < MapType.DEFAULT_KEY_FIELDS.length; i++) {
				String fieldCode = MapType.DEFAULT_KEY_FIELDS[i];
				if (!columnMapping.containsKey(fieldCode)) {
					errors.append(MapType.DEFAULT_FIELDS.get(fieldCode)).append(",");
				}
			}
		} else if (view.getViewType() == View.VIEW_TYPE_CALENDAR) {// 校验日历视图映射
			for (int i = 0; i < CalendarType.DEFAULT_KEY_FIELDS.length; i++) {
				String fieldCode = CalendarType.DEFAULT_KEY_FIELDS[i];
				if (!columnMapping.containsKey(fieldCode)) {
					errors.append(CalendarType.DEFAULT_FIELDS.get(fieldCode)).append(",");
				}
			}
		}

		if (errors.length() > 0) {
			errors.deleteCharAt(errors.lastIndexOf(","));
			throw new Exception("(" + errors.toString() + "){*[require.mapping]*}");
		}
	}

	private String getInvalidChars(String name) {
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
		for (int i = 0; i < p.length; i++) {
			if (name != null && name.contains(p[i])) {			
				return p[i];
			}
		}
		return "";
	}
	
	/**
	 * 编辑
	 */
	public String doView() {
		String rtn ="";
		try{
			String id = getParams().getParameterAsString("id");
			rtn = super.doView();
			View view = (View) getContent();
			if(view.isCheckout() && !view.getCheckoutHandler().equals(getUser().getId())){
				addFieldError("", "{*[core.dynaform.form.message.warning.be.checkedout.by.other.developers]*}");
			}
			if (view.getLink() != null) {
				setLinkName(view.getLink().getName());
			}
			set_viewid(id);
		}catch(Exception e){
			e.printStackTrace();
		}

		return rtn;
	}

	/**
	 * 显示列表
	 * 
	 * @return
	 */
	public String list() {
		// 避免了干扰查询参数
		getParams().removeParameter("content.auth_role");
		getParams().removeParameter("content.auth_user");
		getParams().removeParameter("content.auth_fields");
		return super.doList();
	}

	/**
	 * 返回资源描述
	 * 
	 * @return 资源描述
	 */
	public String get_resourcedesc() {
		View view = (View) getContent();
		String rtn = null;
		if (view != null) {
			if (!StringUtil.isBlank(view.getDescription())) {
				rtn = view.getDescription();
			} else {
				String rid = view.getRelatedResourceid();
				if (rid != null) {
					try {
						ResourceProcess rp = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
						ResourceVO rvo = (ResourceVO) rp.doView(rid);
						if (rvo != null) {
							rtn = rvo.getDescription();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return rtn;
	}

	/**
	 * Set 资源描述
	 * 
	 * @param _resourcedesc
	 *            资源描述
	 */
	public void set_resourcedesc(String _resourcedesc) {
		this._resourcedesc = _resourcedesc;
	}

	/**
	 * 返回是否分页 ,true分页,false不分页
	 * 
	 * @return true或false
	 */
	public String get_isPagination() {
		View content = (View) getContent();
		if (content.isPagination()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 设置View是否分页 . true为分页,false 为不分页
	 * 
	 * @param _isPagination
	 *            是否分页标识
	 */
	public void set_isPagination(String _isPagination) {
		View content = (View) getContent();
		if (_isPagination != null) {
			if (_isPagination.trim().equalsIgnoreCase("true")) {
				content.setPagination(true);
				return;
			}
		}
		content.setPagination(false);
	}

	/**
	 * 返回是否显示数据总行. true为显示,false为不显示.
	 * 
	 * @return true或false
	 */
	public String get_isShowTotalRow() {
		View content = (View) getContent();
		if (content.isShowTotalRow()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * Set 是否显示数据总行. true为显示,false为不显示.
	 * 
	 * @param _isShowTotalRow
	 *            是否显示数据总行
	 */
	public void set_isShowTotalRow(String _isShowTotalRow) {
		View content = (View) getContent();
		if (_isShowTotalRow != null) {
			if (_isShowTotalRow.trim().equalsIgnoreCase("true")) {
				content.setShowTotalRow(true);
				return;
			}
		}
		content.setShowTotalRow(false);
	}

	public void set_readOnly(String _readOnly) {
		View content = (View) getContent();
		if (_readOnly != null) {
			if (_readOnly.trim().equalsIgnoreCase("true")) {
				content.setReadonly(Boolean.valueOf(true));
				return;
			}
		}
		content.setReadonly(false);
	}

	/**
	 * 
	 * 设置view是否只读
	 * 
	 * @return
	 */
	public String get_readOnly() {
		View content = (View) getContent();
		if (content.getReadonly().booleanValue()) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 获取打开View 类型. 三种类型:1.普通, "Normal";
	 * <p>
	 * 2.在子表单 "Open in pop window";
	 * <p>
	 * 3.父窗口"Open in working area";
	 * 
	 * @return 打开View 类型
	 */
	public static Map<Integer, String> get_OPENTYPE() {
		return _OPENTYPE;
	}

	/**
	 * 显示总行数文本
	 * 
	 * @return 显示总行数文本
	 */
	public String getTotalRowText() {
		return totalRowText;
	}

	/**
	 * Set 显示总行数文本
	 * 
	 * @param totalRowText
	 *            显示总行数文本
	 * 
	 */
	public void setTotalRowText(String totalRowText) {
		this.totalRowText = totalRowText;
	}

	/**
	 * 导出Document到Excel
	 * 
	 * @return "SUCESS"表示成功处理.
	 * @throws Exception
	 */
	public String expDocToExcel() throws Exception {
		try {
			ParamsTable parasm = getParams();
			changeOrderBy(parasm);

			String fileName = ((ViewProcess) process).expDocToExcel(get_viewid(), getUser(), parasm);
			ServletActionContext.getRequest().setAttribute("excelFileName", fileName);
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}
	}

	public String get_sortCol() {
		return _sortCol;
	}

	public void set_sortCol(String col) {
		_sortCol = col;
	}

	/**
	 * 返回排序状态 ASC(按升)，DESC(按降)
	 * 
	 * @return ASC(按升)，DESC(按降)
	 */
	public String get_sortStatus() {
		return _sortStatus;
	}

	/**
	 * 设置排序状态 ASC(按升)，DESC(按降)
	 * 
	 * @param status
	 */

	public void set_sortStatus(String status) {
		_sortStatus = status;
	}

	/**
	 * 
	 * @return
	 */
	public String get_orderby() {
		return _orderby;
	}

	/**
	 * 设置排序
	 * 
	 * @param _orderby
	 */
	public void set_orderby(String _orderby) {
		this._orderby = _orderby;
	}

	/**
	 * 获取父Document
	 * 
	 * @return Document对象
	 */
	public Document getParent() {
		return parent;
	}

	/**
	 * Set 父Document
	 * 
	 * @param parent
	 *            父Document对象
	 * @throws Exception
	 */
	public void setParent(Document parent) throws Exception {
		if (parent == null) {
			String parentid = (String) getParams().getParameter("parentid");
			if (parentid != null && parentid.trim().length() > 0) {
				DocumentProcess dp = createDocumentProcess(getContent().getApplicationid());
				this.parent = (Document) dp.doView(parentid);
			}
		} else {
			this.parent = parent;
		}
	}

	/**
	 * 获取当前Document
	 * 
	 * @return
	 */
	public Document getCurrentDocument() {
		return currentDocument;
	}

	/**
	 * Set 当前Document
	 * 
	 * @param currentDocument
	 * @throws Exception
	 */
	public void setCurrentDocument(Document currentDocument) throws Exception {
		this.currentDocument = currentDocument;
	}

	protected Document getSearchDocument(View view) {
		if (view.getSearchForm() != null) {
			try {
				return view.getSearchForm().createDocument(getParams(), getUser());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new Document();
	}

	private static DocumentProcess createDocumentProcess(String applicationid) throws CreateProcessException {
		DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class, applicationid);
		return process;
	}

	public String get_isRefresh() {
		View content = (View) getContent();
		if (content.isRefresh()) {
			return "true";
		} else {
			return "false";
		}
	}

	public void set_isRefresh(String refresh) {
		View content = (View) getContent();
		if (refresh != null) {
			if (refresh.trim().equalsIgnoreCase("true")) {
				content.setRefresh(true);
				return;
			}
		}
		content.setRefresh(false);
	}

	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
		getContent().setDomainid(domain);
	}

	public String getFlieName() throws Exception {
		return (String) getParams().getParameter("filename");
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	/**
	 * 验证查找文档参数，判断是否存在非法参数，防止sql注入。
	 */
	protected void validateDocumentValue(Document document) throws Exception {
		if (document != null) {
			for (Iterator<Item> it = document.getItems().iterator(); it.hasNext();) {
				Item item = it.next();
				if (item != null) {
					Object value = item.getValue();
					if (value instanceof String && specialSymbols(value.toString())) {
						throw new Exception("{*[core.special.symbols.error]*}: " + value.toString());
					}
				}
			}
		}
	}
	
	/**
	 * 视图打印
	 */
	public String printDoDisplayView() throws Exception {
		try {
			View view = (View)getContent();
			//将分页功能去掉
			view.setPagelines(null);
			view.setPagination(false);
			return getSuccessResult(view, DO_DISPLAY_VIEW);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				addFieldError("", e.getMessage());
			} else {
				addFieldError("errorMessage", e.toString());
			}
			return getInputResult((View) getContent());
		}
	}

}
