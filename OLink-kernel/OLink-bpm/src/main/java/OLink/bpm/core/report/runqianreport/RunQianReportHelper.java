package OLink.bpm.core.report.runqianreport;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.util.web.DWRHtmlUtils;

public class RunQianReportHelper {

	/**
	 * 获取润乾报表模板文件
	 * @param selectFieldName
	 * @param def
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public  String getReportFiles(String selectFieldName, String def,HttpServletRequest request) throws Exception{
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("","{*[Select]*}");
		String realPath = request.getRealPath("/reportFiles");
		File file = new File(realPath);
		if(file!=null && file.listFiles()!=null && file.listFiles().length>0){
			for(int i=0;i<file.listFiles().length;i++){
				if(file.listFiles()[i].isFile()){
					map.put("/portal/share/report/runqianreport/content.jsp?reportFileName="+file.listFiles()[i].getName(),file.listFiles()[i].getName());
				}
			}
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}
	
}
