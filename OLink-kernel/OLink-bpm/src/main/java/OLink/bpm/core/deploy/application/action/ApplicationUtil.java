package OLink.bpm.core.deploy.application.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.overview.OverviewBuilder;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfigProcess;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.page.ejb.PageProcess;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.util.web.DWRHtmlUtils;

public class ApplicationUtil {

	// public Map getApplication(String application) throws Exception {
	// LinkedHashMap map = new LinkedHashMap();
	// map.put("none", "Select");
	// //PersistenceUtils.getSessionSignal().sessionSignal++;
	// ApplicationProcess ap = (ApplicationProcess) ProcessFactory
	// .createProcess(ApplicationProcess.class);
	// Collection datas = ap.doSimpleQuery(null, application);
	// Iterator it = datas.iterator();
	// while (it.hasNext()) {
	// ApplicationVO av = (ApplicationVO) it.next();
	// map.put(av.getId(), av.getName());
	// }
	// //PersistenceUtils.getSessionSignal().sessionSignal--;
	// PersistenceUtils.closeSession();
	// return map;
	// }

	public Map<String, String> deepSearchModuleTree(Collection<ModuleVO> cols, String applicationId,
													ModuleVO startNode, String excludeNodeId, int deep) throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();
		list.put("none", "Select");
		if (applicationId == null || applicationId.equals(""))
			return list;

		String prefix = "|------------------------------------------------";
		if (startNode != null) {
			list.put(startNode.getId(), prefix.substring(0, deep * 2) + startNode.getName());
		}

		Iterator<ModuleVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ModuleVO vo = iter.next();

			if (applicationId == null || vo.getApplication() == null
					|| !applicationId.equals(vo.getApplication().getId())) {
				continue;
			}

			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols, applicationId, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null && vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchModuleTree(cols, applicationId, vo, excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	public Map<String, String> getModuleByApp(String applicationid) throws Exception {
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		ApplicationVO av = (ApplicationVO) ap.doView(applicationid);

		Map<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		if (applicationid != null && !applicationid.equals("none") && !applicationid.equals(""))
			map = deepSearchModuleTree(av.getModules(), applicationid, null, null, 0);
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();

		return map;
	}
	
	//通过软件获取模块的JSON格式集合
	public String getModuleByApplication(String applicationid) throws Exception{
		StringBuffer sb = new StringBuffer();
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		ApplicationVO av = (ApplicationVO) ap.doView(applicationid);
		
		if(deepSearchModuleTree(av.getModules(), applicationid, null, null, 0)!=null 
				&& deepSearchModuleTree(av.getModules(), applicationid, null, null, 0).entrySet()!=null 
				&& deepSearchModuleTree(av.getModules(), applicationid, null, null, 0).entrySet().size()>0){
			sb.append("{\"module\":[");
			Iterator<Entry<String, String>> it = deepSearchModuleTree(av.getModules(), applicationid, null, null, 0).entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				sb.append("{\"id\":\""+entry.getKey()+"\",\"label\":\""+entry.getValue()+"\"},");
			}
			if(sb.lastIndexOf(",")!=-1){
				sb.deleteCharAt(sb.lastIndexOf(","));
			}
			sb.append("]}");
			return sb.toString();
		}
		return "";
	}

	public Map<String, String> getViewByMod(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "{*[Select]*}");

		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> col = vp.getViewsByModule(moduleid, application);

		if (col != null && col.size() > 0) {
			Iterator<View> it = col.iterator();
			while (it.hasNext()) {
				View vw = it.next();
				map.put(vw.getId(), vw.getName());
			}
		}
		return map;
	}
	
	/**
	 * 过滤了地图视图、甘特视图、树形视图、当前区域显示、弹出层显示、网格显示的视图集合
	 * @param moduleid
	 * @param application
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getViewByMod1(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "{*[Select]*}");

		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> col = vp.getViewsByModule(moduleid, application);

		if (col != null && col.size() > 0) {
			Iterator<View> it = col.iterator();
			while (it.hasNext()) {
				View vw = it.next();
				if(vw.getViewType()== View.VIEW_TYPE_MAP 
						|| vw.getViewType()== View.VIEW_TYPE_GANTT
						|| vw.getOpenType()== View.OPEN_TYPE_OWN
						|| vw.getOpenType()== View.OPEN_TYPE_DIV
						|| vw.getOpenType()== View.OPEN_TYPE_GRID){
				
				}else{
					map.put(vw.getId(), vw.getName());	
				}
			}
		}
		return map;
	}

	public Map<String, String> getViewByModuleNoGantt(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		if (moduleid.equals("") || application.equals("")) {
			return map;
		}
		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> col = vp.getViewsByModule(moduleid, application);

		Iterator<View> it = col.iterator();
		while (it.hasNext()) {
			View vw = it.next();
			if (vw.getViewType() != View.VIEW_TYPE_GANTT && vw.getViewType() != View.VIEW_TYPE_TREE) {
				map.put(vw.getId(), vw.getName());
			}
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;

	}

	public Map<String, String> getViewByModule(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		if (moduleid.equals("") || application.equals("")) {
			return map;
		}
		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> col = vp.getViewsByModule(moduleid, application);

		Iterator<View> it = col.iterator();
		while (it.hasNext()) {
			View vw = it.next();
			map.put(vw.getId(), vw.getName());
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;

	}

	public Map<String, String> getFormByModule(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "{*[Select]*}");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		if (moduleid != null && !moduleid.equals("")) {
			FormProcess proc = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Collection<Form> col = proc.getFormsByModule(moduleid, application);

			Iterator<Form> it = col.iterator();
			while (it.hasNext()) {
				Form vo = it.next();
				map.put(vo.getId(), vo.getName());
			}
		} else {
			FormProcess proc = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			Collection<Form> col = proc.get_formList(application);

			Iterator<Form> it = col.iterator();
			while (it.hasNext()) {
				Form vo = it.next();
				map.put(vo.getId(), vo.getName());
			}
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;
	}
	
	//通过软件获得页
	public Map<String, String> getPageByApplication(String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		PageProcess pp = (PageProcess) ProcessFactory.createProcess(PageProcess.class);
		Collection<Page> col = pp.getPagesByApplication(application);

		Iterator<Page> it = col.iterator();
		while (it.hasNext()) {
			Page p = it.next();
			map.put(p.getId(), p.getName());
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;
	}
	
	//通过软件获得首页
	public Map<String, String> getUserDefinedByApplication(String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		UserDefinedProcess pp = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
		Collection<UserDefined> col = pp.doViewByApplication(application);

		Iterator<UserDefined> it = col.iterator();
		while (it.hasNext()) {
			UserDefined p = it.next();
			map.put(p.getId(), p.getName());
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return map;
	}

	public Map<String, String> getImpMppingConfigByApplition(String applicationid) throws Exception {
		IMPMappingConfigProcess mapping = (IMPMappingConfigProcess) ProcessFactory
				.createProcess(IMPMappingConfigProcess.class);
		Collection<IMPMappingConfigVO> list = mapping.getAllMappingConfig(applicationid);
		Map<String, String> map = new HashMap<String, String>();
		map.put("", "");
		if (list != null && list.size() > 0) {
			for (Iterator<IMPMappingConfigVO> iterator = list.iterator(); iterator.hasNext();) {
				IMPMappingConfigVO vo = iterator.next();
				map.put(vo.getId(), vo.getName());

			}
		}
		return map;
	}

	public String creatModule(String selectFieldName, String applicationid, String def) throws Exception {
		Map<String, String> map = getModuleByApp(applicationid);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String creatView(String selectFieldName, String application, String moduleid, String def) throws Exception {
		Map<String, String> map = getViewByModule(moduleid, application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String createImpMappingAction(String selectFieldName, String application, String moduleid, String def)
			throws Exception {
		Map<String, String> map = getImpMppingConfigByApplition(application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	//创建首页
	public String creatPage(String selectFieldName, String application, String def) throws Exception {
		Map<String, String> map = getUserDefinedByApplication(application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String creatForm(String selectFieldName, String application, String moduleid, String def) throws Exception {
		Map<String, String> map = getFormByModule(moduleid, application);
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String creatSearchForm(String selectFieldName, String application, String moduleid, String def)
			throws Exception {

		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		if (application != null && application.length() > 0 && !application.equals("none")) {
			Collection<Form> col = fp.getSearchFormsByModule(moduleid, application);
			Iterator<Form> it = col.iterator();
			while (it.hasNext()) {
				Form vw = it.next();
				map.put(vw.getId(), vw.getName());
			}
		}
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);

	}

	public String creatReports(String selectFieldName, String application, String moduleid, String def)
			throws Exception {
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		Map<String, String> map = getReportByModule(moduleid, application);
		// //PersistenceUtils.getSessionSignal().sessionSignal--;
		PersistenceUtils.closeSession();
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public Map<String, String> getReportByModule(String moduleid, String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("none", "Select");
		if (moduleid.equals("") || application.equals("")) {
			return map;
		}
		ReportConfigProcess vp = (ReportConfigProcess) ProcessFactory.createProcess(ReportConfigProcess.class);
		Collection<ReportConfig> col = vp.getReportByModule(moduleid, application);

		Iterator<ReportConfig> it = col.iterator();
		while (it.hasNext()) {
			ReportConfig vw = it.next();
			map.put(vw.getId(), vw.getName());
		}
		return map;

	}

	/**
	 * 生成应用概览
	 * 
	 * 2.6版本新增
	 * 
	 * @param applicationId
	 * @param pdfFileName
	 * @throws Exception
	 */
	public static void createOverview(String applicationId, String pdfFileName)
			throws Exception {
		OverviewBuilder.getInstance().buildOverview(applicationId, pdfFileName);
	}
	
	//保存图形导航xm文件
	public String saveNavigationXml(String xml){
		String path = Environment.getInstance().getRealPath("/core/deploy/application/applet");
		File f1 = new File(path);
		if (!f1.exists()) {
			if(!f1.mkdirs()){
				return "Failed to create folder ("+path+")";
			}
		}
		try{
			  File f2 = new File(path+"\\navigation.xml");
			  //FileWriter fstream = new FileWriter(f2);
			  BufferedWriter out1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f2), "UTF-8"));
			  out1.write(xml);
			  out1.flush();
			  //Close the output stream
		  	  out1.close();
		  }catch (Exception e){//Catch exception if any
			  return "Error: " + e.getMessage();
		  }
		return "";
	}
	
	public String getModuleByViewId(String viewId) throws Exception{
		String rtn ="";
		if(!StringUtil.isBlank(viewId)){
			ViewProcess process = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
			View view = (View) process.doView(viewId);
			if(view != null){
				rtn = view.getModule().getId();
			}
		}
		return rtn;
	}
}
