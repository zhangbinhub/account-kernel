package OLink.bpm.core.dynaform.printer.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import org.apache.log4j.Logger;

/**
 * 动态打印配置模板
 * @author Happy
 */
public class Printer extends ValueObject {
	
	private static final long serialVersionUID = 8300619396983167396L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(Printer.class);
	
	private String id;
	
	private String name;
	
	private String description;
	
	private String template;
	
	private String relatedForm;
	
	private ModuleVO module;
	/**
	 * 是否签出
	 */
	private  boolean checkout = false;
	
	/**
	 * 签出者
	 */
	private String checkoutHandler;
	
	/**
	 * 是否被签出
	 * @return
	 */
	public boolean isCheckout() {
		return checkout;
	}

	/**
	 * 设置是否签出
	 * @param checkout
	 */
	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	/**
	 * 获取签出者
	 * @return
	 */
	public String getCheckoutHandler() {
		return checkoutHandler;
	}

	/**
	 * 设置签出者
	 * @param checkoutHandler
	 */
	public void setCheckoutHandler(String checkoutHandler) {
		this.checkoutHandler = checkoutHandler;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ModuleVO getModule() {
		return module;
	}
	public void setModule(ModuleVO module) {
		this.module = module;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getRelatedForm() {
		return relatedForm;
	}
	public void setRelatedForm(String relatedForm) {
		this.relatedForm = relatedForm;
	}

}
