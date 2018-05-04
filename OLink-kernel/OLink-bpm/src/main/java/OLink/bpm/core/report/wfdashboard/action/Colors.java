package OLink.bpm.core.report.wfdashboard.action;


public class Colors {
	
	String colors[] = new String[16]; 
	
	public Colors() {
		
     colors[0] = "#000000";
     colors[4] = "#7c7c7c";
     colors[8] = "#f0f0f0";
     colors[11] = "#cccccc";
     
     colors[1] = "#ff0000";
     colors[5] = "#ff00ff";
     colors[9] = "#ffff00";
     colors[15] = "#ff8844";
     colors[12] = "#660000";
     colors[14] = "#ff7744";
     
     colors[2] = "#0000ff";
     colors[6] = "#004400";
     colors[10] = "#bb4411";
     colors[13] = "#ff66bb";
     
     
     colors[3] = "#ffff00";
     colors[7] = "#553300";
	}
	
	public String getColor(int index)
	{
		if(index%16 == 0)
		 return colors[index];
		else
		 return colors[index%16];
	}

}
