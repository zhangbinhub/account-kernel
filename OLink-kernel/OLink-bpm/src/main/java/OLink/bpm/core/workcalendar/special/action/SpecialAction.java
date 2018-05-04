package OLink.bpm.core.workcalendar.special.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.core.workcalendar.special.ejb.SpecialDayProcess;
import OLink.bpm.core.workcalendar.special.ejb.SpecialDayVO;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.ProcessFactory;
import eWAP.core.Tools;


public class SpecialAction extends BaseAction<SpecialDayVO> {

	private SpecialDayVO specialDay;

	private CalendarVO calendar;

	private String _calendarid;

	private String _stime;

	private String _etime;

	private String domain;

	public SpecialAction(IDesignTimeProcess<SpecialDayVO> proxy, ValueObject content) {
		super(proxy, content);

	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public SpecialAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(SpecialDayProcess.class), new SpecialDayVO());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String doNew() {
		try {
			specialDay = new SpecialDayVO();
			specialDay.setId(Tools.getSequence());
			specialDay.setWorkingDayStatus("02");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setContent(this.specialDay);
		return SUCCESS;
	}

	public String doEdit() {
		try {
			Map<?, ?> params = getContext().getParameters();
			Object obj = params.get("id");
			String id = ((String[]) obj)[0];
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String doList() {

		String ErrorField = "";
		ParamsTable params = getParams();
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		params.setParameter("_currpage", Integer.valueOf(page));
		params.setParameter("_pagelines", Integer.valueOf(lines));
		params.setParameter("t_calendar", _calendarid);
		params.setParameter("s_domainid", getDomain());
		try {
			setDatas(process.doQuery(params, getUser()));
		} catch (Exception e) {
			ErrorField = e.getMessage() + "," + ErrorField;
		}
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}
		return SUCCESS;
	}

	public String doView() {

		String ErrorField = "";
		Map<?, ?> params = getContext().getParameters();
		String[] ids = (String[]) (params.get("id"));
		String id = null;
		if (ids != null && ids.length > 0) {
			id = ids[0];
		}

		ValueObject contentVO = null;
		try {
			contentVO = process.doView(id);
		} catch (Exception e) {
			ErrorField = e.getMessage() + "," + ErrorField;
		}
		setContent(contentVO);
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}
		return SUCCESS;
	}

	public String doSave() {

		specialDay = (SpecialDayVO) getContent();

		String ErrorField = "";
		if (specialDay != null) {
			try {
				setDate();
				if (specialDay.getCalendar() == null) {
					CalendarProcess process = (CalendarProcess) ProcessFactory.createProcess(CalendarProcess.class);
					calendar = (CalendarVO) process.doView(_calendarid);
					specialDay.setCalendar(calendar);
				}
				if(!specialDay.getStartDate().after(specialDay.getEndDate())){
					process.doUpdate(specialDay);
					this.addActionMessage("{*[Save_Success]*}");
				}else{
					ErrorField = "{*[page.core.calendar.overoftime]*}" + "," + ErrorField;
				}
				
				
				
			} catch (Exception e) {
				ErrorField = e.getMessage() + "," + ErrorField;
			}

		}
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}

		return SUCCESS;
	}

	public String doDelete() {

		String ErrorField = "";
		if (_selects != null) {
			for (int i = 0; i < _selects.length; i++) {
				String id = _selects[i];
				try {
					process.doRemove(id);
				} catch (Exception e) {
					ErrorField = e.getMessage() + "," + ErrorField;
				}
			}
			this.addActionMessage("{*[delete.successful]*}");
		}
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}

		return SUCCESS;

	}

	public SpecialDayVO getSpecialDay() {
		return specialDay;
	}

	public CalendarVO getCalendar() {
		return calendar;
	}

	public void setSpecialDay(SpecialDayVO specialDay) {
		this.specialDay = specialDay;
	}

	public void setCalendar(CalendarVO calendar) {
		this.calendar = calendar;
	}

	public String get_calendarid() {
		return _calendarid;
	}

	public void set_calendarid(String _calendarid) {
		this._calendarid = _calendarid;
	}

	/**
	 * 返回工作状态
	 * 
	 * @return "true"为可用，"false"为不可用
	 * @throws Exception
	 */
	public String get_strstatus() throws Exception {
		SpecialDayVO std = (SpecialDayVO) getContent();
		if (std.getWorkingDayStatus().equals("01")) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 设置 工作状态
	 * 
	 * @param strname
	 *            用户状态字符串true or false
	 * @throws Exception
	 */
	public void set_strstatus(String strname) throws Exception {
		SpecialDayVO std = (SpecialDayVO) getContent();
		if (strname != null) {
			if (strname.equalsIgnoreCase("true")) {
				std.setWorkingDayStatus("01");
			} else {
				std.setWorkingDayStatus("02");
			}
		}
	}

	public String get_stime() throws Exception {
		if (((SpecialDayVO) getContent()).getStartDate() != null) {
			Date dateTime = ((SpecialDayVO) getContent()).getStartDate();
			_stime = DateUtil.format(dateTime, "HH:mm");
		}
		return _stime;
	}

	public void set_stime(String _stime) {
		this._stime = _stime;
	}

	public String get_etime() throws Exception {
		if (((SpecialDayVO) getContent()).getEndDate() != null) {
			Date dateTime = ((SpecialDayVO) getContent()).getEndDate();
			_etime = DateUtil.format(dateTime, "HH:mm");
		}
		return _etime;
	}

	public void set_etime(String _etime) {
		this._etime = _etime;
	}

	public void setDate() throws Exception {
		Date date;
		String dateStr;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			date = specialDay.getEndDate();
			dateStr = DateUtil.format(date, "yyyy-MM-dd");
			dateStr += " " + _etime;
			if (_etime != null && !_etime.equals(""))
				dateStr += " " + _etime;
			else
				dateStr += " 23:59";
			date = formatter.parse(dateStr);
			specialDay.setEndDate(date);
		} catch (Exception e) {
			throw new Exception("{*[core.workcalendar.special.enddate.patternerror]*}");
		}
		try {
			dateStr = DateUtil.format(specialDay.getStartDate(), "yyyy-MM-dd");
			dateStr += " " + _stime;
			if (_stime != null && !_stime.equals(""))
				dateStr += " " + _stime;
			else
				dateStr += " 00:00";
			date = formatter.parse(dateStr);
			specialDay.setStartDate(date);
		} catch (Exception e) {
			throw new Exception("{*[core.workcalendar.special.startdate.patternerror]*}");
		}
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
}
