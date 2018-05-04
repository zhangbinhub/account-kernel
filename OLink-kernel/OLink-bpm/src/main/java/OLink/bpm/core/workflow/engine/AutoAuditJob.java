package OLink.bpm.core.workflow.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.workflow.element.AutoNode;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.timer.Job;
import OLink.bpm.util.timer.Schedule;
import org.apache.log4j.Logger;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.element.FlowDiagram;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;

/**
 * 自动审批任务
 * 
 * @author Administrator
 * 
 */
public class AutoAuditJob extends Job {
	public final static Logger LOG = Logger.getLogger(AutoAuditJob.class);

	private Document doc;
	
	/**
	 * 流程实例
	 */
	private FlowStateRT instance;

	private String currid;

	private WebUser user;

	public AutoAuditJob() {
	}
	
	/**
	 * @param doc
	 * @param currid
	 * @param user
	 * @deprecated since 2.6
	 */
	@Deprecated
	public AutoAuditJob(Document doc, String currid, WebUser user) {
		this.doc = doc;
		this.currid = currid;
		this.user = user;
	}
	
	public AutoAuditJob(FlowStateRT instance, String currid, WebUser user) {
		this.doc = instance.getDocument();
		this.instance = instance;
		this.currid = currid;
		this.user = user;
	}

	public void run() {
		try {
			BillDefiVO flowVO = instance.getFlowVO();
			DocumentProcess docProcess =(DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,instance
					.getApplicationid());
			
			FlowStateRTProcess instanceProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(FlowStateRTProcess.class, instance.getApplicationid());

			Document po = (Document) docProcess.doView(instance.getDocid());
			Document vo = doc;
			if (po != null) {
				vo.setItems(po.getItems());
			}

			IRunner runner = JavaScriptFactory.getInstance(vo.get_params()
					.getSessionid(), doc.getApplicationid());
			runner.initBSFManager(vo, vo.get_params(), user,
					new ArrayList<ValidateMessage>());

			AutoNode currNode = (AutoNode) flowVO.findNodeById(currid);
			Collection<Node> nextNodeList = flowVO.getNextNodeList(currid);
			if (currNode != null && nextNodeList != null
					&& !nextNodeList.isEmpty()) {
				Collection<String> nextIdList = new ArrayList<String>();
				for (Iterator<Node> iterator = nextNodeList.iterator(); iterator
						.hasNext();) {
					Node node = iterator.next();
					nextIdList.add(node.id);
					if (!currNode.issplit) { // 非分散
						break;
					}
				}
				instance.setDocument(vo);
				String[] nextids = nextIdList
						.toArray(new String[nextIdList.size()]);
				instanceProcess.doApprove(new ParamsTable(), instance, currid,
						nextids, "", "", Environment.getInstance(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				PersistenceUtils.closeSessionAndConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Document getDocument() {
		return doc;
	}
	

	public FlowStateRT getInstance() {
		return instance;
	}

	public void setInstance(FlowStateRT instance) {
		this.instance = instance;
	}

	public Date getFirstTime() {
		try {
			BillDefiVO flowVO = instance.getFlowVO();
			FlowDiagram flowDiagram = flowVO.toFlowDiagram();

			Node node = flowDiagram.getNodeByID(currid);
			if (node instanceof AutoNode) {
				return ((AutoNode) node).getAuditDateTime();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static void main(String[] args) {
		Schedule.registerJob(new AutoAuditJob(), new Date());
	}
}
