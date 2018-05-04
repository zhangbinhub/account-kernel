package OLink.bpm.core.workflow.notification.ejb;

import java.util.Map;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.user.ejb.BaseUser;

public interface SendMode {
	/**
	 * 发送信息
	 * 
	 * @param subject
	 *            主题
	 * @param reminder
	 *            待办对象
	 * @param doc
	 *            文档标识
	 * @param responsible
	 *            通知的接收人
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
				 BaseUser responsible) throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param reminder
	 *            待办对象
	 * @param doc
	 *            文档标识
	 * @param responsible
	 *            通知的接收人
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(SummaryCfgVO summaryCfg, Document doc, BaseUser responsible)
			throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param responsible
	 *            通知的接收人
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String subject, String content, BaseUser responsible)
			throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param docid
	 *            文档标识
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param responsible
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String docid, String subject, String content,
				 BaseUser responsible) throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param receiver
	 *            接收者
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String subject, String content, String receiver)
			throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param docid
	 *            文档标识
	 * @param subject
	 *            主题
	 * @param content
	 *            发送内容
	 * @param receiver
	 *            接收者
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String docid, String subject, String content,
				 String receiver) throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param subject
	 *            主题
	 * @param content
	 *            发送内容
	 * @param receiver
	 *            接收者
	 * @param mass
	 *            是否群发
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String subject, String content, String receiver,
				 boolean mass) throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param subject
	 *            主题
	 * @param content
	 *            发送内容
	 * @param receiver
	 *            接收者
	 * @param replyPrompt
	 *            回复提示
	 * @param code
	 *            回复提示代码
	 * @param mass
	 *            是否群发
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String subject, String content, String receiver,
				 String replyPrompt, String code, boolean mass) throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param docid
	 *            文档对象
	 * @param subject
	 *            主题
	 * @param content
	 *            以送内容
	 * @param receiver
	 *            接收者
	 * @param replyPrompt
	 *            回复提示
	 * @param code
	 *            回复提示代码
	 * @param mass
	 *            是否群发
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String docid, String subject, String content,
				 String receiver, String replyPrompt, String code, boolean mass)
			throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param subject
	 *            主题
	 * @param content
	 *            以送内容
	 * @param receiver
	 *            接收者
	 * @param defineReply
	 *            定义答复
	 * @param mass
	 *            是否群发
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String subject, String content, String receiver,
				 Map<String, String> defineReply, boolean mass) throws Exception;

	/**
	 * 发送信息
	 * 
	 * @param docid
	 *            文档对象
	 * @param subject
	 *            主题
	 * @param content
	 *            以送的内容
	 * @param receiver
	 *            接收者
	 * @param defineReply
	 *            定义答复
	 * @param mass
	 *            是否群发
	 * @return 是否发送成功
	 * @throws Exception
	 */
	boolean send(String docid, String subject, String content,
				 String receiver, Map<String, String> defineReply, boolean mass)
			throws Exception;

	boolean send(String docid, String subject, String content,
				 String receiver, boolean isNeedReply, boolean mass)
			throws Exception;
	
	boolean send(String subject, SummaryCfgVO summaryCfg, Document doc, BaseUser responsible, boolean approval) throws Exception;
}
