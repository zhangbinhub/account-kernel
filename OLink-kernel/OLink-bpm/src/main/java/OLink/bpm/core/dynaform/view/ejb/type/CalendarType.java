package OLink.bpm.core.dynaform.view.ejb.type;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.view.ejb.ViewType;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
//import DateUtil;
import OLink.bpm.util.ProcessFactory;

public class CalendarType extends AbstractType implements ViewType {
	
	public final static String[] DEFAULT_KEY_FIELDS = {"CldViewDateColum"};
	
	public final static List<String> ALL_KEY_FIELDS = new ArrayList<String>();
	
	public final static Map<String, String> DEFAULT_FIELDS = new LinkedHashMap<String, String>();
	public final static Map<String, String> ALL_FIELDS = new LinkedHashMap<String, String>();
	static {
		DEFAULT_FIELDS.put(DEFAULT_KEY_FIELDS[0], "{*[CldViewDateColum]*}");
		ALL_FIELDS.putAll(DEFAULT_FIELDS);
	}
	
	public CalendarType(View view) {
		super(view);
	}

	public DataPackage<Document> getViewDatas(ParamsTable params, WebUser user, Document sdoc) throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);

		String _pagelines = view.getPagelines();
		int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
		Date currentDate = params.getParameterAsDate("currentDate");
		Calendar currentDate2 = Calendar.getInstance();
		if(currentDate != null)
			currentDate2.setTime(currentDate);
		currentDate2.set(Calendar.HOUR, 0);
		currentDate2.set(Calendar.MINUTE, 0);
		currentDate2.set(Calendar.SECOND, 0);
//		DataPackage<Document> datas = viewProcess.getDataPackage(view, params, user, view.getApplicationid(), currentDate2.getTime(),
//				DateUtil.getNextDateByDayCount(currentDate2.getTime(), 1), lines);
		//Just to get click day data . modify by Dolly 2011-3-24
		DataPackage<Document> datas = viewProcess.getDataPackage(view, params, user, view.getApplicationid(), currentDate2.getTime(),
				currentDate2.getTime(), lines);

		return datas;
	}

	public int intValue() {
		return View.VIEW_TYPE_CALENDAR;
	}
	
	public static boolean addField(String key_Field, String name_Field){
		if(ALL_FIELDS.containsKey(key_Field))return false;
		ALL_FIELDS.putAll(DEFAULT_FIELDS);
		ALL_KEY_FIELDS.add(key_Field);
		ALL_FIELDS.put(key_Field, name_Field);
		return true;
	}
	
	/**
	 * 获取日历视图关联的column集并映射为一个map
	 */
	public Map<String, Column> getColumnMapping() {
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
