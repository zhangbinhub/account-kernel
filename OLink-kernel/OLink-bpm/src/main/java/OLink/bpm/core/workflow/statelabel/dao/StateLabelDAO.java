package OLink.bpm.core.workflow.statelabel.dao;

import java.util.Collection;

import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;
import OLink.bpm.base.dao.IDesignTimeDAO;

public interface StateLabelDAO extends IDesignTimeDAO<StateLabel> {
	Collection<StateLabel> queryByName(String name, String application)
			throws Exception;

	Collection<StateLabel> queryName(String application)
			throws Exception;

	Collection<StateLabel> queryStates(String application)
			throws Exception;
}
