package OLink.bpm.core.dynaform.document.ejb;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.work.ejb.WorkVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.core.dynaform.form.ejb.Form;

/**
 * 
 * DocumentProcess 为Document逻辑处理接口类.
 * 
 * @author Marky
 * 
 */
public interface DocumentProcess extends IRunTimeProcess<Document> {

	/**
	 * 根据父文档主键(primary key)查询,返回所属父Document的子Document集合
	 * 
	 * @param parentid
	 *            父文档主键(primary key)
	 * @return 所属父Document的子Document集合
	 * @throws Exception
	 */
	Collection<Document> queryByParentID(String parentid)
			throws Exception;

	/**
	 * 根据父文档ID(primary key)与子表单名查询，返回所属父Document的子Document集合.
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
	 * 根据用户创建新Document.
	 * 
	 * @param user
	 *            webuser
	 * @param vo
	 *            ValueObject
	 */
	void doCreate(ValueObject vo, WebUser user)
			throws Exception;

	/**
	 * 根据用户创建或更新Document
	 * 
	 * @param vo
	 *            ValueObject
	 * @param user
	 *            webuser
	 * @throws Exception
	 */
	void doCreateOrUpdate(ValueObject vo, WebUser user)
			throws Exception;

	/**
	 * 根据主键ID，删除文档对象
	 * 
	 * @param id
	 *            主键
	 */
	void doRemove(String id) throws Exception;

	/**
	 * 根据用户更新所属用户相应的文档，文档无状态并有流程时开启流程.
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            参数对象
	 * @param user
	 *            用户对象
	 * @return Document对象
	 * @throws Exception
	 */
	Document doStartFlow(Document doc, ParamsTable params, WebUser user)
			throws Exception;

	/**
	 * 流程提交处理，更新Document.
	 * 
	 * @param doc
	 *            当前Document
	 * @param params
	 *            参数
	 * @param currNodeId
	 *            当前节点
	 * @param nextNodeIds
	 *            下一个节点
	 * @param flowType
	 * @param comments
	 * @param evt
	 * @param user
	 * @return
	 * @throws Exception
	 */
	Document doFlow(Document doc, ParamsTable params, String currNodeId,
					String[] nextNodeIds, String flowType, String comments, WebUser user)
			throws Exception;

	void doCreate(ValueObject vo) throws Exception;

	/**
	 * 更新文档对象
	 * 
	 * @param vo
	 *            值对象
	 */
	void doUpdate(ValueObject vo) throws Exception;
	
	void doUpdate(ValueObject vo, boolean withVersionControl) throws Exception;

	void doUpdate(ValueObject object, WebUser user, boolean withVersionControl) throws Exception;
	
	void doUpdate(ValueObject vo, WebUser user)
			throws Exception;

	// public abstract Document doStore(Document docVO, WebUser user)
	// throws Exception;
	/**
	 * 根据符合DQL语句以及应用标识查询,返回文档的DataPackage . DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * 
	 * 
	 * @param dql
	 *            DQL语句
	 * @retur 文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQL(String dql, String domainid)
			throws Exception;

	/**
	 * 根据符合DQL语句以及域名来查询,返回文档的DataPackage
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * 
	 * 
	 * @param dql
	 * @param domainName
	 * @return
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLDomainName(String dql,
											   String domainName) throws Exception;

	// public abstract DataPackage queryByFormName(String formname,
	// String application) throws Exception;

	// public abstract Document findByItemAndFormname(String itemName,
	// String itemValue, String formanme, String application)
	// throws Exception;

	// public abstract void removeByItemAndFormname(String itemName,
	// String itemValue, String formname, String application)
	// throws Exception;

	// public abstract Collection findDocsByItemAndFormname(String itemName,
	// String itemValue, String formanme, String application)
	// throws Exception;

	// public abstract Collection queryByHql(String hql, String application)
	// throws Exception;
	/**
	 * 根据符合DQL语句以及应用标识查询并分页,返回文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#datas
	 * @param dql
	 *            dql语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示行数
	 * @return 文档的DataPackage
	 * @throws Exceptio
	 */
	DataPackage<Document> queryByDQLPage(String dql, int page,
										 int lines, String domainid) throws Exception;

	/**
	 * 根据符合DQL语句,参数以及应用标识查询,返回文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#datas
	 * @param dql
	 *            DQL语句
	 * @param params
	 *            参数
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQL(String dql,
									 ParamsTable params, String domainid) throws Exception;

	/**
	 * 根据SQL语句,参数以及应用标识查询,返回文档的DataPackage. DataPackage为一个封装类，。
	 * 
	 * @param sql
	 *            SQL语句
	 * @param params
	 *            参数
	 * @param domainid
	 *            域标识
	 * @return 文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryBySQL(String sql,
									 ParamsTable params, String domainid) throws Exception;

	/**
	 * 根据SQL语句,参数以及应用标识查询,返回文档的DataPackage. DataPackage为一个封装类
	 * 
	 * @param sql
	 *            SQL语句
	 * @param domainid
	 *            域标识
	 * @return 文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryBySQL(String sql, String domainid)
			throws Exception;

	/**
	 * 根据符合DQL语句,参数以及应用标识查询,返回按设置缓存的此模块下符合条件的文档的DataPackage
	 * 
	 * @see DataPackage#datas
	 * @param dql
	 *            查询语句
	 * @param params
	 *            参数对象
	 * @see ParamsTable#params
	 * @return 此模块下符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLWithCache(String dql,
											  ParamsTable params, String domainid) throws Exception;

	/**
	 * 根据符合DQL语句以及应用标识查询,返回按设置缓存的此模块下符合条件的文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#datas
	 * @param dql
	 *            DQL语句
	 * @return 设置缓存的此模块下符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLWithCache(String dql, String domainid)
			throws Exception;
	
	/**
	 * 根据符合QL语句以及应用标识查询,返回按设置缓存的此模块下符合条件的文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#datas
	 * @param sql
	 *            SQL语句
	 * @return 设置缓存的此模块下符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryBySQLWithCache(String sql, String domainid)
			throws Exception;	

	/**
	 * 根据DQL语句,参数表以及应用标识查询 ,返回文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @see DataPackage#datas
	 * @param dql
	 *            DQL语句
	 * @param params
	 *            参数
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示的行数
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLPage(String dql,
										 ParamsTable params, int page, int lines, String domainid)
			throws Exception;

	/**
	 * 根据SQL语句,以及应用标识查询 ,返回文档的DataPackage. DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @param sql
	 *            DQL语句
	 * @param page
	 *            当前页码
	 * @param lines
	 *            显示记录
	 * @param domainid
	 *            域名
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryBySQLPage(String sql, int page,
										 int lines, String domainid) throws Exception;

	/**
	 * 根据SQL语句,以及应用标识查询 ,返回文档的DataPackage. DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @param sql
	 *            DQL语句
	 * @param params
	 *            参参数
	 * @param lines
	 *            当前页码
	 * @param domainid
	 *            域名
	 * @return 符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryBySQLPage(String sql,
										 ParamsTable params, int page, int lines, String domainid)
			throws Exception;

	/**
	 * 根据符合DQL语句,模块名,参数以及应用标识查询并分页,返回按设置缓存的此模块下符合条件的文档的DataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            DQL语句
	 * @param params
	 *            参数对象
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示的行数
	 * @return 此模块下符合条件的文档的DataPackage
	 * @throws Exception
	 */
	DataPackage<Document> queryByDQLPageWithCache(String dql,
												  ParamsTable params, int page, int lines, String domainid)
			throws Exception;

	// public DataPackage queryByHQL(String dql, String application)
	// throws Exception;

	// public DataPackage queryByHQL(String dql, ParamsTable params,
	// String application) throws Exception;

	// public DataPackage queryByHQLWithCache(String dql, ParamsTable params,
	// String application) throws Exception;

	// public Collection queryByHQLWithCache(String hql, String application)
	// throws Exception;

	// public DataPackage queryByHQLPage(String dql, ParamsTable params, int
	// page,
	// int lines, String application) throws Exception;

	// public DataPackage queryByHQLPageWithCache(String dql, ParamsTable
	// params,
	// int page, int lines, String application) throws Exception;

	/**
	 * 根据符合DQL语句,最后修改文档日期,以及应用标识查询并分页,返回文档的DDataPackage.
	 * DataPackage为一个封装类，此类封装了所得到的文档数据并分页。
	 * 
	 * @see DataPackage#datas
	 * @see DataPackage#getPageNo()
	 * @see DataPackage#getLinesPerPage()
	 * @see DataPackage#getPageCount()
	 * @param dql
	 *            DQL 语句
	 * @param date
	 *            最后修改文档日期
	 * @param page
	 *            当前页码
	 * @param lines
	 *            每页显示的行数
	 * @return 符合条件的文档的DDataPackage
	 * @throws Exception
	 */
	Iterator<Document> queryByDQLAndDocumentLastModifyDate(
			String dql, Date date, int page, int lines, String domainid)
			throws Exception;

	/**
	 * 获取需要导出的文档个数
	 * 
	 * @param dql
	 *            dql语句
	 * @param date
	 *            日期型
	 * @param domainid
	 *            域标识
	 * @return 文档个数
	 * @throws Exception
	 */
	long getNeedExportDocumentTotal(String dql, Date date,
									String domainid) throws Exception;

	// public abstract String getFormFullName(String formid, String application)
	// throws Exception;

	// public abstract Collection doQueryField(String fieldName, String
	// moduleid,
	// String application) throws Exception;
	/**
	 * 根据符合DQL语句以及应用标识查询并分页,返回文档的集合
	 * 
	 * @param dql
	 *            DQL语句
	 * @param pos
	 *            当前页码
	 * @param size
	 *            每页显示的行数
	 * @return 文档的集合
	 */
	Collection<Document> queryLimitByDQL(String dql, int pos, int size,
										 String domainid) throws Exception;

	/**
	 * 根据DQL语句以及文档某字段名查询,返回此文档此字段总和
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
	 * 根据SQL语句以及文档某字段名查询,返回此文档此字段总和
	 * 
	 * @param sql
	 *            sql语句
	 * 
	 * @return 文档此字段总和
	 * @throws Exception
	 */
	double sumBySQL(String sql, String domainid) throws Exception;

	/**
	 * 根据DQL语句查询,返回此文档总行数
	 * 
	 * @param dql
	 *            DQL语句
	 * @return 文档总行数
	 * @throws Exception
	 */
	long countByDQL(String dql, String domainid) throws Exception;

	/**
	 * 根据符合DQL执行语句以及应用标识查询并分页,返回文档的数据集
	 * 
	 * @param dql
	 *            DQL语句
	 * @param pos
	 *            页码
	 * @param size
	 *            每页显示行数
	 * @return 文档的数据集
	 * @throws Exceptio
	 */
	Iterator<Document> iteratorLimitByDQL(String dql, int pos, int size,
										  String domainid) throws Exception;

	/**
	 * 根据文档主键,返回文档值对象
	 * 
	 * @param pk
	 *            值对象主键
	 */
	ValueObject doView(String pk) throws Exception;

	/**
	 * 根据文档主键,批量移除文档
	 * 
	 * @param pks
	 *            值对象主键数组
	 */
	void doRemove(String[] pks) throws Exception;

	/**
	 * 将新的文档属性与旧的合并,并保留旧文档所有项目(Item)的值
	 * 
	 * @param dest
	 *            新Document
	 * @param webUser
	 * @return 合并后的Document
	 * @throws Exception
	 */
	Document mergePO(Document dest, WebUser webUser) throws Exception;

	/**
	 * 批量审批文档
	 * 
	 * @param docIds
	 *            文档标识数组
	 * @param user
	 *            WebUser对象
	 * @param evt
	 *            环境
	 * @param params
	 *            页面参数
	 * @param limistList
	 *            下一个节点限制列表
	 * @see Activity#getApproveLimit();
	 * @return 成功条数
	 * @throws Exception
	 */
	int doBatchApprove(String[] docIds, WebUser user, Environment evt,
					   ParamsTable params, Collection<String> limistList) throws Exception;

	/**
	 * 验证文档是否正确性
	 * 
	 * @param doc
	 *            文档
	 * @param params
	 *            页面参数
	 * @param user
	 *            WebUser对象
	 * @return 错误信息列表
	 * @throws Exception
	 */
	Collection<ValidateMessage> doValidate(final Document doc,
										   ParamsTable params, WebUser user) throws Exception;

	/**
	 * 判断当前用户是否可以保存文档. 当返回true时为可以保存文档,否则返回false为不可以保存文档. 此实现根据Document id 与
	 * flow(流程) id 查询当前流程的状态.如果当前还没有流程状态,并且根据flow id 获取的第一节点列表不为空时,返回和true.
	 * 否则根据用户获取当前节点,若存在节点时,返回true.
	 * 
	 * @param doc
	 */
	boolean isDocSaveUser(Document doc, ParamsTable params, WebUser user)
			throws Exception;

	/**
	 * 查询文档的个数,是以标准sql的形式
	 * 
	 * @param sql
	 *            标准sql
	 * @param domainid
	 *            域标识
	 * @return
	 * @throws Exception
	 */
	long countBySQL(String sql, String domainid) throws Exception;

	/**
	 * 尚未开启流程
	 * 
	 * @param state
	 *            当前流程状态
	 * @return
	 */
	boolean isNotStart(Document doc, ParamsTable params);

	/**
	 * 查询最后修改的Document
	 * 
	 * @return 文档集合
	 * @throws Exception
	 */
	Collection<Document> queryModifiedDocuments(Document doc)
			throws Exception;

	/**
	 * 根据用户更新所属用户相应的文档，并对Field 校验.若文档无状态并有流程时开启流程.
	 * 
	 * @param doc
	 *            Document对象
	 * @param params
	 *            参数对象
	 * @param user
	 *            用户对象
	 * @return Document
	 * @throws Exception
	 */
	Document doStartFlowOrUpdate(final Document doc, ParamsTable params,
								 WebUser user) throws Exception;

	/**
	 * 移除文档和子文档一并移除
	 * 
	 * @param vo
	 *            ValueObject
	 * @throws Exception
	 */
	void doRemoveWithChildren(ValueObject vo) throws Exception;

	/**
	 * 创建一个新的文档
	 * 
	 * @param form
	 *            Form
	 * @param user
	 *            web用户
	 * @param params
	 *            参数
	 * @return Document
	 * @throws Exception
	 */
	Document doNewWithOutItems(Form form, WebUser user,
							   ParamsTable params) throws Exception;

	/**
	 * 创建一个新的文档
	 * 
	 * @param form
	 *            Form
	 * @param user
	 *            web用户
	 * @param params
	 *            参数
	 * @return Document
	 * @throws Exception
	 */
	Document doNew(Form form, WebUser user, ParamsTable params)
			throws Exception;

	/**
	 * 创建新文档包含子文档
	 * 
	 * @param form
	 *            表单
	 * @param user
	 *            当前用户
	 * @param params
	 *            参数
	 * @param children
	 *            所包含的子文档
	 * @return 新文档
	 */
	Document doNewWithChildren(Form form, WebUser user,
							   ParamsTable params, Collection<Document> children) throws Exception;

	/**
	 * 根据符合DQL语句以及应用标识查询单个文档
	 * 
	 * @param dql
	 * @return
	 * @throws Exception
	 */
	Document findByDQL(String dql, String domainid) throws Exception;

	/**
	 * 根据符合SQL语句以及应用标识查询单个文档
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	Document findBySQL(String sql, String domainid) throws Exception;

	/**
	 * 根据表单名称删除
	 * 
	 * @param form
	 *            表单对象.
	 * @throws Exception
	 */
	void doRemoveByFormName(Form form) throws Exception;

	/**
	 * 清除字段数据
	 * 
	 * @param form
	 *            表单对象
	 * @param fields
	 *            字段
	 * @throws Exception
	 */
	void doRemoveDocByFields(Form form, String[] fields)
			throws Exception;

	/**
	 * 审批文档
	 * 
	 * @param doc
	 *            文档
	 * @param flowVO
	 *            流程
	 * @param currNodeId
	 *            当前节点ID
	 * @param nextNodeIds
	 *            下一个节点ID
	 * @param flowOption
	 *            流程操作
	 * @param comment
	 *            流程日志
	 * @param evt
	 *            应用环境
	 * @param user
	 *            当前用户
	 * @throws Exception
	 */
	void doApprove(ParamsTable params, FlowStateRT instance,
				   String currNodeId, String[] nextNodeIds, String flowOption,
				   String comment, Environment evt, WebUser user) throws Exception;

	/**
	 * 根据表单名称和条件列表，查找数据文档对象
	 * 
	 * @param formName
	 *            表单名称.
	 * @param condition
	 *            条件列表.
	 * @param user
	 *            当前登录用户.
	 * @return 数据文档对象.
	 * @throws Exception
	 */
	Document doViewByCondition(String formName, Map<?, ?> condition,
							   WebUser user) throws Exception;

	/**
	 * 根据用户书写的SQL查询数据文档集合
	 * 
	 * @param sql
	 *            SQL语句
	 * @return 数据文档集合
	 * @throws Exception
	 */
	Collection<Document> queryBySQL(String sql) throws Exception;

	/**
	 * 创建文档头. 此文档头用来保存不同Document的信息. 此方法实现数据库文档表的相应字列插入相应Document属性的值.
	 * 
	 * @param doc
	 *            Document对象
	 * @throws Exception
	 */
	void createDocumentHead(Document doc) throws Exception;

	/**
	 * 查找符合条件的Work
	 * 
	 * @param sql
	 * @param params
	 * @param page
	 * @param lines
	 * @param domainid
	 * @return
	 * @throws Exception
	 */
	DataPackage<WorkVO> queryWorkBySQLPage(ParamsTable params,
										   int page, int lines, WebUser user) throws Exception;

	DataPackage<WorkVO> queryWorks(ParamsTable params,
								   WebUser user) throws Exception;

	DataPackage<Document> queryByProcedure(String procedure,
										   ParamsTable params, int page, int lines, String domainid)
			throws Exception;

	/**
	 * 更改审批人
	 * 
	 * @param doc
	 * @param params
	 * @param user
	 * @throws Exception
	 */
	void doChangeAuditor(Document doc, ParamsTable params, WebUser user)
			throws Exception;
	
	/**
	 * 判断版本号是否已占用，通常用在乐观逻辑锁
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	boolean isImpropriated(Document doc) throws Exception;
}
