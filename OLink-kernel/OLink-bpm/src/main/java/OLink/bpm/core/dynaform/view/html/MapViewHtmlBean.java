package OLink.bpm.core.dynaform.view.html;

import java.util.ArrayList;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

public class MapViewHtmlBean extends ViewHtmlBean {
	private static final Logger LOG = Logger.getLogger(MapViewHtmlBean.class);

	public String getMapColumnName() {
		if (view.getViewTypeImpl().getColumnMapping() != null) {
			return view.getViewTypeImpl().getColumnMapping().get("mapcolumn").getName();
		}

		return "";
	}

	public String toLocationString(DataPackage<Document> dataPackage) {
		StringBuffer sb = new StringBuffer();
		try {
			IRunner runner = getRunner();
			if (dataPackage.rowCount > 0) {
				sb.append("{\"deleteReadOnly\":"+(view.getReadonly() || mapReadonly)+",\"editReadOnly\":"+view.getReadonly()+",\"mapInfo\":[");
				for (Iterator<Document> iterator = dataPackage.datas.iterator(); iterator.hasNext();) {
					Document doc = iterator.next();
					runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());

					Column column = view.findColumnByName(getMapColumnName());
					if (column != null) {
						String text = column.getText(doc, runner, webUser);
						if (!StringUtil.isBlank(text) && !text.equals("&nbsp")) {
							sb.append(text);
							sb.append(",");
						}

					}
				}

				if (sb.indexOf(",") != -1) {
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("]}");
			}
		} catch (Exception e) {
			LOG.warn("toLocationString", e);
		}

		return sb.toString();
	}

	public String toGoogleMapUrl(DataPackage<Document> dataPackage) {
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(request.getContextPath() + "/portal/share/googlemap/view/googlemap.jsp");
		urlBuffer.append("?formid=" + view.getRelatedForm());
		urlBuffer.append("&_viewid=" + view.getId());
		return urlBuffer.toString();
	}
}
