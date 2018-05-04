package pers.acp.communications.server.http.param;

/**
 * Created by zhang on 2016/7/26.
 * 配置参数解析类
 */
public class ParamBuild {

    /**
     * 根据类型获取指定的类
     *
     * @param type 类型
     * @return 类
     */
    public static Class<?> getParamClass(String type) {
        Class<?> result;
        switch (type) {
            case "String":
                result = String.class;
                break;
            case "int":
                result = Integer.TYPE;
                break;
            case "char":
                result = Character.TYPE;
                break;
            case "float":
                result = Float.TYPE;
                break;
            case "double":
                result = Double.TYPE;
                break;
            case "long":
                result = Long.TYPE;
                break;
            case "boolean":
                result = Boolean.TYPE;
                break;
            default:
                result = String.class;
        }
        return result;
    }

    /**
     * 根据类型、值字符串获取对应类型的值
     *
     * @param type  类型
     * @param value 字符串
     * @return 目标类型
     */
    public static Object getParamValue(String type, String value) {
        Object result;
        switch (type) {
            case "String":
                result = value;
                break;
            case "int":
                result = Integer.valueOf(value);
                break;
            case "char":
                result = value.charAt(0);
                break;
            case "float":
                result = Float.valueOf(value);
                break;
            case "double":
                result = Double.valueOf(value);
                break;
            case "long":
                result = Long.valueOf(value);
                break;
            case "boolean":
                result = Boolean.valueOf(value);
                break;
            default:
                result = value;
        }
        return result;
    }

}
