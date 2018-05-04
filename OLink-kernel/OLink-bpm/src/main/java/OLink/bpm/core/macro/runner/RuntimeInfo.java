package OLink.bpm.core.macro.runner;

public class RuntimeInfo {
	private String label;

	private String js;

	private long recentlyStartTime;
	
	private long recentlyCostTime;

	private long totalCostTime;

	private long runningCount;

	private boolean running;
	
	private Thread currThread;

	/**
	 * @hibernate.property
	 * column="currThread"
	 */
	public Thread getCurrThread() {
		return currThread;
	}

	public void setCurrThread(Thread currThread) {
		this.currThread = currThread;
	}

	/**
	 * @hibernate.property column="runningCount"
	 */
	public long getRunningCount() {
		return runningCount;
	}

	public void setRunningCount(long runningCount) {
		this.runningCount = runningCount;
	}

	/**
	 * @param label
	 * @param js
	 * @param recentlyCostTime
	 * @param averageCostTime
	 * @param totalCostTime
	 */
	public RuntimeInfo(String label, String js, long recentlyCostTime) {
		super();
		this.label = label;
		this.js = js;
		this.recentlyCostTime = recentlyCostTime;
		this.totalCostTime = recentlyCostTime;
	}

	public RuntimeInfo(String label, String js, boolean running) {
		super();
		this.label = label;
		this.js = js;
		this.running = running;
	}

	public long getAverageCostTime() {
		return totalCostTime / this.runningCount;
	}

	public String getJs() {
		return js;
	}

	public void setJs(String js) {
		this.js = js;
	}

	/**
	 * @hibernate.property column="label"
	 */
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @hibernate.property column="recentlyCostTime"
	 */
	public long getRecentlyCostTime() {
		return recentlyCostTime;
	}

	public void setRecentlyCostTime(long recentlyCostTime) {
		this.recentlyCostTime = recentlyCostTime;
	}

	/**
	 * @hibernate.property column="totalCostTime"
	 */
	public long getTotalCostTime() {
		return totalCostTime;
	}

	public void setTotalCostTime(long totalCostTime) {
		this.totalCostTime = totalCostTime;
	}

	/**
	 * @hibernate.property column="running"
	 */
	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @hibernate.property
	 * column="recentlyStartTime"
	 */
	public long getRecentlyStartTime() {
		return recentlyStartTime;
	}

	public void setRecentlyStartTime(long recentlyStartTime) {
		this.recentlyStartTime = recentlyStartTime;
	}
}
