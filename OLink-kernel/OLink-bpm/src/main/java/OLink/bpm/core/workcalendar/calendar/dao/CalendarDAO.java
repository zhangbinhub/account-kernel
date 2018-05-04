package OLink.bpm.core.workcalendar.calendar.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;

public interface CalendarDAO extends IDesignTimeDAO<CalendarVO> {
	
	CalendarVO doViewByName(String name, String domainid) throws Exception ;

	Collection<CalendarVO> doQueryList(String domainid) throws Exception;
	
	DataPackage<CalendarVO> doQueryListBySearch(ParamsTable params, int page, int lines) throws Exception;
	
	int queryCountByName(String name, String domainid) throws Exception;
	
	void saveCalendar(String id, String name, String remark) throws Exception;
	
}
