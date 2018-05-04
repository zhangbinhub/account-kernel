package OLink.bpm.core.workflow.engine;

import java.util.Collection;

import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;

public interface State {
	NodeRT process(ParamsTable params, NodeRT origNodeRT, FlowStateRT instance, WebUser user,
				   String flowOption) throws Exception;

	int toInt();

	Collection<String> getPrincipalIdList(ParamsTable params, String domainId, String applicationid, BaseUser auditor)
			throws Exception;
}
