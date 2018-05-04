package OLink.bpm.core.fieldextends.dao;

import java.util.List;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.fieldextends.ejb.FieldExtendsVO;
import org.hibernate.Query;
import org.hibernate.Session;

import OLink.bpm.util.StringUtil;

public class HibernateFieldExtendsDAO extends HibernateBaseDAO<FieldExtendsVO> implements FieldExtendsDAO {
	public HibernateFieldExtendsDAO(String voClassName) {
		super(voClassName);
	}

	
	/**
	 * 查询所有属于用户表的扩展字段
	 * @SuppressWarnings hibernate3.2不支持泛型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FieldExtendsVO> queryUserFieldExtends(String domain) throws Exception{
		List<FieldExtendsVO> fieldExtendses = null;
		
		Session session = currentSession();
		String hql = "from FieldExtendsVO vo where vo.domainid='" + domain + "'";
		Query query = session.createQuery(hql);
		
		fieldExtendses = query.list();
		
		return fieldExtendses;
	}
	
	/**
	 * 根据所属模块和字段名，查询出字段对象
	 * @param forTable 模块名
	 * @param name 字段名
	 * @return 返回FieldExtendsVO对像集合
	 * @SuppressWarnings hibernate3.2不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FieldExtendsVO> queryFieldExtendsByForTableAndName(String domain, String forTable,String name) throws Exception{
		List<FieldExtendsVO> fieldExtendses = null;
		Session session = currentSession();
		
		String hql = "from FieldExtendsVO f where f.forTable=:forTable and f.name=:name and f.domainid=:domain";
		Query query = session.createQuery(hql);
		query.setString("forTable", forTable);
		query.setString("name", name);
		query.setString("domain", domain);
		
		
		fieldExtendses = query.list();
		
		return fieldExtendses;
	}
	
	/**
	 * 根据ID查询FieldExtendsVO对象
	 * @param fid FieldExtendsVO对象的ID
	 * @SuppressWarnings hibernate3.2不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FieldExtendsVO> queryFieldExtendsByFid(String fid) throws Exception{
		List<FieldExtendsVO> fieldExtendses = null;
		Session session = currentSession();
		
		String hql = "from FieldExtendsVO f where f.fid=:fid";
		Query query = session.createQuery(hql);
		query.setString("fid", fid);
		
		
		fieldExtendses = query.list();
		
		return fieldExtendses;
	}
	
	/**
	 * 查询当前字段是否存在数据
	 * @param forTable 字段所在表
	 * @SuppressWarnings hibernate3.2不支持泛型
	 * @param fieldName 字段名
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List checkFieldHasData(String domain, String forTable,String fieldName)throws Exception{
		Session session = currentSession();
		
		String hql = "from UserVO user where (user." + fieldName + " is not null) and user.domainid='" + domain + "'";
		
		if(FieldExtendsVO.TABLE_DEPT.equals(forTable))
			hql = "from DepartmentVO d where (d." + fieldName + " is not null) and d.domainid='" + domain + "'";
		
		Query query = session.createQuery(hql);
		
		
		return query.list();
	}
	
	/**
	 * 根据ID集合删除字段
	 * @param fids 字段ID集合
	 * @throws Exception
	 */
	public void deleteFieldExtendsByIds(List<String> fids)throws Exception{
		Session session = currentSession();
		String hql = "delete from FieldExtendsVO f where f.fid in(:fids)";
		Query query = session.createQuery(hql);
		query.setParameterList("fids", fids);
		query.executeUpdate();
	}
	
	/**
	 * 根据模块名查找字段集合
	 * @SuppressWarnings hibernate3.2不支持泛型
	 * @param forTable 字段所属模块
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FieldExtendsVO> queryFieldExtendsByTable(String domain, String forTable)throws Exception{
		List<FieldExtendsVO> fieldExtendses = null;
		Session session = currentSession();
		
		String hql = "from FieldExtendsVO f where f.forTable=:forTable and f.domainid=:domain order by f.sortNumber";
		Query query = session.createQuery(hql);
		query.setString("forTable", forTable);
		query.setString("domain", domain);
		
		fieldExtendses = query.list();
		
		return fieldExtendses;
	}
	
	/**
	 * 根据模块名和字段可见性查找字段集合
	 * @param forTable 字段所属模块
	 * @param enabel 可见性
	 * @SuppressWarnings hibernate3.2不支持泛型@SuppressWarnings hibernate3.2不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<FieldExtendsVO> queryFieldExtendsByTableAndEnabel(String domain, String forTable,Boolean enabel)throws Exception{
		List<FieldExtendsVO> fieldExtendses = null;
		Session session = currentSession();
		
		String hql = "from FieldExtendsVO f where f.forTable=:forTable and f.enabel=:enabel and f.domainid=:domain order by f.sortNumber";
		Query query = session.createQuery(hql);
		query.setString("forTable", forTable);
		query.setBoolean("enabel", enabel);
		query.setString("domain", domain);
		
		fieldExtendses = query.list();
		
		return fieldExtendses;
	}
	
	/**
	 * 清空field在相应表中的数据
	 * @param tableName 表名
	 * @param fieldName 字段名
	 * @throws Exception
	 */
	public void cleanFieldData(String domain, String tableName,String fieldName)throws Exception{
		Session session = currentSession();
		
		String hql = "update " + tableName + " u set u." + fieldName + "=null where u.domainid=:domain";
		Query query = session.createQuery(hql);
		query.setString("domain", domain);
		query.executeUpdate();
	}


	public DataPackage<FieldExtendsVO> queryByTypeAndForTable(String domain, String type, String table,
															  int page, int lines) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("FROM " + this._voClazzName + " vo");
		if (!StringUtil.isBlank(type)) {
			buffer.append(" WHERE vo.type LIKE '%" + type + "%'");
		}
		if (!StringUtil.isBlank(table)) {
			if (buffer.toString().indexOf("WHERE") >= 0) {
				buffer.append(" AND vo.forTable LIKE '%"+table+"%'");
			} else {
				buffer.append(" WHERE vo.forTable LIKE '%"+table+"%'");
			}
		}
		if (buffer.toString().indexOf("WHERE") >= 0) {
			buffer.append(" AND vo.domainid = '" + domain + "'");
		} else {
			buffer.append(" WHERE vo.domainid = '" + domain + "'");
		}
		return getDatapackage(buffer.toString(), page, lines);
	}


	public DataPackage<FieldExtendsVO> queryUserFieldExtends(String domain, int page, int lines)
			throws Exception {
		String hql = "FROM " + this._voClazzName + " vo";
		hql = hql + " WHERE vo.domainid = '" + domain + "'";
		return getDatapackage(hql, page, lines);
	}
	
	/**
	 * @SuppressWarnings hibernate3.2不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public FieldExtendsVO qeuryFieldByLabelAndDomain(String label, String domain, String forTable)
			throws Exception {
		String hql = "FROM " + this._voClazzName + " vo  WHERE vo.domainid = '" + domain + "' AND vo.label = '" + label + "' AND vo.forTable = '" + forTable + "'";
		Session session = currentSession();
		Query query = session.createQuery(hql);
		List list = query.list();
		if (list == null || list.isEmpty())
			return null;
		return (FieldExtendsVO) list.get(0);
	}
}
