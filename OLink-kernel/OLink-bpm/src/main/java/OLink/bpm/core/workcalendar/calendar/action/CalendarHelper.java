package OLink.bpm.core.workcalendar.calendar.action;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import OLink.bpm.core.workcalendar.standard.ejb.StandardDayVO;
import OLink.bpm.init.InitializationException;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import eWAP.core.Tools;

public class CalendarHelper extends BaseHelper<CalendarVO> {

	public String domain;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public CalendarHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CalendarProcess.class));
	}

	public Map<String, String> getWorkCalendars() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		// map.put("", "{*[Select]*}");
		Collection<CalendarVO> list = ((CalendarProcess) process)
				.doQueryListByDomain(getDomain());
		if (list != null) {
			Iterator<CalendarVO> it = list.iterator();
			while (it.hasNext()) {
				CalendarVO cld = it.next();
				map.put(cld.getId(), "{*[" + cld.getName() + "]*}");
			}
		}
		return map;
	}

	public String getDefaultCalendarByDomain() throws Exception {
		CalendarProcess calendarProcess = ((CalendarProcess) process);
		if (calendarProcess != null) {
			CalendarVO vo = (CalendarVO) calendarProcess.doViewByName(
					CalendarType.NAMES[0], domain);
			if (vo != null)
				return vo.getId();
		}
		return "";
	}

	public void createCalendarByDomain(String domainid)
			throws InitializationException {
		CalendarProcess calendarProcess = ((CalendarProcess) process);

		if (calendarProcess != null) {
			String[] names = CalendarType.NAMES;
			String[] descriptions = CalendarType.DESCRIPTIONS;
			String[] timezones = CalendarType.TIMEZONES;
			// 标准日历
			{
				try {
					CalendarVO vo = new CalendarVO();
					StandardDayVO[] standard = null;
					vo.setId(Tools.getSequence());
					vo.setLastModifyDate(new Date());
					vo.setName(names[0]);
					vo.setRemark(descriptions[0]);
					vo.setType(timezones[0]);
					vo.setWorkingTime(8);
					vo.setDomainid(domainid);
					standard = new StandardDayVO[7];
					for (int i = 0; i < 7; i++) {
						standard[i] = new StandardDayVO();
						standard[i].setDomainid(domainid);
						if (i == 0 || i == 6) {
							standard[i].setId(Tools.getSequence());
							standard[i].setLastModifyDate(new Date());
							standard[i].setWeekDays(i);
							standard[i].setWorkingDayStatus("02");

						} else {
							standard[i].setId(Tools.getSequence());
							standard[i].setLastModifyDate(new Date());
							standard[i].setWeekDays(i);
							standard[i].setWorkingDayStatus("01");
							standard[i].setStartTime1("08:30");
							standard[i].setEndTime1("12:30");
							standard[i].setStartTime2("14:00");
							standard[i].setEndTime2("18:00");
						}
						vo.getStandardDays().add(standard[i]);
						standard[i].setCalendar(vo);
					}

					if (calendarProcess.doViewByName(vo.getName(), domainid) == null)
						calendarProcess.doCreate(vo);

				} catch (Exception e) {
					throw new InitializationException(e.getMessage());
				}
			}
			// 24小时工作日历
			{
				try {
					CalendarVO vo = new CalendarVO();
					StandardDayVO[] standard = null;
					vo.setId(Tools.getSequence());
					vo.setLastModifyDate(new Date());
					vo.setName(names[1]);
					vo.setRemark(descriptions[1]);
					vo.setType(timezones[0]);
					vo.setWorkingTime(24);
					vo.setDomainid(domainid);
					standard = new StandardDayVO[7];
					for (int i = 0; i < 7; i++) {
						standard[i] = new StandardDayVO();
						standard[i].setDomainid(domainid);
						standard[i].setId(Tools.getSequence());
						standard[i].setLastModifyDate(new Date());
						standard[i].setWeekDays(i);
						standard[i].setWorkingDayStatus("01");
						standard[i].setStartTime1("00:00");
						standard[i].setEndTime1("24:00");

						vo.getStandardDays().add(standard[i]);
						standard[i].setCalendar(vo);
					}
					if (calendarProcess.doViewByName(vo.getName(), domainid) == null)
						calendarProcess.doCreate(vo);

				} catch (Exception e) {
					throw new InitializationException(e.getMessage());
				}
			}
			// 夜班日历
			{
				try {
					CalendarVO vo = new CalendarVO();
					StandardDayVO[] standard = null;
					vo.setId(Tools.getSequence());
					vo.setLastModifyDate(new Date());
					vo.setName(names[2]);
					vo.setRemark(descriptions[2]);
					vo.setType(timezones[0]);
					vo.setWorkingTime(8);
					vo.setDomainid(domainid);
					standard = new StandardDayVO[7];
					for (int i = 0; i < 7; i++) {
						standard[i] = new StandardDayVO();
						standard[i].setDomainid(domainid);
						if (i == 0 || i == 6) {
							standard[i].setId(Tools.getSequence());
							standard[i].setLastModifyDate(new Date());
							standard[i].setWeekDays(i);
							standard[i].setWorkingDayStatus("02");

						} else {
							standard[i].setId(Tools.getSequence());
							standard[i].setLastModifyDate(new Date());
							standard[i].setWeekDays(i);
							standard[i].setWorkingDayStatus("01");
							standard[i].setStartTime1("00:00");
							standard[i].setEndTime1("03:00");
							standard[i].setStartTime2("04:00");
							standard[i].setEndTime2("08:00");
							standard[i].setStartTime3("23:00");
							standard[i].setEndTime3("24:00");
						}
						vo.getStandardDays().add(standard[i]);
						standard[i].setCalendar(vo);
					}
					if (calendarProcess.doViewByName(vo.getName(), domainid) == null)
						calendarProcess.doCreate(vo);

				} catch (Exception e) {
					throw new InitializationException(e.getMessage());
				}
			}
		}
	}

	public void removeCalendarByDomain(String domainid) throws Exception {
		CalendarProcess canlendarProcess = ((CalendarProcess) process);

		if (canlendarProcess != null) {
			Collection<CalendarVO> cols = canlendarProcess.doQueryListByDomain(domainid);
			if (cols != null) {
				Iterator<CalendarVO> it = cols.iterator();
				while (it.hasNext()) {
					CalendarVO vo = it.next();
					if (vo != null) {
						canlendarProcess.doRemove(vo.getId());
					}
				}
			}
		}
	}
}
