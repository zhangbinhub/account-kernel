package OLink.bpm.core.fieldextends.ejb;

import java.util.ArrayList;
import java.util.List;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.fieldextends.dao.FieldExtendsDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.util.StringUtil;

public class FieldExtendsProcessBean extends AbstractDesignTimeProcessBean<FieldExtendsVO> implements
		FieldExtendsProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1229739098823541348L;

	protected FieldExtendsDAO getDAO() throws Exception {
		FieldExtendsDAO fieldExtendsDAO = (FieldExtendsDAO) DAOFactory.getDefaultDAO(FieldExtendsVO.class.getName());
		return fieldExtendsDAO;
	}
	
	/**
	 * 查询所有属于用户表的扩展字段
	 * @return
	 */
	public List<FieldExtendsVO> queryUserFieldExtends(String domain) throws Exception{
		return getDAO().queryUserFieldExtends(domain);
	}
	
	/**
	 * 获取所有扩展字段名
	 * @return
	 * @throws Exception
	 */
	public List<String> queryFieldNames() throws Exception{
		List<String> fieldNames = new ArrayList<String>();
		for(int i=1;i<=10;i++){
			fieldNames.add("field" + i);
		}
		return fieldNames;
	}
	
	/**
	 * 根据所属模块和字段名，查询出字段对象
	 * @param forTable 模块名
	 * @param name 字段名
	 * @return 返回TURE表示字段已被使用，FALSE表示未被使用。
	 * @throws Exception
	 */
	public boolean queryFieldExtendsByForTableAndName(String domain, String forTable,String name) throws Exception{
		//检测模块名是否属于部门或者用户
		if(FieldExtendsVO.TABLE_USER.equals(forTable) || FieldExtendsVO.TABLE_DEPT.equals(forTable)){
			List<FieldExtendsVO> fieldExtendses =  getDAO().queryFieldExtendsByForTableAndName(domain, forTable, name);
			return fieldExtendses != null && fieldExtendses.size() > 0;
		}
		return true;
	}
	
	/**
	 * 根据ID查询FieldExtendsVO对象
	 * @param fid FieldExtendsVO对象的ID
	 * @return
	 * @throws Exception
	 */
	public List<FieldExtendsVO> queryFieldExtendsByFid(String fid) throws Exception{
		return getDAO().queryFieldExtendsByFid(fid);
	}
	
	/**
	 * 查询当前字段是否存在数据
	 * @param forTable 字段所在表
	 * @param fieldName 字段名
	 * @return 返回TURE表示字段已有数据，FALSE表示未有数据
	 * @throws Exception
	 */
	//@SuppressWarnings("unchecked")
	public boolean checkFieldHasData(String domain, String forTable,String fieldName)throws Exception{
		if((FieldExtendsVO.TABLE_DEPT.equals(forTable) || FieldExtendsVO.TABLE_USER.equals(forTable)
				&& !StringUtil.isBlank(domain))){
			List<?> objects = getDAO().checkFieldHasData(domain, forTable, fieldName);
			return objects != null && objects.size() > 0;
		}
		
		return true;
	}
	
	/**
	 * 根据ID集合删除字段
	 * @param fids 字段ID集合
	 * @throws Exception
	 */
	public void deleteFieldExtendsByIds(List<String> fids)throws Exception{
		PersistenceUtils.beginTransaction();
		getDAO().deleteFieldExtendsByIds(fids);
		PersistenceUtils.commitTransaction();
	}
	
	/**
	 * 根据模块名查找字段集合
	 * @param forTable 字段所属模块
	 * @throws Exception
	 */
	public List<FieldExtendsVO> queryFieldExtendsByTable(String domain, String forTable)throws Exception{
		return getDAO().queryFieldExtendsByTable(domain, forTable);
	}
	
	/**
	 * 根据模块名和字段可见性查找字段集合
	 * @param forTable 字段所属模块
	 * @param enabel 可见性
	 * @return
	 * @throws Exception
	 */
	public List<FieldExtendsVO> queryFieldExtendsByTableAndEnabel(String domain, String forTable,Boolean enabel)throws Exception{
		return getDAO().queryFieldExtendsByTableAndEnabel(domain, forTable, enabel);
	}
	
	/**
	 * 清空field在相应表中的数据
	 * @param tableName 表名
	 * @param fieldName 字段名
	 * @throws Exception
	 */
	public void cleanFieldData(String domain, String tableName,String fieldName)throws Exception{
		//检测模块名是否属于部门或者用户
		if(FieldExtendsVO.TABLE_USER.equals(tableName)){
			tableName = "UserVO";
		}else if(FieldExtendsVO.TABLE_DEPT.equals(tableName)){
			tableName = "DepartmentVO";
		}
		
		//检测字段名是否合法再执行清空操作，防SQL止脚本注入
		List<String> fieldNames = this.queryFieldNames();
		for (String oldFieldName : fieldNames) {
			if(oldFieldName.equals(fieldName)){
				PersistenceUtils.beginTransaction();
				getDAO().cleanFieldData(domain, tableName, fieldName);
				PersistenceUtils.commitTransaction();
			}
		}
	}

	public DataPackage<FieldExtendsVO> queryByTypeAndForTable(String domain, String type, String table,
															  int page, int lines) throws Exception {
		return getDAO().queryByTypeAndForTable(domain, type, table, page, lines);
	}

	public DataPackage<FieldExtendsVO> queryUserFieldExtends(String domain, int page, int lines)
			throws Exception {
		return getDAO().queryUserFieldExtends(domain, page, lines);
	}

	public FieldExtendsVO qeuryFieldByLabelAndDomain(String label, String domain, String forTable) throws Exception {
		return getDAO().qeuryFieldByLabelAndDomain(label, domain, forTable);
	}
}
