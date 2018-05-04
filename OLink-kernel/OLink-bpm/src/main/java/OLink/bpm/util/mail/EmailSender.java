package OLink.bpm.util.mail;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public class EmailSender {
	
	private static String keyLock = "KeyLock";

	private Queue<Email> emailQueue = new ConcurrentLinkedQueue<Email>();

	private Object waitForJobsMonitor = new Object();

	private Thread thread = new SendMailThread();

	private boolean isWaitForJobs = false;

	private static EmailSender sender = new EmailSender();
	
	private static final Logger log = Logger.getLogger(EmailSender.class);

//	/**
//	 * 为兼容以往模式所做的构造器
//	 * @deprecated 旧版本构造方法，已掉弃，请使用单例
//	 * @see #getInstance()
//	 */
//	public EmailSender() {
//		
//	}

//	/**
//	 * 为兼容以往模式所做的构造器
//	 * 
//	 * @param from
//	 * @param to
//	 * @param subject
//	 * @param body
//	 * @param host
//	 * @param user
//	 * @param password
//	 * @param bbc
//	 * @param validate
//	 * @deprecated 旧版本构造方法，已掉弃，请使用单例
//	 * @see #getInstance()
//	 */
//	public EmailSender(String from, String to, String subject, String body,
//			String host, String user, String password, String bbc,
//			boolean validate) {
//		Email email = new Email(from, to, subject, body, host, user, password,
//				bbc, validate);
//		addEmail(email);
//	}
	
	private EmailSender() {
		// 将邮件发送线程设置成后台线程
		//thread.setDaemon(true);
	}

	public static EmailSender getInstance() {
		synchronized (keyLock) {
			if (sender == null) {
				sender = new EmailSender();
				sender.isWaitForJobs = true;
			}
			return sender;
		}
	}

//	/**
//	 * @deprecated 旧版本构造方法，已掉弃
//	 */
//	public void sendEmail2() throws Exception {
//		// thread.start();
//		this.sendEmail();
//	}
	
	public void sendEmail() {
		if (!thread.isAlive()) {
			thread.start();
		}
	}

//	/**
//	 * 判断是否已在工作
//	 * @deprecated 旧版本构造方法，已掉弃
//	 * @return
//	 */
//	public boolean isWorking() {
//		return thread.isAlive();
//	}

	/**
	 * 加入Email到发送队列
	 * 
	 * @param from
	 * @param to
	 * @param subject
	 * @param body
	 * @param host
	 * @param user
	 * @param password
	 * @param bbc
	 * @param validate
	 */
	public void addEmail(String from, String to, String subject, String body,
			String host, String user, String password, String bcc,
			boolean validate) {
		addEmail(from, to, subject, body, host, user, password, null, bcc, null, validate);
	}
	
	/**
	 * 加入Email到发送队列
	 * @param from
	 * @param to
	 * @param subject
	 * @param body
	 * @param host
	 * @param user
	 * @param password
	 * @param cc
	 * @param bcc
	 * @param attachFileNames
	 * @param validate
	 */
	public void addEmail(String from, String to, String subject, String body, 
			String host, String user, String password, String cc, String bcc, 
			String[] attachFileNames, boolean validate) {
		Email email = new Email(from, to, subject, body, host, user, password,
				cc, bcc, attachFileNames, validate);
		this.addEmail(email);
	}

	/**
	 * 加入Email到发送队列
	 * 
	 * @param email
	 */
	public void addEmail(Email email) {
		emailQueue.add(email);
		kickThread();
	}

	public void clear() {
		emailQueue.clear();
	}

	private void kickThread() {
		if (!this.thread.isInterrupted()) {
			synchronized (waitForJobsMonitor) {
				waitForJobsMonitor.notifyAll();
			}
		}
	}

	public static void main(String[] argv) throws Exception {
		EmailSender sender = EmailSender.getInstance();
		Email email = new Email("taowei1160@163.com", "4112384500@qq.com",
				"test", "testbody", "smtp.163.com", "taowei160", "**************", "", true);
		
		while (true) {
			synchronized (sender) {
				sender.addEmail(email);
				sender.sendEmail();
				sender.wait(20 * 1000);
			}
		}
	}

	/**
	 * 邮件发送的线程
	 * 
	 * @author Nicholas
	 * 
	 */
	public class SendMailThread extends Thread {

		public void run() {
			while (true) {
				synchronized (waitForJobsMonitor) {
					if (!emailQueue.isEmpty()) {
						// 获取并移除此队列的头，如果此队列为空，则返回 null
						Email email = emailQueue.poll();
						// Email不为空时，发邮件
						if (email != null) {
							boolean sendSuccess = false;
							if (email.isHaveAttachment()) {
								MultiEmailSender sender = new MultiEmailSender(email);
								sendSuccess = sender.sendMultiEmail();
							} else {
								SimpleEmailSender sender = new SimpleEmailSender(email, true);
								sendSuccess = sender.sendHtmlEmail();
							}
							if (sendSuccess) {
								log.info("Sent email to: " + email.getTo() + " success");
							}
						}
					}
					isWaitForJobs = emailQueue.isEmpty();
				}

				// 等待新的作业
				if (isWaitForJobs) {
					synchronized (waitForJobsMonitor) {
						try {
							if(waitForJobsMonitor != null) {
								waitForJobsMonitor.wait();
							}
						} catch (InterruptedException e) {
							log.warn("EmailSender.SendMailThread: " + e.getMessage());
						}
					}
				}
			}
		}
	}
}
