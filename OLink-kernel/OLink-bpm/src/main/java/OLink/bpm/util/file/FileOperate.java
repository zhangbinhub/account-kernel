package OLink.bpm.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import OLink.bpm.util.sequence.Sequence;
import org.apache.log4j.Logger;


/**
 * @author Administrator
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class FileOperate {
	private static final Logger log = Logger.getLogger(FileOperate.class);

	public static void createFileFolder(String fileDir) throws IOException {
		File dir = new File(fileDir);
		if (!dir.exists()) {
			if (!dir.mkdirs())
				throw new IOException("create 1.directory failed!");
		}
	}

	public static void copyFileToFolder(String fileName, String folderName) throws Exception {
		try {
			File file = new File(fileName);
			if (file.isFile()) {
				if (!folderName.endsWith("\\"))
					folderName += "\\";
				FileInputStream fis = new FileInputStream(file);
				byte[] fileContent = new byte[fis.available()];
				fis.read(fileContent);
				String path = folderName + file.getName();
				FileOutputStream fos = new FileOutputStream(path);
				fos.write(fileContent);
				fis.close();
				fos.close();
			} else {
				throw new Exception(fileName + " is not a file or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static void cutFileToFolder(String fileName, String folderName) throws Exception {
		try {
			File file = new File(fileName);
			if (file.isFile()) {
				copyFileToFolder(fileName, folderName);
				if (!file.delete())
					throw new IOException("delete file '" + fileName + "' failed!");
			} else {
				throw new Exception(fileName + " is not a file or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static void copyFolderFilesToOtherFolder(String oldFolderName, String folderName) throws Exception {
		try {
			StringBuffer strMsg = new StringBuffer();
			File file = new File(oldFolderName);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						try {
							copyFileToFolder(files[i].getPath(), folderName);
						} catch (Exception ee) {
							strMsg.append("copy file<<" + files[i].getPath() + ">> error,message:" + ee.getMessage()
									+ "\r\n");
						}
					}
				}
				if (!"".equals(strMsg.toString())) {
					throw new Exception(strMsg.toString());
				}
			} else {
				throw new Exception(oldFolderName + " is not a directory or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static void cutFolderFilesToOtherFolder(String oldFolderName, String folderName) throws Exception {
		try {
			StringBuffer strMsg = new StringBuffer();
			File file = new File(oldFolderName);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isFile()) {
						try {
							cutFileToFolder(files[i].getPath(), folderName);
						} catch (Exception ee) {
							strMsg.append("cut file<<" + files[i].getPath() + ">> error,message:" + ee.getMessage()
									+ "\r\n");
						}
					}
				}
				if (!"".equals(strMsg.toString())) {
					throw new Exception(strMsg.toString());
				}
			} else {
				throw new Exception(oldFolderName + " is not a directory or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static void deleteAllFilesInFolder(String folderName) throws Exception {
		try {
			// String strMsg = "";
			File file = new File(folderName);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (!files[i].delete())
						throw new IOException("delete file '" + folderName + "' failed!");
				}
			} else {
				throw new Exception(folderName + " is not a directory or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public static void writeFile(String fileFullName, String content, boolean overwrite) throws IOException {

		if (fileFullName != null) {
			String path = fileFullName.substring(0, fileFullName.lastIndexOf("/"));
			if (!(new File(path).isDirectory())) {
				if (!new File(path).mkdirs())
					throw new IOException("create directory '" + path + "'failed!");
			}
		}
		File file = new File(fileFullName);
		FileWriter writer = new FileWriter(file);
		if (overwrite) {
			writer.write(content);
		}
		writer.flush();
		writer.close();
	}

	public static void writeFileUTF(String fileFullName, String content, boolean overwrite) throws IOException {

		if (fileFullName != null) {
			String path = fileFullName.substring(0, fileFullName.lastIndexOf("/"));
			if (!(new File(path).isDirectory())) {
				if (!new File(path).mkdirs())
					throw new IOException("create directory '" + path + "'failed!");
			}
		}
		OutputStream os = new FileOutputStream(fileFullName);
		OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8");
		if (overwrite) {
			writer.write(content);
		}
		writer.flush();
		writer.close();
	}

	public static void writeFile(String fileFullName, InputStream in) throws Exception {
		if (fileFullName != null) {
			int index = fileFullName.lastIndexOf("/");
			index = index != -1 ? index : fileFullName.lastIndexOf("\\");
			// 获取文件目录，不存在则创建
			if (index != -1) {
				String path = fileFullName.substring(0, index);
				if (!(new File(path).isDirectory())) {
					if (!new File(path).mkdirs())
						throw new IOException("create directory '" + path + "'failed!");
				}
			}
		}

		FileOutputStream outputStream = new FileOutputStream(fileFullName);

		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) > 0) {
			outputStream.write(buffer, 0, len);
		}

		in.close();
		outputStream.close();
	}

	public static void writeFile(String fileFullName, File file) throws Exception {
		if (file != null) {
			FileInputStream fileIn = new FileInputStream(file);
			writeFile(fileFullName, fileIn);
		}
	}

	public static ArrayList<?> splitTxtToArray(String fileFullName) throws Exception {
		ArrayList<Object> txt = new ArrayList<Object>();
		File file = new File(fileFullName);
		BufferedReader in = new BufferedReader(new FileReader(file));
		String strLine = "";
		while ((strLine = in.readLine()) != null) {
			txt.add(strLine);
		}

		in.close();
		return txt;
	}

	public static ArrayList<?> getFolderAllFileName(String folderName) throws Exception {
		ArrayList<Object> fileNamelist = new ArrayList<Object>();
		try {
			// String strMsg = "";
			File file = new File(folderName);
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					fileNamelist.add(files[i].getName());
				}
			} else {
				throw new Exception(folderName + " is not a directory or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return fileNamelist;
	}

	public static final String getFileContentAsStringUTF(String fileFullName) throws Exception {
		File file = new File(fileFullName);
		StringBuffer sb = new StringBuffer();
		InputStreamReader input = new InputStreamReader(new FileInputStream(file), "utf-8");
		BufferedReader in = new BufferedReader(input);
		String strLine = "";
		while ((strLine = in.readLine()) != null) {
			sb.append(strLine);
			sb.append("\n");
		}

		input.close();
		in.close();
		return sb.toString();
	}

	public static final String getFileContentAsString(String fileFullName) throws Exception {
		File file = new File(fileFullName);
		StringBuffer sb = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(file));
		String strLine = "";
		while ((strLine = in.readLine()) != null) {
			sb.append(strLine);
			sb.append("\n");
		}
		in.close();
		return sb.toString();
	}

	public static File[] getAllFilesInFolder(String folderName) {
		return getAllFilesInFolderByExtension(folderName, "");
	}

	public static File[] getAllFilesInFolderByExtension(String folderName, String extension) {
		File dir = new File(folderName);
		if (dir.isDirectory()) {
			Collection<File> rtn = new ArrayList<File>();
			File[] files = dir.listFiles();

			if (extension != null && extension.trim().length() > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().indexOf("." + extension) != -1) {
						rtn.add(files[i]);
					}
				}
				return rtn.toArray(new File[rtn.size()]);
			} else {
				return files;
			}
		}

		return new File[0];
	}

	public static void convertFileEncoding(String path, String fromEncoding, String toEncoding) throws Exception {
		convertFileEncoding(new File(path), fromEncoding, toEncoding);
	}

	public static void convertFileEncoding(File file, String fromEncoding, String toEncoding) throws Exception {
//		String tempFile = System.getProperty("user.dir") + File.separator + "temp.java";
		String tempFile = System.getProperty("user.dir") + "/" + "temp.java";
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				convertFileEncoding(files[i], fromEncoding, toEncoding);
			}
		} else if (file.isFile() && file.getName().trim().toLowerCase().endsWith(".java")) {
			try {
				String command = "native2ascii -encoding " + fromEncoding + " " + file.getAbsolutePath() + " "
						+ tempFile;
				Process process = Runtime.getRuntime().exec(command);
				process.waitFor();
				command = "native2ascii -reverse -encoding " + toEncoding + " " + tempFile + " "
						+ file.getAbsolutePath();
				process = Runtime.getRuntime().exec(command);
				process.waitFor();
				log.info(file.getAbsolutePath() + " Execute Successed");
			} catch (Exception e) {
				log.info(file.getAbsolutePath() + " Execute Failed");
				throw e;
			} finally {
				File temp = new File(tempFile);
				if (temp.exists()) {
					if (!temp.delete())
						log.error("delete file 'c:/temp.java' failed!");
					throw new IOException("delete file 'c:/temp.java' failed!");
				} else {
					log.info("file 'c:/temp.java' not exist!");
				}
			}
		}
	}

	public static void copyFileToFolderAndRenameFile(String fileName, String newFileName, String newFolder)
			throws Exception {
		try {
			File file = new File(fileName);
			if (file.isFile()) {
				if (!newFolder.endsWith("\\"))
					newFolder += "\\";
				FileInputStream fis = new FileInputStream(file);
				byte[] fileContent = new byte[fis.available()];
				fis.read(fileContent);
				String path = newFolder + newFileName;
				FileOutputStream fos = new FileOutputStream(path);
				fos.write(fileContent);
				fis.close();
				fos.close();
			} else {
				throw new Exception(fileName + " is not a file or it is not exist!");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * 根据文件头获取文件真实类型
	 * 
	 * @param file
	 *            文件
	 * @return
	 * @throws IOException
	 */
	// 定义常用文件类型
	private static final HashMap<String, String> map = new HashMap<String, String>();
	static {
		map.put("FFD8FF", "jpg");
		map.put("89504E", "png");
		map.put("474946", "gif");
		map.put("49492A", "tif");
		map.put("424D", "bmp");
		map.put("414331", "dwg");
		map.put("384250", "psd");
		map.put("7B5C72", "rtf");
		map.put("3C3F78", "xml");
		map.put("68746D", "html");
		map.put("3C3F78", "eml");
		map.put("CFAD12", "dbx");
		map.put("214244", "pst");
		map.put("D0CF11", "xls/doc"); // office文件类型
		map.put("FF5750", "mdb");
		map.put("3C3F78", "wpd");
		map.put("252150", "eps/ps");
		map.put("255044", "wpd");
		map.put("3C3F78", "pdf");
		map.put("AC9EBD", "qdf");
		map.put("E38285", "pwl");
		map.put("504B03", "zip");
		map.put("526172", "rar");
		map.put("574156", "wav");
		map.put("415649", "avi");
		map.put("2E7261", "ram");
		map.put("2E524D", "rm");
		map.put("000001", "mpg");
		map.put("6D6F6F", "mov");
		map.put("3026B2", "asf");
		map.put("4D5468", "mid");
		map.put("000000", "txt");
		map.put("706163", "java");
	}

	public static String getFileType(InputStream im) throws IOException {
		String head = getFileHead(im).toUpperCase();
		String type = "";
		if (map.get(head) != null && !map.get(head).toString().equals("")) {
			type = map.get(head).toString();
		}
		return type;
	}

	public static String getFileHead(InputStream im) throws IOException {
		InputStream is = im;
		byte[] b = new byte[3];
		int len = is.read(b, 0, b.length);
		String t = len != -1 ? bytesToHexString(b) : "";

		return t;
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取当前文件夹下的所有文件夹
	 * 
	 * @param file
	 * @return
	 */
	public static Collection<File> deepSearchDirectory(File file) {
		Collection<File> fileList = new ArrayList<File>();
		fileList.add(file);
		File[] dir = file.listFiles(new DirectoryFileFilter());
		if(dir !=null ){
			for (int i = 0; i < dir.length; i++) {
				fileList.addAll(deepSearchDirectory(dir[i]));
			}
		}

		return fileList;
	}
	
	private static class DirectoryFileFilter implements FileFilter {
		public boolean accept(File pathname) {
			return pathname.isDirectory();

		}
	}
	
	public static void main(String[] args) {
		// System.out.println(System.getProperty("user.dir"));

		File file = new File("D:\\java\\workspace_workflow\\obpm\\src\\main\\webapp\\uploads");
		Collection<File> dirs = deepSearchDirectory(file);
		for (Iterator<File> iterator = dirs.iterator(); iterator.hasNext();) {
			File dir = iterator.next();
			System.out.println(Sequence.getFileUUID(dir, "uploads") +": " + dir.toString());
		}
	}
}
