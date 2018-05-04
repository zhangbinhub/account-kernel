package OLink.bpm.core.report.standardreport.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.core.report.standardreport.ejb.StandarReportProcessBean;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcessBean;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

public class ReportUtil {

	public final static int limitedNum = 200;

	protected static String[] dbmethod = {"sum", "avg", "count", "max", "min"};
	
	/**
	 * get user name according to the corresponding use id
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static String getUserNameById(String userId) throws Exception {
		String userName = "";

		if (userId != null && !userId.trim().equals("")) {

			UserProcess upb =  (UserProcess) ProcessFactory.createProcess(
					UserProcess.class);
			
			UserVO user = (UserVO) upb.doView(userId);
			if (user != null)
				userName = user.getName();

		}

		return userName;
	}

	public static String getNodeStateLabel(String flowId, String nodeId)
			throws Exception {
		String status = "";
		BillDefiProcessBean bpb = new BillDefiProcessBean();
		BillDefiVO vo = (BillDefiVO) bpb.doView(flowId);

		if (vo != null) {
			Node node = (Node) (vo.toFlowDiagram().getElementByID(nodeId));
			status = node.statelabel;
		}

		return status;

	}

	public static String getTabContent(String application, String formId,
			String startdate, String enddate, String[] column, String methodIndex) throws Exception {
		int index = Integer.parseInt(methodIndex);
		StandarReportProcessBean spb = new StandarReportProcessBean(application);
		Collection<Map<String, String>> collection = spb.getSummaryReport(formId, startdate,
				enddate, column, dbmethod[index]);
		return getHtmlStr(collection, column, index);
	}

	public static int getRowsNum(String application, String formId,
			String startdate, String enddate, String[] column, String methodIndex) throws Exception {
		int index = Integer.parseInt(methodIndex);
		StandarReportProcessBean spb = new StandarReportProcessBean(application);
		int rowsNum = spb.getReportRowsNum(formId, startdate, enddate, column, dbmethod[index]);
		return rowsNum;
	}

	public static String getHtmlStr(Collection<Map<String, String>> collection, String[] column, int index)
			throws Exception {
		int colLength = column.length;
		int countLine = 1;
		float amountHours = 0;
		int totalRow = collection.size();
		StringBuffer rtnHtml = new StringBuffer();
		String previou[] = new String[colLength];
		rtnHtml.append("<table class=\"list-table\" id='list' border=1 style=\"border-color:black; width:95%;height:90%\"><tr>");

		for (int i = 0; i < column.length; i++) {
			rtnHtml.append("<td class=\"column-head\" style=\"border-color:black\">");
			rtnHtml.append(column[i].replaceAll("ITEM_", ""));
			rtnHtml.append("</td>");
		}
		String str = "USEDTIME";
		if(index==2){
			str = "AMOUNT";
		}
		rtnHtml.append("<td class=\"column-head\" style=\"border-color:black\">" + str + "</td>");
		rtnHtml.append("</tr>");

		for (Iterator<Map<String, String>> iterator = collection.iterator(); iterator.hasNext();) {
			rtnHtml.append("<tr class=\"table-text\" style=\"border-color:black\" >");
			Map<String, String> map = iterator.next();
			for (int j = 0; j < column.length; j++) {
				String temp = map.get(column[j].toUpperCase())==null?"":map.get(column[j].toUpperCase()).toString();
				String values = temp;
				if (countLine != 1 && temp != null && temp.equals(previou[j])) {
					if (totalRow == countLine)
						rtnHtml.append("<td style=\" border-top-width: 0px\">");
					else
						rtnHtml.append("<td style=\" border-bottom-width :0px;border-top-width: 0px\">");
					values = "";
				} else {
					if (totalRow == countLine)
						rtnHtml.append("<td >");
					else
						rtnHtml.append("<td style=\" border-bottom-width :0px\">");
				}

				previou[j] = temp;
				rtnHtml.append(values);
				rtnHtml.append("</td>");

			}

			rtnHtml.append("<td >");
			rtnHtml.append(map.get("USEDTIME"));
			rtnHtml.append("</td>");
			rtnHtml.append("</tr>");
			amountHours += new Float(map.get("USEDTIME")).floatValue();

			countLine++;
		}

		rtnHtml.append(getStatisticsHtml(column.length, amountHours, collection.size(), index));

		rtnHtml.append("</table>");
		return rtnHtml.toString();
	}
	
	public static String getStatisticsHtml(int columnLength, float amountHours, int size, int type){
		StringBuffer tableTr = new StringBuffer();
		tableTr.append("<tr><td colspan=" + (columnLength + 1) + " align=right class=commFont><strong>");
		switch(type){
		      case 0 :
		    	  tableTr.append("total of time consumption："  + amountHours + " Hours");
		    	  break;
		      case 1 :
		    	  tableTr.append("total of time average："  + amountHours/(float)size + " Hours");
		    	  break;
		      case 2 :
		    	  tableTr.append("total of quantity："  + amountHours);
		    	  break;
		      default :
		    	  tableTr.append("");
		    	  break;
		}
		tableTr.append("</strong></td></tr>");
		return tableTr.toString();
	}

	public String getSummaryReportChart(Map<String, String> params, HttpServletRequest request) throws Exception {
		
		//WebUser user = (WebUser)request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);

		String applicationid = params.get("applicationid");
		String formid = params.get("formid");
		String startdate = params.get("startdate");
		String enddate = params.get("startdate");
		String[] cols = params.get("col").split(";");
		int chartType = Integer.parseInt(params.get("ct"));
		int index = Integer.parseInt(params.get("dbmethod"));
		
		StandarReportProcessBean srpb = new StandarReportProcessBean(applicationid);  
		Collection<Map<String, String>> data = srpb.getSummaryReport(formid, startdate, enddate, cols, dbmethod[index]);

		String json = "";
		
		switch(chartType){
		  case 1 : json = OFC2ChartGenerator.getBarChart(data, cols); break;
		  case 2 : json = OFC2ChartGenerator.getLineChart(data, cols); break;
		  case 3 : json = OFC2ChartGenerator.getPieChart(data, cols); break;
		  default : json = OFC2ChartGenerator.getBarChart(data, cols); break;
		}

    	return json;
	}
}
