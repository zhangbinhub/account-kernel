package OLink.bpm.core.dynaform.view.ejb.type;

import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewType;

public class NormalType extends AbstractType implements ViewType {
	public NormalType(View view) {
		super(view);
	}

	public int intValue() {
		return View.VIEW_TYPE_NORMAL;
	}
}
