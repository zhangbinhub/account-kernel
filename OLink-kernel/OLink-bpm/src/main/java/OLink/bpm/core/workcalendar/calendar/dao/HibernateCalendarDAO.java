package OLink.bpm.core.workcalendar.calendar.dao;

import java.sql.PreparedStatement;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.workcalendar.calendar.action.CalendarType;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;

public class HibernateCalendarDAO extends HibernateBaseDAO<CalendarVO> implements
		CalendarDAO {
	
	private static final Logger log = Logger.getLogger(HibernateCalendarDAO.class);

	public HibernateCalendarDAO(String valueObjectName) {
		super(valueObjectName);
	}

	public HibernateCalendarDAO() {

	}

	public CalendarVO doViewByName(String name, String domainid)
			throws Exception {
		String hql = "FROM " + this._voClazzName + " vo WHERE vo.name ='"
				+ name + "' AND vo.domainid='" + domainid + "'";
		return (CalendarVO) getData(hql);
	}

	public Collection<CalendarVO> doQueryList(String domainid) throws Exception {
		String hql = "FROM " + this._voClazzName;
		hql += "  vo WHERE vo.domainid='" + domainid + "' ORDER BY ID";
		return getDatas(hql);
	}
	
	public DataPackage<CalendarVO> doQueryListBySearch(ParamsTable params, int page, int lines) throws Exception {
		String hql = "FROM " + this._voClazzName;
		hql += "  vo WHERE vo.domainid='" + params.getParameter("domain") + "'";
		if(params.getParameter("sm_name")!=null){
			String name=(String) params.getParameter("sm_name");
			if(!StringUtil.isBlank(name)) {
				hql += " AND vo.name like '%" + CalendarType.getKeyByName(name) + "%'";
			}
		}
		hql += " ORDER BY ID";
		
		DataPackage<CalendarVO> result = new DataPackage<CalendarVO>();
		result.rowCount = getTotalLines(hql);
		result.pageNo = page;
		result.linesPerPage = lines;

		if (result.pageNo > result.getPageCount()) {
			result.pageNo = 1;
			page = 1;
		}

		result.datas = getDatas(hql, page, lines);
		return result;
	}

	public int queryCountByName(String name, String domainid) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo WHERE vo.name ='"
				+ name + "' AND vo.domainid='" + domainid + "'";
		return getTotalLines(hql);
	}

	public void saveCalendar(String id, String name, String remark)
			throws Exception {
		PreparedStatement statement = null;
		try {
			String sql = "UPDATE T_CALENDAR SET CALENDARNAME=?, REMARK=? WHERE ID=?";
			
			if (log.isDebugEnabled()) {
				log.info("SQL: " + sql);
			}
			
			statement = currentSession().connection().prepareCall(sql);
			statement.setString(1, name);
			statement.setString(2, remark);
			statement.setString(3, id);
			statement.executeUpdate();
		} catch (Exception e) {
			log.warn("HibernateCalendarDAO.saveCalendar(): " + e.getMessage());
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}
}
