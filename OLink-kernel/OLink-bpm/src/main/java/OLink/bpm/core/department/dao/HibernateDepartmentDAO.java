package OLink.bpm.core.department.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.department.ejb.DepartmentVO;

public class HibernateDepartmentDAO extends HibernateBaseDAO<DepartmentVO> implements DepartmentDAO {

	public HibernateDepartmentDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<DepartmentVO> getDatasByParent(String parent) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.superior = '" + parent + "'";
		Collection<DepartmentVO> rtn = getDatas(hql, null);

		return rtn;
	}

//	public String getIdByName(String deptname, String application, String domain) throws Exception {
//		String hql = "SELECT vo.id FROM " + _voClazzName + " vo WHERE vo.name = '" + deptname
//				+ "' and  vo.domain.id = '" + domain + "'";
//
//		Query query = currentSession().createQuery(hql);
//		if (!query.list().isEmpty()) {
//			return (String) query.list().get(0);
//		} else
//			return null;
//	}

	public Collection<DepartmentVO> getAllDepartment(String application, String domain) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.domain.id = '" + domain + "'";
		return getDatas(hql);
	}

	public Collection<DepartmentVO> getDepartmentByLevel(int level, String application, String domain) throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.level=" + level + " and vo.domain.id = '" + domain + "'";
		return getDatas(hql);
	}

	public Collection<DepartmentVO> getDepartmentByName(String byName, String domain) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.name= '" + byName + "' and  vo.domain.id = '" + domain
				+ "'";
		return getDatas(hql);
	}

	public Collection<DepartmentVO> getDepartmentByCode(String byCode, String domain) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.code= '" + byCode + "' and  vo.domain.id = '" + domain
				+ "'";
		return getDatas(hql);
	}

	public DepartmentVO getRootDepartmentByApplication(String application, String domain) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.superior is null and  vo.domain.id = '" + domain + "'";
		return (DepartmentVO) getData(hql);
	}

	public long getChildrenCount(String parent) throws Exception {
		String hql = "SELECT COUNT(*) FROM " + this._voClazzName + " vo WHERE vo.superior = '" + parent + "'";
		return ((Long) getData(hql)).longValue();
	}

	public Collection<DepartmentVO> queryByDomain(String domain, int page, int pagelines) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.domain.id='" + domain + "'";
		return getDatas(hql, page, pagelines);
	}

	public DepartmentVO findByName(String name, String domain) throws Exception {
		String hql = "FROM " + this._voClazzName + " vo where vo.name= '" + name + "' and  vo.domain.id = '" + domain
				+ "'";
		return (DepartmentVO) getData(hql);
	}

	public DepartmentVO findByIndexCode(String indexCode) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.indexCode = '" + indexCode + "'";
		return (DepartmentVO) getData(hql);
	}

	public DepartmentVO findLineSuperiorByIndexCode(String indexCode)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.indexCode = '" + indexCode.substring(0,indexCode.lastIndexOf("_")) + "'";
		return (DepartmentVO) getData(hql);
	}

	public Collection<DepartmentVO> queryAllSubordinatesByIndexCode(
			String indexCode) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.indexCode like '" + indexCode+"_%" + "'";
		return getDatas(hql);
	}

	public Collection<DepartmentVO> queryAllSuperiorsByIndexCode(
			String indexCode) throws Exception {
		String[] s = indexCode.split("_");
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
		
		String hql = "FROM " + _voClazzName + " vo WHERE vo.indexCode in (" + part+")";
		return getDatas(hql);
	}

	public Collection<DepartmentVO> queryLineSubordinatesByIndexCode(
			String indexCode) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.indexCode like '" +indexCode+ "_____________________________________'";
		return getDatas(hql);
	}

}
