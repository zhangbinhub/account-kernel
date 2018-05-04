package OLink.bpm.core.networkdisk.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface NetDiskFileProcess  extends IDesignTimeProcess<NetDiskFile> {

	/**
	 * 获得总数
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	int getTotalLines(String hql) throws Exception;
}
