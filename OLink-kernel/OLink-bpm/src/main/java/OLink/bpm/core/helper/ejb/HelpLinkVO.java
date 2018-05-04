package OLink.bpm.core.helper.ejb;
/*
 * level parent
 * <a taget='' title='' src=''>XXX</a>
 * */
public class HelpLinkVO {
	
	private String id;
	private String title;
	private String href="";
	private String target="detail";
	private String desc;
	private String onclick;
	private String onmouseover;
	private String onmouseout;
	private String parentid;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getOnclick() {
		return onclick;
	}
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	public String getOnmouseover() {
		return onmouseover;
	}
	public void setOnmouseover(String onmouseover) {
		this.onmouseover = onmouseover;
	}
	public String getOnmouseout() {
		return onmouseout;
	}
	public void setOnmouseout(String onmouseout) {
		this.onmouseout = onmouseout;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
