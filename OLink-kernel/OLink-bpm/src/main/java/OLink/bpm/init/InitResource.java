package OLink.bpm.init;

import java.util.Collection;

import OLink.bpm.core.resource.ejb.ResourceType;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;
import eWAP.core.Tools;

public class InitResource implements IInitialization {

	public void run() throws InitializationException {
		run("");
	}

	public void run(String applicationid) throws InitializationException {
		try {
			ResourceProcess process = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("s_applicationid", applicationid);
			Collection<ResourceVO> colls = process.doSimpleQuery(params, null);

			if (colls.isEmpty()) {
				/**
				 * Application Menu
				 */
				ResourceVO app = new ResourceVO();
				app.setId(Tools.getSequence());
				app.setActionclass("none");
				app.setActionmethod("none");
				app.setActionurl("none");
				app.setDescription("App Definition");
				app.setIsprotected(false);
				app.setOrderno("1");
				app.setSuperior(null);
				app.setType("00");
				app.setOtherurl("/core/deploy/application/appnavigator.jsp");
				app.setApplication("");
				app.setDisplayView("");
				app.setModule("");
				app.setResourceAction(ResourceType.ACTION_TYPE_OTHERURL);
				app.setIsprotected(true);
				app.setReport("");
				app.setReportAppliction("");
				app.setReportModule("");
				app.setSortId(Tools.getTimeSequence());
				app.setApplicationid(applicationid);
				process.doUpdate(app);

				/**
				 * Dev Stiduo Menu
				 */
				ResourceVO dev = new ResourceVO();
				dev.setId(Tools.getSequence());
				dev.setActionclass("none");
				dev.setActionmethod("none");
				dev.setActionurl("none");
				dev.setDescription("Dev Studio");
				dev.setIsprotected(true);
				dev.setOrderno("2");
				dev.setSuperior(null);
				dev.setType("00");
				dev.setOtherurl("");
				dev.setApplication("");
				dev.setDisplayView("");
				dev.setModule("");
				dev.setResourceAction(ResourceType.ACTION_TYPE_NONE);
				dev.setIsprotected(true);
				dev.setReport("");
				dev.setReportAppliction("");
				dev.setReportModule("");
				dev.setSortId(Tools.getTimeSequence());
				dev.setApplicationid(applicationid);
				process.doUpdate(dev);

				addDevSubResource(dev, applicationid);

/*				*//**
				 * Mobile Menu
				 *//*
				ResourceVO mobile = new ResourceVO();
				mobile.setId("mobile");
				mobile.setActionclass("none");
				mobile.setActionmethod("none");
				mobile.setActionurl("none");
				mobile.setDescription("Mobile");
				mobile.setOrderno("3");
				mobile.setSuperior(null);
				mobile.setType("00");
				mobile.setApplication(applicationid);
				mobile.setDisplayView("");
				mobile.setModule("");
				mobile.setResourceAction("00");
				mobile.setIsprotected(true);
				mobile.setReport("");
				mobile.setReportAppliction("");
				mobile.setReportModule("");
				mobile.setSortId(Tools.getTimeSequence());
				mobile.setApplicationid(applicationid);
				process.doUpdate(mobile);*/

				/**
				 * System Menu
				 */
				ResourceVO system = new ResourceVO();
				system.setId(Tools.getSequence());
				system.setActionclass("none");
				system.setActionmethod("none");
				system.setActionurl("none");
				system.setDescription("System");
				system.setOrderno("4");
				system.setSuperior(null);
				system.setType("00");
				system.setOtherurl("/portal/navigator.jsp?_parent="
						+ system.getId());
				system.setApplication("");
				system.setDisplayView("");
				system.setModule("");
				system.setResourceAction(ResourceType.ACTION_TYPE_NONE);
				system.setIsprotected(true);
				system.setReport("");
				system.setReportAppliction("");
				system.setReportModule("");
				system.setSortId(Tools.getTimeSequence());
				system.setApplicationid(applicationid);
				process.doUpdate(system);
				
				addSystemSubResource(system, applicationid);
			}
		} catch (Exception e) {
			throw new InitializationException(e.getMessage());
		}
	}

	/**
	 * 增加System菜单项
	 * 
	 * @param system
	 * @param applicationid
	 * @throws Exception
	 */
	public void addSystemSubResource(ResourceVO system, String applicationid)
			throws Exception {
		ResourceProcess process = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);

		/**
		 * Menu/Page List Item
		 */
		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource
					.setActionclass("ResourceAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/resource/list.action");
			resource.setDescription("Menu/Page");
			resource.setOrderno("1");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setIsprotected(true);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		/**
		 * Role List Item
		 */
		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource.setActionclass("RoleAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/role/list.action");
			resource.setDescription("Role");
			resource.setOrderno("2");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setIsprotected(true);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		/**
		 * Excel Import Mapping Config List Item
		 */
		{
			ResourceVO resource = new ResourceVO();

			resource.setId(Tools.getSequence());
			resource
					.setActionclass("IMPMappingConfigAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/dynaform/dts/excelimport/list.action");
			resource.setDescription("ExcelImpConfig");
			resource.setIsprotected(false);
			resource.setOrderno("3");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction("02");
			resource.setIsprotected(true);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		/**
		 * Multi Language List Item
		 */
		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource
					.setActionclass("MultiLanguageAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/multilanguage/list.action");
			resource.setDescription("Language");
			resource.setIsprotected(true);
			resource.setOrderno("4");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		/**
		 * Task List Item
		 */
		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource.setActionclass("TaskAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/task/list.action");
			resource.setDescription("Task");
			resource.setIsprotected(true);
			resource.setOrderno("5");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		/**
		 * State Label List Item
		 */
		{
			ResourceVO resource = new ResourceVO();

			resource.setId(Tools.getSequence());
			resource
					.setActionclass("StateLabelAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/workflow/statelabel/list.action");
			resource.setDescription("StateLabel");
			resource.setIsprotected(true);
			resource.setOrderno("6");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		/**
		 * Calendar Item List
		 */
		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource
					.setActionclass("OLink.bpm.core.calendar.action.WorkingDayAction");
			resource.setActionmethod("doDisplayView");
			resource.setActionurl("/core/calendar/displayView.action");
			resource.setDescription("Calendar");
			resource.setIsprotected(true);
			resource.setOrderno("7");
			resource.setSuperior(system);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("none");
			resource.setDisplayView("none");
			resource.setModule("none");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setReport("none");
			resource.setReportAppliction("none");
			resource.setReportModule("none");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}
	}

	/**
	 * 增加Dev Stiduo的菜单项
	 * 
	 * @param dev
	 * @param applicationid
	 * @throws Exception
	 */
	public void addDevSubResource(ResourceVO dev, String applicationid)
			throws Exception {
		ResourceProcess process = (ResourceProcess) ProcessFactory
				.createProcess(ResourceProcess.class);

		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource
					.setActionclass("RepositoryAction");
			resource.setActionmethod("doList");
			resource.setActionurl("/core/macro/repository/list.action");
			resource.setDescription("Functions");
			resource.setIsprotected(false);
			resource.setOrderno("0");
			resource.setSuperior(dev);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_ACTIONCLASS);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource.setActionclass("none");
			resource.setActionmethod("none");
			resource.setActionurl("/core/macro/debuger/debuger.jsp");
			resource.setDescription("Debuger");
			resource.setIsprotected(false);
			resource.setOrderno("1");
			resource.setSuperior(dev);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_OTHERURL);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}

		{
			ResourceVO resource = new ResourceVO();
			resource.setId(Tools.getSequence());
			resource.setActionclass("none");
			resource.setActionmethod("none");
			resource.setActionurl("/core/macro/moniter/viewStates.jsp");
			resource.setDescription("StateMoniter");
			resource.setIsprotected(false);
			resource.setOrderno("2");
			resource.setSuperior(dev);
			resource.setType("00");
			resource.setOtherurl("");
			resource.setApplication("");
			resource.setDisplayView("");
			resource.setModule("");
			resource.setResourceAction(ResourceType.ACTION_TYPE_OTHERURL);
			resource.setReport("");
			resource.setReportAppliction("");
			resource.setReportModule("");
			resource.setSortId(Tools.getTimeSequence());
			resource.setApplicationid(applicationid);
			process.doUpdate(resource);
		}
	}
}
