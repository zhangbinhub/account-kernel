package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * ValueStoreField 需要保存数据到数据库的域
 * 
 * @author nicholas
 * 
 */
public interface ValueStoreField {
	/**
	 * 获取Field的类型
	 * 
	 * @return Field的类型
	 */
	String getFieldtype();

	/**
	 * 根据Form模版的组件内容结合Document中的ITEM存放的值,输出重定义后的html文本以网格显示
	 * 
	 * @param runner
	 *            动态脚本执行器
	 * @param doc
	 *            文档对象
	 * @param webUser
	 *            webUser
	 * @return 重定义后的html文本
	 */
	String toGridHtmlText(Document doc, IRunner runner, WebUser webUser) throws Exception;
}
