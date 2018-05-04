package pers.acp.tools.utility;

import pers.acp.tools.dbconnection.annotation.ADBTable;
import pers.acp.tools.dbconnection.annotation.ADBTableField;
import pers.acp.tools.dbconnection.annotation.ADBTablePrimaryKey;
import pers.acp.tools.dbconnection.entity.DBTable;
import pers.acp.tools.dbconnection.entity.DBTableInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbin on 2016/9/9.
 * json操作工具类
 */
public class JSONUtility {

    private static Logger log = Logger.getLogger(JSONUtility.class);

    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    public static JSONObject getJsonObjectFromStr(String src) {
        JSONObject result;
        try {
            result = JSONObject.fromObject(src);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new JSONObject();
        }
        return result;
    }

    /**
     * 字符串转JSON数组
     *
     * @param src 字符串
     * @return json对象
     */
    public static JSONArray getJsonArrayFromStr(String src) {
        JSONArray result;
        try {
            result = JSONArray.fromObject(src);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result = new JSONArray();
        }
        return result;
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj json对象（JSONObject）
     * @param cls     目标类（不支持Map）
     * @return 目标对象（实体对象或List）
     */
    public static <T> T jsonToBean(JSONObject jsonObj, Class<T> cls) {
        try {
            if (cls.equals(Map.class) || cls.equals(HashMap.class)) {
                throw new Exception("map object need init!");
            } else {
                return jsonToBean(jsonObj, cls, cls.newInstance());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj json对象（JSONObject）
     * @param cls     目标类
     * @param obj     目标实体对象（支持Map对象）
     * @return 目标对象（实体对象或List）
     */
    public static <T> T jsonToBean(JSONObject jsonObj, Class<?> cls, T obj) {
        try {
            return jsonToObject(jsonObj, cls, obj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj   json对象（JSONArray）
     * @param itemClass List中元素的类
     * @return 目标对象（实体对象或List）
     */
    public static <T> List<T> jsonToList(JSONArray jsonObj, Class<T> itemClass) {
        try {
            return jsonToArrayList(jsonObj, itemClass);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * json对象内容映射到实体对象中
     *
     * @param json json对象
     * @param cls  目标类
     * @param obj  实体对象
     * @return 实体对象
     */
    @SuppressWarnings("unchecked")
    private static <T> T jsonToObject(JSONObject json, Class<?> cls, T obj) throws Exception {
        T instance;
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Object key : json.keySet()) {
                map.put(key.toString().toLowerCase(), json.get(key));
            }
            instance = (T) map;
        } else {
            instance = obj;
            Map<String, Field> allFields = new HashMap<>();
            if (obj instanceof DBTable && cls.isAnnotationPresent(ADBTable.class)) {
                if (cls.equals(obj.getClass())) {
                    ADBTable adbTable = cls.getAnnotation(ADBTable.class);
                    Class<?> clas = cls;
                    if (adbTable.isVirtual()) {
                        clas = cls.getSuperclass();
                    }
                    while (clas != null && clas.isAnnotationPresent(ADBTable.class)) {
                        Field[] fields = clas.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(ADBTablePrimaryKey.class) || field.isAnnotationPresent(ADBTableField.class)) {
                                if (!allFields.containsKey(field.getName())) {
                                    allFields.put(field.getName(), field);
                                }
                            }
                        }
                        clas = clas.getSuperclass();
                    }
                } else {
                    throw new Exception("object is not an instance of the class : " + cls.getCanonicalName());
                }
            }
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                if (!allFields.containsKey(field.getName())) {
                    allFields.put(field.getName(), field);
                }
            }
            for (Object key : json.keySet()) {
                for (Map.Entry<String, Field> fielditem : allFields.entrySet()) {
                    Field field = fielditem.getValue();
                    field.setAccessible(true);
                    if (key.toString().toLowerCase().equals(field.getName().toLowerCase())) {
                        Class<?> cls_t = field.getType();
                        if (cls_t.equals(String.class)) {
                            field.set(instance, json.getString(key.toString()).trim());
                        } else if (cls_t.equals(Integer.TYPE)) {
                            field.set(instance, Integer.valueOf(json.getString(key.toString()).trim()));
                        } else if (cls_t.equals(Character.TYPE)) {
                            field.set(instance, json.get(key.toString()));
                        } else if (cls_t.equals(Float.TYPE)) {
                            field.set(instance, Float.valueOf(json.getString(key.toString()).trim()));
                        } else if (cls_t.equals(Double.TYPE)) {
                            field.set(instance, Double.valueOf(json.getString(key.toString()).trim()));
                        } else if (cls_t.equals(Long.TYPE)) {
                            field.set(instance, Long.valueOf(json.getString(key.toString()).trim()));
                        } else if (cls_t.equals(Boolean.TYPE)) {
                            field.set(instance, Boolean.valueOf(json.getString(key.toString()).trim()));
                        } else if (cls_t.equals(JSONObject.class)) {
                            field.set(instance, json.getJSONObject(key.toString()));
                        } else if (cls_t.equals(JSONArray.class)) {
                            field.set(instance, json.getJSONArray(key.toString()));
                        } else if (cls_t.equals(BigDecimal.class)) {
                            field.set(instance, BigDecimal.valueOf(json.getDouble(key.toString())));
                        } else {
                            Object itemobj = json.get(key);
                            if (cls_t.equals(List.class) || cls_t.equals(ArrayList.class)) {
                                ParameterizedType pt = (ParameterizedType) field.getGenericType();
                                Class<?> itemClass = (Class<?>) pt.getActualTypeArguments()[0];
                                field.set(instance, jsonToArrayList((JSONArray) itemobj, itemClass));
                            } else {
                                field.set(instance, jsonToObject((JSONObject) itemobj, cls_t, cls_t.newInstance()));
                            }
                        }
                    }
                }
            }
        }
        return instance;
    }

    /**
     * json 数组转换为List
     *
     * @param json      json数组
     * @param itemClass List中元素的类
     * @return 目标List对象
     */
    private static <T> List<T> jsonToArrayList(JSONArray json, Class<T> itemClass) {
        try {
            List<T> list = new ArrayList<>();
            for (int i = 0; i < json.size(); i++) {
                Object json_c = json.get(i);
                if (json_c instanceof JSONObject) {
                    list.add(jsonToObject(json.getJSONObject(i), itemClass, itemClass.newInstance()));
                }
            }
            return list;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @return json对象
     */
    public static JSONObject beanToJson(Object instance) {
        return beanToJson(instance, null);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param cls      实体类（只持Map）
     * @param instance 实体对象（只持Map对象）
     * @return json对象
     */
    public static JSONObject beanToJson(Class<?> cls, Object instance) {
        return beanToJson(cls, instance, null);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @param excludes 排除的实体成员
     * @return json对象
     */
    public static JSONObject beanToJson(Object instance, String[] excludes) {
        return beanToJson(instance.getClass(), instance, excludes);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @param excludes 排除的实体成员
     * @return json对象
     */
    @SuppressWarnings("unchecked")
    public static JSONObject beanToJson(Class<?> cls, Object instance, String[] excludes) {
        JSONObject json = new JSONObject();
        if (instance instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) instance).entrySet()) {
                String key = entry.getKey().toLowerCase();
                Object value = entry.getValue();
                json.put(key, objToJson(value, excludes));
            }
        } else {
            Map<String, Object> fieldsMap = new HashMap<>();
            if (instance instanceof DBTable) {
                List<DBTableInfo> tableInfos = ((DBTable) instance).getTableInfos();
                for (DBTableInfo tableInfo : tableInfos) {
                    tableInfo.getpKeys().entrySet().stream().filter(entry -> excludes == null || !CommonUtility.strInArray(entry.getValue().getFieldName(), excludes, false)).forEach(entry -> fieldsMap.put(entry.getValue().getFieldName(), entry.getValue().getValue()));
                    tableInfo.getFields().entrySet().stream().filter(entry -> excludes == null || !CommonUtility.strInArray(entry.getValue().getFieldName(), excludes, false)).forEach(entry -> fieldsMap.put(entry.getValue().getFieldName(), entry.getValue().getValue()));
                }
            }
            try {
                Field[] fields = cls.getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    if (excludes == null || !CommonUtility.strInArray(field.getName(), excludes, false)) {
                        if (!fieldsMap.containsKey(field.getName())) {
                            fieldsMap.put(field.getName(), field.get(instance));
                        }
                    }
                }
                for (Map.Entry<String, Object> entry : fieldsMap.entrySet()) {
                    json.put(entry.getKey().toLowerCase(), objToJson(entry.getValue(), excludes));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                json = new JSONObject();
            }
        }
        return json;
    }

    /**
     * 实例转json
     *
     * @param value    实例
     * @param excludes 排除的实体成员
     * @return json
     */
    private static Object objToJson(Object value, String[] excludes) {
        if (value != null && !(value instanceof InputStream) && !(value instanceof OutputStream) && !(value instanceof Enum)
                && !(value instanceof Annotation || value.getClass().isAnnotation())) {
            Object result;
            Class<?> cls = value.getClass();
            if (!cls.equals(String.class) && !cls.equals(JSONObject.class) && !cls.equals(JSONArray.class)
                    && !(value instanceof Integer) && !(value instanceof Character) && !(value instanceof BigDecimal)
                    && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof Long)
                    && !(value instanceof Boolean)) {
                if (value instanceof List) {
                    result = listToJson((List) value, excludes);
                } else {
                    result = beanToJson(value, excludes);
                }
            } else {
                result = value;
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * List转换为json数组
     *
     * @param list List对象
     * @return json数组
     */
    public static JSONArray listToJson(List list) {
        return listToJson(list, null);
    }

    /**
     * List转换为json数组
     *
     * @param list     List对象
     * @param excludes 排除的实体成员
     * @return json数组
     */
    public static JSONArray listToJson(List list, String[] excludes) {
        JSONArray json = new JSONArray();
        for (Object item : list) {
            if (item instanceof List) {
                json.add(listToJson((List) item, excludes));
            } else {
                json.add(beanToJson(item, excludes));
            }
        }
        return json;
    }

}
