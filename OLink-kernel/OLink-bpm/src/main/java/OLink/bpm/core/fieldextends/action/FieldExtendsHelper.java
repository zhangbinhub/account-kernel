package OLink.bpm.core.fieldextends.action;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.fieldextends.ejb.FieldExtendsProcess;
import OLink.bpm.core.fieldextends.ejb.FieldExtendsVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.department.ejb.DepartmentVO;

public class FieldExtendsHelper {

	public static List<String> fieldNames;
	
	static {
		fieldNames = new ArrayList<String>();
		for(int i = 1; i <= 10; i++){
			fieldNames.add("field" + i);
		}
	}
	
	public String[] types = {FieldExtendsVO.TYPE_STRING, FieldExtendsVO.TYPE_DATE, FieldExtendsVO.TYPE_CLOB/*, FieldExtendsVO.TYPE_NUMBER*/};
	
	public String[] forTables = {FieldExtendsVO.TABLE_USER, FieldExtendsVO.TABLE_DEPT};
	
	public Map<String, String> getTypesMap() {
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < types.length; i++) {
			switch (i) {
			case 0:
				result.put(types[i], "{*[field.extends.type.string]*}");
				break;
				
			case 1:
				result.put(types[i], "{*[field.extends.type.date]*}");
				break;
				
			case 2:
				result.put(types[i], "{*[field.extends.type.clob]*}");
				break;
			/*case 3:
				result.put(types[i], "数字");
				break;*/
			}
		}
		return result;
	}
	
	public Map<String, String> getTypesMapContent() {
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < 2; i++) {
			switch (i) {
			case 0:
				result.put(types[i], "{*[field.extends.type.string]*}");
				break;
				
			case 1:
				result.put(types[i], "{*[field.extends.type.date]*}");
				break;
				
			case 2:
				result.put(types[i], "{*[field.extends.type.clob]*}");
				break;
			/*case 3:
				result.put(types[i], "数字");
				break;*/
			}
		}
		return result;
	}
	
	public Map<String, String> getForTableMap() {
		Map<String, String> result = new HashMap<String, String>();
		for (int i = 0; i < forTables.length; i++) {
			switch (i) {
			case 0:
				result.put(forTables[i], "{*[field.extends.forTable.user]*}");//用户模块
				break;
				
			case 1:
				result.put(forTables[i], "{*[field.extends.forTable.dept]*}");//部门模块
				break;
			}
		}
		return result;
	}
	
	public Map<String, String> getSelectMap() {
		Map<String, String> result = new HashMap<String, String>();
		result.put("true", "{*[field.extends.isNull.yes]*}");
		result.put("false", "{*[field.extends.isNull.no]*}");
		return result;
	}
	
	public String getValueAsField(FieldExtendsVO field, ValueObject object) {
		//获取当前字段名
		String fieldName = field.getName();
		fieldName = fieldName.replaceFirst("f","F");
		String value = "";
		//根据字段名组成方法名，并获取方法的返回值
		try {
			if (object instanceof DepartmentVO) {
				Method method = DepartmentVO.class.getMethod("get" + fieldName);
				Object result = method.invoke(object);
				if(result != null) {
					value = result.toString();
				}
			} else if (object instanceof UserVO) {
				Method method = UserVO.class.getMethod("get" + fieldName);
				Object result = method.invoke(object);
				if(result != null) {
					value = result.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	/**
	 * 拼装前台显示的字符串。用于前台显示扩展字段
	 * 
	 * @param fieldExtendses
	 *            扩展字段集合
	 * @param userVO
	 *            当前要显示的用户
	 * @throws NoSuchMethodException
	 * @throws Exception
	 */
	public String getFieldHtml(List<FieldExtendsVO> fieldExtendses, ValueObject object) throws Exception {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < fieldExtendses.size(); i++) {
			// 获取当前字段名
			FieldExtendsVO fieldExtendsVO1 = fieldExtendses.get(i);
			String value1 = fieldExtendsVO1.getValue(object);

			// 判断字段2是否存在，如果存在就根据字段2的名字组成方法名，并获取方法的返回值
			FieldExtendsVO fieldExtendsVO2 = null;
			String value2 = "";
			if (i < fieldExtendses.size() - 1) {
				i = i + 1;
				fieldExtendsVO2 = fieldExtendses.get(i);
				value2 = fieldExtendsVO2.getValue(object);
			}

			// 把字段的名字和内容拼装成html table的形式，格式为一行两列
			sb.append("<tr>");
			sb.append("<td class='commFont'>" + fieldExtendsVO1.getLabel()
					+ "：</td>");
			if (fieldExtendsVO2 != null) {
				sb.append("<td class='commFont'>" + fieldExtendsVO2.getLabel()
						+ "：</td>");
			} else {
				sb.append("<td class='commFont'>&nbsp;</td>");
			}
			sb.append("</tr>");

			sb.append("<tr>");
			sb.append("<td>");

			// 根据字段1的类型选择不同的HTML控件
			if ("string".equals(fieldExtendsVO1.getType())) {
				sb.append("<input class='input-cmd' name='content."
						+ fieldExtendsVO1.getName() + "' value='" + value1
						+ "' style=\"width: 280px;\" />");
			} else if ("date".equals(fieldExtendsVO1.getType())) {
				sb
						.append("<input class='Wdate' name='content."
								+ fieldExtendsVO1.getName()
								+ "' value='"
								+ value1
								+ "'"
								+ " onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd'})\" style=\"width: 280px;\" />");
			} else if ("clob".equals(fieldExtendsVO1.getType())) {
				sb.append("<textArea rows='5' cols='56' name='content."
						+ fieldExtendsVO1.getName() + "' style=\"width: 280px;\">" + value1
						+ "</textArea>");
			} else {
				sb.append("<input class='input-cmd' name='content."
						+ fieldExtendsVO1.getName() + "' value='" + value1
						+ "' style=\"width: 280px;\"/>");
			}
			sb.append("</td>");
			sb.append("<td>");

			// 根据字段2的类型选择不同的HTML控件
			if (fieldExtendsVO2 != null) {
				if ("string".equals(fieldExtendsVO2.getType()))
					sb.append("<input class='input-cmd' name='content."
							+ fieldExtendsVO2.getName() + "' value='" + value2
							+ "' style=\"width: 280px;\"/>");
				else if ("date".equals(fieldExtendsVO2.getType()))
					sb
							.append("<input class='Wdate' name='content."
									+ fieldExtendsVO2.getName()
									+ "' value='"
									+ value2
									+ "'"
									+ " onfocus=\"WdatePicker({dateFmt:'yyyy-MM-dd'})\" style=\"width: 280px;\" />");
				else if ("clob".equals(fieldExtendsVO2.getType()))
					sb.append("<textArea rows='5' cols='56' name='content."
							+ fieldExtendsVO2.getName() + "' style=\"width: 280px;\">" + value2
							+ "</textArea>");
			} else {
				sb.append("&nbsp;");
			}
			sb.append("</td>");
			sb.append("</tr>");
		}

		return sb.toString();
	}
	
	public List<String> getUnUseFieldsByUser(String domain) {
		List<String> list = new ArrayList<String>(fieldNames);
		try {
			FieldExtendsProcess fep = (FieldExtendsProcess) ProcessFactory.createProcess(FieldExtendsProcess.class);
			List<FieldExtendsVO> useList = fep.queryFieldExtendsByTable(domain, FieldExtendsVO.TABLE_USER);
			if (useList != null) {
				for (FieldExtendsVO vo : useList) {
					list.remove(vo.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<String> getUnUseFieldsByDep(String domain) {
		List<String> list = new ArrayList<String>(fieldNames);
		try {
			FieldExtendsProcess fep = (FieldExtendsProcess) ProcessFactory.createProcess(FieldExtendsProcess.class);
			List<FieldExtendsVO> useList = fep.queryFieldExtendsByTable(domain, FieldExtendsVO.TABLE_DEPT);
			if (useList != null) {
				for (FieldExtendsVO vo : useList) {
					list.remove(vo.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
}
