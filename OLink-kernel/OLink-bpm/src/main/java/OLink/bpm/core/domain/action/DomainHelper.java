package OLink.bpm.core.domain.action;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.user.ejb.BaseUser;

/**
 * @see DomainHelper class
 * @author Chris
 * @since JDK1.4
 */
public class DomainHelper {

	public BaseUser user = null;

	public int _page;

	public int _line;

	public static DomainVO getDomainVO(BaseUser user) throws Exception {
		if (user == null)
			return null;
		String domainId = user.getDomainid();
		if (domainId == null)
			return null;
		DomainProcess dp = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		return (DomainVO) dp.doView(domainId);
	}

	public Collection<DomainVO> queryDomains() throws Exception {
		DomainProcess dp = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		return dp.queryDomains(user.getId(), 1, 5);
	}

	public BaseUser getUser() {
		return user;
	}

	public void setUser(BaseUser user) {
		this.user = user;
	}

	public int get_page() {
		return _page;
	}

	public void set_page(int _page) {
		this._page = _page;
	}

	public int get_line() {
		return _line;
	}

	public void set_line(int _line) {
		this._line = _line;
	}

	public static DomainVO getDomainVO(String domainid) throws Exception {
		DomainProcess dp = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		return (DomainVO) dp.doView(domainid);
	}

	/**
	 * 返回当前所有的皮肤类型
	 * 
	 * @return 皮肤类型的哈希图
	 * @throws Exception
	 */
	public Map<String, String> querySkinTypes() throws Exception {
		Map<String, String> skins = new HashMap<String, String>();

		Environment ev = Environment.getInstance();
		String contentPath = "";
		if (ev != null) {
			contentPath = ev.getRealPath("") + "portal";
			File dir = new File(contentPath);
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {// 判断是否是目录文件
						if (!files[i].getName().equals("share") && !files[i].getName().equals("dispatch")
								&& files[i].getName().indexOf(".") == -1)
							skins.put(files[i].getName(), files[i].getName());
					} else {
						continue;
					}
				}
			} else {
				skins.put("default", "default");
			}
		}
		return skins;
	}
}