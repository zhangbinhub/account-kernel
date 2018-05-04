package OLink.bpm.core.upload.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;

public interface UploadProcess extends IRunTimeProcess<UploadVO> {
	/**
	 * 通过传人字段名和字段的值查询
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @throws Exception
	 */
	Collection<UploadVO> findByColumnName(String columnName, String columnValue) throws Exception;
	/**
	 * 通过指定的列和列值查找上传文件
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @throws Exception
	 */
	UploadVO findByColumnName1(String columnName, String columnValue) throws Exception;
	/**
	 * 通过id获得uploadvo对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	ValueObject doFindById(String id) throws Exception;
	/**
	 * 创建
	 * @param vo
	 * @throws Exception
	 */
	void doCreate(UploadVO vo) throws Exception;
	/**
	 * 更新
	 * @param vo
	 * @throws Exception
	 */
	void doUpdate(UploadVO vo) throws Exception;
	/**
	 * 删除
	 */
	void doRemove(String pk) throws Exception;
	/**
	 * 查找出mapping获得uploadvo对象
	 * @param mappingColumnName//系统外表映射的列
	 * @param fieldid//字段id
	 * @param tableName//映射表名
	 * @param mappingPrimaryKeyName//映射表主键
	 * @param mappinid//映射的id
	 * @return
	 * @throws Exception
	 */
	ValueObject doFindByMappingToUploadVO(String mappingColumnName, String fieldid, String tableName, String mappingPrimaryKeyName, String mappinid) throws Exception;

}
