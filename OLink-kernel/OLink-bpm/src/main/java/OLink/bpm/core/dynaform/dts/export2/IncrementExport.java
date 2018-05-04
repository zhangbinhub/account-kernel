package OLink.bpm.core.dynaform.dts.export2;

import java.util.Iterator;

import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.dynaform.document.ejb.Document;
import org.apache.log4j.Logger;

import OLink.bpm.core.user.action.WebUser;

public class IncrementExport extends ExportBase {

	public IncrementExport(MappingConfig mappingconfig, String application, WebUser user) {
		super(mappingconfig, application, user);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(ExportBase.class);

	protected long getTotalLine(MappingConfig mfg, String dql, DocumentProcess process) throws Exception {

		long totalLine = 0;
		if (mappingconfig.getLastRun() == null)
			totalLine = process.getNeedExportDocumentTotal(dql, null, domainid);
		else
			totalLine = process.getNeedExportDocumentTotal(dql, mappingconfig.getLastRun(), domainid);
		return totalLine;
	}

	protected Iterator<Document> getAppointedRows(MappingConfig mfg, int page, int lines, String dql, DocumentProcess process)
			throws Exception {
		return process.queryByDQLAndDocumentLastModifyDate(dql, mappingconfig.getLastRun(), page, lines, domainid);
	}

}
