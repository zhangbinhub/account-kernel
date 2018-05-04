package OLink.bpm.core.helper.search;

import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;

public class IndexBuilder implements Runnable {
	public IndexBuilder() {
	}

	public void run() {
		// Web应用的真实路径
		String realPath = Environment.getInstance().getApplicationRealPath();

		String rootDir = realPath + Web.TOC_DIR;
		String indexDir = realPath + Web.INDEX_DIR;

		try {
			new IndexHTML().createIndexForFiles(indexDir, rootDir, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start() {
		Thread thread = new Thread(new IndexBuilder());
		thread.start();
	}
}
