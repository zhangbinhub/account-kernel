package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.util.Debug;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.user.action.WebUser;

/**
 * 引入一个视图组件, 组件以iframe的形式输出
 * 
 * @author nicholas
 */
public class IncludedPage extends IncludedElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1517041188945769061L;

	public UserDefined page;

	/**
	 * 构造方法
	 * 
	 * @param field
	 */
	public IncludedPage(IncludeField field) {
		super(field);
	}

	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		try {
			UserDefinedProcess pp = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
			page = (UserDefined) pp.doView(getValueObjectId(runner));
			if (page != null) {
				return page.toHtml(doc, new ParamsTable(), webUser);
			}
		} catch (Exception e) {
			Debug.println(e.getMessage());
			e.printStackTrace();
		}

		return "";
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return "";
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public IDesignTimeProcess getProcess() throws Exception {
		return ProcessFactory.createProcess(UserDefinedProcess.class);
	}

	public String toXMLTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {

		try {
			UserDefinedProcess pp = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
			UserDefined homepage = (UserDefined) pp.doView(getValueObjectId(runner));
			Page page = homepage.initPage(homepage);
			if (page != null) {
				return page.toXMLCalctext(doc, runner, webUser, false);
			}
		} catch (Exception e) {
			Debug.println(e.getMessage());
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 获取组件名
	 * 
	 * @return 组件名
	 */
	public String getName() {
		if (page != null) {
			return page.getName();
		}
		return "";
	}
}
