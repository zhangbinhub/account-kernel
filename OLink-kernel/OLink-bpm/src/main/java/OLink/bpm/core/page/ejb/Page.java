package OLink.bpm.core.page.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Form;

/**
 * 
 * @hibernate.joined-subclass table="T_PAGE" dynamic-insert = "true"
 *                            dynamic-update = "true"
 * @hibernate.joined-subclass-key column="ID"
 * 
 */
public class Page extends Form {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean defHomePage;

	private String roles;

	private String roleNames;

	public String getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String roleNames) {
		this.roleNames = roleNames;
	}

	/**
	 * @hibernate.property column="ROLES"
	 */
	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	/**
	 * @hibernate.property column="DEFHOMEPAGE"
	 */

	

	/**
	 * Form模版内容结合Document中的ITEM存放的值,返回字符串为重定义后的html。
	 * 返回重定义后的html通过强化HTML标签及语法，表达Document的布局、元素、item值等。 此还设置Document的Field
	 * 的读写权限.
	 * 
	 * @param doc
	 *            文档对象
	 * @param params
	 *            参数
	 * @param isEditMode
	 *            标识文档是否为可编辑(true为可编辑,false为不可编辑)
	 * @param user
	 *            用户
	 * @param errors
	 *            错误信息
	 * @param flowVO
	 *            BillDefiVO(流程定义)对象
	 * @param bRefreshScript
	 * 
	 * @return Form模版内容结合Document的ITEM存放的值为重定义后的html
	 * @throws Exception
	 */
	public String toHtml(ParamsTable params, WebUser user) throws Exception {
		Document doc = createDocument(params, user);
		doc.setId("HOME_PAGE_ID");
		MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, user);

		return toHtml(doc, params, user);
	}

	public String toHtml(Document doc, ParamsTable params, WebUser user) throws Exception {
		StringBuffer buffer = new StringBuffer();
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), this.getApplicationid());

		runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());

		String calctext = toCalctext(doc, runner, user);
		Collection<?> js = compileCalctext(calctext);

		for (Iterator<?> iter = js.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof String) {
				buffer.append(element);
			} else {
				CalculatePart part = (CalculatePart) element;
				StringBuffer label = new StringBuffer();
				label.append("Page(").append(getId()).append(")." + getName()).append(".CalculatePart");
				Object result = runner.run(label.toString(), part.text);
				if (result != null) {
					buffer.append(result);
				}
			}
		}

		buffer.append(addScript(doc, user));
		return buffer.toString();
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public boolean isDefHomePage() {
		return defHomePage;
	}

	public void setDefHomePage(boolean defHomePage) {
		this.defHomePage = defHomePage;
	}

}
