package OLink.bpm.core.workflow.storage.definition.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;

public class BillDefiHelper extends BaseHelper<BillDefiVO> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public BillDefiHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(BillDefiProcess.class));
	}

	private static BillDefiProcess getBillDefiProcess()
			throws ClassNotFoundException {
		return (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
	}

	public Collection<BillDefiVO> get_flowList() throws Exception {
		return getBillDefiProcess().getBillDefiByModule(this.getModuleid());
	}

	public static BillDefiVO getBillDefiVOById(String id) throws Exception {
		return (BillDefiVO) getBillDefiProcess().doView(id);
	}

	public Map<String, String> getBillDefiVOByModules(String moduleId)
			throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		Collection<BillDefiVO> billdefiList = getBillDefiProcess()
				.getBillDefiByModule(moduleId);
		if (billdefiList != null && billdefiList.size() > 0) {
			for (Iterator<BillDefiVO> iterator = billdefiList.iterator(); iterator
					.hasNext();) {
				BillDefiVO billdef = iterator.next();
				map.put(billdef.getId(), billdef.getSubject());
			}
			return map;
		} else {
			return new HashMap<String, String>();
		}

	}

	public String getBillDefiNameById(String billid) throws Exception {
		String rtn = "";
		if (!StringUtil.isBlank(billid)) {
			BillDefiVO vo = (BillDefiVO) getBillDefiProcess().doView(billid);
			if (vo != null)
				rtn = vo.getSubject();
		}

		return rtn;
	}

	public Map<String, String> get_flowList(String applicationid,
			String domainid) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("s_applicationid", applicationid);
		Collection<BillDefiVO> col = getBillDefiProcess().doSimpleQuery(params);
		Map<String, String> rtnMap = new HashMap<String, String>();
		for (Iterator<BillDefiVO> iterator = col.iterator(); iterator.hasNext();) {
			BillDefiVO vo = iterator.next();
			rtnMap.put(vo.getId(), vo.getSubject());
		}

		return rtnMap;
	}

	/**
	 * 取得这个指定domain的指定application的第一个流程
	 * 
	 * @param applicationid
	 * @param domainid
	 * @return
	 * @throws Exception
	 */
	public String get_FirstflowId(String applicationid, String domainid)
			throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("s_applicationid", applicationid);
		Collection<BillDefiVO> col = getBillDefiProcess().doSimpleQuery(params);
		String rtn = "";

		if (col != null && col.size() > 0) {
			Iterator<BillDefiVO> iter = col.iterator();
			if (iter != null && iter.hasNext()) {
				BillDefiVO vo = iter.next();
				rtn = vo.getId();
			}
		}

		return rtn;
	}

	public Collection<?> get_backNodeList(String billid, String nodeid)
			throws Exception {
		BillDefiVO flow = (BillDefiVO) getBillDefiProcess().doView(billid);
		FlowDiagram fd = flow.toFlowDiagram();
		Node node = (Node) fd.getElementByID(nodeid);
		Vector<Node> backNodeList = new Vector<Node>();
		Vector<Node> temp = null;
		temp = fd.getBackSetpNode(node);
		backNodeList.addAll(temp);
		backNodeList = (Vector<Node>) get_backNode(fd, backNodeList, temp);

		return backNodeList;
	}

	public Collection<Node> get_backNode(FlowDiagram fd,
			Collection<Node> backNodeList, Collection<Node> temp)
			throws Exception {
		try {
			Vector<Node> tmp = null;
			for (Iterator<Node> it = temp.iterator(); it.hasNext();) {
				Node node = it.next();
				tmp = fd.getBackSetpNode(node);
				if (tmp != null && !tmp.isEmpty()) {
					toUnique(backNodeList, tmp);
					backNodeList.addAll(tmp);
					get_backNode(fd, backNodeList, tmp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return backNodeList;
	}

	public synchronized void toUnique(Collection<Node> backNodeList,
			Collection<Node> temp) {
		Collection<Node> tmp = new ArrayList<Node>();
		for (Iterator<Node> it = temp.iterator(); it.hasNext();) {
			Node node = it.next();
			for (Iterator<Node> iter = backNodeList.iterator(); iter.hasNext();) {
				Node tnode = iter.next();
				if (node.id.equals(tnode.id)) {
					tmp.add(node);
					break;
				}
			}
		}
		if (!tmp.isEmpty()) {
			temp.removeAll(tmp);
		}
	}
	
	/**
	 * flex版流程编辑器通过RemoteObject访问方法
	 * @param applicationid
	 * @param _currpage
	 * @param subject
	 * @param modulename
	 * @return
	 * @throws Exception
	 */
	public String flexGetBillDefiVOs(String applicationid,int _currpage,String subject,String modulename) throws Exception{
		StringBuffer sb = new StringBuffer();
		ParamsTable params = new ParamsTable();
		params.setParameter("s_module.application.id", applicationid);
		params.setParameter("sm_subject", subject);
		params.setParameter("sm_module.name", modulename);
		params.setParameter("_currpage", _currpage);
		params.setParameter("_pagelines", 10);
		BillDefiProcess BillDefiProcess = (BillDefiProcess)ProcessFactory.createProcess(BillDefiProcess.class);
		DataPackage<BillDefiVO> billDefiVOs = BillDefiProcess.doQuery(params);
		if(billDefiVOs!= null && billDefiVOs.getRowCount()>0){
			sb.append("{\"billDefiVOs\":[");
			for (Iterator<BillDefiVO> iterator = billDefiVOs.datas.iterator(); iterator.hasNext();) {
				BillDefiVO billDefiVO = iterator.next();
				sb.append("{\"subject\":\"");
				sb.append(billDefiVO.getSubject());
				sb.append("\",\"id\":\"");
				sb.append(billDefiVO.getId());
				sb.append("\",\"modulename\":\"");
				sb.append(billDefiVO.getModule().getName());
				sb.append("\"},");
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("],\"totalPages\":\""+getTotalPages(billDefiVOs.getRowCount(),10)+"\"}");
		}
		return sb.toString();
	}
	
	/**
	 * flex流程编辑器通过Flex的RemoteObject获取表单列表
	 * @param applicationid
	 * @param _currpage
	 * @param subject
	 * @param modulename
	 * @return
	 * @throws Exception
	 */
	public String flexGetForms(String applicationid,int _currpage,String subject,String modulename) throws Exception{
		StringBuffer sb = new StringBuffer();
		ParamsTable params = new ParamsTable();
		params.setParameter("s_applicationid", applicationid);
		params.setParameter("sm_name", subject);
		params.setParameter("sm_module.name", modulename);
		params.setParameter("_currpage", _currpage);
		params.setParameter("_pagelines", 10);
		FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		DataPackage<Form> forms = fp.doQuery(params);
		if(forms!=null && forms.rowCount>0){
			sb.append("{\"forms\":[");
			for (Iterator<Form> iterator = forms.datas.iterator(); iterator.hasNext();) {
				Form form = iterator.next();
				sb.append("{\"name\":\"");
				sb.append(form.getName());
				sb.append("\",\"id\":\"");
				sb.append(form.getId());
				sb.append("\",\"modulename\":\"");
				sb.append(form.getModule().getName());
				if (form != null && form.getAllFields() != null) {
					sb.append(flexGetFormFields(form,null));
				}else{
					sb.append("\"},");
				}
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("],\"totalPages\":\""+getTotalPages(forms.getRowCount(),10)+"\"}");
		}
		return sb.toString();
	}
	

	/**
	 * flex流程编辑器通过Flex的RemoteObject获取表单列表
	 * @param applicationid
	 * @param _currpage
	 * @param subject
	 * @param modulename
	 * @return
	 * @throws Exception
	 */
	public String flexGetForms(String applicationid,String moduleid) throws Exception{
		StringBuffer sb = new StringBuffer();
		ParamsTable params = new ParamsTable();
		params.setParameter("s_applicationid", applicationid);
		params.setParameter("s_module.id", moduleid);
		FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class); 
		DataPackage<Form> forms = fp.doQuery(params);
		if(forms!=null && forms.rowCount>0){
			sb.append("{\"forms\":[");
			for (Iterator<Form> iterator = forms.datas.iterator(); iterator.hasNext();) {
				Form form = iterator.next();
				sb.append("{\"name\":\"");
				sb.append(form.getName());
				sb.append("\",\"id\":\"");
				sb.append(form.getId());
				sb.append("\",\"modulename\":\"");
				sb.append(form.getModule().getName());
				if (form != null && form.getAllFields() != null) {
					sb.append(flexGetFormFields(form,null));
				}else{
					sb.append("\"},");
				}
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("]}");
		}
		return sb.toString();
	}
	
	/**
	 * flex版流程编辑器通过RemoteObject获取表单列表
	 * @param form
	 * @param formid
	 * @return
	 * @throws Exception
	 */
	public String flexGetFormFields(String formid) throws Exception{
		return flexGetFormFields(null,formid);
	}
	
	/**
	 * flex版流程编辑器通过RemoteObject获取表单列表
	 * @param form
	 * @param formid
	 * @return
	 * @throws Exception
	 */
	public String flexGetFormFields(Form form,String formid) throws Exception{
		StringBuffer sb = new StringBuffer();
		if(form!=null){
			sb.append("\",\"formFields\":[");
		}else{
			FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class); 
			form = (Form)fp.doView(formid);
			sb.append("{\"formFields\":[");
		}
		
		StringBuffer sb1 = new StringBuffer();
		sb1.append("{\"name\":\"--select--\"},");
		for (Iterator<FormField> iter = form.getAllFields().iterator(); iter.hasNext();) {
			FormField field = iter.next();
			sb1.append("{\"name\":\"");
			sb1.append(field.getName());
			sb1.append("\",\"valuetype\":\"");
			sb1.append(field.getFieldtype());
			sb1.append("\"},");
		}
		if(sb1.lastIndexOf(",")!=-1){
			sb1.deleteCharAt(sb1.lastIndexOf(","));
		}
		if(formid==null){
			sb.append(sb1.toString()+"]},");
		}else{
			sb.append(sb1.toString()+"]}");
		}
		return sb.toString();
	}
	
	/**
	 * Flex版流程编辑器Flex通过RemoteObject调用表单列表
	 * 
	 * @param moduleId
	 * @param applicationid
	 * @return
	 */
	public String flexGetFormListByModule(String moduleId,String applicationid) throws Exception{
		StringBuffer sb=new StringBuffer();
		try{
		FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Collection<Form> col = fp.getFormsByModule(moduleId, applicationid);
		if(col!=null && col.size()>0){
			sb.append("{\"FormList\":[");
			for (Iterator<Form> ite = col.iterator(); ite.hasNext();) {
				Form form = ite.next();
				sb.append("{\"formId\":\""+form.getId()+"\",\"formName\":\""+form.getName()+"\"");
				Collection<FormField> colls = form.getFields();
				if(colls!=null && colls.size()>0){
					sb.append(",\"FormFieldList\":[");
					StringBuffer sb1 = new StringBuffer();
					sb1.append("{\"fieldId\":\"all_data_fields\",\"fieldName\":\"all_data_fields\",");
					sb1.append("\"fieldReadOnly\":false,\"fieldHidden\":false,\"fieldModify\":false},");
					for (Iterator<FormField> iter = colls.iterator(); iter.hasNext();) {
						FormField field=iter.next();
						sb1.append("{\"fieldId\":\""+field.getId()+"\",\"fieldName\":\""+field.getName()+"\",");
						sb1.append("\"fieldReadOnly\":false,\"fieldHidden\":false,\"fieldModify\":true,");
						sb1.append("\"readOnly\":\"@"+field.getName()+";\",\"hidden\":\"\",\"modify\":\"$"+field.getName()+";\"},");
					}
					if(sb1.lastIndexOf(",")!=-1){
						sb1.deleteCharAt(sb1.lastIndexOf(","));
					}
					sb.append(sb1.toString()+"]},");
				}else{
					sb.append("},");
				}
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("]}");
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();	
//		{'aa':[{'name':'1','departName':'2'},{'name':'11','departName':'22'}]}
	}
	
	
	public String flexGetRoleListByModule(String applicationid) throws Exception{
		StringBuffer sb=new StringBuffer();
		RoleProcess process = (RoleProcess) ProcessFactory.createProcess(RoleProcess.class);
		Collection<RoleVO> roles = process.getRolesByApplication(applicationid);
		if(roles!=null && roles.size()>0){
			sb.append("[");
			for (Iterator<RoleVO> ite = roles.iterator(); ite.hasNext();) {
				RoleVO role = ite.next();
				sb.append("{\"role\":\"R"+role.getId()+"|"+role.getName()+";\",\"roleName\":\""+role.getName()+"\",\"roleNamesSelect\":false}," );
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("]");
		
		}
		return sb.toString();
	}
	
	//总的页数
	protected int getTotalPages(int totalRows,int pageSize){
		int totalPagesTemp = totalRows/pageSize;
		int mod = totalRows%pageSize;
		if(mod>0){
			totalPagesTemp+=1; 
		}
		return totalPagesTemp;
	}
	
	//
	public String flexGetCreateSummaryOptions(String application) throws Exception {
		StringBuffer sb = new StringBuffer();
		SummaryCfgProcess mp = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
		ParamsTable params = new ParamsTable();
		params.setParameter("i_scope", SummaryCfgVO.SCOPE_NOTIFY);
		Collection<SummaryCfgVO> summaryCfgs = mp.doSimpleQuery(params, application);
		if (summaryCfgs != null && summaryCfgs.size() > 0) {
			sb.append("[");
			sb.append("{\"id\":\"\",\"title\":\"--select--\"}," );
			for (Iterator<SummaryCfgVO> iterator = summaryCfgs.iterator(); iterator.hasNext();) {
				SummaryCfgVO vo = iterator.next();
				sb.append("{\"id\":\""+vo.getId()+"\",\"title\":\""+vo.getTitle()+"\"}," );
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("]");
		}
		return sb.toString();

	}
}
