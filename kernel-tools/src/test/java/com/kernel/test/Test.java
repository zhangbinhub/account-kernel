package com.kernel.test;

import pers.acp.tools.common.CommonTools;
import pers.acp.tools.common.DBConTools;
import pers.acp.tools.dbconnection.ConnectionFactory;
import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbin on 2016/9/5.
 */
public class Test {


    public static void main(String[] args) throws Exception {
        JSONObject json = new JSONObject();
        json.put("param1", "11");
        json.put("param2", 2);
        JSONObject json2 = new JSONObject();
        json2.put("param1", "11");
        json2.put("param2", 2);

        JSONArray jsonArray2 = new JSONArray();
        jsonArray2.add(json2);

        json.put("bean2", jsonArray2);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(json);

        Bean1 bean1 = CommonTools.jsonToBean(json, Bean1.class);
        System.out.println(bean1.getParam1());
        bean1.setParam3('a');
        bean1.setParam4(1.231f);
        bean1.setParam5(2.43242);
        bean1.setParam6(899999999);
        bean1.setParam7(false);
        String result = CommonTools.beanToJson(bean1, new String[]{"param7"}).toString();
        System.out.println(result);

        System.out.println(CommonTools.class.getName());
        System.out.println(CommonTools.class.getCanonicalName());
        System.out.println(CommonTools.class.getTypeName());
        System.out.println(CommonTools.class.getSimpleName());

        List<String> list = new ArrayList<>();
        list.add("111a");
        list.add("2222");
        System.out.println(CommonUtility.strInList("111A", list, true));

        long start = System.currentTimeMillis();
        String str = FileCommon.getFileContent("C:\\WorkFile\\个人\\Oms.log.2017-02-17");
        System.out.println("耗时：" + (System.currentTimeMillis() - start));

        DBConTools dbConTools = new DBConTools(0);
        System.out.println(dbConTools.getDbType());
    }
}
