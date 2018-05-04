package OLink.bpm.core.wizard.util;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.wizard.ejb.WizardVO;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import eWAP.core.Tools;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * WizardUtil class.
 * 
 * @author zhuxuehong,Sam
 * @since JDK1.4
 */

public class WizardUtil {

	public final String TEMPLATE_DIRECTORY = "/core/wizard/template";
	private static final Logger log = Logger.getLogger(WizardUtil.class);

	/**
	 * 通过在页面中组装起的JSON字符串，转化成数据模型，提供给FreeMarker进行处理
	 * 
	 * @SuppressWarnings JSON lib不支持泛型
	 * @param f_fieldsDescription
	 *            表单中字段的内容
	 * @param f_style
	 *            表单类型
	 * @param contextBasePath
	 *            系统的上下文路径
	 * @return 最终生成的表单内容
	 */
	@SuppressWarnings("unchecked")
	public String getF_TemplateContext(String f_fieldsDescription, String f_style, String contextBasePath) {

		Configuration configuration = new Configuration();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> content = new HashMap<String, Object>();

		try {
			configuration.setDirectoryForTemplateLoading(new File(getTemplateDirectory(contextBasePath)));
			Template template = configuration.getTemplate("step2_form_style.ftl");
			JSONArray obj = JSONArray.fromObject(f_fieldsDescription);
			Collection<MorphDynaBean> collection = JSONArray.toCollection(obj);
			Iterator<MorphDynaBean> iterator = collection.iterator();
			while (iterator.hasNext()) {
				MorphDynaBean oj = iterator.next();

				Map<String, Object> f_templatecontext = new HashMap<String, Object>();

				f_templatecontext.put("fieldname", oj.get("fieldname"));
				f_templatecontext.put("fieldtype", oj.get("fieldtype"));
				f_templatecontext.put("fieldlength", oj.get("fieldlength"));
				f_templatecontext.put("fieldoption", oj.get("fieldoption"));
				f_templatecontext.put("fieldformat", oj.get("fieldformat"));
				f_templatecontext.put("fieldid", oj.get("fieldid"));
				Object fieldtype = oj.get("fieldtype");
				if("06".equals(fieldtype)){
					f_templatecontext.put("numberpattern", oj.get("fieldformat"));
				}else if("01".equals(fieldtype)){
					f_templatecontext.put("datepattern", oj.get("fieldformat"));
				}

				list.add(f_templatecontext);

				content.put("f_templatecontext", list);
			}
			content.put("f_style", f_style);
			StringWriter writer = new StringWriter();

			template.process(content, writer);

			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 通过在页面中组合起的JSON数据，转换成数据模型，提供给FreeMarker进行处理
	 * 
	 * @SuppressWarnings JSONArray.toCollection不支持泛型
	 * @param contextBasePath
	 *            系统的上下文路径
	 * @param wizardVO
	 *            向导的VO
	 * @param formName
	 *            表单名字
	 * @return 视图中filter的内容；出错时返回空字符串
	 */
	@SuppressWarnings("unchecked")
	public String getV_filter(String contextBasePath, WizardVO wizardVO, String formName) {

		Configuration configuration = new Configuration();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> content = new HashMap<String, Object>();

		try {
			configuration.setDirectoryForTemplateLoading(new File(getTemplateDirectory(contextBasePath)));
			Template template = configuration.getTemplate("step5_view_filter.ftl");
			JSONArray array = JSONArray.fromObject(wizardVO.getV_filter());

			JSONArray arr = array.getJSONArray(0);
			String formId = array.getString(1);

			Collection<MorphDynaBean> collection = JSONArray.toCollection(arr);

			Iterator<MorphDynaBean> iterator = collection.iterator();

			while (iterator.hasNext()) {
				MorphDynaBean bean = iterator.next();
				Map<String, Object> v_filterCode = new HashMap<String, Object>();

				v_filterCode.put("fieldtype", bean.get("fieldtype"));
				v_filterCode.put("fieldname", bean.get("fieldname"));

				list.add(v_filterCode);

				content.put("fields", list);
			}

			content.put("formid", formId);
			content.put("filterpendding", Boolean.valueOf(wizardVO.isPending()));
			content.put("formname", formName);
			StringWriter writer = new StringWriter();
			template.process(content, writer);

			return writer.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 根据应用的根目录及从视图中获取的数据，返回流程的内容
	 * 
	 * @param contextBasePath
	 *            应用的根目录
	 * @param w_content
	 *            视图中获取的数据
	 * @return 流程内容
	 */
	public String getW_content(String contextBasePath, String w_content) {
		Configuration configuration = new Configuration();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, List<Map<String, String>>> content = new HashMap<String, List<Map<String, String>>>();

		try {
			JSONArray array = JSONArray.fromObject(w_content);

			String names = array.getString(0);
			String roles = array.getString(1);
			String type = array.getString(2);
			String nodeIds = array.getString(3);

			configuration.setDirectoryForTemplateLoading(new File(getTemplateDirectory(contextBasePath)));
			Template template = configuration.getTemplate(getWorkflowTemplate(type));

			String[] name = names.split(";");
			String[] role = roles.split(" ");
			String[] nodeId = null;
			if (nodeIds.length() > 0)
				nodeId = nodeIds.split(";");

			for (int i = 0; i < name.length; i++) {
				Map<String, String> nodeInfo = new HashMap<String, String>();

				nodeInfo.put("nodeName", name[i]);
				nodeInfo.put("nodeRole", role[i]);
				if (nodeIds.length() > 0)
					nodeInfo.put("nodeId", nodeId[i]);

				list.add(nodeInfo);

				content.put("nodeInfo", list);
			}

			StringWriter writer = new StringWriter();

			template.process(content, writer);

			return writer.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 根据searchForm的数据和应用的根路径，获取searchForm的所有内容
	 * 
	 * @SuppressWarnings JSONArray.toCollection不支持泛型
	 * @param v_searchForm
	 * @param contextBasePath
	 * @return searchForm上下文
	 */
	@SuppressWarnings("unchecked")
	public String getV_searchForm(String v_searchForm, String contextBasePath) {
		Configuration configuration = new Configuration();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, List<Map<String, Object>>> content = new HashMap<String, List<Map<String, Object>>>();

		try {
			configuration.setDirectoryForTemplateLoading(new File(getTemplateDirectory(contextBasePath)));
			Template template = configuration.getTemplate("step5_view_searchform.ftl");
			JSONArray array = JSONArray.fromObject(v_searchForm);
			Collection<MorphDynaBean> collection = JSONArray.toCollection(array);
			Iterator<MorphDynaBean> it = collection.iterator();

			while (it.hasNext()) {
				MorphDynaBean bean = it.next();

				Map<String, Object> v_filter = new HashMap<String, Object>();

				v_filter.put("fieldtype", bean.get("fieldtype"));
				v_filter.put("fieldid", bean.get("fieldid"));
				v_filter.put("fieldname", bean.get("fieldname"));

				list.add(v_filter);

				content.put("v_searchForm", list);
			}
			StringWriter writer = new StringWriter();
			template.process(content, writer);

			return writer.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 根据流程的类型，获得相关的模板
	 * 
	 * @param workflowType
	 *            流程类型
	 * @return 类型对应的模板
	 */
	public String getWorkflowTemplate(String workflowType) {
		StringBuffer templatePath = new StringBuffer();
		templatePath.append("step3_workflow_").append(workflowType).append(".ftl");
		return templatePath.toString();
	}

	/**
	 * 根据应用的根路径获取模板目录
	 * 
	 * @param contextBasePath
	 * @return 存放模板的目录
	 */
	public String getTemplateDirectory(String contextBasePath) {
		return contextBasePath.concat(TEMPLATE_DIRECTORY);
	}

	public String getUpdatedTemplateContext(WizardVO vo, String contextBasePath) {
		Configuration configuration = new Configuration();
		try {
			configuration.setDirectoryForTemplateLoading(new File(getTemplateDirectory(contextBasePath)));
			Template template = configuration.getTemplate("step2_form_style_md.ftl");
			Map<String, String> map = new HashMap<String, String>();
			map.put("viewid", vo.getF_subForm_viewid());
			map.put("moduleid", vo.getModuleid());
			map.put("includefieldid", Tools.getSequence());
			map.put("subName", vo.getF_name_sub());

			StringWriter writer = new StringWriter();

			template.process(map, writer);

			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String validateForm(String formName, String mainFormName, String application) throws Exception {
		FormProcess process = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

		Form form = process.doViewByFormName(formName, application);
		String regex = "^[a-zA-Z]{1}\\w*$";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(formName);

		String dbType = DbTypeUtil.getDBType(application);
		if (dbType != null && dbType.equals(DbTypeUtil.DBTYPE_DB2)) {
			if (!matcher.find()) {
				return "db2";
			}
		}

		if (form == null) {
			if (mainFormName != null) {
				if (formName.equals(mainFormName)) {
					return "yes";
				}
			}
			return "no";
		}

		return "yes";
	}

	public static void main(String[] args) {
		String w_content = "['1;2;3','(R01b87b98-68d1-9620-9054-15b2ccd1308d|员工;) (R01b87b98-a31e-3540-9bdd-1ec2928ffec0|经理;) (R01b87b98-cc31-0a20-b5ce-8a120f1a2245|老总;)','01','1249293724500;1249293724515;1249293724531;']";
		String basePath = "E:\\workspace3.4\\obpm\\src\\main\\webapp\\";

		WizardUtil util = new WizardUtil();
		log.info(util.getW_content(basePath, w_content));
	}
}
