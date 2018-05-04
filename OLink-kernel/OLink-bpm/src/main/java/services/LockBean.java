package services;

public class LockBean {
	private boolean hasPrivate;
	private String startDate;
	private String endDate;
	
	public boolean isHasPrivate() {
		return hasPrivate;
	}
	public void setHasPrivate(boolean hasPrivate) {
		this.hasPrivate = hasPrivate;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
