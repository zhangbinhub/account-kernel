package OLink.bpm.core.expimp.exp.ejb;

import java.io.File;
import java.io.Serializable;

public interface ExpProcess extends Serializable {
	File createZipFile(ExpSelect select) throws Exception;

	File getExportFile(String fileName) throws Exception;

	/**
	 * 导出应用所有子元素的XML文件,包含（角色,菜单资源,角色菜单权限,函数库,校验库,Excel导入配置,提醒,模块,表单,视图）
	 * 
	 * @param select
	 *            选择的应用、模块或元素等
	 * @param fileName
	 *            文件保存路径
	 * @return
	 * @throws Exception
	 */
	File createZipFile(ExpSelect select, String fileName) throws Exception;
}
