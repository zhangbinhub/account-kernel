package OLink.bpm.core.helper.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;

/**
 * 使用本类读取html模版，生成静态html文件
 * @author Aiming 2010-12-15
 *
 */
public class BuildIscriptHtml {
	
	//填充静态模版内容的变量
	private static String NAME = "";
	
	private static String SUMMARY = "";
	
	private static String RETURNVALUE = "";
	
	private static String PARAMETERTYPE = "";
	private static String PARAMETERVALUE = "";
	private static String PARAMETERDISCRIBE = "";
	
	private static String EXAMPLEDISCRIBE = "";
	private static String HTMLCODE = "";
	private static String ISCRIPTCODE = "";
	private static String RESULT = "";
	
	@SuppressWarnings("unused")
	private static String TITLE = "";
	
	//流定义
	private static FileInputStream is;
	private static BufferedWriter o;
	
	//获取html文件模版的字符串
	private static String htmlStr = "";
	
	//设置html文件的路径
	private static String sourceHtmlModulPath = "";
	//设置目标文件的输出html文件
	private static String targetHtmlPath = "";
	
	
	//将html拆分为4各部分，开始区域，参数区域，描述区域，和结束区域		这是模版源
	private static String beginArea = "";
	
	private static String parameterBeginArea = "";
	private static String parameterArea = "";
	private static String parameterEndArea = "";
	
	private static String discribeBeginArea = "";
	private static String discribeArea = "";
	private static String discribeEndArea = "";
	private static String endArea = "";
	
	
	//替换后的模版源区域块变量
	private static String beginStr = "";
	private static String parameterStr = "";
	private static String discribeStr = "";
	//private static String endStr = "";
	
	//用于重组替换的区域块变量
	private static String targetStr = "";
	
	private static String link;
	private static List<String> list = new ArrayList<String>();
	
	/**
	 * 本方法通过读取指定路径下的html模版文件生成字符串
	 * @param sourceHtmlModulPath
	 */
	public static void getHtmlFileString(String sourceHtmlModulPath){
		
		try {
			String tempStr = "";
			is = new FileInputStream(sourceHtmlModulPath);//读取模块文件
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((tempStr = br.readLine()) != null)
				htmlStr = htmlStr + tempStr;
			is.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//如果htmlStr的长度为0说明模版文件为空或者找不到路径
		if(htmlStr.length() == 0)
		{
			System.out.println("提示：获取html模版文件为空或者找不到文件路径...");
		}
		
		//读取模版字符串，划分出区域块，作为基础模版，可重用
		beginArea = GetStringByMark.getStr(htmlStr, "<!-- #开始区域# -->", "<!-- *开始区域* -->");
		
		parameterBeginArea = GetStringByMark.getStr(htmlStr, "<!-- #参数开始区域# -->", "<!-- *参数开始区域* -->");
		parameterArea = GetStringByMark.getStr(htmlStr, "<!-- #参数替换区域# -->", "<!-- *参数替换区域* -->");
		parameterEndArea = GetStringByMark.getStr(htmlStr, "<!-- #参数结束区域# -->", "<!-- *参数结束区域* -->");
		
		discribeBeginArea = GetStringByMark.getStr(htmlStr, "<!-- #描述开始区域# -->", "<!-- *描述开始区域* -->");
		discribeArea = GetStringByMark.getStr(htmlStr, "<!-- #描述替换区域# -->", "<!-- *描述替换区域* -->");
		discribeEndArea = GetStringByMark.getStr(htmlStr, "<!-- #描述结束区域# -->", "<!-- *描述结束区域* -->");
		
		endArea = GetStringByMark.getStr(htmlStr, "<!-- #结束区域 # -->", "<!-- *结束区域 * -->");
		
//		System.out.println(beginArea);
//		System.out.println("----------------------------------------------------------------------");
//		System.out.println(parameterArea);
//		System.out.println("----------------------------------------------------------------------");
//		System.out.println(discribeArea);
//		System.out.println("----------------------------------------------------------------------");
//		System.out.println(endArea);
		
		
		
	}
	
	
	
	
	/**
	 * 本方法通过生成指定保存路径下的静态html文件
	 * @param savePathName
	 */
	public static void buildHtmlFile(String savePathName){
		try {
			File f = new File(savePathName);
			o = new BufferedWriter(new FileWriter(f));
			o.write(targetStr);
			o.close();
			System.out.println("提示：html文件生成成功...");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("提示：生成html文件出错...");
		}
	}
	
	
	
	//读取数据库document信息
	public static void getDocumentInfo(String applicationId){
		String id = applicationId;
		
		try {
			//创建指定应用id的document
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,id);
			//查询主表
			String sql = "select * from tlk_Iscripthelp";
			Collection<Document> documents = dp.queryBySQL(sql);
			//遍历主表
			for (Iterator<Document> it = documents.iterator(); it.hasNext(); ) {
				
				Document doc = it.next();
				//获取头部信息和返回值
				NAME = doc.getItemValueAsString("name");
				//System.out.println(NAME);
				//获取返回值
				RETURNVALUE = doc.getItemValueAsString("returnvalue");
				//获取文档标题
				TITLE = doc.getItemValueAsString("doctitle");
				//System.out.println(RETURNVALUE);
				Collection<Document> childDocuments = doc.getChilds();
				for (Iterator<Document> its = childDocuments.iterator(); its.hasNext(); ) {
					Document childDoc = its.next();
					//根据条件拼接html模版内容
					if(childDoc.getFormShortName().equals("概述")){
						
						SUMMARY = childDoc.getItemValueAsString("summary");
						//System.out.println(SUMMARY);
						
						//开始替换开始区域块
						String currentBeginArea = beginArea;
						System.out.println(beginArea + "****########********" + NAME);
						currentBeginArea = currentBeginArea.replace("namexxxxxxxxxx", NAME);
						currentBeginArea = currentBeginArea.replace("returnvaluexxxxxxxxxx", RETURNVALUE);
						currentBeginArea = currentBeginArea.replace("summaryxxxxxxxxxx", SUMMARY);
						//得到目标html参数区域块
						beginStr = currentBeginArea;
						System.out.println(beginStr);
						
					}
					else if(childDoc.getFormShortName().equals("参数")){
						
						PARAMETERVALUE = childDoc.getItemValueAsString("xuhao") + "、" + childDoc.getItemValueAsString("parameter");
						System.out.println(PARAMETERVALUE);
						if(childDoc.getItemValueAsString("type").length() > 0)
						{
							PARAMETERTYPE = childDoc.getItemValueAsString("type");
							System.out.println("提示：########参数类型为：" + PARAMETERTYPE);
						}
						if(childDoc.getItemValueAsString("discribe").length() > 0)
						{
							PARAMETERDISCRIBE = childDoc.getItemValueAsString("discribe"); 
							//System.out.println(PARAMETERDISCRIBE);
						}
						
						//开始替换参数区域
						String currentArea = parameterArea;
						currentArea = currentArea.replace("parametervaluexxxxxxxxxx", PARAMETERVALUE);
						currentArea = currentArea.replace("parametertypexxxxxxxxxx", PARAMETERTYPE);
						currentArea = currentArea.replace("parameterdiscriptxxxxxxxxxx", PARAMETERDISCRIBE);
						//得到目标html参数区域块
						parameterStr = parameterStr + currentArea;
						//System.out.println(parameterStr);
						
						
					}
					else if(childDoc.getFormShortName().equals("示例")){
						
						EXAMPLEDISCRIBE = childDoc.getItemValueAsString("discribe");
						System.out.println(EXAMPLEDISCRIBE);
						if(!childDoc.getItemValueAsString("htmlcode").equals("")){
							HTMLCODE = childDoc.getItemValueAsString("htmlcode");
							System.out.println(HTMLCODE);
						}
						if(!childDoc.getItemValueAsString("iscriptcode").equals("")){
							ISCRIPTCODE = childDoc.getItemValueAsString("iscriptcode");
							System.out.println(ISCRIPTCODE);
						}
						if(!childDoc.getItemValueAsString("result").equals("")){
							RESULT = childDoc.getItemValueAsString("result");
							System.out.println(RESULT);
						}
						
						//开始替换参数区域
						String currentArea = discribeArea;
						currentArea = currentArea.replace("examplediscribexxxxxxxxxx", EXAMPLEDISCRIBE);
						currentArea = currentArea.replace("HTMLxxxxxxxxxx", HTMLCODE);
						currentArea = currentArea.replace("iScriptxxxxxxxxxx", ISCRIPTCODE);
						currentArea = currentArea.replace("resultxxxxxxxxxx", RESULT);
						//得到目标html描述区域块
						discribeStr = discribeStr + currentArea;
						System.out.println(discribeStr);
						
					}
					
				}
				
				//组合html区域代码块得到目标html的源文件字符串
				targetStr = beginStr + parameterBeginArea + parameterStr + parameterEndArea 
							+ discribeBeginArea + discribeStr + discribeEndArea + endArea;
				System.out.println("*************************");
				System.out.println(targetStr);
				
				//设置目标htnl静态文件的输出路径
				targetHtmlPath =  System.getProperty("user.dir") + "\\src\\main\\webapp\\toc\\"
				  + doc.getItemValueAsString("location") + "\\"
				  + NAME + ".html";
				
				//保存目标html
				buildHtmlFile(targetHtmlPath);
				
				link = "<topic label=\"" + NAME + "\" id=\"iscripthelp_help\" href=\"iscripthelp/" + NAME + ".html\"/>";
				list.add(link);
				
				//程序运行至此，进入下一个文件的生成！需重置相关变量
				beginStr = "";
				parameterStr = "";
				discribeStr = "";
				
				NAME = "";
				SUMMARY = "";
				RETURNVALUE = "";
				PARAMETERTYPE = "";
				PARAMETERVALUE = "";
				PARAMETERDISCRIBE = "";
				EXAMPLEDISCRIBE = "";
				HTMLCODE = "";
				ISCRIPTCODE = "";
				RESULT = "";
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	
	public static void main(String args[]) {
		sourceHtmlModulPath  =  System.getProperty("user.dir") + "\\src\\main\\webapp\\toc\\" + "iscripthelpmodel.html";
		getHtmlFileString(sourceHtmlModulPath);
		String id = "11e0-13b9-3212df90-9260-697b1ccac59d";
		getDocumentInfo(id);
		for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
			String url = it.next();
			System.out.println(url);
		}
	}
}

















