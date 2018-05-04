package OLink.bpm.core.workcalendar.standard.action;

import java.util.Map;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import OLink.bpm.core.workcalendar.standard.ejb.BaseDay;
import OLink.bpm.core.workcalendar.standard.ejb.StandardDayProcess;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.core.workcalendar.standard.ejb.StandardDayVO;
import OLink.bpm.util.ProcessFactory;

public class StandardAction extends BaseAction<StandardDayVO> {

	private BaseDay standardDay;

	private CalendarVO calendar;

	private String _calendarid;

	private String domain;

	public StandardAction(IDesignTimeProcess<StandardDayVO> proxy, ValueObject content) {
		super(proxy, content);

	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public StandardAction() throws Exception {
		super(ProcessFactory.createProcess(StandardDayProcess.class), new StandardDayVO());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String doList() {
		try {
			String ErrorField = "";
			ParamsTable params = new ParamsTable();
			params.setParameter("_currpage", Integer.valueOf(1));
			params.setParameter("_pagelines", Integer.valueOf(7));
			params.setParameter("_orderby", "weekDays");
			params.setParameter("_desc", "ASC");
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
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String doEdit() {

		String ErrorField = "";

		Map<?, ?> params = getContext().getParameters();
		String[] ids = (String[]) (params.get("id"));
		String id = null;
		if (ids != null && ids.length > 0) {
			id = ids[0];
		}

		ValueObject vo = null;
		try {
			vo = process.doView(id);
		} catch (Exception e) {
			ErrorField = e.getMessage() + "," + ErrorField;
		}
		setContent(vo);
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}
		return SUCCESS;
	}

	public String doSave() {

		this.standardDay = (BaseDay) this.getContent();
		String ErrorField = "";
		if (this.standardDay != null) {
			try {
				if (standardDay.getCalendar() == null) {
					CalendarProcess process = (CalendarProcess) ProcessFactory.createProcess(CalendarProcess.class);
					calendar = (CalendarVO) process.doView(_calendarid);
					standardDay.setCalendar(calendar);
				}
				process.doUpdate(standardDay);
				this.addActionMessage("{*[Save_Success]*}");
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
		}
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}

		return SUCCESS;

	}

	public BaseDay getStandardDay() {
		return standardDay;
	}

	public void setStandardDay(BaseDay standardDay) {
		this.standardDay = standardDay;
	}

	public CalendarVO getCalendar() {
		return calendar;
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
		BaseDay std = (BaseDay) getContent();
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
		BaseDay std = (BaseDay) getContent();
		if (strname != null) {
			if (strname.equalsIgnoreCase("true")) {
				std.setWorkingDayStatus("01");
			} else {
				std.setWorkingDayStatus("02");
			}
		}
	}

	public void set_strweekDays(String strname) throws Exception {
		BaseDay std = (BaseDay) getContent();
		if (strname != null) {
			if (strname.equals("Sunday")) {
				std.setWeekDays(0);
			} else if (strname.equals("Monday")) {
				std.setWeekDays(1);
			} else if (strname.equals("Tuesday")) {
				std.setWeekDays(2);
			} else if (strname.equals("Wednesday")) {
				std.setWeekDays(3);
			} else if (strname.equals("Thursday")) {
				std.setWeekDays(4);
			} else if (strname.equals("Friday")) {
				std.setWeekDays(5);
			} else if (strname.equals("Saturday")) {
				std.setWeekDays(6);
			}
		}
	}

	public String get_strweekDays() throws Exception {
		BaseDay std = (BaseDay) getContent();
		String strname = "";
		if (std != null) {
			if (std.getWeekDays() == 0) {
				strname = "Sunday";
			} else if (std.getWeekDays() == 1) {
				strname = "Monday";
			} else if (std.getWeekDays() == 2) {
				strname = "Tuesday";
			} else if (std.getWeekDays() == 3) {
				strname = "Wednesday";
			} else if (std.getWeekDays() == 4) {
				strname = "Thursday";
			} else if (std.getWeekDays() == 5) {
				strname = "Friday";
			} else if (std.getWeekDays() == 6) {
				strname = "Saturday";
			}
		}
		return strname;
	}

	public static void main(String[] args) throws Exception {
		/*
		StandardAction action = new StandardAction();
		// action.set_calendarid();
		java.util.Map parameters = new java.util.HashMap();
		parameters.put("t_calendar", "01b1fc1c-8d61-e140-8e92-4f70cd75b66a");
		StandardAction.getContext().setParameters(parameters);
		// action.set_calendarid("01b20350-ac12-b980-98c4-ca9ca94ffcfe");
		action.doList();
		Collection datas = action.getDatas().datas;
		if (datas != null) {
			for (java.util.Iterator its = datas.iterator(); its.hasNext();) {
				BaseDay stand = (BaseDay) its.next();
			}
		} 
		*/
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
