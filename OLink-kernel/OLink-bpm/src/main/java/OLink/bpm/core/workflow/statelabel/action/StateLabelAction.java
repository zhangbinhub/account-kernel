package OLink.bpm.core.workflow.statelabel.action;

import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabelProcess;
import OLink.bpm.util.ProcessFactory;

import com.opensymphony.webwork.ServletActionContext;

/**
 * @see BaseAction CommonInfoAction class.
 * @author Darvense
 * @since JDK1.4
 */
public class StateLabelAction extends BaseAction<StateLabel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6445416716227432447L;

	/**
	 * 
	 * CommonInfoAction structure function.
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public StateLabelAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(StateLabelProcess.class),
				new StateLabel());
	}

	public String doSelectState() throws Exception {
		ParamsTable params = getParams();
		String application = params.getParameterAsString("application");

		// 清除页面缓存操作
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);

		((StateLabelProcess) process).doQueryState(application);
		return SUCCESS;
	}

	@Override
	public String doSave() {
		StateLabel tempStateLabel = (StateLabel) this.getContent();
		boolean flag = false;
		StateLabel stateLabel = null;
		Iterator<StateLabel> it;
		this.getParams().setParameter("s_name",
				this.getParams().getParameterAsString("content.name"));
		this.getParams().setParameter("s_value",
				this.getParams().getParameterAsString("content.value"));

		try {
			it = (process.doQuery(this.getParams())).datas
					.iterator();
			while (it.hasNext()) {
				stateLabel = it.next();
				if (null != stateLabel) {
					break;
				}
			}

			if (stateLabel != null) {
				if (tempStateLabel.getId() == null
						|| tempStateLabel.getId().trim().length() <= 0) {// 判断新建不能重名
					this.addFieldError("1", "{*[LabelExist]*}");
					flag = true;
				} else if (tempStateLabel.getValue().trim().equalsIgnoreCase(
						stateLabel.getValue())
						&& !tempStateLabel.getId().trim().equalsIgnoreCase(
								stateLabel.getId())) {// 修改不能重值
					this.addFieldError("1", "{*[LabelExist]*}");
					flag = true;
				}
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

		if (!flag) {
			return super.doSave();
		} else
			return INPUT;

	}

	/** 保存并新建 */
	public String doSaveAndNew() {
		StateLabel tempStateLabel = (StateLabel) this.getContent();
		boolean flag = false;
		StateLabel stateLabel = null;
		Iterator<StateLabel> it;
		this.getParams().setParameter("s_name",
				this.getParams().getParameterAsString("content.name"));
		this.getParams().setParameter("s_value",
				this.getParams().getParameterAsString("content.value"));

		try {
			it = (process.doQuery(this.getParams())).datas
					.iterator();
			while (it.hasNext()) {
				stateLabel = it.next();
				if (null != stateLabel) {
					break;
				}
			}

			if (stateLabel != null) {
				if (tempStateLabel.getId() == null
						|| tempStateLabel.getId().trim().length() <= 0) {// 判断新建不能重名
					this.addFieldError("1", "{*[LabelExist]*}");
					flag = true;
				} else if (tempStateLabel.getValue().trim().equalsIgnoreCase(
						stateLabel.getValue())
						&& !tempStateLabel.getId().trim().equalsIgnoreCase(
								stateLabel.getId())) {// 修改不能重值
					this.addFieldError("1", "{*[LabelExist]*}");
					flag = true;
				}
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			flag = true;
		}

		if (!flag) {
			try {
				super.doSave();
				setContent(new StateLabel());
				return SUCCESS;
			} catch (Exception e) {
				this.addFieldError("1", e.getMessage());
				return INPUT;
			}
		} else {
			return INPUT;
		}
	}

}
