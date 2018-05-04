package OLink.bpm.core.report.oreport.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.report.basereport.dao.AbstractReportDAO;
import OLink.bpm.util.Arith;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.document.dql.SQLFunction;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class AbstractOReportDAO extends AbstractReportDAO {

	protected SQLFunction sqlFuction;
	
	protected String applicationId;
	
	final static String _TBNAME = "T_DOCUMENT";
	
	protected static String[] singleFuncs = new String[7];
	protected static String[] doubleFuncs = new String[3];
	protected static String[] sDateFuncs = new String[2];
	
	protected static String[] doubleBys = new String[3];
	protected static String[] sDateBys = new String[2];
	
	protected static String[] itemLabelFuncs = new String[3];
	
	protected static String[] funcMath = new String[5];
	
	protected static String stdFunc = null;
	
	public static final int YEAR = 0;//年
	public static final int QUARTER = 1;//季度
	public static final int MONTH = 2;//月
	public static final int WEEK_OF_YEAR = 3;//周
	public static final int DAY_OF_WEEK = 4;//工作日
	public static final int DAY_OF_MONTH = 5;//日
	public static final int HOUR = 6;//小时
	public static final int QUARTER_AND_YEAR = 7;//季度&年
	public static final int MONTH_AND_YEAR = 8;//月&年
	public static final int WEEK_AND_YEAR = 9;//周&年
	public static final int FULLDATE = 10;//日期
	public static final int DATE_AND_TIME = 11;//日期&时间
	
	protected String[][] relativeGeneral = {
			new String[]{"11", "前1个小时", "-1"},
			new String[]{"11", "前6个小时", "-6"},
			new String[]{"11", "前12小时", "-12"},
			new String[]{"11", "前24小时", "-24"},
			new String[]{"10", "今天", "0"},
			new String[]{"10", "昨天", "-1"},
			new String[]{"3", "本周", "0"},
			new String[]{"10", "上7天", "-7"},
			new String[]{"2", "本月", "0"},
			new String[]{"10", "上30天", "-30"},
			new String[]{"1", "本季度", "0"},
			new String[]{"2", "上3个月", "-3"}
	};
	
	AbstractOReportDAO(Connection conn, String applicationId) throws Exception{
		super(conn);
		this.applicationId = applicationId;
	}

	/**
	 * 将获取数据保存为ArrayList
	 * 
	 * @param sql 执行的SQL
	 * @return ArrayList对象
	 * @throws Exception
	 */
	public Collection<Map<String, String>> getData(String sql, int max) throws Exception {
		PreparedStatement stat = null;
		ResultSet rs = null;
		Collection<Map<String, String>> datas = new ArrayList<Map<String, String>>();//将结果放入List
		try {
			stat = connection.prepareStatement(sql);
			//System.out.println("--->sql:"+sql);
			stat.setMaxRows(max);
			rs = stat.executeQuery();
			ResultSetMetaData metaData=rs.getMetaData();
			int columnCount=metaData.getColumnCount();
			while(rs.next()){
				Map<String, String> rowData = new LinkedHashMap<String, String>();//保存行
				for(int i=1;i<=columnCount;i++){
					if(rs.getObject(i) instanceof byte[]){
						String str = new String((byte[])rs.getObject(i));
						rowData.put(metaData.getColumnLabel(i), str);//保存列名和值
					}else{
						String value = "";
						if(rs.getObject(i)!=null) value = rs.getObject(i).toString();
						rowData.put(metaData.getColumnLabel(i), value);//保存列名和值
					}
				}
				datas.add(rowData);//将行内容放到List中。
			}
			return datas;

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(stat);
			PersistenceUtils.closeConnection(connection);
		}
	}
	
	/**
	 * 生成group by 和 order by语句
	 * @param xcolumn
	 * @param ycolumns
	 * @return
	 */
	public Map<String, String> getGroupAndOrderBy(JSONObject xcolumn, JSONArray ycolumns) {

		StringBuffer gb = new StringBuffer("group by ");
		StringBuffer ob = new StringBuffer("order by ");
		String xcolName = null;
		// String xft = null;
		if (!xcolumn.isNullObject()) {
			xcolName = xcolumn.getString("value");
			// xft = xcolumn.getString("fieldtype");

			gb.append(xcolName + ",\n");
			ob.append(xcolName + ",\n");
		}
		
		if (ycolumns != null) {
			for (int i = 0; i < ycolumns.size(); i++) {
				JSONObject ycol = ycolumns.getJSONObject(i);
				String ycolName = ycol.getString("value");
				String yft = ycol.getString("fieldtype");
				String yFx = ycol.getString("fx");
				String by = "";
				if (yft.equals(Item.VALUE_TYPE_DATE)) {
					// int index = Integer.parseInt(yFx);
					by = getBy(yFx, ycolName);
				} else {
					by = ycolName;
				}

				if (-1 == gb.indexOf(by) && !yft.equals(Item.VALUE_TYPE_NUMBER)) {
					if (gb.length() == 9 || ob.length() == 9) {
						//屏蔽
						//gb.append(by + ",\n");
						ob.append(by + ",\n");
					} else {
						//屏蔽
						//gb.append("         " + by + ",\n");
						ob.append("         " + by + ",\n");
					}
				}
			}
		}

		gb.deleteCharAt(gb.lastIndexOf(","));
		ob.delete(ob.lastIndexOf(","), ob.length());
		
		Map<String, String> gob = new LinkedHashMap<String, String>();
		gob.put("gb", gb.toString());
		gob.put("ob", ob.toString());
		return gob;
	}
	
	/**
	 * 单列处理
	 * 
	 * @param column JSONObject对象的列
	 * @param tabName 查询表名(为视图SQL)
	 * @return 拼凑出来的SQL语句
	 * @throws Exception
	 */
	public Map<String, String> getSingleAxisSqlAndBy(JSONObject column, String tabName) throws Exception{
		
		Map<String, String> map = null;
		
		String columnName = column.getString("value");
		String fieldType = column.getString("fieldtype");
		String xcolFx = column.getString("fx");
		
		if(fieldType.equals(Item.VALUE_TYPE_NUMBER)){//数字类型列
			map = getNumberTypeSqlAndBy(columnName, xcolFx, tabName);
		}else if(fieldType.equals(Item.VALUE_TYPE_DATE)){//日期类型列
			map = getDateTypeSqlAndBy(xcolFx, columnName);
		}else{//其他类型(文本)
			map = getTextTypeSqlAndBy(columnName);
		}
		
		return map;
	}
	
	/***
	 * 生成where过滤条件语句(除数字类型以外过滤列)
	 * @param filters
	 * @return
	 */
	public String getWhereFilterSql(JSONArray filters){
		
		StringBuffer where = new StringBuffer("where ");
		
		if (filters.size() > 0) {

			for (int i = 0; i < filters.size(); i++) {

				JSONObject filter = filters.getJSONObject(i);
				String filterName = filter.getString("value");
				String ft = filter.getString("fieldtype");
				String fx = filter.getString("fx");
				String rule = filter.getString("rule");
				JSONArray content = filter.getJSONArray("content");
				
				String strRule = "";
				if (rule.equals("contains")) {
					strRule = " in ";
				} else {
					strRule = " not in ";
				}

				if (ft.equals(Item.VALUE_TYPE_DATE)) {//日期、文本

					StringBuffer dateStr = new StringBuffer();
					String type = "";//, cycle, relative
					if(fx.equals("realValue")){
						//日期_实际值—分为:范围、其他(年、季度、月、周、日期、日期时间)
						if (type.equals("range")) {// 日期-范围
							//String df = getDateFunc(11, filterName);
							if (rule.equals("contains")) {
								strRule = " between ";
							} else {
								strRule = " not between ";
							}
							dateStr.append(filterName + strRule);
							dateStr.delete(dateStr.lastIndexOf("and") - 1, dateStr.length());
						} else {
							String df = getDateFunc(filter.getString("fxType"), filterName);
							where.append(df + strRule + getContentStr(content));
						}
					}else if(fx.equals("cycle")){//日期_周期
						String df = getDateFunc(filter.getString("fxType"), filterName);
						where.append(df + strRule + content);
					}else{//日期_相对
						where.append(getDateRelative(filter));
					}

				} else {// 文本
					where.append(filterName + strRule + getContentStr(content));
				}
				where.append("\n  and ");
			}
			where.delete(where.lastIndexOf(" and ") + 1, where.length());
			return where.toString().trim() + "\n";
		}else{
			return "";
		}
	}
	
	/**
	 * 生成having过滤条件语句(数字类型过滤列)
	 * @param filters
	 * @return
	 * @throws Exception 
	 */
	public String getHavingFilterSql(JSONArray filters){
		StringBuffer having = new StringBuffer("having ");
		if (filters.size() > 0) {
			for (int i = 0; i < filters.size(); i++) {
				JSONObject filter = filters.getJSONObject(i);
				String fx = filter.getString("fx");
				String fxType = filter.getString("fxType");
				String columnName = filter.getString("value");
				String rule = filter.getString("rule");
				JSONArray content = filter.getJSONArray("content");	
				
				if(fxType.equals("range")){
					String under = "";
					String above = "";
					String between = "";
					if (rule.equals("contains")) {
						under = "< ";
						above = "> ";
						between = "between ";
					} else {
						under = "> ";
						above = "< ";
						between = "not between ";
					}
					for(int j = 0; j<content.size(); j++){
						String item = content.getString(j);
						if(item.startsWith("under")){
							having.append(fx + "(" + columnName + ") " + under + item.substring(6) + " and ");
						}else if(item.startsWith("above")){
							having.append(fx + "(" + columnName + ") " + above + item.substring(6) + " and ");
						}else{
							String[] items = item.split(" to ");
							having.append(fx + "(" + columnName + ") " + between + items[0] + " and " + items[1] + " and ");
						}
					}
					
				//}else if(fxType.startsWith("beforeAfterN")){
				//	having.append(getBeforeAfterWhereClause(fx, fxType, columnName, rule, content, tabName, gb));
				}else{
					String strRule = "";
					if (rule.equals("contains")) {
						strRule = " in ";
					} else {
						strRule = " not in ";
					}
					having.append(fx + "(" + columnName + ")" + strRule + getContentStr(content) + " and ");
				}

			}
			if(having.lastIndexOf(" and ")!=-1){
				having.delete(having.lastIndexOf(" and "), having.length());
			}
			having.append("\n");
			return having.toString();
		} else {
			return "";
		}
	}
	
	/*public String getBeforeAfterWhereClause(String fx, String fxType, String columnName, String rule, JSONArray items, String tabName, String gb) throws Exception{
		List<Map<String, String>> data = (List<Map<String, String>>)getData(getNumberSingleValueSQL(fx, columnName, tabName, gb), 50000000);
		return null;
	}*/
	
	/**
	 * 获取过滤勾选项数据
	 * @param tabName
	 * @param filter
	 * @param gb
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, String>> getFilterItemsData(String tabName, JSONObject filter, String gb) throws Exception{

		String ft = filter.getString("fieldtype");
		String fx = filter.getString("fx");
	    String fxType = filter.getString("fxType");
		String columnName = filter.getString("value");

		if(ft.equals(Item.VALUE_TYPE_DATE)){//日期类型
			return (List<Map<String, String>>)getDateFilterItemsData(fxType, columnName, tabName, gb);
		}else if(ft.equals(Item.VALUE_TYPE_NUMBER) || fx.equals("count")){//数字类型
			return (List<Map<String, String>>)getNumberFilterItemsData(fxType, fx, columnName, tabName, gb);
		}else{//文本类型
			return (List<Map<String, String>>)getTextFilterItemsData(columnName, tabName, gb);
		}
	}

	/**
	 * 构造日期类型过滤器勾选项
	 * @param fxType
	 * @param columnName
	 * @param tabName
	 * @param gb
	 * @return
	 * @throws Exception
	 */
	public Collection<Map<String, String>> getDateFilterItemsData(String fxType, String columnName, String tabName, String gb) throws Exception{
		
		StringBuffer sql = new StringBuffer("select ");
		sql.append("distinct " + getDateFunc(fxType, columnName) + " Item\n");
		sql.append("from (").append(tabName).append(") table_0\n");
		sql.append(gb);
		sql.append("order by " + getBy(fxType, columnName));
		
		return getData(sql.toString(), 50000000);
	}
	
	
	/**
	 * 构造数字类型过滤器勾选项
	 * @param fxType
	 * @param fx
	 * @param columnName
	 * @param tabName
	 * @param gb
	 * @return
	 * @throws Exception
	 */
	public Collection<Map<String, String>> getNumberFilterItemsData(String fxType, String fx, String columnName, String tabName, String gb) throws Exception{
		
		if(fxType.equals("singleValue")){//单个值
			return getData(getNumberSingleValueSQL(fx, columnName, tabName, gb), 50000000);
		}else if(fxType.equals("range")){
			return getNumberRangeValueData(fx, columnName, tabName, gb);
		}
		return null;
	}
	
	/**
	 * 数字类型过滤器范围勾选项处理
	 * @param fx
	 * @param columnName
	 * @param tabName
	 * @param gb
	 * @return
	 * @throws Exception
	 */
	public Collection<Map<String, String>> getNumberRangeValueData(String fx, String columnName, String tabName, String gb)
			throws Exception {
		List<Map<String, String>> itemsData = (List<Map<String, String>>) getData(getNumberSingleValueSQL(fx, columnName, tabName, gb), 50000000);
		Double maxValue = null;
		Double minValue = null;
		for (int i = 0; i < itemsData.size(); i++) {
			Map<String, String> rowData = itemsData.get(i);
			for (Map.Entry<String, String> entry : rowData.entrySet()) {
				double temp = Double.parseDouble(entry.getValue().toString());
				if (maxValue == null || minValue == null) {
					maxValue = temp;
					minValue = temp;
				} else {
					if (temp > maxValue) {
						maxValue = temp;
					}
					if (temp < minValue) {
						minValue = temp;
					}
				}
			}
		}
		
		double dResult = Arith.div(Arith.sub(maxValue, minValue), 10D);
		
		if(dResult>1){
			int max = (int)Math.ceil(maxValue);
			int min = (int)Math.ceil(minValue);
			int result = (int)Math.ceil((max - min) / 10D);
			itemsData = constructNumberRangeItems(max, min, result);
		}else if(0.1<=dResult && dResult<1){
			double result = Arith.div(Math.ceil(Arith.mul(dResult, 10D)), 10D);
			if(result>0.95D){
				result = 1D;
			}
			itemsData = constructNumberRangeItems(maxValue, minValue, result);
		}else{
			itemsData = new ArrayList<Map<String, String>>();
			double result = Arith.sub(Arith.sub(maxValue, minValue), 4D);
			Map<String, String> rowDataUnder = new HashMap<String, String>();
			rowDataUnder.put("Item", Arith.add(minValue, result) + "以下");
			rowDataUnder.put("Label", "under " + Arith.add(minValue, result));
			Map<String, String> rowDataAbove = new HashMap<String, String>();
			rowDataAbove.put("Item", Arith.add(minValue, Arith.mul(result, 3D)) + "以上");
			rowDataAbove.put("Label", "above " + Arith.add(minValue, Arith.mul(result, 3D)));
			itemsData.add(rowDataUnder);
			itemsData.add(rowDataAbove);
		}
		
		return itemsData;
	}
	
	/**
	 * 数字类型过滤器范围勾选项构造
	 * @param maxValue 最大值
	 * @param minValue 最小值
	 * @param interval 范围
	 * @return
	 */
	public List<Map<String, String>> constructNumberRangeItems(double maxValue, double minValue, double interval){
		
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		double minTemp = minValue;

		if(maxValue>0 && minValue<0){

			while(Arith.div(Math.abs(minTemp), interval)>1){
				Map<String, String> newRowData = new HashMap<String, String>();
				double tmp = minTemp;
				minTemp = Arith.add(minTemp, interval);
				if(tmp==minValue){
					newRowData.put("Item", minTemp + "以下");
					newRowData.put("Label", "under " + minTemp);
					
				}else{
					newRowData.put("Item", tmp + " to " + minTemp);
					newRowData.put("Label", tmp + " to " + minTemp);
				}
				data.add(newRowData);
			}
			
			double middle = Arith.add(minTemp, interval);
			
			if(middle>=maxValue){//??
				Map<String, String> newRowData = new HashMap<String, String>();
				newRowData.put("Item", minValue + "以上");
				newRowData.put("Label", "above " + minValue);
				data.add(newRowData);
			}else{
				Map<String, String> middleRowData = new HashMap<String, String>();
				middleRowData.put("Item", minTemp + " to " + middle);
				middleRowData.put("Label", minTemp + " to " + middle);
				data.add(middleRowData);
				
				while(middle<maxValue){
					Map<String, String> newRowData = new HashMap<String, String>();
					double tmp = middle;
					middle = Arith.add(middle, interval);
					if(middle==maxValue){
						newRowData.put("Item", tmp + "以上");
						newRowData.put("Label", "above " + tmp);
					}else{
						newRowData.put("Item", minValue + " to " + middle);
						newRowData.put("Label", minValue + " to " + middle);
					}
					data.add(newRowData);

				}
			}

		}else{
			while(minTemp<maxValue){
				Map<String, String> newRowData = new HashMap<String, String>();
				double tmp = minTemp;
				minTemp = Arith.add(minTemp, interval);
				if(tmp == minValue){
					newRowData.put("Item", minTemp + "以下");
					newRowData.put("Label", "under " + minTemp);
				}else if(tmp >= (Arith.sub(maxValue, interval))){
					newRowData.put("Item", tmp + "以上");
					newRowData.put("Label", "above " + tmp);
				}else{
					newRowData.put("Item", tmp + " to " + minTemp);
					newRowData.put("Label", tmp + " to " + minTemp);
				}
				data.add(newRowData);
			}
		}
		return data;
	}
	
	public List<Map<String, String>> constructNumberRangeItems(int maxValue, int minValue, int interval){
		
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		int minTemp = minValue;
		
		if(maxValue>0 && minValue<0){

			while(Arith.div(Math.abs(minTemp), interval)>1){
				Map<String, String> newRowData = new HashMap<String, String>();
				int tmp = minTemp;
				minTemp = minTemp + interval;
				if(tmp==minValue){
					newRowData.put("Item", minTemp + "以下");
					newRowData.put("Label", "under " + minTemp);
					
				}else{
					newRowData.put("Item", tmp + " to " + minTemp);
					newRowData.put("Label", tmp + " to " + minTemp);
				}
				data.add(newRowData);
			}
			
			int middle = minTemp + interval;
			
			if(middle>=maxValue){//??
				Map<String, String> newRowData = new HashMap<String, String>();
				newRowData.put("Item", minTemp + "以上");
				newRowData.put("Label", "above " + minTemp);
				data.add(newRowData);
			}else{
				Map<String, String> middleRowData = new HashMap<String, String>();
				middleRowData.put("Item", minTemp + " to " + middle);
				middleRowData.put("Label", minTemp + " to " + middle);
				data.add(middleRowData);
				
				while(middle<maxValue){
					Map<String, String> newRowData = new HashMap<String, String>();
					int tmp = middle;
					middle = middle + interval;
					if(middle>=maxValue){
						newRowData.put("Item", tmp + "以上");
						newRowData.put("Label", "above " + tmp);
					}else{
						newRowData.put("Item", minTemp + " to " + middle);
						newRowData.put("Label", minTemp + " to " + middle);
					}
					data.add(newRowData);

				}
			}

		}else{
			while(minTemp<maxValue){
				Map<String, String> newRowData = new HashMap<String, String>();
				int tmp = minTemp;
				minTemp = minTemp + interval;
				if(tmp == minValue){
					newRowData.put("Item", minTemp + "以下");
					newRowData.put("Label", "under " + minTemp);
				}else if(tmp >= (maxValue - interval)){
					newRowData.put("Item", tmp + "以上");
					newRowData.put("Label", "above " + tmp);
				}else{
					newRowData.put("Item", tmp + " to " + minTemp);
					newRowData.put("Label", tmp + " to " + minTemp);
				}
				data.add(newRowData);
			}
		}
		return data;
	}
	public String getNumberSingleValueSQL(String fx, String columnName, String tabName, String gb){
		if(fx.equals("realValue")){
			fx = "";
			gb = "";
		}else if(fx.equals("std")){
			fx = stdFunc;
		}
		String method = fx + "(" + columnName + ")";
		StringBuffer sql = new StringBuffer("select ");
		sql.append(method + " Item\n");
		sql.append("from (").append(tabName).append(") table_0\n");
		sql.append(gb);
		sql.append("order by " + method);
		
		return sql.toString();
	}
	
	public Collection<Map<String, String>> getTextFilterItemsData(String columnName, String tabName, String gb) throws Exception{
		
		StringBuffer sql = new StringBuffer("select ");
		sql.append("distinct " + columnName + " Item\n");
		sql.append("from (").append(tabName).append(") table_0\n");
		sql.append("order by " + columnName);
		
		return getData(sql.toString(), 50000000);
	}
	
	/**
	 * 日期类型过滤条件相对项处理方法
	 * @param filter
	 * @return
	 */
	public String getDateRelative(JSONObject filter){
		String fxType = filter.getString("fxType");
		String columnName = filter.getString("value");
		JSONArray content = filter.getJSONArray("content");
		StringBuffer condition = new StringBuffer();
	    for(int i=0; i<content.size(); i++){
	    	String value = content.getString(i);
			if(fxType.equals("general")){
				for(String[] item : relativeGeneral){
					if(value.equals(item[1])){
						//String dateFunc = getDateFunc(Integer.parseInt(item[0]), columnName);
						condition.append(getRelativeItemPartSQL(item, columnName) + "\n  and ");
					}
				}
			}else{
				String[] item = {fxType, "", value};
				condition.append(getRelativeItemPartSQL(item, columnName) + "\n  and ");
			}
		}
	    condition.delete(condition.lastIndexOf(" and "), condition.length());
		return condition.toString();
	}
	
	/***
	 * 获取一个相对项的where过滤条件
	 * @param item
	 * @param columnName
	 * @return
	 */
	public String getRelativeItemPartSQL(String[] item, String columnName){
		int type = Integer.parseInt(item[0]);
		int diff = Integer.parseInt(item[2]);
		StringBuffer fp = new StringBuffer();
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int week = c.get(Calendar.WEEK_OF_YEAR);
		int nowCount = 0;
		int mathCount = 0;
		String nowDate = null;
		String mathDate = null;
		int funMathType = 0;
		SimpleDateFormat date_iso = new SimpleDateFormat("yyyy-MM-dd");
		//if(diff==0){
			
		//}else{
			if(type == QUARTER){//季度
				int quarter = (int)Math.ceil(month / 3D);
				nowCount = year * 4 + quarter;
				mathCount = nowCount + diff;
				funMathType = 0;
			}else if(type == MONTH){//月
				nowCount = year * 12 + month;
				mathCount = nowCount + diff;
				funMathType = 1;
			}else if(type == WEEK_OF_YEAR){//周
				nowCount = year * 52 + week;
				mathCount = nowCount + diff;
				funMathType = 2;
			}else if(type == FULLDATE){//日期
				long math = now.getTime() + ((long)diff * 24 * 3600 * 1000);
				nowDate = date_iso.format(now);
				mathDate = date_iso.format(new Date(math));
				funMathType = 3;
			}else if(type == DATE_AND_TIME){//日期时间
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String timeStr = date_iso.format(now);
				int hour = c.get(Calendar.HOUR_OF_DAY);
				timeStr = timeStr + " " + hour + ":00:00";
				Date nowDatetime = null;
				try{
					nowDatetime = sdf.parse(timeStr);
					long math = nowDatetime.getTime() + ((long)diff * 3600 * 1000);
					nowDate = sdf.format(nowDatetime);
					mathDate = sdf.format(new Date(math));
					funMathType = 4;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			if(diff<0){
				fp.append(getFuncMath(funMathType, mathCount, nowCount, columnName, mathDate, nowDate));
			}else{
				fp.append(getFuncMath(funMathType, nowCount, mathCount, columnName, nowDate, mathDate));
			}

		//}

		return fp.toString();
	}
	
	/**
	 * 将content改为SQL格式
	 * @param content
	 * @return
	 */
	public String getContentStr(JSONArray items){
		StringBuffer content = new StringBuffer();
		content.append("(");
		for(int i=0; i<items.size(); i++){
			content.append("'" + items.getString(i) + "', ");
		}
		content.delete(content.lastIndexOf(", "), content.length());
		content.append(")");
		return content.toString();
	}
	
	/**
	 * 多列报表select部分
	 * 
	 * @param xcolumn
	 * @param ycolumns
	 * @param tabName
	 * @return
	 */
	public String getMultipleAxisesSql(JSONObject xcolumn, JSONArray ycolumns, String tabName) {

		StringBuffer sql = new StringBuffer("select ");

		// x轴信息
		String xAxis = xcolumn.getString("value");
		String xFieldType = xcolumn.getString("fieldtype");
		String xFx = xcolumn.getString("fx");
		if (xFx.equals("std")) {
			xFx = stdFunc;
		}

		String xfunc = null;
		if (xFieldType.equals(Item.VALUE_TYPE_DATE)) {
			xfunc = getDateFunc(xFx, xAxis);
		} else {
			xfunc = xFx + "(" + xAxis + ")";
		}

		sql.append(xfunc + " xAxis,\n");

		for (int i = 0; i < ycolumns.size(); i++) {
			JSONObject ycolumn = ycolumns.getJSONObject(i);
			String yAxis = ycolumn.getString("value");
			String yFieldType = ycolumn.getString("fieldtype");
			String yFx = ycolumn.getString("fx");
			if (yFx.equals("std")) {
				yFx = stdFunc;
			}

			String yfunc = null;
			if (yFieldType.equals(Item.VALUE_TYPE_DATE)) {
				yfunc = getDateFunc(yFx, yAxis);
			} else {
				yfunc = yFx + "(" + yAxis + ")";
			}

			sql.append("       " + yfunc + " yAxis" + i + ",\n");
		}

		sql.deleteCharAt(sql.lastIndexOf(","));

		return sql.toString();
	}
	
	/**
	 * 生成单列文本类型SQL语句和排序字段
	 * 
	 * @param columnName 列名
	 * @return 文本类型对应的SQL语句
	 */
	public Map<String, String> getTextTypeSqlAndBy(String columnName){
		String method = "count(" + columnName + ")";
		String sql = "select " + columnName + " xAxis, " + method +" yAxis0\n";
		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", sql);
		map.put("by", columnName);
		return map;
	}
	
	/**
	 * 生成单列数字类型SQL语句和排序
	 * 
	 * @param xyAxis 列名
	 * @param tabName 表名(视图SQL)
	 * @param xcolFx 列方法
	 * @return 数字类型对应SQL语句 
	 * @throws Exception
	 */
	public Map<String, String> getNumberTypeSqlAndBy(String xyAxis, String xcolFx, String tabName) throws Exception{

		String maxsql = "select max(" + xyAxis + ") MAXXY from (" + tabName + ") table_0";
		ArrayList<Map<String, String>> maxData = (ArrayList<Map<String, String>>)getData(maxsql, 1);
		Map<String, String> max = maxData.get(0);
		double maxType = Double.parseDouble(max.get("MAXXY").toString());
		
		Map<String, Number> returnValue = getNumberRound(maxType);
		//int length = (Integer)returnValue.get("time");
		double interval = (Double)returnValue.get("interval");
		String method = xcolFx + "(" + xyAxis + ")";
		
		return getCeilFuncSqlAndBy(xyAxis, interval, method);
	}
	
	/**
	 * 生成单列日期类型SQL语句
	 * 
	 * @param dateType 日期类型
	 * @param columnName 列名
	 * @return 日期类型对应SQL
	 */
	public Map<String, String> getDateTypeSqlAndBy(String dateType, String columnName){
		
		String method = "count(" + columnName + ")";
		
		StringBuffer strSQL = new StringBuffer("select ");
		
		strSQL.append(getDateFunc(dateType, columnName) + " xAxis,\n");
		strSQL.append("       " + method + " yAxis0\n");

		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("sql", strSQL.toString());
		map.put("by", getBy(dateType, columnName));
		
		return map;
	}
	
	/**
	 * 范围取整并获取间隔。
	 * 用数据库最大值前两位数运算，如果为42则换算为45，如果为46则换算为50，如果为67则换算为100
	 * 然后通过除以5，生成列数。
	 * 以为无法得知zoho单数字类型列生成规则，所以暂时这样实现
	 * 
	 * @param inum 最大值或取整的数
	 * @return
	 */
	public Map<String, Number> getNumberRound(double inum){

		Map<String, Number> returnValue = new HashMap<String, Number>();
		
		double interval = 0;
		
		if(Math.abs(inum)<10){
			interval = Math.abs(inum) / 5D;
			returnValue.put("time", 5);
		}else{
			DecimalFormat df = new DecimalFormat("0.0");
			String strNum = df.format(Math.abs(inum));
			String roundPart = strNum.substring(0, strNum.indexOf("."));

			int firstLetter = Integer.parseInt(strNum.substring(0, 1));
			int secLetter = Integer.parseInt(strNum.substring(1, 2));

			if(firstLetter > 5){
				firstLetter = 10;
				secLetter = 0;
			}else{
				if(secLetter > 5){
					firstLetter = firstLetter + 1;
					secLetter = 0;
				}else{
					secLetter = 5;
				}
			}
		
			String headTwo = firstLetter + "" + secLetter;
			
			if(roundPart.length()>2){
				roundPart = headTwo + roundPart.substring(2, roundPart.length());
			}else{
				roundPart = headTwo;
			}
			
			StringBuffer snum = new StringBuffer("5");
			for(int i=1; i<roundPart.length()-1; i++){
				snum.append("0");
			}

			int result = Integer.parseInt(roundPart) / Integer.parseInt(snum.toString());
			returnValue.put("time", result);
			interval = Integer.parseInt(roundPart) / (double)result;
			
			if(inum<0){
				interval = 0 - interval;
			}

		}
		returnValue.put("interval", interval);
		return returnValue;
	}
	
	/**
	 * 获取对应的ceil函数SQL和排序
	 * 
	 * @param column 列名
	 * @param interval 间隔
	 * @param method 统计方法
	 * @param tabName 表名
	 * @return 对应数据库的ceil函数SQL
	 */
	public abstract Map<String, String> getCeilFuncSqlAndBy(String column, double interval, String method);
	
	/**
	 * 获取日期函数与分组、排序函数
	 * 
	 * @param dateType 日期类型
	 * @param column 列名
	 * @return 对应数据库的日期函数
	 */
	public Map<String, String> getDateFuncAndBy(String dateType, String column) {
		
		Map<String, String> FuncAndBy = new LinkedHashMap<String, String>();

		FuncAndBy.put("func", getDateFunc(dateType, column));
		FuncAndBy.put("by", getBy(dateType, column));
		
		return FuncAndBy;
	}
	
	/**
	 * 替换数据库方法中的列和值
	 * @param type
	 * @param nowCount
	 * @param mathCount
	 * @param columnName
	 * @param earlyDate
	 * @param lateDate
	 * @return
	 */
	public String getFuncMath(int type, int nowCount, int mathCount, String columnName, String earlyDate, String lateDate){
		String dtSql = funcMath[type];
		dtSql = dtSql.replace("column", columnName);
		if(earlyDate != null && lateDate != null){
			dtSql = dtSql.replace("earlyDate", earlyDate);
			dtSql = dtSql.replace("laterDate", lateDate);
		}else{
			//dtSql = dtSql.replace("column", columnName);
			dtSql = dtSql.replace("smallCount", String.valueOf(nowCount));
			dtSql = dtSql.replace("bigCount", String.valueOf(mathCount));
			
		}
		return dtSql;
	}
	
	/**
	 * 获取日期函数
	 * 
	 * @param index 日期函数数组索引
	 * @param column 列名
	 * @return 对应数据库日期函数
	 */
	public String getDateFunc(String fx, String column) {
		if(fx.startsWith("a")){//两个日期单位
			int index = Integer.parseInt(fx.substring(1));
			return doubleFuncs[index].replace("column", column);
		}else if(fx.startsWith("date")){//全日期与日期&时间
			int index = Integer.parseInt(fx.substring(4));
			return sDateFuncs[index].replace("column", column);
		}else if(fx.startsWith("i")){//过滤器项
			int index = Integer.parseInt(fx.substring(1));
			return itemLabelFuncs[index].replace("column", column);
		}else{//单个日期单位
			int index = Integer.parseInt(fx);
			return singleFuncs[index].replace("column", column);
		}
	}
	
	/**
	 * 获取日期函数与分组排序函数
	 * 
	 * @param index 分组、排序函数索引
	 * @param column 列名
	 * @return 对应数据库by子句函数
	 */
	public String getBy(String fx, String column) {
		if(fx.startsWith("a") || fx.startsWith("i")){
			int index = Integer.parseInt(fx.substring(1));
			return doubleBys[index].replace("column", column);
		}else if(fx.startsWith("date")){
			int index = Integer.parseInt(fx.substring(4));
			return sDateBys[index].replace("column", column);
		}else{
			int index = Integer.parseInt(fx);
			return singleFuncs[index].replace("column", column);
		}
		
	}
	
	/**
	 * 将视图的DQL转换为SQL
	 * 
	 * @param dql 视图的DQL
	 * @return 视图的SQL
	 * @throws Exception
	 */
	public String dqlParseSql(String dql) throws Exception{
		String formName = parseDQLFormName(dql);

		TableMapping tableMapping = getTableMapping(formName);

		String dqlWhere = parseDQLWhere(dql);

		String where = "";
		if (dqlWhere != null && dqlWhere.trim().length() > 0) {
			where = DQLASTUtil.parseToHQL(dqlWhere, tableMapping, sqlFuction);
		}

		String sql = "SELECT " + getSelectPart(tableMapping) + " FROM " + getInnerJoinPart(tableMapping);
		sql += " WHERE d.ISTMP=0";

		if (!StringUtil.isBlank(where)) {
			sql += " AND " + where;
		}
		return sql;
	}
	
	/**
	 * 根据DQL,返回表单名
	 * 
	 * @param dql
	 * @return 表单名
	 */
	public String parseDQLFormName(String dql) {

		int pos = dql.toUpperCase().indexOf("$FORMNAME");
		dql = dql.substring(pos + 9);

		int pos2 = dql.toUpperCase().indexOf(" AND");
		String formName = "";
		if (pos2 > 0) {
			formName = dql.substring(1, pos2).trim();
		} else {
			formName = dql.substring(1);
		}

		// String formName = dql.trim().substring(1, pos2 - 1).trim();

		formName = formName.replaceAll("\\(", "");
		formName = formName.replaceAll("\\)", "");
		formName = formName.replaceAll("\\'", "");

		return getFormShortName(formName);
	}
	
	private String getFormShortName(String formName) {
		String rtn = formName;
		if (formName.indexOf("=") != -1) {
			rtn = rtn.substring(formName.indexOf("=") + 1).trim();
		}
		return rtn.substring((rtn.lastIndexOf("/") + 1), rtn.length()).trim();
	}
	
	/**
	 * 获取表格映射关系
	 * 
	 * @param formName
	 *            表单全名
	 * @param applicationId
	 *            应用ID
	 * @return 表格映射
	 * @throws Exception
	 */
	protected TableMapping getTableMapping(String formName) throws Exception {
		String shortName = getFormShortName(formName);

		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Form form = formProcess.doViewByFormName(shortName, applicationId);
		if (form == null) {
			throw new Exception("Form: " + formName + " does not exist");
		}

		TableMapping mapping = form.getTableMapping();
		return mapping;
	}
	
	/**
	 * 返回dql语句 条件. dql语句的详细说明参考以上文档头.
	 * 
	 * @param dql
	 * @return dql 条件
	 */
	public String parseDQLWhere(String dql) {
		int pos = dql.toUpperCase().indexOf("$FORMNAME");
		dql = dql.substring(pos + 9);

		int pos2 = dql.toUpperCase().indexOf(" AND");
		if (pos2 > 0) {
			return dql.substring(pos2 + 4).trim();
		} else {
			return "";
		}
		// return dql.trim().substring(pos2 + 3);
	}
	
	String getSelectPart(TableMapping tableMapping) {
		return getSelectPart(tableMapping, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 获取SQL查询内容部分
	 * 
	 * @param tableMapping
	 * @param tableType
	 * @return
	 */
	String getSelectPart(TableMapping tableMapping, int tableType) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += "d.*";
		} else {
			sql += "d.*," + tableMapping.getColumnListString();
		}

		return sql;
	}
	
	/**
	 * SQL关联部分
	 * 
	 * @param tableMapping
	 *            关系映射
	 * @return
	 */
	String getInnerJoinPart(TableMapping tableMapping) {
		return getInnerJoinPart(tableMapping, DQLASTUtil.TABEL_TYPE_CONTENT);
	}

	/**
	 * 获取SQL关联部分
	 * 
	 * @param tableMapping
	 *            表关系映射
	 * @param tableType
	 *            表类型
	 * @return
	 */
	String getInnerJoinPart(TableMapping tableMapping, int tableType) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += getFullTableName(tableMapping.getTableName(tableType)) + " d";
		} else {
			sql = getFullTableName(_TBNAME) + " d";
			sql += " INNER JOIN " + getFullTableName(tableMapping.getTableName(tableType)) + " m";
			sql += " ON d.MAPPINGID=m." + tableMapping.getPrimaryKeyName();
		}

		return sql;
	}
	
	/**
	 * 获得指定列最大值
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int getMaxColumnValue(String sql) throws Exception{
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			//log.info(dbType + sql);
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}else{
				return 0;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
			PersistenceUtils.closeConnection(connection);
		}
	}
	
}