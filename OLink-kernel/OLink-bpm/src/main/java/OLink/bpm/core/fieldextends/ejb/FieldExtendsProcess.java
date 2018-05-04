package OLink.bpm.core.fieldextends.ejb;

import java.util.List;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * 
 * 用户操作接口
 * 
 */
public interface FieldExtendsProcess extends IDesignTimeProcess<FieldExtendsVO> {
	/**
	 * 查询所有属于用户表的扩展字段
	 * @return
	 */
	List<FieldExtendsVO> queryUserFieldExtends(String domain) throws Exception;
	
	/**
	 * 获取所有扩展字段名
	 * @return
	 * @throws Exception
	 */
	List<String> queryFieldNames() throws Exception;
	
	/**
	 * 根据所属模块和字段名，查询出字段对象
	 * @param forTable 模块名
	 * @param name 字段名
	 * @return 返回TURE表示字段已被使用，FALSE表示未被使用。
	 * @throws Exception
	 */
	boolean queryFieldExtendsByForTableAndName(String domain, String forTable, String name) throws Exception;
	
	/**
	 * 根据ID查询FieldExtendsVO对象
	 * @param fid FieldExtendsVO对象的ID
	 * @return
	 * @throws Exception
	 */
	List<FieldExtendsVO> queryFieldExtendsByFid(String fid) throws Exception;
	
	/**
	 * 查询当前字段是否存在数据
	 * @param forTable 字段所在表
	 * @param fieldName 字段名
	 * @return 返回TURE表示字段已有数据，FALSE表示未有数据
	 * @throws Exception
	 */
	boolean checkFieldHasData(String domain, String forTable, String fieldName)throws Exception;
	
	/**
	 * 根据ID集合删除字段
	 * @param fids 字段ID集合
	 * @throws Exception
	 */
	void deleteFieldExtendsByIds(List<String> fids)throws Exception;
	
	/**
	 * 根据模块名查找字段集合
	 * @param forTable 字段所属模块
	 * @throws Exception
	 */
	List<FieldExtendsVO> queryFieldExtendsByTable(String domain, String forTable)throws Exception;
	
	/**
	 * 根据模块名和字段可见性查找字段集合
	 * @param forTable 字段所属模块
	 * @param enabel 可见性
	 * @return
	 * @throws Exception
	 */
	List<FieldExtendsVO> queryFieldExtendsByTableAndEnabel(String domain, String forTable, Boolean enabel)throws Exception;
	
	/**
	 * 清空field在相应表中的数据
	 * @param tableName 表名
	 * @param fieldName 字段名
	 * @throws Exception
	 */
	void cleanFieldData(String domain, String tableName, String fieldName)throws Exception;

	DataPackage<FieldExtendsVO> queryByTypeAndForTable(String domain, String type, String table,
													   int page, int lines) throws Exception;
	
	DataPackage<FieldExtendsVO> queryUserFieldExtends(String domain, int page, int lines)
		throws Exception;
	
	FieldExtendsVO qeuryFieldByLabelAndDomain(String label, String domain, String forTable) throws Exception;
	
}
