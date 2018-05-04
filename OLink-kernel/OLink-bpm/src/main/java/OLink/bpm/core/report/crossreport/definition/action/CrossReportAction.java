package OLink.bpm.core.report.crossreport.definition.action;

import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.sysconfig.ejb.CheckoutConfig;
import OLink.bpm.constans.Web;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.PropertyUtil;

public class CrossReportAction extends BaseAction<CrossReportVO> {
	
	String domain;
	
	private String checkoutConfig;

	public String getCheckoutConfig() {
		PropertyUtil.reload("checkout");
		String _checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
		return _checkoutConfig;
	}

	public void setCheckoutConfig(String checkoutConfig) {
		this.checkoutConfig = checkoutConfig;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public CrossReportAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CrossReportProcess.class), new CrossReportVO());
	}
	
	//为了兼容旧数据
	public String doList(){
		try {
			this.validateQueryParams();
			
			datas = process.doQuery(getParams(), getUser());
			for (Iterator<CrossReportVO> ite = datas.datas.iterator(); ite.hasNext();){
				CrossReportVO crossReportVO = ite.next();
				if(crossReportVO.getJson()==null && crossReportVO.getType()==null){
					crossReportVO.setType("CrossReport");
					process.doUpdate(crossReportVO);
				}
			}
			datas = process.doQuery(getParams(), getUser());
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	public String doSave() 
	{
		try{
			CrossReportVO vo = (CrossReportVO)this.getContent();
			if(StringUtil.isBlank(vo.getId())){
				vo.setCheckout(true);
				vo.setCheckoutHandler(getUser().getId());
			}
			
			if(!vo.isDisplayRow()){
				vo.setRowCalMethod(null);
			}
			if(!vo.isDisplayCol()){
				vo.setColCalMethod(null);
			}
			if(vo.getDatas()==null){
				vo.setCalculationMethod(null);
			}else if(vo.getDatas().equals("[]")){
				vo.setCalculationMethod(null);
			}
			vo.setFilters("[]");
			vo.setType("CrossReport");
			return super.doSave();
		 } catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		 }
	}
	
	public String doEdit() {
		try {
			PropertyUtil.reload("checkout");
			String _checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
			Map<?, ?> params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			CrossReportVO vo = (CrossReportVO) process.doView(id);
			if(_checkoutConfig.equals("true") && vo.isCheckout() && !vo.getCheckoutHandler().equals(getUser().getId())){
				SuperUserProcess sp = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
				SuperUserVO speruser = (SuperUserVO) sp.doView(vo.getCheckoutHandler());
				addFieldError("", "此报表已经被"+speruser.getName()+"签出，您目前没有修改的权限！");
			}
			setContent(vo);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}
	
	/**
	 * 签出
	 * @return
	 * @throws Exception
	 */
	public String doCheckout() throws Exception {
		try{
			CrossReportVO vo = (CrossReportVO)this.getContent();
			process.doCheckout(vo.getId(), getUser());
			vo.setCheckout(true);
			vo.setCheckoutHandler(getUser().getId());
			setContent(vo);
			this.addActionMessage("{*[core.dynaform.form.success.checkout]*}");
			return SUCCESS;
		 } catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		 }
	}
	
	/**
	 * 签入
	 * @return
	 * @throws Exception
	 */
	public String doCheckin() throws Exception {
		try{
			CrossReportVO vo = (CrossReportVO)this.getContent();
			process.doCheckin(vo.getId(), getUser());
			vo.setCheckout(false);
			vo.setCheckoutHandler("");
			setContent(vo);
			this.addActionMessage("{*[core.dynaform.form.success.checkin]*}");
			return SUCCESS;
		 } catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		 }
	}
	
	private static final long serialVersionUID = 1L;
	
	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}


	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * 用于叛断是否是选择的列不存在
	 * @param str
	 * @param metadatas
	 * @return
	 
	private boolean isExistColumn(String str,Map<String, String> metadatas)
	{
		Collection<Object> cols = JsonUtil.toCollection(str);
		for (Iterator<Object> iterator = cols.iterator(); iterator.hasNext();) {
			String col = (String) iterator.next();
			if(!metadatas.containsKey(col))
			 return false;
		}
		return true;
	}*/
}
