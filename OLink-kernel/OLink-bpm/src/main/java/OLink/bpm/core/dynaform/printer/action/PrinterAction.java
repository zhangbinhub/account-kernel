package OLink.bpm.core.dynaform.printer.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.printer.ejb.Printer;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.sysconfig.ejb.CheckoutConfig;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.printer.ejb.PrinterProcess;
import org.apache.log4j.Logger;

import OLink.bpm.util.StringUtil;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @author Happy
 * 
 */
public class PrinterAction extends BaseAction<Printer> {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(PrinterAction.class);

	private static final long serialVersionUID = -5834805743250764407L;
	private String fieldList;
	private String subViewList;
	private String _formid;
	private String _flowid;
	private String _docid;
	// private String _moduleid;
	private String reportData;
	private String checkoutConfig;

	public String getFieldList() {
		return fieldList;
	}

	public void setFieldList(String fieldList) {
		this.fieldList = fieldList;
	}

	public String getSubViewList() {
		return subViewList;
	}

	public void setSubViewList(String subViewList) {
		this.subViewList = subViewList;
	}

	public String get_formid() {
		return _formid;
	}

	public void set_formid(String formid) {
		_formid = formid;
	}

	public String get_flowid() {
		return _flowid;
	}

	public void set_flowid(String flowid) {
		_flowid = flowid;
	}

	public String get_docid() {
		return _docid;
	}

	public void set_docid(String docid) {
		_docid = docid;
	}

	public String getCheckoutConfig() {
		PropertyUtil.reload("checkout");
		String _checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
		return _checkoutConfig;
	}

	public void setCheckoutConfig(String checkoutConfig) {
		this.checkoutConfig = checkoutConfig;
	}
	
	public String get_moduleid() {
		Printer printer = (Printer) getContent();
		if (printer.getModule() != null) {
			return printer.getModule().getId();
		}
		return null;
	}

	public void set_moduleid(String moduleid) {
		Printer printer = (Printer) getContent();
		if (moduleid != null) {
			ModuleProcess mp;
			try {
				mp = (ModuleProcess) ProcessFactory.createProcess((ModuleProcess.class));
				ModuleVO module = (ModuleVO) mp.doView(moduleid);
				printer.setModule(module);
			} catch (Exception e) {
			}
		}
	}
	public String doEdit() {
		try {
			PropertyUtil.reload("checkout");
			String _checkoutConfig = PropertyUtil.get(CheckoutConfig.INVOCATION);
			Map<?, ?> params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			Printer printer = (Printer) process.doView(id);
			if(_checkoutConfig.equals("true") && printer.isCheckout() && !printer.getCheckoutHandler().equals(super.getUser().getId())){
				SuperUserProcess sp = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
				SuperUserVO speruser = (SuperUserVO) sp.doView(printer.getCheckoutHandler());
				addFieldError("", "此打印模板已经被"+speruser.getName()+"签出，您目前没有修改的权限！");
			}
			setContent(printer);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 返回web 用户对象
	 * 
	 * @SuppressWarnings webwork 不支持泛型
	 * @return web user.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public WebUser getFrontUser() throws Exception {
		Map<String, Object> session = getContext().getSession();
		WebUser user = null;
		if (session == null || session.get(Web.SESSION_ATTRIBUTE_FRONT_USER) == null) {
			UserVO vo = new UserVO();
			vo.getId();
			vo.setName("GUEST");
			vo.setLoginno("guest");
			vo.setLoginpwd("");
			vo.setRoles(null);
			vo.setEmail("");
			// vo.setLanguageType(1);
			user = new WebUser(vo);
		} else {
			user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_FRONT_USER);
		}
		return user;
	}

	public void setReportData(String reportData) {
		this.reportData = reportData;
	}

	public String getReportData() {
		return reportData;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public PrinterAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(PrinterProcess.class), new Printer());
	}

	
	public String doSave() {
	 try {
		 Printer printer = (Printer) getContent();
		 if(StringUtil.isBlank(printer.getId())){
			 printer.setCheckout(true);
			 printer.setCheckoutHandler(getUser().getId());
			}
		 
		 ParamsTable params = new ParamsTable();
		 params.setParameter("t_name", printer.getName());
		 Collection<Printer> collection = process.doSimpleQuery(params);
		 if(collection.size()>0){
			 for (Iterator<Printer> iterator = collection.iterator(); iterator.hasNext();) {
				Printer printer2 = iterator.next();
				if(printer2.getModule().getId().equals(printer.getModule().getId()) && !printer2.getId().equals(printer.getId())){
					addFieldError("1","{*[core.form.exist]*}");
					return INPUT;
				}
			}
		 }
	 }catch (Exception e) {
		 addFieldError("1", e.getMessage());
		 return INPUT;
	 }
	 	return super.doSave();
	}
	
	
	/**
	 * 签入
	 * @return
	 * @throws Exception
	 */
	public String doCheckin() throws Exception {
		try {
			Printer printer = (Printer) (this.getContent());
			process.doCheckin(printer.getId(), getUser());
			printer.setCheckout(false);
			printer.setCheckoutHandler("");
			this.addActionMessage("{*[core.dynaform.form.success.checkin]*}");
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	/**
	 * 签出
	 * @return
	 * @throws Exception
	 */
	public String doCheckout() throws Exception {
		try {
			Printer printer = (Printer) (this.getContent());
			process.doCheckout(printer.getId(), getUser());
			printer.setCheckout(true);
			printer.setCheckoutHandler(super.getUser().getId());
			this.addActionMessage("{*[core.dynaform.form.success.checkout]*}");
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	public String doGetFields() {
		PrinterProcess printerProcess = (PrinterProcess) process;
		this.fieldList = printerProcess.getFields(get_formid());
		return SUCCESS;
	}

	public String doGetSubViews() {
		IRunner runner = null;
		PrinterProcess printerProcess = (PrinterProcess) process;
		try {
			FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Form form = (Form) fp.doView(get_formid());
			if (form != null) {
				runner = JavaScriptFactory.getInstance(ServletActionContext.getRequest().getSession().getId(), form
						.getApplicationid());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.subViewList = printerProcess.getSubViews(get_formid(), runner);
		return SUCCESS;
	}

	public String doFlexPrint() throws Exception {
		return SUCCESS;
	}

	public String doPrint() {
		ParamsTable params = getParams();
		try {
			this.reportData = ((PrinterProcess) process).getReportData(this.getContent().getId(), this.get_formid(),
					this.get_docid(), this.get_flowid(), this.getFrontUser(), params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

}
