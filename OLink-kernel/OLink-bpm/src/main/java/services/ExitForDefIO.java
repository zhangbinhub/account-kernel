package services;

import eWAP.core.IDefIO;
import eWAP.core.ResourcePool;
import org.apache.poi.hssf.util.HSSFColor;

public class ExitForDefIO {
	public void test(ResourcePool obj )
	{   
		IDefIO  defio=(IDefIO)obj.getResultObj();
		
		defio.setTextColour(1,"A",HSSFColor.RED.index);
		defio.setTextColour(5,"E",HSSFColor.RED.index);
		defio.setCellColour(6,"D",HSSFColor.GREEN.index);
		defio.setCellColour(7,"D",HSSFColor.RED.index);
		defio.setCellValue(1, "G", "出口");
		defio.setCellFormula(1, "H", "SUM(F5:F9)");
		
	}

}
