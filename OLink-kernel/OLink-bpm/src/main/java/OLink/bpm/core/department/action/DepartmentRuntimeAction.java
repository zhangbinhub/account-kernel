package OLink.bpm.core.department.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.util.json.JsonUtil;
import OLink.bpm.core.tree.Node;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @see BaseAction DepartmentAction class.
 * @author Darvense
 * @since JDK1.4
 */
public class DepartmentRuntimeAction extends DepartmentAction {
	private static final Logger LOG = Logger.getLogger(DepartmentRuntimeAction.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -3096692954428344339L;

	public DepartmentRuntimeAction() throws Exception {
		super();
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	/**
	 * 显示部门树
	 * 
	 * @throws Exception
	 */
	public void departTree() {

		ParamsTable params = getParams();
		String parentid = params.getParameterAsString("parentid");
		String domain = params.getParameterAsString("domain");
		try {
			if (parentid == null || "".equals(parentid)) {
				Collection<DepartmentVO> depts = ((DepartmentProcess) process).getDepartmentByLevel(0,
						getApplication(), domain);
				for (Iterator<DepartmentVO> ite = depts.iterator(); ite.hasNext();) {
					DepartmentVO dept = ite.next();
					Node node = new Node();
					node.setId(dept.getId());
					node.setData(dept.getName());
					node.addAttr("name", dept.getName());
					if (((DepartmentProcess) process).getChildrenCount(dept.getId()) > 0) {
						node.setState(Node.STATE_CLOSED);
					}
					childNodes.add(node);
				}
				ResponseUtil
						.setJsonToResponse(ServletActionContext.getResponse(), JsonUtil.collection2Json(childNodes));
			} else {
				Collection<DepartmentVO> depts = ((DepartmentProcess) process).getDatasByParent(parentid);
				for (Iterator<DepartmentVO> ite = depts.iterator(); ite.hasNext();) {
					DepartmentVO dept = ite.next();
					Node node = new Node();
					node.setId(dept.getId());
					node.setData(dept.getName());
					node.addAttr("name", dept.getName());
					if (((DepartmentProcess) process).getChildrenCount(dept.getId()) > 0) {
						node.setState(Node.STATE_CLOSED);
					}
					childNodes.add(node);
				}
				ResponseUtil
						.setJsonToResponse(ServletActionContext.getResponse(), JsonUtil.collection2Json(childNodes));
			}
		} catch (Exception e) {
			LOG.error("departTree", e);
		}
	}
}
