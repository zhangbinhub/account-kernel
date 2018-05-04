package OLink.bpm.util.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {
	public final static int BUFFER_SIZE = 10240;

	/**
	 * 
	 * @param srcFolder
	 * @param destZipFile
	 * @throws Exception
	 */
	public static void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip);
		zip.flush();
		zip.close();
	}

	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}

			in.close();
		}
	}

	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
		File folder = new File(srcFolder);

		String[] fileNames = folder.list();
		for (int i = 0; i < fileNames.length; i++) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder + "/" + fileNames[i], zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileNames[i], zip);
			}
		}
	}

	/**
	 * 
	 * @param archiveFile
	 * @param tobeZippedFiles
	 * @throws Exception
	 */
	public static void createZipArchive(File archiveFile, File[] tobeZippedFiles) throws Exception {
		try {
			byte buffer[] = new byte[BUFFER_SIZE];
			// Open archive file
			FileOutputStream stream = new FileOutputStream(archiveFile);
			ZipOutputStream out = new ZipOutputStream(stream);
			//out.setEncoding(Charset.forName("UTF-8").name());

			for (int i = 0; i < tobeZippedFiles.length; i++) {
				if (tobeZippedFiles[i] == null || !tobeZippedFiles[i].exists() || tobeZippedFiles[i].isDirectory())
					continue;

				// Add archive entry
				// String fileName =
				// StringUtil.toUTFBody(tobeZippedFiles[i].getName());
				ZipEntry zipAdd = new ZipEntry(tobeZippedFiles[i].getName());
				zipAdd.setTime(tobeZippedFiles[i].lastModified());
				out.putNextEntry(zipAdd);

				// Read input & write to output
				FileInputStream in = new FileInputStream(tobeZippedFiles[i]);
				while (true) {
					int nRead = in.read(buffer, 0, buffer.length);
					if (nRead <= 0)
						break;
					out.write(buffer, 0, nRead);
				}
				in.close();
			}

			out.flush();
			out.close();
			stream.close();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 
	 * @param newFileName
	 *            压缩文件名称
	 * @param inputFileName
	 *            要压缩文件的路径
	 * @param destDir
	 *            压缩文件存放目录
	 * @throws Exception
	 */
	public static void compressFiles(String newFileName, String inputFilePath, String destDir) throws Exception {
		compressFiles(newFileName, new String[] { inputFilePath }, destDir);
	}

	public static void compressFiles(String newFileName, String[] inputFilePaths, String destDir) throws Exception {
		String zipPathName = destDir + "/" + newFileName + ".zip";

		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPathName));

		for (int i = 0; i < inputFilePaths.length; i++) {
			File inputFile = new File(inputFilePaths[i]);

			if (inputFile.exists() && inputFile.isFile()) {
				FileInputStream in = new FileInputStream(inputFile);

				zipOut.putNextEntry(new ZipEntry(inputFile.getName()));

				int nNumber;
				byte[] buffer = new byte[512];
				while ((nNumber = in.read(buffer)) != -1) {
					zipOut.write(buffer, 0, nNumber);
				}
				in.close();
			}
		}
		zipOut.close();
	}

	/**
	 * 默认以UTF-8方式读取文件
	 * 
	 * @param fileName
	 *            文件全名
	 * @return 文件内容的数组
	 * @throws IOException
	 */
	public static String[] readZipFile(String fileName) throws IOException {
		return readZipFile(fileName, "UTF-8");
	}

	public static String[] readZipFile(String fileName, String charsetName) throws IOException {
		return readZipFile(new File(fileName), charsetName);
	}

	public static String[] readZipFile(File zipFile) throws IOException {
		return readZipFile(zipFile, "UTF-8");
	}

	public static String[] readZipFile(File zipFile, String charsetName) throws IOException {
		Collection<String> contentList = new ArrayList<String>();
		ZipFile zip = new ZipFile(zipFile);
		if (zip != null) {
			// 建立与目标文件的输入连接
			Enumeration<?> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				//String fileName = entry.getName();
				InputStream in = zip.getInputStream(entry);
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				int nNumber;
				byte[] buffer = new byte[512];
				while ((nNumber = in.read(buffer)) != -1) {
					out.write(buffer, 0, nNumber);
				}
				contentList.add(out.toString(charsetName));

				in.close();
				out.close();
			}
		}

		return contentList.toArray(new String[contentList.size()]);
	}

	public static void decompress(String inputFileName) throws Exception {
		decompress(new File(inputFileName), "");
	}

	/**
	 * 解压zip文件到指定目录
	 * 
	 * @param infile
	 *            要解压的文件
	 * @param destDir
	 *            目标目录
	 * @throws Exception
	 */
	public static void decompress(File infile, String destDir) throws Exception {
		// 检查是否是ZIP文件
		ZipFile zip = new ZipFile(infile);
		if (zip != null) {
			// 建立与目标文件的输入连接
			Enumeration<?> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String fileName = entry.getName();
				InputStream in = zip.getInputStream(entry);

				File dir = new File(destDir);
				if (!dir.exists()) {
					if(!dir.mkdirs())
						throw new IOException("create directory '" + destDir + "' failed!");
				}

				String fullPath = destDir + "/" + fileName;
				FileOutputStream out = new FileOutputStream(fullPath);

				int nNumber;
				byte[] buffer = new byte[512];
				while ((nNumber = in.read(buffer)) != -1) {
					out.write(buffer, 0, nNumber);
				}

				in.close();
				out.close();
			}
		}
	}

	/**
	 * 根据扩展名获取zip中的文件
	 * 
	 * @param extension
	 * @throws IOException
	 */
	public static File getFileByExtension(ZipFile zipFile, String extension) throws IOException {
		Enumeration<?> entries = zipFile.getEntries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if (entry.getName().indexOf("." + extension) != -1) {
				InputStream in = zipFile.getInputStream(entry);
				File rtn = new File(entry.getName());
				FileOutputStream out = new FileOutputStream(rtn);

				int nNumber;
				byte[] buffer = new byte[512];
				while ((nNumber = in.read(buffer)) != -1) {
					out.write(buffer, 0, nNumber);
				}

				in.close();
				out.close();

				return rtn;
			}
		}

		return null;
	}

	public static void main(String[] args) throws IOException {
	}
}
