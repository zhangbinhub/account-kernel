package OLink.bpm.core.expimp.imp.ejb;

import java.io.File;

import com.opensymphony.xwork.ValidationAware;

public interface ImpProcess {
	ValidationAware doImportValidate(ImpSelect select, File importFile)
			throws Exception;

	void doImport(ImpSelect select, File importFile) throws Exception;

}
