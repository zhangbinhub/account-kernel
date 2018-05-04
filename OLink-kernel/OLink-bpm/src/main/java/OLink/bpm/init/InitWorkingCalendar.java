package OLink.bpm.init;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.workcalendar.calendar.action.CalendarHelper;

public class InitWorkingCalendar implements IInitialization {

	public void run() throws InitializationException {
		try {
			DomainProcess process = (DomainProcess) ProcessFactory
					.createProcess(DomainProcess.class);
			Collection<DomainVO> cols = process.doSimpleQuery(null);
			if (cols != null) {
				CalendarHelper cldHelper = new CalendarHelper();
				Iterator<DomainVO> it = cols.iterator();
				while (it.hasNext()) {
					DomainVO vo = it.next();
					if (vo != null) {
						cldHelper.createCalendarByDomain(vo.getId());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InitializationException {
		InitWorkingCalendar init = new InitWorkingCalendar();
		init.run();
	}

}
