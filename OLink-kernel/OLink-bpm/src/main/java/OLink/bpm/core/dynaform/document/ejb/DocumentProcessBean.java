package OLink.bpm.core.dynaform.document.ejb;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.document.dao.DocumentDAO;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.form.action.FormHelper;
import OLink.bpm.core.dynaform.form.action.ImpropriateException;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.pending.dao.PendingDAO;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcessBean;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.dynaform.work.ejb.WorkVO;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.FlowState;
import OLink.bpm.core.workflow.FlowType;
import OLink.bpm.core.workflow.element.Node;
import OLink.bpm.core.workflow.engine.AutoAuditJobManager;
import OLink.bpm.core.workflow.engine.StateMachine;
import OLink.bpm.core.workflow.notification.ejb.NotificationProcess;
import OLink.bpm.core.workflow.notification.ejb.NotificationProcessBean;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.intervention.ejb.FlowInterventionProcess;
import OLink.bpm.core.workflow.storage.runtime.intervention.ejb.FlowInterventionProcessBean;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.util.cache.CacheKey;
import OLink.bpm.util.cache.IMyCache;
import OLink.bpm.util.cache.IMyElement;
import OLink.bpm.util.cache.MyCacheManager;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorHIS;
import OLink.bpm.util.CreateProcessException;
import OLink.bpm.util.StringUtil;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.CirculatorProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcessBean;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRTProcessBean;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHIS;
import OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcess;
import OLink.bpm.util.cache.ICacheProvider;
import eWAP.core.Tools;

/**
 * DocumentProcessBean 为Document逻辑处理实现类.
 *
 * @author Marky
 */
public class DocumentProcessBean extends AbstractRunTimeProcessBean<Document> implements DocumentProcess {
    /**
     *
     */
    private static final long serialVersionUID = -3115132096995666143L;
    public final static Logger log = Logger.getLogger(DocumentProcessBean.class);

    public DocumentProcessBean(String applicationId) {
        super(applicationId);
    }

    /**
     * 根据主键,返回相应文档对象
     *
     * @param pk 文档主键
     * @return 文档对象
     */
    public ValueObject doView(String pk) throws Exception {
//		HttpSession oldSession = OBPMSessionContext.getInstance().getSession(sessionid)；
//		Document tmpDoc=(Document) ((DocumentDAO) getDAO()).find(pk);
//		log.info("currdoc sessionid is:"+tmpDoc.get_params().getSessionid());

        return getDAO().find(pk);
    }

    public Document doStartFlowOrUpdate(final Document doc, ParamsTable params, WebUser user) throws Exception {

        if (isNotStart(doc, params) && !StringUtil.isBlank(params.getParameterAsString("_flowid"))) { // 启动流程
            doStartFlow(doc, params, user);
        } else {
            doCreateOrUpdate(doc, user);
//			if(!StringUtil.isBlank(params.getParameterAsString("_isChangeAuditor"))){//优化性能 只有需要改变流程处理人时才执行此方法
//				updateActorRTList(doc, params, user);
//			}
        }

        return doc;
    }

    public Document doNewWithOutItems(Form form, WebUser user, ParamsTable params) throws Exception {
        // 清空所有field参数
        Collection<String> fieldNames = form.getAllFieldNames();
        for (Iterator<String> iter = fieldNames.iterator(); iter.hasNext(); ) {
            String name = iter.next();
            params.removeParameter(name);
        }

        Document newDoc = form.createDocument(params, user);
        // doCreate(newDoc);

        return newDoc;
    }

    public Document doNew(Form form, WebUser user, ParamsTable params) throws Exception {
        Document newDoc = form.createDocument(params, user);
        // doCreate(newDoc);

        return newDoc;
    }

    /**
     * 根据用户更新所属用户相应的文档，并对Field 校验.若文档无状态并有流程时开启流程.
     *
     * @param doc    Document对象
     * @param params 参数对象
     * @param user   用户对象
     * @return Document
     * @throws Exception
     */
    public Document doStartFlow(final Document doc, ParamsTable params, WebUser user) throws Exception {
        FlowStateRTProcess stateProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(
                FlowStateRTProcess.class, doc.getApplicationid());
        if (doc.getState() == null) {// 创建瞬态流程实例
            if (!StringUtil.isBlank(params.getParameterAsString("_flowid"))) {
                doc
                        .setState(stateProcess.createTransientFlowStateRT(doc, params.getParameterAsString("_flowid"),
                                user));
            } else {
                doCreateOrUpdate(doc, user);
                log.warn("Can not find the document corresponding to the flow");
            }
        }
        FlowStateRT instance = doc.getState();
        BillDefiVO flowVO = instance.getFlowVO();
        Node firstNode = StateMachine.getFirstNode(flowVO, user);

        if (firstNode != null) {
            doc.setInitiator(user.getId());// 设置流程发起人
            Node startNode = StateMachine.getStartNodeByFirstNode(flowVO, firstNode);
            if (startNode != null) {
                String currNodeId = startNode.id;
                String[] nextNodeIds = new String[]{firstNode.id};

                stateProcess.doApprove(params, doc.getState(), currNodeId, nextNodeIds, FlowType.START2RUNNING, "",
                        Environment.getInstance(), user);
            }
        } else {
            doCreateOrUpdate(doc, user);
        }

        return doc;
    }

    public Document doFlow(final Document doc, ParamsTable params, String currNodeId, String[] nextNodeIds,
                           String flowOption, String comment, WebUser user) throws Exception {
        FlowStateRTProcess stateProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(
                FlowStateRTProcess.class, doc.getApplicationid());
        // BillDefiVO flowVO = doc.getFlowVO();
        FlowStateRT instance = doc.getState(currNodeId);
        if (!flowOption.equals(FlowType.RUNNING2RUNNING_RETRACEMENT)
                && !FlowType.RUNNING2RUNNING_INTERVENTION.equals(flowOption))

			/*if (!isInNextNodeList(doc, currNodeId, instance.getFlowVO(), user, nextNodeIds)) {
				throw new DocumentException("{*[Could not submit to]*} "
						+ StateMachine.getNodeNameListStr(instance.getFlowVO(), nextNodeIds)
						+ " {*[please choose again]*}.", doc);
			}*/

            stateProcess.doApprove(params, instance, currNodeId, nextNodeIds, flowOption, comment, Environment
                    .getInstance(), user);

        return instance.getDocument();
    }

    /**
     * 批量审批文档(下一个节点无任何限制)
     *
     * @param docIds 文档ID数组
     * @param user   当前用户
     * @param evt    应用环境
     * @param params 请求参数
     * @return successCount 成功条数
     * @throws Exception
     */
    public int doBatchApprove(String[] docIds, WebUser user, Environment evt, ParamsTable params) throws Exception {
        return doBatchApprove(docIds, user, evt, params, null);
    }

    /**
     * 批量审批文档
     *
     * @param docIds      文档ID数组
     * @param user        当前用户
     * @param evt         应用环境
     * @param params      请求参数
     * @param allowedList 下一个节点限制列表
     * @return successCount 成功条数
     * @see Activity#getApproveLimit();
     */
    public int doBatchApprove(String[] docIds, WebUser user, Environment evt, ParamsTable params,
                              Collection<String> allowedList) throws Exception {
        if (docIds == null || docIds.length == 0) {
            throw new Exception("{*[Please choose one]*}");
        }

        // BillDefiVO flowVO = null;
        FlowStateRT instance = null;

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < docIds.length; i++) {
            Document doc = (Document) doView(docIds[i]);

            if (doc != null) {
                if (instance == null) {
                    instance = doc.getState();
                }

                if (instance != null) {
                    try {
                        IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), instance.getFlowVO()
                                .getApplicationid());
                        runner.initBSFManager(doc, params, user, new ArrayList<ValidateMessage>());

                        String currNodeId = "";
                        // FlowStateRT rt = doc.getState();
                        if (instance != null) {
                            NodeRT node = instance.getNodeRT(user);
                            if (node != null)
                                currNodeId = node.getNodeid();
                        }

                        Node nextNode = StateMachine.getNextAllowedNode(allowedList, instance.getFlowVO(), currNodeId);

                        doc.setLastFlowOperation(FlowType.getActionCode(nextNode));

                        doApprove(params, instance, currNodeId, new String[]{nextNode.id}, FlowType
                                .getActionCode(nextNode), "", evt, user);

                        successCount++;
                    } catch (Exception e) {
                        failCount++;
                    }
                }
            }
        }

        log.info("Approve " + docIds.length + " document(s) with " + failCount + " fail(s)");

        return successCount;
    }

    public void doApprove(ParamsTable params, final Document doc, String currNodeId, String[] nextNodeIds,
                          String flowOption, String comment, WebUser user) throws Exception {
        FlowStateRT instance = doc.getState();
        Environment evt = Environment.getInstance();

        doApprove(params, instance, currNodeId, nextNodeIds, flowOption, comment, evt, user);
    }

    /**
     * 审批文档
     *
     * @param doc         文档
     * @param flowVO      流程
     * @param currNodeId  当前节点ID
     * @param nextNodeIds 下一个节点ID
     * @param flowOption  流程操作
     * @param comment     流程日志
     * @param evt         应用环境
     * @param user        当前用户
     * @throws Exception
     * @deprecated since 2.6 move to FlowStateProcess
     */
    @Deprecated
    public void doApprove(ParamsTable params, FlowStateRT instance, String currNodeId, String[] nextNodeIds,
                          String flowOption, String comment, Environment evt, WebUser user) throws Exception {
        NotificationProcess notificationProcess = new NotificationProcessBean(getApplicationId());
        PendingProcess pendingProcess = new PendingProcessBean(getApplicationId());
        try {
            beginTransaction();
            // Document doc = instance.getDocument();
            Document origDoc = (Document) instance.getDocument().clone();

            // 出错后要处理
            instance.getDocument().setAuditdate(new Date());
            instance.getDocument().setAudituser(user.getId());
            instance.getDocument().setLastFlowOperation(flowOption);
            instance.getDocument().setLastmodifier(user.getId());

            // 为批量审批时添加审批备注_attitude。 add by by dolly 2011-3-10
            if (comment == null || comment.equals("")) {
                if (params.getParameterAsString("_attitude") != null && !params.getParameterAsString("_attitude").equals("")) {
                    comment = params.getParameterAsString("_attitude");
                } else if (params.getParameterAsString("_remark") != null && !params.getParameterAsString("_remark").equals("")) {
                    comment = params.getParameterAsString("_remark");
                }
            }

            // 处理提交或回退提醒
            if (!flowOption.equals(FlowType.START2RUNNING) && !flowOption.equals(FlowType.SUSPEND2RUNNING)
                    && !flowOption.equals(FlowType.AUTO2RUNNING)) {
                notificationProcess.notifySender(origDoc, instance.getFlowVO(), user); // 送出提醒
            }

            StateMachine.doFlow(params, instance, currNodeId, nextNodeIds, user, flowOption, comment, evt);

            // updateFlowState(doc);

            doCreateOrUpdate(instance.getDocument(), user);
            PendingVO pending = (PendingVO) pendingProcess.doView(instance.getDocument().getId());

            if (flowOption.equals(FlowType.RUNNING2RUNNING_NEXT) || flowOption.equals(FlowType.START2RUNNING)
                    || flowOption.equals(FlowType.AUTO2RUNNING)) {
                notificationProcess.notifyCurrentAuditors(instance.getDocument(), pending, instance.getFlowVO()); // 到达提醒
            } else if (flowOption.equals(FlowType.RUNNING2RUNNING_BACK)) {
                notificationProcess.notifyRejectees(instance.getDocument(), pending, instance.getFlowVO()); // 回退提醒
            }

            commitTransaction();
            // 启动自动审批任务
            AutoAuditJobManager.startJobByDoc(instance.getDocument());
        } catch (Exception e) {
            rollbackTransaction();
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isInNextNodeList(Document doc, String currNodeId, BillDefiVO flowVO, WebUser user,
                                    String[] nextNodeIds) throws Exception {
        Collection<Node> nodeList = new ArrayList<Node>();
        Collection<Node> nextNodeList = flowVO.getNextNodeList(currNodeId);
        Collection<Node> backNodeList = StateMachine.getBackToNodeList(doc, flowVO, currNodeId, user);

        nodeList.addAll(nextNodeList);
        nodeList.addAll(backNodeList);

        for (int i = 0; i < nextNodeIds.length; i++) {

            boolean flag = false;

            for (Iterator<Node> iter = nodeList.iterator(); iter.hasNext(); ) {
                Node node = iter.next();
                if (nextNodeIds[i] != null && nextNodeIds[i].endsWith(";")) {
                    nextNodeIds[i] = nextNodeIds[i].substring(0, nextNodeIds[i].length() - 1);
                }
                if (nextNodeIds[i].equals("") || nextNodeIds[i].equals(node.id)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            }
        }

        return true;
    }

    public boolean isDocSaveUser(Document doc, ParamsTable params, WebUser user) throws Exception {

        boolean isDocSaveUser = true;

        // if (!doc.getIstmp()) {
        if (doc.getParentid() != null && doc.getParentid().trim().length() > 0) {
            isDocSaveUser = StateMachine.isDocSaveUser(doc.getParent(), user);
        } else {
            isDocSaveUser = StateMachine.isDocSaveUser(doc, user);
        }
        // }

        return isDocSaveUser;
    }

    public Collection<ValidateMessage> doValidate(final Document doc, ParamsTable params, WebUser user)
            throws Exception {
        FormProcess fb = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
        Form form = (Form) fb.doView(doc.getFormid());
        if (form != null) {
            return form.validate(doc, params, user);
        }
        return null;
    }

    /**
     * 改变审批角色列表
     *
     * @param doc    文档
     * @param params 页面参数
     * @param user   当前用户
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public void updateActorRTList(Document doc, ParamsTable params, WebUser user) throws Exception {
        NodeRTProcess nodeRTProcess = new NodeRTProcessBean(getApplicationId());
        PendingProcess pendingProcess = new PendingProcessBean(getApplicationId());

        FlowStateRT state = doc.getState();
        String currNodeId = params.getParameterAsString("_currid");
        if (StringUtil.isBlank(currNodeId) || state == null) {
            return;
        }

        String auditorJSON = doc.getAuditorList();

        try {
            Map<?, ?> map = JSONObject.fromObject(auditorJSON);
            BillDefiVO flowVO = doc.getFlowVO();
            Collection<NodeRT> noderts = state.getNoderts();
            for (Iterator<NodeRT> iterator = noderts.iterator(); iterator.hasNext(); ) {
                // NodeRT nodeRT = (NodeRT) iterator.next();
                NodeRT nodeRT = iterator.next();
                Collection<?> actorIdList = (Collection<?>) map.get(currNodeId);
                if (nodeRT.getNodeid().equals(currNodeId)) {
                    String[] actorIdArray = actorIdList.toArray(new String[actorIdList.size()]);
                    nodeRTProcess.doUpdateByActorIds(nodeRT, doc, flowVO, actorIdArray);
                }
            }

            pendingProcess.doUpdateByDocument(doc, user);
        } catch (JSONException e) {
            log.warn("", e);
        }
    }

    /**
     * 判断是否已占用
     *
     * @param doc
     * @return
     * @throws Exception
     */
    public boolean isImpropriated(Document doc) throws Exception {
        return doc != null && doc.getVersions() != (((DocumentDAO) getDAO()).findVersions(doc.getId()));
    }

    /**
     * 将新的文档属性与旧的合并,并保留旧文档所有项目(Item)的值
     *
     * @param dest 新Document
     * @return 合并后的Document
     * @throws Exception
     */
    public Document mergePO(Document dest, WebUser webUser) throws Exception {
        if (dest != null && dest.getId() != null) {
            Document orig = (Document) getDAO().find(dest.getId());
            if (orig == null && webUser != null) {
                orig = (Document) webUser.getFromTmpspace(dest.getId());
            }

            try {
                if (orig != null) {
                    dest.set_issubdoc(orig.get_issubdoc());
                    dest.setApplicationid(orig.getApplicationid());
                    dest.setAuditdate(orig.getAuditdate());
                    dest.setAudituser(orig.getAudituser());
                    dest.setAudituserid(orig.getAudituserid());
                    dest.setAuditusers(orig.getAuditusers());
                    dest.setAuthor(orig.getAuthor());
                    dest.setAuthor(orig.getAuthor());
                    dest.setCreated(orig.getCreated());
                    dest.setDomainid(orig.getDomainid());
                    dest.setIstmp(orig.getIstmp());
                    dest.setLastFlowOperation(orig.getLastFlowOperation());
                    dest.setLastmodified(orig.getLastmodified());
                    dest.setLastmodifier(orig.getLastmodifier());
                    dest.setEditAble(orig.isEditAble(webUser));
                    dest.setParent(orig.getParent());
                    dest.setParent(orig.getParent());
                    dest.setInitiator(orig.getInitiator());
                    dest.setMappingId(orig.getMappingId());
                    dest.setMulitFlowState(orig.isMulitFlowState());
                }
                return dest;
            } catch (Exception e) {
                e.printStackTrace();
                return dest;
            }
        }
        return dest;
    }

    /**
     * 根据符合DQL语句以及应用标识查询,返回文档的DataPackage.
     * <p>
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql DQL语句
     * @throws Exception
     * @retur 文档的DataPackage
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    public DataPackage<Document> queryByDQL(String dql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryByDQL(dql, domainid);
    }

    /**
     * 根据符合DQL语句以及应用标识查询,返回按设置缓存的此模块下符合条件的文档的DataPackage.
     * <p>
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql DQL语句
     * @return 设置缓存的此模块下符合条件的文档的DataPackage
     * @throws Exception
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    @SuppressWarnings("unchecked")
    public DataPackage<Document> queryByDQLWithCache(String dql, String domainid) throws Exception {

        DataPackage<Document> result = null;
        try {
            // signal.sessionSignal++;

            String cacheName = "OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean.queryByDQLWithCache(java.lang.String,java.lang.String)";

            ICacheProvider provider = MyCacheManager.getProviderInstance();
            if (provider != null) {
                if (provider.clearByCacheName(cacheName)) {
                    JavaScriptFactory.clear();
                    log.info("##CLEAN-CACHE-->>" + cacheName);
                }

                IMyCache cache = provider.getCache(cacheName);
                if (cache != null) {

                    Class<?>[] parameterTypes = new Class<?>[2];
                    parameterTypes[0] = String.class;
                    parameterTypes[1] = String.class;

                    Method method = DocumentProcessBean.class.getMethod("queryByDQLWithCache", parameterTypes);

                    Object[] methodParameters = new Object[2];
                    methodParameters[0] = dql;
                    methodParameters[1] = "";
                    CacheKey cacheKey = new CacheKey(this, method, methodParameters);

                    IMyElement cachedElement = cache.get(cacheKey);

                    if (cachedElement != null && cachedElement.getValue() != null) {
                        log.info("@@CACHED-METHOD-->>" + cacheKey);
                        result = (DataPackage<Document>) cachedElement.getValue();
                    } else {

                        result = ((DocumentDAO) getDAO()).queryByDQL(dql, domainid);
                        cache.put(cacheKey, result);

                        return result;
                    }
                }
            }

            return result;

        } catch (Exception t) {
            throw t;
        } finally {
            // signal.sessionSignal--;
            // if (signal.sessionSignal <= 0) {
            // PersistenceUtils.closeSession();
            // }
        }
    }

    /**
     * 根据符合DQL语句以及应用标识查询并分页,返回文档的DataPackage.
     * <p>
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql   dql语句
     * @param page  当前页码
     * @param lines 每页显示行数
     * @return 文档的DataPackage
     * @throws Exceptio
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    public DataPackage<Document> queryByDQLPage(String dql, int page, int lines, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryByDQLPage(dql, page, lines, domainid);
    }

    /**
     * 根据符合DQL语句以及应用标识查询单个文档
     *
     * @param dql
     * @return
     * @throws Exception
     */
    public Document findByDQL(String dql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).findByDQL(dql, domainid);
    }

    /**
     * 根据符合SQL语句以及应用标识查询单个文档
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public Document findBySQL(String sql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).findBySQL(sql, domainid);
    }

    /**
     * 根据符合DQL语句,参数以及应用标识查询,返回按设置缓存的此模块下符合条件的文档的DataPackage.
     * <p>
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql    查询语句
     * @param params 参数对象
     * @return 此模块下符合条件的文档的DataPackage
     * @throws Exception
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     * @see ParamsTable#params
     */
    public DataPackage<Document> queryByDQLWithCache(String dql, ParamsTable params, String domainid) throws Exception {
        DataPackage<Document> dpgs = ((DocumentDAO) getDAO()).queryByDQL(dql, params, domainid);
        return dpgs;
    }

    /**
     * 获取DocumentDAO接口
     *
     * @return DocumentDAO接口
     * @throws Exception
     */
    protected IRuntimeDAO getDAO() throws Exception {
        // return new OracleDocStaticTblDAO(getConnection());
        // ApplicationVO app=getApplicationVO(getApplicationId());

        return new RuntimeDaoManager().getDocStaticTblDAO(getConnection(), getApplicationId());
    }

    protected PendingDAO getPendingDAO() throws Exception {
        return (PendingDAO) new RuntimeDaoManager().getPendingDAO(getConnection(), getApplicationId());
    }

    /**
     * 根据父文档ID(primary key)与子表单名查询，返回所属父Document的子Document集合.
     *
     * @param parentid 父文档ID(primary key)
     * @param formName 子表单名
     * @return 所属父Document的子Document集合.
     * @throws Exception
     */
    public Collection<Document> queryByParentID(String parentid, String formName) throws Exception {
        return ((DocumentDAO) getDAO()).queryByParentID(parentid, formName);
    }

    /**
     * 根据符合DQL语句,最后修改文档日期,以及应用标识查询并分页,返回文档的DataPackage.
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql   DQL 语句
     * @param date  最后修改文档日期
     * @param page  当前页码
     * @param lines 每页显示的行数
     * @return 符合条件的文档的DDataPackage
     * @throws Exception
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    public Iterator<Document> queryByDQLAndDocumentLastModifyDate(String dql, Date date, int page, int lines,
                                                                  String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryByDQLAndDocumentLastModifyDate(dql, date, page, lines, domainid);
    }

    public long getNeedExportDocumentTotal(String dql, Date date, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).getNeedExportDocumentTotal(dql, date, domainid);

    }

    /**
     * 根据符合DQL语句,参数以及应用标识查询,返回文档的DataPackage.
     * <p>
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql    DQL语句
     * @param params 参数
     * @return 符合条件的文档的DataPackage
     * @throws Exception
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    public DataPackage<Document> queryByDQL(String dql, ParamsTable params, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryByDQL(dql, params, domainid);
    }

    /**
     * 根据DQL语句,参数表以及应用标识查询 ,返回文档的DataPackage.
     * <p>
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql    DQL语句
     * @param params 参数
     * @param page   当前页码
     * @param lines  每页显示的行数
     * @return 符合条件的文档的DataPackage
     * @throws Exception
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    public DataPackage<Document> queryByDQLPage(String dql, ParamsTable params, int page, int lines, String domainid)
            throws Exception {
        return ((DocumentDAO) getDAO()).queryByDQLPage(dql, params, page, lines, domainid);
    }

    /**
     * 根据DQL语句以及文档某字段名查询,返回此文档此字段总和
     *
     * @param dql       dql语句
     * @param fieldName 字段名
     * @return 文档此字段总和
     * @throws Exception
     */
    public double sumByDQL(String dql, String fieldName, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).sumByDQL(dql, fieldName, domainid);
    }

    /**
     * 根据符合DQL执行语句以及应用标识查询并分页,返回文档的数据集
     *
     * @param dql  DQL语句
     * @param pos  页码
     * @param size 每页显示行数
     * @return 文档的数据集
     * @throws Exceptio
     */
    public Iterator<Document> iteratorLimitByDQL(String dql, int pos, int size, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).iteratorLimitByDQL(dql, pos, size, domainid);
    }

    /**
     * 根据符合DQL语句以及应用标识查询并分页,返回文档的集合
     *
     * @param dql  DQL语句
     * @param pos  当前页码
     * @param size 每页显示的行数
     * @return 文档的集合
     */
    public Collection<Document> queryLimitByDQL(String dql, int pos, int size, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryLimitByDQL(dql, pos, size, domainid);
    }

    /**
     * 根据符合DQL语句,模块名,参数以及应用标识查询并分页,返回按设置缓存的此模块下符合条件的文档的DataPackage.
     * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
     *
     * @param dql    DQL语句
     * @param params 参数对象
     * @param page   当前页码
     * @param lines  每页显示的行数
     * @return 此模块下符合条件的文档的DataPackage
     * @throws Exception
     * @see DataPackage#datas
     * @see DataPackage#getPageNo()
     * @see DataPackage#getLinesPerPage()
     * @see DataPackage#getPageCount()
     * @see DataPackage#datas
     */
    public DataPackage<Document> queryByDQLPageWithCache(String dql, ParamsTable params, int page, int lines,
                                                         String domainid) throws Exception {

        DataPackage<Document> dpgs = queryByDQLPage(dql, params, page, lines, domainid);
        return dpgs;
    }

    /**
     * 根据用户创建新Document
     *
     * @param user webuser
     * @param vo   ValueObject
     */
    public void doCreate(ValueObject vo, WebUser user) throws Exception {
        if (vo instanceof Document) {
            try {
                beginTransaction();
                //1.持久化文档
                if (StringUtil.isBlank(vo.getId())) {
                    vo.setId(Tools.getSequence());
                }
                if (((Document) vo).getLastmodified() == null) {
                    ((Document) vo).setLastmodified(new Date());
                }
                Document doc = (Document) vo;
                doc.setIstmp(false);
                ((DocumentDAO) getDAO()).createDocument((Document) vo);
                createAuth(doc);

                //2. 创建或更新待办列表
                //2016-11-08 note by zhangbin
//				PendingProcess pendingProcess = new PendingProcessBean(getApplicationId());
//				pendingProcess.doCreateOrRemoveByDocument((Document) vo,user);

                //3. 创建 流程干预信息
                if (vo instanceof Document && !StringUtil.isBlank(((Document) vo).getStateid())) {
                    FlowInterventionProcess interventionProcess = new FlowInterventionProcessBean(getApplicationId());
                    interventionProcess.doCreateByDocument((Document) vo, user);
                }

                commitTransaction();

                //4.成功持久化后刷新上下文保存的文档版本
                ((Document) vo).setVersions(((Document) vo).getVersions() + 1);
            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        }
    }

    /**
     * 根据主键,删除对应值对象
     *
     * @param id 主键
     */
    public void doRemove(String id) throws Exception {
        try {
            Document doc = (Document) getDAO().find(id);

            beginTransaction();

            // 删除相关的flowStateRT
            FlowStateRTProcess instanceProcess = new FlowStateRTProcessBean(getApplicationId());
            Collection<FlowStateRT> instances = instanceProcess.getFlowStateRTsByDocId(id);
            if (instances != null && !instances.isEmpty()) {
                for (FlowStateRT instance : instances) {
                    instanceProcess.doRemove(instance.getId());
                }
            }
            // 删除相关的Auth表中信息
            ((DocumentDAO) getDAO()).removeAuthByDoc(doc);

            ((DocumentDAO) getDAO()).removeDocument(doc);
            getPendingDAO().remove(id);

            // 删除 流程干预信息
            FlowInterventionProcess interventionProcess = new FlowInterventionProcessBean(getApplicationId());
            interventionProcess.doRemove(id);


            //删除 流程代阅信息
            CirculatorProcess cProcess = (CirculatorProcess) ProcessFactory.createRuntimeProcess(CirculatorProcess.class, getApplicationId());
            cProcess.doRemoveByForeignKey("DOC_ID", id);

            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    public void doCreate(ValueObject vo) throws Exception {
        if (vo instanceof Document) {
            try {
                if (StringUtil.isBlank(vo.getId())) {
                    vo.setId(Tools.getSequence());
                }
                // vo.setSortId(Tools.getTimeSequence());

                if (((Document) vo).getLastmodified() == null) {
                    ((Document) vo).setLastmodified(new Date());
                }

                // ((Document) vo).setIstmp(false);

                Document doc = (Document) vo;

				/*
				 * 判断doc的父doc是否存在且非临时文档
				 * 如果校验父表单是否为临时会导致创建子文档时不显示
				 */
//				if (!checkParentExistAndNotIstmp(doc.getParentid())) {
//					doc.setIstmp(true);
//				} else {
                doc.setIstmp(false);
//				}

                beginTransaction();
                ((DocumentDAO) getDAO()).createDocument((Document) vo);
                createAuth(doc);
                //2. 创建或更新待办列表
                PendingProcess pendingProcess = new PendingProcessBean(getApplicationId());
                pendingProcess.doCreateOrRemoveByDocument((Document) vo, null);
                commitTransaction();
                //成功持久化后刷新上下文保存的文档版本
                ((Document) vo).setVersions(((Document) vo).getVersions() + 1);

            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        } else {
            throw new Exception("this is not Document ValueObject!");
        }
    }

    /**
     * 创建或更新
     *
     * @param vo   文档值对象
     * @param user WebUser
     * @throws Exception
     */
    public void doCreateOrUpdate(ValueObject vo, WebUser user) throws Exception {
        if (((DocumentDAO) getDAO()).isExist(vo.getId())) {
            doUpdate(vo, user, true);
        } else {
            doCreate(vo, user);
        }
    }


    /**
     * 根据用户,更新文档对象.
     *
     * @param object 值对象
     * @param user   用户
     */
    public void doUpdate(ValueObject object, WebUser user,
                         boolean withVersionControl) throws Exception {
        if (object instanceof Document) {
            try {
                beginTransaction();

                Document doc = (Document) object;

                createAuth(doc);

                doc.setLastmodifier(user.getId());
                doUpdate(doc, withVersionControl);
                // 更新待办列表
                //2016-11-08 note by zhangbin
//				PendingProcess pendingProcess = new PendingProcessBean(
//						getApplicationId());
//				pendingProcess.doUpdateByDocument(doc, user);
                // 更新流程干预信息
                FlowInterventionProcess interventionProcess = new FlowInterventionProcessBean(
                        getApplicationId());
                interventionProcess.doUpdateByDocument(doc, user);
                commitTransaction();
            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        }
    }

    /**
     * 判断本文档的上级文档是否存在并且非临时文档
     * <p>
     * 2011-07-08
     *
     * @param parentId
     * @return
     * @author keezzm
     */
    private boolean checkParentExistAndNotIstmp(String parentId) {
        try {
            if (StringUtil.isBlank(parentId)) {
                return true;
            } else {
                Document parent = (Document) doView(parentId);
                if (parent != null && !parent.getIstmp()) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doCreate(ValueObject[] vos) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    public void doCreate(Collection<ValueObject> vos) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    public DataPackage<Document> doQuery(ParamsTable params, WebUser user) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    /**
     * 删除主键数组值对象组
     *
     * @param pks 主键数组
     */
    public void doRemove(String[] pks) throws Exception {
        try {
            beginTransaction();

            if (pks != null) {
                for (int i = 0; i < pks.length; i++) {
                    String id = pks[i];
                    if (id.endsWith(";"))
                        id = id.substring(0, id.length() - 1);
                    Document doc = (Document) doView(id);

                    if (doc != null) {
                        // 删除相关的flowStateRT
                        FlowStateRTProcess instanceProcess = new FlowStateRTProcessBean(getApplicationId());
                        Collection<FlowStateRT> instances = instanceProcess.getFlowStateRTsByDocId(id);
                        if (instances != null && !instances.isEmpty()) {
                            for (FlowStateRT instance : instances) {
                                instanceProcess.doRemove(instance.getId());
                            }
                        }

                        // 删除相关的Auth表中信息
                        ((DocumentDAO) getDAO()).removeAuthByDoc(doc);

                        ((DocumentDAO) getDAO()).removeDocument(doc);
                        getPendingDAO().remove(id);

                        // 删除 流程干预信息
                        FlowInterventionProcess interventionProcess = new FlowInterventionProcessBean(
                                getApplicationId());
                        interventionProcess.doRemove(id);

                        //删除 流程代阅信息
                        CirculatorProcess cProcess = (CirculatorProcess) ProcessFactory.createRuntimeProcess(CirculatorProcess.class, getApplicationId());
                        cProcess.doRemoveByForeignKey("DOC_ID", id);

                    }
                }
            }
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    public void doRemove(ValueObject vo) throws Exception {

        // 删除相关的flowStateRT
        FlowStateRTProcess instanceProcess = new FlowStateRTProcessBean(getApplicationId());
        Collection<FlowStateRT> instances = instanceProcess.getFlowStateRTsByDocId(vo.getId());
        if (instances != null && !instances.isEmpty()) {
            for (FlowStateRT instance : instances) {
                instanceProcess.doRemove(instance.getId());
            }
        }
        // 删除相关的Auth表中信息
        ((DocumentDAO) getDAO()).removeAuthByDoc((Document) vo);

        ((DocumentDAO) getDAO()).removeDocument((Document) vo);

        // 删除 流程干预信息
        FlowInterventionProcess interventionProcess = new FlowInterventionProcessBean(getApplicationId());
        interventionProcess.doRemove(vo.getId());

        //删除 流程代阅信息
        CirculatorProcess cProcess = (CirculatorProcess) ProcessFactory.createRuntimeProcess(CirculatorProcess.class, getApplicationId());
        cProcess.doRemoveByForeignKey("DOC_ID", vo.getId());
    }

    public Collection<Document> doSimpleQuery(ParamsTable params) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    public Collection<Document> doSimpleQuery(ParamsTable params, String application) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    /**
     * 更新文档对象数组
     *
     * @param vos 文档对象数组
     */

    public void doUpdate(ValueObject[] vos) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    /**
     * 更新文档
     *
     * @param vo
     */
    public void doUpdate(Collection<ValueObject> vos) throws Exception {
        throw new Exception("this method is not be realized!");
    }

    /**
     * 更新文档
     *
     * @param doc    Document对象
     * @param params 参数对象
     * @param user   用户对象
     * @return
     * @throws Exception
     */
    public void doUpdate(ValueObject vo) throws Exception {
        doUpdate(vo, false);
    }

    public void doUpdate(ValueObject vo, boolean withVersionControl)
            throws Exception {
        try {
            Document doc = (Document) vo;
            if (withVersionControl && isImpropriated(doc)) {
                throw new ImpropriateException("{*[core.util.cannotsave]*}");
            }

			/*
			 * 判断doc的父doc是否存在且非临时文档
			 */
            if (!checkParentExistAndNotIstmp(doc.getParentid())) {
                doc.setIstmp(true);
            } else {
                doc.setIstmp(false);
            }

            beginTransaction();
            Document oldDoc = (Document) doView(doc.getId());

            // if (oldDoc != null) {// 版本并发控制
            // int currentVersion = oldDoc.getVersions();
            // int oldVersion = doc.getVersions();
            // if (oldVersion < currentVersion)
            // throw new
            // Exception("Version is inconsistent,you should update this document");
            // }

            doc.setLastmodified(new Date());

            Document compareDoc = new Document();
            boolean flag = compareDoc.compareFieldValue(oldDoc, doc);
            if (flag) {
                if (oldDoc != null
                        && !StringUtil.isBlank(oldDoc.getLastmodifier())) {
                    doc.setLastmodifier(oldDoc.getLastmodifier());
                }
            }
            ((DocumentDAO) getDAO()).updateDocument(doc);

            // 添加日志记录
            if (!flag) {
                TableMapping tableMapping = doc.getForm().getTableMapping();
                String tblname = tableMapping
                        .getTableName(DQLASTUtil.TABEL_TYPE_LOG);
                boolean isLogTableExists = ((DocumentDAO) getDAO())
                        .checkTable(tblname);
                if (isLogTableExists) {
                    ((DocumentDAO) getDAO()).createDocument(oldDoc,
                            DQLASTUtil.TABEL_TYPE_LOG);
                }
            }

            commitTransaction();
            // 成功持久化后刷新上下文保存的文档版本
            ((Document) vo).setVersions(((Document) vo).getVersions() + 1);
        } catch (ImpropriateException e) {
            rollbackTransaction();
            throw e;
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    public Document doViewByCondition(String formName, Map<?, ?> condition, WebUser user) throws Exception {
        StringBuffer dql = new StringBuffer();
        dql.append("$formname='" + formName + "'");
        for (Iterator<?> iterator = condition.entrySet().iterator(); iterator.hasNext(); ) {
            Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
            dql.append(" AND " + entry.getKey() + "='" + entry.getValue() + "'");
        }

        Document doc = findByDQL(dql.toString(), user.getDomainid());
        if (StringUtil.isBlank(doc.getId())) {
            return null;
        }

        return doc;
    }

    /**
     * 根据用户,更新文档对象.
     *
     * @param object 值对象
     * @param user   用户
     */
    public void doUpdate(ValueObject object, WebUser user) throws Exception {
        if (object instanceof Document) {
            try {
                beginTransaction();

                Document doc = (Document) object;

                createAuth(doc);

                doc.setLastmodifier(user.getId());
                doUpdate(doc);
                // 更新待办列表
                PendingProcess pendingProcess = new PendingProcessBean(getApplicationId());
                pendingProcess.doUpdateByDocument(doc, user);
                // 更新流程干预信息
                FlowInterventionProcess interventionProcess = new FlowInterventionProcessBean(getApplicationId());
                interventionProcess.doUpdateByDocument(doc, user);
                commitTransaction();
            } catch (Exception e) {
                rollbackTransaction();
                throw e;
            }
        }
    }


    /* (non-Javadoc)
     * @see DocumentProcess#doChangeAuditor(Document, ParamsTable, WebUser)
     */
    public void doChangeAuditor(Document doc, ParamsTable params, WebUser user)
            throws Exception {
        try {
            beginTransaction();
            //1.更改流程审批人、待办信息
            updateActorRTList(doc, params, user);

            //2.创建历史记录
            FlowStateRT instance = doc.getState();
            RelationHISProcess process = StateMachine.getRelationHISProcess(instance.getApplicationid());
            RelationHIS his = process.doViewLastByDocIdAndFolowStateId(doc.getId(), instance.getId());
            ActorHIS actorHIS = null;
            if (user.getEmployer() != null) {
                actorHIS = new ActorHIS((new WebUser(user.getEmployer())));
                actorHIS.setAgentid(user.getId());
                actorHIS.setAgentname(user.getName());
            } else {
                actorHIS = new ActorHIS(user);
            }
            String attitude = params.getParameterAsString("_attitude");
            actorHIS.setProcesstime(new Date());
            actorHIS.setAttitude(attitude);
            his.getActorhiss().add(actorHIS);
            StringBuffer _attitude = new StringBuffer();
            _attitude.append(his.getAttitude());
            _attitude.append(",").append(attitude);
            his.setAttitude(_attitude.toString()); // 审批意见
            process.doUpdate(his);

            //3.更新文档
            //doUpdate(doc, user);

            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    /**
     * 新建权限记录到Auth_动态表
     *
     * @param doc
     * @throws Exception
     */
    public void createAuth(Document doc) throws Exception {
        FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
        Form form = (Form) formProcess.doView(doc.getFormid());

        Collection<String> condition = new ArrayList<String>();
        if (doc.getState() != null) {
            doc.getState().setActors(null);
        }
        // 从流程中获取权限值(用户)
        if (doc.getState() != null && doc.getState().getActors() != null) {
            for (Iterator<?> iterator = doc.getState().getActors().iterator(); iterator.hasNext(); ) {
                ActorRT actorrt = (ActorRT) iterator.next();
                if (actorrt.isPending()) {
                    condition.add(actorrt.getActorid());
                }
            }
        }

        // 从表单的权限字段中获取权限值(部门)
        FormHelper helper = new FormHelper();
        Map<?, ?> map = helper.getAllAuthorityFields(doc.getFormid());
        if (!map.isEmpty()) {
            for (Iterator<?> iterator = map.values().iterator(); iterator.hasNext(); ) {
                Object obj = iterator.next();
                if (obj != null && !obj.equals("")) {
                    String valueStr = doc.getItemValueAsString(obj.toString());
                    if (!StringUtil.isBlank(valueStr)) {
                        String[] values = valueStr.split(";");
                        for (int i = 0; i < values.length; i++) {
                            condition.add(values[i]);
                        }
                    }
                }
            }
        }

        ((DocumentDAO) getDAO()).createAuthDocWithCondition(form.getName(), doc.getId(), condition);
    }

    /**
     * 根据父表单主键,返回Document集合
     *
     * @param parentid
     * @return 符合父表单主键的Document集合
     * @throws Exception
     */
    public Collection<Document> queryByParentID(String parentid) throws Exception {
        return ((DocumentDAO) getDAO()).queryByParentID(parentid);
    }

    public static DocumentProcess createMonitoProcess(String applicationid) throws CreateProcessException {
        DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
                applicationid);
        return process;
    }

    public DataPackage<Document> queryBySQL(String sql, ParamsTable params, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryBySQL(sql, params, domainid);
    }

    // vinsun
    public DataPackage<Document> queryBySQL(String sql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryBySQL(sql, domainid);
    }

    public Collection<Document> queryBySQL(String sql) throws Exception {
        return ((DocumentDAO) getDAO()).queryBySQL(sql, 1, Integer.MAX_VALUE, "");
    }

    public DataPackage<Document> queryBySQLPage(String sql, int page, int lines, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryBySQLPage(sql, page, lines, domainid);
    }

    public DataPackage<Document> queryBySQLPage(String sql, ParamsTable params, int page, int lines, String domainid)
            throws Exception {
        return ((DocumentDAO) getDAO()).queryBySQLPage(sql, params, page, lines, domainid);
    }

    public long countByDQL(String dql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).countByDQL(dql, domainid);
    }

    public double sumBySQL(String sql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).sumBySQL(sql, domainid);
    }

    public long countBySQL(String sql, String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).countBySQL(sql, domainid);
    }

    /**
     * 尚未开启流程
     *
     * @param state 当前流程状态
     * @return
     */
    public boolean isNotStart(Document doc, ParamsTable params) {
        try {
            return !(doc.getState() != null && doc.getState().getState() != FlowState.START);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Collection<Document> queryModifiedDocuments(Document doc) throws Exception {
        Collection<Document> col = null;
        col = ((DocumentDAO) getDAO()).queryModifiedDocuments(doc);
        return col;
    }

    /**
     * 创建新文档包含子文档
     *
     * @param form     表单
     * @param user     当前用户
     * @param params   参数
     * @param children 所包含的子文档
     * @return 新文档
     */
    public Document doNewWithChildren(Form form, WebUser user, ParamsTable params, Collection<Document> children)
            throws Exception {
        Document root = null;
        try {
            beginTransaction();
            root = form.createDocument(params, user);
            recursiveCreate(root, children);

            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }

        return root;
    }

    /**
     * 递归创建
     *
     * @param parent   父文档
     * @param children 子文档
     * @throws Exception
     */
    protected void recursiveCreate(Document parent, Collection<Document> children) throws Exception {
        try {
            doCreate(parent);

            if (children != null && !children.isEmpty()) {
                for (Iterator<Document> iterator = children.iterator(); iterator.hasNext(); ) {
                    Document child = iterator.next();
                    Collection<Document> nestedChildren = child.getChilds();

                    child.setId(Tools.getSequence());
                    child.setParent(parent);
                    if (nestedChildren != null && !nestedChildren.isEmpty()) {
                        recursiveCreate(child, nestedChildren);
                    } else {
                        doCreate(child);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void doRemoveWithChildren(ValueObject vo) throws Exception {
        Document root = null;
        try {
            // beginTransaction();
            root = (Document) vo;
            // recursiveRemove(root, children);
            doRemove(root.getId());
            // commitTransaction();
        } catch (Exception e) {
            // rollbackTransaction();
            throw e;
        }
    }

    /**
     * 根据表单名称删除
     *
     * @throws Exception
     */
    public void doRemoveByFormName(Form form) throws Exception {
        try {
            beginTransaction();
            ((DocumentDAO) getDAO()).removeDocumentByForm(form);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    /**
     * 清楚字段数据
     *
     * @throws Exception
     */
    public void doRemoveDocByFields(Form form, String[] fields) throws Exception {
        try {
            beginTransaction();
            ((DocumentDAO) getDAO()).removeDocumentByField(form, fields);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    /**
     * 递归删除
     *
     * @param parent   父文档
     * @param children 子文档
     * @throws Exception
     */
    protected void recursiveRemove(Document parent, Collection<Document> children) throws Exception {
        try {

            if (children != null && !children.isEmpty()) {
                for (Iterator<Document> iterator = children.iterator(); iterator.hasNext(); ) {
                    Document child = iterator.next();
                    Collection<Document> nestedChildren = child.getChilds();

                    if (nestedChildren != null && !nestedChildren.isEmpty()) {

                        // 删除相关的Auth表中信息
                        ((DocumentDAO) getDAO()).removeAuthByDoc(child);

                        recursiveRemove(child, nestedChildren);
                        doRemove(child);
                    } else {
                        doRemove(child);
                    }
                }
            }
            doRemove(parent);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public DataPackage<Document> queryByDQLDomainName(String dql, String domainName) throws Exception {
        DomainProcess process = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
        DomainVO vo = process.getDomainByName(domainName);
        if (vo != null) {
            return ((DocumentDAO) getDAO()).queryByDQL(dql, vo.getId());
        } else {
            return null;
        }
    }

    /**
     * 创建文档头. 此文档头用来保存不同Document的信息. 此方法实现数据库文档表的相应字列插入相应Document属性的值.
     *
     * @param doc Document对象
     * @throws Exception
     */
    public void createDocumentHead(Document doc) throws Exception {
        ((DocumentDAO) getDAO()).createDocumentHead(doc);
    }

    public DataPackage<WorkVO> queryWorkBySQLPage(ParamsTable params, int page, int lines, WebUser user)
            throws Exception {
        return ((DocumentDAO) getDAO()).queryWorkBySQLPage(params, page, lines, user);
    }

    public DataPackage<WorkVO> queryWorks(ParamsTable params, WebUser user) throws Exception {
        String _currpage = params.getParameterAsString("_currpage");
        String _pagelines = params.getParameterAsString("_pagelines");

        int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
        int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
        return ((DocumentDAO) getDAO()).queryWorkBySQLPage(params, page, lines, user);
    }

    public DataPackage<Document> queryByProcedure(String procedure, ParamsTable params, int page, int lines,
                                                  String domainid) throws Exception {
        return ((DocumentDAO) getDAO()).queryByProcedure(procedure, params, page, lines, domainid);
    }

    @SuppressWarnings("unchecked")
    public DataPackage<Document> queryBySQLWithCache(String sql, String domainid)
            throws Exception {
        DataPackage<Document> result = null;
        try {
            // signal.sessionSignal++;

            String cacheName = "OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean.queryBySQLWithCache(java.lang.String,java.lang.String)";

            ICacheProvider provider = MyCacheManager.getProviderInstance();
            if (provider != null) {
                if (provider.clearByCacheName(cacheName)) {
                    JavaScriptFactory.clear();
                    log.info("##CLEAN-CACHE-->>" + cacheName);
                }

                IMyCache cache = provider.getCache(cacheName);
                if (cache != null) {

                    Class<?>[] parameterTypes = new Class<?>[2];
                    parameterTypes[0] = String.class;
                    parameterTypes[1] = String.class;

                    Method method = DocumentProcessBean.class.getMethod("queryBySQLWithCache", parameterTypes);

                    Object[] methodParameters = new Object[2];
                    methodParameters[0] = sql;
                    methodParameters[1] = "";
                    CacheKey cacheKey = new CacheKey(this, method, methodParameters);

                    IMyElement cachedElement = cache.get(cacheKey);

                    if (cachedElement != null && cachedElement.getValue() != null) {
                        log.info("@@CACHED-METHOD-->>" + cacheKey);
                        result = (DataPackage<Document>) cachedElement.getValue();
                    } else {

                        result = ((DocumentDAO) getDAO()).queryBySQL(sql, domainid);
                        cache.put(cacheKey, result);

                        return result;
                    }
                }
            }

            return result;

        } catch (Exception t) {
            throw t;
        } finally {
            // signal.sessionSignal--;
            // if (signal.sessionSignal <= 0) {
            // PersistenceUtils.closeSession();
            // }
        }
    }

}
