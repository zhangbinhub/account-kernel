package OLink.bpm.core.helper.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.constans.Environment;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;

/**
 * 
 * 帮助html文件生成类
 * 
 * @author aiming 2010-12-15
 * 
 */
public class BuildHelpHtml {

	private static String MODEDISCRIPT = "";
	private static String OPERATEINTRODUCE = "";
	private static String ABOUTMODE = "";
	private static String OTHEROPERATE = "";

	/**
	 * 读取数据库document信息
	 */
	public static void getDocument() {

		String id = "11e0-027b-6342308c-b1f5-27b2e369e9d8";
		try {
			// 创建指定应用id的document
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,id);
			// 查询主表
			String sql = "select * from tlk_helpmode";
			Collection<Document> documents = dp.queryBySQL(sql);
			// 遍历主表
			for (Iterator<Document> it = documents.iterator(); it.hasNext();) {

				Document doc = it.next();
				Collection<Document> childDocuments = doc.getChilds();
				for (Iterator<Document> its = childDocuments.iterator(); its
						.hasNext();) {
					Document childDoc = its.next();
					// 根据条件拼接html模版内容
					if (childDoc.getFormname().equals("帮助/help_user/操作说明")) {
						OPERATEINTRODUCE = OPERATEINTRODUCE + "<li>"
								+ childDoc.getItemValueAsString("xuhao") + "、"
								+ childDoc.getItemValueAsString("op_name");
					} else if (childDoc.getFormname().equals(
							"帮助/help_user/模块描述")) {
						MODEDISCRIPT = MODEDISCRIPT
								+ "<li>"
								+ childDoc.getItemValueAsString("xuhao")
								+ "、"
								+ childDoc
										.getItemValueAsString("moduldiscribe");
					} else if (childDoc.getFormname().equals(
							"帮助/help_user/其它操作")) {
						OTHEROPERATE = OTHEROPERATE + "<li>"
								+ childDoc.getItemValueAsString("xuhao") + "、"
								+ childDoc.getItemValueAsString("op_name");
					} else if (childDoc.getFormname().equals(
							"帮助/help_user/相关模块")) {
						ABOUTMODE = ABOUTMODE
								+ "<li>"
								+ childDoc.getItemValueAsString("xuhao")
								+ "、"
								+ childDoc
										.getItemValueAsString("aboutdiscribe");
					}
				}

				// 开始通过html模版替换模版信息
				String url = Environment.getInstance().getRealPath("toc\\")
						+ "helpmodel.html";// 模板文件地址
				// System.out.println("*************" + url);
				String savePath = Environment.getInstance()
						.getRealPath("toc\\")
						+ doc.getItemValueAsString("location")
						+ "\\"
						+ doc.getItemValueAsString("htmlname") + ".html";
				toBuildHtml(url, savePath, MODEDISCRIPT, OPERATEINTRODUCE,
						ABOUTMODE, OTHEROPERATE);
				System.out.println("提示：文件保存路径：" + savePath);

				OPERATEINTRODUCE = "";
				MODEDISCRIPT = "";
				OTHEROPERATE = "";
				ABOUTMODE = "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据本地模板生成静态页面
	 * 
	 * @param HtmlFile
	 *            html路经
	 * @return
	 */
	public static boolean toBuildHtml(String filePath, String HtmlFile,
			String MODEDISCRIPT, String OPERATEINTRODUCE, String ABOUTMODE,
			String OTHEROPERATE) {
		String str = "";
		FileInputStream is = null;
		BufferedWriter o = null;
		try {
			String tempStr = "";
			StringBuffer buffer = new StringBuffer();
			is = new FileInputStream(filePath);// 读取模块文件
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((tempStr = br.readLine()) != null) {
				buffer.append(tempStr);
			}
			str = buffer.toString();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {

			str = str.replaceAll("MODEDISCRIPT_CONTENTxxxxx", MODEDISCRIPT);
			str = str.replaceAll("OPERATEINTRODUCE_CONTENTxxxxx",
					OPERATEINTRODUCE);
			str = str.replaceAll("ABOUTMODE_CONTENTxxxxx", ABOUTMODE);
			str = str.replaceAll("OTHEROPERATE_CONTENTxxxxx", OTHEROPERATE);

			File f = new File(HtmlFile);
			o = new BufferedWriter(new FileWriter(f));
			o.write(str);
			System.out.println("提示：文件生成完成...");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 测试main 函数
	 * 
	 * @param arg
	 */
	// public static void main(String[] arg) {
	// getDocument();
	// }
}