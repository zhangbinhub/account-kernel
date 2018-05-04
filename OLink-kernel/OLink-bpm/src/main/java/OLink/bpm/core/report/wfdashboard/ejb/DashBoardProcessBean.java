package OLink.bpm.core.report.wfdashboard.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.report.wfdashboard.action.Colors;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.core.workflow.storage.definition.action.BillDefiHelper;
import jofc2.model.Chart;
import jofc2.model.axis.XAxis;
import jofc2.model.axis.YAxis;
import jofc2.model.elements.BarChart;
import jofc2.model.elements.HorizontalBarChart;
import jofc2.model.elements.PieChart;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.report.wfdashboard.dao.WFDashBoardDAO;

public class DashBoardProcessBean extends AbstractRunTimeProcessBean<DashBoardVO> implements DashBoardProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1074601389103677754L;

	public DashBoardProcessBean(String applicationId) {
		super(applicationId);

	}

	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().getDashBoardDAO(getConnection(),getApplicationId());
	}

	public String getSumWFChartStr(String domainid) throws Exception {
		DataPackage<DashBoardVO> dpg = new DataPackage<DashBoardVO>();
		dpg.setDatas(((WFDashBoardDAO)getDAO()).getSumWfData(getApplicationId(), domainid));
		return createSumWFChart(dpg);
	}

	public String getSumStateLabelChartStr(String domainid, String flowid)throws Exception {
		DataPackage<DashBoardVO> dpg = new DataPackage<DashBoardVO>();
		dpg.setDatas(((WFDashBoardDAO)getDAO()).getSumStableLabelData(getApplicationId(), domainid,flowid));
		return createSumStateLabelChart(dpg);
	}

	public DataPackage<DashBoardVO> getSumRole(String domainid, String flowid,int curPage)
			throws Exception {
		//String applicationid = getApplicationId();		
		return ((WFDashBoardDAO)getDAO()).getSumRole(getApplicationId(), domainid,flowid,curPage);
	}

	public String getSumTimeChartStr(String domainid, String flowid)
			throws Exception {
		DataPackage<DashBoardVO> dpg = new DataPackage<DashBoardVO>();
		dpg.setDatas(((WFDashBoardDAO)getDAO()).getSumTimeData(getApplicationId(), domainid,flowid));
		return createSumTimeChart(dpg);
	}
	
	public String createSumTimeChart(DataPackage<DashBoardVO> dpg){
		Chart chart = new Chart();
		int max = 0;
		//int i = 1;
		BarChart bar = new BarChart(BarChart.Style.GLASS);
		YAxis y_axis = new YAxis();
		XAxis x_axis = new XAxis();
		//Colors color = new Colors();

		for (Iterator<DashBoardVO> iterator = dpg.datas.iterator(); iterator.hasNext();) {
			DashBoardVO vo = iterator.next();
			bar.addValues(vo.getValue());

			x_axis = x_axis.addLabels(vo.getName());

			if (max < vo.getValue().intValue())
				max = vo.getValue().intValue() + 5;
		}
		bar.setColour("#ff00ff");
		y_axis.setMax(Integer.valueOf(max));
		y_axis.setSteps(max / 10);//y轴步距分成10份
		y_axis.setGridColour("#dfe8f6");
		x_axis.setGridColour("#dfe8f6");
		chart.setYAxis(y_axis);
		chart.setXAxis(x_axis);
		chart.addElements(bar);
		chart.setBackgroundColour("#dfe8f6");
		
		return chart.toString();
	}
	
	public String createSumStateLabelChart(DataPackage<DashBoardVO> dpg){
		Chart chart = new Chart();
		ArrayList<String> colors = new ArrayList<String>();
		Colors color = new Colors();
		int index = 0;
		PieChart pie = new PieChart();
		
		if (dpg != null && dpg.datas.size() > 0) {
		for (Iterator<DashBoardVO> iterator = dpg.datas.iterator(); iterator.hasNext();) {
			DashBoardVO vo = iterator.next();
			pie.addSlice(vo.getValue(), vo.getName());
			colors.add(color.getColor(index++));
		}
		}else
		{
			pie.addSlice(Integer.valueOf(100), "");
			colors.add("#ff0000");
		}
		pie.setColours(colors);// 饼图每块的颜色
		pie.setBorder(Integer.valueOf(1));
		chart.addElements(pie);
		chart.setBackgroundColour("#dfe8f6");
		
		//Map<String, String> map = new HashMap<String, String>();
		//map.put("flowid", flowid);	
		return chart.toString();
	}

	public String createSumWFChart(DataPackage<DashBoardVO> dpg) throws Exception{
		Chart chart = new Chart();
		HorizontalBarChart hbar = new HorizontalBarChart();
		//ArrayList bars = new ArrayList();
		//ArrayList ylabel = new ArrayList();
		ArrayList<Number> lvalues = new ArrayList<Number>();
		YAxis y_axis = new YAxis();
		XAxis x_axis = new XAxis();
		//Colors color = new Colors();
		//List<Label> tempCols=new ArrayList<Label>();
		List<String> tlabels = new ArrayList<String>();
		
		BillDefiHelper helper = new BillDefiHelper();
		int max = 0;
		y_axis.setGridColour("#dfe8f6");
		y_axis.addLabels("");
		for (Iterator<DashBoardVO> iterator = dpg.datas.iterator(); iterator.hasNext();) {
			DashBoardVO vo = iterator.next();
			tlabels.add(helper.getBillDefiNameById(vo.getName()));
			//tempCols.add(new Label(helper.getBillDefiNameById(vo.getName())));
			lvalues.add(vo.getValue());
			
			if (max < vo.getValue().intValue())
				max = vo.getValue().intValue() + 1;
		}
		
		Collections.reverse(tlabels);
	    //Collections.reverse(tempCols);

	    String[] labels = new String[tlabels.size()];
	    for(int i =0; i<tlabels.size(); i++){
	    	labels[i] = tlabels.get(i);
	    }
	    y_axis.addLabels(labels);
	    //y_axis.addLabels(tempCols);
	    lvalues.add(Integer.valueOf(0));
			
		x_axis.setSteps(Integer.valueOf(max / 10));
		x_axis.setMax(Integer.valueOf(max + 10));
		x_axis.setGridColour("#dfe8f6");
		y_axis.setGridColour("#dfe8f6");

	   // y_axis.addLabels(ylabel);
		//y_axis.setMax(10.00);
	    
		hbar.addValues(lvalues);
		hbar.setColour("#ff6600");
		hbar.setAlpha(new Float(100.0));
		chart.addElements(hbar);
		chart.setYAxis(y_axis);
		chart.setXAxis(x_axis);
		chart.setBackgroundColour("#dfe8f6");
        return getRightChartStr(chart.toString());
	}
	
	/**
	 * 水平柱形图时,Y轴的Label会多出一个,所以在这里替换掉
	 * @param str
	 * @return
	 * @throws Exception
	 */
  public String getRightChartStr(String str)throws Exception{
		String strtemp = str;
		int firstLoc = strtemp.indexOf("\"labels\":{\"labels\":[");
		if(firstLoc>0)
			{
			strtemp = strtemp.replaceAll("\"labels\":\\{\"labels\":\\[","\"labels\":\\[");
			  
			  String ftemp = strtemp.substring(0,firstLoc+10);
			  String etemp = strtemp.substring(firstLoc+10);
			  etemp =etemp.replaceFirst("\\]\\}\\}","\\]\\}");
			  strtemp = ftemp+etemp;
	  }
	return strtemp;
   }
}
