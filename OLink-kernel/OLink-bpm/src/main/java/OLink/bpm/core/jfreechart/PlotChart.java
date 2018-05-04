package OLink.bpm.core.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class PlotChart {

    /**  
     * 生成制图对象  
     */ 
    public static JFreeChart createChart(
    		String title,//图表标题
    		JSONObject xColumn,//x轴的对象
    		final JSONArray yColumns,//y轴的对象集合
    		List<Map<String, String>> data,//数据集
    		final List<String[]> hashSet /*所有y轴唯一值*/) { 
    	
    	JFreeChart chart = ChartFactory.createScatterPlot(  
								    				  title,//图表标题  
								    				  xColumn.getString("label"),//X轴标题  
								    				  "",//Y轴标题  
									                  null,//绘图数据集  
									                  PlotOrientation.VERTICAL,//绘制方向  
									                  true,//显示图例
									                  true,//采用标准生成器
									                  false//是否生成超链接
									                  );
    	 //设置标题字体
        chart.getTitle().setFont(new Font("隶书", Font.BOLD, 23)); 
        //设置图例类别字体  
        chart.getLegend().setItemFont(new Font("宋体", Font.BOLD, 12));
        chart.setBorderStroke(new BasicStroke(1.5f)); 
        XYPlot categoryPlot = (XYPlot)chart.getPlot();
        categoryPlot.setNoDataMessage("没有数据显示");
        categoryPlot.setNoDataMessagePaint(Color.red);
       
        for(int i=0;i<yColumns.size();i++){
    		categoryPlot.setDataset(i, JfreeChartUtil.createDataset(xColumn,((JSONObject)yColumns.get(i)),data, hashSet,i));
    		createyColumn(categoryPlot,xColumn,((JSONObject)yColumns.get(i)),hashSet,i);
    	}
        
        //x所有唯一轴数据
    	String[]  xobjArray = hashSet.get(0);
        categoryPlot.setDomainAxis(AxisUtil.getSymbolAxis((xColumn.getString("label")+"("+JfreeChartUtil.changeUnit(xColumn.getString("fx"))+")"),xobjArray));
        
    	categoryPlot.setBackgroundPaint(Color.WHITE);         //设置绘图区背景色  
    	categoryPlot.setRangeGridlinePaint(Color.RED);       //设置水平方向背景线颜色  
    	categoryPlot.setRangeGridlinesVisible(true);       //设置是否显示水平方向背景线,默认值为true  
    	categoryPlot.setDomainGridlinePaint(Color.RED);     //设置垂直方向背景线颜色  
    	categoryPlot.setDomainGridlinesVisible(true);    //设置是否显示垂直方向背景线,默认值为false  
    	
    	categoryPlot.setDomainCrosshairVisible(true); 
    	categoryPlot.setRangeCrosshairVisible(true); 
    	
    	
    	return chart;  
    	
    	}
    
    
  //创建相应的y轴
    public static void createyColumn(XYPlot categoryPlot,final JSONObject xColumn,final JSONObject yColumn,List<String[]> hashSet,int index){
    	final String[]  xobjArray = hashSet.get(0);
    	final String[]  objArray = hashSet.get(index+1);
    	
    	XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false,true); 
    	renderer.setBaseShapesVisible(true); 
    	renderer.setBaseShapesFilled(true); 
    	renderer.setShapesVisible(true);//设置曲线是否显示数据点 
    	renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());// 显示折点数据
    	
    	if(yColumn.getString("fieldtype").equals("VALUE_TYPE_NUMBER")){
    		categoryPlot.setRangeAxis(index, AxisUtil.getNumberAxis(yColumn.getString("label")+"("+JfreeChartUtil.changeUnit(yColumn.getString("fx"))+")"));
    		renderer.setBaseToolTipGenerator(new XYToolTipGenerator(){ 
    			public String generateToolTip(XYDataset dataset, int series, int item){
    				return xColumn.getString("label")+":"+xobjArray[(int)dataset.getXValue(series, item)]+ "  " +yColumn.getString("label")+":"+ dataset.getYValue(series, item);
    			} 
    			});
    	}else{
    		if(yColumn.getString("fx").equals("count")){
    			//数字类型
    			categoryPlot.setRangeAxis(index, AxisUtil.getNumberAxis(yColumn.getString("label")+"("+JfreeChartUtil.changeUnit(yColumn.getString("fx"))+")"));
    			renderer.setBaseToolTipGenerator(new XYToolTipGenerator(){ 
        			public String generateToolTip(XYDataset dataset, int series, int item){
        				return xColumn.getString("label")+":"+xobjArray[(int) dataset.getXValue(series, item)]+ "  " +yColumn.getString("label")+":"+ dataset.getYValue(series, item);
        			} 
        			});
    		}else{
    			//获得
    			categoryPlot.setRangeAxis(index, AxisUtil.getSymbolAxis(yColumn.getString("label")+"("+JfreeChartUtil.changeUnit(yColumn.getString("fx"))+")",objArray ));
    			renderer.setBaseToolTipGenerator(new XYToolTipGenerator(){ 
        			public String generateToolTip(XYDataset dataset, int series, int item){
        				return xColumn.getString("label")+":"+xobjArray[(int) dataset.getXValue(series, item)]+ "  " +yColumn.getString("label")+":"+ objArray[(int)dataset.getYValue(series, item)];
        			} 
        			});
    		}
    	}
    	
        categoryPlot.mapDatasetToRangeAxis(index, index);
        categoryPlot.setRenderer(index, renderer);
    	
    }
}
