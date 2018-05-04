package OLink.bpm.core.privilege.res.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.tree.Node;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ResProcess extends IDesignTimeProcess<ResVO> {

	/**
	 * 获得显示资源树形结构
	 * 
	 * @param params
	 * @param rprocess
	 * @return
	 * @throws Exception
	 */
	Collection<Node> getResTree(ParamsTable params) throws Exception;

	ValueObject doViewByName(String name, String application) throws Exception;

}
