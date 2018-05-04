package OLink.bpm.core.dynaform.dts.excelimport.config.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.dts.excelimport.ExcelMappingDiagram;
import OLink.bpm.core.dynaform.dts.excelimport.config.ImpExcelToDoc;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.dts.excelimport.Factory;
import OLink.bpm.core.dynaform.dts.excelimport.config.ImpExcelException;

/**
 * @author nicholas
 */
public class IMPMappingConfigAction extends BaseAction<IMPMappingConfigVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4597984625198311088L;

	private String _path;

	private String _impmappingconfigid;

	private String _msg;

	/**
	 * @return the _msg
	 * @uml.property name="_msg"
	 */
	public String get_msg() {
		return _msg;
	}

	/**
	 * @param _msg
	 *            the _msg to set
	 * @uml.property name="_msg"
	 */
	public void set_msg(String _msg) {
		this._msg = _msg;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public IMPMappingConfigAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(IMPMappingConfigProcess.class), new IMPMappingConfigVO());
	}

	/*
	 * public String doSave() throws Exception {
	 * 
	 * IMPMappingConfigVO vo = (IMPMappingConfigVO) getContent();
	 * 
	 * vo.setApplicationid(getApplication());
	 * 
	 * super.doSave(); return SUCCESS; }
	 */

	public String doSave() {
		IMPMappingConfigVO IMPmcVO = (IMPMappingConfigVO) (this.getContent());
		ParamsTable params = this.getParams();
		params.setParameter("s_name", IMPmcVO.getName());
		boolean flag = false;
		DataPackage<IMPMappingConfigVO> tempVOs =null;
		IMPMappingConfigVO tempIMPmcVO = null;
		try{
			 tempVOs = process.doQuery(params);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(tempVOs !=null && tempVOs.getRowCount()>0){
			for (Iterator<IMPMappingConfigVO> it = tempVOs.datas.iterator(); it.hasNext();) {
				//tempIMPmcVO =(IMPMappingConfigVO)it.next();
				tempIMPmcVO =it.next();
				break;
			}
		}
		
		if (tempIMPmcVO != null) {
			if (tempIMPmcVO.getId() == null|| tempIMPmcVO.getId().trim().length() <= 0) {// 判断新建不能重名
				this.addFieldError("1", "{*[page.name.exist]*}");
				flag = true;
			} else if (!tempIMPmcVO.getId().trim().equalsIgnoreCase(IMPmcVO.getId())) {// 修改不能重名
				this.addFieldError("1", "{*[page.name.exist]*}");
				flag = true;
			}
		}
		
		if(!flag){
			return super.doSave();
		}else{
			return INPUT;
		}
		
	}

	public String improtExcelToDocument() throws Exception {
		Environment evt = Environment.getInstance();
		String excelPath = evt.getRealPath(_path);
		//add by lr 2013-11-27

		String strReal=evt.getApplicationRealPath().replaceAll("\\\\", "/");
		excelPath=strReal.substring(0,strReal.lastIndexOf("/"))+_path.substring(1);

		if (!excelPath.toLowerCase().endsWith(".xls") && !excelPath.toLowerCase().endsWith(".xlsx")) {
			this.addFieldError("fileTypeError", "{*[core.dts.excelimport.config.cannotimport]*}");
			return SUCCESS;
		}
		IMPMappingConfigVO vo = (IMPMappingConfigVO) process.doView(this._impmappingconfigid);

		ExcelMappingDiagram em = Factory.trnsXML2Dgrm(vo.getXml());
		ImpExcelToDoc imp = new ImpExcelToDoc(excelPath, em);
		try {
			_msg = imp.creatDocument(getUser(), getParams(), vo.getApplicationid());
		} catch (ImpExcelException iee) {
			Collection<String> errors = iee.getRowErrors();
			int count = 0;
			for (Iterator<String> iterator = errors.iterator(); iterator.hasNext();) {
				//String error = (String) iterator.next();
				String error = iterator.next();
				this.addFieldError(Integer.toString(count), error);
				count++;
			}
			return INPUT;
		} catch (Exception e) {
			this.addFieldError("error", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * @return the _path
	 * @uml.property name="_path"
	 */
	public String get_path() {
		return _path;
	}

	/**
	 * @param _path
	 *            the _path to set
	 * @uml.property name="_path"
	 */
	public void set_path(String _path) {
		this._path = _path;
	}

	/**
	 * @return the _impmappingconfigid
	 * @uml.property name="_impmappingconfigid"
	 */
	public String get_impmappingconfigid() {
		return _impmappingconfigid;
	}

	/**
	 * @param _impmappingconfigid
	 *            the _impmappingconfigid to set
	 * @uml.property name="_impmappingconfigid"
	 */
	public void set_impmappingconfigid(String _impmappingconfigid) {
		this._impmappingconfigid = _impmappingconfigid;
	}

	/**
	 * 获取RunTime用户
	 */
	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

}
