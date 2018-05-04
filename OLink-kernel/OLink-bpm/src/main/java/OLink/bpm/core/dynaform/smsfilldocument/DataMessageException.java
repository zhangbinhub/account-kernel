package OLink.bpm.core.dynaform.smsfilldocument;

public class DataMessageException extends Exception {
	
	private static final long serialVersionUID = 5553009470555737587L;
	private int status = -1;
	
	public DataMessageException(String msg){
		this(-1,msg);
	}
	
	public DataMessageException(int status,String msg){
		super(msg);
		this.status = status;
	}
	
	public int getStatus(){
		return status;
	}
}
