package OLink.bpm.core.xmpp.notification;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.xmpp.XMPPNotification;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.user.action.OnlineUsers;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ObjectUtil;

/**
 * IQ格式：  <notification xmlns="obpm:iq:notification">
 *				<pending xmlns="obpm:iq:notification:pending">
 *					<action>update</action>
 *				</pending>
 *			</notification>
 * @author znicholas
 *
 */
public class PendingNotification extends XMPPNotification {
	public final static String ACTION_CREATE = "create";
	public final static String ACTION_UPDATE = "update";
	public final static String ACTION_REMOVE = "remove";
	
	/**
	 * 待办主题
	 */
	private String subject;
	/**
	 * 待办内容
	 */
	private String summary;
	
	private PendingVO pending;
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	protected String action;

	public PendingVO getPending() {
		return pending;
	}

	public void setPending(PendingVO pending) {
		this.pending = pending;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * 获取整完整内容，表单摘要标题+生成的摘要内容
	 * 
	 * @return
	 */
	public String getContent() {
		return "[" + getSubject() + "]" + getSummary();
	}

	public PendingNotification(PendingVO pending, String action) {
		super();
		this.action = action;
		this.pending = pending;
		this.summary = pending.getSummary();

		try {
			// 系统管理员为发送者
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
			sender = superUserProcess.getDefaultAdmin();
			
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory
					.createProcess(SummaryCfgProcess.class);
			if (!StringUtil.isBlank(pending.getFormid())) {
				SummaryCfgVO summary = summaryCfgProcess.doViewByFormIdAndScope(pending.getFormid(),SummaryCfgVO.SCOPE_PENDING);
				if (summary != null) {
					this.subject = summary.getTitle();
				} else {
					this.subject = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getInnerXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<pending xmlns=\"obpm:iq:notification:pending\">");
		buffer.append("<action>" + getAction() + "</action>");
		buffer.append("</pending>");

		return buffer.toString();
	}
	
	@Override
	public Object clone() {
		PendingNotification clone = new PendingNotification(this.pending, this.action);
		try {
			ObjectUtil.copyProperties(clone, this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return clone;
	}
	
	@Override
	public BaseUser getSender() {
		return super.getSender();
	}
	
	@Override
	public Collection<BaseUser> getReceivers() {
		DataPackage<WebUser> dataPackage = OnlineUsers.doQuery(new ParamsTable());
		Collection<WebUser> users = dataPackage.getDatas();
		for (Iterator<WebUser> iterator = users.iterator(); iterator.hasNext();) {
			WebUser webUser = iterator.next();
			addReceiver(webUser);
		}
		
		return this.receivers;
	}
}
