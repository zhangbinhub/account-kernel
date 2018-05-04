package OLink.bpm.core.dynaform.dts.datasource.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.util.ProcessFactory;

public class DatasourceHelper {
	
	public Map<String, String>  getAllDatasource(String application) throws Exception
	{
		Collection<DataSource> dc = getDataSources(application);
		HashMap<String, String> dshm = new HashMap<String, String>();
		for (Iterator<DataSource> iter = dc.iterator(); iter.hasNext();) {
			DataSource ds = iter.next();
			dshm.put(ds.getId(),ds.getName());
		}
		return dshm;
		
	}
	
	public Collection<DataSource> getDataSources(String applicationid)throws Exception{
		DataSourceProcess dp = (DataSourceProcess) ProcessFactory.createProcess((DataSourceProcess.class));
		return dp.doSimpleQuery(null, applicationid);
	}
}
