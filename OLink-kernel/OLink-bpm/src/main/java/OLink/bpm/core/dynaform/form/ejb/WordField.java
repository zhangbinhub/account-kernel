// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\form\\ejb\\InputField.java

package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.macro.runner.AbstractRunner;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.PermissionType;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.sequence.Sequence;
import OLink.bpm.base.action.ParamsTable;

import eWAP.core.Tools;

public class WordField extends FormField implements ValueStoreField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9221526684588846664L;

	private String openType;

	private String signatureScript;

	/**
	 * 
	 * Form模版的WordField组件内容结合Document中的ITEM存放的值,返回重定义后的html
	 * 
	 * @param doc
	 *            文档对象
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html为Form模版的WordField组件内容结合Document中的ITEM存放的值
	 * 
	 */
	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		StringBuffer html = new StringBuffer();
		Item item = null;
		boolean saveable = true;
		int displayType = getDisplayType(doc, runner, webUser);
		boolean isSignature = runSignatureScript(runner);
		ParamsTable params = new ParamsTable();
		params.setParameter("saveable", saveable);
		params.setParameter("displayType", displayType);
		params.setParameter("isSignature", isSignature);
		if (webUser.isDeveloper() || webUser.isDomainAdmin()
				|| webUser.isSuperAdmin()) {
			saveable = false;
		}
		if (displayType == PermissionType.HIDDEN) {
			return this.getHiddenValue();
		} else {
			if (doc != null) {
				item = doc.findItem(this.getName());
				if (displayType == PermissionType.DISABLED
						|| displayType == PermissionType.READONLY) {
					saveable = false;
				}

				if (item == null) {
					item = new Item();
					try {
						item.setId(Tools.getSequence());
					} catch (Exception e) {
					}
				}
				if (WebUser.TYPE_BG_USER.equals(webUser.getType()))
					html = toPreviewHtml(doc, displayType);
				else
					html = toHtml(doc, params, getOpenType());
			}
		}
		return html.toString();
	}

	public StringBuffer toPreviewHtml(Document doc, int displayType)
			throws Exception {
		StringBuffer html = new StringBuffer();

		if (getOpenType() != null
				&& ("2".equals(getOpenType()) || "3".equals(getOpenType()))) {
			html.append("<button class='button-class'");
			html.append(" onclick=\"preWordDialog('" + getId() + "', '"
					+ this.getName() + "', 'WordControl', '"
					+ Sequence.getSequenceTimes() + "', '" + this.getName()
					+ "'");
			html.append(", " + getOpenType() + ", " + displayType + ")\"");
			html.append(">");

			html
					.append(" <img src='"
							+ this.getContextPath(doc)
							+ "/core/dynaform/form/formeditor/buttonimage/v1/word.gif'></img>");
			html.append("</button>");
			html
					.append("<font size=2 color='red'>" + getDiscript()
							+ "</font>");
		} else {// 生成一个iframe
			html.append("<iframe id='" + getId() + "' src='");
			html.append(this.getContextPath(doc)
					+ "/core/dynaform/document/newword.action?id=" + getId());
			html.append("&_docid=" + Sequence.getSequenceTimes());
			html.append("&filename=" + this.getName() + "&_type=word");
			html.append("&_isEdit=1");
			html.append("&saveable=false" + "'");
			html.append(" name='word'");
			html
					.append(" frameborder='0' width='100%' height='645px' scrolling='no' style='overflow:visible;z-index:-1px;' type='word'");
			html.append("></iframe>");

		}
		return html;
	}

	/**
	 * 
	 * Form模版的WordField内容结合Document中的ITEM存放的word文档的名,返回重定义后的html，
	 * 
	 * @param doc
	 * @param type
	 *            显示方式(1:默认(在页面以iframe的方式显示) 2:弹出窗口 3: 弹出层)
	 * @param doc
	 *            (Document)文档对象
	 * @param isPre
	 *            是否为预览模式
	 * @see ParamsTable#params
	 * @see AbstractRunner#run(String, String)
	 * @return 重定义后的html
	 * 
	 */
	public StringBuffer toHtml(Document doc, ParamsTable params, String type)
			throws Exception {
		StringBuffer html = new StringBuffer();
		if (getOpenType() != null
				&& (getOpenType().equals("2") || getOpenType().equals("3"))) {
			// html.append(addScript(doc, saveable, displayType));
			html.append("<input type='hidden' "); // 保存值
			html.append(" name='" + this.getName() + "'");
			html.append(" id='" + getName() + "'");
			String wordid=String.valueOf(Sequence.getSequenceTimes());
			if (doc.getItemValueAsString(this.getName()) != null&& !doc.getItemValueAsString(this.getName()).equals("")){
				html.append(" value='"+ doc.getItemValueAsString(this.getName()) + "'");
			}else{
				html.append(" value='"+wordid+"'");
			}
			html.append("/>");

			html.append("<input type='hidden' ");
			html.append(" id='" + getId() + "'");
			String value;
			if (doc.getItemValueAsString(this.getName()) == null
					|| doc.getItemValueAsString(this.getName()).equals("")) {// 否是是一个新的文档
				value = wordid;
			} else {
				value = doc.getItemValueAsString(this.getName());
			}
			html.append(" value='" + value + "'");
			html.append("/>");
			// 按钮事件
			html.append("<button class='button-class'");
			// html.append(" onclick=\"showWordControl();\"");
			html.append(" onclick=\"");

			// addScript方法，同一表单两个Word控件不同弹出方式
			html
					.append("showWordDialog('{*[Show]*} {*[Word]*}', 'WordControl', '"
							+ value + "',");
			if (doc.getItemValueAsString(this.getName()) != null
					&& !doc.getItemValueAsString(this.getName()).equals("")) {
				html.append("'" + doc.getItemValueAsString(this.getName())
						+ "'");
			} else {
				html.append("'" + doc.getId() + "'");
			}
			html.append(", '" + getName() + "', " + getOpenType() + ", "
					+ params.getParameter("displayType") + ", ");
			// 是否允许编辑
			if (params.getParameterAsBoolean("saveable")) {
				html.append("true, ");
			} else {
				html.append("false, ");
			}
			// 是否允许签章
			if (params.getParameterAsBoolean("isSignature")) {
				html.append("true)\"");
			} else {
				html.append("false)\"");
			}
			// end

			html.append(">");
			html
					.append(" <img src='"
							+ this.getContextPath(doc)
							+ "/core/dynaform/form/formeditor/buttonimage/v1/word.gif'></img>");
			html.append("</button>");
			html
					.append("<font size=2 color='red'>" + getDiscript()
							+ "</font>");
		} else {// 生成一个iframe
			html.append("<input type='hidden' ");
			html.append(" name='" + this.getName() + "'");
			html.append(" id='" + getId() + "'");
			if (doc.getItemValueAsString(this.getName()) != null
					&& !doc.getItemValueAsString(this.getName()).equals(""))
				html.append(" value='"
						+ doc.getItemValueAsString(this.getName()) + "'");
			html.append(">");
			html.append("<iframe id='" + getId() + "' src='");
			html.append(this.getContextPath(doc)
					+ "/portal/dynaform/document/newword.action?id=" + getId());
			if (doc.getItemValueAsString(this.getName()) != null
					&& !doc.getItemValueAsString(this.getName()).equals(""))
				html.append("&_docid="
						+ doc.getItemValueAsString(this.getName()));
			else {
				html.append("&_docid=" + Sequence.getSequenceTimes());
			}
			html.append("&filename=" + this.getName() + "&_type=word");
			html.append("&_isEdit=" + params.getParameter("displayType"));
			html.append("&isSignature=" + params.getParameter("isSignature")
					+ "'");
			html.append(" name='word'");
			html
					.append(" frameborder='0' width='100%' height='645px' scrolling='no' style='overflow:visible;z-index:-1px;' type='word'");
			html.append("></iframe>");

		}
		return html;
	}

	/**
	 * 生成SCRIPT的方法
	 * 
	 * @param doc
	 *            文档对象
	 * @param displayType
	 *            类型
	 * @return 生成的function
	 * @throws Exception
	 */
	public String addScript(Document doc, boolean saveable, int displayType)
			throws Exception {
		StringBuffer html = new StringBuffer();
		html.append("<script>");
		html.append("function showWordControl(){");
		html
				.append("var id =document.getElementById('" + getId()
						+ "').value;");
		html.append(" var fileName = document.getElementById(\"" + getName()
				+ "\").value;");
		html
				.append("showWordDialog('{*[Show]*} {*[Word]*}', 'WordControl', id, ");
		if (doc.getItemValueAsString(this.getName()) != null
				&& !doc.getItemValueAsString(this.getName()).equals("")) {
			html.append("'" + doc.getItemValueAsString(this.getName()) + "'");
		} else {
			html.append("'" + doc.getId() + "'");
		}
		html.append(", '" + getName() + "', " + getOpenType() + ", "
				+ displayType + ", ");
		if (saveable) {
			html.append("true)");
		} else {
			html.append("false)");
		}
		html.append("}");
		html.append("</script>");

		return html.toString();
	}

	/**
	 * 返回模板描述文本
	 * 
	 * @return java.lang.String
	 * @roseuid 41E7917A033F
	 */
	public String toTemplate() {
		StringBuffer template = new StringBuffer();
		template.append("<input type='text'");
		template.append(" className='" + this.getClass().getName() + "'");
		template.append(" id='" + getId() + "'");
		template.append(" name='" + getName() + "'");
		template.append(" formid='" + getFormid() + "'");
		template.append(" discript='" + getDiscript() + "'");
		template.append(" hiddenScript='" + getHiddenScript() + "'");
		template.append(" hiddenPrintScript='" + getHiddenPrintScript() + "'");
		template.append(" opentype='" + getOpenType() + "'");
		template.append(">");
		return template.toString();
	}

	/**
	 * 空实现
	 */
	public void recalculate(IRunner runner, Document doc, WebUser webUser)
			throws Exception {
	}

	/**
	 * 空实现
	 */
	public Object runValueScript(IRunner runner, Document doc) throws Exception {
		return null;
	}

	/**
	 * 空实现
	 */
	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		if (doc != null) {
			int displayType = getPrintDisplayType(doc, runner, webUser);

			if (displayType == PermissionType.HIDDEN) {
				return this.getPrintHiddenValue();
			}
		}
		// return printHiddenElement(doc);
		return "";
	}

	/**
	 * 2.6新增
	 * 
	 * @param runner
	 * @return
	 * @throws Exception
	 */
	public boolean runSignatureScript(IRunner runner) throws Exception {
		return runBooleanScript(runner, "signatureScript", getSignatureScript());
	}

	/**
	 * 空实现
	 */
	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return null;
	}

	/**
	 * 获取打开视图框的类型(1:弹出窗口,3:弹出层)
	 * 
	 * @return 打开视图框的类型 (1:弹出窗口,3:弹出层)
	 */
	public String getOpenType() {
		return openType;
	}

	/**
	 * 设置打开视图框的类型
	 * 
	 * @param openType
	 *            打开视图框的类型
	 */
	public void setOpenType(String openType) {
		this.openType = openType;
	}

	/**
	 * 空实现
	 */
	public String toGridHtmlText(Document doc, IRunner runner, WebUser webUser)
			throws Exception {
		return null;
	}

	public String getSignatureScript() {
		return signatureScript;
	}

	public void setSignatureScript(String signatureScript) {
		this.signatureScript = signatureScript;
	}

}
