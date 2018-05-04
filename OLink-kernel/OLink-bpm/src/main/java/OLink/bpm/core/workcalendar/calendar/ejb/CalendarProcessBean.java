package OLink.bpm.core.workcalendar.calendar.ejb;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.workcalendar.calendar.dao.CalendarDAO;
import OLink.bpm.core.workcalendar.standard.ejb.DayPart;
import OLink.bpm.core.workcalendar.util.Month;
import OLink.bpm.util.DateUtil;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.log4j.Logger;

import eWAP.core.Tools;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.PersistenceUtils;

/**
 * 工作日ProcessBean
 * 
 * @author Chris
 * 
 */
public class CalendarProcessBean extends AbstractDesignTimeProcessBean<CalendarVO> implements CalendarProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4881996288102824528L;
	public final static Logger log = Logger.getLogger(CalendarProcessBean.class);

	protected IDesignTimeDAO<CalendarVO> getDAO() throws Exception {
		IDesignTimeDAO<CalendarVO> dao = (CalendarDAO) DAOFactory.getDefaultDAO(CalendarVO.class.getName());
		return dao;
	}

	public Collection<CalendarVO> doQueryListByDomain(String domainid) throws Exception {

		return ((CalendarDAO) getDAO()).doQueryList(domainid);
	}
	
	public DataPackage<CalendarVO> doQueryList(ParamsTable params) throws Exception {
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer
				.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer
				.parseInt(_pagelines) : 10;
		return ((CalendarDAO) getDAO()).doQueryListBySearch(params,page,lines);
	}

	public ValueObject doViewByName(String name, String domainid) throws Exception {
		return ((CalendarDAO) getDAO()).doViewByName(name, domainid);
	}
	
	public void doRemove(String[] pks) throws Exception {
		try {
			super.doRemove(pks);
		} catch (Exception e) {
			throw new Exception("{*[core.user.cannotremove]*}");
		}
	}

	/**
	 * 根据日历类别calendar及起始日期与结束日期，统计出calendar日历中startDate与endDate之间多少天是工作日
	 * 
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @param calendar
	 *            日历类别
	 * @return 工作日天数
	 * @throws Exception
	 */
	public double countWorkingDays(Date startDate, Date endDate, String calendar) throws Exception {
		double count = 0;
		Month month = null;
		boolean flag = false;
		CalendarVO vo = (CalendarVO) doView(calendar);
		if (endDate != null && startDate != null) {
			int startyear = 0;
			int startmonth = 0;
			int startdate = 0;
			int endyear = 0;
			int endmonth = 0;
			int enddate = 0;
			double count1 = 0;
			double count2 = 0;
			if (startDate.compareTo(endDate) == 1) {
				flag = true;
				Date temp = startDate;
				startDate = endDate;
				endDate = temp;
			}
			Calendar cld = Calendar.getInstance();
			{
				cld.setTime(startDate);
				month = vo.getMonth(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH));
				count1 = month.countRemainingTimes(cld.getTime());
				startyear = cld.get(Calendar.YEAR);
				startmonth = cld.get(Calendar.MONTH);
				startdate = cld.get(Calendar.DAY_OF_MONTH) + 1;

				cld.setTime(endDate);
				month = vo.getMonth(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH));
				count2 = month.countWorkedTimes(cld.getTime());
				endyear = cld.get(Calendar.YEAR);
				endmonth = cld.get(Calendar.MONTH);
				enddate = cld.get(Calendar.DAY_OF_MONTH) - 1;
			}
			double daytimeCount = vo.getWorkingTime();
			if (enddate - startdate == -2 && month.isWorkingDay(cld.getTime())) {
				count += count2 / daytimeCount;
			} else {
				count += (count1 + count2) / daytimeCount;
			}
			for (int y = 0; y <= (endyear - startyear); y++) {
				if (endyear - startyear > y) {
					for (; startmonth < 12; startmonth++) {

						month = vo.getMonth(startyear + y, startmonth);
						if (month != null) {
							count += month.countWorkingDays(startdate);
						}
						startdate = 1;
					}
					startmonth = 0;

				} else {
					for (; startmonth < endmonth; startmonth++) {
						month = vo.getMonth(startyear + y, startmonth);
						if (month != null) {
							count += month.countWorkingDays(startdate);
						}
						startdate = 1;
					}
					month = vo.getMonth(startyear + y, startmonth);
					if (month != null) {
						count += month.countWorkingDays(startdate, enddate);
					}
				}
			}
		}
		if (flag)
			return -count;
		return count;
	}

	public Date getNextDate(Date currentDate, int minuteCount, String calendar) throws Exception {
		CalendarVO vo = (CalendarVO) doView(calendar);
		Calendar cld = Calendar.getInstance();
		cld.setTime(currentDate);
		cld.set(Calendar.SECOND, 0);
		int yearCount = cld.get(Calendar.YEAR);
		int monthCount = cld.get(Calendar.MONTH);

		Month month = vo.getMonth(yearCount, monthCount);
		double remainingTiems = 0.0;
		if (month.isWorkingDay(cld.getTime()))
			remainingTiems = month.countRemainingTimes(cld.getTime());
		minuteCount -= (remainingTiems * 60.0);
		if (minuteCount > 0) { // 非当天超时
			double hourCount = month.getStartTimeOfDay(currentDate);
			int hour = (int) (hourCount);
			int minute = (int) ((hourCount - hour) * 60);
			cld.set(Calendar.HOUR_OF_DAY, hour);
			cld.set(Calendar.MINUTE, minute);
			while (minuteCount > 0) {
				currentDate = DateUtil.getNextDateByDayCount(cld.getTime(), 1);
				cld.setTime(currentDate);
				if (monthCount < cld.get(Calendar.MONTH)) {
					yearCount = cld.get(Calendar.YEAR);
					monthCount = cld.get(Calendar.MONTH);
					month = vo.getMonth(yearCount, monthCount);
				}
				if (month.isWorkingDay(cld.getTime())) {
					double workedTimes = month.countWorkedTimes(cld.getTime());
					int worked = (int) (workedTimes * 60.0);
					if (minuteCount > worked) {
						minuteCount -= worked;
						remainingTiems = month.countRemainingTimes(cld.getTime());
						minuteCount -= (remainingTiems * 60.0);
					} else {
						cld.add(Calendar.MINUTE, minuteCount);
						if (monthCount < cld.get(Calendar.MONTH)) {
							yearCount = cld.get(Calendar.YEAR);
							monthCount = cld.get(Calendar.MONTH);
							month = vo.getMonth(yearCount, monthCount);
						}
						workedTimes = month.countWorkedTimes(cld.getTime());
						if ((workedTimes * 60.0) > minuteCount || (workedTimes * 60.0) < minuteCount) {
							return getNextDate(cld.getTime(), minuteCount - (int) (workedTimes * 60.0), calendar);
						} else {
							break;
						}
							
					}
				}
			}
			if (minuteCount < 0) {
				minuteCount += (remainingTiems * 60.0);
				cld.add(Calendar.MINUTE, minuteCount);
				if (monthCount < cld.get(Calendar.MONTH)) {
					yearCount = cld.get(Calendar.YEAR);
					monthCount = cld.get(Calendar.MONTH);
					month = vo.getMonth(yearCount, monthCount);
				}
				double workedTimes = month.countWorkedTimes(cld.getTime());
				if (minuteCount > (workedTimes * 60.0)) {
					return getNextDate(cld.getTime(), minuteCount - (int) (workedTimes * 60.0), calendar);
				}
			}
		} else if (minuteCount < 0) { // 当天超时
			minuteCount += (remainingTiems * 60.0);
			cld.add(Calendar.MINUTE, minuteCount);
			double workedTimes = month.countWorkedTimes(cld.getTime());
			if (minuteCount > (workedTimes * 60.0)) {
				return getNextDate(cld.getTime(), minuteCount - (int) (workedTimes * 60.0), calendar);
			}
		} else {
			cld.add(Calendar.MINUTE, (int) (remainingTiems * 60.0));
		}

		return cld.getTime();
	}

	public Date getNextDateByMinuteCount(Date currentDate, int minuteCount, String calendar) throws Exception {
		CalendarVO vo = (CalendarVO) doView(calendar);
		Calendar cld = Calendar.getInstance();
		cld.setTime(currentDate);

		if (minuteCount > 0) {
			DayPart dayPart = vo.findDayPart(cld.getTime());
			if (dayPart != null && !dayPart.isEnd(cld.getTime())) {
				int fromHour = cld.get(Calendar.HOUR_OF_DAY);
				int fromMinute = cld.get(Calendar.MINUTE);

				int toHour = dayPart.getToHour();
				int toMinute = dayPart.getToMinute();

				int duration = (toHour - fromHour) * 60 + (toMinute - fromMinute);
				minuteCount -= duration;

				cld.add(Calendar.MINUTE, duration);
			} else {
				cld.setTime(vo.findNextStartDate(cld.getTime()));
			}

			return getNextDateByMinuteCount(cld.getTime(), minuteCount, calendar);
		}

		cld.add(Calendar.MINUTE, minuteCount);

		return cld.getTime();
	}

	// 新修改
	public double countTimesOfWorkingDays(Date startDate, Date endDate, String calendar) throws Exception {
		double count = 0;
		CalendarVO vo = (CalendarVO) doView(calendar);
		boolean isNegative = false;

		if (endDate != null && startDate != null) {
			if (startDate.compareTo(endDate) == 1) {
				isNegative = true;
				Date temp = startDate;
				startDate = endDate;
				endDate = temp;
			}

			Date currDate = startDate;				
			
			// 处理开始日期剩余时间
			boolean isStart = true;
			while (currDate.compareTo(endDate) <= 0) {
				
				DayPart dayPart = vo.findDayPart(currDate);
				
				if (dayPart != null) {
					Calendar cald = Calendar.getInstance();
					cald.setTime(currDate);
					cald.set(Calendar.HOUR_OF_DAY, dayPart.getToHour());
					cald.set(Calendar.MINUTE, dayPart.getToMinute());
					boolean flag = cald.getTime().compareTo(endDate) <= 0;
					
					if (flag) {
						count += dayPart.getRemainingMinutes(currDate);
					} else if (dayPart.includes(currDate)) {
						// 处理结束日期剩余时间
						Calendar cald2 = Calendar.getInstance();
						cald2.setTime(endDate);
						int fromHour = cald2.get(Calendar.HOUR_OF_DAY);
						int fromMinute = cald2.get(Calendar.MINUTE);
						int toHour = dayPart.getFromHour();
						int toMinute = dayPart.getFromMinute();
						if (isStart) {
							cald2 = Calendar.getInstance();
							cald2.setTime(startDate);
							toHour = cald2.get(Calendar.HOUR_OF_DAY);
							toMinute = cald2.get(Calendar.MINUTE);
							isStart = false;
						}
						count += (fromHour - toHour) * 60 + (fromMinute - toMinute);
					}
					
					if (isStart) {
						cald.set(Calendar.HOUR_OF_DAY, dayPart.getFromHour());
						cald.set(Calendar.MINUTE, dayPart.getFromMinute());
						currDate = cald.getTime();
						isStart = false;
					}
				}
				isStart = false;	
				currDate = vo.findNextStartDate(currDate);
			}
		}

		count = Math.round(count * 100.0) / 100.0 / 60;

		if (isNegative)
			return -count;

		return count;
	}

	public double countTimesOfWorkingDays2(Date startDate, Date endDate, String calendar) throws Exception {
		double count = 0;
		CalendarVO vo = (CalendarVO) doView(calendar);
		boolean flag = false;
		Month month = null;
		if (endDate != null && startDate != null) {
			int startyear = 0;
			int startmonth = 0;
			int startdate = 0;
			int endyear = 0;
			int endmonth = 0;
			int enddate = 0;
			double count1 = 0;
			double count2 = 0;
			if (startDate.compareTo(endDate) == 1) {
				flag = true;
				Date temp = startDate;
				startDate = endDate;
				endDate = temp;
			}
			Calendar cld = Calendar.getInstance();
			{
				cld.setTime(startDate);
				month = vo.getMonth(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH));
				count1 = month.countRemainingTimes(cld.getTime());
				startyear = cld.get(Calendar.YEAR);
				startmonth = cld.get(Calendar.MONTH);
				startdate = cld.get(Calendar.DAY_OF_MONTH) + 1;

				cld.setTime(endDate);
				month = vo.getMonth(cld.get(Calendar.YEAR), cld.get(Calendar.MONTH));
				count2 = month.countWorkedTimes(cld.getTime());
				endyear = cld.get(Calendar.YEAR);
				endmonth = cld.get(Calendar.MONTH);
				enddate = cld.get(Calendar.DAY_OF_MONTH) - 1;
			}
			if (startdate - enddate == 2 && month.isWorkingDay(cld.getTime())) {
				count += vo.getWorkingTime() - (vo.getWorkingTime() - count1) - (vo.getWorkingTime() - count2);
			} else {
				count += count1 + count2;
			}
			for (int y = 0; y <= (endyear - startyear); y++) {
				if (endyear - startyear > y) {
					for (int m = startmonth; m < 12; m++) {
						month = vo.getMonth(startyear + y, m);
						if (month != null) {
							count += month.countWorkingDays(startdate) * vo.getWorkingTime();
						}
						startdate = 1;
					}
					startmonth = 0;

				} else {
					for (; startmonth < endmonth; startmonth++) {
						month = (vo).getMonth(startyear + y, startmonth);
						if (month != null) {
							count += month.countWorkingDays(startdate) * vo.getWorkingTime();
						}
						startdate = 1;
					}
					month = vo.getMonth(startyear + y, startmonth);
					if (month != null) {
						int count8 = month.countWorkingDays(startdate, enddate) * vo.getWorkingTime();
						count += count8;

					}
				}
			}
		}
		count = Math.round(count * 100.0) / 100.0;
		if (flag)
			return -count;
		return count;
	}

	/**
	 * 根据日历类别calendar及年份月份，统计出calendar日历中year年month月有多少天是工作日
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @param calendar
	 *            日历类别
	 * @return 工作日天数
	 * @throws Exception
	 */
	public double countWorkingDays(String year, String month, String calendar) throws Exception {
		CalendarVO vo = ((CalendarVO) doView(calendar));
		int yearInt = Integer.parseInt(year);
		int monthInt = Integer.parseInt(month);
		Month mnth = vo.getMonth(yearInt, monthInt);
		if (mnth != null) {
			return mnth.countWorkingDays();
		}
		return 0;
	}

	/**
	 * 根据日历类别calendar及年份，统计出calendar日历中year年中有多少天是工作日
	 * 
	 * @param year
	 *            年份
	 * @param calendar
	 *            日历类别
	 * @return 工作日天数
	 * @throws Exception
	 */
	public double countWorkingDays(String year, String calendar) throws Exception {
		double count = 0;
		for (int i = 1; i < 13; i++) {
			count += countWorkingDays(year, "" + i, calendar);
		}
		return count;
	}

	public void doSave(ValueObject vo) throws Exception {

		try {
			if (getDAO().find(vo.getId()) != null)
				getDAO().update(vo);
			else {
				if (vo.getId() == null || vo.getId().equals("")) {
					vo.setId(Tools.getSequence());
				}
				if (vo.getSortId() == null || vo.getSortId().equals("")) {
					vo.setSortId(Tools.getTimeSequence());
				}
				getDAO().create(vo);
			}
		} catch (Exception e) {
			throw e;
		}

	}

	static public void main(String[] args) throws Exception {
		String calendarId = "11de-c138-783b5ff8-9a62-8bacb70a86e1";
		Calendar cld = Calendar.getInstance();
		Date startDate = DateUtil.parseDate("2009-9-30 08:00:00", "yyyy-MM-dd HH:mm:ss");
		cld.setTime(startDate);

		Date endDate = DateUtil.parseDate("2009-9-30 20:00:00", "yyyy-MM-dd HH:mm:ss");

		CalendarProcessBean cpb = new CalendarProcessBean();
		// int minuteCount = 5 * 60 + 1;

		long start0 = System.currentTimeMillis();

		log.info("getNextDateByMinuteCount: "
				+ DateUtil.format(cpb.getNextDateByMinuteCount(startDate, 60 * 8, calendarId), "yyyy-MM-dd HH:mm:ss"));
		log.info("countTimesOfWorkingDays: " + cpb.countTimesOfWorkingDays(startDate, endDate, calendarId));
		log.info("total waste: " + (System.currentTimeMillis() - start0) + " ms");
	}

	public int getCountByName(String name, String domainid) throws Exception {
		return ((CalendarDAO)getDAO()).queryCountByName(name, domainid);
	}

	public void doUpdate(String id, String name, String remark) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			((CalendarDAO)getDAO()).saveCalendar(id, name, remark);
			PersistenceUtils.commitTransaction();
			//PermissionPackage.clearCache();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}
}
