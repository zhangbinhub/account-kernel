package OLink.bpm.core.upload.dao;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.base.dao.IRuntimeDAO;

public interface UploadDAO extends IRuntimeDAO {

	/**
	 * 通过指定的列和列值查找上传文件
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
	 * 查找出mapping获得uploadvo对象
	 * @param mappingColumnName//系统外表映射的列
	 * @param fieldid//字段id
	 * @param tableName//映射表名
	 * @param mappingPrimaryKeyName//映射表主键
	 * @param mappinid//映射的id
	 * @return
	 * @throws Exception
	 */
	ValueObject findByMappingToUploadVO(String mappingColumnName, String fieldid, String tableName, String mappingPrimaryKeyName, String mappinid) throws Exception;
}
