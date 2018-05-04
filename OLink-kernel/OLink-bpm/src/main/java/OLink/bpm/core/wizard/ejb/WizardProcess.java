package OLink.bpm.core.wizard.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.user.action.WebUser;

/**
 * WizardProcess class.
 * 
 * @author zhuxuehong ,Sam
 * @since JDK1.4
 */
public interface WizardProcess extends IDesignTimeProcess<WizardVO> {

	/**
	 * 当确认时，程序创建所有的表单，视图，菜单，流程
	 * 
	 * @param vo
	 *            向导VO
	 * @param user
	 *            当前在线用户
	 * @param applicationid
	 *            当前应用ID
	 * @param contextBasePath
	 *            当前应用的相对路径
	 * @throws Exception
	 */
	void confirm(ValueObject vo, WebUser user, String applicationid, String contextBasePath)
			throws Exception;
}
