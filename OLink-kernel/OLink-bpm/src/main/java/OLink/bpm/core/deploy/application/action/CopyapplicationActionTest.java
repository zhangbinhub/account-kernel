package OLink.bpm.core.deploy.application.action;

import OLink.bpm.core.deploy.application.ejb.CopyApplicationProcessBean;
import junit.framework.TestCase;

public class CopyapplicationActionTest extends TestCase {
	CopyApplicationProcessBean copyApplication;

	protected void setUp() throws Exception {
		copyApplication = new CopyApplicationProcessBean(
				"11de-864f-88dc4a0a-942f-136ef581a559");
	}

	public void test() throws Exception {

		// copyApplication.copyDataSource("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyMenu("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyComponent("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyPage("11de-5eed-13772906-b72b-0789a1c7b2ba");
		//
		// copyApplication
		// .copyValidatelibs("11de-5eed-13772906-b72b-0789a1c7b2ba");

		copyApplication.copyRole("11de-861e-b649bcab-8aa1-45da9efe66b0");

		// copyApplication.copyReminder("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// //
		// //
		// copyApplication.copyHomepage("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyStatelabel("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyStylelibs("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyMacrolibs("11de-5eed-13772906-b72b-0789a1c7b2ba");
		// copyApplication.copyExcelConf("11de-5eed-13772906-b72b-0789a1c7b2ba");

		// copyApplication.copyModule("11de-5eed-13772906-b72b-0789a1c7b2ba");
	}

	protected void tearDown() throws Exception {
	}
}
