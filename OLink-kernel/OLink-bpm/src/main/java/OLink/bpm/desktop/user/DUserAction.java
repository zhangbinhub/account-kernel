package OLink.bpm.desktop.user;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.workcalendar.calendar.action.CalendarHelper;
import OLink.bpm.desktop.personal.DPersonalAction;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;

/**
 * 无需登录即可以使用的UserAction
 * @author znicholas
 *
 */
public class DUserAction extends DPersonalAction {
	private static final Logger LOG = Logger.getLogger(DUserAction.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9167960649402640178L;

	public String doViewByEmail() {
		ParamsTable params = getParams();
		String email = params.getParameterAsString("t_email");
	
		try {
			if (StringUtil.isBlank(email) || StringUtil.isBlank(email)){
				throw new Exception("Please input email");
			}
			
			UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			Collection<UserVO> users = process.doSimpleQuery(params);
			if (users != null && !users.isEmpty()){
				UserVO uservo = users.iterator().next();
				ServletActionContext.getRequest().setAttribute("toXml", getUserXML(uservo));
				return SUCCESS;
			} else {
				throw new Exception("User not found");
			}
		} catch (Exception e) {
			addFieldError("SystemError", e.toString());
			LOG.warn(e);
			return ERROR;
		}
	}
	
	/**
	 * 通过账号和企业名称获取企业用户XML
	 * @return
	 */
	public String doViewByLoginnoAndDomainName() {
		ParamsTable params = getParams();
		String loginno = params.getParameterAsString("loginno");
		String domain = params.getParameterAsString("domain");
		
		try {
			if (StringUtil.isBlank(loginno) || StringUtil.isBlank(domain)){
				throw new Exception("please input loginno and domain");
			}
			
			UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			UserVO uservo = process.getUserByLoginnoAndDoaminName(loginno, domain);
			if (!uservo.getUseIM()){
					throw new Exception("Non IM users");
			}
			
			ServletActionContext.getRequest().setAttribute("toXml", getUserXML(uservo));
		} catch (Exception e) {
			addFieldError("SystemError", e.toString());
			LOG.warn(e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	/**
	 * 通过账号和企业名称获取企业用户XML
	 * @return
	 */
	public String doViewAdminByLoginno() {
		ParamsTable params = getParams();
		String loginno = params.getParameterAsString("loginno");
		
		try {
			if (StringUtil.isBlank(loginno)){
				throw new Exception("please input loginno");
			}
			
			SuperUserProcess process = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
			SuperUserVO uservo = process.doViewByLoginno(loginno);
			
			ServletActionContext.getRequest().setAttribute("toXml", getUserXML(uservo));
		} catch (Exception e) {
			addFieldError("SystemError", e.toString());
			LOG.warn(e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	/**
	 * 获取所有企业用户
	 */
	public String doList(){
		ParamsTable params = getParams();
		params.setParameter("t_useIM", 1);
		
		try {
			UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			
			Collection<UserVO> userList = process.doSimpleQuery(params);
			StringBuffer sb = new StringBuffer();
			sb.append("<"+MobileConstant.TAG_USERPANEL_LIST+">");
			for (Iterator<UserVO> iterator = userList.iterator(); iterator.hasNext();) {
				UserVO userVO = iterator.next();
				sb.append(getUserXML(userVO));
			}
			sb.append("</"+MobileConstant.TAG_USERPANEL_LIST+">");
			
			ServletActionContext.getRequest().setAttribute("toXml", sb.toString());
		} catch (Exception e) {
			addFieldError("SystemError", e.toString());
			LOG.warn(e);
			return ERROR;
		}
		
		return SUCCESS;
	}
	
	private String getUserXML(BaseUser uservo) throws Exception{
		StringBuffer sb = new StringBuffer();
		
		sb.append("<" + MobileConstant.TAG_USERPANEL + " " + MobileConstant.ATT_OPTION + "='user' " + MobileConstant.ATT_ID  +"='" + uservo.getId() +"'>");
		sb.append("<" + MobileConstant.TAG_TEXTFIELD + " " + MobileConstant.ATT_NAME + "='uservo.name' " + MobileConstant.ATT_VALUE + "='" + uservo.getName() + "'>").append("</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TEXTFIELD + " " + MobileConstant.ATT_NAME + "='uservo.loginno' " + MobileConstant.ATT_VALUE + "='" + uservo.getLoginno() + "'>").append("</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TEXTFIELD + " " + MobileConstant.ATT_NAME + "='_password' " + MobileConstant.ATT_VALUE + "='" + uservo.getLoginpwd() + "'>").append("</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TEXTFIELD + " " + MobileConstant.ATT_NAME + "='uservo.email' " + MobileConstant.ATT_VALUE + "='" + uservo.getEmail() + "'>").append("</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TEXTFIELD + " " + MobileConstant.ATT_NAME + "='uservo.telephone' " + MobileConstant.ATT_VALUE + "='" + uservo.getTelephone() + "'>").append("</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TEXTFIELD + " " + MobileConstant.ATT_NAME + "='uservo.domainName' " + MobileConstant.ATT_VALUE + "='" + getDomain(uservo).getName() + "'>").append("</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_SELECTFIELD + " " + MobileConstant.ATT_NAME + "='uservo.calendarType' " + MobileConstant.ATT_VALUE + "='" + uservo.getCalendarType() + "'>");
		
		CalendarHelper ch = new CalendarHelper();
		ch.setDomain(uservo.getDomainid());
		Map<String, String> map = ch.getWorkCalendars();
		for (Iterator<Entry<String, String>> it = map.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append("<" + MobileConstant.TAG_OPTION + " " + MobileConstant.ATT_VALUE + "='" + key + "'>").append(value).append("</" + MobileConstant.TAG_OPTION + ">");
		}
		sb.append("</" + MobileConstant.TAG_SELECTFIELD + ">");
		
		sb.append("<" + MobileConstant.TAG_SELECTFIELD + " " + MobileConstant.ATT_NAME + "='_proxyUser' " + MobileConstant.ATT_VALUE + "='" + get_proxyUser() + "'>");
		Collection<UserVO> list = getAllUsers();
		for (Iterator<UserVO> it = list.iterator(); it.hasNext(); ) {
			UserVO vo = it.next();
			sb.append("<" + MobileConstant.TAG_OPTION + " " + MobileConstant.ATT_VALUE + "='" + vo.getId() + "'>").append(vo.getLoginno()).append("</" + MobileConstant.TAG_OPTION + ">");
		}
		sb.append("</" + MobileConstant.TAG_SELECTFIELD + ">");
		
		sb.append("</" + MobileConstant.TAG_USERPANEL + ">");
		
		return sb.toString();
	}
	
	private DomainVO getDomain(BaseUser uservo) throws Exception{
		String domainid = uservo.getDomainid();
		DomainProcess domainProcess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		if (!StringUtil.isBlank(domainid)){
			DomainVO domain = (DomainVO) domainProcess.doView(domainid);
			return domain;
		}
		return new DomainVO();
	}
}
