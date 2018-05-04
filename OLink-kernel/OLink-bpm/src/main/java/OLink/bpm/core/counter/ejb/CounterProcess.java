package OLink.bpm.core.counter.ejb;

import OLink.bpm.base.ejb.IRunTimeProcess;

public interface CounterProcess extends IRunTimeProcess<CounterVO> {
	/**
	 * 根据参数移除自动生成的计数号 (Remove the sequence counter according the tag name.)
	 * 
	 * @param name
	 *            (The sequence tag name) field的名字
	 * @param application
	 *            应用标识
	 * @param domainid
	 *            域标识
	 * @throws Exception
	 */
	void doRemoveByName(String name, String application, String domainid) throws Exception;

	/**
	 * 根据参数查询出生成的计数号,并地当前的计数号上递增1(find the sequence according the tag name.)
	 * 
	 * @param name
	 *            (The sequence tag name) field的名字
	 * @param application
	 *            应用标识
	 * @param domainid
	 *            域标识
	 * @return The sequence counter.(计数号)
	 * @throws Exception
	 */
	int getNextValue(String name, String application, String domainid) throws Exception;

}
