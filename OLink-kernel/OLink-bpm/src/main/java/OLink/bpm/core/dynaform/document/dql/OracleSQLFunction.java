package OLink.bpm.core.dynaform.document.dql;

public class OracleSQLFunction extends AbstractSQLFunction implements
		SQLFunction {

	@Override
	public String toChar(String field, String patten) {
		String t_patten = patten;
		if("yyyy-MM-dd HH:mm:ss".equals(patten)){
			t_patten = "yyyy-MM-dd HH24:mi:ss";
		} else if("yyyy-MM-dd HH:mm".equals(patten)){
			t_patten = "yyyy-MM-dd HH24:mi";
		} else if("HH:mm:ss".equals(patten)){
			t_patten = "HH24:mi:ss";
		}
		return super.toChar(field, t_patten);
	}
	
	@Override
	public String toDate(String field, String patten) {
		String t_patten = patten;
		if("yyyy-MM-dd HH:mm:ss".equals(patten)){
			t_patten = "yyyy-MM-dd HH24:mi:ss";
		} else if("yyyy-MM-dd HH:mm".equals(patten)){
			t_patten = "yyyy-MM-dd HH24:mi";
		} else if("HH:mm:ss".equals(patten)){
			t_patten = "HH24:mi:ss";
		}		
		return super.toDate(field, t_patten);
	}
}
