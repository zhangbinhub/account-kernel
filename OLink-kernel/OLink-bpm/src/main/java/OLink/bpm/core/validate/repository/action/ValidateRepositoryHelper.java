package OLink.bpm.core.validate.repository.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryProcess;
import OLink.bpm.core.validate.repository.ejb.ValidateRepositoryVO;
import OLink.bpm.util.ProcessFactory;

public class ValidateRepositoryHelper {

	/**
	 * 取得该模块及其下属模块,还有所属应用,还有不属于任何应用及模块的所有validate
	 * 
	 * @return validate集合
	 * @throws Exception
	 */
	public Map<String, String> get_validate(String applicationid) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		ValidateRepositoryProcess sp = (ValidateRepositoryProcess) ProcessFactory
				.createProcess(ValidateRepositoryProcess.class);
		Collection<ValidateRepositoryVO> coll = sp.get_validate(applicationid);

		for (Iterator<ValidateRepositoryVO> iter = coll.iterator(); iter.hasNext();) {
			ValidateRepositoryVO em = iter.next();
			map.put(em.getId(), em.getName());
		}
		return map;
	}

}
