package OLink.bpm.core.upload;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.core.upload.ejb.UploadInfo;
import OLink.bpm.core.upload.ejb.UploadProcess;
import OLink.bpm.core.upload.ejb.UploadVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.DefaultProperty;
import nl.justobjects.pushlet.util.Log;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class Upload
 */
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = -3986799601741140351L;
	private String path;// 上传文件保存的路径
	private String fileSaveMode;// 文件保存模式
	private String fieldid;// 表单字段
	private String allowedTypes;// 允许上传的类型
	private String applicationid;// 上传限制大小
	private String uploadContentType;// 上传文件类型
	private File saveFile;// 上传时生成文件
	private String uploadFileName;// 上传文件的名称
	private String uuid = "";// 保存到数据库中编号
	private UploadInfo uploadInfo1;
	private String returnPath;

	/**
	 * 文件上传请求调用的方法
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 * @throws Exception
	 */
	public void processRequest(HttpServletRequest request,
			HttpServletResponse response)

	throws Exception {

		// 获取upload.jsp传来参数
		String data = request.getParameter("data");
		// 分割分别获得对应数据
		String[] dataArry = data.split(",");

		// 赋值相应参数
		path = dataArry[0].split(":")[1].equals("null") ? "" : dataArry[0]
				.split(":")[1];
		fileSaveMode = dataArry[1].split(":")[1].equals("null") ? ""
				: dataArry[1].split(":")[1];
		fieldid = dataArry[2].split(":")[1].equals("null") ? "" : dataArry[2]
				.split(":")[1];
		allowedTypes = dataArry[3].split(":")[1].equals("null") ? ""
				: dataArry[3].split(":")[1];
		applicationid = dataArry[4].split(":")[1].equals("null") ? ""
				: dataArry[4].split(":")[1];
		String userid = request.getSession().getAttribute(
				"FRONT_USER") != null ? ((WebUser) request.getSession()
				.getAttribute("FRONT_USER")).getId() : null;

		if (path.indexOf("/") != -1) {
			path = path.substring(1);
		}
		// 获得文件保存的真实路径
		String savePath = this.getServletConfig().getServletContext()
				.getRealPath("");
		// 拼接好文件要保存的真实文件夹
		savePath += getUploadInfo().getFileDir();
		// 生成文件
		File f1 = new File(savePath);
		// 如果该文件夹不存在则创建
		if (!f1.exists()) {
			if (!f1.mkdirs()) {
				Log.warn("Failed to create folder (" + savePath + ")");
				throw new IOException("Failed to create folder (" + savePath
						+ ")");
			}
		}
		// 磁盘文件个项工厂
		DiskFileItemFactory fac = new DiskFileItemFactory();
		// servlet文件上传
		ServletFileUpload upload = new ServletFileUpload(fac);
		// 设置上传编码
		upload.setHeaderEncoding("utf-8");
		List<?> fileList = null;// 文件列表
		try {
			fileList = upload.parseRequest(request);// 获得列表
		} catch (FileUploadException ex) {
			ex.printStackTrace();
			throw ex;
		}
		Iterator<?> it = fileList.iterator();
		String fileName = "";// 文件名
		String extName = "";// 文件扩展名
		long size = 0;// 图片大小
		while (it.hasNext()) {
			FileItem item = (FileItem) it.next();
			if (!item.isFormField()) {
				fileName = filter(item.getName().substring(0,
						item.getName().lastIndexOf(".")));// 获得文件名
				fileName = fileName.replace(" ", "");
				extName = item.getName().substring(
						item.getName().lastIndexOf("."));
				size = item.getSize();// 获得图片大小
				if (item.getName() == null || item.getName().trim().equals("")
						|| item.getName().trim().equals("null")) {
					continue;
				}
				// 扩展名格式：
				if (item.getName().lastIndexOf(".") >= 0) {
					uploadFileName = fileName + extName;
					uploadContentType = allowedTypes
							+ "/"
							+ item.getName().substring(
									item.getName().lastIndexOf(".") + 1);
				}
				uploadInfo1 = getUploadInfo();
				reName(uploadInfo1, 0, fileName, extName);// 递归重命名文件
//				FileItem fileData = null;
				try {
					// // add by linrui for nginx server to pre deal with remote
					// // server
					// Boolean nginxSetFlag=false;
					// fileData = item;
					// String useFlag = DefaultProperty
					// .getProperty("USE_IMAGESERVER_FLAG");
					// String nginxUploadUrl=DefaultProperty
					// .getProperty("IMAGE_UPLOAD_URL");
					//
					// if (useFlag.equals("1")) {// use image server
					// String Nginx_url = DefaultProperty
					// .getProperty("NGINX_URL");
					//
					// Log.info(Nginx_url);
					// InputStream in = fileData.getInputStream();
					// BufferedInputStream bis = new BufferedInputStream(in);
					// HttpClient client = new HttpClient();
					// PostMethod postMethod = new PostMethod(Nginx_url);
					// postMethod.getParams().setParameter(
					// HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
					// RequestEntity requestEntity = new
					// InputStreamRequestEntity(
					// bis);
					// postMethod.setRequestHeader("fileName",
					// URLEncoder.encode(uploadInfo1.getFileName(), "UTF-8"));
					// // 这里把相关信息放到头信息里面了,方便解析,针对中文乱码,先编码再解码
					//
					// postMethod.setRequestHeader("content-type",
					// "multipart/form-data");
					// postMethod.setRequestEntity(requestEntity);
					// client.getHttpConnectionManager().getParams()
					// .setConnectionTimeout(500000);// 设置超时
					// int status;
					//
					//
					// try {
					// status = client.executeMethod(postMethod);
					// if(status==200){
					// nginxSetFlag=true;
					// }
					// Log.info("status:" + status);
					// // 关闭流
					// in.close();
					// // 将链接返回给connection manager
					// postMethod.releaseConnection();
					// fileData.delete();
					//
					// } catch (Exception e) {
					// Log.warn("exception deal-------");
					// // 关闭流
					// in.close();
					// // 将链接返回给connection manager
					// //postMethod.releaseConnection();
					// fileData.delete();
					//
					// // 原有方式直接在web服务器上写文件，
					// saveFile = new File(
					// uploadInfo1.getFileRealSavePath());
					// item.write(saveFile);
					//
					// }
					// add by lr 20151209 savefile on image server
					Boolean imageSetFlag = false;
					String useFlag = DefaultProperty
							.getProperty("USE_IMAGESERVER_FLAG");
					String imageUploadReturnUrl="";
					if (useFlag.equals("1")) {// this is mean imageserver config
												// ok

						// 新的方式在存储服务器上写文件，
						String imageUploadUrl = DefaultProperty
								.getProperty("IMAGE_UPLOAD_URL");
						
						String sourceFilName=imageUploadUrl+uploadInfo1.getFileName();
						String changedFilename=reName(sourceFilName,0,fileName,extName,imageUploadUrl,uploadInfo1);
						saveFile = new File(changedFilename);
						Log.info("--------file upload path:"+saveFile.getPath());
						item.write(saveFile);
						imageSetFlag=true;
						imageUploadReturnUrl=DefaultProperty.getProperty("IMAGE_UPLOAD_RETURN_URL");
						//uploadInfo1.setFileName(changedFilename);

					} else {
						// 原有方式直接在web服务器上写文件，
						saveFile = new File(uploadInfo1.getFileRealSavePath());
						item.write(saveFile);
					}

					if (applicationid != null && !applicationid.equals("")) {
						UploadProcess uploadProcess = (UploadProcess) ProcessFactory
								.createRuntimeProcess(UploadProcess.class,
										applicationid);
						uuid = UUID.randomUUID().toString();
						UploadVO uploadVO = new UploadVO();
						uploadVO.setId(uuid);
						uploadVO.setName(uploadInfo1.getFileName());
						// Log.info(uploadInfo1.getFileName());
						uploadVO.setFieldid(fieldid);
						uploadVO.setType(extName);
						uploadVO.setSize(size);
						uploadVO.setModifyDate(new SimpleDateFormat(
								"yyyy-MM-dd").format(new Date()));
						uploadVO.setUserid(userid);

						if (imageSetFlag == true) {
							uploadVO.setPath(imageUploadReturnUrl+
									uploadInfo1.getWebPath());
							uploadVO.setFolderPath(imageUploadReturnUrl
									+ uploadInfo1.getFileDir());
							returnPath = imageUploadReturnUrl
									+ uploadInfo1.getWebPath();

						} else {
							uploadVO.setPath(uploadInfo1.getWebPath());
							uploadVO.setFolderPath(uploadInfo1.getFileDir());
							returnPath = uploadInfo1.getWebPath();
						}

						uploadProcess.doCreate(uploadVO);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// Log.info(returnPath);
		// Log.info(uploadInfo1.getWebPath());
		// 解决中文乱码返回网络路径
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().print(returnPath);
	}

	// 递归文件名称
	protected void reName(UploadInfo uploadInfo, int i, String fileName,
			String extName) {
		File file = new File(uploadInfo.getFileRealSavePath());
	    System.out.println("---------file real save path is :=========="+file.getPath());
		if (file.exists()) {
			i++;
			uploadInfo.setFileName(fileName + i + extName);
			reName(uploadInfo, i, fileName, extName);
		}
	}
	// 递归文件名称
		protected String reName(String sourceFileName, int i, String fileName,
				String extName,String savePath,UploadInfo uploadInfo) {
			String changedFileName=sourceFileName;
			File file = new File(sourceFileName);
		    Log.info("---------file real save path is :=========="+file.getPath());
			if (file.exists()) {
				Log.info("----------file exists and can be changed-------------");
				i++;
				changedFileName=savePath+fileName + i + extName;
							
				Log.info("-----------changed file name is:-----------"+changedFileName);
				return reName(changedFileName, i, fileName, extName,savePath,uploadInfo);
			}else{
				if(i>0){
					uploadInfo.setFileName(fileName + i + extName);
				}
					
				return changedFileName;
			}
		}

	// 过滤 '_'
	protected String filter(String str) {
		StringBuffer sb = new StringBuffer();
		sb.append(str);
		if (sb.indexOf("_") != -1) {
			sb.deleteCharAt(sb.indexOf("_"));
			filter(sb.toString());
		}
		return sb.toString();
	}

	// doget请求
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// dopost请求
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回UploadInfo实例
	 * 
	 * @return
	 */
	public UploadInfo getUploadInfo() {
		// 设置属性
		UploadInfo uploadInfo = new UploadInfo();
		uploadInfo.setAllowedTypes(allowedTypes);
		uploadInfo.setContentType(uploadContentType);
		uploadInfo.setFileName(uploadFileName);
		uploadInfo.setFileSaveMode(fileSaveMode);
		// uploadInfo.setMaximumSize(Integer.parseInt(maximumSize));
		try {
			uploadInfo.setPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadInfo;
	}

}
