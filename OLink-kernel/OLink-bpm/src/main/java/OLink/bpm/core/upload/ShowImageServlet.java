package OLink.bpm.core.upload;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.constans.Environment;
import OLink.bpm.core.upload.ejb.UploadProcess;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.util.ProcessFactory;

/**
 * Servlet implementation class ShowImageServlet
 */
public class ShowImageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 4433214690967799970L;
	/**
	 * doget请求
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {    
	try {
		processRequest(request, response);
	} catch (Exception e) {
		e.printStackTrace();
	}    
	}    
    /**
     * dopost请求
     */
	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {    
	try {
		processRequest(request, response);
	} catch (Exception e) {
		e.printStackTrace();
	}    
	}
	/**
	 * 显示图片
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public void processRequest(HttpServletRequest request, HttpServletResponse response)throws Exception {    
		String id = request.getParameter("id"); 
		String type = request.getParameter("type");
		String applicationid = request.getParameter("applicationid");
		if(id != null && id.length() > 0){ 
			//StringBuffer sb = new StringBuffer(); 
			UploadProcess uploadProcess =(UploadProcess) ProcessFactory.createRuntimeProcess(UploadProcess.class,applicationid);
			UploadVO uploadVO = (UploadVO)uploadProcess.doFindById(id);
			InputStream in = null;
			if(uploadVO!=null){
				in = uploadVO.getImgBinary();
			}
			if(in!=null){
				int bytesRead = 0; 
				byte[] buffer = new byte[8192];
				OutputStream os = null;
				try{
					if(type.equals("image")){
						response.setContentType("image/jpeg"); 
						response.setContentLength(in.available()); 
						int size = 0; 
						OutputStream outs = response.getOutputStream(); 
						while ((bytesRead = in.read(buffer, 0, 8192)) != -1) { 
							size = bytesRead; 
							outs.write(buffer, 0, bytesRead); 
						} 
						response.setContentLength(size); 
						outs.flush(); 
						outs.close();
					}else{
				        byte[] content=readFile(in);
				        String encoding = Environment.getInstance().getEncoding();
				        response.setContentType("application/x-download; charset=" + encoding + "");
						response.setHeader("Content-Disposition", "attachment;filename=\"" + java.net.URLEncoder.encode(uploadVO.getName(), encoding) + "\"");
						os = response.getOutputStream();
						os.write(content);
						os.flush();
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					if(in!=null){
						in.close(); 
					}
					if(os!=null){
						os.close();
					}
				}
			}
			
			 
		}

	}
	
	/** *//**读文件到字节数组
     * @param file
     * @return
     * @throws Exception
     */
    static byte[] readFile(InputStream in) throws   Exception {
    	ByteArrayOutputStream bytestream = new ByteArrayOutputStream();  
    	int ch;  
    	while ((ch = in.read()) != -1) {  
    		bytestream.write(ch);  
    	}  
    	byte imgdata[] = bytestream.toByteArray();  
    	bytestream.close();  
    	return imgdata;  
    }
    /** *//**将字节数组写入文件
     * @param filePath
     * @param content
     * @return
     * @throws IOException
     */
    static boolean writeBytes(String filePath, byte[] content)
            throws IOException {
        File file = new File(filePath);
        synchronized (file) {
            BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(filePath));
            fos.write(content);
            fos.flush();
            fos.close();
        }
        return true;

    }
}
