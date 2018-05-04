package OLink.bpm.core.dynaform.signature.action;

import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.signature.ejb.Htmlsignature;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.signature.ejb.HtmlsignatureProcess;
import OLink.bpm.core.user.action.WebUser;

/**
 * 
 * @author Alex
 * 
 */
public class SignatureAction extends BaseAction<Htmlsignature> {
	
	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public SignatureAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(HtmlsignatureProcess.class),
				new Htmlsignature());
	}

	private static final long serialVersionUID = 1L;
	/**
	 * 电子签章控件的命令
	 */
	public String mCommand;

	/**
	 * 根据电子签章的命令，进行相应的操作。
	 * 
	 * @throws Exception
	 */
	public void doCommand() throws Exception {
		mCommand = this.getParams().getParameterAsString("COMMAND");
		/**
		 * 保存签章数据信息
		 */
		if ("SAVESIGNATURE".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).saveSignature(this
					.getParams());
		}
		/**
		 * 获取服务器时间
		 */
		if ("GETNOWTIME".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).getNowTime();
		}
		/**
		 * 删除签章数据信息
		 */
		if ("DELESIGNATURE".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).deleSignature(this
					.getParams());
		}
		/**
		 * 调入签章数据信息
		 */
		if ("LOADSIGNATURE".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).loadSignature(this
					.getParams());
		}
		/**
		 * 获取当前签章SignatureID，调出SignatureID，再自动调LOADSIGNATURE数据
		 */
		if ("SHOWSIGNATURE".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).showSignature(this
					.getParams());
		}
		/**
		 * 批量签章时，获取所要保护的数据
		 */
		if ("GETSIGNATUREDATA".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process)
					.getSignatureData(this.getParams());
		}
		/**
		 * 批量签章时，写入签章数据
		 */
		if ("PUTSIGNATUREDATA".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process)
					.putSignatureData(this.getParams());
		}
		/**
		 * 获取印章
		 */
		if ("SIGNATUREKEY".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).signatureKey(this
					.getParams());
		}
		/**
		 * 保存签章历史信息
		 */
		if ("SAVEHISTORY".equalsIgnoreCase(mCommand)) {

			((HtmlsignatureProcess<Htmlsignature>) process).saveHistory(this
					.getParams());
		}

	}

	public WebUser getUser() throws Exception {
		Map<?, ?> session = getContext().getSession();
		WebUser user = null;
		if (session == null
				|| session.get(Web.SESSION_ATTRIBUTE_FRONT_USER) == null) {
			UserVO vo = new UserVO();
			vo.getId();
			vo.setName("GUEST");
			vo.setLoginno("guest");
			vo.setLoginpwd("");
			vo.setRoles(null);
			vo.setEmail("");
			user = new WebUser(vo);
		} else {
			user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_FRONT_USER);
		}
		return user;
	}

	/**
	 * 单个签章时，返回需要保护的字段。 通过ajax,给function
	 * DoSignature()中的document_content.SignatureControl.FieldsList赋值。
	 * 
	 * @throws Exception
	 */
	public void getDocument() throws Exception {
		WebUser user = this.getUser();
		((HtmlsignatureProcess<Htmlsignature>) process).getDocument(this
				.getParams(), user);
	}

	/**
	 * 批量签章时，返回需要保护的字段。 通过ajax,给function
	 * DoBatchSignature()中的document_content.SignatureControl.FieldsList赋值。
	 * 
	 * @throws Exception
	 */
	public void getBatchDocument() throws Exception {
		((HtmlsignatureProcess<Htmlsignature>) process).getBatchDocument(this
				.getParams());
	}

}
