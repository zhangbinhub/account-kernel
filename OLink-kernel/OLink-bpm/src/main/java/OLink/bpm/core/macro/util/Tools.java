/*
 * Created on 2005-2-10
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.macro.util;

import OLink.bpm.core.dynaform.form.ejb.Option;
import OLink.bpm.core.dynaform.form.ejb.Options;
import OLink.bpm.core.workflow.engine.StateMachineUtil;
import OLink.bpm.core.workflow.utility.CommonUtil;
import OLink.bpm.core.workflow.utility.Sequence;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.util.StringList;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.DateUtil;

/**
 * @author zhouty
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class Tools {

	public static StringList createStringList() {
		return new StringList();
	}

	public static Options createOptions() {
		return new Options();
	}

	public static Option createOption() {
		return new Option();
	}

	public static final StringUtil STRING_UTIL = new StringUtil();

	public static final DateUtil DATE_UTIL = new DateUtil();

	public static final CommonUtil COMMON_UTIL = new CommonUtil();

	public static final Sequence SEQ_UTIL = new Sequence();

	public static final StateMachineUtil STATE_MACHINE_UTIL = new StateMachineUtil();

	public static final PropertyUtil PROP_UTIL = new PropertyUtil();


	// public static final EmailUtil EMAIL_UTIL = new EmailUtil();

}
