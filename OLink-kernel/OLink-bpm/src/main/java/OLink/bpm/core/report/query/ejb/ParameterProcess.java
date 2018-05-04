package OLink.bpm.core.report.query.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ParameterProcess extends IDesignTimeProcess<Parameter> {
	Collection<Parameter> getParamtersByQuery(String queryid, String application)
			throws Exception;
}
