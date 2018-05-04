package OLink.bpm.core.networkdisk.action;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.networkdisk.ejb.*;
import OLink.bpm.core.role.ejb.RoleProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.networkdisk.ejb.NetDisk;
import OLink.bpm.core.networkdisk.ejb.NetDiskFolder;
import OLink.bpm.core.networkdisk.ejb.NetDiskFolderProcess;
import OLink.bpm.core.networkdisk.ejb.NetDiskPemission;
import OLink.bpm.core.networkdisk.ejb.NetDiskProcess;

public class NetworkDisk {

	private DecimalFormat df = new DecimalFormat("0.00");
	
	//获得相应文件夹及其文件
	public String getFolderTree(String path,String userid){
		StringBuffer sb = new StringBuffer();
		try{
			// 获得文件保存的真实路径
			File f = new File(path);
			if(!f.exists()){
				if(!f.mkdirs()){
					throw new IOException("create folder '" + f + "' failed!");
				}
			}
			NetDiskFolderProcess netDiskFolderProcess = (NetDiskFolderProcess) ProcessFactory.createProcess(NetDiskFolderProcess.class);
			ParamsTable params = new ParamsTable();
			DataPackage<NetDiskFolder> datapackage = netDiskFolderProcess.doQuery(params);
			sb.append("<root>");
			if(path.indexOf("public")!=-1){
				sb.append("<menuitem label='公共共享[根目录]");
			}else{
				sb.append("<menuitem label='我的网盘[根目录]");
			}
			String permisstion = appendFolderPermission(datapackage,path,userid);
			sb.append(permisstion);
			sb.append("' path='").append(path);
			sb.append("'>");
			sb = getChildFolderTree(f.listFiles(),sb, datapackage, userid,permisstion);
			sb.append("</root>");
			//System.out.println("getFolderTree:"+sb.toString());
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	//迭代获得相应的文件夹
	public StringBuffer  getChildFolderTree(File[] list, StringBuffer sb , DataPackage<NetDiskFolder> datapackage, String userid, String permission){
		try{
			if(list.length>0){
				for (int i = 0; i < list.length; i++) {
					if (list[i].isDirectory()) {
						sb.append("<menuitem label='" + list[i].getName() + "' path='").append(list[i].getPath());
						if(permission!=null && !permission.equals("")){
							sb.append(permission);
						}else{
							permission = appendFolderPermission(datapackage,list[i].getPath(),userid);
							sb.append(permission);
						}
						sb.append("'>");
						if (list[i].listFiles().length > 0) {
							getChildFolderTree(list[i].listFiles(),sb , datapackage, userid,permission);
						}else{
							sb.append("</menuitem>");
						}
						permission = "";
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return sb;
		}
		sb.append("</menuitem>");
		return sb;
	}
	
	/**
	 * 文件夹权限
	 * @param datapackage
	 * @param path
	 * @param userid
	 * @return
	 */
	public String appendFolderPermission(DataPackage<NetDiskFolder> datapackage,String path,String userid){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskFolder netDiskFolder = null;
			if(datapackage.rowCount>0){
				for (Iterator<NetDiskFolder> it = datapackage.getDatas().iterator(); it.hasNext();) {
					netDiskFolder = it.next();
					if(path.trim().indexOf(netDiskFolder.getFolderPath().trim())!=-1){
						if(netDiskFolder.getPemission()!=null){
							NetDiskPemission netDiskPemission = netDiskFolder.getPemission();
							SuperUserProcess superUserProcess = (SuperUserProcess)ProcessFactory.createProcess(SuperUserProcess.class);
							if(netDiskPemission.getSelectObject().equals("用户")){
								if(netDiskPemission.getUsers().indexOf(userid)!=-1 || superUserProcess.doView(userid)!=null){
									sb.append("' id='").append(netDiskFolder.getId());
									sb.append("' userid='").append(netDiskFolder.getUserid());
									sb.append("' pemissionid='").append(netDiskFolder.getPemission().getId());
									sb.append("' operate='").append(netDiskPemission.getOperate());
									break;
								}
							}else if(netDiskPemission.getSelectObject().equals("部门")){
								DepartmentVO department = netDiskPemission.getDepartment();
								for (Iterator<UserVO> iterator = department.getUsers().iterator(); iterator.hasNext();) {
									UserVO user = iterator.next();
									if(user.getId().equals(userid) || superUserProcess.doView(userid)!=null){
										sb.append("' id='").append(netDiskFolder.getId());
										sb.append("' userid='").append(netDiskFolder.getUserid());
										sb.append("' pemissionid='").append(netDiskFolder.getPemission().getId());
										sb.append("' operate='").append(netDiskPemission.getOperate());
										break;
									}
								}
								break;
							}else if(netDiskPemission.getSelectObject().equals("角色")){
								RoleVO role = netDiskPemission.getRole();
								for (Iterator<UserVO> iterator = role.getUsers().iterator(); iterator.hasNext();) {
									UserVO user = iterator.next();
									if(user.getId().equals(userid) || superUserProcess.doView(userid)!=null){
										sb.append("' id='").append(netDiskFolder.getId());
										sb.append("' userid='").append(netDiskFolder.getUserid());
										sb.append("' pemissionid='").append(netDiskFolder.getPemission().getId());
										sb.append("' operate='").append(netDiskPemission.getOperate());
										break;
									}
								}
								break;
							}else if(netDiskPemission.getSelectObject().equals("组")){
								NetDiskGroup group = netDiskPemission.getGroup();
								if(group.getUseridGroup().indexOf(userid)!=-1 || superUserProcess.doView(userid)!=null){
									sb.append("' id='").append(netDiskFolder.getId());
									sb.append("' userid='").append(netDiskFolder.getUserid());
									sb.append("' pemissionid='").append(netDiskFolder.getPemission().getId());
									sb.append("' operate='").append(netDiskPemission.getOperate());
									break;
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		return sb.toString();
	}
	
	//创建文件夹
	public String create(String folderPath) {
		try {
			String temp = folderPath.substring(folderPath.lastIndexOf("\\"));
			if(temp.toLowerCase().indexOf("public")!=-1){
				return "'public' 为关键字";
			}
			File file = new File(folderPath);
			if (!file.exists()) {
				if(file.mkdirs())
					return "";
				else
					return "create folder '" + folderPath + "' failed!";
			} else {
				return "该文件夹已存在";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	//重命名
	public String reName(String oldNamePath, String newNamePath) {
		try {
			File file = new File(oldNamePath);
			if(file.renameTo(new File(newNamePath))){
				if(oldNamePath.indexOf("public")!=-1){
					ParamsTable params = new ParamsTable();
					params.setParameter("sm_folderPath", "public");
					NetDiskFolderProcess netDiskFolderProcess = (NetDiskFolderProcess)ProcessFactory.createProcess(NetDiskFolderProcess.class);
					DataPackage<NetDiskFolder> datapackageFolder = netDiskFolderProcess.doQuery(params);
					if(datapackageFolder.rowCount>0){
						for (Iterator<NetDiskFolder> itFolder = datapackageFolder.getDatas().iterator(); itFolder.hasNext();) {
							NetDiskFolder netDiskFolder = itFolder.next();
							if(netDiskFolder.getFolderPath().trim().equals(oldNamePath.trim())){
								netDiskFolder.setFolderPath(newNamePath);
								netDiskFolderProcess.doUpdate(netDiskFolder);
							}
						}
					}
				}
				return "";
			} else {
				return "'" + oldNamePath + "'-->'" + newNamePath + "' 重命名失败!";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	
	//只删除文件夹及其文件
	public String remove(String folderPath,String userid) {
		try {
			ParamsTable params = new ParamsTable();
			if(userid!=null &&!userid.equals("")){
				params.setParameter("t_userid", userid);
			}else{
				params.setParameter("sm_folderPath", "public");
			}
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			DataPackage<NetDiskFile> datapackage = netDiskFileProcess.doQuery(params);
			delAllFile(folderPath, datapackage, userid); // 删除完里面所有内容
			File myFilePath = new File(folderPath);
			if(myFilePath.exists()){
				if(!myFilePath.delete()){// 删除空文件夹
					throw new IOException("delete folder '" + folderPath + "' failed!");
				}
			} else {
				throw new IOException("folder '" + folderPath + "' does not exist!");
			}
			if(userid ==null || userid.equals("")){
				NetDiskFolderProcess netDiskFolderProcess = (NetDiskFolderProcess)ProcessFactory.createProcess(NetDiskFolderProcess.class);
				DataPackage<NetDiskFolder> datapackageFolder = netDiskFolderProcess.doQuery(params);
				if(datapackageFolder.rowCount>0){
					for (Iterator<NetDiskFolder> itFolder = datapackageFolder.getDatas().iterator(); itFolder.hasNext();) {
						NetDiskFolder netDiskFolder = itFolder.next();
						if(netDiskFolder.getFolderPath().equals(myFilePath.getPath())){
							netDiskFolderProcess.doRemove(netDiskFolder);
							break;
						}
					}
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	//删除所有文件
	public void delAllFile(String path,DataPackage<NetDiskFile> datapackage,String userid) throws Exception {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
//			if (path.endsWith(File.separator)) {
			if (path.endsWith("/")) {
				temp = new File(path + tempList[i]);
			} else {
//				temp = new File(path + File.separator + tempList[i]);
				temp = new File(path + "/" + tempList[i]);
			}
			if(temp.exists()){
				if (temp.isFile()) {
					if(!temp.delete()){
						throw new IOException("delete file '" + temp.getAbsolutePath() + "' failed!");
					} else {
						for (Iterator<NetDiskFile> it = datapackage.getDatas().iterator(); it.hasNext();) {
							NetDiskFile netDiskFile = it.next();
							if(temp.getPath().indexOf(netDiskFile.getFolderPath())!=-1){
								if(temp.getName().equals(netDiskFile.getName())){
								NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
								netDiskFileProcess.doRemove(netDiskFile);
								break;
								}
							}
						}
					}
				}
				if (temp.isDirectory()) {
					delAllFile(path + "/" + tempList[i],datapackage,userid);// 先删除文件夹里面的文件
					remove(path + "/" + tempList[i],userid);// 再删除空文件夹
				}
			} else {
				throw new IOException("'" + temp.getAbsolutePath() + "' does not exist!");
			}
		}
	}
	
	//保存文件夹权限信息
	public String saveFolderPemission(String id,String userid,String folderPath,String pemissionid){
		try{
			ParamsTable params = new ParamsTable();
			params.setParameter("t_folderPath", folderPath);
			NetDiskFolder netDiskFolder = null;
			NetDiskFolderProcess netDiskFolderProcess = (NetDiskFolderProcess)ProcessFactory.createProcess(NetDiskFolderProcess.class);
			NetDiskPemissionProcess netDiskPemissionProcess = (NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
			DataPackage<NetDiskFolder> datas = netDiskFolderProcess.doQuery(params);
			for (Iterator<NetDiskFolder> iterator = datas.datas.iterator(); iterator.hasNext();) {
				netDiskFolder = iterator.next();
				break;
			}
			if(netDiskFolder == null){
				netDiskFolder = new NetDiskFolder();
				netDiskFolder.setUserid(userid);
				netDiskFolder.setFolderPath(folderPath);
				NetDiskPemission netDiskPemission = (NetDiskPemission)netDiskPemissionProcess.doView(pemissionid);
				netDiskFolder.setPemission(netDiskPemission);
				netDiskFolderProcess.doCreate(netDiskFolder);
			}else{
				if(netDiskFolder==null)
				netDiskFolder = (NetDiskFolder)netDiskFolderProcess.doView(id);
				if(pemissionid.equals("")){
					netDiskPemissionProcess.doRemove(netDiskFolder);
				}else{
					NetDiskPemission netDiskPemission = (NetDiskPemission)netDiskPemissionProcess.doView(pemissionid);
					netDiskFolder.setPemission(netDiskPemission);
					netDiskFolderProcess.doUpdate(netDiskFolder);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	   return "";
	}
	
	//总的页数
	protected int getTotalPages(int totalRows,int pageSize){
		int totalPagesTemp = totalRows/pageSize;
		int mod = totalRows%pageSize;
		if(mod>0){
			totalPagesTemp+=1; 
		}
		return totalPagesTemp;
	}
	
	
	//迭代获得相应文件
	public String getFiles(String path,String userid,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			SuperUserProcess superUserProcess = (SuperUserProcess)ProcessFactory.createProcess(SuperUserProcess.class);
			UserProcess userProcess = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			
			ParamsTable params = new ParamsTable();
			String folderWebPath = path.substring(path.indexOf("networkdisk")-1).replaceAll("\\\\", "/");
			params.setParameter("t_folderwebpath", folderWebPath);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("sm_name", userName);
			params.setParameter("t_userid", userid);
			DataPackage<NetDiskFile> datapackage = netDiskFileProcess.doQuery(params);
			if(datapackage.rowCount>0){
				sb.append("{\"files\":[");
				for (Iterator<NetDiskFile> it = datapackage.getDatas().iterator(); it.hasNext();) {
					NetDiskFile netDiskFile = it.next();
					sb.append("{\"name\":\"");
					sb.append(netDiskFile.getName());
					sb.append("\",\"id\":\"");
					sb.append(netDiskFile.getId());
					sb.append("\",\"type\":\"");
					sb.append(netDiskFile.getType());
					sb.append("\",\"size\":\"");
					if(netDiskFile.getSize()<1024){
						sb.append((double)netDiskFile.getSize()+" B");
					}else if(netDiskFile.getSize()>=1024 && netDiskFile.getSize()<(1024*1024) ){
						sb.append((double)(netDiskFile.getSize()/1024)+" KB");
					}else if(netDiskFile.getSize()>=(1024*1024)){
						sb.append((double)(netDiskFile.getSize()/(1024*1024))+" M");
					}
					
					sb.append("\",\"modifyTime\":\"");
					sb.append(netDiskFile.getModifyTime());
					if(userid==null || userid.equals("")){
						sb.append("\",\"userName\":\"");
						if(superUserProcess.doView(netDiskFile.getUserid())!=null){
							sb.append(((SuperUserVO)superUserProcess.doView(netDiskFile.getUserid())).getName());
						}else if(userProcess.doView(netDiskFile.getUserid())!=null){
							sb.append(((UserVO)userProcess.doView(netDiskFile.getUserid())).getName());
						}
					}
					sb.append("\",\"folderPath\":\"");
					sb.append(netDiskFile.getFolderPath());
					sb.append("\",\"fileWebPath\":\"");
					sb.append(netDiskFile.getFolderWebPath()+"/"+netDiskFile.getName());
					sb.append("\",\"share\":\"");
					sb.append(netDiskFile.getPemission()!=null);
					sb.append("\",\"pemissionid\":\"");
					sb.append(netDiskFile.getPemission()!=null?netDiskFile.getPemission().getId():"");
					
					sb.append("\"},");
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(datapackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		//System.out.println("getFiles:"+sb.toString());
		return sb.toString();
	}
	
	//迭代获得相应公共文件
	public String getFilesPublic(String path,String userid,String operate,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			SuperUserProcess superUserProcess = (SuperUserProcess)ProcessFactory.createProcess(SuperUserProcess.class);
			UserProcess userProcess = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			
			ParamsTable params = new ParamsTable();
			String folderWebPath = path.substring(path.indexOf("networkdisk")-1).replaceAll("\\\\", "/");
			params.setParameter("t_folderwebpath", folderWebPath);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("sm_name", userName);
			DataPackage<NetDiskFile> datapackage = netDiskFileProcess.doQuery(params);
			NetDiskPemission netDiskPemission = null;
			
			if(datapackage.rowCount>0){
				sb.append("{\"files\":[");
				for (Iterator<NetDiskFile> it = datapackage.getDatas().iterator(); it.hasNext();) {
					NetDiskFile netDiskFile = it.next();
					sb.append("{\"name\":\"");
					sb.append(netDiskFile.getName());
					sb.append("\",\"id\":\"");
					sb.append(netDiskFile.getId());
					sb.append("\",\"type\":\"");
					sb.append(netDiskFile.getType());
					sb.append("\",\"size\":\"");
					if(netDiskFile.getSize()<1024){
						sb.append((double)netDiskFile.getSize()+" B");
					}else if(netDiskFile.getSize()>=1024 && netDiskFile.getSize()<(1024*1024) ){
						sb.append((double)(netDiskFile.getSize()/1024)+" KB");
					}else if(netDiskFile.getSize()>=(1024*1024)){
						sb.append((double)(netDiskFile.getSize()/(1024*1024))+" M");
					}
					
					sb.append("\",\"modifyTime\":\"");
					sb.append(netDiskFile.getModifyTime());
					sb.append("\",\"userName\":\"");
					if(superUserProcess.doView(netDiskFile.getUserid())!=null){
						sb.append(((SuperUserVO)superUserProcess.doView(netDiskFile.getUserid())).getName());
					}else if(userProcess.doView(netDiskFile.getUserid())!=null){
						sb.append(((UserVO)userProcess.doView(netDiskFile.getUserid())).getName());
					}
					sb.append("\",\"folderPath\":\"");
					sb.append(netDiskFile.getFolderPath());
					sb.append("\",\"fileWebPath\":\"");
					sb.append(netDiskFile.getFolderWebPath()+"/"+netDiskFile.getName());
					sb.append("\",\"share\":\"");
					boolean flag = false;
					if(userid !=null && !userid.equals("") && netDiskFile.getPemission()!=null){
						netDiskPemission = netDiskFile.getPemission();
						if(netDiskPemission.getSelectObject().equals("用户")){
							if(netDiskPemission.getUsers().indexOf(userid)!=-1 || superUserProcess.doView(userid)!=null){
								flag = true;
							}
						}else if(netDiskPemission.getSelectObject().equals("部门")){
							DepartmentVO department = netDiskPemission.getDepartment();
							for (Iterator<UserVO> iterator = department.getUsers().iterator(); iterator.hasNext();) {
								UserVO user = iterator.next();
								if(user.getId().equals(userid) || superUserProcess.doView(userid)!=null){
									flag = true;
									break;
								}
							}
						}else if(netDiskPemission.getSelectObject().equals("角色")){
							RoleVO role = netDiskPemission.getRole();
							for (Iterator<UserVO> iterator = role.getUsers().iterator(); iterator.hasNext();) {
								UserVO user = iterator.next();
								if(user.getId().equals(userid) || superUserProcess.doView(userid)!=null){
									flag = true;
									break;
								}
							}
						}else if(netDiskPemission.getSelectObject().equals("组")){
							NetDiskGroup group = netDiskPemission.getGroup();
							if(group.getUseridGroup().indexOf(userid)!=-1 || superUserProcess.doView(userid)!=null){
								flag = true;
							}
						}
						if(flag){
							sb.append(true);
							sb.append("\",\"operate\":\"");
							sb.append(operate+","+netDiskPemission.getOperate());
						}else{
							sb.append(true);
							sb.append("\",\"operate\":\"");
							sb.append(operate);
						}
					}else{
						sb.append(netDiskFile.getPemission()!=null);
						sb.append("\",\"operate\":\"");
						sb.append(operate);
					}
					sb.append("\",\"pemissionid\":\"");
					sb.append(netDiskFile.getPemission()!=null?netDiskFile.getPemission().getId():"");
					sb.append("\"},");
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(datapackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		//System.out.println("getFiles:"+sb.toString());
		return sb.toString();
	}
	
	//迭代获得相应共享文件
	public String getShareFiles(String userid,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			
			ParamsTable params = new ParamsTable();
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("sm_name", userName);
			params.setParameter("inn_pemission","11");
			params.setParameter("t_userid",userid);
			DataPackage<NetDiskFile> datapackage = netDiskFileProcess.doQuery(params);
			if(datapackage.rowCount>0){
				sb.append("{\"files\":[");
				for (Iterator<NetDiskFile> it = datapackage.getDatas().iterator(); it.hasNext();) {
					NetDiskFile netDiskFile = it.next();
					sb.append("{\"name\":\"");
					sb.append(netDiskFile.getName());
					sb.append("\",\"id\":\"");
					sb.append(netDiskFile.getId());
					sb.append("\",\"type\":\"");
					sb.append(netDiskFile.getType());
					sb.append("\",\"size\":\"");
					if(netDiskFile.getSize()<1024){
						sb.append((double)netDiskFile.getSize()+" B");
					}else if(netDiskFile.getSize()>=1024 && netDiskFile.getSize()<(1024*1024) ){
						sb.append((double)(netDiskFile.getSize()/1024)+" KB");
					}else if(netDiskFile.getSize()>=(1024*1024)){
						sb.append((double)(netDiskFile.getSize()/(1024*1024))+" M");
					}
					sb.append("\",\"modifyTime\":\"");
					sb.append(netDiskFile.getModifyTime());
					sb.append("\",\"shareTime\":\"");
					sb.append(netDiskFile.getShareTime());
					sb.append("\",\"fileWebPath\":\"");
					String temp = netDiskFile.getFolderWebPath().substring(netDiskFile.getFolderWebPath().indexOf("/")+1);
					temp = temp.substring(temp.indexOf("/")+1);
					if(temp.indexOf("/")==-1){
						sb.append("我的网盘[根目录]"+"/"+netDiskFile.getName());
					}else{
						temp = temp.substring(temp.indexOf("/"));
						sb.append("我的网盘[根目录]"+temp+"/"+netDiskFile.getName());
					}
					sb.append("\",\"share\":\"");
					sb.append(netDiskFile.getPemission()!=null);
					sb.append("\",\"pemissionid\":\"");
					sb.append(netDiskFile.getPemission()!=null?netDiskFile.getPemission().getId():"");
					sb.append("\"},");
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(datapackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		return sb.toString();
	}
	
	public static void main(String[] args){
		NetworkDisk net = new NetworkDisk();
		System.out.println(net.getuserShareFiles("11de-c13a-26b53fc4-a3db-1bc87eaaad4c",1,15,""));
	}
	
	
	//迭代获得相应好友共享文件
	public String getuserShareFiles(String userid,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			UserProcess userProcess = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			
			UserVO user = (UserVO)userProcess.doView(userid);
			
			ParamsTable params = new ParamsTable();
			params.setParameter("sm_name", userName);
			params.setParameter("inn_pemission","11");
			DataPackage<NetDiskFile> datapackage = netDiskFileProcess.doQuery(params);
			
			if(datapackage.rowCount>0){
				sb.append("{\"files\":[");
				for (Iterator<NetDiskFile> it = datapackage.getDatas().iterator(); it.hasNext();) {
					NetDiskFile netDiskFile = it.next();
					boolean flag = false;
					if(!netDiskFile.getPemission().getUserid().equals(userid) && netDiskFile.getPemission().getUsers()!=null){
						if(netDiskFile.getPemission().getUsers().indexOf(userid)!=1){
							flag = true;
						}
					}else if(!netDiskFile.getPemission().getUserid().equals(userid) && netDiskFile.getPemission().getDepartment()!=null){
						for (Iterator<DepartmentVO> iterator = user.getDepartments().iterator(); iterator.hasNext();) {
							DepartmentVO department = iterator.next();
							if(netDiskFile.getPemission().getDepartment().getId().equals(department.getId())){
								flag = true;
								break;
							}
						}
					}else if(!netDiskFile.getPemission().getUserid().equals(userid) && netDiskFile.getPemission().getRole()!=null){
						for (Iterator<RoleVO> iterator = user.getRoles().iterator(); iterator.hasNext();) {
							RoleVO role = iterator.next();
							if(role.getId().equals(netDiskFile.getPemission().getRole().getId())){
								flag = true;
								break;
							}
						}
					}else if(!netDiskFile.getPemission().getUserid().equals(userid) && netDiskFile.getPemission().getGroup()!=null){
						if(netDiskFile.getPemission().getGroup().getUseridGroup().indexOf(userid)!=-1){
							flag = true;
						}
					}
					if(flag){
						sb.append("{\"name\":\"");
						sb.append(netDiskFile.getName());
						sb.append("\",\"id\":\"");
						sb.append(netDiskFile.getId());
						sb.append("\",\"type\":\"");
						sb.append(netDiskFile.getType());
						sb.append("\",\"size\":\"");
						if(netDiskFile.getSize()<1024){
							sb.append((double)netDiskFile.getSize()+" B");
						}else if(netDiskFile.getSize()>=1024 && netDiskFile.getSize()<(1024*1024) ){
							sb.append((double)(netDiskFile.getSize()/1024)+" KB");
						}else if(netDiskFile.getSize()>=(1024*1024)){
							sb.append((double)(netDiskFile.getSize()/(1024*1024))+" M");
						}
						sb.append("\",\"modifyTime\":\"");
						sb.append(netDiskFile.getModifyTime());
						sb.append("\",\"shareTime\":\"");
						sb.append(netDiskFile.getShareTime());
						sb.append("\",\"userName\":\"");
						if(netDiskFile.getUserid()!=null){
							sb.append(userProcess.doView(netDiskFile.getUserid()) !=null?((UserVO)userProcess.doView(netDiskFile.getUserid())).getName():"匿名");
						}else{
							sb.append("匿名");
						}
						sb.append("\",\"folderPath\":\"");
						sb.append(netDiskFile.getFolderPath());
						sb.append("\",\"fileWebPath\":\"");
						sb.append(netDiskFile.getFolderWebPath()+"/"+netDiskFile.getName());
						sb.append("\",\"share\":\"");
						sb.append(netDiskFile.getPemission()!=null);
						if(netDiskFile.getPemission()!=null){
							sb.append("\",\"operate\":\"");
							sb.append(netDiskFile.getPemission().getOperate());
						}
						sb.append("\"},");
					}
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(datapackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		return sb.toString();
	}
	
	
	//获得网盘基本信息
	public String getInfo(String userid){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskProcess netDiskProcess = (NetDiskProcess)ProcessFactory.createProcess(NetDiskProcess.class);
					NetDisk netDisk = (NetDisk)netDiskProcess.doView(userid);
					if(netDisk!=null){
						sb.append("{\"id\":\"");
						sb.append(netDisk.getId());
						sb.append("\",\"totalSizeLabel\":\"");
						if(netDisk.getTotalSize()<1024){
							sb.append(netDisk.getTotalSize()+" B");
						}else if(netDisk.getTotalSize()>=1024 && netDisk.getTotalSize()<(1024*1024) ){
							sb.append(df.format(netDisk.getTotalSize()/1024)+" KB");
						}else if(netDisk.getTotalSize()>=(1024*1024)){
							sb.append(df.format(netDisk.getTotalSize()/((1024*1024)))+" M");
						}
						sb.append("\",\"haveUseSizeLabel\":\"");
						if(netDisk.getHaveUseSize()<1024){
							sb.append(netDisk.getHaveUseSize()+" B");
						}else if(netDisk.getHaveUseSize()>=1024 && netDisk.getHaveUseSize()<(1024*1024) ){
							sb.append(df.format(netDisk.getHaveUseSize()/1024)+" KB");
						}else if(netDisk.getHaveUseSize()>=(1024*1024)){
							sb.append(df.format(netDisk.getHaveUseSize()/((1024*1024)))+" M");
						}
						sb.append("\",\"canUseSizeLabel\":\"");
						if((netDisk.getTotalSize()-netDisk.getHaveUseSize())<1024){
							sb.append(netDisk.getTotalSize()-netDisk.getHaveUseSize()+" B");
						}else if((netDisk.getTotalSize()-netDisk.getHaveUseSize())>=1024 && (netDisk.getTotalSize()-netDisk.getHaveUseSize())<(1024*1024) ){
							sb.append(df.format((netDisk.getTotalSize()-netDisk.getHaveUseSize())/1024)+" KB");
						}else if((netDisk.getTotalSize()-netDisk.getHaveUseSize())>=(1024*1024)){
							long temp = netDisk.getTotalSize()-netDisk.getHaveUseSize();
							float result = temp/1024;
							result = result/1024;
							sb.append(df.format(result)+" M");
						}
						sb.append("\",\"totalSize\":\"");
						sb.append(netDisk.getTotalSize());
						sb.append("\",\"haveUseSize\":\"");
						sb.append(netDisk.getHaveUseSize());
						sb.append("\",\"uploadSize\":\"");
						sb.append(netDisk.getUploadSize());
						sb.append("\",\"pemission\":\"");
						sb.append(netDisk.getPemission()==null?"false":netDisk.getPemission());
						sb.append("\"}");
					}else{
						sb.append("{\"pemission\":\"false\"}");
					}
					//System.out.println("getInfo:"+sb.toString());
					return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	//获得用户列表
	public String getAllUser(String userid,String domainid,String userids,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			UserProcess userProcess = (UserProcess)ProcessFactory.createProcess(UserProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_domainid", domainid);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			if(userName!=null && !userName.equals("")){
				params.setParameter("sm_name", userName);
			}
			DataPackage<UserVO> dataPackage = userProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				sb.append("{\"users\":[");
				for (Iterator<UserVO> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					UserVO userVO = it.next();
					if(!userVO.getId().equals(userid)){
						sb.append("{\"id\":\"");
						sb.append(userVO.getId());
						sb.append("\",\"name\":\"");
						sb.append(userVO.getName());
						sb.append("\",\"selected\":");
						if(userids.equals(userVO.getId()) || userids.indexOf(userVO.getId())!=-1){
							sb.append("true},");
						}else{
							sb.append("false},");
						}
					}
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(dataPackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	//获得部门
	public String getAllDepartment(String domainid,String departments,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			DepartmentProcess departmentProcess = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_domainid", domainid);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("sm_name", userName);
			DataPackage<DepartmentVO> dataPackage = departmentProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				sb.append("{\"departments\":[");
				for (Iterator<DepartmentVO> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					DepartmentVO departmentVO = it.next();
					sb.append("{\"id\":\"");
					sb.append(departmentVO.getId());
					sb.append("\",\"name\":\"");
					sb.append(departmentVO.getName());
					sb.append("\",\"selected\":");
					if(departments.equals(departmentVO.getId())){
						sb.append("true},");
					}else{
						sb.append("false},");
					}
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(dataPackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println("getAllDepartment:"+sb.toString());
		return sb.toString();
	}
	
	
	//获得角色
	public String getAllRole(String applicationid,String roles,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			RoleProcess roleProcess = (RoleProcess)ProcessFactory.createProcess(RoleProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_applicationid",applicationid);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("sm_name", userName);
			DataPackage<RoleVO> dataPackage = roleProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				sb.append("{\"roles\":[");
				for (Iterator<RoleVO> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					RoleVO roleVO = it.next();
					sb.append("{\"id\":\"");
					sb.append(roleVO.getId());
					sb.append("\",\"name\":\"");
					sb.append(roleVO.getName());
					sb.append("\",\"selected\":");
					if(roles.equals(roleVO.getId())){
						sb.append("true},");
					}else{
						sb.append("false},");
					}
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(dataPackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	//获得组
	public String getAllGroup(String userid,String groups,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskGroupProcess netDiskGroupProcess = (NetDiskGroupProcess)ProcessFactory.createProcess(NetDiskGroupProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_userid",userid);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("sm_name", userName);
			DataPackage<NetDiskGroup> dataPackage = netDiskGroupProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				sb.append("{\"groups\":[");
				for (Iterator<NetDiskGroup> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					NetDiskGroup netDiskGroup = it.next();
					sb.append("{\"id\":\"");
					sb.append(netDiskGroup.getId());
					sb.append("\",\"name\":\"");
					sb.append(netDiskGroup.getName());
					sb.append("\",\"description\":\"");
					sb.append(netDiskGroup.getDescription());
					sb.append("\",\"useridGroup\":\"");
					sb.append(netDiskGroup.getUseridGroup());
					sb.append("\",\"selected\":");
					if(groups.equals(netDiskGroup.getId())){
						sb.append("true},");
					}else{
						sb.append("false},");
					}
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(dataPackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	//获得单个组
	public String getGroup(String id){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskGroupProcess netDiskGroupProcess = (NetDiskGroupProcess)ProcessFactory.createProcess(NetDiskGroupProcess.class);
			NetDiskGroup netDiskGroup = (NetDiskGroup)netDiskGroupProcess.doView(id);
			sb.append("{\"id\":\"");
			sb.append(netDiskGroup.getId());
			sb.append("\",\"name\":\"");
			sb.append(netDiskGroup.getName());
			sb.append("\",\"description\":\"");
			sb.append(netDiskGroup.getDescription());
			sb.append("\",\"useridGroup\":\"");
			sb.append(netDiskGroup.getUseridGroup());
			sb.append("\"}");
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		return sb.toString();
	}
	
	//保存组
	public String saveGroup(String id,String name,String desc,String userid,String useridGroup){
		try{
			NetDiskGroupProcess netDiskGroupProcess = (NetDiskGroupProcess)ProcessFactory.createProcess(NetDiskGroupProcess.class);
			NetDiskGroup netDiskGroup = null;
			if(id == null || id.equals("")){
				netDiskGroup = new NetDiskGroup();
				netDiskGroup.setName(name);
				netDiskGroup.setDescription(desc);
				netDiskGroup.setUserid(userid);
				netDiskGroup.setUseridGroup(useridGroup);
				netDiskGroupProcess.doCreate(netDiskGroup);
				return "{\"icon\":\"assets/ok.png\",\"message\":\"新建组成功\"}";
			}else{
				netDiskGroup = (NetDiskGroup)netDiskGroupProcess.doView(id);
				netDiskGroup.setName(name);
				netDiskGroup.setDescription(desc);
				netDiskGroup.setUseridGroup(useridGroup);
				netDiskGroupProcess.doUpdate(netDiskGroup);
				return "{\"icon\":\"assets/ok.png\",\"message\":\"更新组成功\"}";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		
	}
	
	//删除组
	public String deleteGroup(String id,String userid){
		try{
			NetDiskPemissionProcess netDiskPemissionProcess = (NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
			NetDiskGroupProcess netDiskGroupProcess = (NetDiskGroupProcess)ProcessFactory.createProcess(NetDiskGroupProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_userid",userid);
			params.setParameter("t_type", "group");
			params.setParameter("t_group.id",id);
			DataPackage<NetDiskPemission> dataPackage = netDiskPemissionProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				for (Iterator<NetDiskPemission> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					NetDiskPemission netDiskPemission = it.next();
					if(netDiskPemission.getGroup()!=null){
						netDiskPemission.setGroup(null);
						netDiskPemissionProcess.doUpdate(netDiskPemission);
					}
				}
			}
			netDiskGroupProcess.doRemove(id);
			return "{\"icon\":\"assets/ok.png\",\"message\":\"删除组成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	//设置共享
	public String setShare(String fileid,String pemessionid){
		try{
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			NetDiskFile netDiskFile =(NetDiskFile)netDiskFileProcess.doView(fileid);
			NetDiskPemissionProcess netDiskPemissionProcess =(NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
			NetDiskPemission netDiskPemission = (NetDiskPemission)netDiskPemissionProcess.doView(pemessionid);
			netDiskFile.setPemission(netDiskPemission);
			netDiskFile.setShareTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			netDiskFileProcess.doUpdate(netDiskFile);
			return "{\"icon\":\"assets/ok.png\",\"message\":\"设置共享成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	//撤销共享
	public String cancelShare(String fileid){
		try{
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			NetDiskFile netDiskFile =(NetDiskFile)netDiskFileProcess.doView(fileid);
			netDiskFile.setPemission(null);
			netDiskFile.setShareTime(null);
			netDiskFileProcess.doUpdate(netDiskFile);
			return "{\"icon\":\"assets/ok.png\",\"message\":\"撤销共享成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	//删除文件
	public String deleteNetDiskFile(String userid,String fileid){
		try{
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			NetDiskFile netDiskFile =(NetDiskFile)netDiskFileProcess.doView(fileid);
			File file = new File(netDiskFile.getFolderPath()+"\\"+netDiskFile.getName());
			if(file.exists()){
				if(!file.delete())
					throw new IOException("delete file '" + file.getAbsolutePath() + "' failed!");
			}
			
			if(userid!=null&&!userid.equals("")&&netDiskFile.getFolderPath().indexOf("public")==-1){
				NetDiskProcess netDiskProcess = (NetDiskProcess)ProcessFactory.createProcess(NetDiskProcess.class);
				NetDisk netDisk = (NetDisk)netDiskProcess.doView(userid);
				netDisk.setHaveUseSize(netDisk.getHaveUseSize()-netDiskFile.getSize());
				netDiskProcess.doUpdate(netDisk);
			}
			netDiskFileProcess.doRemove(netDiskFile);
			return "{\"icon\":\"assets/ok.png\",\"message\":\"删除文件成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	
	//获得权限列表
	public String getPemission(String userid,String pemission,int _currpage,int _pagelines,String userName){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskPemissionProcess netDiskPemissionProcess = (NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_userid",userid);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			DataPackage<NetDiskPemission> dataPackage = netDiskPemissionProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				sb.append("{\"pemission\":[");
				for (Iterator<NetDiskPemission> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					NetDiskPemission netDiskPemission = it.next();
					sb.append("{\"id\":\"");
					sb.append(netDiskPemission.getId());
					sb.append("\",\"name\":\"");
					sb.append(netDiskPemission.getName());
					sb.append("\",\"type\":\"");
					if(netDiskPemission.getType().equals("folder")){
						sb.append("文件夹");
					}else{
						sb.append("文件");
					}
					sb.append("\",\"selectObject\":\"");
					if(netDiskPemission.getSelectObject().equals("用户")){
						sb.append("用户");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getUsers()!=null?netDiskPemission.getUsers():"");
					}else if(netDiskPemission.getSelectObject().equals("部门")){
						sb.append("部门");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getDepartment()!=null?netDiskPemission.getDepartment().getId():"");
					}else if(netDiskPemission.getSelectObject().equals("角色")){
						sb.append("角色");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getRole()!=null?netDiskPemission.getRole().getId():"");
					}else if(netDiskPemission.getSelectObject().equals("组")){
						sb.append("组");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getGroup()!=null?netDiskPemission.getGroup().getId():"");
					}
					sb.append("\",\"operate\":\"");
					sb.append(netDiskPemission.getOperate());
					//获得已设置权限文件夹
					sb.append("\",\"selected\":");
					if(pemission.indexOf(netDiskPemission.getId())!=-1){
						sb.append("true");
					}else{
						sb.append("false");
					}
					sb.append("},");
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(dataPackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		return sb.toString();
	}
	
	
	//创建权限
	public String createPemission(String id,String userid,String name,String type,String selectObject,String pemission,String operate){
		try{
			NetDiskPemission netDiskPemission = null;
			NetDiskPemissionProcess netDiskPemissionProcess = (NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
//Update XGY 2012.11.29
			if(id.equals("")){
				netDiskPemission = new NetDiskPemission();
				netDiskPemission.setUserid(userid);
				netDiskPemission.setName(name);
				netDiskPemission.setType(type);
				netDiskPemission.setSelectObject(selectObject);
				if(selectObject.equals("用户")){
					netDiskPemission.setUsers(pemission);
				}else if(selectObject.equals("部门")){
					DepartmentProcess departmentProcess = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
					DepartmentVO department = (DepartmentVO)departmentProcess.doView(pemission);
					netDiskPemission.setDepartment(department);
				}else if(selectObject.equals("角色")){
					RoleProcess roleProcess = (RoleProcess)ProcessFactory.createProcess(RoleProcess.class);
					RoleVO role = (RoleVO)roleProcess.doView(pemission);
					netDiskPemission.setRole(role);
				}else if(selectObject.equals("组")){
					NetDiskGroupProcess netDiskGroupProcess = (NetDiskGroupProcess)ProcessFactory.createProcess(NetDiskGroupProcess.class);
					NetDiskGroup group = (NetDiskGroup)netDiskGroupProcess.doView(pemission);
					netDiskPemission.setGroup(group);
				}
				netDiskPemission.setOperate(operate);
				netDiskPemissionProcess.doCreate(netDiskPemission);
				return "{\"icon\":\"assets/ok.png\",\"message\":\"创建权限成功\"}";
			}else{
				netDiskPemission = (NetDiskPemission)netDiskPemissionProcess.doView(id);
				if(!name.equals("")){
					netDiskPemission.setName(name);
				}
				if(!type.equals("")){
					netDiskPemission.setType(type);
				}
				if(!selectObject.equals("")){
					netDiskPemission.setSelectObject(selectObject);
				}
				if(!pemission.equals("")){
					if(netDiskPemission.getSelectObject().equals("用户")){
						netDiskPemission.setUsers(pemission);
					}else if(netDiskPemission.getSelectObject().equals("部门")){
						DepartmentProcess departmentProcess = (DepartmentProcess)ProcessFactory.createProcess(DepartmentProcess.class);
						DepartmentVO department = (DepartmentVO)departmentProcess.doView(pemission);
						netDiskPemission.setDepartment(department);
					}else if(netDiskPemission.getSelectObject().equals("角色")){
						RoleProcess roleProcess = (RoleProcess)ProcessFactory.createProcess(RoleProcess.class);
						RoleVO role = (RoleVO)roleProcess.doView(pemission);
						netDiskPemission.setRole(role);
					}else if(netDiskPemission.getSelectObject().equals("组")){
						NetDiskGroupProcess netDiskGroupProcess = (NetDiskGroupProcess)ProcessFactory.createProcess(NetDiskGroupProcess.class);
						NetDiskGroup group = (NetDiskGroup)netDiskGroupProcess.doView(pemission);
						netDiskPemission.setGroup(group);
					}
				}
				if(!operate.equals("")){
					netDiskPemission.setOperate(operate);
				}
				netDiskPemissionProcess.doUpdate(netDiskPemission);
				return "{\"icon\":\"assets/ok.png\",\"message\":\"更新权限成功\"}";
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	//删除权限
	public String delPemission(String userid,String id){
		try{
			ParamsTable params = new ParamsTable();
			params.setParameter("t_userid", userid);
			params.setParameter("t_pemission.id", id);
			NetDiskFileProcess netDiskFileProcess = (NetDiskFileProcess)ProcessFactory.createProcess(NetDiskFileProcess.class);
			DataPackage<NetDiskFile> datapackage = netDiskFileProcess.doQuery(params);
			for (Iterator<NetDiskFile> it = datapackage.getDatas().iterator(); it.hasNext();) {
				NetDiskFile netDiskFile = it.next();
				netDiskFile.setPemission(null);
				netDiskFileProcess.doUpdate(netDiskFile);
			}
			NetDiskFolderProcess netDiskFolderProcess = (NetDiskFolderProcess)ProcessFactory.createProcess(NetDiskFolderProcess.class);
			DataPackage<NetDiskFolder> datapackage1 = netDiskFolderProcess.doQuery(params);
			for (Iterator<NetDiskFolder> it = datapackage1.getDatas().iterator(); it.hasNext();) {
				NetDiskFolder netDiskFolder = it.next();
				netDiskFolder.setPemission(null);
				netDiskFileProcess.doUpdate(netDiskFolder);
			}
			NetDiskPemissionProcess netDiskPemissionProcess = (NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
			netDiskPemissionProcess.doRemove(id);
			return "{\"icon\":\"assets/ok.png\",\"message\":\"删除权限成功\"}";
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
	}
	
	//后台获得权限列表  kharry 
	public String getPemissionAdmin(String userid,String pemission,int _currpage,int _pagelines,String userName,String type){
		StringBuffer sb = new StringBuffer();
		try{
			NetDiskPemissionProcess netDiskPemissionProcess = (NetDiskPemissionProcess)ProcessFactory.createProcess(NetDiskPemissionProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_userid",userid);
			params.setParameter("_currpage", _currpage);
			params.setParameter("_pagelines", _pagelines);
			params.setParameter("t_type", type);
			params.setParameter("sm_name", userName);
			DataPackage<NetDiskPemission> dataPackage = netDiskPemissionProcess.doQuery(params);
			if(dataPackage.rowCount>0){
				sb.append("{\"pemission\":[");
				for (Iterator<NetDiskPemission> it = dataPackage.getDatas().iterator(); it.hasNext();) {
					NetDiskPemission netDiskPemission = it.next();
					sb.append("{\"id\":\"");
					sb.append(netDiskPemission.getId());
					sb.append("\",\"name\":\"");
					sb.append(netDiskPemission.getName());
					sb.append("\",\"type\":\"");
					if(netDiskPemission.getType().equals("folder")){
						sb.append("文件夹");
					}else{
						sb.append("文件");
					}
					sb.append("\",\"selectObject\":\"");
					if(netDiskPemission.getSelectObject().equals("用户")){
						sb.append("用户");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getUsers()!=null?netDiskPemission.getUsers():"");
					}else if(netDiskPemission.getSelectObject().equals("部门")){
						sb.append("部门");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getDepartment()!=null?netDiskPemission.getDepartment().getId():"");
					}else if(netDiskPemission.getSelectObject().equals("角色")){
						sb.append("角色");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getRole()!=null?netDiskPemission.getRole().getId():"");
					}else if(netDiskPemission.getSelectObject().equals("组")){
						sb.append("组");
						sb.append("\",\"pemission\":\"");
						sb.append(netDiskPemission.getGroup()!=null?netDiskPemission.getGroup().getId():"");
					}
					sb.append("\",\"operate\":\"");
					sb.append(netDiskPemission.getOperate());
					//获得已设置权限文件夹
					sb.append("\",\"selected\":");
					if(pemission.indexOf(netDiskPemission.getId())!=-1){
						sb.append("true");
					}else{
						sb.append("false");
					}
					sb.append("},");
				}
				if(sb.lastIndexOf(",")!=-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				sb.append("],\"totalPages\":\""+getTotalPages(dataPackage.rowCount,_pagelines)+"\"}");
			}
		}catch(Exception e){
			e.printStackTrace();
			return "{\"icon\":\"assets/warning.png\",\"message\":\""+e.getMessage()+"\"}";
		}
		return sb.toString();
	}

}
