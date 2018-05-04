package OLink.bpm.util.sequence;

import java.io.File;
import java.util.UUID;

import OLink.bpm.constans.Framework;
import OLink.bpm.core.counter.ejb.CounterProcess;
import OLink.bpm.core.counter.ejb.CounterProcessBean;
import OLink.bpm.core.counter.ejb.CounterVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.safehaus.uuid.UUIDGenerator;

/**
 * The system sequence utility.
 */

public class Sequence {
	private final static int MAX_DEPTS_PER_LEVEL = 100;

	private final static int MAX_GROUP_PER_LEVEL = 100;

	private final static int MAX_PERSONS_PER_LEVEL = 1000;

	private static final int base = 100000;

	private static long millis = 0;

	private static long counter = 0;

	private static long old = 0;

	public final static int MAX_TOPIC_NUMBER = 1000000;

	public final static int MAX_TOPICCHILD_NUMBER = 1000;

	public final static int MAX_TITLE_COUNT = 10000;

	public final static int MAX_SHORTMESSAGE_NUMBER = 9999;

	private static char[] HandlerChars = { 'A', 'B', 'C' };

	private static char baseChar = 'A';

	private static final char maxChar = 'Z';

	private static Object obj = new Object();

	public static synchronized String getShortMessageCode(String telephone, String applicationid) throws Exception {
		String num_hi = "0000";
		CounterProcessBean process = new CounterProcessBean(applicationid);
		int value = process.getLastValue(telephone, applicationid, null);
		int base2;
		if (value == MAX_SHORTMESSAGE_NUMBER) {
			base2 = process.getShortSquence(telephone + HandlerChars[1], applicationid, null, maxChar + 1, baseChar);
		}
		base2 = process.getLastValue(telephone + HandlerChars[1], applicationid, null);
		if (base2 == 0) {
			base2 = process.getShortSquence(telephone + HandlerChars[1], applicationid, null, maxChar + 1, baseChar);
		}
		int base1;
		if (base2 > maxChar) {
			CounterVO couter = process.findByName(telephone + HandlerChars[1], applicationid, null);
			couter.setCounter(baseChar);
			process.doUpdate(couter);
			base2 = baseChar;
			base1 = process.getShortSquence(telephone + HandlerChars[0], applicationid, null, maxChar, baseChar);
		}
		base1 = process.getLastValue(telephone + HandlerChars[0], applicationid, null);
		if (base1 == 0) {
			base1 = process.getShortSquence(telephone + HandlerChars[0], applicationid, null, maxChar, baseChar);
		}
		String rtn = "" + (char) base1 + (char) base2;
		value = process.getShortSquence(telephone, applicationid, null, MAX_SHORTMESSAGE_NUMBER);
		if (value < 10) {
			rtn += num_hi.substring(0, num_hi.length() - 1);
		} else if (value < 100) {
			rtn += num_hi.substring(0, num_hi.length() - 2);
		} else if (value < 1000) {
			rtn += num_hi.substring(0, num_hi.length() - 3);
		}
		rtn += value;
		return rtn;
	}

	/**
	 * Get the base Sequence.
	 * 
	 * @return the Sequence
	 * @throws SequenceException
	 */
	public static synchronized String getSequence() throws SequenceException {
		UUIDGenerator gen = UUIDGenerator.getInstance();
		org.safehaus.uuid.UUID uuid = gen.generateTimeBasedUUID();

		String uuidStr = uuid.toString();
		String[] uuidParts = uuidStr.split("-");
		StringBuffer builder = new StringBuffer();
		builder.append(uuidParts[2]);
		builder.append("-");
		builder.append(uuidParts[1]);
		builder.append("-");
		builder.append(uuidParts[0]);
		builder.append("-");
		builder.append(uuidParts[3]);
		builder.append("-");
		builder.append(uuidParts[4]);

		return builder.toString();
	}

	public static synchronized String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public static synchronized String getTimeSequence() throws SequenceException {
		long result = System.currentTimeMillis();
		if (result == millis) {
			old++;
			if (old >= base)
				throw new SequenceException("It had exceed the maxium sequence in this moment.");
			result = millis * base + old;
		} else {
			millis = result;
			result *= base;
			old = 0;
		}
		return result + "";
	}

	/**
	 * Get the next department number
	 * 
	 * @param superiorid
	 *            The superior department number.
	 * @return The next department number
	 * @throws SequenceException
	 */
	public static synchronized String getDeptId(String superiorid, String domainid) throws SequenceException {
		if (superiorid == null || superiorid.trim().length() == 0) {
			return null;
		}

		int counter;
		try {
			CounterProcess delegate = (CounterProcess) ProcessFactory.createProcess(CounterProcess.class);
			counter = delegate.getNextValue(superiorid, null, domainid);
		} catch (Exception ex) {
			throw new SequenceException("Cann't get next department number : " + superiorid + ",detail error message:"
					+ ex.getMessage());
		}

		if (counter >= MAX_DEPTS_PER_LEVEL) {
			throw new SequenceException("It had exceed the maxium department number: " + superiorid);
		}

		return superiorid + "-" + StringUtil.leftPad(String.valueOf(counter), 2, "0");
	}

	/**
	 * Get the next group number
	 * 
	 * @param superiorid
	 *            The superior group number
	 * @return The next group number
	 * @throws SequenceException
	 */
	public static synchronized String getGroupId(String superiorid, String domainid) throws SequenceException {
		if (superiorid == null || superiorid.trim().length() == 0) {
			return null;
		}

		superiorid = StringUtil.replaceOnce(superiorid, Framework.ROOT_DEPARTMENT, Framework.INTERNAL_USERGROUP_PREFIX);
		int counter;
		try {
			CounterProcess delegate = (CounterProcess) ProcessFactory.createProcess(CounterProcess.class);
			counter = delegate.getNextValue(superiorid, null, domainid);
		} catch (Exception ex) {
			throw new SequenceException("Cann't get next group number : " + superiorid + ",detail error message:"
					+ ex.getMessage());
		}

		if (counter >= MAX_GROUP_PER_LEVEL) {
			throw new SequenceException("It had exceed the maxium group number: " + superiorid);
		}

		return superiorid + "-" + StringUtil.leftPad(String.valueOf(counter), 2, "0");
	}

	/**
	 * Get the next user number
	 * 
	 * @param superiorid
	 *            The superior user number
	 * @return The next user number
	 * @throws SequenceException
	 */
	public static synchronized String getUserId(String superiorid, String domainid) throws SequenceException {
		if (superiorid == null || superiorid.trim().length() == 0) {
			return null;
		}

		superiorid = StringUtil.replaceOnce(superiorid, Framework.ROOT_DEPARTMENT, Framework.INTERNAL_USER_PREFIX);
		int counter;
		try {
			CounterProcess delegate = (CounterProcess) ProcessFactory.createProcess(CounterProcess.class);
			counter = delegate.getNextValue(superiorid, null, domainid);
		} catch (Exception ex) {
			throw new SequenceException("Cann't get next user number : " + superiorid + ",detail error message:"
					+ ex.getMessage());

		}

		if (counter >= MAX_PERSONS_PER_LEVEL) {
			throw new SequenceException("It had exceed the maxium user number: " + superiorid);
		}

		return superiorid + "-" + StringUtil.leftPad(String.valueOf(counter), 3, "0");
	}

	public static synchronized long getSequenceTimes() {
		try {
			long rtn = System.currentTimeMillis();
			while (rtn == counter) {
				// Thread.sleep(2);
				// Thread.currentThread().wait(2);
				obj.wait(2);
				rtn = System.currentTimeMillis();
			}
			counter = rtn;
			return rtn;
		} catch (Exception ie) {
			return System.currentTimeMillis();
		}
	}

	public static String getFileUUID(File file, String startWith) {
		UUIDGenerator gen = UUIDGenerator.getInstance();
		// file.getCanonicalPath();
		String fileName = file.toString();
		if (!StringUtil.isBlank(startWith)) {
			if (fileName.indexOf(startWith) != -1) {
				fileName = fileName.substring(fileName.indexOf(startWith));
				org.safehaus.uuid.UUID uuid = gen.generateNameBasedUUID(null, fileName);
				return uuid.toString();
			}
		} else {
			org.safehaus.uuid.UUID uuid = gen.generateNameBasedUUID(null, fileName);
			return uuid.toString();
		}
		return "";
	}

	public static void main(String[] args) throws Exception {

		// String receiver = "13725303059";
		// String applicationid = "01b807a0-8c7f-3f80-aa27-c741f5c90259";
		// for (int i = 0; i < 260 + 1; i++) {
		// (getShortMessageCode(receiver, applicationid));
		// }

		// (Sequence.getSequenceTimes());// 125292303662500000

	}
}
