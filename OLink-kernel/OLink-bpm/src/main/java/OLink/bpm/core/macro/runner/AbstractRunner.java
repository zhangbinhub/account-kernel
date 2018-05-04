package OLink.bpm.core.macro.runner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.util.CurrDocJsUtil;
import OLink.bpm.core.macro.util.FactoryJsUtil;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.ftp.FTPUpload;
import OLink.bpm.util.mail.EmailUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.macro.util.HTMLJsUtil;
import OLink.bpm.core.macro.util.PrintJsUtil;
import OLink.bpm.core.macro.util.Tools;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.message.MessageUtil;
import OLink.bpm.core.macro.util.WebJsUtil;

public abstract class AbstractRunner implements IRunner {
	private static final String _BASELIB_FILENAME = "baselib.js";

	private static String _BASE_LIB_JS = null;

	protected String sessionid;

	protected HTMLJsUtil _htmlJsUtil;

	protected String applicationId;
	
	protected Document _currdoc=null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunner#run(java.lang.String,
	 * java.lang.String)
	 */
	public abstract Object run(String label, String js) throws Exception;

	/*
	 * (non-Javadoc)
	 * 
	 * @see IRunner#readBaseLib()
	 */
	/**
	 * 读取baselib.js文件中的function
	 * 
	 * @return 读取文件后的内容
	 */
	public String readBaseLib() throws Exception {
		if (_BASE_LIB_JS == null) { // /

			StringBuffer js = new StringBuffer();
			try {
				URL url = AbstractRunner.class.getResource(_BASELIB_FILENAME);
				if (url != null) {
					InputStream is = url.openStream();
					InputStreamReader reader = new InputStreamReader(is, "UTF-8");

					BufferedReader bfReader = new BufferedReader(reader);

					String tmp = null;

					do {
						tmp = bfReader.readLine();
						if (tmp != null) {
							js.append(tmp).append('\n');
						}
					} while (tmp != null);

					bfReader.close();
					reader.close();
					is.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			_BASE_LIB_JS = js.toString();
		} // /
		return _BASE_LIB_JS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * IRunner#initBSFManager(OLink.bpm.core.dynaform
	 * .document.ejb.Document, ParamsTable,
	 * WebUser, java.util.Collection)
	 */
	/**
	 * 注册一些公共工具类,使在调用时可以直接使用如($EMAIL,调用类方法时需要直接$EMAIL.方法名)
	 * 
	 * @param currdoc
	 *            文档对象
	 * @param params
	 *            参数
	 * @param user
	 *            web用户
	 * @param errors
	 *            错误信息
	 */
	public void initBSFManager(Document currdoc, ParamsTable params, WebUser user, Collection<ValidateMessage> errors)
			throws Exception {
		this._currdoc=currdoc;

		if (params != null && params.getSessionid() != null) {
			this.sessionid = params.getSessionid();
		}

		if (params != null && !StringUtil.isBlank(this.getApplicationId())) {
			params.setParameter("application", this.getApplicationId());
		}

		// 设置页面参数
		if (currdoc != null) {
			currdoc.set_params(params);
		}

		// 定以自动发送服务
		if (this.applicationId != null && this.applicationId.trim().length() > 0) {
			declareBean("$EMAIL", new EmailUtil(this.applicationId), EmailUtil.class);
		} else {
			declareBean("$EMAIL", new EmailUtil(), EmailUtil.class);
		}

		// 定以自动发送服务
		if (this.applicationId != null && this.applicationId.trim().length() > 0) {
			declareBean("$MESSAGE", new MessageUtil(this.applicationId), MessageUtil.class);

		} else {
			declareBean("$MESSAGE", new MessageUtil(), MessageUtil.class);
		}

		declareBean("$FTP", new FTPUpload(), FTPUpload.class);
		// 注册当前文档
		declareBean("$CURRDOC", new CurrDocJsUtil(currdoc), CurrDocJsUtil.class);
		// 申明打印工具类
		declareBean("$PRINTER", new PrintJsUtil(this.sessionid), PrintJsUtil.class);
		// 申明WEB工具类
		declareBean("$WEB", new WebJsUtil(currdoc, params, user, errors), WebJsUtil.class);
		// 申明工具类
		declareBean("$TOOLS", new Tools(), Tools.class);
		// JSFactory类，可调用任一proxy
		declareBean("$BEANFACTORY", new FactoryJsUtil(), FactoryJsUtil.class);
		// PROCESSFactory类，可调用任一proxy
		declareBean("$PROCESSFACTORY", ProcessFactory.getInstance(), ProcessFactory.class);

		/*
		 * // 函数库 BSF_MANAGER.declareBean("$REPOSITORY", new RepositoryVO(),
		 * RepositoryVO.class);
		 */
		// HTML类
		if (_htmlJsUtil == null)
			_htmlJsUtil = new HTMLJsUtil();
		declareBean("$HTML", _htmlJsUtil, HTMLJsUtil.class);

	}

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
	public abstract void declareBean(String registName, Object bean, Class<?> clazz) throws Exception;

	/**
	 * 释放已声明对象
	 * 
	 * @param registName
	 *            注册对象的别名
	 */
	public abstract void undeclareBean(String registName) throws Exception;

	/**
	 * 获取应用标识
	 * 
	 * @return 应用标识
	 */
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * 设置应用标识
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * 获取htmlJsUtil的解析类
	 * 
	 * @return 解析类
	 */
	public HTMLJsUtil get_htmlJsUtil() {
		return _htmlJsUtil;
	}

	/**
	 * 设置htmlJsUtil的解析类
	 * 
	 * @param htmlJsUtil的解析类
	 */
	public void set_htmlJsUtil(HTMLJsUtil jsUtil) {
		_htmlJsUtil = jsUtil;
	}
	
	public void setSessionId(String sessionID){
		this.sessionid=sessionID;
	}
}
