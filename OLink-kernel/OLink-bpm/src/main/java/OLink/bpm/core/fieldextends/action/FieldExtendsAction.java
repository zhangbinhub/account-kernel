package OLink.bpm.core.fieldextends.action;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.fieldextends.ejb.FieldExtendsProcess;
import OLink.bpm.core.fieldextends.ejb.FieldExtendsVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;



public class FieldExtendsAction extends BaseAction<FieldExtendsVO> {

	private static final long serialVersionUID = 1L;
	private List<String> fieldNameAndIds;//字段名、ID、所属模块合成的串集合，格式为ID-NAME-FORTABLE
	//private List<String> fieldsNames;//扩展字段名字集合
	private FieldExtendsVO fieldExtends;
	public static final String FIELDEXTENDS = "FieldExtends";
	/**
	 * 构造方法，并把业务类实例化
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public FieldExtendsAction() throws Exception {
		super(ProcessFactory.createProcess(FieldExtendsProcess.class), new FieldExtendsVO());
		//fieldsNames = ((FieldExtendsProcess) process).queryFieldNames();
	}
	
	@Override
	public String doSave() {
		try {
			//所属字段非空校验
			String tempName = fieldExtends.getName();
			if(tempName == null && fieldExtends.getFid().equals("")){
				addFieldError("1", "{*[core.fieldextends.name.not.null]*}");
				return INPUT;
			}
			
			String domain = getParams().getParameterAsString("domain");
			if (StringUtil.isBlank(fieldExtends.getDomainid()))
				fieldExtends.setDomainid(domain);
			if(fieldExtends.getLabel() == null || "".equals(fieldExtends.getLabel())){
				addFieldError("resetError", "{*[field.extends.message.labelNullError]*}");
				return INPUT;
			}
			if (!regex(fieldExtends.getLabel())) {
				addFieldError("1", "{*[core.name.regex.illegal]*}");
				return INPUT;
			}
			//设置默认的排序为0
			if(fieldExtends.getSortNumber() == null) {
				fieldExtends.setSortNumber(0);
			}
			if (StringUtil.isBlank(fieldExtends.getFid())) {
				FieldExtendsVO vo = ((FieldExtendsProcess) process).qeuryFieldByLabelAndDomain(fieldExtends.getLabel(), domain, fieldExtends.getForTable());
				if (vo != null) {
					addFieldError("1", "{*[field.extends.message.label.failed]*}");
					return INPUT;
				}
				
				//检查该字段是否被使用
				boolean check = ((FieldExtendsProcess) process).queryFieldExtendsByForTableAndName(domain, fieldExtends.getForTable(),fieldExtends.getName());
				
				//如果被使用就返回信息告知用户，否则就添加字段
				if(check){
					addFieldError("repeatError", "{*[field.extends.message.repeatError]*}");
					return INPUT;
				}
				if (StringUtil.isBlank(fieldExtends.getType())) {
					fieldExtends.setType(FieldExtendsVO.TYPE_CLOB);
				}
				process.doCreate(fieldExtends);
			} else {
				//获取当前ID对应的字段对象
				List<FieldExtendsVO> fieldExtendses = ((FieldExtendsProcess) process).queryFieldExtendsByFid(fieldExtends.getFid());
				
				if(fieldExtendses != null && fieldExtendses.size() > 0){
					FieldExtendsVO oldField =  fieldExtendses.get(0);
					if (oldField.getType() == null) {
						oldField.setType(FieldExtendsVO.TYPE_STRING);
					}
					if (StringUtil.isBlank(fieldExtends.getType())) {
						fieldExtends.setType(oldField.getType());
					}
					//判断用户是否要更新字段的类型
					if(!oldField.getType().equals(fieldExtends.getType())){
						
						//检索要更新的字段，在相应的模块中是否已存在数据，如果有数据就不能更新字段的类型
						boolean check = ((FieldExtendsProcess) process).checkFieldHasData(domain, fieldExtends.getForTable(),oldField.getName());
						if(check){
							addFieldError("resetError", "{*[field.extends.message.hasDataError]*}");
							return INPUT;
						}
					}
					if (!oldField.getLabel().equals(fieldExtends.getLabel())) {
						FieldExtendsVO vo = ((FieldExtendsProcess) process).qeuryFieldByLabelAndDomain(fieldExtends.getLabel(), domain, fieldExtends.getForTable());
						if (vo != null) {
							addFieldError("1", "{*[field.extends.message.label.failed]*}");
							return INPUT;
						}
					}
					if(fieldExtends.getSortNumber() == null)
						oldField.setSortNumber(0);
					else
						oldField.setSortNumber(fieldExtends.getSortNumber());
					
					//oldField.setForTable(fieldExtends.getForTable());
					oldField.setLabel(fieldExtends.getLabel());
					oldField.setIsNull(fieldExtends.getIsNull());
					oldField.setEnabel(fieldExtends.getEnabel());
					oldField.setType(fieldExtends.getType());
					process.doUpdate(oldField);
					fieldExtends = oldField;
				} else {
					throw new Exception();
				}
			}
			//ServletActionContext.getRequest().setAttribute(FIELDEXTENDS, fieldExtends);
			addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("addError", "{*[Save]*}{*[field.extends.message.failed]*}");//field.extends.message.addError
			return INPUT;
		}
	}
	
	@Override
	public String doList() {
		try {
			ParamsTable params = getParams();
			String _currpage = params.getParameterAsString("_currpage");
			String _pagelines = params.getParameterAsString("_pagelines");
			String domain = params.getParameterAsString("domain");

			String type = params.getParameterAsString("sm_type");
			String forTable = params.getParameterAsString("sm_forTable");
			
			int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
			int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
			setDatas(((FieldExtendsProcess) process).queryByTypeAndForTable(domain, type, forTable, page, lines));
			//setDatas(((FieldExtendsProcess) process).queryUserFieldExtends(domain, page, lines));
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("showFieldExtendsList", e.getMessage());
			return INPUT;
		}
	}
	
	public String doSaveAndNew() {
		String result = doSave();
		if (result.equals(SUCCESS)) {
			FieldExtendsVO vo = new FieldExtendsVO();
			setContent(vo);
			fieldExtends = vo;
			
		}
		return result;
	}
	
	@Override
	public String doDelete() {
		if (fieldNameAndIds == null) {
			return SUCCESS;
		}
		try {
			//查询结果
			boolean checkResult = false;
			String domain = getParams().getParameterAsString("domain");
			//准备要删除的字段ID集合
			List<String> readyDeleteFieldIds = new ArrayList<String>();
			
			for (String fieldNameAndId : fieldNameAndIds) {
				//从字符串中初始化各参数
				String[] split = fieldNameAndId.split("-");
				String fid = split[0];
				String fieldName = split[1];
				String forTable = split[2];
				
				//检索要更新的字段，在相应的模块中是否已存在数据，如果有数据就清空该字段的数据
				checkResult = ((FieldExtendsProcess) process).checkFieldHasData(domain, forTable,fieldName);
				if(checkResult)
					((FieldExtendsProcess) process).cleanFieldData(domain, forTable, fieldName);
				
				readyDeleteFieldIds.add(fid);
			}
			
			((FieldExtendsProcess) process).deleteFieldExtendsByIds(readyDeleteFieldIds);
				
			addActionMessage("{*[field.extends.message.deleteSuccess]*}");
			return doList();
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("deleteError", "{*[field.extends.message.deleteError]*}");	
			return doList();
		}
	}
	
	public String doQuery() {
		try {
			ParamsTable table = getParams();
			String type = table.getParameterAsString("sm_type");
			String forTable = table.getParameterAsString("sm_forTable");
			String _currpage = table.getParameterAsString("_currpage");
			String _pagelines = table.getParameterAsString("_pagelines");
			int page = (_currpage != null && _currpage.length() > 0) ? Integer.parseInt(_currpage) : 1;
			int lines = (_pagelines != null && _pagelines.length() > 0) ? Integer.parseInt(_pagelines) : 10;
			if (type == null) {
				type = "";
			}
			if (forTable == null) {
				forTable = "";
			}
			String domain = table.getParameterAsString("domain");
			setDatas(((FieldExtendsProcess) process).queryByTypeAndForTable(domain, type, forTable, page, lines));
			return SUCCESS;
		} catch (Exception e) {
			
		}
		return doList();
	}
	
	@Override
	public String doView() {
		try {
			List<?> fieldExtendses = ((FieldExtendsProcess) process).queryFieldExtendsByFid(fieldExtends.getFid());
			if(fieldExtendses != null && fieldExtendses.size() > 0) {
				setContent((ValueObject) fieldExtendses.get(0));
				fieldExtends = (FieldExtendsVO) fieldExtendses.get(0);
			}
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	private boolean regex(String str) {
		if (str == null)
			return false;
		String regex = "^[a-zA-Z0-9\u4e00-\u9fa5][a-zA-Z0-9_\u4e00-\u9fa5-.]{0,48}[a-zA-Z0-9\u4e00-\u9fa5]{1}";
		return Pattern.matches(regex, str);
	}
	
	public List<String> getFieldNameAndIds() {
		return fieldNameAndIds;
	}

	public void setFieldNameAndIds(List<String> fieldNameAndIds) {
		this.fieldNameAndIds = fieldNameAndIds;
	}

	public List<String> getFieldsNames() {
		return FieldExtendsHelper.fieldNames;
	}

	public void setFieldsNames(List<String> fieldsNames) {
		//this.fieldsNames = fieldsNames;
	}

	public FieldExtendsVO getFieldExtends() {
		return fieldExtends;
	}

	public void setFieldExtends(FieldExtendsVO fieldExtends) {
		this.fieldExtends = fieldExtends;
	}
	
	public void setNameByUser(String nameByUser) {
		if (fieldExtends != null) {
			if (FieldExtendsVO.TABLE_USER.equals(fieldExtends.getForTable())) {
				fieldExtends.setName(nameByUser);
			}
		}
	}

	public void setNameByDep(String nameByDep) {
		if (fieldExtends != null) {
			if (FieldExtendsVO.TABLE_DEPT.equals(fieldExtends.getForTable())) {
				fieldExtends.setName(nameByDep);
			}
		}
	}
	
}
