package OLink.core.protection;

/**
 * Created by zhang on 2016/5/28.
 */
public class KeyPool {

    public static String getKey(int num) {
        String result;
        switch (num) {
            case 0://公司官网地址
                result = "www.pbless.com.cn";
                break;
            case 1://加密密码，暂时无用
                result = "szzt20160528";
                break;
            case 2://原包名
                result = "cn.myapps.";
                break;
            case 3://试用版有效期截止日期，正版则为空
                result = "";
                break;
            case 4://工作流引擎名称
                result = "obpm";
                break;
            case 5://新包名
                result = "OLink.bpm.";
                break;
            case 6://公司名称
                result = "广州证通网络有限公司昆明分公司";
                break;
            default:
                result = "";
        }
        return result;
    }

}
