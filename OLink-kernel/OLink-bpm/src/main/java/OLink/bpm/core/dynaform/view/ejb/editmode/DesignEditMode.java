package OLink.bpm.core.dynaform.view.ejb.editmode;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.user.ejb.UserProcess;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.view.ejb.condition.FilterConditionParser;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;

/**
 * 
 * @author nicholas zhen
 * 
 */
public class DesignEditMode extends AbstractEditMode implements EditMode {
	public DesignEditMode(View view) {
		super(view);
	}

	public String getQueryString(ParamsTable params, WebUser user, Document sDoc) throws Exception {
		String relatedFormId = view.getRelatedForm();
		if(relatedFormId==null)
			return "";

		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Form relatedForm = (Form) formProcess.doView(relatedFormId);
		if(relatedForm==null){
			return "";
		}
		TableMapping tableMapping = relatedForm.getTableMapping();

		StringBuffer sqlTmp = new StringBuffer();
		sqlTmp.append("SELECT ").append(getSelectPart(tableMapping));
		sqlTmp.append(" FROM ").append(getInnerJoinPart(tableMapping));
		
		/**
		 * 2011-7-11
		 * 
		 * params参数集增加istmp的定义
		 * 
		 * @author keezzm
		 */
		String istmp = params.getParameterAsString("istmp");
		if (!StringUtil.isBlank(istmp)) {
			/*
			 * istmp有值
			 */
			sqlTmp.append(" WHERE d.ISTMP = " + istmp);
		} else {
			/*
			 * istmp无值
			 */
			sqlTmp.append(" WHERE 1 = 1");
		}
		
		StringBuffer conditionTmp = new StringBuffer();
		
		String authField = view.getAuth_fields();
		String scope = view.getAuthFieldScope();
		String auth_user = view.getAuth_user();
		if(View.AUTHFIELD_AUTHOR.equals(authField)){//作者
			sqlTmp.append(this.buildQueryString4Author(params, user, scope));
		}else if(View.AUTHFIELD_AUTHOR_DEFAULT_DEPT.equals(authField)){//作者默认部门
			sqlTmp.append(this.buildQueryString4AuthorDefaultDept(params, user, scope));
		}else if(View.AUTHFIELD_AUDITOR.equals(authField)){//流程当前处理人
			sqlTmp.append(this.buildQueryString4Auditor(params, user, scope, tableMapping));
		}
		
		// 作者条件
		if (!StringUtil.isBlank(auth_user)) {
			if(sqlTmp.indexOf("AND")>=0){
				sqlTmp.append(" OR ");
			}else{
				sqlTmp.append(" AND ");
			}
			sqlTmp.append(" d.AUTHOR ='").append(user.getId()).append("'");
		}
		String sql = sqlTmp.toString();

		String condition = view.getFilterCondition();

		// 判断有没有附加的字段Filter
		if (!StringUtil.isBlank(condition)) {
			FilterConditionParser parser = new FilterConditionParser(params, user, view);
			String sqlPart2 = parser.parseToSQL(condition);
			sql += sqlPart2;
		}
		return sql;
	}
	
	private String buildQueryString4Author(ParamsTable params, WebUser user,String scope) throws Exception{
		StringBuffer sql = new StringBuffer();
		if(View.AUTHFIELD_SCOPE_ITSELF.equals(scope)){//作者自身
			sql.append(" AND d.AUTHOR ='").append(user.getId()).append("'");
			
		}else if(View.AUTHFIELD_SCOPE_AUTHOR_SUPERIOR.equals(scope)){//上级用户
			UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			Collection<UserVO> lowerList = process.getUnderList(user.getId(), 1);
			StringBuffer ids = new StringBuffer();
			for(Iterator<UserVO> iter = lowerList.iterator();iter.hasNext();){
				UserVO u = iter.next();
				ids.append("'").append(u.getId()).append("',");
			}
			if(ids.length()>0) ids.setLength(ids.length()-1);
			if(ids.length()==0) ids.append("''");
			sql.append(" AND d.AUTHOR in(").append(ids).append(")");
			
		}else if(View.AUTHFIELD_SCOPE_AUTHOR_LOWER.equals(scope)){//下级用户
			String superior ="";
			if(user.getSuperior() !=null){
				superior = user.getSuperior().getId();
			}
			sql.append(" AND d.AUTHOR ='").append(superior).append("'");
		}
		
		return sql.toString();
	}
	
	private String buildQueryString4AuthorDefaultDept(ParamsTable params, WebUser user, String scope) throws Exception{
		
		if(StringUtil.isBlank(user.getDefaultDepartment())){
			throw new Exception("您没有设置默认部门，系统无法检索数据。");
		}
		
		StringBuffer sql = new StringBuffer();
		String defaultDeptIndex ="";//用户默认部门的索引编号
		DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		defaultDeptIndex = ((DepartmentVO)process.doView(user.getDefaultDepartment())).getIndexCode();
		
		if(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_DEFAULT.equals(scope)){//作者默认部门
			sql.append(" AND d.AUTHOR_DEPT_INDEX ='").append(defaultDeptIndex).append("'");
			
		}else if(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_ALL_SUPERIOR.equals(scope)){//所有上级部门
			sql.append(" AND d.AUTHOR_DEPT_INDEX like '").append(defaultDeptIndex).append("_%'");
			
		}else if(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_ALL_LOWER.equals(scope)){//所有下级用户
			String[] s = defaultDeptIndex.split("_");
			StringBuffer part = new StringBuffer();
			if (s.length > 1) {
				StringBuffer temp = new StringBuffer(s[0]);
				for (int i = 1; i < s.length-1; i++) {
					temp.append("_").append(s[i]);
					part.append("'").append(temp).append("',");
				}
				if(part.length()>0) part.setLength(part.length()-1);
				
			}
			if(part.length()==0) part.append("''");
			sql.append(" AND d.AUTHOR_DEPT_INDEX in (").append(part).append(")");
			
		}else if(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_LINE_SUPERIOR.equals(scope)){//直属上级部门
			sql.append(" AND d.AUTHOR_DEPT_INDEX like '").append(defaultDeptIndex).append("_____________________________________'");
			
		}else if(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_LINE_LOWER.equals(scope)){//直属下级部门
			sql.append(" AND d.AUTHOR_DEPT_INDEX ='").append(defaultDeptIndex.substring(0,defaultDeptIndex.lastIndexOf("_"))).append("'");
		}
		
		return sql.toString();
	}
	
	private String buildQueryString4Auditor(ParamsTable params, WebUser user,String scope,TableMapping tableMapping) throws Exception{
		StringBuffer sql = new StringBuffer();
		if(View.AUTHFIELD_SCOPE_ITSELF.equals(scope)){
			sql.append(" AND (d.ID IN(SELECT DOC_ID FROM ").append(tableMapping.getTableName(DQLASTUtil.TABLE_TYPE_AUTH))
				.append(" AUTH WHERE AUTH.VALUE = '").append(user.getId()).append("'))");
		}
		return sql.toString();
	}
	
	
	/**
	 * 获取SQL关联部分
	 * 
	 * @param tableMapping
	 *            表关系映射
	 * @param tableType
	 *            表类型
	 * @return
	 */
	private String getInnerJoinPart(TableMapping tableMapping) {
		int tableType = DQLASTUtil.TABEL_TYPE_CONTENT;

		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += tableMapping.getTableName(tableType) + " d";
		} else {
			sql = DQLASTUtil._TBNAME + " d";
			sql += " INNER JOIN " + tableMapping.getTableName(tableType) + " m";
			sql += " ON d.MAPPINGID=m." + tableMapping.getPrimaryKeyName();
		}

		return sql;
	}

	private String getSelectPart(TableMapping tableMapping) {
		String sql = "";
		if (tableMapping.getFormType() == Form.FORM_TYPE_NORMAL) {
			sql += "d.*";
		} else {
			sql += "d.*," + tableMapping.getColumnListString();
		}

		return sql;
	}

	/**
	 * 根据在视图中定义的部门级别返回当前用户可看的部门列表
	 * 
	 * @param dept
	 *            视图中定义的部门列表
	 * @param user
	 *            当前用户
	 * @return 用户可看的部门列表
	 * @throws Exception
	 * @deprecated 2.5vSP4改造视图权限配置后 此方法废弃
	 */
	@Deprecated
	private String getDepartmentList(String dept, WebUser user) throws Exception {

		StringBuffer deptList = new StringBuffer();
		if (dept.equalsIgnoreCase("superior")) {// 上级部门
			String lowerDepartmentList = user.getLowerDepartmentList(false);
			if(lowerDepartmentList !=null){
				deptList.append(user.getDeptlist()).append(",");
				deptList.append(lowerDepartmentList);
			}
			/*if (user.getSuperiorDepartmentList() != null) {
				deptList = user.getSuperiorDepartmentList();
			}*/
		} else if (dept.equalsIgnoreCase("lower")) {// 下级部门
			String superiorDepartmentList = user.getSuperiorDepartmentList();
			if(superiorDepartmentList!=null){
				deptList.append(user.getDeptlist()).append(",");
				deptList.append(superiorDepartmentList);
			}
			/*if (user.getLowerDepartmentList() != null) {
				deptList = user.getLowerDepartmentList(true);// 排除同级部门
			}*/
		} else if (dept.equalsIgnoreCase("self")) {// 同级部门
			if (user.getDepartments() != null) {
				Collection<DepartmentVO> depts = user.getDepartments();
				for (Iterator<DepartmentVO> iterator = depts.iterator(); iterator.hasNext();) {
					DepartmentVO vo = iterator.next();
					deptList.append("'").append(vo.getId()).append("'").append(",");
				}

				while (deptList.toString().endsWith(",")) {
					deptList.setLength(deptList.length() - 1);
				}
			}
		}
		if (StringUtil.isBlank(deptList.toString())) {
			return "''";
		}
		return deptList.toString();
	}
}
