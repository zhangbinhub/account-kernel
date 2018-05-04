package OLink.bpm.core.dynaform.dts.el2xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.xml.XmlUtil;
import OLink.bpm.util.xml.converter.ActivityConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.util.xml.converter.ColumnConverter;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.xml.converter.HibernateCollectionConverter;

import com.thoughtworks.xstream.XStream;

public class FormElementToXml extends ElementToXml {
	XStream xstream;

	public FormElementToXml() throws Exception {
		super();
		xstream = new XStream(new DomDriver());
		xstream.registerConverter(new HibernateCollectionConverter(xstream.getMapper()));
		xstream.registerConverter(new ActivityConverter());
		xstream.registerConverter(new ColumnConverter());
	}

	public void doTransfer() throws Exception {
		ApplicationProcess ap = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
		Collection<?> apps = ap.doSimpleQuery(null);
		for (Iterator<?> iterator = apps.iterator(); iterator.hasNext();) {
			ApplicationVO app = (ApplicationVO) iterator.next();
			if (app.getName().equals("CHINA")) {
				doTransferByApp(app.getId());
			}
		}
	}

	public void doTransferByApp(String applicationId) throws Exception {
		FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		ParamsTable params = new ParamsTable();
		params.setParameter("t_applicationid", applicationId);
		Collection<?> formList = fp.doSimpleQuery(params);
		try {
			for (Iterator<?> iterator = formList.iterator(); iterator.hasNext();) {
				Form form = (Form) iterator.next();

				TreeSet<Activity> activitySet = new TreeSet<Activity>();
				String sql = "select ID, ORDERNO, NAME, BEFOREACTIONSCRIPT, HIDDENSCRIPT, ONACTIONFORM_ID ONACTIONFORM, ONACTIONVIEW_ID ONACTIONVIEW, TYPE, FORM_ID PARENTFORM, VIEW_ID PARENTVIEW, ONACTIONFLOW_ID ONACTIONFLOW, ICONURL, STATETOSHOW, AFTERACTIONSCRIPT, APPROVELIMIT, SORTID, APPLICATIONID from T_ACTIVITY where FORM_ID='"
						+ form.getId() + "' order by ORDERNO";
				Collection<?> activitys = getElementsBySQL(sql, Activity.class);
				int orderno = 0;
				for (Iterator<?> iterator2 = activitys.iterator(); iterator2.hasNext(); orderno++) {
					Activity activity = (Activity) iterator2.next();
					activity.setOrderno(orderno);
					activitySet.add(activity);
				}
				String xml = XmlUtil.toXml(activitySet);
				form.setActivityXML(xml);
				try {
					fp.doUpdate(form);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new FormElementToXml().doTransfer();
	}

}
