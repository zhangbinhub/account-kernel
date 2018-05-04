/*
 * Created on 2005-1-22
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * @author ZhouTY
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public interface FormElement {
	String toTemplate();

	/**
	 * Form模版内容结合Document中的ITEM存放的值,返回重定义后的html。
	 * 
	 * @param doc
	 *            Document对象
	 * @param runner
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html
	 * @throws Exception
	 */
	String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception;

	/**
	 * Form模版内容结合Document中的ITEM存放的值,返回重定义后的xml。
	 * 
	 * @param doc
	 *            Document对象
	 * @param runner
	 * 
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的xml
	 * @throws Exception
	 */
	String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception;
	
	String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception;
	
	String toPdfHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception;
}
