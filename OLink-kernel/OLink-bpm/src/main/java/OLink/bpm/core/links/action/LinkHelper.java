package OLink.bpm.core.links.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigProcess;
import OLink.bpm.core.links.ejb.LinkProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;

public class LinkHelper extends BaseHelper<LinkVO> {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(LinkHelper.class);

	public LinkHelper(IDesignTimeProcess<LinkVO> process) {
		super(process);
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public LinkHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(LinkProcess.class));
	}

	public Map<String, String> get_ExcelImportCfgList(String application) throws Exception {
		IMPMappingConfigProcess ep = (IMPMappingConfigProcess) ProcessFactory
				.createProcess(IMPMappingConfigProcess.class);
		Collection<IMPMappingConfigVO> col = ep.getAllMappingConfig(application);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Iterator<IMPMappingConfigVO> it = col.iterator();
		while (it.hasNext()) {
			IMPMappingConfigVO em = it.next();
			map.put(em.getId(), em.getName());
		}
		return map;
	}

	public String buildActionUrl(String pk, ParamsTable params) throws Exception {
		LinkVO link = (LinkVO) process.doView(pk);
		int type = Integer.parseInt(link.getType());
		String actionContent = link.getActionContent();
		String contextPath = params.getContextPath();
		StringBuffer url = new StringBuffer();
		switch (type) {
		case 0:
			url.append(contextPath).append("/portal/dynaform/document/new.action?_formid=").append(actionContent)
					.append("&_isJump=1");
			break;
		case 1:
			url.append(contextPath).append("/portal/dynaform/view/displayView.action?_viewid=").append(actionContent)
					.append("&clearTemp=true");
			break;
		case 2:
			url.append(contextPath).append("/portal/report/crossreport/runtime/runreport.action?reportId=").append(
					actionContent);
			break;
		case 3:
			url.append(contextPath).append("/portal/share/dynaform/dts/excelimport/importbyid.jsp?id=").append(
					actionContent).append("&applicationid="+link.getApplicationid());
			break;
		case 5:
			url.append(actionContent);
			break;

		}
		return url.toString();

	}

	public static void main(String[] args) {
		String a = "03";
		int b = Integer.parseInt(a);
		System.out.println(b);
	}

}
