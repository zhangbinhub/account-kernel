package OLink.bpm.core.macro.runner;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.jamonapi.proxy.MonProxyFactory;

public class JavaScriptFactory {
	static Logger logger = Logger.getLogger(JavaScriptFactory.class);

	private static final ThreadLocal<IRunner> _threadRunner = new ThreadLocal<IRunner>();

	private static final HashMap<String, Object> _debugRunners = new HashMap<String, Object>();

	private static final HashSet<String> _debugSessionIds = new HashSet<String>();

	public static void clear() {
		// IRunner runner = (IRunner) _threadRunner.get();
		// if (runner != null) {
		// runner.clear();
		// }
		JavaScriptRunner.clearScripts();
	}

	public static boolean isDebug(String sessionid) {
		if (sessionid != null) {
			return _debugSessionIds.contains(sessionid);
		} else {
			return false;
		}
	}

	public static IRunner getDebugerInstance(String sessionid) {
		Object d = _debugRunners.get(sessionid);
		if (d != null) {
			return (IRunner) d;
		} else {
			return null;
		}
	}

	public static IRunner getInstance(String sessionid, String applicationid) {
		if (isDebug(sessionid)) {
			if (_threadRunner.get() != null) {
				IRunner runner = _threadRunner.get();
				// logger.info("调试模式下的JavaScriptRunner实例......");
				runner.clear();
				_threadRunner.set(null);
			}

			return JavaScriptDebuger.getInstance(applicationid);
		} else {
			if (_threadRunner.get() == null) {
				_threadRunner.set((IRunner) MonProxyFactory
						.monitor(new JavaScriptRunner()));
				// logger.info("创建了一个新的JavaScriptRunner实例......");
			}

			IRunner runner = _threadRunner.get();
			((AbstractRunner) runner).setSessionId(sessionid);
			// logger.info("设置session id："+sessionid);
			// 由于线程被重用问题，每次获取后需要重新设置软件ID
			runner.setApplicationId(applicationid);
			if (runner.get_htmlJsUtil() != null) {
				runner.get_htmlJsUtil().clear();
			}
			return runner;
		}

	}

	public static void set_debug(String sessionid, boolean debug) {
		if (debug) {
			_debugSessionIds.add(sessionid);
		} else {
			_debugSessionIds.remove(sessionid);
		}
	}

}
