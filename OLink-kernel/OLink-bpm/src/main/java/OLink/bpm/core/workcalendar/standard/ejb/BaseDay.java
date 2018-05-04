package OLink.bpm.core.workcalendar.standard.ejb;

import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;

public class BaseDay extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public final String WORKING_DAY = "01";

	public final String NOT_WORKING_DAY = "02";

	private String id;

	private String remark;// 备注

	private CalendarVO calendar;// 日历类别

	private String workingDayStatus; // 01:工作日；02:非工作日；

	private int weekDays;// 星期

	private String cssClass; // cssClassName

	private int spc;

	private boolean isBorder = false;

	private Date lastModifyDate;// 最后修改时间

	private String startTime1;

	private String startTime2;

	private String startTime3;

	private String startTime4;

	private String startTime5;

	private String endTime1;

	private String endTime2;

	private String endTime3;

	private String endTime4;

	private String endTime5;

	private int dayIndex;

	public boolean isWorkingDay() {
		return WORKING_DAY.equals(workingDayStatus.trim());
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getLastModifyDate() {
		return this.lastModifyDate;
	}

	public void setLastModifyDate(Date lastModifyDate) {
		this.lastModifyDate = lastModifyDate;
	}

	public String getWorkingDayStatus() {
		return this.workingDayStatus;
	}

	public void setWorkingDayStatus(String workingDayStatus) {
		this.workingDayStatus = workingDayStatus;
	}

	public int getWeekDays() {
		return this.weekDays;
	}

	public void setWeekDays(int weekDays) {
		this.weekDays = weekDays;
	}

	public String getRemark() {
		if (this.remark != null)
			this.remark = this.remark.trim();
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getEndTime1() {
		if (this.endTime1 == null)
			this.endTime1 = "";
		return endTime1;
	}

	public String getEndTime2() {
		if (this.endTime2 == null)
			this.endTime2 = "";
		return endTime2;
	}

	public String getEndTime3() {
		if (this.endTime3 == null)
			this.endTime3 = "";
		return endTime3;
	}

	public String getEndTime4() {
		if (this.endTime4 == null)
			this.endTime4 = "";
		return endTime4;
	}

	public String getEndTime5() {
		if (this.endTime5 == null)
			this.endTime5 = "";
		return endTime5;
	}

	public String getStartTime1() {
		if (this.startTime1 == null)
			this.startTime1 = "";
		return startTime1;
	}

	public String getStartTime2() {
		if (this.startTime2 == null)
			this.startTime2 = "";
		return startTime2;
	}

	public String getStartTime3() {
		if (this.startTime3 == null)
			this.startTime3 = "";
		return startTime3;
	}

	public String getStartTime4() {
		if (this.startTime4 == null)
			this.startTime4 = "";
		return startTime4;
	}

	public String getStartTime5() {
		if (this.startTime5 == null)
			this.startTime5 = "";
		return startTime5;
	}

	public void setEndTime1(String endTime1) {
		this.endTime1 = endTime1;
	}

	public void setEndTime2(String endTime2) {
		this.endTime2 = endTime2;
	}

	public void setEndTime3(String endTime3) {
		this.endTime3 = endTime3;
	}

	public void setEndTime4(String endTime4) {
		this.endTime4 = endTime4;
	}

	public void setEndTime5(String endTime5) {
		this.endTime5 = endTime5;
	}

	public void setStartTime1(String startTime1) {
		this.startTime1 = startTime1;
	}

	public void setStartTime2(String startTime2) {
		this.startTime2 = startTime2;
	}

	public void setStartTime3(String startTime3) {
		this.startTime3 = startTime3;
	}

	public void setStartTime4(String startTime4) {
		this.startTime4 = startTime4;
	}

	public void setStartTime5(String startTime5) {
		this.startTime5 = startTime5;
	}

	public CalendarVO getCalendar() {
		return calendar;
	}

	public void setCalendar(CalendarVO calendar) {
		this.calendar = calendar;
	}

	public String toHtml() {
		StringBuffer htmlBuilder = new StringBuffer();

		if (getDayIndex() == 0) {
			htmlBuilder.append("<td align='center' class='" + getCssClass()
					+ "'>&nbsp;</td>");
		} else {
			if (this.getSpc() == 3) {
				htmlBuilder
						.append("<td align='center' class='"
								+ getCssClass()
								+ "' onmouseover='onMouseOver(this,1);' onmouseout='onMouseOut(this,3);' ><a href='javascript:dayInfo("
								+ getDayIndex() + ");'>" + getDayIndex()
								+ "</a></td>");
			} else if (this.getSpc() == 0) {
				htmlBuilder
						.append("<td align='center' class='"
								+ getCssClass()
								+ "' onmouseover='onMouseOver(this,0);' onmouseout='onMouseOut(this,0);' ><a href='javascript:dayInfo("
								+ getDayIndex() + ");'>" + getDayIndex()
								+ "</a></td>");
			} else if (this.getSpc() == 1) {
				htmlBuilder
						.append("<td align='center' class='"
								+ getCssClass()
								+ "' onmouseover='onMouseOver(this,1);' onmouseout='onMouseOut(this,1);' ><a href='javascript:dayInfo("
								+ getDayIndex() + ");'>" + getDayIndex()
								+ "</a></td>");
			} else if (this.getSpc() == 2) {
				htmlBuilder
						.append("<td align='center' class='"
								+ getCssClass()
								+ "' onmouseover='onMouseOver(this,1);' onmouseout='onMouseOut(this,2);' ><a href='javascript:dayInfo("
								+ getDayIndex() + ");'>" + getDayIndex()
								+ "</a></td>");
			}

		}

		return htmlBuilder.toString();
	}

	public String getDayInfo(int year, int month) {
		StringBuffer htmlBuilder = new StringBuffer();
		if (dayIndex > 0) {
			String srt = "{*[Working-day]*}";
			if (!isWorkingDay())
				srt = "{*[Day-off]*}";
			htmlBuilder
					.append("<table valign='top' style='font-size: 14px' height='200' width='250' border='0' cellpadding='0' cellspacing='0' bordercolor='#FFFFFF'>");
			htmlBuilder
					.append("<tr align='center'><th style='font-size: 16px'>"
							+ year + "-" + month + "-" + dayIndex + "   " + srt
							+ "</th></tr>");
			if (getStartTime1() != null && !getStartTime1().trim().equals(""))
				htmlBuilder.append("<tr><td align='center'>" + getStartTime1()
						+ " ~ " + getEndTime1() + "</td></tr>");
			if (getStartTime2() != null && !getStartTime2().trim().equals(""))
				htmlBuilder.append("<tr><td align='center'>" + getStartTime2()
						+ " ~ " + getEndTime2() + "</td></tr>");
			if (getStartTime3() != null && !getStartTime3().trim().equals(""))
				htmlBuilder.append("<tr><td align='center'>" + getStartTime3()
						+ " ~ " + getEndTime3() + "</td></tr>");
			if (getStartTime4() != null && !getStartTime4().trim().equals(""))
				htmlBuilder.append("<tr><td align='center'>" + getStartTime4()
						+ " ~ " + getEndTime4() + "</td></tr>");
			if (getStartTime5() != null && !getStartTime5().trim().equals(""))
				htmlBuilder.append("<tr><td align='center'>" + getStartTime5()
						+ " ~ " + getEndTime5() + "</td></tr>");
			if (getRemark() != null && !getRemark().trim().equals(""))
				htmlBuilder
						.append("<tr><td align='center'><table width ='200' style='font-size: 14px' border='0' cellpadding='0' cellspacing='0' valign='top'><tr><td><b>{*[Description]*}:</b></td></tr><tr><td>&nbsp;&nbsp;&nbsp;&nbsp;{*["
								+ getRemark()
								+ "]*}</td></tr></table></td></tr>");
			htmlBuilder.append("</table>");
		}
		return htmlBuilder.toString();
	}

	public int getDayIndex() {
		return this.dayIndex;
	}

	public void setDayIndex(int dayIndex) {
		this.dayIndex = dayIndex;
	}

	public boolean isBorder() {
		return this.isBorder;
	}

	public void setBorder(boolean isBorder) {
		this.isBorder = isBorder;
	}

	public String getCssClass() {
		if (this.cssClass == null || this.cssClass.trim().equals("")) {
			if (getSpc() == 0) {
				this.cssClass = "Wwday";
			} else if (getSpc() == 1) {
				this.cssClass = "Wday";
			} else if (getSpc() == 2) {
				this.cssClass = "Wspcday";
			} else if (getSpc() == 3) {
				this.cssClass = "Wtoday";
			}
		}
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public int getSpc() {
		if (this.spc == 0)
			if (isWorkingDay() && !isBorder()) {
				this.spc = 1;
			} else if (this.isBorder()) {
				this.spc = 3;
			}
		return spc;
	}

	public void setSpc(int spc) {
		this.spc = spc;
	}

}
