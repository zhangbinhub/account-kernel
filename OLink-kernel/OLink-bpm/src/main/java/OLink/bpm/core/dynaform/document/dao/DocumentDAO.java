// Source file:
// C:\\Java\\workspace\\SmartWeb3\\src\\com\\cyberway\\dynaform\\document\\dao\\DocumentDAO.java

package OLink.bpm.core.dynaform.document.dao;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.work.ejb.WorkVO;

/**
 * 本系统的Document的查询语句运用自定义的DQL语句.DQL语句类似HQL语句.
 * <p>
 * DQL查询语句语法为:$formname=formname(模块表单名)+ 查询条件;
 * 
 * 例1: 查询付款费用模块下payment的Document的一条为广东省广州市的记录.
 * <p>
 * formname="付款费用/payment";条件为w="and 省份='广东省' and 城市='广州'",条件用"and" 连接起来.
 * 此时的DQL语句为$formname=formname+w . 此处的"付款费用"为模块名,"payment"该模块下的表单名.((表单名与动态表名同名)
 * <p>
 * 系统会将上述所得的DQL转为hibernate的HQL, 最后得出的SQL语句为"SELECT Item_省份,item_城市 FROM
 * tlk_payment where item_省份='广东省' and item_城市='广州'".
 * tlk_payment为动态表名(表名规则为前缀"tlk"+表单名). (动态表的字段名为前缀"item_"+表单字段名).
 * <p>
 * 如果查询语句中的字列有Document的属性时.要加上"$"+属性名,如:$id,$formname.
 * 有Document属性字段的DQL:$formname="付款费用/payment and $id='1000' and
 * $childs.id='1111'";id,chinlds为Document的属性名. Document的属性名有如下: ID, PARENT,
 * LASTMODIFIED, FORMNAME, STATE, AUDITDATE, AUTHOR, CREATED, FORMID, ISTMP,
 * FLOWID, VERSIONS, SORTID, APPLICATIONID, STATEINT, STATELABEL ".
 * 
 * <p>
 * 若查询语句中的字列有item字段时直接写item名.如上述的省份,城市.$formname="付款费用/payment and 省份='广东省 and
 * 城市='广州'".省份,城市为ITEM字段.
 * 
 * 此类为Document DAO 接口类.
 * 
 * @author Marky
 * 
 */
public interface DocumentDAO extends IRuntimeDAO {

	void removeDocumentByField(Form form, String[] fields)
			throws Exception;

	void removeDocumentByForm(Form form) throws Exception;

	/**
	 * 根据父文档主键查询,获取所属父Document的子Document集合
	 * 
	 * @param parentid
	 *            父文档ID(primary key)
	 * @return Document集合
	 * @throws Exception
	 */
	Collection<Document> queryByParentID(String parentid)
			throws Exception;

	/**
	 * 根据父文档ID(primary key)与子表单名查询，返回所属父Document的子Document集合。
	 * 
	 * @param parentid
	 *            父文档ID(primary key)
	 * @param formName
	 *            子表单名
	 * @return 所属父Document的子Document集合.
	 * @throws Exception
	 */

	Collection<Document> queryByParentID(String parentid, String formName)
			throws Exception;

	/**
	 * 根据父文档ID(primary key)与子表单名查询，返回所属父Document的子Document集合。
	 * 
	 * @param parentid
	 *            父文档ID(primary key)
	 * @param formName
	 *            子表单名
	 * @param istmp
	 *            是否临时表单可选0|1|null
	 * @return 所属父Document的子Document集合.
	 * @throws Exception
	 */
	Collection<Document> queryByParentID(String parentid,
										 String formName, Boolean istmp) throws Exception;

	/**
	 * 存储Document Execute Create Or Update
	 * 
	 * @param doc
	 *            Document
	 * @throws Exception
	 */
	void storeDocument(Document doc) throws Exception;

	/**
	 * 根据Document主键查询Document.
	 * 
	 * @param id
	 *            文档主键
	 * @return 文档值对象
	 * @throws Exception
	 */
	ValueObject find(String id) throws Exception;

	long getNeedExportDocumentTotal(String dql, Date date,
									String domainid) throws Exception;

	/**
	 * 根据符合DQL语句以及应用标识查询,返回文档的DataPackage .
	 * <p>
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            DQL语句
	 * @param application
	 *            应用标识
	 * @retur 文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQL(String dql, String domainid)
			throws Exception;

	/**
	 * 根据符合DQL执行语句,参数表以及应用标识查询,返回文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            DQL语句
	 * @param params
	 *            参数表
	 * @param application
	 *            应用标识
	 * @return 文档DataPackage
	 * @see ParamsTable#params
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQL(String dql, ParamsTable params,
									 String domainid) throws Exception;

	DataPackage<Document> queryBySQL(String sql, String domainid)
			throws Exception;

	DataPackage<Document> queryBySQL(String sql, ParamsTable params,
									 String domainid) throws Exception;

	/**
	 * 根据符合DQL语句以及应用标识查询并分页,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            dql语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLPage(String dql, int page,
										 int lines, String domainid) throws Exception;

	DataPackage<Document> queryBySQLPage(String sql, int page,
										 int lines, String domainid) throws Exception;

	DataPackage<Document> queryBySQLPage(String sql, ParamsTable params,
										 int page, int lines, String domainid) throws Exception;

	/**
	 * 根据符合DQL语句,参数表以及应用标识查询并分页,返回文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            dql语句
	 * @param params
	 *            参数
	 * @see ParamsTable#params
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLPage(String dql, ParamsTable params,
										 int page, int lines, String domainid) throws Exception;

	// public DataPackage queryByHQL(String hql, String application)
	// throws Exception;

	// public DataPackage queryByHQL(String hql, ParamsTable params,
	// String application) throws Exception;

	// public DataPackage queryByHQLPage(String hql, ParamsTable params, int
	// page,
	// int lines, String application) throws Exception;

	// public DataPackage queryByFormName(String formname, String application)
	// throws Exception;

	// public Collection findDocsByItemAndFormname(String itemName,
	// String itemValue, String formname, String application)
	// throws Exception;

	// public Document findByItemAndFormname(String itemName, String itemValue,
	// String formname, String application) throws Exception;

	// public void store(Document doc, WebUser user) throws Exception;

	// public void removeByItemAndFormname(String itemName, String itemValue,
	// String formname, String application) throws Exception;
	/**
	 * 根据符合DQL语句,最后修改文档日期,以及应用标识查询并分页,返回文档的DataPackage.
	 * 
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            dql语句
	 * @param date
	 *            最后修改文档日期
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	Iterator<Document> queryByDQLAndDocumentLastModifyDate(String dql,
														   Date date, int page, int lines, String domainid) throws Exception;

	// public String findFormFullName(String formid, String application)
	// throws Exception;

	// public Collection queryField(String fieldName, String moduleid,
	// String application) throws Exception;
	/**
	 * 根据符合DQL语句,以及应用标识查询并分页,返回文档的集合.
	 * 
	 * @see DataPackage#datas
	 * @param dql
	 *            dql语句
	 * @param pos
	 *            页码
	 * @param size
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的集合
	 */
	Collection<Document> queryLimitByDQL(String dql, int pos, int size,
										 String domainid) throws Exception;

	/**
	 * 根据DQL语句以及文档某字段名查询,返回此文档此字段总和.
	 * 
	 * @param dql
	 *            dql语句
	 * @param fieldName
	 *            字段名
	 * @return 文档此字段总和
	 * @throws Exception
	 */
	double sumByDQL(String dql, String fieldName, String domainid)
			throws Exception;

	/**
	 * 根据DQL语句查询,返回此文档总行数..
	 * 
	 * @param dql
	 *            DQL语句
	 * @return 文档总行数
	 * @throws Exception
	 */

	long countByDQL(String dql, String domainid) throws Exception;

	/**
	 * 根据符合DQL执行语句以及应用标识查询并分页,返回文档的数据集.
	 * 
	 * @param dql
	 *            DQL语句
	 * @param pos
	 *            页码
	 * @param size
	 *            每页显示行数
	 * @param application
	 *            应用标识
	 * @return 文档的数据集
	 * @throws Exceptio
	 */
	Iterator<Document> iteratorLimitByDQL(String dql, int pos, int size,
										  String domainid) throws Exception;

	/**
	 * 删除此Document
	 * 
	 * @param doc
	 *            Document
	 * @throws Exception
	 */
	void removeDocument(Document doc) throws Exception;

	/**
	 * 根据用户,更新相应此文档.
	 * 
	 * @param doc
	 *            文档对象
	 * @param user
	 *            webuser
	 * @throws Exception
	 */
	void updateDocument(Document doc) throws Exception;

	void createDocument(Document doc) throws Exception;

	void createDocument(Document doc, int tabelType) throws Exception;

	int findVersions(String id) throws Exception;

	double sumBySQL(String sql, String domainid) throws Exception;

	long countBySQL(String sql, String domainid) throws Exception;

	boolean checkTable(String tableName) throws Exception;

	Collection<Document> queryModifiedDocuments(Document doc)
			throws Exception;

	boolean isExist(String id) throws Exception;

	Document findByDQL(String dql, String domainid) throws Exception;

	Document findBySQL(String sql, String domainid) throws Exception;

	// For Authority Table testing. UNSTABLE
	void createAuthDocWithCondition(String formName, String docId,
									Collection<?> condition) throws Exception;

	Collection<Document> queryBySQL(String sql, int page, int lines,
									String domainid) throws Exception;

	void createDocumentHead(Document doc) throws Exception;

	/**
	 * 删除Auth表对应记录
	 * 
	 * @param doc
	 *            Document对象
	 * @throws Exception
	 */
	void removeAuthByDoc(Document doc) throws Exception;

	DataPackage<WorkVO> queryWorkBySQLPage(String sql, int page,
										   int lines, WebUser user) throws Exception;

	DataPackage<WorkVO> queryWorkBySQLPage(ParamsTable params, int page,
										   int lines, WebUser user) throws Exception;

	void setWorkProperties(WorkVO work, ResultSet rs);

	DataPackage<Document> queryByProcedure(String procedure,
										   ParamsTable params, int page, int lines, String domainid)
			throws Exception;
}
