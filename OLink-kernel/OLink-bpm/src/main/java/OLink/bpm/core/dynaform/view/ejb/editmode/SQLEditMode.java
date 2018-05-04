package OLink.bpm.core.dynaform.view.ejb.editmode;

import java.util.ArrayList;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.EditMode;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * 
 * @author nicholas zhen
 * 
 */
public class SQLEditMode extends AbstractEditMode implements EditMode {

	public SQLEditMode(View view) {
		super(view);
	}

	public String getQueryString(ParamsTable params, WebUser user, Document sDoc) throws Exception {
		StringBuffer label = new StringBuffer();
		label.append("VIEW(").append(view.getId()).append(")." + view.getName()).append(".SqlFilterScript");

		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		runner.initBSFManager(sDoc, params, user, new ArrayList<ValidateMessage>());

		return runScript(runner, label.toString(), view.getSqlFilterScript());
	}

}
