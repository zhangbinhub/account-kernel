package OLink.bpm.core.email.runtime.mail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Folder;

import OLink.bpm.util.StringUtil;

/**
 * 
 * @author Tom
 *
 */
public final class FolderCache {

	private static Map<String, Map<String, Folder>> imapFolders = Collections.synchronizedMap(new HashMap<String, Map<String, Folder>>());
	
	private FolderCache() {}
	
	public static void putUserFolderMap(AuthProfile auth, Map<String, Folder> userFolderMap) {
		imapFolders.put(auth.getUserName(), userFolderMap);
	}
	
	public static Map<String, Folder> getUserFolderMap(AuthProfile auth) {
		Map<String, Folder> result = null;
		if (auth != null && !StringUtil.isBlank(auth.getUserName())) {
			result = imapFolders.get(auth.getUserName());
			if (result == null) {
				result = new HashMap<String, Folder>();
				putUserFolderMap(auth, result);
			}
		}
		return result;
	}
	
	public static void removeUserFolderMap(AuthProfile auth) {
		if (auth != null && !StringUtil.isBlank(auth.getUserName())) {
			imapFolders.remove(auth.getUserName());
		}
	}
	
	public static void clearCache() {
		imapFolders.clear();
	}
	
}
