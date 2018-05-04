package OLink.bpm.core.filemanager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class FileManagerSaveFileServlet
 */
public class FileManagerSaveFileServlet extends HttpServlet {
	
	private static final long serialVersionUID = 883501646436462124L;
	private int len=0;//处理流
	private int mm=0;//重命名
	private String fileName="";//文件原名
	private String extName="";//文件扩展名
	private String tempFileName="";//文件名加扩展名
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)    
	throws ServletException, IOException {    
	processRequest(request, response);    
	}    
	  
	public void doPost(HttpServletRequest request, HttpServletResponse response)    
	 throws ServletException, IOException {    
	processRequest(request, response);    
	}    
	
	public void processRequest(HttpServletRequest request, HttpServletResponse response)

    throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String realPath=request.getParameter("realPath");
		response.setContentType("application/octet-stream");
		InputStream is = request.getInputStream();
		try {
		int size = 0;
		byte[] tmp = new byte[100000];
		
		tempFileName=realPath.substring(realPath.lastIndexOf("\\")+1);//切割获得文件名加扩展名
		fileName=tempFileName.substring(0,tempFileName.lastIndexOf("."));//切割获得文件名
		//确保获得真实的文件名如：1(1)可以获得真实为1,
		if(fileName.indexOf("(")!=-1){
			fileName=fileName.substring(0,fileName.indexOf("("));
		}
		
		extName=tempFileName.substring(tempFileName.lastIndexOf("."));//切割获得扩展名
		
		//调用递归方法
		fileName+=reNameFile(realPath.substring(0,realPath.lastIndexOf("\\")+1),fileName,extName);
		// 创建一个文件夹用来保存发过来的图片；
		File f = new File(realPath.substring(0,realPath.lastIndexOf("\\")+1)+fileName+extName);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(f));
		while ((len = is.read(tmp)) != -1) {
		dos.write(tmp, 0, len);
		size += len;
		}
		dos.flush();
		dos.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
	//递归来重命名文件名
	String str="";
	public String reNameFile(String realPath,String filename,String extName){
		File file =new File(realPath+"\\"+filename+extName);
		str="";
        if(file.exists()){
        	mm++;
        	str="_"+mm;
        	reNameFile(realPath,fileName+str,extName);
        }else{
        	if(mm!=0){
    		str="_"+mm;
        	}
        }
		return str;
	}
}
