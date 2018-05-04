package OLink.bpm.util.file;

import java.sql.Blob;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.constans.Environment;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;

public class FileUtil {

	public static String writeFileByDS(String fileWebName, String dataSourceName, String sql, String applicationId)
			throws Exception {
		DataSourceProcess dsProcess = (DataSourceProcess) ProcessFactory.createProcess(DataSourceProcess.class);

		try {
			Environment env = Environment.getInstance();
			Map<?, ?> map = dsProcess.findDataSourceSQL(dataSourceName, sql, applicationId);
			for (Iterator<?> iterator = map.entrySet().iterator(); iterator.hasNext();) {
				Entry<?, ?> entry = (Entry<?, ?>) iterator.next();
				if (entry.getValue() instanceof Blob) {
					Blob blob = (Blob) entry.getValue();
					String fileTypes = FileOperate.getFileType(blob.getBinaryStream());

					String[] types = fileTypes.split("/");
					if (types.length > 0) {
						StringBuffer fileNames = new StringBuffer();
						for (int i = 0; i < types.length; i++) {
							FileOperate.writeFile(env.getRealPath(fileWebName + "." + types[i]), blob.getBinaryStream());
							fileNames.append(env.getContextPath() + fileWebName + "." + types[i] + ";");
						}
						//fileNames = fileNames.substring(0, fileNames.toString().lastIndexOf(";"));
						return fileNames.substring(0, fileNames.toString().lastIndexOf(";"));
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
