package OLink.bpm.util.timer;

import java.util.Date;
import java.util.Timer;

public class Schedule {

	private static Timer timer = new Timer();

	/**
	 * register a new job.
	 * 
	 * @param job
	 *            The job
	 * @param firstTime
	 * @param period
	 */
	public static void registerJob(Job job, Date firstTime, long period) {
		timer.schedule(job, firstTime, period);
	}

	public static void registerJob(Job job, Date firstTime) {
		timer.schedule(job, firstTime);
	}

	public static void registerJob(Job job, long delay, long period) {
		timer.schedule(job, delay, period);
	}

	/**
	 * Cancel all the jobs.
	 */
	public static void cancelAllJob() {
		timer.cancel();
		timer = null;
	}
}
