package OLink.bpm.core.image.repository.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ImageRepositoryActionHelper {
	
	Collection<String> Repotype = new ArrayList<String>();
	
	int i=100;

	public ImageRepositoryActionHelper()
	{
		 Repotype.add("文档");
		 Repotype.add("日期");
		 Repotype.add("字符串");
		 Repotype.add("数字");
	}
	
	public int getI() {
		return i;
	}


	public void setI(int i) {
		this.i = i;
	}

	public Collection<String> getRepotype() {
		return Repotype;
	}

	public void setRepotype(List<String> repotype) {
		Repotype = repotype;
	}



}
