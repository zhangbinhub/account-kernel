package OLink.bpm.core.domain.level.action;

import java.util.Collection;
import java.util.HashSet;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.domain.level.ejb.DomainLevelProcess;
import OLink.bpm.core.domain.level.ejb.DomainLevelVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;

/**
 * @see BaseAction DomainRateAction class.
 * @author Chris
 * @since JDK1.4
 */
public class DomainLevelAction extends BaseAction<DomainLevelVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6943941877298293428L;

	/**
	 * 
	 * DomainRateAction structure function.
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public DomainLevelAction() throws Exception {
		super(ProcessFactory.createProcess(DomainLevelProcess.class), new DomainLevelVO());
	}

	public String doNew() {
		setContent(new DomainLevelVO());
		return SUCCESS;
	}

	/**
	 * Delete a DomainRateVO.
	 * 
	 * @return If the action execution was successful.return "SUCCESS".Show an
	 *         success view .
	 *         <p>
	 *         If the action execution was a failure. return "ERROR".Show an
	 *         error view, possibly asking the user to retry entering data.
	 *         <p>
	 *         The "INPUT" is also used if the given input params are invalid,
	 *         meaning the user should try providing input again.
	 * 
	 * @throws Exception
	 */
	public String doDelete() {
		try {
			String errorField = "";
			if (_selects != null) {
				for (int i = 0; i < _selects.length; i++) {
					String id = _selects[i];
					try {
						process.doRemove(id);
					} catch (Exception e) {
						errorField = e.getMessage() + "," + errorField;
					}
				}
				if (!errorField.equals("")) {
					if (errorField.endsWith(",")) {
						errorField = errorField.substring(0, errorField.length() - 1);
					}
					this.addFieldError("1", errorField);
					return INPUT;
				}
				addActionMessage("{*[delete.successful]*}");
			}
			

			return SUCCESS;
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
			return INPUT;
		}

	}

	/**
	 * Save tempDomainRate.
	 * 
	 * @return If the action execution was successful.return "SUCCESS".Show an
	 *         success view .
	 *         <p>
	 *         If the action execution was a failure. return "ERROR".Show an
	 *         error view, possibly asking the user to retry entering data.
	 *         <p>
	 *         The "INPUT" is also used if the given input params are invalid,
	 *         meaning the user should try providing input again.
	 * 
	 * @throws Exception
	 */
	public String doSave() {

		try {
			DomainLevelVO tmpRate = (DomainLevelVO) (this.getContent());
			boolean flag = false;
			String tempname = tmpRate.getName();
			DomainLevelVO rate = ((DomainLevelProcess) process).getRateByName(tempname);

			if (rate != null) {
				if (tmpRate.getId() == null || tmpRate.getId().trim().length() <= 0) {
					this.addFieldError("1", "{*[core.domain.exist]*}");
					flag = true;
				} else if (!tmpRate.getId().trim().equalsIgnoreCase(rate.getId())) {
					this.addFieldError("1", "{*[core.domain.exist]*}");
					flag = true;
				}
			}

			if (!flag) {
				return super.doSave();
			} else {
				return INPUT;
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String doList() {
		try {
			ParamsTable params = getParams();
			WebUser user = getUser();
			setDatas(process.doQuery(params, user));

			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String doRemoveDomain() throws Exception {
		ParamsTable params = getParams();

		String[] ids = params.getParameterAsArray("_selects");
		try {

			String tid = params.getParameterAsString("s_level.id");
			DomainLevelProcess drprocess = (DomainLevelProcess) ProcessFactory.createProcess(DomainLevelProcess.class);
			DomainProcess dprocess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
			DomainLevelVO rate = (DomainLevelVO) drprocess.doView(tid);

			Collection<DomainVO> domains = new HashSet<DomainVO>();
			domains.addAll(rate.getDomains());
			for (int i = 0; i < ids.length; i++) {
				DomainVO vo = (DomainVO) dprocess.doView(ids[i]);
				if (domains.contains(vo)) {
					domains.remove(vo);
				}
			}
			rate.setDomains(domains);
			drprocess.doUpdate(rate);
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}
}
