package OLink.bpm.core.macro.repository.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.macro.repository.ejb.RepositoryProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;

public class RepositoryActionHelper extends BaseHelper<RepositoryVO> {

	Collection<String> Repotype = new ArrayList<String>();

	int i = 100;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public RepositoryActionHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(RepositoryProcess.class));

		Repotype.add("{*[File]*}");
		Repotype.add("{*[Date]*}");
		Repotype.add("{*[String]*}");
		Repotype.add("{*[Number]*}");
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public Collection<String> getRepotype() {
		return Repotype;
	}

	public void setRepotype(List<String> repotype) {
		Repotype = repotype;
	}

}
