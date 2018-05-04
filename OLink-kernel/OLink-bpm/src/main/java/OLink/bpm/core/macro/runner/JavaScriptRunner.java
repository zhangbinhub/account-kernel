package OLink.bpm.core.macro.runner;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;
import OLink.bpm.core.macro.util.CurrDocJsUtil;
import OLink.bpm.core.macro.util.PrintJsUtil;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import OLink.bpm.util.OBPMSessionContext;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class JavaScriptRunner extends AbstractRunner {
	private Logger logger = Logger.getLogger(this.getClass());

	private static HashSet<String> libs = new HashSet<String>();

	private static HashMap<String, Script> scripts = new HashMap<String, Script>();

	ContextFactory contextFactory;

	private Context context;

	private Scriptable scope;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public static void clearScripts() {

		libs.clear();
		scripts.clear();

	}

	protected JavaScriptRunner() {
		Monitor monitor = MonitorFactory.start("New JavaScriptRunner");
		contextFactory = new ContextFactory();
		context = contextFactory.enter();
		scope = new ImporterTopLevel(context);

		// Load BaseLib
		try {
			String baselib = readBaseLib();
			if (baselib != null && baselib.length() > 0) {
				Script script = context.compileString(baselib, "baselib", 1,
						null);

				script.exec(context, scope);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		monitor.stop();
	}

	/**
	 * 进行脚本运行，传入label和js进行运行
	 * 
	 * @param label
	 * @param js
	 */
	public Object run(String label, String js) throws Exception {
		try {

			Object result = null;

			if (js != null) {
				if (js.indexOf("#include") != -1) {
					String Content = js;

					Monitor monitor = MonitorFactory.start("include lib");
					while (Content.indexOf("#include") != -1) {
						String libname = null;
						int k = 0, t = 0, e = 0;

						k = Content.indexOf("#include");
						t = Content.indexOf("\"", k + 8);
						e = Content.indexOf("\"", t + 1);
						libname = Content.substring(t + 1, e);

						String threadId = Thread.currentThread().getName() + "";
						if (!libs.contains(libname + "@" + this.applicationId
								+ "@" + threadId)) {
							RepositoryProcess rp = (RepositoryProcess) ProcessFactory
									.createProcess(RepositoryProcess.class);
							RepositoryVO rpvo = rp.getRepositoryByName(libname,
									this.applicationId);
							if (rpvo != null && rpvo.getContent() != null
									&& rpvo.getContent().trim().length() > 0) {

								Script script = context.compileString(
										rpvo.getContent(), rpvo.getName(), 1,
										null);
								script.exec(context, scope);
								libs.add(libname + "@" + this.applicationId
										+ "@" + threadId);
							}
						}
						String st = Content.substring(0, k);
						String ed = Content.substring(e + 1, Content.length());
						Content = st + ed;
					}
					monitor.stop();
					js = Content;
				}
				Monitor monitor = MonitorFactory
						.start("Compile JavaScriptRunner Label: " + label);

				Script script = scripts.get(label);
				if (script == null) {
					// logger.info("准备编译脚本......");
					script = context.compileString(js, label, 1, null);
					scripts.put(label, script);
				}

				monitor.stop();
				// HttpSession oldSession =
				// OBPMSessionContext.getInstance().getSession(sessionid);
				// logger.info("准备执行脚本......");
				Document setDoc = null;
				try {
					NativeJavaObject tmpDoc = (NativeJavaObject) scope.get(
							"$CURRDOC", scope);
					Object unwraptmpDoc = tmpDoc.unwrap();

					if (((CurrDocJsUtil) unwraptmpDoc)
							.getCurrDoc()
							.getClass()
							.getName()
							.equals(Document.class.getName())) {
						// logger.info("当前文档对象已经创建");
					} else {
						HttpSession oldSession = OBPMSessionContext
								.getInstance().getSession(sessionid);
						setDoc = (Document) oldSession
								.getAttribute("_currentDocObj");
						declareBean("$CURRDOC", new CurrDocJsUtil(setDoc),
								CurrDocJsUtil.class);
						declareBean("$PRINTER",
								new PrintJsUtil(this.sessionid),
								PrintJsUtil.class);
						// logger.info("重新注册了doc对象.........." + setDoc);
					}

				} catch (Exception ex) {
					// logger.error("还有异常：" + ex.getMessage());
					// logger.info("从session中取出预先设置好的文档对象，session id is:"+sessionid);
					HttpSession oldSession = OBPMSessionContext.getInstance()
							.getSession(sessionid);
					// logger.info("获取session成功");

					setDoc = (Document) oldSession
							.getAttribute("_currentDocObj");
					// logger.info("获取对象成功："+setDoc);
					declareBean("$CURRDOC", new CurrDocJsUtil(setDoc),
							CurrDocJsUtil.class);
					declareBean("$PRINTER", new PrintJsUtil(this.sessionid),
							PrintJsUtil.class);
					logger.info("重新注册了doc对象.........." + setDoc);
				}

				// logger.info("当前的doc对象："+setDoc);

				monitor = MonitorFactory.start("Exec JavaScriptRunner Label: "
						+ label);

				result = script.exec(context, scope);
				// logger.info("执行成功.......");
				monitor.stop();

				if (result instanceof NativeJavaObject) {
					NativeJavaObject nobj = (NativeJavaObject) result;
					result = nobj.unwrap();
				}

			}
			if (result instanceof Undefined) {
				return null;
			}
			return result;

		} catch (Exception e) {
			Environment env = Environment.getInstance();
			String path = env.getRealPath("/logs/iscript/");
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			BufferedOutputStream outStream = new BufferedOutputStream(
					new FileOutputStream(path + "/" + sessionid + ".log", true));
			PrintWriter printWriter = new PrintWriter(outStream);
			e.printStackTrace(printWriter);

			printWriter.close();

			throw new Exception("JavaScriptRunning:[" + label + "]: "
					+ e.getMessage(), e);
		} finally {
			// Moniter.unRegistRunningInfo(label);
		}
	}

	public String getHtmlText() {
		if (_htmlJsUtil != null) {
			String text = _htmlJsUtil.toString();
			if (text != null) {
				return text;
			}
		}
		return "";
	}

	public void declareBean(String registName, Object bean, Class<?> clazz)
			throws Exception {
		if ((bean instanceof Number) || (bean instanceof String)
				|| (bean instanceof Boolean)) {
			scope.put(registName, scope, bean);
		} else {
			// Must wrap non-scriptable objects before presenting to Rhino
			// logger.info("正在注册名称：" + registName + ",注册类是：" + clazz.getName());
			Scriptable wrapped = Context.toObject(bean, scope);
			scope.put(registName, scope, wrapped);

		}

		// Object jsBean = Context.javaToJS(bean, scope);
		// scope.put(registName, scope, jsBean);
	}

	public void clear() {
		if (context != null) {
			Context.exit();
			libs.clear();
			scripts.clear();
			// logger.info("所有申明的iscript对象被清空.........");
		}
	}

	public void undeclareBean(String registName) throws Exception {
		scope.delete(registName);
	}

}
