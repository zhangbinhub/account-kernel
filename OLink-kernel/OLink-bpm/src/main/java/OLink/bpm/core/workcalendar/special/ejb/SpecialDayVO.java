package OLink.bpm.core.workcalendar.special.ejb;

import java.util.Date;

import OLink.bpm.core.workcalendar.standard.ejb.BaseDay;

/**
 * 特别日,例外日
 * @author Tom
 *
 */
public class SpecialDayVO extends BaseDay {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private Date startDate;// 执行开始日期

	private Date endDate;// 执行结束日期

	/**
	 * 获取执行结束日期
	 * 
	 * @return 执行结束日期
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * 设置执行结束日期
	 * 
	 * @param endDate
	 *            执行结束日期
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * 获取标识
	 * 
	 * @param 标识
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * 设置标识
	 * 
	 * @param id
	 *            标识
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取开始日期
	 * 
	 * @return 开始日期
	 */
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * 设置开始日期
	 * 
	 * @param startDate
	 *            开始日期
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
