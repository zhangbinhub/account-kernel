import com.szzt.ztmq.factory.MqFactory;
import com.szzt.ztmq.message.MessageEntity;
import com.szzt.ztmq.producer.KafkaProducer;

/**
 * Created by zhangbin on 2017/4/6.
 * 消息发送
 */
public class TestProducer {

    public static void main(String[] ags) throws InterruptedException {
        TestConsumer consumer = MqFactory.createConsumerInstance(TestConsumer.class);
        if (consumer != null) {
            consumer.startConsumer();
        }

        Thread.sleep(5000);

        KafkaProducer producer = MqFactory.createProducerInstance(KafkaProducer.class);
        if (producer != null) {
            for (int i = 0; i < 10; i++) {
                MessageEntity messageEntity = new MessageEntity("0key" + i, "0value" + i);
                producer.sendMessage("test0", messageEntity);
            }
            for (int i = 0; i < 10; i++) {
                MessageEntity messageEntity = new MessageEntity("1key" + i, "1value" + i);
                producer.sendMessage("test1", messageEntity);
            }
        }
    }

}
