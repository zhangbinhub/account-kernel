package OLink.bpm.core.dynaform.printer.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * @author Happy
 *
 */
public interface PrinterProcess extends IDesignTimeProcess<Printer> {
	
	/**根据表单id获取所有字段
	 * @param formid
	 * @return 返回XML字符串
	 */
	String getFields(String formid);
	
	
	/**
	 * 根据表单id获取所有视图列表XML文件
	 * @param formid
	 * @return
	 */
	String getSubViews(String formid, IRunner runner);
	
	/**
	 * 
	 * @param formid
	 * @return
	 * @throws Exception 
	 */
	String getReportData(String id, String _formid, String _docid, String _flowid, WebUser user, ParamsTable params) throws Exception;
	
	Printer findByFormid(String formid) throws Exception;
	
	/**
	 * 获取 视图数据集合
	 * @param view
	 * @param parentId
	 * @param line
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	DataPackage<Document> getViewDatas(View view, int line, WebUser user, ParamsTable params) throws Exception;
	
	/**
	 * 根据模块id获取Print对象的集合
	 * @param moduleid 模块id
	 * @return Print对象的集合
	 * @throws Exception
	 */
	Collection<Printer> getPrinterByModule(String moduleid) throws Exception;
	

}
