package OLink.bpm.core.workcalendar.util;

public class Year implements java.io.Serializable{

	private static final long serialVersionUID = 8920828917228685128L;

	private Month[] months=new Month[12];
	
	private int yearValue;

	public Month[] getMonths() {
		return months;
	}

	public int getYearValue() {
		return yearValue;
	}

	public void setMonths(Month[] months) {
		Month[] month0=new Month[12];
		for (int i=0;months!=null && months.length>i;i++){
			month0[i]=months[i];
		}
		this.months = month0;
	}

	public void setYearValue(int yearValue) {
		this.yearValue = yearValue;
	}
	
	public Month getMonths(int monthIndex){
		if (months!=null){
			return months[monthIndex];
		}
		return null;
		
	}
}
