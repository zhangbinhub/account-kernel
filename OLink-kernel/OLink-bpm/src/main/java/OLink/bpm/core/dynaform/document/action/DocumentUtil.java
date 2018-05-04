package OLink.bpm.core.dynaform.document.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.cache.MemoryCacheUtil;
import OLink.bpm.util.json.JsonUtil;

public class DocumentUtil {

	/**
	 * 执行新建
	 * 
	 * @param activityId
	 *            操作ID
	 * @param parameters
	 *            参数
	 * @param request
	 *            请求
	 * @return
	 */
	public String doNew(String activityId, Map<String, Object> parameters, HttpServletRequest request) {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);

			ParamsTable params = ParamsTable.convertHTTP(request);
			params.putAll(parameters);

			View view = getView(params);

			Activity act = view.findActivity(activityId);

			// 1.Create Document
			String formId = view.getRelatedForm();
			Form form = (Form) formProcess.doView(formId);
			Document doc = form.createDocument(params, webUser);

			IRunner runner = getRunner(view, params, webUser);

			// 2.Execute before action script
			doBeforeScript(act, runner);

			// 3. Save Document, get create script
			// dp.doCreate(doc);
			String refreshScript = view.getRowCreateScript(doc, runner, webUser);

			// 4.Execute after action script
			doAfterScript(act, runner);
			MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, webUser);
			return refreshScript;
		} catch (Exception e) {
			e.printStackTrace();
			return "showError('" + e.getMessage() + "');";
		}
	}

	/**
	 * 运行操作前脚本
	 * 
	 * @param act
	 *            操作
	 * @param runner
	 *            脚本执行器
	 * @throws Exception
	 */
	public void doBeforeScript(Activity act, IRunner runner) throws Exception {
		if (act == null) {
			return;
		}

		StringBuffer label = new StringBuffer();
		label.append("Activity Action(").append(act.getId()).append(")." + act.getName()).append("beforeActionScript");
		Object result = runner.run(label.toString(), act.getBeforeActionScript());
		if (result instanceof String && !StringUtil.isBlank((String) result)) {
			throw new Exception((String) result);
		}
	}

	/**
	 * 运行操作后脚本
	 * 
	 * @param act
	 *            操作
	 * @param runner
	 *            脚本执行器
	 * @throws Exception
	 */
	public void doAfterScript(Activity act, IRunner runner) throws Exception {
		if (act == null) {
			return;
		}

		StringBuffer label = new StringBuffer();
		label.append("Activity Action(").append(act.getId()).append(")." + act.getName()).append("afterActionScript");
		Object result = runner.run(label.toString(), act.getBeforeActionScript());
		if (result instanceof String && !StringUtil.isBlank((String) result)) {
			throw new Exception((String) result);
		}
	}

	/**
	 * 执行删除
	 * 
	 * @param activityId
	 * @param selects
	 * @param parameters
	 * @param request
	 * @return
	 */
	public String doRemove(String activityId, String[] selects, Map<String, Object> parameters,
			HttpServletRequest request) {
		try {
			WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);

			ParamsTable params = ParamsTable.convertHTTP(request);
			params.putAll(parameters);
			params.setParameter("_selects", selects);

			View view = getView(params);
			DocumentProcess dp =(DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,view.getApplicationid());

			Activity act = view.findActivity(activityId);
			IRunner runner = getRunner(view, params, webUser);

			doBeforeScript(act, runner);

			dp.doRemove(selects);

			doAfterScript(act, runner);
		} catch (Exception e) {
			e.printStackTrace();
			return "showError('" + e.getMessage() + "');";
		}
		return "";
	}

	/**
	 * 获取脚本执行器
	 * 
	 * @param view
	 * @param params
	 * @param webUser
	 * @return
	 * @throws Exception
	 */
	private IRunner getRunner(View view, ParamsTable params, WebUser webUser) throws Exception {
		Form sForm = view.getSearchForm();
		Document sfDoc = new Document();
		if (sForm != null) {
			sForm.createDocument(params, webUser);
		}

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		runner.initBSFManager(sfDoc, params, webUser, new ArrayList<ValidateMessage>());

		return runner;
	}

	private View getView(ParamsTable params) throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		View view = (View) viewProcess.doView(params.getParameterAsString("_viewid"));

		return view;
	}

	public String doSingleRemove(String id, String application) {
		try {
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,application);
			Document doc = (Document) dp.doView(id);
			if (doc != null) {
				dp.doRemove(id);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "showError('" + e.getMessage() + "');";
		}
		return "";

	}

	public String doRefresh(String dataJSON, Map<String, Object> parameters, HttpServletRequest request) {
		try {
			WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);

			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			ParamsTable params = ParamsTable.convertHTTP(request);
			params.putAll(parameters);
			if (dataJSON != null) {
				params.putAll(JsonUtil.toMap(dataJSON));
			}

			View view = getView(params);
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,view.getApplicationid());

			String formId = view.getRelatedForm();
			Form form = (Form) formProcess.doView(formId);
			String id = params.getParameterAsString("id"); // Document id
			// 从Session中获取Document
			Document doc = (Document) MemoryCacheUtil.getFromPrivateSpace(id, webUser);

			// 从数据库中获取Document
			if (doc == null) {
				doc = (Document) dp.doView(id);
			}
			form.addItems(doc, params);
			form.recalculateDocument(doc, params, false, webUser);

			IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
			runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());

			// 1.Generate row script
			String refreshScript = view.getRowRefreshScript(doc, runner, webUser);

			return refreshScript;
		} catch (Exception e) {
			e.printStackTrace();
			return "showError('" + e.getMessage() + "');";
		}
	}

	/**
	 * @SuppressWarnings JsonUtil.toCollection返回的对象集类型不定
	 * @param activityId
	 * @param datasJSON
	 * @param parameters
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String doSave(String activityId, String datasJSON, Map<String, Object> parameters, HttpServletRequest request) {
		try {
			WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			ParamsTable params = ParamsTable.convertHTTP(request);
			params.putAll(parameters);

			View view = getView(params);
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,view.getApplicationid());
			String formId = view.getRelatedForm();
			Form form = (Form) formProcess.doView(formId);
			Activity act = view.findActivity(activityId);

			Collection<Object> datas = JsonUtil.toCollection(datasJSON);

			for (Iterator<Object> iterator = datas.iterator(); iterator.hasNext();) {
				Map<String, Object> data = (Map<String, Object>) iterator.next();
				params.putAll(data);
				String id = params.getParameterAsString("id");

				// PO, VO整合
				Document doc = (Document) MemoryCacheUtil.getFromPrivateSpace(id, webUser);
				// 从数据库中获取Document
				if (doc == null) {
					doc = (Document) process.doView(id);
				}
				if (doc != null) {
					doc.setLastmodifier(webUser.getId());
					doc = form.createDocument(doc, params, webUser);
				} else {
					doc = form.createDocument(params, webUser);
				}

				IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
				runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());

				doBeforeScript(act, runner);

				// 对文档进行校验
				Collection<ValidateMessage> errors = process.doValidate(doc, params, webUser);
				StringBuffer error = new StringBuffer();
				if (errors != null && errors.size() > 0) {
					for (Iterator<ValidateMessage> iter = errors.iterator(); iter.hasNext();) {
						ValidateMessage err = iter.next();
						error.append(err.getErrmessage());
						error.append(";");
					}
					error.deleteCharAt(error.lastIndexOf(";"));
					throw new Exception(error.toString());
				}

				process.doStartFlowOrUpdate(doc, params, webUser);

				doAfterScript(act, runner);

				// MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, webUser);
				// 放置到缓存中
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "showError('" + e.getMessage() + "');";
		}
	}

	public Map<String, String> getFileList(int index, HttpServletRequest request) throws Exception {
		String[] path = { "SECSIGN_PATH", "REDHEAD_DOCPATH", "TEMPLATE_DOCPATH" };
		String[] defaultOption = { "请选择服务器印章", "请选择模板进行套红", "请选择模板进行打开" };
		String dir = DefaultProperty.getProperty(path[index]);
		Environment evt = Environment.getInstance();
		evt.setContextPath(request.getContextPath());
		String realPath = evt.getRealPath(dir);
		File file = new File(realPath);
		if (!file.exists()) {
			if (!file.mkdir())
				throw new Exception("Folder create failure");
		}
		File[] fileList = file.listFiles();
		// StringBuffer options = new StringBuffer();
		Map<String, String> options = new LinkedHashMap<String, String>();
		options.put("", defaultOption[index]);
		if (fileList.length > 0) {
			for (int i = 0; i < fileList.length; i++) {
				String fileName = fileList[i].getName();
				String filePath = dir + fileName;
				options.put(filePath, fileName);
			}
		}
		return options;
	}

	public ArrayList<ArrayList<String>> getSecFileList(HttpServletRequest request) throws Exception {
		String dir = DefaultProperty.getProperty("SECSIGN_PATH");
		Environment evt = Environment.getInstance();
		evt.setContextPath(request.getContextPath());
		String realPath = evt.getRealPath(dir);
		File file = new File(realPath);
		if (!file.exists()) {
			if (!file.mkdir())
				throw new Exception("Folder create failure");
		}
		File[] fileList = file.listFiles();
		ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
		if (fileList.length > 0) {
			for (int i = 0; i < fileList.length; i++) {
				ArrayList<String> tr = new ArrayList<String>();
				tr.add(fileList[i].getName());
				// tr.add(new
				// Date(fileList[i].lastModified()).toLocaleString());
				tr.add(new SimpleDateFormat().format(new Date(fileList[i].lastModified())));
				tr.add(String.valueOf(fileList[i].length()));
				table.add(tr);
			}
		}
		return table;
	}
}
