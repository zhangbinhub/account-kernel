package OLink.bpm.core.upload.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.util.file.FileOperate;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.SQLFunction;
import org.apache.log4j.Logger;

public abstract class AbstractUploadDAO {


	final static String _TBNAME = "T_UPLOAD";
	
	//private static FileOperate foperate = new FileOperate(); // 过滤常用文件类型

	protected final static Logger log = Logger.getLogger(AbstractUploadDAO.class);

	protected Connection connection;

	protected String applicationId;

	protected String dbType = "Oracle :";// 标识数据库类型

	protected SQLFunction sqlFuction;

	protected String schema = "";

	public AbstractUploadDAO(Connection conn, String applicationId) throws Exception {
		this.connection = conn;
		this.applicationId = applicationId;
	}
	
	/**
	 * 创建
	 * @param vo
	 * @throws Exception
	 */
	public void create(ValueObject vo) throws Exception {
		storeUpload((UploadVO) vo);
	}

	/**
	 * 删除
	 * @param pk
	 * @throws Exception
	 */
	public void remove(String pk) throws Exception {
		PreparedStatement statement = null;
		try {
			String sql = "DELETE FROM " + getFullTableName(_TBNAME) + " WHERE ID=?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, pk);
			statement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 更新
	 * @param vo
	 * @throws Exception
	 */
	public void update(ValueObject vo) throws Exception {
		updateUpload((UploadVO)vo);
	}
	/**
	 * 查找
	 * @param pk
	 * @return
	 * @throws Exception
	 */
	public ValueObject find(String pk) throws Exception {
		UploadVO uploadVO = new UploadVO();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT ID,NAME,IMGBINARY,FIELDID,TYPE,FILESIZE,USERID,MODIFYDATE,PATH,FOLDERPATH  FROM " + getFullTableName(_TBNAME);
			sql += " WHERE ID = ?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, pk);
			rs = statement.executeQuery();
			if (rs.next()) {
				if(rs.getString(1)!=null){
					uploadVO.setId(rs.getString(1));
				}
				if(rs.getString(2)!=null){
					uploadVO.setName(rs.getString(2));
				}
				if(rs.getBlob(3)!=null){
					uploadVO.setImgBinary(rs.getBlob(3).getBinaryStream());
				}
				if(rs.getString(4)!=null){
					uploadVO.setFieldid(rs.getString(4));
				}
				if(rs.getString(5)!=null){
				uploadVO.setType(rs.getString(5));
				}
				if(rs.getLong(6)!=0){
					uploadVO.setSize(rs.getLong(6));
				}
				if(rs.getString(7)!=null){
				uploadVO.setUserid(rs.getString(7));
				}
				if(rs.getString(8)!=null){
					uploadVO.setModifyDate(rs.getString(8));
				}
				if(rs.getString(9)!=null){
					uploadVO.setPath(rs.getString(9));
				}
				if(rs.getString(10)!=null){
					uploadVO.setFolderPath(rs.getString(10));
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
		return uploadVO;
	}
	
	/**
	 * 通过指定的列和列值查找上传文件
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @throws Exception
	 */
	public Collection<UploadVO> findByColumnName(String columnName,String columnValue) throws Exception {
		ArrayList<UploadVO> docs = new ArrayList<UploadVO>();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT ID,NAME,IMGBINARY,FIELDID,TYPE,FILESIZE,USERID,MODIFYDATE,PATH,FOLDERPATH  FROM " + getFullTableName(_TBNAME);
			sql += " WHERE "+columnName+" = ?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, columnValue);
			rs = statement.executeQuery();
			while (rs != null && rs.next()) {
				UploadVO uploadVO = new UploadVO();
				if(rs.getString(1)!=null){
					uploadVO.setId(rs.getString(1));
				}
				if(rs.getString(2)!=null){
					uploadVO.setName(rs.getString(2));
				}
				if(rs.getBlob(3).getBinaryStream()!=null){
					uploadVO.setImgBinary(rs.getBlob(3).getBinaryStream());
				}
				if(rs.getString(4)!=null){
					uploadVO.setFieldid(rs.getString(4));
				}
				if(rs.getString(5)!=null){
				uploadVO.setType(rs.getString(5));
				}
				if(rs.getLong(6)!=0){
					uploadVO.setSize(rs.getLong(6));
				}
				if(rs.getString(7)!=null){
				uploadVO.setUserid(rs.getString(7));
				}
				if(rs.getString(8)!=null){
					uploadVO.setModifyDate(rs.getString(8));
				}
				if(rs.getString(9)!=null){
					uploadVO.setPath(rs.getString(9));
				}
				if(rs.getString(10)!=null){
					uploadVO.setFolderPath(rs.getString(10));
				}
				docs.add(uploadVO);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
		return docs;
	}
	
	
	/**
	 * 通过指定的列和列值查找上传文件
	 * @param columnName
	 * @param columnValue
	 * @return
	 * @throws Exception
	 */
	public UploadVO findByColumnName1(String columnName,String columnValue) throws Exception {
		UploadVO uploadVO = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT ID,NAME,IMGBINARY,FIELDID,TYPE,FILESIZE,USERID,MODIFYDATE,PATH,FOLDERPATH  FROM " + getFullTableName(_TBNAME);
			sql += " WHERE "+columnName+" = ?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, columnValue);
			rs = statement.executeQuery();
			while (rs != null && rs.next()) {
				uploadVO = new UploadVO();
				if(rs.getString(1)!=null){
					uploadVO.setId(rs.getString(1));
				}
				if(rs.getString(2)!=null){
					uploadVO.setName(rs.getString(2));
				}
				if(rs.getBlob(3)!=null){
					uploadVO.setImgBinary(rs.getBlob(3).getBinaryStream());
				}
				if(rs.getString(4)!=null){
					uploadVO.setFieldid(rs.getString(4));
				}
				if(rs.getString(5)!=null){
				uploadVO.setType(rs.getString(5));
				}
				if(rs.getLong(6)!=0){
					uploadVO.setSize(rs.getLong(6));
				}
				if(rs.getString(7)!=null){
				uploadVO.setUserid(rs.getString(7));
				}
				if(rs.getString(8)!=null){
					uploadVO.setModifyDate(rs.getString(8));
				}
				if(rs.getString(9)!=null){
					uploadVO.setPath(rs.getString(9));
				}
				if(rs.getString(10)!=null){
					uploadVO.setFolderPath(rs.getString(10));
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
		return uploadVO;
	}
	
	/**
	 * 存储所建的storeUpload.
	 * 
	 * @param doc
	 *           
	 */
	public void storeUpload(UploadVO doc) throws Exception {
		boolean isExit = false;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT COUNT(*)  FROM " + getFullTableName(_TBNAME);
			sql += " WHERE ID = ?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, doc.getId());
			rs = statement.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				isExit = true;
			}

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}

		if (isExit) {
			updateUpload(doc);
		} else {
			if(doc.getImgBinary()!=null){
				createUpload(doc);
			}else{
				createUploadNoImg(doc);
			}
		}
	}
	
	/**
	 * 更新
	 * @param doc
	 * @throws Exception
	 */
	public void updateUpload(UploadVO doc) throws Exception {
		PreparedStatement statement = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE " + getFullTableName(_TBNAME) + " SET");
			if(doc.getName()!=null){
				sql.append(" ,NAME='"+doc.getName()+"'");
			}
			if(doc.getImgBinary()!=null){
				sql.append(",IMGBINARY=?");//update by zb 2015-1-10
			}
			if(doc.getFieldid()!=null){
				sql.append(",FIELDID='"+doc.getFieldid()+"'");
			}
			if(doc.getType()!=null){
				sql.append(",TYPE='"+doc.getType()+"'");
			}
			if(doc.getSize()!=0){
				sql.append(",FILESIZE="+doc.getSize());
			}
			if(doc.getUserid()!=null){
				sql.append(",USERID='"+doc.getUserid()+"'");
			}
			if(doc.getModifyDate()!=null){
				sql.append(",MODIFYDATE='"+doc.getModifyDate()+"'");
			}
			if(doc.getPath()!=null){
				sql.append(",PATH='"+doc.getPath()+"'");
			}
			if(doc.getFolderPath()!=null){
				sql.append(",FOLDERPATH='"+doc.getFolderPath()+"'");
			}
			sql.deleteCharAt(sql.indexOf(","));
			sql.append(" where id ='"+doc.getId()+"'");
			// Transfer Data
			statement = connection.prepareStatement(sql.toString());
			//add by zb 2015-1-10
			if (doc.getImgBinary() != null) {
				statement.setBinaryStream(1, doc.getImgBinary(), doc
						.getImgBinary().available());
			}
			// Exec SQL
			if (statement.executeUpdate() < 1) {
				throw new Exception("Row does not exist");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 创建
	 * @param doc
	 * @throws Exception
	 */
	public void createUpload(UploadVO doc) throws Exception {
		
		PreparedStatement statement = null;
		
		String sql = "INSERT INTO " + getFullTableName(_TBNAME) + "(ID,NAME,IMGBINARY,FIELDID,TYPE,FILESIZE,USERID,MODIFYDATE,PATH,FOLDERPATH) VALUES (?,?,?,?,?,?,?,?,?,?)";
		try{
			statement = connection.prepareStatement(sql);
			statement.setString(1, doc.getId());
			statement.setString(2, doc.getName());
			statement.setBinaryStream(3,doc.getImgBinary(),doc.getImgBinary().available());
			statement.setString(4, doc.getFieldid());
			statement.setString(5, doc.getType());
			statement.setLong(6, doc.getSize());
			statement.setString(7, doc.getUserid());
			statement.setString(8, doc.getModifyDate());
			statement.setString(9, doc.getPath());
			statement.setString(10, doc.getFolderPath());
			// Exec SQL
			statement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);

		}
	}
	
	
	/**
	 * 创建
	 * @param doc
	 * @throws Exception
	 */
	public void createUploadNoImg(UploadVO doc) throws Exception {
		
		PreparedStatement statement = null;
		
		String sql = "INSERT INTO " + getFullTableName(_TBNAME) + "(ID,NAME,FIELDID,TYPE,FILESIZE,USERID,MODIFYDATE,PATH,FOLDERPATH) VALUES (?,?,?,?,?,?,?,?,?)";
		try{
			statement = connection.prepareStatement(sql);
			statement.setString(1, doc.getId());
			statement.setString(2, doc.getName());
			statement.setString(3, doc.getFieldid());
			statement.setString(4, doc.getType());
			statement.setLong(5, doc.getSize());
			statement.setString(6, doc.getUserid());
			statement.setString(7, doc.getModifyDate());
			statement.setString(8, doc.getPath());
			statement.setString(9, doc.getFolderPath());
			// Exec SQL
			statement.executeUpdate();

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);

		}
	}
	
	
	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "." + tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
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
	public ValueObject findByMappingToUploadVO(String mappingColumnName,String fieldid,String tableName,String mappingPrimaryKeyName,String mappinid) throws Exception{
		UploadVO uploadVO = new UploadVO();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT "+mappingColumnName+"  FROM " + tableName;
			sql += " WHERE "+mappingPrimaryKeyName+" = ?";

//			log.info(dbType + sql);

			statement = connection.prepareStatement(sql);
			statement.setString(1, mappinid);
			rs = statement.executeQuery();
			if (rs.next()) {
				uploadVO.setId(UUID.randomUUID().toString());
				uploadVO.setName(UUID.randomUUID().toString()+"."+ FileOperate.getFileType(rs.getBlob(1).getBinaryStream()));
				uploadVO.setImgBinary(rs.getBlob(1).getBinaryStream());
				uploadVO.setFieldid(fieldid);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
		return uploadVO;
	}
	

}
