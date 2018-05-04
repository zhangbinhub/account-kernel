package OLink.bpm.core.dynaform.view.action;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.view.ejb.Column;

public class ColumnHelper {
	public static final HashMap<String, String> typeList = new LinkedHashMap<String, String>();
	static {
		typeList.put(Column.COLUMN_TYPE_FIELD, "{*[Field]*}");
		typeList.put(Column.COLUMN_TYPE_SCRIPT, "{*[Script]*}");
		typeList.put(Column.COLUMN_TYPE_OPERATE, "{*[Operate]*}");
		typeList.put(Column.COLUMN_TYPE_LOGO, "{*[LogoUrl]*}");
	}
	
	public static final HashMap<String, String> buttonTypeList = new HashMap<String, String>();
	static{
		buttonTypeList.put(Column.BUTTON_TYPE_DELETE, "{*[Delete]*}");
		buttonTypeList.put(Column.BUTTON_TYPE_DOFLOW, "{*[Submit_WorkFlow]*}");
		buttonTypeList.put(Column.BUTTON_TYPE_TEMPFORM, "{*[core.dynaform.form.type.templateform]*}");
	}

	/**
	 * 返回Column type
	 * 
	 * @return Column type
	 */
	public static Map<String, String> getTypeList() {
		return typeList;
	}
	
	/**
	 * 返回buttonType
	 * 
	 * @return buttonType
	 */
	public static HashMap<String, String> getButtontypelist() {
		return buttonTypeList;
	}

}
