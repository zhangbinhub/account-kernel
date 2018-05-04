package OLink.bpm.core.email.email.dao;

import java.util.Date;
import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.util.ProcessFactory;
import org.hibernate.Query;
import org.hibernate.Session;

import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;

public class HibernateEmailDAO extends HibernateBaseDAO<Email> implements
		EmailDAO {

	public HibernateEmailDAO(String voClassName) {
		super(voClassName);
	}
	
	public DataPackage<Email> getDatapackage(String hql, ParamsTable params, int page,
											 int lines) throws Exception {
		DataPackage<Email> result = new DataPackage<Email>();
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

	public void updateRead(String messageId) throws Exception {
		try {
			Email message = (Email) find(messageId);
			if (message != null) {
				message.setRead(true);
				super.update(message);
			}
			currentSession().beginTransaction().commit();
		} catch (Exception e) {
			currentSession().beginTransaction().rollback();
			throw e;
		}
	}

	public void moveToOtherFolder(Email message, String otherFolderId)
			throws Exception {
		try {
			EmailFolderProcess folderProcess = (EmailFolderProcess) ProcessFactory.createProcess(EmailFolderProcess.class);
			EmailFolder mailFolder = (EmailFolder) folderProcess.doView(otherFolderId);
			if (mailFolder == null) {
				throw new Exception("Invalid folder id!");
			}
			message.setEmailFolder(mailFolder);
			super.update(message);
			currentSession().beginTransaction().commit();
		} catch (Exception e) {
			currentSession().beginTransaction().rollback();
			throw e;
		}
	}

	public void updateMarkRead(String[] ids) throws Exception {
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				updateRead(ids[i]);
			}
		}
	}

	public DataPackage<Email> queryMessageByFolderId(String folderid,
			ParamsTable params) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.emailFolder.id = '").append(folderid).append("' ");
		String unread = params.getParameterAsString("unread");
		if (!StringUtil.isBlank(unread) && unread.equals("true")) {
			hql.append("and vo.read = ").append(false).append(" ");
		}
		String userid = params.getParameterAsString("emailUserid");
		if (!StringUtil.isBlank(userid)) {
			hql.append("and vo.emailUser.id = '").append(userid).append("' ");
		}
		hql.append("order by vo.emailBody.sendDate desc");
		
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");

		int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		// params.setParameter("_currpage", page);
		// params.setParameter("_pagelines", lines);
		return getDatapackage(hql.toString(), params, page, lines);
	}
	
	@Override
	public void create(ValueObject vo) throws Exception {
		try {
			if (vo instanceof Email) {
				Session session = currentSession();
				Email email = (Email) vo;
				email.setEmailId(System.currentTimeMillis());
				if (StringUtil.isBlank(email.getEmailBody().getId())) {
					email.getEmailBody().setId(Tools.getSequence());
					if (email.getEmailBody().getSendDate() == null) {
						email.getEmailBody().setSendDate(new Date());
					}
					session.save(email.getEmailBody());
				}
				session.save(vo);
			}
			currentSession().beginTransaction().commit();
		} catch (Exception e) {
			currentSession().beginTransaction().rollback();
			throw e;
		}
	}

	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public int queryUnreadMessageCount(String folderid, EmailUser user) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.emailFolder.id = '").append(folderid).append("' ");
		hql.append("and vo.emailUser.id = '").append(user.getId()).append("' ");
		hql.append("and vo.read = ").append(false);
		Query query = currentSession().createQuery(hql.toString());
		List list = query.list();
		if (list != null && !list.isEmpty()) {
			Long count = (Long) list.get(0);
			return count.intValue();
		}
		return 0;
	}

	/**
	 * @SuppressWarnings API不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public int queryEmailCount(String folderid, EmailUser user)
			throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append("select count(*) from ").append(_voClazzName).append(" vo ");
		hql.append("where vo.emailFolder.id = '").append(folderid).append("' ");
		hql.append("and vo.emailUser.id = '").append(user.getId()).append("'");
		Query query = currentSession().createQuery(hql.toString());
		List list = query.list();
		if (list != null && !list.isEmpty()) {
			Long count = (Long) list.get(0);
			return count.intValue();
		}
		return 0;
	}
	
}
