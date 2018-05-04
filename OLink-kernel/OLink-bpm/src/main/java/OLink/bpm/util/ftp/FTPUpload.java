/*
 * Created on 2005-5-31
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.util.ftp;

import java.io.File;

import OLink.bpm.util.StringUtil;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPTransferType;


/**
 * @author Administrator
 * 
 * Preferences - Java - Code Style - Code Templates
 */
public class FTPUpload {

	private String ftpServer;

	private String ftpPort;

	private String ftpUserName;

	private String ftpPassword;

	private FTPClient ftpClient;

	private boolean isLogin = false;

	public FTPUpload() {

	}

	// 登陆ftp服务器
	public void login(String pFtpServer, String pFtpPort, String pFtpUserName,
			String pFtpPassword) throws Exception {
		this.ftpServer = pFtpServer;
		if (pFtpPort.trim().equals(""))
			this.ftpPort = "21";
		else
			this.ftpPort = pFtpPort;
		if (pFtpUserName.trim().equals(""))
			this.ftpUserName = "Anonymous";
		else
			this.ftpUserName = pFtpUserName;
		this.ftpPassword = pFtpPassword;
		try {
			//ftpClient = new FTPClient(ftpServer, Integer.parseInt(ftpPort));
			ftpClient = new FTPClient();
			ftpClient.setRemoteHost(ftpServer);
			ftpClient.setRemotePort(Integer.parseInt(ftpPort));
			ftpClient.login(ftpUserName, ftpPassword);
			ftpClient.chdir("/");
			isLogin = true;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	// 上传指定文件夹到ftp服务器上
	public String uploadFolder(String folderName, String ftpPath)
			throws Exception {
		if (isLogin) {
			StringBuffer strMsg = new StringBuffer();
			try {
				File file = new File(folderName);
				if (file.isDirectory()) {
					ftpClient.chdir("/");
					ftpClient.setType(FTPTransferType.BINARY);
					if (checkFolderIsExist(ftpPath)) {
						ftpClient.chdir(ftpPath);
					} else {
						createFolder(ftpPath);
					}
					if (!checkFolderIsExist(file.getName())) {
						ftpClient.mkdir(file.getName());
					}
					ftpClient.chdir(file.getName());
					ftpPath = ftpPath + "\\" + file.getName();
					File[] files = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						if (files[i].isDirectory()) {
							uploadFolder(files[i].getPath(), ftpPath);
						} else {
							if (files[i].isFile()) {
								try {
									ftpClient.put(files[i].getPath(), files[i]
											.getName());
								} catch (Exception ee) {
									strMsg.append("upload file<<：" + files[i].getPath() + ">> error！Message:"
											+ ee.getMessage() + "\r\n");
								}
							}
						}
					}
					if (!"".equals(strMsg.toString())) {
						throw new Exception(strMsg.toString());
					}
				} else {
					throw new Exception(folderName + " is not a folder'name!");
				}
			} catch (Exception e) {
				strMsg.append(e.getMessage() + "\r\n");
			}
			return strMsg.toString();
		} else {
			throw new Exception("you didnot login remote ftp server!");
		}
	}

	// 把指定目录下所有的文件上传到ftp服务器上
	public void uploadAllFilesInFolder(String folderName, String ftpPath)
			throws Exception {
		if (isLogin) {
			StringBuffer strMsg = new StringBuffer();
			try {
				File file = new File(folderName);
				if (file.isDirectory()) {
					ftpClient.chdir("/");
					ftpClient.setType(FTPTransferType.BINARY);
					ftpPath = cleanFixString(ftpPath, "/");
					if (checkFolderIsExist(ftpPath)) {
						ftpClient.chdir(ftpPath);
					} else {
						createFolder(ftpPath);
					}
					File[] files = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						if (files[i].isFile()) {
							try {
								ftpClient.put(files[i].getPath(), files[i]
										.getName());
							} catch (Exception ee) {
								strMsg.append("upload file<<：" + files[i].getPath()
										+ ">> error！Message:" + ee.getMessage()
										+ "\r\n");
							}
						}
					}
				} else {
					throw new Exception(folderName + " is not a folder'name!");
				}
			} catch (Exception e) {
				throw new Exception(e.getMessage());
			}
		} else {
			throw new Exception("you didnot login remote ftp server!");
		}
	}

	// 上传指定文件到ftp服务器上
	public void uploadFile(String clientFileName, String ftpPath)
			throws Exception {
		if (isLogin) {
			try {
				// 获取文件名
				String filename = "";
				int index = clientFileName.lastIndexOf("\\");
				filename = clientFileName.substring(index + 1);
				ftpClient.chdir("/");
				ftpClient.setType(FTPTransferType.BINARY);
				if (checkFolderIsExist(ftpPath)) {
					ftpClient.chdir(ftpPath);
				} else {
					createFolder(ftpPath);
				}
				ftpClient.put(clientFileName, filename);
			} catch (Exception ex) {
				throw new Exception(ex.getMessage());
			}
		} else {
			throw new Exception("you didnot login remote ftp server!");
		}
	}

	// 检查FTP服务器上文件夹是否存在
	public boolean checkFolderIsExist(String pFolder) throws Exception {
		if (isLogin) {
			boolean result = false;
			try {
				ftpClient.chdir(pFolder);
				result = true;
			} catch (Exception ex) {
				result = false;
			}
			ftpClient.chdir("/");
			return result;
		} else {
			throw new Exception("you didnot login remote ftp server!");
		}
	}

	// 创建远程FTP服务器文件夹
	public void createFolder(String pFolder) throws Exception {
		if (isLogin) {
			if (checkFolderIsExist(pFolder) == false) {
				try {
					//String path = "";
					ftpClient.chdir("/");
					String[] folders = pFolder.split("\\\\");
					for (int i = 0; i < folders.length; i++) {
						try {
							ftpClient.chdir(folders[i]);
						} catch (Exception ex) {
							ftpClient.mkdir(folders[i]);
							ftpClient.chdir(folders[i]);
						}
					}
				} catch (Exception ex) {
					throw new Exception(ex.getMessage());
				}
			}
		} else {
			throw new Exception("you didnot login remote ftp server!");
		}
	}

	// 清除字符串两边的特定字符串
	public String cleanFixString(String str, String fix) {
		if (str.startsWith(fix))
			str = str.substring(fix.length());
		if (str.endsWith(fix))
			str = str.substring(0, str.length() - fix.length());
		return str;
	}

	public boolean checkFileIsExist(String folder, String filename)
			throws Exception {
		if (isLogin) {
			if (StringUtil.isBlank(folder))
				folder = ".";
			if (StringUtil.isBlank(filename))
				filename = "*.*";
			// filename=filename.toLowerCase();
			ftpClient.chdir(folder);
			String[] filesFolder = ftpClient.dir(filename, true);
			int filesNumber = 0;
			for (int i = 0; i < filesFolder.length; i++) {
				if (filesFolder[i].indexOf("<DIR>") == -1) {
					// files[filesNumber]=filesFolder[i].substring(beginloc);\
					if (filesFolder[i].toUpperCase().endsWith(".ERR")) {
						filesNumber++;
					}
				}
			}
			if (filesNumber > 0)
				return true;
			/*
			 * if (filename=="*.*"){ if (filesNumber>0) return true; }else if
			 * (filename.indexOf("*")==-1) { for (int i = 0; i < filesNumber;
			 * i++){ String s=files[i].toLowerCase(); if(s.equals(filename))
			 * return true; } } else if
			 * ((filename.indexOf("*.")==0)&&(filename.length()>2)) { String
			 * s2=filename.substring(filename.indexOf("*.")+1); for (int i = 0;
			 * i < filesNumber; i++){ String s=files[i].toLowerCase(); if
			 * (s.endsWith(s2)) return true; } } else if
			 * ((filename.indexOf(".*")>0)&&(filename.length()>2)) { for (int i =
			 * 0; i < filesNumber; i++){ String s=files[i].toLowerCase(); if
			 * (s.indexOf(filename.substring(0,filename.indexOf(".*")+1))!=-1)
			 * return true; } }
			 */
			return false;
		} else {
			throw new Exception("you didnot login remote ftp server!");
		}
	}

	public static void main(String[] args) {
		try {
			FTPUpload ftp = new FTPUpload();

			ftp.login("sapint.gdc.wrigley.com", "21", "cnguatrd", "Thr33");

			//(ftp.checkFileIsExist(
			//		"goldbear/trdexp/inbound/new", "*.ERR"));
			// ftp.uploadAllFilesInFolder("goldbear/trdexp/inbound/new","yhp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
