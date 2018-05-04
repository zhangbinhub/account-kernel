package OLink.bpm.init;

import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.util.ProcessFactory;
import eWAP.core.Tools;

/**
 * The user initialization object.
 */
public class InitUserInfo implements IInitialization {

	/*
	 * (non-Javadoc)
	 * 
	 * @see IInitialization#run()
	 */
	public void run() throws InitializationException {
		try {
			SuperUserProcess process = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			//CalendarProcess cldprocess = (CalendarProcess) ProcessFactory.createProcess(CalendarProcess.class);
			
			// DataPackage dp = process.doQuery(new ParamsTable(), null);
			if (process.isEmpty()) {

				SuperUserVO user = new SuperUserVO();
				user.setId(Tools.getSequence());
				user.setSortId(Tools.getTimeSequence());
				user.setApplicationid(null);
				user.setName("Admin");
				user.setLoginno("admin");
				user.setLoginpwd("888888");
				user.setSuperAdmin(true);
				user.setDomainPermission(SuperUserVO.NORMAL_DOMAIN);
				user.setStatus(1);
//				if (cldprocess!=null){
//					CalendarVO cld=(CalendarVO)cldprocess.doViewByName("标准日历");
//					user.setCalendarType(cld.getId());
//				}
				process.doCreate(user);
			}
		} catch (Exception e) {
			throw new InitializationException(e.getMessage());
		}
	}
}
