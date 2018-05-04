package OLink.bpm.core.personalmessage.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageVO;
import org.hibernate.Query;
import org.hibernate.Session;

public class HibernatePersonalMessageDAO extends HibernateBaseDAO<PersonalMessageVO> implements
		PersonalMessageDAO {

	public HibernatePersonalMessageDAO(String voClassName) {
		super(voClassName);
	}

	public DataPackage<PersonalMessageVO> queryTrash(String userid, ParamsTable params)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.ownerId= '").append(userid).append("' and vo.trash = true");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), params, page, lines);
	}

	public int countNewMessages(String userid) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.ownerId ='").append(userid);
		hql.append("' and vo.trash = false and vo.read = false and vo.inbox = true");

		Session session = currentSession();
		Query query = session.createQuery(hql.toString());
		List<?> list = query.list();
		if (list != null && !list.isEmpty()) {
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}
	
	public String[] getReceiverUserIdsByMessageBodyId(String bodyId)
			throws Exception {
		String hql = "select vo.receiverId from " + _voClazzName + " vo where BODYID = '" + bodyId + "'";
		Collection<PersonalMessageVO> collection = getDatas(hql);
		if (collection != null && !collection.isEmpty()) {
			String[] result = new String[collection.size()];
			int count = 0;
			for (Iterator<PersonalMessageVO> it = collection.iterator(); it.hasNext(); ) {
				Object vo = it.next();
				result[count ++] = vo.toString();
			}
			return result;
		}
		return new String[0];
}

	public DataPackage<PersonalMessageVO> queryNewMessage(String userid, ParamsTable params)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.receiverId= '").append(userid).append("' and vo.trash = false and vo.read = false order by vo.sendDate desc");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), params, page, lines);
	}

	public DataPackage<PersonalMessageVO> queryInBox(String userid, ParamsTable params)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.receiverId = '").append(userid).append("'").append(" and vo.inbox = true and vo.trash = false");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), params, page, lines);
	}

	public DataPackage<PersonalMessageVO> queryOutbox(String userid, ParamsTable params)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.ownerId = '").append(userid).append("' and vo.outbox = true and vo.trash = false");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		return getDatapackage(hql.toString(), params, page, lines);
	}
	
	public DataPackage<PersonalMessageVO> getDatapackage(String hql, ParamsTable params, int page,
			int lines) throws Exception {
		DataPackage<PersonalMessageVO> result = new DataPackage<PersonalMessageVO>();
		result.rowCount = getTotalLines(hql);
		result.pageNo = page;
		result.linesPerPage = lines;

		if (result.pageNo > result.getPageCount()) {
			result.pageNo = 1;
			page = 1;
		}

		result.datas = getDatas(hql, params, page, lines);
		return result;
	}
	
}