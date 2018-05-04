package OLink.bpm.mobile.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.http.HttpServletRequest;

import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;

public class MbUploadActionHelper {

	public static void saveFile(HttpServletRequest request, String path) throws IOException {
		String path1 = request.getSession().getServletContext().getRealPath("/");
		File file = null;
		if (path.lastIndexOf("/") > 0) {
			int index = path.lastIndexOf("/");
			String pathName = path.substring(0, index);
			file = new File(path1 + pathName);
			if (!file.exists()) {
				if (!file.mkdirs()) {
					throw new IOException("系统无法创建文件夹");
				}
			}
			file = new File(path1 + path);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new IOException("系统无法创建文件");
				}
			}
		}
		if (file != null) {
			InputStream in = request.getInputStream();
			FileImageOutputStream fios = new FileImageOutputStream(file);
			byte[] tmp = new byte[4096];
			int len = 0;
			int size = 0;
			while ((len = in.read(tmp)) != -1) {
				fios.write(tmp, 0, len);
				fios.flush();
				size += len;
			}
			fios.close();		
		}
		
	}
	
	public static String getFilePath() throws Exception {
		String path0 = DefaultProperty.getProperty("ITEM_PATH");
		if (!StringUtil.isBlank(path0)) {
			if (path0.startsWith("/"))
				path0 = path0.substring(1, path0.length());
		} else {
			path0 = "uploads/mobile/";
		}
		path0 += Tools.getSequence() + ".png";
		return path0;
	}
	
}
