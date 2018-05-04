package OLink.bpm.version.transfer;

import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.links.ejb.LinkProcess;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.resource.ejb.ResourceVO;

public class ResourceTransfer extends BaseTransfer {

	/**
	 * 升级到2.4版本，Resource模块的链接定制转移到Link模块
	 */
	public void to2_4() {
		Connection conn = getConnection();

		try {
			ResourceProcess rp = (ResourceProcess) ProcessFactory.createProcess(ResourceProcess.class);
			LinkProcess lp = (LinkProcess) ProcessFactory.createProcess(LinkProcess.class);

			QueryRunner qr = new QueryRunner();
			String sql = "select * from t_resource";
			Iterator<?> datas = ((List<?>) qr.query(conn, sql, new MapListHandler())).iterator();
			while (datas.hasNext()) {
				Map<?, ?> data = (Map<?, ?>) datas.next();

				// String appi = (String) data.get("applicationid");
				String linkid = (String) data.get("linkid");
				if (StringUtil.isBlank(linkid)) {
					String actioncontent = (String) data.get("displayview");
					String application = (String) data.get("applicationid");
					String linkname = (String) data.get("description");
					String moduleid = (String) data.get("module");
					String resourceAction = (String) data.get("resourceaction");
					Object obj = data.get("otherurl");
					String otherurl = "";
					if (obj instanceof java.sql.Clob) {
						java.sql.Clob clob = (java.sql.Clob) obj;
						Reader reader = clob.getCharacterStream();
						StringBuffer content = new StringBuffer();
						char[] chars = new char[1024];
						int readNum = 0;
						while((readNum = reader.read(chars, 0, chars.length)) != -1){
							content.append(chars, 0, readNum);
						}
						otherurl = content.toString();
					} else if (obj instanceof String) {
						otherurl = (String) obj;
					} else {
						otherurl = obj.toString();
					}
					String report = (String) data.get("report");
					String impmappingconfig = (String) data.get("impmappingconfig");

					String rid = (String) data.get("id");
					LinkVO lVO = new LinkVO();
					lVO.setApplicationid(application);
					lVO.setName(linkname);
					lVO.setQueryString("[]");
					lVO.setModuleid(moduleid);
					if ("01".equals(resourceAction)) {
						lVO.setType(LinkVO.LinkType.VIEW.getCode());
						lVO.setActionContent(actioncontent);
						lp.doCreate(lVO);
						ResourceVO rVO = (ResourceVO) rp.doView(rid);
						rVO.setLink(lVO);
						rp.doUpdate(rVO);
					} else if ("03".equals(resourceAction)) {
						lVO.setActionContent(otherurl);
						lVO.setType(LinkVO.LinkType.MANUAL_INTERNAL.getCode());
						lp.doCreate(lVO);
						ResourceVO rVO = (ResourceVO) rp.doView(rid);
						rVO.setLink(lVO);
						rp.doUpdate(rVO);
					} else if ("04".equals(resourceAction)) {
						lVO.setActionContent(report);
						lVO.setType(LinkVO.LinkType.REPORT.getCode());
						lp.doCreate(lVO);
						ResourceVO rVO = (ResourceVO) rp.doView(rid);
						rVO.setLink(lVO);
						rp.doUpdate(rVO);
					} else if ("08".equals(resourceAction)) {
						lVO.setActionContent(impmappingconfig);
						lVO.setType(LinkVO.LinkType.EXCELIMPORT.getCode());
						lp.doCreate(lVO);
						ResourceVO rVO = (ResourceVO) rp.doView(rid);
						rVO.setLink(lVO);
						rp.doUpdate(rVO);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				PersistenceUtils.closeSessionAndConnection();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new ResourceTransfer().to2_4();
	}
}
