package OLink.bpm.util.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.tree.Node;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JsonUtil {
	public static Collection<Object> toCollection(String JSONStr) {
		Collection<Object> rtn = new ArrayList<Object>();
//Update By XGY 2012.11.23		
		if(JSONStr==null || JSONStr.equals("[]") || JSONStr.equals("")) return rtn;
		JSONArray array = JSONArray.fromObject(JSONStr);
		for (int i = 0; i < array.size(); i++) {
			Object obj = array.get(i);
			if (obj instanceof JSONObject) {
				rtn.add(toMap((JSONObject) obj));
			} else {
				rtn.add(obj);
			}
		}

		return rtn;
		
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static Collection<Object> toCollection(String JSONStr, Class<?> objClass) {
		JSONArray array = JSONArray.fromObject(JSONStr);
		List<Object> list = JSONArray.toList(array, objClass);
		return list;
	}

	public static Map<String, Object> toMap(String jsonStr) {
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		return toMap(jsonObject);
	}

	private static Map<String, Object> toMap(JSONObject jsonObject) {
		Map<String, Object> rtn = new HashMap<String, Object>();

		if (jsonObject.isNullObject()) {
			return null;
		}
		for (Iterator<?> iterator = jsonObject.keys(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object obj = jsonObject.get(key);

			if (obj instanceof JSONArray) {
				rtn.put(key, toArray((JSONArray) obj));
			} else if (obj instanceof JSONObject) {
				rtn.put(key, toMap((JSONObject) obj));
			} else {
				rtn.put(key, obj);
			}
		}

		return rtn;
	}

	private static Object[] toArray(JSONArray jsonArray) {
		Object[] rtn = new Object[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {
			Object obj = jsonArray.get(i);
			if (obj instanceof JSONObject) {
				rtn[i] = toMap((JSONObject) obj);
			} else {
				rtn[i] = obj;
			}
		}

		return rtn;
	}

	public static Object toBean(String jsonStr, Class<?> objClass) {
		JSONObject jsonObject = JSONObject.fromObject(jsonStr);
		Object obj = JSONObject.toBean(jsonObject, objClass);
		return obj;
	}

	public static String collection2Json(Collection<?> collection) {
		return collection2Json(collection, new String[] {});
	}

	public static String collection2Json(Collection<?> collection, String[] excludes) {
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(collection, jsonConfig);
		
//		Object[] rtn = new Object[jsonArray.size()];
//		for (int i = 0; i < jsonArray.size(); i++) {
//			Object obj = jsonArray.get(i);
//			if (obj instanceof JSONObject) {
//				rtn[i] = toMap((JSONObject) obj);
//			} else {
//				rtn[i] = obj;
//			}
//		}
		
		return jsonArray.toString();
	}

	public static String toJson(Object obj) {
		JSONObject jsonObject = JSONObject.fromObject(obj);
		return jsonObject.toString();
	}

	public static void main(String[] args) throws Exception {
		// 1. Collection to String
		Collection<Node> list = new ArrayList<Node>();
		Node node = new Node();
		node.setId("001");
		list.add(node);
		Map <String, Object> attr = new HashMap<String, Object>();
		Map <String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("aaa", "aaa");
		attr.put("valueMap", "{11df-8a45-2d46df42-be72-dfc9d0c0080b:'名称5',11df-8a45-2d4b9a33-be72-dfc9d0c0080b:'n1',11df-8a45-2d4b9a34-be72-dfc9d0c0080b:'@amp;nbsp'}");
		node.setAttr(attr);
		
		String json = collection2Json(list);
		System.out.println(json);
	}
}
