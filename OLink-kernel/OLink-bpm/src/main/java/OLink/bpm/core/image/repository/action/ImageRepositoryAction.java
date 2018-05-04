package OLink.bpm.core.image.repository.action;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryProcess;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

public class ImageRepositoryAction extends BaseAction<ImageRepositoryVO> {
	/**
	 * @author yecp
	 */
	String _applicationid;

	private String _moduleid;

	private static final long serialVersionUID = 1L;

	/**
	 * @throws Exception
	 */

	private String path;

	public String getPath() throws Exception {
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest request = (HttpServletRequest) ctx
				.get(ServletActionContext.HTTP_REQUEST);
		this.path = request.getSession().getServletContext().getRealPath(DefaultProperty
				.getProperty("IMAGE_PATH"));
		this.path += "\\";
		return path;
	}

	public void setPath(String path) throws Exception {
		this.path = path;
	}

	/*
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public ImageRepositoryAction() throws Exception {
		super(ProcessFactory.createProcess(ImageRepositoryProcess.class),
				new ImageRepositoryVO());
	}

	public String get_moduleid() {
		ImageRepositoryVO sv = (ImageRepositoryVO) getContent();
		if (sv.getModule() != null)
			return sv.getModule().getId();
		else
			return _moduleid;
	}

	public void set_moduleid(String _moduleid) {
		this._moduleid = _moduleid;
	}

	public String doSave() {
		try {
			ImageRepositoryVO vo = (ImageRepositoryVO) getContent();
			if (get_moduleid() != null && get_moduleid().trim().length() > 0
					&& !get_moduleid().equals("none")) {
				ModuleProcess mp = (ModuleProcess) ProcessFactory
						.createProcess(ModuleProcess.class);
				ModuleVO mv = (ModuleVO) mp.doView(this.get_moduleid());

				vo.setModule(mv);
			}
			setContent(vo);
			return super.doSave();

		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public ImageRepositoryVO doListByImageid(String imageid) throws Exception {
		ImageRepositoryVO datas = (ImageRepositoryVO) (process
				.doView(imageid));
		return datas;
	}

}
