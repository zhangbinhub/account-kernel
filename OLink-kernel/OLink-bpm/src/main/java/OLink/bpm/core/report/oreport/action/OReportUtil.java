package OLink.bpm.core.report.oreport.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.jfreechart.*;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.report.oreport.ejb.OReportProcessBean;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.jfreechart.LineChart;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jfree.chart.JFreeChart;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.jfreechart.AreaChart;
import OLink.bpm.core.jfreechart.BarChart;
import OLink.bpm.core.jfreechart.ColumnChart;
import OLink.bpm.core.jfreechart.PieChart;
import OLink.bpm.core.report.oreport.ejb.OReportProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;

import eWAP.itext.text.pdf.AsianFontMapper;

public class OReportUtil {
	
	private final static String MNUMBER = "\"mfxarr\":[{\"label\":\"汇总\", \"data\":\"sum\"},{\"label\":\"最大值\", \"data\":\"max\"},{\"label\":\"最小值\", \"data\":\"min\"},{\"label\":\"平均数\", \"data\":\"avg\"},{\"label\":\"标准差\", \"data\":\"std\"},{\"label\":\"计数\", \"data\":\"count\"}]";
	private final static String MTEXT = "\"mfxarr\":[{\"label\":\"实际值\", \"data\":\"\"},{\"label\":\"计数\", \"data\":\"count\"}]";
	private final static String MDATE = "\"mfxarr\":[{\"label\":\"年\", \"data\":\"0\"},{\"label\":\"季度\", \"data\":\"1\"},{\"label\":\"季度&年\", \"data\":\"a0\"},{\"label\":\"月\", \"data\":\"2\"},{\"label\":\"月&年\", \"data\":\"a1\"},{\"label\":\"周\", \"data\":\"3\"},{\"label\":\"周&年\", \"data\":\"a2\"},{\"label\":\"工作日\", \"data\":\"4\"},{\"label\":\"全日期\", \"data\":\"date0\"},{\"label\":\"日\", \"data\":\"5\"},{\"label\":\"日期&时间\", \"data\":\"date1\"},{\"label\":\"小时\", \"data\":\"6\"}]";

	
	/**
	 * 获得用户
	 * @return
	 * @throws Exception
	 */
	protected WebUser getAnonymousUser() throws Exception {
		UserVO vo = new UserVO();

		vo.getId();
		vo.setName("GUEST");
		vo.setLoginno("guest");
		vo.setLoginpwd("");
		vo.setRoles(null);
		vo.setEmail("");

		return new WebUser(vo);
	}

	/**
	 * 获取软件下所有企业
	 * @param applicationid
	 * @return
	 */
	public String getDomain(String applicationid){
		StringBuffer sb = new StringBuffer();
		try{
			ApplicationProcess ap = (ApplicationProcess)ProcessFactory.createProcess(ApplicationProcess.class);
			ApplicationVO apvo = (ApplicationVO)ap.doView(applicationid);
			if(apvo!=null && apvo.getDomains()!=null && apvo.getDomains().size()>0){
				sb.append("{\"domain\":[");
				for (Iterator<DomainVO> itedvo = apvo.getDomains().iterator(); itedvo.hasNext();) {
					DomainVO dvo = itedvo.next();
					sb.append("{\"label\":\"").append(dvo.getName()).append("\",\"id\":\"").append(dvo.getId()).append("\"},");
				}
				if(sb.toString().indexOf(",")>-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("]}");
				return sb.toString();
			}else{
				return "{\"icon\":\"assets/warning.png\",\"message\":\"没有企业域\"}";
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 软件编号获得该软件下所有模块
	 * @param applicationid
	 * @return
	 * @throws Exception
	 */
	public String getModuleByApplictionid(String applicationid,String moduleid){
		StringBuffer sb = new StringBuffer();
		String temp = "";
		Collection<ModuleVO> datas;
		try{
			ModuleProcess moduleProcess=(ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
			if(moduleid != null && !moduleid.equals("") && !moduleid.equals("null")){
				datas = new ArrayList<ModuleVO>();
				ModuleVO moduleVO = (ModuleVO)moduleProcess.doView(moduleid);
				datas.add(moduleVO);
			}else{
				datas = moduleProcess.getModuleByApplication(applicationid);
			}
			if(datas.size()>0){
				sb.append("{\"module\":[");
				for (Iterator<ModuleVO> iterator = datas.iterator(); iterator.hasNext();) {
					ModuleVO moduleVO = iterator.next();
						if(temp.equals("")){
							temp = moduleVO.getId();
						}
						sb.append("{\"label\":\"").append(moduleVO.getName()).append("\",\"id\":\"").append(moduleVO.getId()).append("\"},");
				}
				
				if(sb.toString().indexOf(",")>-1&&!temp.equals("")){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("]");
				if(!temp.equals("")){
					String str = getViewCommon(temp,applicationid);
					if(!StringUtil.isBlank(str)){
						sb.append(","+str);
					}else{
						return "{\"icon\":\"assets/warning.png\",\"message\":\"没有视图\"}";
					}
				}
				sb.append("}");
			}
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	//视图公共部分
	protected String getViewCommon(String moduleid,String applicationid ){
		try{
			ViewProcess viewProcess=(ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			Collection<View> datas = viewProcess.getViewsByModule(moduleid, applicationid);
			if(datas.size()>0){
				StringBuffer sb = new StringBuffer();
				sb.append("\"view\":[");
				for (Iterator<View> iterator = datas.iterator(); iterator.hasNext();) {
					View view = iterator.next();
					sb.append("{\"label\":\"").append(view.getName()).append("\",");
					sb.append("\"editMode\":\"").append(view.getEditMode()).append("\",");
					sb.append("\"query\":\"").append(view.getEditModeType().getQueryString(new ParamsTable(), getAnonymousUser(), new Document())).append("\",");
					sb.append("\"id\":\"").append(view.getId()).append("\"},");
				}
				if(sb.toString().indexOf(",")>-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("]");
				return sb.toString();
			}else{
				return "";
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	/**
	 * 根据模块的编号获得该模块下所有的视图
	 * @param moduleid
	 * @return
	 * @throws Exception
	 */
	public String getViewByModuleid(String moduleid,String applicationid){
		StringBuffer sb = new StringBuffer();
		String str = getViewCommon(moduleid,applicationid);
		if(!StringUtil.isBlank(str)){
			sb.append("{");
			sb.append(str);
			sb.append("}");
		}
		return sb.toString();
	}
	

	/**
	 * 根据视图数组来获得列
	 * @param arr
	 * @return
	 * @throws Exception
	 */
	public String getViewColumnsByViewArray(String viewid) throws Exception{
		try{
			ViewProcess viewProcess=(ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			if(!StringUtil.isBlank(viewid)){
				View view = (View)viewProcess.doView(viewid);
				Collection<Column> valueFields = view.getColumns();
				if(valueFields.size()>0){
					StringBuffer sb = new StringBuffer();
					sb.append("{\"node\":[");
					for (Iterator<Column> iterator = valueFields.iterator(); iterator.hasNext();) {
						Column column = iterator.next();
						if (column != null && column.getType() != null && column.getType().equals("COLUMN_TYPE_FIELD") 
								&& column.getFormField() != null && column.getFormField().getFieldtype() != null 
								&& !column.getFormField().getFieldtype().equals("VALUE_TYPE_TEXT")) {
							sb.append("{");
							sb.append("\"label\":\"").append(column.getName()).append("\",");
							sb.append("\"value\":\"").append(column.getFieldName()).append("\",");
							sb.append("\"selected\":").append(false).append(",");
							sb.append("\"fieldType\":\"").append(column.getFormField().getFieldtype()).append("\",");
							if (column.getFormField().getFieldtype().equals("VALUE_TYPE_NUMBER")) {
								sb.append("\"icon\":\"assets/mxlist/number.png\",");
								sb.append(MNUMBER).append(",");
								sb.append("\"mfx\":\"sum\"");
							} else if (column.getFormField().getFieldtype().equals("VALUE_TYPE_DATE")) {
								sb.append("\"icon\":\"assets/mxlist/date.png\",");
								sb.append(MDATE).append(",");
								sb.append("\"mfx\":\"0\"");
							} else if (column.getFormField().getFieldtype().equals("VALUE_TYPE_VARCHAR")) {
								sb.append("\"icon\":\"assets/mxlist/text.png\",");
								sb.append(MTEXT).append(",");
								sb.append("\"mfx\":\"\"");
							}
							sb.append("},");
						}
					}
					if(sb.lastIndexOf(",")!=-1){
						sb.deleteCharAt(sb.lastIndexOf(","));
					}else{
						return "";
					}
					sb.append("]}");
					return sb.toString();
				}else{
					return "";
				}
			}else{
				return "";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}

	/**
	 * 根据json数组来获得点击生成报表返回数据
	 *
	 * @param json Flex返回的JSON字符串
	 * @return 处理后的图表JSON
	 * @throws Exception
	 */
	public static JFreeChart getCreateChar(String json,String chartType,ParamsTable params){
		//System.out.println("InfoJson\n"+json);
		try{
			JSONObject jo = JSONObject.fromObject(json);
			
			String applicationid = jo.getString("applicationid");
			String viewid = jo.getString("viewid");
			String viewLabel = jo.getString("viewLabel");
			String domainid = params.getParameterAsString("domainid");
			String userid = jo.getString("userid");
			JSONArray yColumns = null;
			JSONArray filters = null;
			
			JSONObject xColumn = jo.getJSONObject("xColumn");
			
			if(jo.containsKey("yColumn")){
				yColumns = jo.getJSONArray("yColumn");
			}
			
			if(jo.containsKey("fColumn")){
				filters = jo.getJSONArray("fColumn");
			}
			UserProcess up = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
			WebUser user = up.getWebUserInstance(userid);
			if(user==null){
				SuperUserProcess sup = (SuperUserProcess)ProcessFactory.createProcess(SuperUserProcess.class);
				user = sup.getWebUserInstance(userid);
			}
			OReportProcess op = new OReportProcessBean(applicationid);
			List<Map<String, String>> data = (List<Map<String, String>>)op.getCustomizeOReportData(viewid, domainid, xColumn, yColumns, filters, user,params);
			Map<String, Object> map = op.singleColumnHandle(xColumn, yColumns);
			JSONObject xCol = (JSONObject)map.get("xCol");
			JSONArray yCols = (JSONArray)map.get("yCols");
			List<String[]> hashSet = op.getNoDupContent(data,xCol, yCols);
			if(chartType.equals("LineChart")){
				return LineChart.createChart(viewLabel, xCol, yCols, data, hashSet);
			}else if(chartType.equals("BarChart")){
				return BarChart.createChart(viewLabel, xCol, yCols, data, hashSet);
			}else if(chartType.equals("AreaChart")){
				return AreaChart.createChart(viewLabel, xCol, yCols, data, hashSet);
			}else if(chartType.equals("ColumnChart")){
				return ColumnChart.createChart(viewLabel, xCol, yCols, data, hashSet);
			}else if(chartType.equals("PlotChart")){
				return PlotChart.createChart(viewLabel, xCol, yCols, data, hashSet);
			}else if(chartType.equals("PieChart")){
				return PieChart.createChart(viewLabel, xCol, yCols, data, hashSet);
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 将图表保存为pdf
	 * @param json
	 * @param chartType
	 * @return
	 */
	public String saveChartAsPDF(String json,String chartType,String filter,String domainid){
		try{
			JSONObject jo = JSONObject.fromObject(json);
			ParamsTable params = new ParamsTable();
			params.setParameter("domainid",domainid);
			if(filter !=null && !filter.equals("")&& !filter.equals("null")){
				JSONObject jsonObject = JSONObject.fromObject(filter);
				for(int i=0;i<jsonObject.names().size();i++){
					String key = jsonObject.names().getString(i);
					if(jsonObject.get(key)!=null && !jsonObject.get(key).equals("") && !jsonObject.get(key).equals("null")){
						params.setParameter(key,jsonObject.get(key));
					}
				}

			}
			JfreeChartUtil.saveChartAsPDF(jo.getString("viewLabel"),getCreateChar(json,chartType,params), 800, 300, new AsianFontMapper("STSong-Light","UniGB-UCS2-H"));
			return "{\"name\":\""+jo.getString("viewLabel")+"\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	/**
	 * 保存视图
	 * @param applicationid
	 * @param id
	 * @param name
	 * @param description
	 * @param type
	 * @param json
	 * @param operateJson
	 * @return
	 */
	public String doSaveViewVO(String applicationid,String moduleid,String id,String name,String description,String json,String userid){
		try{
			CrossReportVO crossReportVO = null;
			CrossReportProcess viewVOProcess=(CrossReportProcess) ProcessFactory.createProcess(CrossReportProcess.class);
			if(id==null || id.equals("")){
				crossReportVO = new CrossReportVO();
				id = UUID.randomUUID().toString();
				crossReportVO.setId(id);
				crossReportVO.setApplicationid(applicationid);
				crossReportVO.setModule(moduleid);
				crossReportVO.setName(name);
				crossReportVO.setNote(description);
				crossReportVO.setJson(json);
				crossReportVO.setType("CustomizeReport");
				if(userid !=null && !userid.equals("")){
					crossReportVO.setUserid(userid);
				}else{
					crossReportVO.setUserid("null");
				}
				viewVOProcess.doCreate(crossReportVO);
			}else{
				crossReportVO = (CrossReportVO)viewVOProcess.doView(id);
				if(name!=null&&!name.equals("")){
					crossReportVO.setName(name);
				}
				if(description!=null&&!description.equals("")){
					crossReportVO.setNote(description);
				}
				if(json!=null&&!json.equals("")){
					crossReportVO.setJson(json);
				}
				if(userid!=null&&!userid.equals("")){
					crossReportVO.setUserid(userid);
				}
				viewVOProcess.doUpdate(crossReportVO);
			}
			return "{\"icon\":\"assets/ok.png\",\"message\":\"报表 '"+crossReportVO.getName()+"' 保存成功\",\"viewVOid\":\""+id+"\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		
	}
	
	
	/**
	 * 获取所有视图
	 * @param applicationid
	 * @param flag
	 * @return
	 */
	public String getAllViewVO(String applicationid,String moduleid,String flag,String userid){
		try{
			CrossReportProcess viewVOProcess=(CrossReportProcess) ProcessFactory.createProcess(CrossReportProcess.class);
			return viewVOProcess.getAllCrossReportVO(applicationid,moduleid, flag,userid);
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	
	//获得创建图表的JSON
	public String getCreateCharJson(String id){
		try{
			CrossReportProcess viewVOProcess=(CrossReportProcess) ProcessFactory.createProcess(CrossReportProcess.class);
			return ((CrossReportVO)viewVOProcess.doView(id)).getJson();
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	/**
	 * 根据id获得视图
	 * @param id
	 * @return
	 */
	public String getViewVO(String id){
		try{
			CrossReportProcess viewVOProcess=(CrossReportProcess) ProcessFactory.createProcess(CrossReportProcess.class);
			return viewVOProcess.getCrossReportVO(id);
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	/**
	 * 删除视图
	 * @param id
	 * @return
	 */
	public String deleteViewVO(String id){
		try{
			CrossReportProcess viewVOProcess=(CrossReportProcess) ProcessFactory.createProcess(CrossReportProcess.class);
			CrossReportVO viewvo = (CrossReportVO)viewVOProcess.doView(id);
			viewVOProcess.doRemove(viewvo);
			return "{\"icon\":\"assets/ok.png\",\"message\":\"报表 '"+viewvo.getName()+"' 删除成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	/**
	 * 删除多个视图
	 * @param id
	 * @return
	 */
	public String deleteViewVO(String[] ids){
		try{
			CrossReportProcess viewVOProcess=(CrossReportProcess) ProcessFactory.createProcess(CrossReportProcess.class);
			for(int i =0;i<ids.length;i++){
				viewVOProcess.doRemove(ids[i]);
			}
			return "{\"icon\":\"assets/ok.png\",\"message\":\"报表删除成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
}
