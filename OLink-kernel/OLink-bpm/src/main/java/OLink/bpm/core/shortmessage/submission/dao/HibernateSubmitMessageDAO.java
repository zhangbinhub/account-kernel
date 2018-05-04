package OLink.bpm.core.shortmessage.submission.dao;

import java.util.Calendar;
import java.util.List;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import org.hibernate.Query;
import org.hibernate.Session;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.core.user.action.WebUser;

public class HibernateSubmitMessageDAO extends HibernateBaseDAO<SubmitMessageVO> implements SubmitMessageDAO {

	public HibernateSubmitMessageDAO(String voClassName) {
		super(voClassName);
	}

	public void create(Object po) throws Exception {
		Session session = currentSession();
		session.save(po);
	}

	public SubmitMessageVO getMessageByReplyCode(String replyCode, String recvtel) throws Exception {
//		String hql = "FROM " + _voClazzName + " vo where replyCode = '" + replyCode + "' and ((receiver like '%"
//				+ recvtel + "%') or (mass=1)) and submission=false and sendDate >=?";
		String hql = "FROM " + _voClazzName + " vo where replyCode = '" + replyCode + "' and (receiver like '" + recvtel + "') and sendDate >=?";
		Session session = currentSession();
		Query query = session.createQuery(hql);
		Calendar cld = Calendar.getInstance();
		cld.add(Calendar.DAY_OF_MONTH, -10);
		cld.set(Calendar.HOUR_OF_DAY, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.SECOND, 0);
		query.setDate(0, cld.getTime());
		query.setFirstResult(0);
		query.setFetchSize(1);
		List<?> list = query.list();
		return (SubmitMessageVO) ((list.size() > 0) ? list.get(0) : null);
	}

	public DataPackage<SubmitMessageVO> list(WebUser user, ParamsTable params) throws Exception {
		String tel = user.getTelephone();
		tel = (tel != null && tel.trim().length() > 0 ? tel : null);
		String hql = "from " + _voClazzName + " vo where ((sender like '%" + tel + "%' and submission=1) or sender='" + tel + "') order by vo.sendDate desc";
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : Integer.MAX_VALUE;
		return this.getDatapackage(hql, params, page, lines);
	}
	
	public DataPackage<SubmitMessageVO> getDatapackage(String hql, ParamsTable params, int page,
			int lines) throws Exception {
		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();

		String whereClause = sqlUtil.createWhere(_voClazzName, params);
		if (whereClause != null && whereClause.trim().length() > 0) {
			int p = hql.toLowerCase().indexOf(" where ");

			hql = (p >= 0) ? hql = hql.substring(0, p) + " where " + whereClause + " and " + hql.substring(p + 7) : hql
					+ " where " + whereClause;
		}
		
		DataPackage<SubmitMessageVO> result = new DataPackage<SubmitMessageVO>();
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

	public static void main(String[] args) throws Exception {
		HibernateSubmitMessageDAO dao = new HibernateSubmitMessageDAO(SubmitMessageVO.class.getName());
		dao.getMessageByReplyCode("AA0004", "13533679742");
	}
	
}