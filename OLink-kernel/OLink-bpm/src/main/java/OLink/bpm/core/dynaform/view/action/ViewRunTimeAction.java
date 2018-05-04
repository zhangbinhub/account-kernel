package OLink.bpm.core.dynaform.view.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewType;
import OLink.bpm.core.tree.Tree;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.tree.DocumentTree;
import OLink.bpm.core.tree.Node;
import OLink.bpm.core.user.action.WebUser;
import com.opensymphony.xwork.Action;
import OLink.bpm.util.cache.MemoryCacheUtil;

import com.opensymphony.webwork.ServletActionContext;

public class ViewRunTimeAction extends ViewAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 970248216937958653L;

	public ViewRunTimeAction() throws ClassNotFoundException {
		super();
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	public String doInnerPage() {
		try {
			DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class,view.getApplicationid());
			String docid = getParams().getParameterAsString("_docid");
			if (!StringUtil.isBlank(docid) && !Web.TREEVIEW_ROOT_NODEID.equals(docid)) {
				// 父节点文档
				Document doc = (Document) process.doView(docid);
				if (doc != null) {
					setCurrentDocument(doc);
					MemoryCacheUtil.putToPrivateSpace(doc.getId(), doc, getUser());
				}
			}

			setContent(view);
			
			if ( !"root".equals(docid)){
				String innerType=(String) this.getParams().getParameter("innerType");
				if(innerType!=null && innerType.equals("FORM")){
					return "successForm";
				}else if(innerType!=null && innerType.equals("VIEW")){
					return "successView";
				}else{
					if (View.TREENODE_HREF_FORM.equals(view.getInnerType()) && !"root".equals(docid)) {
						return "successForm";
					} else if (View.TREENODE_HREF_VIEW.equals(view.getInnerType())) {
						return "successView";
					} else {
						return Action.SUCCESS;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
		}
		
		return "successView";
	}

	public String doSearch() {
		try {
			WebUser user = getUser();
			Document sDoc = getSearchDocument(view);

			DocumentTree tree = new DocumentTree(view, getParams(), user, sDoc);
			tree.search();

			ResponseUtil.setJsonToResponse(ServletActionContext.getResponse(), tree.toSearchJSON());
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return Action.INPUT;
		}

		return Action.SUCCESS;
	}

	public String getChildren() {
		try {
			WebUser user = getUser();
			Document sDoc = getSearchDocument(view);

			// 添加关联条件
			ViewType viewType = view.getViewTypeImpl();
			Tree<Document> tree = new DocumentTree(view, getParams(), user, sDoc);
			// 解析并获取子文档
			Collection<Document> children = viewType.getViewDatas(getParams(), user, sDoc).getDatas();
			tree.parse(children);

			Collection<Node> childNodes = tree.getChildNodes();
			for (Iterator<Node> iterator = childNodes.iterator(); iterator.hasNext();) {
				Node node = iterator.next();
				node.addAttr("viewid", get_viewid()); // 当前视图ID
				node.addAttr("curr_node", node.getAttr().get("nodeValue"));
				node.addAttr("super_node_fieldName", viewType.getColumnMapping().get("superior_Node").getFieldName());
			}

			ResponseUtil.setJsonToResponse(ServletActionContext.getResponse(), tree.toJSON());
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getMessage()!=null){
				addFieldError("", e.getMessage());
			}else{
				addFieldError("errorMessage", e.toString());
			}
			return Action.INPUT;
		}

		return Action.NONE;
	}
}
