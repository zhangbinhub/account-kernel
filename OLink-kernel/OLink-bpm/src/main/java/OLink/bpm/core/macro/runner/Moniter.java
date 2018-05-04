package OLink.bpm.core.macro.runner;

import java.util.HashMap;
import java.util.Map;

public class Moniter {
	private static ThreadLocal<RuntimeInfo> _runningInfoThreadLocal = new ThreadLocal<RuntimeInfo>();

	private static HashMap<String, RuntimeInfo> _runningInfos = new HashMap<String, RuntimeInfo>();

	private static HashMap<String, RuntimeInfo> _runnedInfos = new HashMap<String, RuntimeInfo>();

	public static void registRunningInfo(String label, String js) {

		RuntimeInfo rt = new RuntimeInfo(label, js, true);

		rt.setRecentlyStartTime(System.currentTimeMillis());

		rt.setCurrThread(Thread.currentThread());
		
		_runningInfoThreadLocal.set(rt);

		_runningInfos.put(rt.getLabel(), rt);

	}

	public static void unRegistRunningInfo(String label) {
		RuntimeInfo rt = _runningInfoThreadLocal.get();
		if (rt != null) {
			long start = rt.getRecentlyStartTime();

			RuntimeInfo old = _runnedInfos.get(label);
			if (old!=null) {
				old.setRecentlyCostTime(System.currentTimeMillis() - start);
				old.setRunningCount(old.getRunningCount() + 1);
				old.setTotalCostTime(old.getTotalCostTime()
						+ old.getRecentlyCostTime());
				_runnedInfos.put(label, old);

			}
			else {
				rt.setRecentlyCostTime(System.currentTimeMillis() - start);
				rt.setRunningCount(rt.getRunningCount() + 1);
				rt.setTotalCostTime(rt.getTotalCostTime()
						+ rt.getRecentlyCostTime());
				_runnedInfos.put(label, rt);

			}

			rt.setCurrThread(null);

			_runningInfoThreadLocal.set(null);

			_runningInfos.remove(label);
		}
	}

	public static Map<String, RuntimeInfo> getRunningInfos() {
		return _runningInfos;
	}

	public static RuntimeInfo getRunningInfo(String label) {
		return _runningInfos.get(label);
	}

	public static Map<String, RuntimeInfo> getRunnedInfos() {
		return _runnedInfos;
	}

}
