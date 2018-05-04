package OLink.bpm.core.report.oreport.ejb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.dynaform.form.ejb.FormProcessBean;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.dynaform.view.ejb.ViewProcessBean;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.report.oreport.dao.OReportDAO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.RuntimeDaoManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import OLink.bpm.core.user.action.WebUser;
import edu.emory.mathcs.backport.java.util.Arrays;

public class OReportProcessBean extends AbstractRunTimeProcessBean<Object> implements OReportProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4791212334079496055L;

	public OReportProcessBean(String applicationId) {
		super(applicationId);
	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getOReportDAO(getConnection(), getApplicationId());
	}
	
	public Collection<Map<String, String>> getCustomizeOReportData(String viewid, String domainid, JSONObject xcolumn, JSONArray ycolumns, JSONArray filter, WebUser user,ParamsTable params) throws Exception{

		//映射表单视图字段处理
		handlerColumnName(viewid, xcolumn, ycolumns, filter);
		
		//表名处理
		String tabName = "select * from (" + getViewSQL(viewid, user,params) + ") otb where otb.DOMAINID='" + domainid + "'";
		
		//获取sql处理
		String sql = null;
		Map<String, String> by = null;
		if(null!=ycolumns && !xcolumn.isNullObject()){//一对或多对XY列
			by = ((OReportDAO) getDAO()).getGroupAndOrderBy(xcolumn, ycolumns);
			sql = ((OReportDAO) getDAO()).getMultipleAxisesSql(xcolumn, ycolumns, tabName);
		}else {//单列
			Map<String, String> SqlAndBy = null;
			by = new LinkedHashMap<String, String>();
			if(null!=ycolumns){
				JSONObject ycolumn = ycolumns.getJSONObject(0);
				SqlAndBy = ((OReportDAO) getDAO()).getSingleAxisSqlAndBy(ycolumn, tabName);
				sql = SqlAndBy.get("sql").toString();

			}else{
				SqlAndBy = ((OReportDAO) getDAO()).getSingleAxisSqlAndBy(xcolumn, tabName);
				sql = SqlAndBy.get("sql").toString();
			}
			by.put("gb", "group by " + SqlAndBy.get("by") + "\n");
			by.put("ob", "order by " + SqlAndBy.get("by"));
		}
		
		//过滤器
		Map<String, String> filterSqls = null;
		if(filter!=null){
			filterSqls = getFilterSqls(filter, tabName, by.get("gb").toString());
		}
		
		//获取数据
		Collection<Map<String, String>> data = null;
		if(null!=sql){
			String where = "";
			String having = "";
			if(null!=filterSqls){
				where = filterSqls.get("where").toString();
				having = filterSqls.get("having").toString();
			}
			String gb = by.get("gb").toString();
			String ob = by.get("ob").toString();

			String executeSQL = sql + "from (" + tabName + ") table_0\n" + where + gb + having + ob;
			//打印出调用SQL
			//System.out.println(executeSQL);
			data = ((OReportDAO) getDAO()).getData(executeSQL, 50);
		}

		return data; 
	}
	
	public List<String[]> getNoDupContent(Collection<Map<String, String>> data, JSONObject xCol, JSONArray yCols){
		
		List<HashSet<String>> noDupContent = new ArrayList<HashSet<String>>();
		for(int i=0; i<yCols.size() + 1; i++){
			noDupContent.add(new HashSet<String>());
		}

		if(data!=null){
			for(Iterator<Map<String, String>> it = data.iterator(); it.hasNext(); ){
				Map<String, String> map = it.next();
				int i = 0;
				for(Map.Entry<String, String> entry : map.entrySet()){//循环获取y1、y2列值
					noDupContent.get(i).add(entry.getValue());
					i++;
				}
			}
		}
		
		return sortNoDupContent(noDupContent, xCol, yCols);

	}
	
	public Map<String, Object> singleColumnHandle(JSONObject xCol, JSONArray yCols){
		JSONObject xColtmp = null;
		JSONArray yColstmp = null;
		if(null==yCols){
			xColtmp = JSONObject.fromObject(xCol.toString());
			yColstmp = new JSONArray();
			JSONObject yCol = JSONObject.fromObject(xCol.toString());
			if(yCol.getString("fieldtype").equals(Item.VALUE_TYPE_NUMBER) || yCol.getString("fx").equals("count")){
				xColtmp.discard("fieldtype");
				xColtmp.discard("fx");
				xColtmp.accumulate("fieldtype", Item.VALUE_TYPE_VARCHAR);
				xColtmp.accumulate("fx", "");
			}else{
				yCol.discard("fieldtype");
				yCol.discard("fx");
				yCol.accumulate("fieldtype", Item.VALUE_TYPE_NUMBER);
				yCol.accumulate("fx", "count");
			}
			yColstmp.add(yCol);
		}else if(xCol.isNullObject()){
			xColtmp = JSONObject.fromObject(yCols.getJSONObject(0).toString());
			yColstmp = JSONArray.fromObject(yCols.toString());
			
			if(xColtmp.getString("fieldtype").equals(Item.VALUE_TYPE_NUMBER) || xColtmp.getString("fx").equals("count")){
				xColtmp.discard("fieldtype");
				xColtmp.discard("fx");
				xColtmp.accumulate("fieldtype", Item.VALUE_TYPE_VARCHAR);
				xColtmp.accumulate("fx", "");
			}
			JSONObject yCol = yColstmp.getJSONObject(0);
			if(!yCol.getString("fieldtype").equals(Item.VALUE_TYPE_NUMBER) && !yCol.getString("fx").equals("count")){
				yCol.discard("fieldtype");
				yCol.discard("fx");
				yCol.accumulate("fieldtype", Item.VALUE_TYPE_NUMBER);
				yCol.accumulate("fx", "count");
			}

		}else{
			xColtmp = xCol;
			yColstmp = yCols;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("xCol", xColtmp);
		map.put("yCols", yColstmp);
		return map;
	}
	
	/**
	 * 对不重复的值根据轴的信息排序
	 * @param data
	 * @param xCol
	 * @param yCols
	 * @return
	 */
	public List<String[]> sortNoDupContent(List<HashSet<String>> data, JSONObject xCol, JSONArray yCols){
		List<String[]> noDupSortData = new ArrayList<String[]>();
		int i =0;
		for(Iterator<HashSet<String>> it = data.iterator(); it.hasNext(); i++){
			HashSet<String> columnData = it.next();
			if(i==0){//X轴内容排序
				noDupSortData.add(sortContentByFieldType(columnData, xCol));
			}else{//Y轴内容排序
				noDupSortData.add(sortContentByFieldType(columnData, yCols.getJSONObject(i-1)));
			}

		}
		return noDupSortData;
	}
	
	/***
	 * 根据类型排序所有不重复数据
	 * @param set
	 * @param col
	 */
	public String[] sortContentByFieldType(HashSet<String> set, JSONObject col){
		String fieldType = col.getString("fieldtype");
		String fx = col.getString("fx");
		if(fieldType.equals(Item.VALUE_TYPE_DATE)){
			return sortDateType(fx, set, col);
		}else if(fieldType.equals(Item.VALUE_TYPE_NUMBER) || fx.equals("count")){
			return sortNumberType(set);
		}else{
			return set.toArray(new String[0]);
		}

	}
	
	/**
	 * 排序数字类型数据
	 * @param set
	 * @return
	 */
	public String[] sortNumberType(HashSet<String> set){
		
		Map<Double, String> map = new HashMap<Double, String>();
		String[] noDupSort = new String[set.size()];
		double[] array = new double[set.size()];
		int i=0;
		for(Iterator<String> it = set.iterator(); it.hasNext(); ){
			String value = it.next();
			double d = Double.parseDouble(value);
			array[i] = d;
			map.put(d, value);
			i++;
		}
		Arrays.sort(array);
		for(Iterator<String> it = set.iterator(); it.hasNext(); ){
			String item = it.next();
			for(int j = 0; j<array.length; j++){
				if(map.get(array[j]).equals(item)){
					noDupSort[j] = item;
				}
			}
		}
		return noDupSort;
	}
	
	/**
	 * 排序日期类型列数据
	 * @param fx
	 * @param set
	 * @param col
	 */
	public String[] sortDateType(String fx, HashSet<String> set, JSONObject col){
		
		
		if(set.contains("")){
			set.remove("");
		}
		if(set.contains(null)){
			set.remove(null);
		}
		String[] noDupSort = new String[set.size()];
		
		if(fx.startsWith("date")){//日期、日期&时间
			Map<Long, String> map = new HashMap<Long, String>();
			int type = Integer.parseInt(fx.substring(4));
			long[] array = new long[set.size()];
			SimpleDateFormat sdf = null;
			if(type == 0){
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			}else{
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			}
			int i=0;
			for(Iterator<String> it = set.iterator(); it.hasNext(); ){
				String item = it.next();
				try {
					Date d = sdf.parse(item);
					map.put(d.getTime(), item);
					array[i] = d.getTime();
					i++;
				} catch (Exception e) {
					System.out.println("Unparseable date: " + item);
				}
			}
			Arrays.sort(array);
			
			for(Iterator<String> it = set.iterator(); it.hasNext(); ){
				String item = it.next();
				for(int j = 0; j<array.length; j++){
					if(map.get(array[j]).equals(item)){
						noDupSort[j] = item;
					}
				}
			}
		}else if(fx.startsWith("a")){//季度&年、月&年、周年
			int type = Integer.parseInt(fx.substring(1));
			int[] array = new int[set.size()];
			int i = 0;
			Map<Integer, String> map = new HashMap<Integer, String>();
			for(Iterator<String> it = set.iterator(); it.hasNext(); ){
				String item = it.next();
				int value = sortDoubleDateUnit(type, item);
				map.put(value, item);
				array[i] = value;
				i++;
			}
			Arrays.sort(array);
			for(Iterator<String> it = set.iterator(); it.hasNext(); ){
				String item = it.next();
				for(int j = 0; j<array.length; j++){
					if(map.get(array[j]).equals(item)){
						noDupSort[j] = item;
					}
				}
			}
		}else{//其他
			int[] array = new int[set.size()];
			int i = 0;
			for(Iterator<String> it = set.iterator(); it.hasNext(); ){
				String item = it.next();
				array[i] = Integer.parseInt(item);
				i++;
			}
			Arrays.sort(array);
			for(int j=0; j<array.length; j++){
				noDupSort[j] = String.valueOf(array[j]);
			}
		}
		
		return noDupSort;
	}
	
	public int sortDoubleDateUnit(int type, String item){
		int value = 0;
		int year = 0;
		switch(type){
		     case 0 : {
	    	    String[] itemData = item.split(" ");
				int quarter = Integer.parseInt(itemData[0].substring(1));
				year = Integer.parseInt(itemData[1]);
				value = quarter + year * 4;
				break;
		     }
		     case 1 : {
		    	String[] itemData = item.split("-");
				int month = Integer.parseInt(itemData[0]);
				year = Integer.parseInt(itemData[1]);
				value = month + year * 12;
				break;
		     }
		     case 2 : {
		    	String[] itemData = item.split(" ");
				int week = Integer.parseInt(itemData[0].substring(1));
				year = Integer.parseInt(itemData[1]);
				value = week + year * 53;
				break;
		     }
		}
		return value;
	}
	
	/**
	 * 划分where和having过滤条件过滤列(数字过滤列为having)
	 * @param filters
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getFilterSqls(JSONArray filters, String tabName, String gb) throws Exception{
		JSONArray whereFilter = new JSONArray();
		JSONArray havingFilter = new JSONArray();
		for(int i=0;i<filters.size();i++){
			JSONObject filter = filters.getJSONObject(i);
			String ft = filter.getString("fieldtype");
			String fx = filter.getString("fx");
			if(filter.containsKey("content")){
				if(ft.equals(Item.VALUE_TYPE_NUMBER) || fx.equals("count")){
					havingFilter.add(filter);
				}else{
					whereFilter.add(filter);
				}
			}
		}
		String where = ((OReportDAO) getDAO()).getWhereFilterSql(whereFilter);
		String having = ((OReportDAO) getDAO()).getHavingFilterSql(havingFilter);
		Map<String, String> filterSqls = new LinkedHashMap<String, String>();
		filterSqls.put("where", where);
		filterSqls.put("having", having);
		return filterSqls;
	}
	
	/**
	 * 字段名统一处理
	 * @param viewid
	 * @param xcolumn
	 * @param ycolumns
	 * @param filter
	 * @throws Exception
	 */
	public void handlerColumnName(String viewid, JSONObject xcolumn, JSONArray ycolumns, JSONArray filter) throws Exception{
		if(!xcolumn.isNullObject()){
			getDatabaseColumnName(viewid, xcolumn);
		}

		if(null!=ycolumns){
			for(int i=0;i<ycolumns.size();i++){
				JSONObject column = ycolumns.getJSONObject(i);
				getDatabaseColumnName(viewid, column);
			}
		}

		if(null!=filter){
			for(int i=0;i<filter.size();i++){
				JSONObject column = filter.getJSONObject(i);
				getDatabaseColumnName(viewid, column);
			}
		}
	}

	public String getFilterItems(String viewid, String domainid, JSONObject xcolumn, JSONArray ycolumns, JSONObject filter, WebUser user,ParamsTable params) throws Exception {
		
		//映射表单视图字段处理
		handlerColumnName(viewid, xcolumn, ycolumns, null);

		if(!filter.isNullObject()){
			getDatabaseColumnName(viewid, filter);
		}
		
		String ft = filter.getString("fieldtype");
		String fx = filter.getString("fx");
		String fxType = filter.getString("fxType");
		
		Map<String, String> goby = ((OReportDAO) getDAO()).getGroupAndOrderBy(xcolumn, ycolumns);
		String gb = goby.get("gb").toString();
		
		//表名处理
		String tabName = "select * from (" + getViewSQL(viewid, user,params) + ") otb where otb.DOMAINID='" + domainid + "'";
		
		List<Map<String, String>> itemsData = ((OReportDAO) getDAO()).getFilterItemsData(tabName, filter, gb);
		
		if(itemsData!=null && itemsData.size()>0){
			return getFilterItemsJson(itemsData, ft, fx, fxType);
		}else{
			JSONObject emptyJO = new JSONObject();
			JSONArray emptyJA = new JSONArray();
			emptyJO.accumulate("filter", emptyJA);
			return emptyJO.toString();
		}
	}
	
	/**
	 * 过滤器Items转JSON, 数字类型去除重复内容
	 * @param itemsData
	 * @param ft
	 * @param fx
	 * @return
	 */
	public String getFilterItemsJson(List<Map<String, String>> itemsData, String ft, String fx, String fxType){
		
		JSONObject filterItems = new JSONObject();
		JSONArray fjo = new JSONArray();
		
		if(ft.equals(Item.VALUE_TYPE_NUMBER) || fx.equals("count")){
			if(fxType.equals("range")){
				for(int i =0;i<itemsData.size();i++){
					Map<String, String> item = itemsData.get(i);
					String strItem = item.get("Item").toString();
					String strLabel = item.get("Label");
					JSONObject aitem = new JSONObject();
					aitem.accumulate("label", strItem);
					aitem.accumulate("data", strLabel);
					aitem.accumulate("selected", false);
					fjo.add(aitem);
				}
			}else{
				Set<String> data = new LinkedHashSet<String>();
				for(int i =0; i<itemsData.size(); i++){
					Map<String, String> item = itemsData.get(i);
					String strItem = item.get("Item").toString();
					data.add(strItem);
				}
				
				for(Iterator<String> it = data.iterator(); it.hasNext(); ){
					String strItem = it.next();
					JSONObject aitem = new JSONObject();
					aitem.accumulate("label", strItem);
					aitem.accumulate("data", strItem);
					aitem.accumulate("selected", false);
					fjo.add(aitem);
				}
			}

		}else{
			for(int i =0;i<itemsData.size();i++){
				Map<String, String> item = itemsData.get(i);
				String strItem = item.get("Item").toString();
				JSONObject aitem = new JSONObject();
				aitem.accumulate("label", strItem);
				aitem.accumulate("data", strItem);
				aitem.accumulate("selected", false);
				fjo.add(aitem);
			}
		}
		
		filterItems.accumulate("filter", fjo);
		
		return filterItems.toString();
	}

	/**
	 * 获取数据库列名
	 * @param viewid
	 * @param column
	 * @throws Exception
	 */
	public void getDatabaseColumnName(String viewid, JSONObject column) throws Exception{
		
		ViewProcess viewProcess=(ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		FormProcess formProcess=(FormProcess) ProcessFactory.createProcess(FormProcess.class);
		View view = (View)viewProcess.doView(viewid);
		Form form = (Form)formProcess.doView(view.getRelatedForm());
		
		if(null!=form && null!=column){
			String columnName = column.getString("value");
			if(!columnName.startsWith("$")){
				String temp = form.getTableMapping().getColumnName(columnName);
				if(null!=temp){
					column.remove("value");
					column.accumulate("value", temp.toUpperCase());
				}
			}else{
				column.remove("value");
				column.accumulate("value", columnName.substring(1).toUpperCase());
			}
		}
	}
	
	/**
	 * 获得视图的SQL
	 * 
	 * @param pk 视图ID
	 * @param user 用户
	 * @return 视图的SQL
	 * @throws Exception
	 */
	public String getViewSQL(String pk, WebUser user,ParamsTable params) throws Exception{
		
		ViewProcess vp = new ViewProcessBean();
		View view = (View)vp.doView(pk);
		String editMode = view.getEditMode();
		String sql = "";
		
		if(editMode.equals("01")){//DQL模式
			String dql = view.getEditModeType().getQueryString(params, user, new Document());
			sql = ((OReportDAO) getDAO()).dqlParseSql(dql);
		}else{
			sql = view.getEditModeType().getQueryString(params, user, new Document());
		}
		
		return sql;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public String getText(String applicationid, String sessionid, String pk, WebUser user, String columnName, String columnValue) throws Exception{
		ViewProcess vp = new ViewProcessBean();
		FormProcess fpb = new FormProcessBean();
		View view = (View)vp.doView(pk);
		String formid = view.getRelatedForm();
		Form form = (Form)fpb.doView(formid);
		
		IRunner jsrun = JavaScriptFactory.getInstance(sessionid, applicationid);
		FormField formField = form.findFieldByName(columnName);
		
		ParamsTable params=new ParamsTable();
		params.setParameter(columnName, columnValue);
		Document doc = form.createDocument(params, user);
		formField.getText(doc, jsrun, user);
		return null;
	}
	
	/**
	 * 获得指定列最大值
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int getMaxColumnValue(String sql) throws Exception{
		return ((OReportDAO) getDAO()).getMaxColumnValue(sql);
	}
}
