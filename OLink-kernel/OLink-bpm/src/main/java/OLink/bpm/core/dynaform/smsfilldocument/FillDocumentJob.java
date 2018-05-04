package OLink.bpm.core.dynaform.smsfilldocument;

import java.util.Vector;

import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageVO;
import OLink.bpm.util.timer.Job;
import org.apache.log4j.Logger;

public class FillDocumentJob extends Job {

	private final static Logger log = Logger.getLogger(FillDocumentJob.class);

	private Vector<ReceivedMessageVO> receiveQueue;

	private boolean stop = false;

	public FillDocumentJob() {
		receiveQueue = new Vector<ReceivedMessageVO>(20);
	}

	public void run() {
		log.debug("#####[ Starting... ]#####");
		new Thread(new Reader(receiveQueue)).start();
		while (true && !stop) {
			log.debug("#####[ Runing... ]#####");
			try {
				if (!receiveQueue.isEmpty()) {
					//ReceivedMessageVO recVO = (ReceivedMessageVO) receiveQueue.remove(0);
					ReceivedMessageVO recVO = receiveQueue.remove(0);
					if (recVO != null)
						RecievedMessageParser.parse(recVO);
				} else {
					Thread.sleep(60000);
				}
			} catch (DataMessageException e) {
				String message = e.getMessage();
				int status = e.getStatus();
				if (status == 0)
					log.debug("#####[ " + message + " ]#####");
				else
					log.warn("******! " + message + " !*****");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.info("#####[ Stoping... ]#####");
	}

	public static void main(String[] args) {
		FillDocumentJob job = new FillDocumentJob();
		job.run();
		job.cancel();
	}

	public boolean cancel() {
		Reader.setStop(true);
		stop = true;
		return super.cancel();
	}
}
