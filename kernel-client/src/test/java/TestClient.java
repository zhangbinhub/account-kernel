import pers.acp.communications.client.http.HttpServerClient;
import pers.acp.tools.security.MD5Utils;
import net.sf.json.JSONObject;

/**
 * Created by zhang on 2016/6/1.
 * 客户端测试demo
 */
public class TestClient {

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 1; i++) {
            final int x = i + 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1; j++) {
                        int flag = 2;
                        if (flag == 1) {
                            HttpServerClient client = new HttpServerClient();
                            JSONObject body = new JSONObject();
                            body.put("param1", "尼玛");
                            body.put("param2", Integer.valueOf(x + "" + j));
                            client.setUrl("http://192.168.50.117:8080/acp/ctrl/test");
                            client.setClientCharset("utf-8");
                            long begin = System.currentTimeMillis();
                            String recevStr = client.doHttpPostJSONStr(body.toString());
                            System.out.println(recevStr);
                            System.out.println(x + "" + j + "----->" + (System.currentTimeMillis() - begin));
                        } else {
//                            ESBEntity entity = new ESBEntity();
//                            entity.setUrl("http://192.168.50.112:8192/olinkbus");
//                            entity.setServerName("account");
//                            entity.setServerno("cust");
//                            AccountPackage accountPackage = new AccountPackage();
//                            accountPackage.setServerno("cust_1002");
//                            accountPackage.setAction("cust_add");
//                            JSONObject messageBody = new JSONObject();
//                            JSONObject data = new JSONObject();
//                            data.put("nickname", "你猜");
//                            data.put("telephone", "13002312345");
//                            data.put("loginpwd", MD5Utils.encrypt(MD5Utils.encrypt("000000").toLowerCase() + "13002312345").toLowerCase());
//                            messageBody.put("data", data);
//                            accountPackage.setMessageBody(messageBody);
//                            JSONObject body = accountPackage.doPackage("EF658A52-FEA4-6A3D-BB5E-51C5EA590074", "3FB5CDE4EFD41D407E5371FC74E75F31");
//                            entity.setBody(body);
//                            ESBClient client = new ESBClient(entity);
//                            try {
//                                Response response = client.doRequest();
//                                System.out.println(response.getBody().toString());
//                            } catch (ESBException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                }
            }).start();
        }
    }
}
