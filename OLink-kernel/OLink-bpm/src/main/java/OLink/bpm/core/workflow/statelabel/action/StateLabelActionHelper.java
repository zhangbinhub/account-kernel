package OLink.bpm.core.workflow.statelabel.action;

import java.util.Collection;

import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabelProcess;
import OLink.bpm.util.ProcessFactory;

public class StateLabelActionHelper {
	public Collection<StateLabel> getStateList(String application)
			throws Exception {
		StateLabelProcess cp = (StateLabelProcess) ProcessFactory
				.createProcess((StateLabelProcess.class));
		Collection<StateLabel> rtn = cp.doQueryState(application);
		return rtn;
	}
}
