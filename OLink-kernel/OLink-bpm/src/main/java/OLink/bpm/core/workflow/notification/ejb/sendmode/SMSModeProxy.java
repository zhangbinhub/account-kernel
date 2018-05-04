package OLink.bpm.core.workflow.notification.ejb.sendmode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.notification.ejb.SendMode;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.ejb.BaseUser;
import eWAP.core.license.InitLicense;

import com.olink.tools.SMSEmayClient;

import eWAP.core.ResourcePool;
import eWAP.core.dbaccess.ConnectionFactory;

/**
 * @author Happy
 * 
 */
public class SMSModeProxy implements SendMode {

	private SendMode _sendMode;

	public SMSModeProxy() {
		super();

	}

	public SMSModeProxy(WebUser user) {
		super();
		getInstance(user);

	}

	public SMSModeProxy(String sign, WebUser user) {
		super();
		getInstance(sign, user);

	}

	public SMSModeProxy(String sign, String application, String domainid) {
		super();
		getInstance(sign, application, domainid);

	}

	public SMSModeProxy(String from, String sign, String application,
			String domainid) {
		super();
		getInstance(from, sign, application, domainid);

	}

	public SendMode getInstance() {// 增加 by XGY
		if (_sendMode == null) {
			try {
				_sendMode = (SendMode) InitLicense.initSMSSendMode()
						.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _sendMode;
	}

	public SendMode getInstance(WebUser user) {
		if (_sendMode == null) {
			try {// 增加 by XGY
				_sendMode = (SendMode) InitLicense.initSMSSendMode()
						.getConstructor(WebUser.class).newInstance(user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _sendMode;
	}

	public SendMode getInstance(String sign, WebUser user) {
		if (_sendMode == null) {
			try {// 增加 by XGY
				Class<?>[] argTypes = { String.class, WebUser.class };
				_sendMode = (SendMode) InitLicense.initSMSSendMode()
						.getConstructor(argTypes).newInstance(sign, user);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _sendMode;
	}

	public SendMode getInstance(String sign, String application, String domainid) {
		if (_sendMode == null) {
			try {// 增加 by XGY
				Class<?>[] argTypes = { String.class, String.class,
						String.class };
				_sendMode = (SendMode) InitLicense.initSMSSendMode()
						.getConstructor(argTypes)
						.newInstance(sign, application, domainid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _sendMode;
	}

	public SendMode getInstance(String from, String sign, String application,
			String domainid) {
		if (_sendMode == null) {
			try {
				Class<?>[] argTypes = { String.class, String.class,
						String.class, String.class };
				_sendMode = (SendMode) InitLicense.initSMSSendMode()
						.getConstructor(argTypes)
						.newInstance(from, sign, application, domainid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _sendMode;
	}

	public void setApplication(String application) {
		if (_sendMode != null) {
			try {
				Class clazz = _sendMode.getClass();
				Method m = clazz.getDeclaredMethod("setApplication",
						String.class);
				m.invoke(_sendMode, application);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	public void setDomainid(String domainid) {
		if (_sendMode != null) {
			try {
				Class clazz = _sendMode.getClass();
				Method m = clazz.getDeclaredMethod("setDomainid", String.class);
				m.invoke(_sendMode, domainid);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	public void setReceiverUserId(String receiverUserId) {
		if (_sendMode != null) {
			try {
				Class clazz = _sendMode.getClass();
				Method m = clazz.getDeclaredMethod("setReceiverUserId",
						String.class);
				m.invoke(_sendMode, receiverUserId);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

		}
	}

	/***
	 * 该方法用于流程中短信调用
	 */

	/*
	 * public boolean send(String subject, SummaryCfgVO summaryCfg, Document
	 * doc, BaseUser responsible) throws Exception {
	 * //System.out.println(subject); //getInstance().send(subject, summaryCfg,
	 * doc, responsible); //responsible.getTelephone();
	 * 
	 * return getInstance().send(subject, summaryCfg, doc, responsible); }
	 */

	public boolean send(SummaryCfgVO summaryCfg, Document doc,
						BaseUser responsible) throws Exception {
		return getInstance().send(summaryCfg, doc, responsible);
	}

	public boolean send(String subject, String content, BaseUser responsible)
			throws Exception {
		return getInstance().send(subject, content, responsible);
	}

	public boolean send(String docid, String subject, String content,
			BaseUser responsible) throws Exception {
		return getInstance().send(docid, subject, content, responsible);
	}

	public boolean send(String subject, String content, String receiver)
			throws Exception {
		return getInstance().send(subject, content, receiver);
	}

	public boolean send(String docid, String subject, String content,
			String receiver) throws Exception {
		return getInstance().send(docid, subject, content, receiver);
	}

	public boolean send(String subject, String content, String receiver,
			boolean mass) throws Exception {
		return getInstance().send(subject, content, receiver, mass);
	}

	public boolean send(String subject, String content, String receiver,
			String replyPrompt, String code, boolean mass) throws Exception {
		return getInstance().send(subject, content, receiver, replyPrompt,
				code, mass);
	}

	public boolean send(String docid, String subject, String content,
			String receiver, String replyPrompt, String code, boolean mass)
			throws Exception {
		return getInstance().send(docid, subject, content, receiver,
				replyPrompt, code, mass);
	}

	public boolean send(String subject, String content, String receiver,
			Map<String, String> defineReply, boolean mass) throws Exception {
		return getInstance()
				.send(subject, content, receiver, defineReply, mass);
	}

	public boolean send(String docid, String subject, String content,
			String receiver, Map<String, String> defineReply, boolean mass)
			throws Exception {
		return getInstance().send(docid, subject, content, receiver,
				defineReply, mass);
	}

	public boolean send(String docid, String subject, String content,
			String receiver, boolean isNeedReply, boolean mass)
			throws Exception {
		return getInstance().send(docid, subject, content, receiver,
				isNeedReply, mass);
	}

	public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
			BaseUser responsible, boolean approval) throws Exception {
		return getInstance().send(subject, summaryCfg, doc, responsible,
				approval);
	}

	/*
	 * annotation by lr 2013-05-13 for sms platform merge public boolean
	 * send(SubmitMessageVO vo)throws Exception { getInstance(); if(_sendMode
	 * !=null){ try { Class clazz = _sendMode.getClass(); Method m =
	 * clazz.getDeclaredMethod("send", SubmitMessageVO.class); return
	 * Boolean.parseBoolean((m.invoke(_sendMode, vo).toString())); } catch
	 * Auto-generated catch block e.printStackTrace(); } catch
	 * Auto-generated catch block e.printStackTrace(); } catch
	 * e.printStackTrace(); }
	 * 
	 * } return false; }
	 */
	/***
	 * new method by lr 2013-05-13 for new sms platform
	 * 
	 */
	public boolean send(SubmitMessageVO vo) throws Exception {
		getInstance();
		if (_sendMode != null) {
			try {
				ConnectionFactory dbclass = new ResourcePool(null, false)
						.getConnectionFactory(0); // 数据库操作实例
				ArrayList<Hashtable<String, String>> arraySupplier = dbclass
						.doQuery(
								"select * from t_sms_supplier_info where defaultdevice=1 limit 0,1 ",
								0, 0);
				if (arraySupplier.get(0).get("SSNAME").equals("emay")) {// use
																		// emay
																		// sms
																		// platform
					String strAcc = arraySupplier.get(0).get("SSACCOUNT");
					String strPwd = arraySupplier.get(0).get("SSPWD");
					SMSEmayClient emayClient = new SMSEmayClient();
					int iRet = emayClient.SMSSend(strAcc, strPwd, vo);
					if (iRet == 0) {
						System.out.println("emay client send shortmessage ok!");// when
																				// send
																				// success
																				// update
																				// t_shortmessage_submit
																				// table
						dbclass.executeUpdate("update t_shortmessage_submit set submission=1  where id='"
								+ vo.getId() + "'");

						return true;
					} else {
						System.out
								.println("emay client send shortmessage has an error! Error code is "
										+ iRet);
						return false;
					}
				}

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	/***
	 * add by lr 2013-05-14 for sms platform in progress
	 * 
	 * @param subject 主题
	 * @param summaryCfg 标示
	 * @param doc 文档
	 * @param responsible 接收者
	 */
	public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
			BaseUser responsible) throws Exception {
		// 首先执行系统原来方法，在短信提交表中插入一条记录，会留下一条没有注册的信息，应该没有关系。
		getInstance().send(subject, summaryCfg, doc, responsible);
		if (_sendMode != null) {
			try {
				// 获取短信供应商信息
				ConnectionFactory dbclass = new ResourcePool(null, false)
						.getConnectionFactory(0); // 数据库操作实例
				ArrayList<Hashtable<String, String>> arraySupplier = dbclass
						.doQuery(
								"select * from t_sms_supplier_info where defaultdevice=1 limit 0,1 ",
								0, 0);
				// 获取短信内容信息
				// 控制一次只取一条记录
				ArrayList<Object[]> arrayMessage = dbclass
						.doQuery(
								"select * from t_shortmessage_submit where docid=? and receiveruserid=? and submission=0 limit 0,1",
								new String[] { doc.getId(), responsible.getId() },
								0, 0, false);
				if (arraySupplier.get(0).get("SSNAME").equals("emay")) {// use
																		// emay
																		// sms
																		// platform
					String strAcc = arraySupplier.get(0).get("SSACCOUNT");
					String strPwd = arraySupplier.get(0).get("SSPWD");
					if (arrayMessage.size() > 0) {
						SMSEmayClient emayClient = new SMSEmayClient();
						int iRet = emayClient.SMSSend(strAcc, strPwd,
								responsible.getTelephone(),
								arrayMessage.get(0)[2].toString(),
								arrayMessage.get(0)[3].toString());
						if (iRet == 0) {
							System.out
									.println("emay client send shortmessage ok!");// when
																					// send
																					// success
																					// update
																					// t_shortmessage_submit
																					// table
							dbclass.executeUpdate("update t_shortmessage_submit set submission=1  where id='"
									+ arrayMessage.get(0)[0] + "'");

							return true;
						} else {
							System.out
									.println("emay client send shortmessage has an error! Error code is "
											+ iRet);
							return false;
						}

					}
				}

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

		}
		return false;

	}

}
