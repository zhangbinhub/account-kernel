package OLink.bpm.init;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.privilege.operation.ejb.OperationProcess;
import OLink.bpm.core.privilege.operation.ejb.OperationVO;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.ProcessFactory;

/**
 * 
 * @author Happy
 * 
 */
public class InitOperationInfo implements IInitialization {

	private static final Map<String, String> viewOperationTypeMap = ActivityType.getViewActivityTypeMap();
	private static final Map<String, String> formOperationTypeMap = ActivityType.getFormActivityTypeMap();
	private static final Map<String, String> menuOperationTypeMap;
	private static final Map<String, String> formFieldOperationTypeMap;
	private static final Map<String, String> fileOperationTypeMap;

	static {

		menuOperationTypeMap = new LinkedHashMap<String, String>();
		menuOperationTypeMap.put(OperationVO.MENU_VISIABLE + "", "Isview");// 可视
		menuOperationTypeMap.put(OperationVO.MENU_INVISIBLE + "", "Invisible");// 不可视

		formFieldOperationTypeMap = new LinkedHashMap<String, String>();
		formFieldOperationTypeMap.put(OperationVO.FORMFIELD_MODIFY + "", "Modify");// 修改
		formFieldOperationTypeMap.put(OperationVO.FORMFIELD_DISABLED + "", "Disabled");// 禁用
		formFieldOperationTypeMap.put(OperationVO.FORMFIELD_HIDDEN + "", "Hidden");// 隐藏
		formFieldOperationTypeMap.put(OperationVO.FORMFIELD_READONLY + "", "Readonly");// 只读

		fileOperationTypeMap = new LinkedHashMap<String, String>();
		fileOperationTypeMap.put(OperationVO.FOLDER_CREATE + "", "FolderCreate");// 文件夹创建
		fileOperationTypeMap.put(OperationVO.FOLDER_RENAME + "", "FolderRename");// 重命名
		fileOperationTypeMap.put(OperationVO.FOLDER_DELETE + "", "FolderDelete");// 删除
		fileOperationTypeMap.put(OperationVO.FILE_REVIEW + "", "FileReview");// 文件查看
		fileOperationTypeMap.put(OperationVO.FILE_EDIT + "", "FileEdit");// 文件编辑
		fileOperationTypeMap.put(OperationVO.FILE_DELETE + "", "FileDelete");// 文件删除
		fileOperationTypeMap.put(OperationVO.FILE_DOWN + "", "FileDownload");// 文件下载
		fileOperationTypeMap.put(OperationVO.FILE_REMOVE + "", "FileMove");// 文件移动
		fileOperationTypeMap.put(OperationVO.FILE_COPY + "", "FileCopyAll");// 文件复制
		fileOperationTypeMap.put(OperationVO.FILE_UPLOAD + "", "UploadFile");// 文件上传
		fileOperationTypeMap.put(OperationVO.FILE_ADD_SELETE_FILE + "", "AddChooseFile");// 添加选择文件

	}
	
	public void run() throws InitializationException {
		try {
			OperationProcess process = (OperationProcess) ProcessFactory.createProcess(OperationProcess.class);
			ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
			Collection<ApplicationVO> apps = appProcess.doSimpleQuery(null);
			for (Iterator<ApplicationVO> iterator = apps.iterator(); iterator.hasNext();) {
				ApplicationVO app = iterator.next();

				if (process.isEmpty(app.getId())) {
					persistenceAll(process, app.getId());// 持久化
				} else {
					Collection<OperationVO> operations = process.doSimpleQuery(new ParamsTable(), app.getId());

					validate(process, viewOperationTypeMap, ResVO.VIEW_TYPE, operations, app.getId());
					validate(process, formOperationTypeMap, ResVO.FORM_TYPE, operations, app.getId());
					validate(process, menuOperationTypeMap, ResVO.MENU_TYPE, operations, app.getId());
					validate(process, formFieldOperationTypeMap, ResVO.FORM_FIELD_TYPE, operations, app.getId());
					validate(process, fileOperationTypeMap, ResVO.FOLDER_TYPE, operations, app.getId());
				}
			}

		} catch (Exception e) {
			throw new InitializationException(e.getMessage());
		}

	}

	/**
	 * 校验操作对象是否已经持久化 否则持久化此对象
	 * 
	 * @param process
	 * @param map
	 * @param type
	 * @param operations
	 * @throws Exception
	 */
	public void validate(OperationProcess process, Map<String, String> map, int type, Collection<OperationVO> operations, String applicationid)
			throws Exception {
		try {
			for (Iterator<Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();) {
				Entry<String, String> entry = iter.next();
				boolean isEqual = false;// 是否存在
				for (Iterator<OperationVO> it = operations.iterator(); it.hasNext();) {
					OperationVO operation = it.next();
					Integer operCode = operation.getCode();
					if (operCode != null && Integer.parseInt(entry.getKey()) == operCode.intValue()) {
						isEqual = true;
						break;
					}
				}
				if (!isEqual) {
					persistenceOne(process, entry, type, applicationid);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 持久化单个操作对象
	 * 
	 * @param process
	 * @param entry
	 * @param type
	 * @throws Exception
	 */
	public synchronized void persistenceOne(OperationProcess process, Entry<String, String> entry, int type, String applicationid)
			throws Exception {
		try {
			int code = Integer.parseInt(entry.getKey());
			OperationVO operation = new OperationVO();
			operation.setId(getUUID(code, type, applicationid));
			operation.setName(entry.getValue());
			operation.setCode(code);
			operation.setResType(Integer.valueOf(type));
			operation.setApplicationid(applicationid);

			process.doCreate(operation);
		} catch (Exception e) {
			System.out.println("PersistenceOne Error: " + entry);
			e.printStackTrace();
		}
	}

	/**
	 * 持久化所有操作
	 * 
	 * @param process
	 * @throws InitializationException
	 */
	public void persistenceAll(OperationProcess process, String applicationid) throws InitializationException {
		try {
			for (Iterator<Entry<String,String>> iterator = viewOperationTypeMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,String> entry = iterator.next();
				persistenceOne(process, entry, ResVO.VIEW_TYPE, applicationid);

			}

			for (Iterator<Entry<String,String>> iterator = formOperationTypeMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,String> entry = iterator.next();
				persistenceOne(process, entry, ResVO.FORM_TYPE, applicationid);
			}

			for (Iterator<Entry<String,String>> iterator = menuOperationTypeMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,String> entry = iterator.next();
				persistenceOne(process, entry, ResVO.MENU_TYPE, applicationid);
			}

			for (Iterator<Entry<String,String>> iterator = formFieldOperationTypeMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,String> entry = iterator.next();
				persistenceOne(process, entry, ResVO.FORM_FIELD_TYPE, applicationid);
			}

			for (Iterator<Entry<String,String>> iterator = fileOperationTypeMap.entrySet().iterator(); iterator.hasNext();) {
				Entry<String,String> entry = iterator.next();
				persistenceOne(process, entry, ResVO.FOLDER_TYPE, applicationid);
			}
		} catch (Exception e) {
			throw new InitializationException(e.getMessage());
		}

	}

	private String getUUID(int operCode, int resType, String applicationId) {
		DecimalFormat format = new DecimalFormat("0000");
		String code = format.format((operCode));
		// 使用固定ID + 操作code + 操作类型 = 操作唯一ID
		String ID = applicationId + code + "" + resType;

		return ID;
	}

	public static void main(String[] args) throws Exception {
		InitOperationInfo initOperation = new InitOperationInfo();
		initOperation.run();
	}
}
