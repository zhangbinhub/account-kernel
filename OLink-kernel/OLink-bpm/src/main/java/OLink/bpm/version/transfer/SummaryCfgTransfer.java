package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.util.ProcessFactory;

/**
 * @author Happy
 *
 */
public class SummaryCfgTransfer extends BaseTransfer {

	public void to2_4() {

	}
	
	public void to2_5() {
		try {
			transfer4to5();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	/**
	 * 2.4前的版本升级到2.5
	 * @throws Exception
	 */
	public void transfer4to5() throws Exception {
		SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
		ReminderProcess reminderProcess = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
//		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
//		HomePageProcess homepageProcess = (HomePageProcess) ProcessFactory.createProcess(HomePageProcess.class);
//		UserDefinedProcess userDefinedProcess = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
//		ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		Collection<Reminder> reminderList = reminderProcess.doSimpleQuery(new ParamsTable());
		for(Iterator<Reminder> iterator = reminderList.iterator(); iterator.hasNext();){
			Reminder reminder = iterator.next();
			//10将原来的提醒转换到对应表单的摘要
			if(reminder.getFormId()!=null && reminder.getFormId().trim().length()>0){
				SummaryCfgVO vo = (SummaryCfgVO) summaryCfgProcess.doView(reminder.getId());
//				Form form = (Form) formProcess.doView(reminder.getFormId());
//				if(form !=null && form.getSummaryCfg()==null){
				if(vo == null){
					vo = calculatSummaryCfgFromReminder(reminder);
					summaryCfgProcess.doCreate(vo);
				}
					//form.setSummaryCfg(vo);
					//formProcess.doUpdate(form);
//				}
			}
			
			//20将原来首页绑定的提醒转换为摘要
			/*
			Collection<HomePage> homepageList = homepageProcess.doSimpleQuery(new ParamsTable());
			for(Iterator<HomePage> iter = homepageList.iterator(); iter.hasNext();){
				HomePage page = iter.next();
				UserDefined ud = new UserDefined();
				ud.setName(page.getName());
				ud.setDisplayTo("");
				if(!page.getReminders().isEmpty()){
					for(Reminder rm : page.getReminders()){
						SummaryCfgVO summaryvo =  (SummaryCfgVO) summaryCfgProcess.doView(rm.getId());
						if(summaryvo !=null){
							
						}
					}
				//	page.setSummaryCfgs(summarys);
					//homepageProcess.doUpdate(page);
				}
			}
			*/
			
			
			//30流程提醒绑定转换为对应摘要
			/*
			Collection<ApplicationVO> appList = applicationProcess.doSimpleQuery(new ParamsTable());
			for(Iterator<ApplicationVO> it = appList.iterator();it.hasNext();){
				ApplicationVO app = it.next();
				
				NotificationProcess notificationProcess = new NotificationProcessBean(app.getApplicationid());
				
				//Collection<Notification> notiList = notificationProcess.doSimpleQuery(new ParamsTable());
				
				
			}
			*/
			
			
			
		}
	}
	
	/**
	 * 将原来的提醒转换成新的摘要对象
	 * @param reminder
	 * @return
	 */
	public SummaryCfgVO calculatSummaryCfgFromReminder(Reminder reminder){
		SummaryCfgVO vo = new SummaryCfgVO();
		vo.setId(reminder.getId());
		vo.setFormId(reminder.getFormId());
		vo.setTitle(reminder.getTitle());
		vo.setType(reminder.getType());
		vo.setStyle(reminder.getStyle());
		vo.setOrderby(reminder.getOrderby());
		vo.setFieldNames(reminder.getSummaryFieldNames());
		vo.setSummaryScript(reminder.getFilterScript());
		vo.setApplicationid(reminder.getApplicationid());
		vo.setDomainid(reminder.getDomainid());
		vo.setSortId(reminder.getSortId());
		vo.setVersion(reminder.getVersion());
		return vo;
	}
	
	public static void main(String[] args) {
		new SummaryCfgTransfer().to2_5();
	}

}
