package OLink.bpm.core.workcalendar.calendar.action;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.core.workcalendar.standard.ejb.StandardDayVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.workcalendar.util.Month;
import OLink.bpm.core.workcalendar.util.Year;
import eWAP.core.Tools;

public class CalendarAction extends BaseAction<CalendarVO> {

	private Year year; // 存储一年12个月每月的所有天。

	private String yearValue;

	private int month;

	private String _calendarid;

	private CalendarVO calendar;
	/** DataPackage */
	private DataPackage<CalendarVO> datas = null;

	private int day;

	private String domain;

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public CalendarAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CalendarProcess.class),
				new CalendarVO());
	}

	public CalendarAction(IDesignTimeProcess<CalendarVO> proxy, ValueObject content) {
		super(proxy, content);
	}

	public Map<String, String> get_yearValueList() {
		Map<String, String> year = new HashMap<String, String>();
		int value = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = value - 10; i < value + 11; i++) {
			year.put("" + i, "" + i);
		}
		return year;
	}

	public String doNew() {

		return SUCCESS;
	}
	
	//@SuppressWarnings("unchecked")
	public String doCalendarlist(){
		try {
			this.validateQueryParams();
			DataPackage<CalendarVO> datas =((CalendarProcess) process).doQueryList(super.getParams());
			this.setDatas(datas);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}
	
	public String doDelete(){
		try {
			String[] selects = get_selects();
			if (selects != null && selects.length > 0) {
				CalendarProcess calProcess = (CalendarProcess) ProcessFactory.createProcess(CalendarProcess.class);
				calProcess.doRemove(selects);
				this.addActionMessage("{*[Delete]*}{*[Success]*}");
				return SUCCESS;
			} else {
				throw new Exception("{*[core.domain.notChoose]*}");
			}

		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doSave() {
		String ErrorField = "";
		calendar = (CalendarVO) getContent();
		try {
			CalendarVO cld = null;
			String name = calendar.getName().trim();
			if (StringUtil.isBlank(calendar.getId())) {
				if (_calendarid == null || _calendarid.trim().equals("")) {
					throw new Exception("{*[core.workcalendar.type.notempty]*}");
				}
			}
			if (StringUtil.isBlank(name)) {
				throw new Exception("{*[core.workcalendar.name.notempty]*}");
			}
			String keyName = CalendarType.getKeyByName(name);
			String domainid = getParams().getParameterAsString("domain");
			// 对旧数据兼容
			int count = ((CalendarProcess) process).getCountByName(keyName, domainid);
			if (count > 1) {
				throw new Exception("{*[core.form.exist]*}");
			} else {
				CalendarVO calender2 = (CalendarVO) process.doViewByName(keyName, domainid);
				if (calender2 != null) {
					if (calender2.getId().equals(calendar.getId())) {
						((CalendarProcess)process).doUpdate(calendar.getId(), calendar.getName(), calendar.getRemark());
						return SUCCESS;
					} else {
						throw new Exception("{*[core.form.exist]*}");
					}
				} else {
					if (!StringUtil.isBlank(calendar.getId())) {
						((CalendarProcess)process).doUpdate(calendar.getId(), calendar.getName(), calendar.getRemark());
						return SUCCESS;
					}
				}
			}
			
			cld = (CalendarVO) process.doView(_calendarid);
			calendar.setId(Tools.getSequence());
			calendar.setSortId(Tools.getTimeSequence());
			calendar.setApplicationid(cld.getApplicationid());
			calendar.setLastModifyDate(new Date());
			calendar.setType(cld.getType());
			calendar.setWorkingTime(cld.getWorkingTime());
			calendar.setSpecialDays(null);
			calendar.setDomainid(cld.getDomainid());
			Collection<StandardDayVO> datas = cld.getStandardDays();
			calendar.setStandardDays(null);
			if (datas != null) {
				Iterator<StandardDayVO> its = datas.iterator();
				StandardDayVO standard = null;
				StandardDayVO std = null;
				while (its.hasNext()) {
					standard = new StandardDayVO();
					std = its.next();
					standard.setEndTime1(std.getEndTime1());
					standard.setEndTime2(std.getEndTime2());
					standard.setEndTime3(std.getEndTime3());
					standard.setEndTime4(std.getEndTime4());
					standard.setEndTime5(std.getEndTime5());
					standard.setStartTime1(std.getStartTime1());
					standard.setStartTime2(std.getStartTime2());
					standard.setStartTime3(std.getStartTime3());
					standard.setStartTime4(std.getStartTime4());
					standard.setStartTime5(std.getStartTime5());
					standard.setWorkingDayStatus(std.getWorkingDayStatus());
					standard.setApplicationid(std.getApplicationid());
					standard.setWeekDays(std.getWeekDays());
					standard.setLastModifyDate(new Date());
					standard.setRemark(std.getRemark());
					standard.setDomainid(std.getDomainid());
					standard.setId(Tools.getSequence());
					standard.setSortId(Tools.getTimeSequence());
					standard.setCalendar(calendar);
					calendar.getStandardDays().add(standard);
				}
			}

			process.doCreate(calendar);
			_calendarid = calendar.getId();
			this.addActionMessage("{*[Save_Success]*}");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorField = e.getMessage() + "," + ErrorField;
		}
		setContent(calendar);
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
			return INPUT;
		}
		return SUCCESS;
	}

	public String doDisplayView() throws Exception {
		calendar = (CalendarVO) getContent();
		String ErrorField = "";
		try {
			if (calendar == null) {
				calendar = new CalendarVO();
			}
			if (_calendarid != null && _calendarid.trim().length() > 0) {
				calendar.setId(_calendarid);
			}
			if (calendar.getId() == null || calendar.getId().trim().equals("")) {
				calendar = (CalendarVO) process
						.doViewByName(CalendarType.getName(1), getDomain());
			} else {
				calendar = (CalendarVO) process
						.doView(calendar.getId());
			}
			setYear();
			setTodayDate();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorField = e.getMessage() + "," + ErrorField;
		}
		setContent(calendar);
		if (!ErrorField.equals("")) {
			if (ErrorField.endsWith(",")) {
				ErrorField = ErrorField.substring(0, ErrorField.length() - 1);
			}
			this.addFieldError("1", ErrorField);
		}

		return SUCCESS;
	}

	private void setYear() {
		int yearValue = Calendar.getInstance().get(Calendar.YEAR);
		try {
			int tempYear = Integer.parseInt(this.yearValue);
			yearValue = tempYear;
		} catch (Exception e) {
		}
		if (yearValue <= 1900) {
			yearValue = (Calendar.getInstance().get(Calendar.YEAR));
		}
		if (this.month < 1 || this.month > 12) {
			this.month = (Calendar.getInstance().get(Calendar.MONTH)) + 1;
		}
		Month[] monthsOfyear = new Month[12];
		if (calendar != null) {
			monthsOfyear[month - 1] = calendar.getMonth(yearValue, month - 1);
		}
		this.yearValue = String.valueOf(yearValue);
		if (year == null) {
			year = new Year();
		}
		year.setMonths(monthsOfyear);
		year.setYearValue(yearValue);
	}

	/**
	 * @SuppressWarnings webwork不支持泛型
	 */
	@SuppressWarnings("unchecked")
	private void setTodayDate() {
		getContext().getSession().put("year", year);
		getContext().getSession().put("month", "" + month);
		Calendar thisMonth = Calendar.getInstance();
		int thisyear = thisMonth.get(Calendar.YEAR);
		int thismonth = thisMonth.get(Calendar.MONTH);
		boolean flag = false;
		if (year.getYearValue() == thisyear && month == thismonth + 1)
			flag = true;
		if (day <= 0)
			day = thisMonth.get(Calendar.DAY_OF_MONTH);
		thisMonth.setFirstDayOfWeek(Calendar.SUNDAY);
		thisMonth.set(Calendar.YEAR, year.getYearValue());
		thisMonth.set(Calendar.MONTH, month - 1);
		thisMonth.set(Calendar.DAY_OF_MONTH, 1);
		int maxDay = thisMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		int firstIndex = thisMonth.get(Calendar.DAY_OF_WEEK) - 1;
		day = maxDay < day ? maxDay : day;
		int dayI = ((day + firstIndex - 1) / 7);
		int dayJ = ((day + firstIndex - 1) % 7);

		getContext().getSession().put("dayI", Integer.valueOf(dayI));
		getContext().getSession().put("dayJ", Integer.valueOf(dayJ));
		getContext().getSession().put("showToday", Boolean.valueOf(flag));

	}

	public String listStandardAndSpecial() {

		return SUCCESS;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public DataPackage<CalendarVO> getDatas() {
		return datas;
	}

	public void setDatas(DataPackage<CalendarVO> datas) {
		this.datas = datas;
	}

	public String getYearValue() {
		return yearValue;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public void setYearValue(String yearValue) {
		this.yearValue = yearValue;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		CalendarAction action = new CalendarAction();
		action.get_yearValueList();
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

	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(
					Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@Override
	public String doEdit() {
		return super.doEdit();
	}
	
}
