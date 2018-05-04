package OLink.bpm.core.jfreechart;

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;

public class PieChart {

    /**  
     * 生成制图对象  
     */ 
    public static JFreeChart createChart(
    		String title,//图表标题
    		JSONObject xColumn,//x轴的对象
    		final JSONArray yColumns,//y轴的对象集合
    		List<Map<String, String>> data,//数据集
    		final List<String[]> hashSet /*所有y轴唯一值*/) { 
    	
    	JFreeChart chart = ChartFactory.createPieChart(  
								    				  title,//图表标题
								    				  createDataset(xColumn,((JSONObject)yColumns.get(0)),data,hashSet,0),
									                  true,//显示图例
									                  true,//采用标准生成器
									                  false//是否生成超链接
									                  );
    	 //设置标题字体
        chart.getTitle().setFont(new Font("隶书", Font.BOLD, 23)); 
        //设置图例类别字体  
        chart.getLegend().setItemFont(new Font("宋体", Font.BOLD, 12));
       // chart.setBorderStroke(new BasicStroke(1.5f)); 
        PiePlot categoryPlot = (PiePlot)chart.getPlot();
        categoryPlot.setLabelFont(new Font("宋体", Font.BOLD, 12));
        categoryPlot.setNoDataMessage("没有数据显示");
        categoryPlot.setNoDataMessagePaint(Color.red);
        categoryPlot.setCircular(false);
        categoryPlot.setLabelGap(0.02D);
    	return chart;  
    	
    	}
    
    //创建图表数据
    private static PieDataset createDataset(JSONObject xColumn,JSONObject yColumn,List<Map<String, String>> data,List<String[]> hashSet,int index){
      DefaultPieDataset localDefaultPieDataset = new DefaultPieDataset();
	    //x所有唯一轴数据
	  	String[]  objArray = hashSet.get(index+1);
	  	for (Iterator<Map<String,String>> ite = data.iterator(); ite.hasNext();) {
	  	    Map<String, String> map = ite.next();
	  	    String xValue = map.get("xAxis");
	  	    if(xValue==null){
	  	    	xValue = map.get("XAXIS");
	  	    }
	  	    String yValue = map.get("yAxis" + index);
	  	    if(yValue==null){
	  	    	yValue = map.get("YAXIS" + index);
	  	    }
	  	    
	  	    //字符串类型
	  	    if(yColumn.getString("fieldtype").equals("VALUE_TYPE_DATE") || yColumn.getString("fieldtype").equals("VALUE_TYPE_VARCHAR")){
	  	    	if(yColumn.getString("fx").equals("count")){
	  	    		//数字类型
		    	    localDefaultPieDataset.setValue(xValue, Double.valueOf(yValue));
	  	    	}else{
	    	    	for(int i=0;i<objArray.length;i++){
	    	    		if(objArray[i].equals(yValue)){
	    	    	    	localDefaultPieDataset.setValue(xValue, Double.valueOf(i));
	    	    		}
	    			}
	  	    	}
	  		}else{
	  			//数字类型
  	    		if(yValue !=null){
  	    			localDefaultPieDataset.setValue(xValue, Double.valueOf(yValue));
  	    		}
	  		}
	  	}
      return localDefaultPieDataset;
    }
    
    
  //创建相应的y轴
    public static void createyColumn(PiePlot categoryPlot,final JSONObject xColumn,final JSONObject yColumn,List<String[]> hashSet,int index){
    	final String[]  xobjArray = hashSet.get(0);
    	final String[]  objArray = hashSet.get(index+1);
    	
    	XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false,true); 
    	renderer.setBaseShapesVisible(true); 
    	renderer.setBaseShapesFilled(true); 
    	renderer.setShapesVisible(true);//设置曲线是否显示数据点 
    	renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());// 显示折点数据
    	
    	if(yColumn.getString("fieldtype").equals("VALUE_TYPE_NUMBER")){
    		//categoryPlot.setRangeAxis(index, AxisUtil.getNumberAxis(yColumn.getString("label")+"("+JfreeChartUtil.changeUnit(yColumn.getString("fx"))+")"));
    		renderer.setBaseToolTipGenerator(new XYToolTipGenerator(){ 
    			public String generateToolTip(XYDataset dataset, int series, int item){
    				return xColumn.getString("label")+":"+xobjArray[(int)dataset.getXValue(series, item)]+ "  " +yColumn.getString("label")+":"+ dataset.getYValue(series, item);
    			} 
    			});
    	}else{
    		if(yColumn.getString("fx").equals("count")){
    			//数字类型
    			//categoryPlot.setRangeAxis(index, AxisUtil.getNumberAxis(yColumn.getString("label")+"("+JfreeChartUtil.changeUnit(yColumn.getString("fx"))+")"));
    			renderer.setBaseToolTipGenerator(new XYToolTipGenerator(){ 
        			public String generateToolTip(XYDataset dataset, int series, int item){
        				return xColumn.getString("label")+":"+xobjArray[(int) dataset.getXValue(series, item)]+ "  " +yColumn.getString("label")+":"+ dataset.getYValue(series, item);
        			} 
        			});
    		}else{
    			//获得
    			//categoryPlot.setRangeAxis(index, AxisUtil.getSymbolAxis(yColumn.getString("label")+"("+JfreeChartUtil.changeUnit(yColumn.getString("fx"))+")",objArray ));
    			renderer.setBaseToolTipGenerator(new XYToolTipGenerator(){ 
        			public String generateToolTip(XYDataset dataset, int series, int item){
        				return xColumn.getString("label")+":"+xobjArray[(int) dataset.getXValue(series, item)]+ "  " +yColumn.getString("label")+":"+ objArray[(int)dataset.getYValue(series, item)];
        			} 
        			});
    		}
    	}
    	
      //  categoryPlot.mapDatasetToRangeAxis(index, index);
      //  categoryPlot.setRenderer(index, renderer);
    	
    }
}
