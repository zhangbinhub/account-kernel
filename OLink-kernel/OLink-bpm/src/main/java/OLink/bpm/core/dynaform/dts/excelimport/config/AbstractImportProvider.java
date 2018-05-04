package OLink.bpm.core.dynaform.dts.excelimport.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.dts.excelimport.DetailSheet;
import OLink.bpm.core.dynaform.dts.excelimport.ExcelMappingDiagram;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.dts.excelimport.Column;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.dts.excelimport.MasterSheet;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;

import eWAP.core.Tools;

import OLink.bpm.core.dynaform.dts.excelimport.AbstractSheet;
import OLink.bpm.core.dynaform.dts.excelimport.LinkageKey;
import OLink.bpm.core.user.action.WebUser;

/**
 * 抽象的Excel导入服务提供者
 * @author Happy.Lau
 */
public abstract class AbstractImportProvider implements ImportProvider{
	final static String IMPORT_FIELD_ID_PREFIX = Long.toHexString(new Date().getTime());
	public final static Logger log = Logger.getLogger(ImpExcelToDoc.class);
	
	
	private Workbook workBook;

	private ExcelMappingDiagram mappingConfig;
	

	public AbstractImportProvider(Workbook workBook,
			ExcelMappingDiagram mappingConfig) {
		super();
		this.workBook = workBook;
		this.mappingConfig = mappingConfig;
	}
	
	/**
	 * 对表单的字段进行校验
	 * 
	 * @param form
	 * @param doc
	 * @param subParams
	 * @param user
	 * @throws Exception
	 */
	public void validate(Form form, Document doc, ParamsTable subParams, WebUser user) throws Exception {
		// 对文档的字段进行校验
		Collection<ValidateMessage> errs = form.validate(doc, subParams, user, false);
		if (errs != null && errs.size() > 0) {
			StringBuffer fieldErrors = new StringBuffer();

			Iterator<ValidateMessage> iter4 = errs.iterator();
			while (iter4.hasNext()) {
				// ValidateMessage err = (ValidateMessage) iter4.next();
				ValidateMessage err = iter4.next();
				// 字段名称及出错信息
				fieldErrors.append(err.getFieldname() + "(" + err.getErrmessage() + ");");
			}

			fieldErrors.deleteCharAt(fieldErrors.lastIndexOf(";"));

			throw new Exception(fieldErrors.toString());
		}
	}

	/**
	 * 获取主键的值映射
	 * 
	 * @param sheet
	 *            配置定义
	 * @param doc
	 *            文档
	 * @return
	 * @throws Exception
	 */
	 Map<String, String> getPrimaryKeyValueMap(AbstractSheet sheet, Document conditionDoc) throws Exception {
		Map<String, String> rtn = new HashMap<String, String>();
		Collection<? extends Column> cols = sheet.getColumns();
		for (Iterator<?> iterator = cols.iterator(); iterator.hasNext();) {
			Column clm = (Column) iterator.next();
			if (clm.primaryKey) {
				rtn.put(clm.fieldName, conditionDoc.getItemValueAsString(clm.fieldName));
			}

		}
		return rtn;
	}

	Form rebuildFormFieldProperty(AbstractSheet sheet, Form form) {
		Iterator<? extends Column> iter = sheet.getColumns().iterator();
		while (iter.hasNext()) {
			Column clm = iter.next();
			FormField field = form.findFieldByName(clm.fieldName);
			if (field == null) {
				continue;
			}
			String importFieldId = IMPORT_FIELD_ID_PREFIX + "-" + field.getId();
			field.setId(importFieldId);

			// 替换 值脚本
			if (clm.valueScript != null && clm.valueScript.trim().length() > 0) {
				field.setValueScript(clm.valueScript);
			}

			// 替换 校验脚本
			if (clm.validateRule != null && clm.validateRule.trim().length() > 0) {
				field.setValidateRule(clm.validateRule);
			}

		}
		return form;
	}
	
	Map<String, Object> transExcelValueList2Params(AbstractSheet sheet, Map<?, ?> valueList) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Iterator<? extends Column> iter = sheet.getColumns().iterator();
		while (iter.hasNext()) {
			Column clm = iter.next();
			if (clm.name != null && clm.fieldName != null) {
				if (sheet instanceof MasterSheet) {
					map.put(clm.fieldName, valueList.get(clm.name));
				} else if (sheet instanceof DetailSheet) {
					map.put(clm.fieldName, valueList.get(clm.name));
				}
			}
		}

		return map;
	}
	
	@SuppressWarnings("deprecation")
	public String creatDocument(WebUser user, ParamsTable params, String applicationid) throws Exception {
		// //PersistenceUtils.getSessionSignal().sessionSignal++;
		FormProcess proxy = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		DocumentProcess docproxy = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,applicationid);

		Collection<String> errors = new ArrayList<String>();// 输出到前台的 异常 集合

		int rowCount = getMasterSheetRowCount();

		int row = 0;

		int detailErrorCount = 1;

		int i = 0;
		// ArrayList parentDocList = new ArrayList();
		// ArrayList subDocList = new ArrayList();

		Form form = proxy.doViewByFormName(getMappingConfig().getMasterSheet().formName, applicationid);

		form = rebuildFormFieldProperty(getMappingConfig().getMasterSheet(), (Form) form.clone());
		// boolean flag = false; // 标志MasterSheet出错还是detialSheet出错

		String rowError = ""; // 主表单出错信息

		String parentid = (String) params.getParameter("parentid");// 当导入为子表单数据时会使用
		try {
			//changed by lr 2013-11-28
			//there is an error : if transaction is not commit then iscript will be deadlock ,because they access the same table
			//so set transaction into  loop body
			for (i = 1; i < rowCount; i++) {
				docproxy.beginTransaction();
				rowError = getMappingConfig().getMasterSheet().name + " {*[Row]*}[" + (i + 1) + "]: ";

				long start = System.currentTimeMillis();

				// flag = false;
				ParamsTable parentParams = new ParamsTable(params);

				Map<String, String> values = getMasterSheetRow(i);

				log.debug("Import SETP-1 times->" + (System.currentTimeMillis() - start) + "(ms)");

				if (values == null) {
					continue;
				} else {
					row++;
				}
				Map<String, Object> fieldValues = transExcelValueList2Params(getMappingConfig().getMasterSheet(), values);

				parentParams.putAll(fieldValues);

				log.debug("Import SETP-2 times->" + (System.currentTimeMillis() - start) + "(ms)");

				// 条件文档
				Document conditionDoc = form.createDocument(parentParams, user);
				Map<String, String> condition = getPrimaryKeyValueMap(getMappingConfig().getMasterSheet(), conditionDoc);

				boolean isCreate = true;

				Document parentDoc = null;
				if (!condition.isEmpty()) {
					parentDoc = docproxy.doViewByCondition(form.getName(), condition, user);
					if (parentDoc != null) {
						parentDoc = form.createDocument(parentDoc, parentParams, user);
						if (parentid != null && !parentid.equals("")) {
							parentDoc.setParent(parentid);
						}
						isCreate = false;
					}
				}

				if (isCreate) { // 是否创建新文档
					parentDoc = form.createDocument(parentParams, user);
					if (form.getOnActionFlow() != null) {
						parentDoc.setFlowid(form.getOnActionFlow());
						if (parentid != null && !parentid.equals("")) {
							parentDoc.setParent(parentid);
						}
					}
//					docproxy.doCreate(parentDoc,user);重复保存 
				}

				log.debug("Import SETP-3 times->" + (System.currentTimeMillis() - start) + "(ms)");
				log.info("ID--------->" + parentDoc.getId());
				log.debug("Import SETP-4 times->" + (System.currentTimeMillis() - start) + "(ms)");

				log.debug("Import SETP-5 times->" + (System.currentTimeMillis() - start) + "(ms)");

				Iterator<LinkageKey> iter = getMappingConfig().getLinkageKeys().iterator();

				log.debug("Import SETP-6 times->" + (System.currentTimeMillis() - start) + "(ms)");

				Collection<Document> childs = new HashSet<Document>();
				while (iter.hasNext()) {
					// LinkageKey key = (LinkageKey) iter.next();
					LinkageKey key = iter.next();

					String sheetName = key.getDetailSheet().name;
					String columnName = key.getDetailSheetKeyColumn().name;
					String matchValue = values.get(key.getMasterSheetKeyColumn().name);

					Collection<LinkedHashMap<String, String>> detailRows = getDetailSheetRowCollection(sheetName,
							columnName, matchValue);
					int detailRow = 0;

					String formName = key.getDetailSheet().formName;
					
					Form subform = proxy.doViewByFormName(formName, applicationid);
					subform = rebuildFormFieldProperty(key.getDetailSheet(), (Form) subform.clone());
					
					Collection<Document> subDocs = parentDoc.getChilds(formName);
					// 更新则把原有子表单删除
					if (subDocs != null && !subDocs.isEmpty()) {
						for (Iterator<Document> iterator = subDocs.iterator(); iterator.hasNext();) {
							// Document subDoc = (Document) iterator.next();
							Document subDoc = iterator.next();
							docproxy.doRemove(subDoc.getId());
						}
					}

					String subRowError = ""; // 子表单出错信息
					try {
						Iterator<LinkedHashMap<String, String>> iter2 = detailRows.iterator();
						if (iter2.hasNext()) {
							do {
								subRowError = sheetName + " {*[Row]*}[" + (detailErrorCount + 1) + "]: ";
								Map<String, Object> tmp = transExcelValueList2Params(key.getDetailSheet(),
										iter2.next());
								ParamsTable subParams = new ParamsTable(params);
								subParams.setParameter("parentid", parentDoc.getId());
								subParams.putAll(tmp);

								// 此内容参考Form的createDocument
								Document subdoc = new Document();
								subdoc.setId(Tools.getSequence());
								subdoc.setAuthor(user);
								subdoc.setCreated(new Date());
								subdoc.setIstmp(false);
								subdoc.setLastmodifier(user.getId());
								subdoc.setLastmodified(new Date());
								subdoc.setParent(parentDoc);

								if (subform.getType() == Form.FORM_TYPE_NORMAL) {
									subdoc.setMappingId(subdoc.getId());
								} else if (subform.getType() == Form.FORM_TYPE_NORMAL_MAPPING) {
									subdoc.setMappingId(Tools.getUUID());
								}

								subdoc = subform.createDocument(subdoc, subParams, user);

								try {
									validate(subform, subdoc, subParams, user); // 对子文档的正确性进行校验

									childs.add(subdoc);
									docproxy.doCreate(subdoc);
								} catch (Exception e) {
									errors.add(subRowError + e.getMessage());
									throw new Exception(subRowError + e.getMessage());
								}

								detailErrorCount++;
								detailRow++;
							} while (iter2.hasNext());
						} else if (matchValue == null && detailRow == 0) {
							throw new Exception(subRowError + " [" + columnName + " 不能为空]");
						}
					} catch (Throwable t) {
						throw new Exception(subRowError + t.getMessage());
					}
				}

				log.debug("Import SETP-7 times->" + (System.currentTimeMillis() - start) + "(ms)");

				try {
					// 对主文档的正确性进行校验
					validate(form, parentDoc, parentParams, user);
					// 获取主键映射值
					//docproxy.doStartFlowOrUpdate(parentDoc, parentParams, user);//此方法会查询文档的流程实例等信息 增加复杂度 导致性能下降
					docproxy.doCreate(parentDoc, user);
					log.info("Import " + getMappingConfig().getMasterSheet().name + " Row " + i + " SUCCESS");
					log.debug("Import SETP-0 times->" + (System.currentTimeMillis() - start) + "(ms)");
				} catch (Exception e) {
					errors.add(rowError + e.getMessage());
				}
				docproxy.commitTransaction();
			}
			if (!errors.isEmpty()) {
				throw new ImpExcelException(errors);
			} 
			
			
			
		} catch (Exception e) {
			docproxy.rollbackTransaction();
			e.printStackTrace();
			throw e;
		} finally {
			// PersistenceUtils.closeSession();
			docproxy.closeConnection();
			PersistenceUtils.currentSession().clear();
		}

		String msg = "";
		if (row == 0 && i == rowCount) {
			msg = "{*[Error]*}[Excel上传文件中主表单无数据]";
			throw new Exception(msg);
		} else if (row != 0) {
			msg = "{*[Success.total.imported]*} (" + row + ") {*[rows]*}";
		}

		return msg;

	}



	public Workbook getWorkBook() {
		return workBook;
	}


	public void setWorkBook(Workbook workBook) {
		this.workBook = workBook;
	}


	public ExcelMappingDiagram getMappingConfig() {
		return mappingConfig;
	}


	public void setMappingConfig(ExcelMappingDiagram mappingConfig) {
		this.mappingConfig = mappingConfig;
	}

	
	
	
}
