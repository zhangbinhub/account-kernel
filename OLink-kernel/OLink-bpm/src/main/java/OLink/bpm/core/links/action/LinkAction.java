package OLink.bpm.core.links.action;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.core.links.ejb.LinkProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.util.ProcessFactory;

/**
 * @author Happy
 *
 */
public class LinkAction extends BaseAction<LinkVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6246619093006382857L;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public LinkAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(LinkProcess.class), new LinkVO());
	}
	
	
	/** 保存并新建 */
	public String doSaveAndNew() {
		try{
			if(!doValidateLink())return INPUT;
			if (getContent().getId() == null || getContent().getId().equals(""))
				process.doCreate(getContent());
			else
				process.doUpdate(getContent());
				this.addActionMessage("{*[Save_Success]*}");
				LinkVO link = new LinkVO();
				link.setApplicationid(application);
				setContent(new LinkVO());
				return SUCCESS;
			
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	//保存
	public String doSave(){
		if(!doValidateLink())return INPUT;
		return super.doSave();
	}
	
	//校验link不能重名
	private boolean doValidateLink(){
		boolean isOK = true;
		LinkVO lVO = (LinkVO)getContent();
		if(lVO != null){
			String name = lVO.getName();
			try {
				LinkVO l = (LinkVO) DAOFactory.getDefaultDAO(LinkVO.class.getName()).findByName(name, lVO.getApplicationid());
				if(l != null && !l.getId().equals(lVO.getId())){
					isOK =  false;
					this.addFieldError("existName", "{*[Exist.same.name]*}(" + name + ")");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isOK;
	}

}
