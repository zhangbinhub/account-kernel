package OLink.bpm.core.dynaform.smsfilldocument;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import net.sf.json.JSONObject;

public class DataMessageParser {

	private String[] parse;

	private String[] values;

	private WebUser user;

	public DataMessageParser(String[] parse) throws Exception {
		this.parse = parse;
		parse();
	}

	private void parse() throws Exception {
		if (parse == null || parse.length < 4)
			throw new Exception("Error");
		UserProcess uprocess = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);
		// 第二和第三个字符串是用户名和密码
		UserVO vo = uprocess.login(parse[1].trim(), parse[2].trim(), parse[0].trim());
		if (vo != null && vo.getStatus() == 1) {
			user = new WebUser(vo);
//			ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory
//					.createProcess(ApplicationProcess.class);
//			String application = appProcess.getDefaultApplication(user.getDefaultApplication(), user.getDomainid()).getId();
//			user.setApplicationid(application);
		} else {
			user = new WebUser(vo);
		}
		// 从第5个开始为表单字段内容
		values = new String[parse.length - 4];
		for (int i = 4; i < parse.length; i++) {
			values[i - 4] = parse[i].trim();
		}

	}

	public WebUser getUser() {
		return user;
	}

	public String[] getFieldValuses() {
		return values;
	}

	public ParamsTable getParamsByRelationText(String relationFieldNames)
			throws Exception {
		ParamsTable params = new ParamsTable();
		if (!StringUtil.isBlank(relationFieldNames)) {
			JSONObject jsonObject = JSONObject.fromObject(relationFieldNames);
			if (jsonObject.size() > values.length)
				throw new Exception("缺少参数");
			for (int i = 1; i < jsonObject.size() + 1; i++) {
				String fieldName = (String) jsonObject.get("" + i);
				params.setParameter(fieldName, values[i - 1].trim());
			}
		}
		return params;
	}

}
