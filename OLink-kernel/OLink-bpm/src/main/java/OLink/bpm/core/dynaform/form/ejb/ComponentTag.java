package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.component.ejb.Component;
import OLink.bpm.core.dynaform.component.ejb.ComponentProcess;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * @author nicholas
 */
public class ComponentTag extends FormField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 553483994695841995L;

	/**
	 * @uml.property name="componentid"
	 */
	private String componentid;

	private static Logger log = Logger.getLogger(ComponentTag.class);

	/**
	 * @uml.property name="_elements"
	 */
	private Collection<? extends FormElement> _elements;

	public ComponentTag() {

	}

	/**
	 * 获取组件标识
	 * 
	 * @return component id
	 * @uml.property name="componentid"
	 */
	public String getComponentid() {
		return componentid;
	}

	/**
	 * 设置组件标识
	 * 
	 * @param componentid
	 * @uml.property name="componentid"
	 */
	public void setComponentid(String componentid) {
		this.componentid = componentid;
	}

	public ValidateMessage validate(IRunner bsf, Document doc) throws Exception {
		return null;
	}

	/**
	 * 返回模板描述Component
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<span'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(" refreshOnChanged='" + isRefreshOnChanged() + "'");
		template.append(" validateRule='" + getValidateRule() + "'");
		template.append(" valueScript='" + getValueScript() + "'");
		template.append("/>");
		return template.toString();
	}

	public String toHtmlTxt(ParamsTable params, WebUser webUser, IRunner runner) {
		return toHtmlTxt(null, runner, webUser);
	}

	/**
	 * 
	 * Form模版的component内容结合Document,返回重定义后的html
	 * 
	 * @see ParamsTable#params
	 * @param doc
	 *            文档对象
	 * @return 符串内容为重定义后的html的Component组件标签及语法
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) {
		String html ="";
		try {
			if(doc != null){
				ComponentProcess cp = (ComponentProcess) ProcessFactory.createProcess(ComponentProcess.class);
				Component component = (Component) cp.doView(componentid);
	
				set_elements(component.getFields());
				this.setCalculateOnRefresh(true);
	
				html = component.toHtml(doc, doc.get_params(), webUser, new ArrayList<ValidateMessage>());
			}
		} catch (Exception e) {
			log.error("Couldn't get the Component");
		}
		
		return html;
	}

	/**
	 * 
	 * Form模版的component内容结合Document,返回重定义后的打印html文本
	 * 
	 * @param doc
	 *            Document
	 * @param runner
	 *            AbstractRunner(执行脚本的接口类)
	 * @param params
	 *            参数
	 * @param user
	 *            webuser
	 * 
	 * @see AbstractRunner#run(String, String)
	 * @return Form模版的component组件内容结合Document为重定义后的打印html
	 * @throws Exception
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return toHtmlTxt(doc, runner, webUser);
	}

	/**
	 * 获取Component组件所有元素
	 * 
	 * @return Component所有元素
	 * @uml.property name="_elements"
	 */
	public Collection<? extends FormElement> get_elements() {
		return _elements;
	}

	/**
	 * 设置Component组件所有元素
	 * 
	 * @param _elements
	 * @uml.property name="_elements"
	 */
	public void set_elements(Collection<? extends FormElement> _elements) {
		this._elements = _elements;
	}

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}
}
