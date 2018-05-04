package OLink.bpm.version.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import OLink.bpm.core.homepage.ejb.HomePage;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.homepage.ejb.HomePageProcess;
import OLink.bpm.core.page.ejb.PageProcess;
import org.apache.log4j.Logger;

import OLink.bpm.util.ProcessFactory;

public class PageTransfer extends BaseTransfer {
	
	private final static Logger LOG = Logger.getLogger(PageTransfer.class);

	public void to2_4() {
		try {
			PageProcess pp = (PageProcess)ProcessFactory.createProcess(PageProcess.class);
			HomePageProcess hp = (HomePageProcess)ProcessFactory.createProcess(HomePageProcess.class);
			Collection<?> dataList = pp.doSimpleQuery(new ParamsTable());
			List<ValueObject> homePageData = new ArrayList<ValueObject>();
			LOG.info("---->begin transfer page to homepage...");
			for(Iterator<?> iterator = dataList.iterator(); iterator.hasNext();){
				Page page = (Page)iterator.next();
				HomePage existhp = (HomePage)hp.doView(page.getId());
				if(existhp == null || StringUtil.isBlank(existhp.getId())){
					LOG.info("--->initialize [" + page.getName() + "]");
					HomePage homePage = new HomePage();
					homePage.setId(page.getId());
					homePage.setSortId(page.getSortId());
					homePage.setName(page.getName());
					homePage.setDescription(page.getDiscription());
					homePage.setRoles(page.getRoles());
					homePage.setRoleNames(page.getRoleNames());
					homePage.setApplicationid(page.getApplicationid());
					homePage.setTemplateContext(page.getTemplatecontext());
					homePage.setStyle(page.getStyle());
					homePage.setPublished(page.isDefHomePage());
					homePage.setLayoutType("left");
					homePageData.add(homePage);
				}
			}
			if(homePageData != null && homePageData.size() >0){
				LOG.info("--->begin update all page to homepage...");
				hp.doUpdate(homePageData);
				LOG.info("---->transfer page data successfully!");
			}else{
				LOG.info("---->no data to transfer!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info("---->transfer page data failed!");
		}
	}
	
	public static void main(String[] args){
		new PageTransfer().to2_4();
	}
}
