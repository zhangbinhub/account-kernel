package OLink.bpm.core.dynaform.document.ejb;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.user.action.WebUser;
import eWAP.core.Tools;


public class DocumentBuilder {
	private final static Logger log = Logger.getLogger(DocumentBuilder.class);

	private Document doc;

	private Form form;

	private ParamsTable params;

	public DocumentBuilder(ParamsTable params) {
		this.doc = new Document();
		this.params = params;
		doc.set_params(params);
	}

	public DocumentBuilder(Document doc, ParamsTable params) {
		this.doc = doc;
		this.params = params;
		doc.set_params(params);
	}

	public void mergePO() {
		try {
			DocumentProcess proxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,params.getParameterAsString("application"));
			doc.setId(params.getParameterAsString("content.id"));
			proxy.mergePO(doc, null);
		} catch (Exception e) {
			log.warn("Merge document PO failed, the ID is: " + doc.getId());
			e.printStackTrace();
		}
	}

	public void addItems(WebUser user) {
		addItems(user, true);
	}

	public void addItems(WebUser user, boolean calcAll) {
		try {
			getForm().createDocument(doc, params, calcAll, user);
		} catch (Exception e) {
			log.warn("Add Items failed: " + e.getMessage());
		}
	}

	private void setFormName() {
		try {
			String formname = getForm().getName();

			ModuleVO mv = getForm().getModule();

			if (mv != null) {
				formname = mv.getName() + "/" + formname;
				while (mv.getSuperior() != null) {
					mv = mv.getSuperior();
					formname = mv.getName() + "/" + formname;
				}
				formname = mv.getApplication().getName() + "/" + formname;
			}

			doc.setFormname(formname);
		} catch (Exception e) {
			log.warn(e.getMessage());
		}
	}

	/**
	 * 添加临时文档所需要的参数
	 * 
	 * @param user
	 *            创建用户
	 * @throws Exception
	 */
	public void setCreateProperties(WebUser user) {
		try {
			doc.setId(Tools.getSequence());
		} catch (Exception e) {
			doc.setId(Long.toString(new Date().getTime()));
		}

		// 设置是否为子表单
		try {
			if (getForm().getType() == Form.FORM_TYPE_SUBFORM) {
				doc.set_issubdoc(true);
			} else {
				doc.set_issubdoc(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String isnew = params.getParameterAsString("_new");
		if (isnew != null && isnew.equals("true")) {
			doc.set_new(true);
		}

		doc.setAuthor(user);
		doc.setCreated(new Date());
		doc.setIstmp(true);
	}

	/**
	 * 添加正式文档的基本参数
	 * 
	 */
	public void setBaseProperties() {
		doc.setId(params.getParameterAsString("content.id"));
		doc.setSortId(params.getParameterAsString("content.sortId"));
		Long versions = params.getParameterAsLong("content.versions");
		doc.setVersions(versions != null ? versions.intValue() : 0);

		doc.setFlowid(params.getParameterAsString("_flowid"));
	}

	private void setCommonProperties() {
		doc.setParent(params.getParameterAsString("parentid"));
		doc.setApplicationid(params.getParameterAsString("application"));

		try {
			doc.setFormid(getForm().getId());
			setFormName();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetDocument() {
		doc = new Document();
	}

	public void removeItemParams() {
		try {
			Collection<FormField> fields = getForm().getAllFields();
			for (Iterator<FormField> iter = fields.iterator(); iter.hasNext();) {
				FormField field = iter.next();
				params.removeParameter(field.getName());
				doc.removeItem(field.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document getDocument() {
		return doc;
	}

	public Document getBaseDocument(WebUser user) {
		setCommonProperties();
		setBaseProperties();
		addItems(user);

		return doc;
	}

	public Document getMergeDocument(WebUser user) {
		setCommonProperties();
		setBaseProperties();
		mergePO();
		addItems(user);

		return doc;
	}

	public Document getNewDocument(WebUser user) {
		setCommonProperties();
		setCreateProperties(user);
		addItems(user);

		return doc;
	}

	private Form getForm() throws Exception {
		if (form == null) {
			FormProcess fb = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			if (params.getParameterAsString("formid") != null) {
				form = (Form) fb.doView(params.getParameterAsString("formid"));
			}
		}
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
}
