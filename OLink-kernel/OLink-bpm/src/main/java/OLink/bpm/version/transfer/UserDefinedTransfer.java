package OLink.bpm.version.transfer;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.homepage.ejb.HomePage;
import OLink.bpm.core.homepage.ejb.HomePageProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.base.action.ParamsTable;

/**
 * @author Jack
 * 
 */
public class UserDefinedTransfer extends BaseTransfer {

	public void to2_4() {

	}
	
	public void to2_5() {
		try {
			moveOldHomepageToUserDefined();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//把homepage里的数据copy到userdefined中
	public void moveOldHomepageToUserDefined() throws Exception {
		try {
			HomePageProcess udprocss = (HomePageProcess) ProcessFactory.createProcess(HomePageProcess.class);
			HomePage homePage = new HomePage();
			ParamsTable params = new ParamsTable();
			DataPackage<HomePage> dataPackage2 = udprocss.doQuery(params);
			if(dataPackage2.rowCount > 0){
				for(Iterator<HomePage> ite1 = dataPackage2.datas.iterator();ite1.hasNext();){
					homePage = ite1.next();
					UserDefinedProcess userDefinedudprocss = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
					
					UserDefined userDefined1 = (UserDefined) userDefinedudprocss.doView(homePage.getId());
					if(userDefined1 == null){
						UserDefined userDefined2 = new UserDefined();
						setValueToUserDefined(userDefined2, homePage);
						userDefinedudprocss.doCreate(userDefined2);
					}else{
						setValueToUserDefined(userDefined1, homePage);
						userDefinedudprocss.doUpdate(userDefined1);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param userDefined1
	 * @param homePage
	 * 设置参数
	 */
	public void setValueToUserDefined(UserDefined userDefined1, HomePage homePage){
		userDefined1.setName(homePage.getName());
		userDefined1.setApplicationid(homePage.getApplicationid());
		userDefined1.setRoleIds(homePage.getRoles());
		userDefined1.setRoleNames(homePage.getRoleNames());
		userDefined1.setDefineMode(homePage.getDefineMode());
		userDefined1.setPublished(homePage.getPublished());
		userDefined1.setTemplateContext(homePage.getTemplateContext());
		userDefined1.setSortId(homePage.getSortId());
		userDefined1.setStyle(homePage.getStyle());
		userDefined1.setType("1");
		userDefined1.setDefineMode(homePage.getDefineMode());
		if(StringUtil.isBlank(homePage.getRoles())){
			userDefined1.setDisplayTo("0");
		}else{
			userDefined1.setDisplayTo("1");
		}
		userDefined1.setTemplateStyle("td1-x3-y2");
		//构建templateElement
		String templateEleStr = "{'td1-x3-y2':";
		String templateEleStrId = "'";
		String templateEleStrEle = "'";
		Collection<Reminder> reminders1 = homePage.getReminders();
		for(Iterator<Reminder> ite2 = reminders1.iterator();ite2.hasNext();){
			Reminder reminder1 = ite2.next();
			templateEleStrId += reminder1.getId() + "|";
			templateEleStrEle += reminder1.getTitle() + "|";
		}
		if(templateEleStrId.length() > 1){
			templateEleStrId = templateEleStrId.substring(0,templateEleStrId.length()-1);
		}
		if(templateEleStrEle.length() > 1){
			templateEleStrEle = templateEleStrEle.substring(0,templateEleStrEle.length()-1);
		}
		templateEleStrId += "'";
		templateEleStrEle += "'";
		templateEleStr += templateEleStrId + ";" + templateEleStrEle + "}";
		userDefined1.setTemplateElement(templateEleStr);
	}

	
	public static void main(String[] args) {
		new UserDefinedTransfer().to2_5();
	}

}
