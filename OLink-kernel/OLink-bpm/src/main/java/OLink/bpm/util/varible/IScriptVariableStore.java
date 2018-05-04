package OLink.bpm.util.varible;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;

public class IScriptVariableStore implements VariableStore {
	private Map<String, String> variableMap = new HashMap<String, String>();

	public String getVariableValue(String variableName) {
		return variableMap.get(variableName);
	}
	
	public void parseVariableNames(Collection<String> varNames, Document doc, ParamsTable params) {
		for (Iterator<String> iterator = varNames.iterator(); iterator.hasNext();) {
			String varName = iterator.next();
			parseVariableNames(varName, doc, params);
		}
	}
	
	public void parseVariableNames(String varName, Document doc, ParamsTable params) {
		if (varName.startsWith("doc.")) {
			String itemName = varName.substring("doc.".length());
			try {
				variableMap.put(varName, doc.getItemValueAsString(itemName));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (varName.startsWith("params.")) {
			String paramName = varName.substring("params.".length());
			variableMap.put(varName, params.getParameterAsString(paramName));
		}
	}
}
