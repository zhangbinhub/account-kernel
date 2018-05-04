package OLink.core.protection;

import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.util.timer.Job;
import org.apache.log4j.Logger;

public class ReceiveJob extends Job
{
    private static final Logger log = Logger.getLogger(ReceiveJob.class);

    private boolean stop = false;
    private int time = 60000;

    public ReceiveJob() {
        String temp = PropertyUtil.getByPropName("shortmessage", "received.job.interval.time");
        try {
            this.time = Integer.parseInt(temp);
        } catch (Exception e) {
            this.time = 60000;
            log.warn("##[ 短信获取回复时间间隔配置出错: + " + e.getMessage() + " ]##");
        }
    }

    public void run() {
        log.info("########[ Starting auto receive message job... ]########");
        while (!this.stop) {
            try {
                String domainid = null;
                String applicationid = null;
                MessageManager manager =
                        MessageManager.getInstance(new Validator(domainid, applicationid));

                IReceiver receiver = manager.getReceiver();
                receiver.receiveMessage();
                if (log.isDebugEnabled())
                    log.info("########[ Auto receive message job... ]########");
            }
            catch (Exception e) {
                log.warn("######## " + e.toString() + " ########");
            }
            try {
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                log.warn("######## " + e.toString() + " ########");
            }
        }
        log.info("########[ Stoping auto receive message job... ]########");
    }

    public boolean cancel() {
        this.stop = true;
        return super.cancel();
    }

    public static void main(String[] args) {
        try {
            ReceiveJob job = new ReceiveJob();
            job.run();
            job.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}