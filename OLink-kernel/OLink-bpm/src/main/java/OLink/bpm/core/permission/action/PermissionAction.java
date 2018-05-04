package OLink.bpm.core.permission.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.permission.ejb.PermissionProcess;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.privilege.res.ejb.ResVO;
import OLink.bpm.util.json.JsonUtil;

/**
 * @SuppressWarnings 此类不能使用泛型
 * @author Administrator
 * 
 */
@SuppressWarnings("unchecked")
public class PermissionAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7968551832827739066L;

	private String[] _selectsResources;

	private List<Object> reslist = new ArrayList<Object>();

	private Collection<ResVO> innerResourceList = new ArrayList<ResVO>();
	
	private String permissionJSON;
	
	public String getPermissionJSON() {
		return permissionJSON;
	}

	public void setPermissionJSON(String permissionJSON) {
		this.permissionJSON = permissionJSON;
	}

	public String[] get_selectsResources() {
		return _selectsResources;
	}

	public void set_selectsResources(String[] selectsResources) {
		_selectsResources = selectsResources;
	}

	public PermissionAction() throws Exception {
		super(ProcessFactory.createProcess(PermissionProcess.class), new PermissionVO());
	}

	public String doEdit() {
		return SUCCESS;
	}

	public Collection<ResVO> getInnerResourceList() {
		return innerResourceList;
	}

	public void setInnerResourceList(Collection<ResVO> innerResourceList) {
		this.innerResourceList = innerResourceList;
	}

	/**
	 * 列表
	 */
	public String doList() {
		try {
			String roleid = getParams().getParameterAsString("roleid");
			setPermissionJSON(((PermissionProcess)process).getPermissionJSONByRole(roleid));
//			ResProcess rprocess = (ResProcess) ProcessFactory.createProcess(ResProcess.class);
//			this.setDatas(rprocess.doQuery(getParams(), getUser()));
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	/**
	 * 保存
	 */
	public String doSave() {
		try {
			if (!StringUtil.isBlank(getPermissionJSON())){
				Map<String, Object> permissionMap = JsonUtil.toMap(getPermissionJSON());
				((PermissionProcess) process).grantAuth(permissionMap, this.getParams());
			}
//			((PermissionProcess) process).grantAuth(_selectsResources, this.getParams());
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("doSave", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	/**
	 * 删除
	 */
	public String doDelete() {
		try {
			((PermissionProcess) process).removeAuth(_selectsResources, this.getParams());
		} catch (Exception e) {
			addFieldError("doDelete", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	public List<Object> getReslist() {
		return reslist;
	}

	public void setReslist(List<Object> reslist) {
		this.reslist = reslist;
	}

}
