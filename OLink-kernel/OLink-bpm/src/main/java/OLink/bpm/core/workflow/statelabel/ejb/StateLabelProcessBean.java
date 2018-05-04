package OLink.bpm.core.workflow.statelabel.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.workflow.statelabel.dao.StateLabelDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class StateLabelProcessBean extends
		AbstractDesignTimeProcessBean<StateLabel> implements StateLabelProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4652165914002594393L;

	//@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<StateLabel> getDAO() throws Exception {
		return (StateLabelDAO) DAOFactory.getDefaultDAO(StateLabel.class.getName());
	}

	public Collection<StateLabel> doQueryName(String application)
			throws Exception {
		return ((StateLabelDAO) getDAO()).queryName(application);
	}

	public Collection<StateLabel> doQueryByName(String name, String application)
			throws Exception {
		return ((StateLabelDAO) getDAO()).queryByName(name, application);
	}

	public Collection<StateLabel> doQueryState(String application)
			throws Exception {
		return ((StateLabelDAO) getDAO()).queryStates(application);
	}
}
