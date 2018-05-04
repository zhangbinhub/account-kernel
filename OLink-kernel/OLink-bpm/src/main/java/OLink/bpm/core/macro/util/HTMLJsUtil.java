package OLink.bpm.core.macro.util;


public class HTMLJsUtil {
    private StringBuffer _html;  
	
    public HTMLJsUtil() {
    	_html = new StringBuffer();	
	}
	
    public void clear(){
		_html.delete(0,_html.length());
	}

    public void append(String v){
		_html.append(v);
	}
    public void append(int v){
		_html.append(v);
	}    
    public void append(float v){
		_html.append(v);
	}    
    public void append(long v){
		_html.append(v);
	}  
    public void append(double v){
		_html.append(v);
	}      
    public String toString(){
    	return _html.toString();    	
    }
	
    public static void main(String[] args) {
	}
}
