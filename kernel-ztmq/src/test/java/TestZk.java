import com.szzt.ztmq.tools.ZkTools;
import org.I0Itec.zkclient.ZkClient;

/**
 * Created by zhangbin on 2017/4/7.
 */
public class TestZk {

    public static void main(String[] ags) {
        //zk集群的地址
        String ZKServers = "10.40.1.201:2181";
        ZkClient zkClient = ZkTools.getInstance(ZKServers);
        System.out.println(ZkTools.getKafkaBrokerListString(zkClient));
    }

}
