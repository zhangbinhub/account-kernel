package OLink.bpm.core.macro.runner;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.util.HTMLJsUtil;
import OLink.bpm.core.user.action.WebUser;

public interface IRunner {
	/**
	 * 运行宏脚本命令
	 * 
	 * @param label
	 *            标签
	 * @param js
	 *            宏脚本
	 * @return 运行的结果
	 * @throws Exception
	 */
	Object run(String label, String js) throws Exception;

	/**
	 * 读取默认的baseLib.js文件
	 * 
	 * @return 读取到的内容
	 * @throws Exception
	 */
	String readBaseLib() throws Exception;

	void initBSFManager(Document currdoc, ParamsTable params, WebUser user, Collection<ValidateMessage> errors)
			throws Exception;

	/**
	 * 声明对象,注册对象
	 * 
	 * @param registName
	 *            注册对象的别名
	 * @param bean
	 *            注册类
	 * @param clazz
	 *            注册类的Class
	 */
	void declareBean(String registName, Object bean, Class<?> clazz) throws Exception;

	/**
	 * 释放已声明对象
	 * 
	 * @param registName
	 *            注册对象的别名
	 */
	void undeclareBean(String registName) throws Exception;

	/**
	 * 设置应用的标识
	 * 
	 * @param applicationId
	 *            应用的标识
	 */
	void setApplicationId(String applicationId);

	/**
	 * 获取htmlJsUtil的解析类
	 * 
	 * @return 解析类
	 */
	HTMLJsUtil get_htmlJsUtil();

	/**
	 * 设置htmlJsUtil的解析类
	 * 
	 * @return htmlJsUtil的解析类
	 */
	void set_htmlJsUtil(HTMLJsUtil jsUtil);

	/**
	 * 清除脚本的内容
	 */
	void clear();
}
