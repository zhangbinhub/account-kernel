package OLink.bpm.core.dynaform.dts.export2;

import java.util.Iterator;

import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.user.action.WebUser;
import org.apache.log4j.Logger;

public class ExportAll extends ExportBase {

	public ExportAll(MappingConfig mappingconfig, String application, WebUser user) {
		super(mappingconfig, application, user);
	}

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(ExportBase.class);

	protected long getTotalLine(MappingConfig mfg, String dql, DocumentProcess process) throws Exception {
		return process.getNeedExportDocumentTotal(dql, null, domainid);
	}

	protected Iterator<Document> getAppointedRows(MappingConfig mfg, int page, int lines, String dql, DocumentProcess process)
			throws Exception {
		return process.iteratorLimitByDQL(dql, page, lines, domainid);
	}

}
