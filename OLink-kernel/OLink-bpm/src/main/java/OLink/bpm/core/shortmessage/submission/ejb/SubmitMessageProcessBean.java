package OLink.bpm.core.shortmessage.submission.ejb;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.shortmessage.submission.dao.SubmitMessageDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class SubmitMessageProcessBean extends AbstractDesignTimeProcessBean<SubmitMessageVO>
		implements SubmitMessageProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7565562384213061002L;

	/**
	 * 创建待发短信
	 * 
	 * @param vo
	 *            短信内容
	 */
	public void doCreate(ValueObject vo) throws Exception {

		Date date = new Date();
		SubmitMessageVO senderVO = (SubmitMessageVO) vo;
		senderVO.setSendDate(date);
		super.doCreate(senderVO);
		PermissionPackage.clearCache();
	}

	/**
	 * 根据内容群发短信
	 * 
	 * @param messages
	 *            短信内容的集合
	 */
	public void doCreate(Collection<ValueObject> messages) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			for (Iterator<ValueObject> iterator = messages.iterator(); iterator.hasNext();) {
				SubmitMessageVO obj = (SubmitMessageVO) iterator.next();
				doCreate(obj);
			}
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
		PermissionPackage.clearCache();
	}

	/**
	 * 根据条件查询用户所有在待发件箱中的短信
	 * 
	 * @param userid
	 *            用户的标识
	 * @param params
	 *            查询的条件
	 * @return 短信的集合
	 * @throws Exception
	 */
	public DataPackage<SubmitMessageVO> listWaitSendMessage(WebUser user, ParamsTable params)
			throws Exception {
		params.setParameter("n_submission", "0");
		params.setParameter("n_failure", "0");
		return getDAO().query(params);
	}

	/**
	 * 将指定的短信删除
	 * 
	 * @param pk
	 *            短信标识
	 */
	public void doRemove(String pk) throws Exception {
		super.doRemove(pk);
	}

	protected IDesignTimeDAO<SubmitMessageVO> getDAO() throws Exception {
		return (SubmitMessageDAO) DAOFactory.getDefaultDAO(SubmitMessageVO.class.getName());
	}

	/**
	 * 根据当前用户提交的参数params查询出符合条件的发送成功的记录
	 * @param user 当前用户
	 * @param params @see ParamsTable
	 * @return 返回发送成功的记录信息集合
	 * @throws Exception
	 */
	public DataPackage<SubmitMessageVO> listSubmitMessage(WebUser user, ParamsTable params)
			throws Exception {
		params.setParameter("n_submission", "1");
		return getDAO().query(params);
	}

	/**
	 * 根据当前用户提交的参数params查询出符合条件的发送失败的记录
	 * @param user 当前用户
	 * @param params @see ParamsTable
	 * @return 返回发送失败的记录信息集合
	 * @throws Exception
	 */
	public DataPackage<SubmitMessageVO> listFailureMessage(WebUser user, ParamsTable params)
			throws Exception {
		params.setParameter("n_failure", "1");
		return getDAO().query(params);
	}

	/**
	 * 根据回复者电话号码与回复代码查找发送记录
	 * @param replyCode 回复代码
	 * @param recvtel 接收者电话号码
	 * @return 根据回复者电话号码与回复代码查找发送记录
	 * @throws Exception
	 */
	public SubmitMessageVO getMessageByReplyCode(String replyCode, String recvtel)
			throws Exception {
		return ((SubmitMessageDAO) getDAO()).getMessageByReplyCode(replyCode,recvtel);
	}

	/**
	 * 判断回复代码是否可用
	 * @param code 回复代码
	 * @param recvtel 接收者电话号码
	 * @return 判断回复代码是否可用，true:可用，否则，不可用。
	 * @throws Exception
	 */
	public boolean unAvailableCode(String code, String recvtel) throws Exception{
		return (((SubmitMessageDAO) getDAO()).getMessageByReplyCode(code, recvtel)) != null;
	}

	public DataPackage<SubmitMessageVO> list(WebUser user, ParamsTable params) throws Exception {
		return ((SubmitMessageDAO) getDAO()).list(user, params);
	}

}
