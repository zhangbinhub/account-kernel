package OLink.bpm.core.shortmessage.runtime;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.sequence.Sequence;

public class DeFineReplyContentUtil {

	public static String getDeFineReplyString(Map<String, String> params) {
		StringBuffer rtn = new StringBuffer();
		if (params != null) {
			Set<Entry<String, String>> entrys = params.entrySet();
			for (Iterator<Entry<String, String>> its = entrys.iterator(); its.hasNext();) {
				Entry<String, String> entry =  its.next();
				rtn.append(getDeFineReplyString(entry.getKey(), entry.getValue())).append(" ");
			}
		}
		return rtn.toString();
	}

	public static String getDeFineReplyString(String name, String value) {
		if (StringUtil.isBlank(name)) {
			return "";
		}
		return name + ",请回复：" + value;
	}

	/**
	 * 根据值信息加上自动序列字串组成新的字串
	 * 
	 * @param replyValue
	 * @param telephone
	 * @param application
	 * @return
	 * @throws Exception
	 */
	public static String getReplyCode(String replyValue, String telephone, String application) throws Exception {
		String temp = Sequence.getShortMessageCode(telephone, application);
		SubmitMessageProcess process = (SubmitMessageProcess) ProcessFactory.createProcess(SubmitMessageProcess.class);
		while (process.unAvailableCode(temp, telephone)) {
			temp = Sequence.getShortMessageCode(telephone, application);
		}
		if (StringUtil.isBlank(replyValue)) {
			return temp;
		} else {
			return replyValue.trim() + temp;
		}
	}
}
