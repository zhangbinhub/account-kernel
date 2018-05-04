package OLink.bpm.core.dynaform.view.ejb.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewType;

public class GanttType extends AbstractType implements ViewType {
	//private final static String GANNT_VIEW = "ganttview";
	
	/**
	 * 默认必须的字段
	 */
	public final static String[] DEFAULT_KEY_FIELDS = {"name", "start", "end", "complete"};
	/**
	 * 所有字段
	 */
	public final static List<String> ALL_KEY_FIELDS = new ArrayList<String>();

	
	public final static Map<String, String> DEFAULT_FIELDS = new LinkedHashMap<String, String>();
	static {
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[0], "{*[Task]*}{*[Name]*}");
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[1], "{*[StartDate]*}");
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[2], "{*[EndDate]*}");
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[3], "{*[Completeness]*}");
	}
	public final static Map<String, String> ALL_FIELDS = new LinkedHashMap<String, String>();
	static {
		ALL_FIELDS.putAll(DEFAULT_FIELDS);
		ALL_FIELDS.put("color", "{*[Color]*}");
		ALL_KEY_FIELDS.add("color");
		ALL_FIELDS.put("group", "{*[IsGroup]*}");
		ALL_KEY_FIELDS.add("group");
		ALL_FIELDS.put("milestone", "{*[IsMilestone]*}");
		ALL_KEY_FIELDS.add("milestone");
		ALL_FIELDS.put("caption", "{*[Caption]*}");
		ALL_KEY_FIELDS.add("caption");
		ALL_FIELDS.put("resource", "{*[Resource]*}");
		ALL_KEY_FIELDS.add("resource");
		ALL_FIELDS.put("parent", "{*[ParentTaskID]*}");
		ALL_KEY_FIELDS.add("parent");
		ALL_FIELDS.put("open", "{*[IsOpen]*}");
		ALL_KEY_FIELDS.add("open");
		ALL_FIELDS.put("dependency", "{*[Dependency]*}{*[Task]*}");
		ALL_KEY_FIELDS.add("dependency");
	}
	public GanttType(View view) {
		super(view);
	}

	public int intValue() {
		return View.VIEW_TYPE_GANTT;
	}
	
	/**
	 * 添加字段
	 * @param key_Field 字段(ID)
	 * @param name_Field 字段名
	 * @return
	 */
	public static boolean addField(String key_Field, String name_Field){
		if(ALL_FIELDS.containsKey(key_Field))return false;
		ALL_FIELDS.putAll(DEFAULT_FIELDS);
		ALL_KEY_FIELDS.add(key_Field);
		ALL_FIELDS.put(key_Field, name_Field);
		return true;
	}
	
	/**
	 * 获取甘特视图关联的column集并映射为一个map
	 * @return
	 */
	public Map<String, Column> getColumnMapping(){
		Map<String, Column> columnMapping = new HashMap<String, Column>();
		if(view != null){
			Iterator<Column> columns = view.getColumns().iterator();
			while(columns.hasNext()){
				Column column = columns.next();
				columnMapping.put(column.getMappingField(), column);
			}
		}
		return columnMapping;
	}

}
