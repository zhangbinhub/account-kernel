/*
 * Created on 2005-4-19
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.macro.util;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Options;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.file.FileOperate;

/**
 * @author ZhouTY
 * 
 */
public class WebJsUtil {
	@SuppressWarnings("unused")
	private Document _doc;

	private WebUser _user;

	private ParamsTable _params;

	private Collection<ValidateMessage> _errors;

	public WebJsUtil(Document doc, ParamsTable params, WebUser user, Collection<ValidateMessage> errors) {
		_doc = doc;
		_user = user;
		_params = params;
		_errors = errors;
	}

	public WebUser getWebUser() {
		return _user;
	}

	public ParamsTable getParamsTable() {
		return _params;
	}

	public Collection<ValidateMessage> getErrors() {
		return _errors;
	}

	public String getErrorMessage() {
		StringBuffer errorMessage = new StringBuffer();
		if (_errors != null && _errors.size() > 0) {
			errorMessage.append("错误信息：");
			Iterator<ValidateMessage> iter = _errors.iterator();
			while (iter.hasNext()) {
				ValidateMessage error = iter.next();
				errorMessage.append(error.getErrmessage()).append("\n");
			}
		}
		return errorMessage.toString();
	}

	public static ParamsTable createParamsTable() {
		return new ParamsTable();
	}

	public String getParameterAsText(String tag) {
		String value = _params.getParameterAsText(tag);
		return (value == null) ? "" : value;
	}

	public String[] getParameterAsArray(String tag) {
		String[] value = _params.getParameterAsArray(tag);
		return value;
	}

	public double getParameterAsDouble(String tag) {
		Double value = _params.getParameterAsDouble(tag);
		if (value != null) {
			return value.doubleValue();
		} else {
			return 0;
		}
	}

	public String getParameterAsString(String tag) {
		String value = _params.getParameterAsString(tag);
		return (value == null) ? "" : value;
	}

	public int getParameterAsInteger(String tag) {
		String value = (String) _params.getParameter(tag);

		int rtn = 0;

		if (value != null) {
			try {
				rtn = Integer.parseInt(value);

			} catch (Exception e) {
			}
		}

		return rtn;
	}

	public long getParameterAsLong(String tag) {
		String value = (String) _params.getParameter(tag);
		long rtn = 0;
		if (value != null) {
			try {
				rtn = Long.parseLong(value);
			} catch (Exception e) {
			}
		}

		return rtn;
	}

	public int get_currpage() {
		String currage = getParameterAsString("_currpage");
		return (currage == null || currage.trim().length() <= 0) ? 1 : Integer.parseInt(currage);
	}

	public String getApplication() {
		String appid = getParamsTable().getParameterAsString("application");
		if(StringUtil.isBlank(appid)){
			appid = getParamsTable().getParameterAsString("applicationid");
		}
		return appid;
	}

	public String getDomainid() {
		String domainid = _user !=null? _user.getDomainid() : "";
		if (domainid == null || domainid.trim().length() <= 0) {
			domainid = this.getParamsTable().getParameterAsString("domain");
		}
		return domainid;
	}

	public Options listFiles(String path) {
		String realPath = _params.getParameterAsString("realPath");
		Options opts = new Options();
		opts.add("", "");
		File file = new File(realPath != null ? realPath + path : path);
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					opts.add(files[i].getName(), files[i].getName());
				}
			}
		}
		return opts;
	}

	public void cutFileToFolder(String fileName, String folderName) {
		String realPath = _params.getParameterAsString("realPath");
		try {
			FileOperate.cutFileToFolder(realPath != null ? realPath + fileName : fileName, realPath != null ? realPath
					+ folderName : folderName);
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
	}
}
