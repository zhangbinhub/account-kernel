package OLink.bpm.webservice;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

public class WebServiceUtil {

	public static DomainVO validateDomain(String domainName) throws Exception {

		if (StringUtil.isBlank(domainName)) {
			throw new Exception("域名称不能为空.");
		} else {
			DomainProcess dp = (DomainProcess) ProcessFactory
					.createProcess(DomainProcess.class);
			DomainVO vo = dp.getDomainByName(domainName);
			if (vo == null) {
				throw new Exception("该域(" + domainName + ")不存在.");
			}
			return vo;
		}

	}

	public static ApplicationVO validateApplication(String name)
			throws Exception {
		ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		ApplicationVO app = applicationProcess
				.doViewByName(name);
		if (app == null)
			throw new Exception("该应用(" + name + ")不存在.");
		return app;
	}

}
