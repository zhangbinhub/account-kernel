package OLink.bpm.core.superuser.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.superuser.ejb.SuperUserVO;

public interface SuperUserDAO extends IDesignTimeDAO<SuperUserVO> {

	SuperUserVO login(String loginno) throws Exception;

	boolean isEmpty() throws Exception;

	Collection<SuperUserVO> getDatasByDomain(String domain) throws Exception;

	Collection<SuperUserVO> getDatasByType(int userType) throws Exception;

	Collection<SuperUserVO> queryHasMail() throws Exception;

	/**
	 * 根据loginno获取管理员
	 * 
	 * @return 用户的Email集合
	 * 
	 */
	SuperUserVO findByLoginno(String loginno) throws Exception;
}
