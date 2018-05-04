package OLink.bpm.webservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.util.StringUtil;
import OLink.bpm.webservice.model.SimpleDocument;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRTProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.webservice.fault.DocumentServiceFault;

public class DocumentService {
	/**
	 * 获取应用使用的宾客
	 * 
	 * @param applicationId
	 *            应用标识
	 * @return 用户
	 * @throws Exception
	 */
	private WebUser getGuest(String applicationId) throws Exception {
		UserVO uservo = new UserVO();
		uservo.setId("guest");
		uservo.setName("guest");
		uservo.setLoginno("guest");
		uservo.setDomainid("guest");
		uservo.setApplicationid(applicationId);
		WebUser user = new WebUser(uservo);
		user.setDefaultApplication(applicationId);
		return user;
	}

	/**
	 * 用宾客创建文档
	 * 
	 * @param formName
	 *            表单名称
	 * @param parameters
	 *            表单参数
	 * @param applicationId
	 *            表单所在应用标识
	 * @throws DocumentServiceFault
	 */
	public void createDocumentByGuest(String formName, Map<String, Object> parameters, String applicationId)
			throws DocumentServiceFault {
		try {
			WebUser user = getGuest(applicationId);
			createDocument(formName, parameters, user, applicationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 用宾客更新文档
	 * 
	 * @param documentId
	 *            文档标识
	 * @param parameters
	 *            表单参数
	 * @param applicationId
	 *            表单所在应用标识
	 * @throws DocumentServiceFault
	 */
	public void updateDocumentByGuest(String documentId, Map<String, Object> parameters, String applicationId)
			throws DocumentServiceFault {
		try {
			WebUser user = getGuest(applicationId);
			updateDocument(documentId, parameters, user, applicationId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 用域用户创建文档
	 * 
	 * @param formName
	 *            表单名称
	 * @param parameters
	 *            表单参数
	 * @param domainUserId
	 *            域用户标识
	 * @param applicationId
	 *            表单所在应用标识
	 * 
	 * @return 文档主键
	 * 
	 * @throws DocumentServiceFault
	 * 
	 */
	public String createDocumentByDomainUser(String formName, Map<String, Object> parameters, String domainUserId,
			String applicationId) throws DocumentServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			UserVO user = (UserVO) userProcess.doView(domainUserId);
			if (user != null) {
				return createDocument(formName, parameters, new WebUser(user), applicationId);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 用域用户更新文档
	 * 
	 * @param documentId
	 *            文档标识
	 * @param parameters
	 *            表单参数
	 * @param domainUserId
	 *            域用户标识
	 * @param applicationId
	 *            表单所在应用标识
	 * @throws DocumentServiceFault
	 */
	public void updateDocumentByDomainUser(String documentId, Map<String, Object> parameters, String domainUserId,
			String applicationId) throws DocumentServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			UserVO user = (UserVO) userProcess.doView(domainUserId);
			if (user != null) {
				updateDocument(documentId, parameters, new WebUser(user), applicationId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 创建文档，如果有流程则创建流程
	 * 
	 * @param formName
	 * @param parameters
	 * @param user
	 * @param applicationId
	 * @return
	 * @throws DocumentServiceFault
	 */
	private String createDocument(String formName, Map<String, Object> parameters, WebUser user, String applicationId)
			throws DocumentServiceFault {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			
			DocumentProcess docProcess = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,
					applicationId);

			Form form = formProcess.doViewByFormName(formName, applicationId);

			ParamsTable params = new ParamsTable();
			params.putAll(parameters);
			Document doc = form.createDocument(params, user);
			
			// 设值流程实例，但不一定都有流程
			doc.setState(creareFlowInstance(doc, user, params, applicationId));
			
			if (doc != null) {
				doc.setIstmp(false);
				docProcess.doStartFlowOrUpdate(doc, params, user);
				return doc.getId();
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}
	
	/**
	 * 根据流程名称创建流程实例
	 * @return
	 * @throws Exception 
	 */
	private FlowStateRT creareFlowInstance(Document doc, WebUser user, ParamsTable params, String applicationId) throws Exception{
		BillDefiProcess flowProcess = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		FlowStateRTProcess stateProcess = (FlowStateRTProcess) ProcessFactory.createRuntimeProcess(
				FlowStateRTProcess.class, applicationId);
		// 创建流程实例
		String flowName = params.getParameterAsString("flowname"); // 需启动的流程名称
		if (!StringUtil.isBlank(flowName)){
			BillDefiVO flowVO = flowProcess.doViewBySubject(flowName, applicationId);
			if (flowVO != null){
				return stateProcess.createTransientFlowStateRT(doc, flowVO.getId(), user);
			}
		}
		return null;
	}

	private void updateDocument(String documentId, Map<String, Object> parameters, WebUser user, String applicationId)
			throws DocumentServiceFault {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
			DocumentProcess docProcess = new DocumentProcessBean(applicationId);

			Document doc = (Document) docProcess.doView(documentId);
			Form form = (Form) formProcess.doView(doc.getFormid());

			ParamsTable params = new ParamsTable();
			params.putAll(parameters);

			doc = form.createDocument(doc, params, user);
			docProcess.doUpdate(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 删除文档
	 * 
	 * @param documentId
	 *            文档标识
	 * @param applicationId
	 *            应用标识
	 * @throws DocumentServiceFault
	 */
	public void removeDocument(String documentId, String applicationId) throws DocumentServiceFault {
		try {
			// WebUser user = getGuest(applicationId);
			DocumentProcess docProcess = new DocumentProcessBean(applicationId);
			docProcess.doRemove(documentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 通过参数查询出数据集
	 * 
	 * @param formName
	 *            表彰名
	 * @param parameters
	 *            参数
	 * @param applicationId
	 *            应用标识
	 * @return 数据集
	 * @throws DocumentServiceFault
	 */
	public Collection<SimpleDocument> searchDocumentsByFilter(String formName, Map<String, Object> parameters,
															  String applicationId) throws DocumentServiceFault {
		try {
			return searchDocumentsByFilter(formName, parameters, applicationId, getGuest(applicationId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		}
	}

	private Collection<SimpleDocument> searchDocumentsByFilter(String formName, Map<String, Object> parameters,
			String applicationId, WebUser webUser) throws DocumentServiceFault {
		try {
			DocumentProcess docProcess = new DocumentProcessBean(applicationId);
			ParamsTable params = new ParamsTable();
			params.putAll(parameters);

			String dql = "$formname='" + formName + "'";

			String _currpage = params.getParameterAsString("_currpage");
			String _pagelines = params.getParameterAsString("_pagelines");

			int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
			int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines)
					: Integer.MAX_VALUE;

			DataPackage<Document> dataPackage = docProcess.queryByDQLPage(dql, page, lines, webUser.getDomainid());
			return convertToSimpleDatas(dataPackage);

			// return (Collection<SimpleDocument>)dataPackage.getDatas();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 根据过滤的参数查询数据集
	 * 
	 * @param formName
	 *            表单名
	 * @param parameters
	 *            参数名
	 * @param applicationId
	 *            应用标识
	 * @param domainUserId
	 *            域用户
	 * @return
	 * @throws DocumentServiceFault
	 */
	public Collection<SimpleDocument> searchDocumentsByFilter(String formName, Map<String, Object> parameters,
			String applicationId, String domainUserId) throws DocumentServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			UserVO user = (UserVO) userProcess.doView(domainUserId);
			if (user != null) {
				return searchDocumentsByFilter(formName, parameters, applicationId, new WebUser(user));
			}
			return new ArrayList<SimpleDocument>();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 
	 * @param formName
	 *            表单名
	 * @param parameters
	 *            参数
	 * @param applicationId
	 *            应用标识
	 * @param domainUserId
	 *            域用户标识
	 * @return 简单的文档对象
	 * @throws DocumentServiceFault
	 */
	public SimpleDocument searchDocumentByFilter(String formName, Map<String, Object> parameters, String applicationId,
			String domainUserId) throws DocumentServiceFault {
		try {
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			UserVO user = (UserVO) userProcess.doView(domainUserId);
			if (user != null) {
				Collection<?> docList = searchDocumentsByFilter(formName, parameters, applicationId, new WebUser(user));
				if (docList != null && docList.isEmpty()) {
					return (SimpleDocument) docList.iterator().next();
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DocumentServiceFault(e.getMessage());
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				throw new DocumentServiceFault(e.getMessage());
			}
		}
	}

	/**
	 * 转换DataPackage中的Document为SimpleDocument
	 * 
	 * @param dataPackage
	 *            数据集
	 */
	private Collection<SimpleDocument> convertToSimpleDatas(DataPackage<Document> dataPackage) {
		Collection<SimpleDocument> datas = new ArrayList<SimpleDocument>();

		for (Iterator<Document> iterator = dataPackage.getDatas().iterator(); iterator.hasNext();) {
			Document document = iterator.next();
			SimpleDocument sDocument = new SimpleDocument();
			sDocument.setId(document.getId());
			sDocument.setStateLabel(document.getStateLabel());
			for (Iterator<?> iterator2 = document.getItems().iterator(); iterator2.hasNext();) {
				Item item = (Item) iterator2.next();

				sDocument.getItems().put(item.getName(), item.getValue());
			}
			datas.add(sDocument);
		}

		return datas;

		// simpledatapackage.setDatas(datas);
	}
}
