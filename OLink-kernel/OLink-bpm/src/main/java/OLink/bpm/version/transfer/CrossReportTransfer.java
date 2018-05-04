package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import org.apache.log4j.Logger;

import eWAP.core.Tools;

public class CrossReportTransfer extends BaseTransfer {

	private final static Logger LOG = Logger.getLogger(PermissionTransfer.class);
	
	public void to2_4() {

	}
	
	public void to2_5(){
		try{
			FormProcess formPross = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			ViewProcess viewPross = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			CrossReportProcess crossReportProcess = (CrossReportProcess)ProcessFactory.createProcess(CrossReportProcess.class);
			DataPackage<CrossReportVO> datePackage = crossReportProcess.doQuery(new ParamsTable());
			for (Iterator<CrossReportVO> crVOIte = datePackage.datas.iterator(); crVOIte.hasNext();) {
				CrossReportVO  crossReportVO = crVOIte.next();
				if(crossReportVO.getForm()!=null){
					Form form = (Form)formPross.doView(crossReportVO.getForm());
					if(form!=null){
						Collection<FormField> formfield = form.getValueStoreFields();// 获得form存储值的field
						View view = new View();
						if (view.getId() == null || view.getId().trim().length() <= 0) {
							view.setId(Tools.getSequence());
							view.setSortId(Tools.getTimeSequence());
						}
						StringBuffer sb = new StringBuffer();
						reName(viewPross,sb,form.getApplicationid(),form.getName()+"_CrossReport",0);
						view.setName(sb.toString());
						view.setOpenType(View.OPEN_TYPE_NORMAL);
						view.setLastmodifytime(new Date());
						view.setApplicationid(form.getApplicationid());
						view.setModule(form.getModule());
						view.setPagelines("10");
						view.setShowTotalRow(true);
						view.setPagination(true);
						//view.setRelatedForm(form.getId());
						if(crossReportVO.getSql()!=null){
							view.setEditMode(View.EDIT_MODE_CODE_SQL);
							view.setSqlFilterScript(crossReportVO.getSql());
						}else if(crossReportVO.getDql()!=null){
							view.setEditMode(View.EDIT_MODE_CODE_DQL);
							view.setFilterScript(crossReportVO.getDql());
						}
	
						// 将表单中对应有值的列转换为视图的列
						int i = 0;
						for (Iterator<FormField> iterator = formfield.iterator(); iterator.hasNext();) {
							FormField field = iterator.next();
	
							Column column = new Column();
							if (column.getId() == null || column.getId().trim().length() <= 0) {
								column.setId(Tools.getSequence());
								column.setOrderno(i);
							}
							if (field.getDiscript() != null && !field.getDiscript().equals("")) {
								column.setName(field.getDiscript());
							} else {
								column.setName(field.getName());
							}
							column.setFormid(form.getId());
							column.setApplicationid(form.getApplicationid());
							column.setFieldName(field.getName());
							column.setParentView(view.getId());
							column.setIsOrderByField("false");
	
							view.getColumns().add(column);
							i++;
						}
						viewPross.doCreate(view);
						
						crossReportVO.setView(view.getId());
						crossReportVO.setType("CrossReport");
						if(crossReportVO.getColumns()!=null){
							crossReportVO.setColumns(crossReportVO.getColumns().toUpperCase().replace("ITEM_", ""));
						}
						if(crossReportVO.getRows()!=null){
							crossReportVO.setRows(crossReportVO.getRows().toUpperCase().replace("ITEM_", ""));
						}
						if(crossReportVO.getDatas()!=null){
							crossReportVO.setDatas(crossReportVO.getDatas().toUpperCase().replace("ITEM_", ""));
						}
						if(crossReportVO.getFilters()!=null){
							crossReportVO.setFilters("[]");
						}
						crossReportProcess.doUpdate(crossReportVO);
					}
				}
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			LOG.warn(e.getMessage());
		}
	}
	
	//迭代重名视图
	protected void reName(ViewProcess viewPross,StringBuffer sb,String application,String name,int i) throws Exception{
		i++;
		if(viewPross.checkExitName(name, application)){
			reName(viewPross,sb,application,(name+i),i);
		}else{
			sb.append(name);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CrossReportTransfer crossReportTransfer = new CrossReportTransfer();
		// permissionTransfer.to2_4();
		crossReportTransfer.to2_5();
	}

}
