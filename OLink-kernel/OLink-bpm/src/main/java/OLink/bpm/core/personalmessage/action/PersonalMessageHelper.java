package OLink.bpm.core.personalmessage.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class PersonalMessageHelper {

	public String createUsers(String domainid, String[] def) throws Exception {

		UserProcess up = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);
		Collection<UserVO> users = up.queryByDomain(domainid, 1, 10);

		return createDomainUserList(users, def);
	}

	public String createDomainUserList(Collection<UserVO> cols, String[] def) {
		StringBuffer fun = new StringBuffer();
		fun.append("<table width='100%'>");
		fun.append("<tr>");
		fun.append("<td>&nbsp;</td>");
		fun.append("<td>{*[UserName]*}</td><td>{*[Account]*}</td>");
		fun.append("</tr>");
		for (Iterator<UserVO> iter = cols.iterator(); iter.hasNext();) {
			fun.append("<tr>");

			UserVO user = iter.next();
			String checked = "";
			if (def != null) {
				for (int k = 0; k < def.length; k++) {
					if (def[k] != null && def[k].equals(user.getId())) {
						checked = " checked ";
						break;
					}
				}
			}
			fun.append("<td><input name='colids' type='checkbox' value='")
					.append(user.getId()).append("'").append(checked).append(
							" /></td>");
			fun.append("<td class='commFont'>").append(user.getName()).append(
					"</td>");
			fun.append("<td class='commFont'>").append(user.getLoginno())
					.append("</td>");

			fun.append("</tr>");
		}
		fun.append("</table>");
		return fun.toString();
	}

	public String findUserName(String ids) throws Exception {
		if (ids != null) {
			UserProcess up = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			String[] id = ids.split(",");
			StringBuffer names = new StringBuffer();
			for (int i = 0; i < id.length; i++) {
				UserVO user = (UserVO) up.doView(id[i]);
				if (user != null)
					names.append(user.getName()).append(";");
			}
			return names.toString();
		}
		return "";
	}
	
	public String findUserNamesByBodyId(String bodyId) throws Exception {
		try {
			PersonalMessageProcess process = (PersonalMessageProcess) ProcessFactory.createProcess(PersonalMessageProcess.class);
			String[] ids = process.getReceiverUserIdsByMessageBodyId(bodyId);
			if (ids != null && ids.length > 0) {
				UserProcess up = (UserProcess) ProcessFactory
						.createProcess(UserProcess.class);
				StringBuffer names = new StringBuffer();
				for (int i = 0; i < ids.length; i++) {
					UserVO user = (UserVO) up.doView(ids[i]);
					if (user != null) {
						names.append(user.getName()).append(";");
					}
				}
				String namesString = names.toString();
				return namesString.endsWith(";") ? namesString.substring(0, namesString.length()-1) : namesString;
			}
		} catch (Exception e) {
			
		}
		return "";
	}
	
	public String findUserNamesByMsgIds(String ids) throws Exception {
		try {
			
			if(ids != null){
				String[] idStrings = ids.split(",");
				if (idStrings.length > 0) {
					StringBuffer names = new StringBuffer();
					for (int i = 0; i < idStrings.length; i++) {
						names.append(findUserName(idStrings[i]));
					}
					String namesString = names.toString().trim();
					return namesString.endsWith(";") ? namesString.substring(0, namesString.length()-1) : namesString;
				}
			}

		} catch (Exception e) {
			
		}
		return "";
	}
	
	public String findUserNameById(String id) throws Exception {
		if (!StringUtil.isBlank(id)) {
			UserProcess up = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO user = (UserVO) up.doView(id);
			if (user != null) {
				return user.getName();
			}
		}
		return "";
	}

	public int countMessage(String userId) throws Exception {
		PersonalMessageProcess pmp = (PersonalMessageProcess) ProcessFactory
				.createProcess(PersonalMessageProcess.class);
		return pmp.countNewMessages(userId);
	}
	
	public String regexHtml(String text) {
		if (!StringUtil.isBlank(text)) {
			text = htmlDecodeEncoder(text);
			Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(text);
			text = matcher.replaceAll(" ");
			text = text.replaceAll("&#160;", " ");
		}
		return text;
	}
	
	public String htmlDecodeEncoder(String content) {
		if (StringUtil.isBlank(content)) {
			return content;
		}
		content = content.replaceAll("&quot;", "\"");
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");
		return content;
	}

}
