package OLink.bpm.core.report.standardreport.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.report.wfdashboard.action.Colors;

import jofc2.model.Chart;
import jofc2.model.axis.XAxis;
import jofc2.model.axis.YAxis;
import jofc2.model.elements.BarChart;
import jofc2.model.elements.LineChart;
import jofc2.model.elements.PieChart;


public class OFC2ChartGenerator {
	
	public static String getBarChart(Collection<Map<String, String>> data, String[] cols){

		Chart chart = new Chart();

		int max = 0;
		//int i = 1;
		BarChart bar = new BarChart(BarChart.Style.GLASS);
		YAxis y_axis = new YAxis();
		XAxis x_axis = new XAxis();
		//Colors color = new Colors();
		Double usedtime = null;
		Iterator<Map<String, String>> iter = data.iterator();

		while(iter.hasNext()) {
			Map<String, String> map = iter.next();
			String tempLables = "";
			for(int j=0;j<cols.length;j++)
			{
				String temp = map.get(cols[j]);
				tempLables = temp;
				if(tempLables!=null&&!tempLables.equals(""))
				 break;
			}
			
			usedtime = Double.parseDouble(map.get("USEDTIME"));
			bar.addValues(usedtime);

			x_axis = x_axis.addLabels(tempLables);

			if (max < usedtime.intValue())
				max = usedtime.intValue() + 5;
			
		}

		bar.setColour("#ff00ff");

		if(max>10){
			max = max + 10;
		}
		y_axis.setMax(Integer.valueOf(max));
		y_axis.setGridColour("#dfe8f6");
		y_axis.setSteps(max/10);
		x_axis.setGridColour("#dfe8f6");
		chart.setYAxis(y_axis);
		chart.setXAxis(x_axis);
		chart.addElements(bar);
		chart.setBackgroundColour("#dfe8f6");
		
		return chart.toString();
	}
	
	public static String getLineChart(Collection<Map<String, String>> data, String[] cols){
		Chart chart = new Chart();
		LineChart  line = new LineChart();
		XAxis x_axis = new XAxis();
		YAxis y_axis = new YAxis();

		int max = 0;
		Double usedtime = null;
		Iterator<Map<String, String>> iter = data.iterator();
			
		while(iter.hasNext()) {
		 Map<String, String> map = iter.next();
		 String tempLables = "";
		 for(int j=0;j<cols.length;j++)
		 {
		   String temp = map.get(cols[j]);
		   tempLables = temp;
		   if(tempLables !=null && !tempLables.equals(""))
			  break;
		 }
			usedtime = Double.valueOf(String.valueOf(map.get("USEDTIME")));
			if(max<usedtime.intValue())
			max = usedtime.intValue()+1;
			
			x_axis = x_axis.addLabels(tempLables);
			line.addValues(usedtime);
		}
		chart.addElements(line);
		chart.setBackgroundColour("#dfe8f6");
		max = max +(int)(max*0.2);
		chart.setXAxis(x_axis);
		if(max>30)y_axis.setSteps(max/10);
		y_axis.setMax(Integer.valueOf(max));
		y_axis.setGridColour("#dfe8f6");
		chart.setYAxis(y_axis);
			// set the Y max
			//g.set_y_max(max);
			// label every 20 (0,20,40,60)
			//g.y_label_steps(6);
		
		return chart.toString();
	}
	
	public static String getPieChart(Collection<Map<String, String>> data, String[] cols){
		
		List<String> colors = new ArrayList<String>();
		Chart chart = new Chart();
		Iterator<Map<String, String>> iter = data.iterator();
		Colors color = new Colors();
		PieChart pie = new PieChart();
		int index = 0;
		
		while(iter.hasNext()) {
			Map<String, String> map = iter.next();
			String tempLables = "";
			for(int j=0;j<cols.length;j++)
			{
				String temp = map.get(cols[j]);
				tempLables = temp;
				if(tempLables!=null&&!tempLables.equals(""))
				 break;
			}
		
			pie.addSlice(Double.parseDouble(map.get("USEDTIME")),tempLables);
			if(index<16){
				colors.add(color.getColor(index++));
			}
		}
		
			//pie.addSlice(new Integer(100), "");
		//	colors.add("#ff0000");
		pie.setColours(colors);// 饼图每块的颜色
		pie.setBorder(Integer.valueOf(1));
		chart.addElements(pie);
		chart.setBackgroundColour("#dfe8f6");
		
		return chart.toString();
	}

	
}
