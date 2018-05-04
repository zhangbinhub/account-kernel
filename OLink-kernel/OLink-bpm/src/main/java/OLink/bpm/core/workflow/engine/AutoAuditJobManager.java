package OLink.bpm.core.workflow.engine;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.*;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcessBean;
import org.apache.log4j.Logger;

import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;

public class AutoAuditJobManager {
	private static Map<String, AutoAuditJob> waittingJobMap = new HashMap<String, AutoAuditJob>();

	public final static Logger LOG = Logger
			.getLogger(AutoAuditJobManager.class);

	public static void addJob(AutoAuditJob job) {
		FlowStateRT instance = job.getInstance();
		if (instance != null) {
			String key = instance.getDocid() + "_" + instance.getId();
			waittingJobMap.put(key, job);
		}
	}

	/**
	 * @param doc
	 * @deprecated since 2.6
	 */
	@Deprecated
	public static void startJobByDoc(Document doc) {
		if (doc != null) {
			String key = doc.getId() + "_" + doc.getStateid();
			AutoAuditJob job = waittingJobMap.get(key);

			if (job != null) {
				Date firstTime = job.getFirstTime();
				if (firstTime != null) {
					Timer timer = new Timer();
					timer.schedule(job, firstTime);
					waittingJobMap.remove(key);
				}
			}
		}
	}
	
	public static void startJobByFlowInstance(FlowStateRT instance) {
		if (instance != null) {
			String key = instance.getDocid() + "_" + instance.getId();
			AutoAuditJob job = waittingJobMap.get(key);
			if (job != null) {
				Date firstTime = job.getFirstTime();
				if (firstTime != null) {
					Timer timer = new Timer();
					timer.schedule(job, firstTime);
					waittingJobMap.remove(key);
				}
			}
		}
	}

	/**
	 * 初始化自动节点任务
	 */
	public static void initJobs() {
		try {
			LOG
					.info("********************* Auto Audit Job Manager Init Jobs Start ********************");
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			// 系统用户
			WebUser admin = new WebUser(superUserProcess.getDefaultAdmin());

			Collection<ApplicationVO> applications = applicationProcess
					.doSimpleQuery(null);
			for (Iterator<ApplicationVO> iterator = applications.iterator(); iterator
					.hasNext();) {
				try {
					ApplicationVO application = iterator.next();
					if (application.testDB()) {
						try {
							FlowStateRTProcess instanceProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, application.getId());
							NodeRTProcess nodeRTProcess = new NodeRTProcessBean(
									application.getId());
							
							String flowStateRTSQL = "SELECT * FROM T_FLOWSTATERT vo WHERE vo.STATE = "+ FlowState.AUTO;
							
							Collection<FlowStateRT> instances = instanceProcess.doQueryBySQL(flowStateRTSQL);
							
							for (Iterator<FlowStateRT> iter = instances
									.iterator(); iter.hasNext();) {
								FlowStateRT instance = iter.next();
								NodeRT nodert = nodeRTProcess.doView(instance.getDocid(), instance.getFlowid(),instance.getId(), admin);
								// 添加自动节点任务
								addJob(new AutoAuditJob(instance, nodert.getNodeid(), admin));
								// 启动自动节点任务
								startJobByFlowInstance(instance);
							}
						
						} catch (UnsupportedOperationException e) {
							continue;
						}
					}
				} catch (Exception e) {
					LOG.error("Auto Audit Job Manager Init Jobs Error: ", e);
				} finally {
					PersistenceUtils.closeSessionAndConnection();
				}
			}
		} catch (Exception e) {
			LOG.error("Auto Audit Job Manager Init Jobs Error: ", e);
		} finally {
			try {
				PersistenceUtils.closeSessionAndConnection();
			} catch (Exception e) {
				LOG.error("Auto Audit Job Manager Init Jobs Error: ", e);
			}
		}
		LOG
				.info("********************* Auto Audit Job Manager Init Jobs End ********************");
	}
	
	/*
	 * 2.6之前的逻辑  
	public static void initJobs() {
		try {
			LOG
					.info("********************* Auto Audit Job Manager Init Jobs Start ********************");
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			SuperUserProcess superUserProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			FormProcess formProcess = (FormProcess) ProcessFactory
					.createProcess(FormProcess.class);
			// 系统用户
			WebUser admin = new WebUser(superUserProcess.getDefaultAdmin());

			Collection<ApplicationVO> applications = (Collection<ApplicationVO>) applicationProcess
					.doSimpleQuery(null);
			for (Iterator<ApplicationVO> iterator = applications.iterator(); iterator
					.hasNext();) {
				try {
					ApplicationVO application = (ApplicationVO) iterator.next();
					if (application.testDB()) {
						try {
							DocumentProcess documentProcess = new DocumentProcessBean(
									application.getId());
							NodeRTProcess nodeRTProcess = new NodeRTProcessBean(
									application.getId());

							String formnameSQL = "select FORMNAME from T_DOCUMENT doc";
							formnameSQL += " where doc.STATEINT="
									+ FlowState.AUTO;
							formnameSQL += " group by FORMNAME";

							Collection<Document> docHeads = documentProcess
									.queryBySQL(formnameSQL);
							// 获取的文档头信息(自动状态)
							for (Iterator<Document> dociterator = docHeads
									.iterator(); dociterator.hasNext();) {
								Document docHead = (Document) dociterator
										.next();
								// 获取表单名称
								String formname = docHead.getFormShortName();
								Form form = formProcess.doViewByFormName(
										formname, application.getId());

								String sql = "select * from "
										+ form.getTableMapping().getTableName()
										+ " doc";
								sql += " where doc.STATEINT=" + FlowState.AUTO;

								Collection<Document> docs = documentProcess
										.queryBySQL(sql);
								// 获取文档
								for (Iterator<Document> iterator2 = docs
										.iterator(); iterator2.hasNext();) {
									Document doc = (Document) iterator2.next();
									NodeRT nodert = nodeRTProcess.doView(doc
											.getId(), doc.getFlowid(),doc.getStateid(), admin);
									// 添加自动节点任务
									addJob(new AutoAuditJob(doc, nodert
											.getNodeid(), admin));
									// 启动自动节点任务
									startJobByDoc(doc);
								}
							}
						} catch (UnsupportedOperationException e) {
							continue;
						}
					}
				} catch (Exception e) {
					LOG.error("Auto Audit Job Manager Init Jobs Error: ", e);
				} finally {
					PersistenceUtils.closeSessionAndConnection();
				}
			}
		} catch (Exception e) {
			LOG.error("Auto Audit Job Manager Init Jobs Error: ", e);
		} finally {
			try {
				PersistenceUtils.closeSessionAndConnection();
			} catch (Exception e) {
				LOG.error("Auto Audit Job Manager Init Jobs Error: ", e);
			}
		}
		LOG
				.info("********************* Auto Audit Job Manager Init Jobs End ********************");
	}
	*/

	public static void main(String[] args) throws Exception {
		initJobs();
	}
}
