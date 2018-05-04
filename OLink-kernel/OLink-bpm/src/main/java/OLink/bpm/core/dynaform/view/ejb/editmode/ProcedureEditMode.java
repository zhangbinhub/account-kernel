package OLink.bpm.core.dynaform.view.ejb.editmode;

import java.util.ArrayList;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;

public class ProcedureEditMode extends AbstractEditMode implements EditMode {

	public ProcedureEditMode(View view) {
		super(view);
	}

	public DataPackage<Document> getDataPackage(ParamsTable params, WebUser user, Document sDoc) throws Exception {
		return getDataPackage(params, 1, Integer.MAX_VALUE, user, sDoc);
	}

	public DataPackage<Document> getDataPackage(ParamsTable params, int page, int lines, WebUser user, Document sDoc)
			throws Exception {
		DocumentProcess dp = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class, view
				.getApplicationid());
		String procedure = appendCondition(getQueryString(params, user, sDoc));

		return dp.queryByProcedure(procedure, params, page, lines, user.getDomainid());
	}

	public String getQueryString(ParamsTable params, WebUser user, Document sDoc) throws Exception {
		StringBuffer label = new StringBuffer();
		label.append("VIEW(").append(view.getId()).append(")." + view.getName()).append(".ProcedureFilterScript");

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		runner.initBSFManager(sDoc, params, user, new ArrayList<ValidateMessage>());

		return runScript(runner, label.toString(), view.getProcedureFilterScript());
	}

}
