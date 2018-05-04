package OLink.bpm.core.workflow.statelabel.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface StateLabelProcess extends IDesignTimeProcess<StateLabel> {
	/**
	 * 根据应用获取流程的状态名称
	 * 
	 * @param application
	 *            应用标识
	 * @return 流程的状态(Statelable)对象
	 * @throws Exception
	 */
	Collection<StateLabel> doQueryName(String application)
			throws Exception;

	/**
	 * 根据流程的状态的名称查询当下应用下的流程的状态的集合
	 * 
	 * @param name
	 *            流程的状态的名称
	 * @param application
	 *            应用 标识
	 * @return 流程的状态(Statelable)的集合
	 * @throws Exception
	 */
	Collection<StateLabel> doQueryByName(String name, String application)
			throws Exception;

	/**
	 * 根据应用获取流程的状态的值
	 * 
	 * @param application
	 *            应用 标识
	 * @return 流程的状态(Statelable)的集合
	 * @throws Exception
	 */
	Collection<StateLabel> doQueryState(String application)
			throws Exception;

}
