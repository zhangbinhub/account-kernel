package OLink.bpm.core.upload.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.upload.dao.UploadDAO;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;

public class UploadProcessBean  extends AbstractRunTimeProcessBean<UploadVO> implements UploadProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1732807652621691357L;
	public UploadProcessBean(String applicationId) {
		super(applicationId);
	}

	@Override
	protected IRuntimeDAO getDAO() throws Exception {
		return new RuntimeDaoManager().abstractUploadDAO(getConnection(), getApplicationId());
	}
	/**
	 * 创建
	 * @param vo
	 * @throws Exception
	 */
	public void doCreate(UploadVO vo) throws Exception {
		getDAO().create(vo);
	}
	
	/**
	 * 更新
	 * @param vo
	 * @throws Exception
	 */
	public void doUpdate(UploadVO vo) throws Exception {
		getDAO().update(vo);
	}
	
	/**
	 * 删除
	 */
	public void doRemove(String pk) throws Exception {
		getDAO().remove(pk);
	}
	
	public ValueObject doFindById(String pk) throws Exception {
		return getDAO().find(pk);
	}

	/**
	 * 通过传人字段名和字段的值查询
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @throws Exception
	 */
	public Collection<UploadVO> findByColumnName(String columnName,String columnValue) throws Exception {
		return ((UploadDAO)getDAO()).findByColumnName(columnName,columnValue);
	}
	/**
	 * 通过指定的列和列值查找上传文件
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @throws Exception
	 */
	public UploadVO findByColumnName1(String columnName,String columnValue) throws Exception{
		return ((UploadDAO)getDAO()).findByColumnName1(columnName,columnValue);
	}
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
	public ValueObject doFindByMappingToUploadVO(String mappingColumnName,String fieldid,String tableName,String mappingPrimaryKeyName,String mappinid) throws Exception {
		return ((UploadDAO)getDAO()).findByMappingToUploadVO(mappingColumnName,fieldid,tableName,mappingPrimaryKeyName,mappinid);
	}
	
	
	public static UploadProcess createMonitoProcess(String applicationid) {
		UploadProcess process = new UploadProcessBean(applicationid);
		return process;
	}
}
