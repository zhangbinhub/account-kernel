import com.szzt.ztmq.config.ConsumerConfig;
import com.szzt.ztmq.consumer.KafkaConsumer;
import com.szzt.ztmq.message.MessageEntity;

/**
 * Created by zhangbin on 2017/4/9.
 */
public class TestConsumer extends KafkaConsumer {

    public TestConsumer(ConsumerConfig consumerConfig) {
        super(consumerConfig);
    }

    @Override
    public void doProcess(MessageEntity messageEntity) {
        System.out.println("key=" + messageEntity.getMessageKey() + "  >>>>>>>>>>>>  content=" + messageEntity.getMessageContent());
    }
}
