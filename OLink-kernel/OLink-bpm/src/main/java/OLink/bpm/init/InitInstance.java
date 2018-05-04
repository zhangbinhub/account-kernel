package OLink.bpm.init;


import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.util.ProcessFactory;
import eWAP.core.Tools;

public class InitInstance implements IInitialization {

	public void run() throws InitializationException {
		try {
			ApplicationProcess process = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			// Collection colls = process.doSimpleQuery(null, null);
			if (process.isEmpty()) {
				ApplicationVO vo = new ApplicationVO();
				vo.setId(Tools.getSequence());
				vo.setSortId(Tools.getTimeSequence());
				vo.setName("Default");
				vo.setDescription("eWAP - Default System");
//				vo.setDbtype(DefaultProperty.getProperty("DB_TYPE"));
				process.doUpdate(vo);

//				new InitResource().run(vo.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
