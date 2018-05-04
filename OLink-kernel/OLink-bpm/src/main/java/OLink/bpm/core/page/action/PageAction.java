package OLink.bpm.core.page.action;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.action.FormAction;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.page.ejb.PageProcess;
import com.opensymphony.xwork.Action;

public class PageAction extends FormAction<Page> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5699074196676830462L;

	public PageAction() throws ClassNotFoundException {
		super(new Page());
	}

	public String get_applicationid() {
		Page content = (Page) getContent();

		return content.getApplicationid();
	}

	public String doListByApp(String application) throws Exception {
		DataPackage<Page> datas = ((PageProcess) process).doListExcludeMod(getParams(), application);
		setDatas(datas);
		return Action.SUCCESS;
	}

	public String doList(String application) throws Exception {
		String module = (String) getParams().getParameter("s_module");
		if (module != null && module.trim().length() > 0) {
			return super.doList();
		} else {
			return doListByApp(application);
		}
	}

	public void set_applicationid(String _applicationid) throws Exception {
		Page content = (Page) getContent();

		content.setApplicationid(_applicationid);
	}

	public String get_default() {
		Page content = (Page) getContent();
		if (content.isDefHomePage()) {
			return "true";
		} else {
			return "false";
		}
	}

	public void set_default(String _default) {
		Page content = (Page) getContent();
		if (_default != null) {
			if (_default.trim().equalsIgnoreCase("true")) {
				content.setDefHomePage(true);
				return;
			}
		}
		content.setDefHomePage(false);
	}

}
