package OLink.bpm.core.jfreechart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.constans.Environment;
import net.sf.json.JSONObject;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import eWAP.itext.text.Document;
import eWAP.itext.text.DocumentException;
import eWAP.itext.text.Rectangle;
import eWAP.itext.text.pdf.FontMapper;
import eWAP.itext.text.pdf.PdfContentByte;
import eWAP.itext.text.pdf.PdfTemplate;
import eWAP.itext.text.pdf.PdfWriter;

public class JfreeChartUtil {
    
    //创建数据集
    public static XYSeriesCollection createDataset(
    											JSONObject xColumn,
    											JSONObject yColumn,
    											List<Map<String, String>> data,
    											List<String[]> hashSet,
    											int index){
    	XYSeriesCollection dataset = new XYSeriesCollection();
    	XYSeries series1 = new XYSeries(xColumn.getString("label")+"("+changeUnit(xColumn.getString("fx"))+")-"+yColumn.getString("label")+"("+changeUnit(yColumn.getString("fx"))+")");
    	
    	//x所有唯一轴数据
    	String[]  xobjArray = hashSet.get(0);
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
    	    		for(int x=0;x<xobjArray.length;x++){
	    	    		if(xobjArray[x].equals(xValue)){
	    	    			series1.add(Double.valueOf(x), Double.valueOf(yValue));
	    	    			break;
	    	    		}
	    			}
    	    	}else{
	    	    	for(int i=0;i<objArray.length;i++){
	    	    		if(objArray[i].equals(yValue)){
	    	    			for(int x=0;x<xobjArray.length;x++){
	    	    	    		if(xobjArray[x].equals(xValue)){
	    	    	    			series1.add(Double.valueOf(x), Double.valueOf(i));
	    	    	    			break;
	    	    	    		}
	    	    			}
	    	    			break;
	    	    		}
	    			}
    	    	}
    		}else{
    			//数字类型
    			for(int x=0;x<xobjArray.length;x++){
    	    		if(yValue !=null && xobjArray[x].equals(xValue)){
    	    			series1.add(Double.valueOf(x), Double.valueOf(yValue));
    	    			break;
    	    		}
    			}	
    		}
    			
    		
    	}
    	dataset.addSeries(series1);
    	return dataset;
    }
    
    
    //单位转换
    public static String changeUnit(String unit){
    	if(unit.equals("")){
    		return "真实值";
    	}else if(unit.equals("count")){
    		return "计数";
    	}else if(unit.equals("sum")){
    		return "汇总";
    	}else if(unit.equals("max")){
    		return "最大值";
    	}else if(unit.equals("min")){
    		return "最小值";
    	}else if(unit.equals("avg")){
    		return "平均";
    	}else if(unit.equals("std")){
    		return "标准差";
    	}else if(unit.equals("0")){
    		return "年";
    	}else if(unit.equals("1")){
    		return "季度";
    	}else if(unit.equals("a0")){
    		return "季度&年";
    	}else if(unit.equals("2")){
    		return "月";
    	}else if(unit.equals("&1")){
    		return "月&年";
    	}else if(unit.equals("3")){
    		return "周";
    	}else if(unit.equals("a2")){
    		return "周&年";
    	}else if(unit.equals("4")){
    		return "工作日";
    	}else if(unit.equals("date0")){
    		return "全日期";
    	}else if(unit.equals("5")){
    		return "日";
    	}else if(unit.equals("date1")){
    		return "日期&时间";
    	}else if(unit.equals("6")){
    		return "小时";
    	}else{
    		return "";
    	}
    }
    
    //将jfreechart转为pdf
    public static void saveChartAsPDF(String name,JFreeChart chart, int width,int height, FontMapper mapper) throws IOException {
    	File file = new File(Environment.getInstance().getRealPath("/uploads/customizereport"));
    	if(!file.exists()){
    		file.mkdirs();
    	}
    	File file1 = new File(Environment.getInstance().getRealPath("/uploads/customizereport")+"\\"+name+".pdf");
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file1));    
        writeChartAsPDF(out, chart, width, height, mapper);    
        out.close();    
    }
    
    public static void writeChartAsPDF(OutputStream out, JFreeChart chart,    
            int width, int height, FontMapper mapper) throws IOException {    
         Rectangle pagesize = new Rectangle(width, height);    
         Document document = new Document(pagesize, 50, 50, 50, 50);    
        try {    
             PdfWriter writer = PdfWriter.getInstance(document, out);    
             document.addAuthor("JFreeChart");    
             document.addSubject("Demonstration");    
             document.open();    
             PdfContentByte cb = writer.getDirectContent();    
             PdfTemplate tp = cb.createTemplate(width, height);    
             Graphics2D g2 = tp.createGraphics(width, height, mapper);    
             Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);    
             chart.draw(g2, r2D);    
             g2.dispose();    
             cb.addTemplate(tp, 0, 0);    
         } catch (DocumentException de) {    
             System.err.println(de.getMessage());    
         }    
         document.close();    
     }    


}
