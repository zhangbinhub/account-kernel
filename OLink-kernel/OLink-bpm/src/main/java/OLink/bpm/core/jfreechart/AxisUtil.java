package OLink.bpm.core.jfreechart;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;

public class AxisUtil {

	/**
	 * 字符串类型
	 * @param title 轴标题
	 * @param strArray 字符串数组
	 * @return
	 */
	public static SymbolAxis getSymbolAxis(String title,String[] strArray){
		SymbolAxis symbolAxis = new SymbolAxis(title, strArray);
		return symbolAxis;
	}
	
	/**
	 * 数字类型
	 * @param title 轴标题
	 * @return
	 */
	public static NumberAxis getNumberAxis(String title){
		NumberAxis numberaxis = new NumberAxis (title);
		numberaxis.setAxisLineVisible(true);
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());// 设置图中的刻度线的单位
		//numberaxis.setAxisLinePaint(Color.RED);
		//numberaxis.setAxisLineStroke(new BasicStroke(BasicStroke.CAP_BUTT));
		//numberaxis.setAutoTickUnitSelection(true);
	    return numberaxis;
	}
	
	
}
