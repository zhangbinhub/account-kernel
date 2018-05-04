package OLink.bpm.core.task.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.task.ejb.Task;

public interface TaskDAO extends IDesignTimeDAO<Task> {

	Collection<Task> query(String application) throws Exception;

	Collection<Task> getTaskByModule(String application, String module)
			throws Exception;
}
