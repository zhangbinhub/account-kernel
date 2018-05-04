package OLink.bpm.core.multilanguage.action;

import java.util.Map;
import java.util.TreeMap;

import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.core.multilanguage.ejb.LanguageType;

public class MultiLanguageHelper {
	public Map<Integer, String> getTypeList() {
		Map<Integer, String> rtn = new TreeMap<Integer, String>();
		
		int[] types = LanguageType.TYPES;
		String[] names = LanguageType.NAMES;
 		
		for (int i = 0; i < types.length; i++) {
			rtn.put(Integer.valueOf(types[i]) , names[i]);
		}
		return rtn;
	}

	public String getTypeName(int type) {
		return LanguageType.getName(type);
	}
	
	public int getType(String name){
		return MultiLanguageProperty.getType(name);
	}
}
