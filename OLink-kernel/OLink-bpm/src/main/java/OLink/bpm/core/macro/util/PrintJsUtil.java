package OLink.bpm.core.macro.util;

import java.io.IOException;

import OLink.bpm.constans.Environment;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

public class PrintJsUtil {
	private static final Logger log = Logger.getLogger(PrintJsUtil.class);
	private String sessionid;

	public PrintJsUtil(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getFileName() {
		Environment env = Environment.getInstance();
		String path = env.getRealPath("/logs/iscript/");
		String fileName = path + "/" + sessionid + ".log";

		return fileName;
	}

	public void println(Object obj) throws IOException {
		log.info("JsUtil:"+obj);
		StringUtil.printlnToFile(getFileName(), "JsUtil:" + obj);
	}

	public void println(int obj) throws IOException {
		log.info("JsUtil:"+obj);
		StringUtil.printlnToFile(getFileName(), "JsUtil:" + obj);
	}

	public void println(long obj) throws IOException {
		log.info("JsUtil:"+obj);
		StringUtil.printlnToFile(getFileName(), "JsUtil:" + obj);
	}

	public void println(double obj) throws IOException {
		log.info("JsUtil:"+obj);
		StringUtil.printlnToFile(getFileName(), "JsUtil:" + obj);
	}

	public void println(float obj) throws IOException {
		log.info("JsUtil:"+obj);
		StringUtil.printlnToFile(getFileName(), "JsUtil:" + obj);
	}

	public void println(char obj) throws IOException {
		log.info("JsUtil:"+obj);
		StringUtil.printlnToFile(getFileName(), "JsUtil:" + obj);
	}

	public void print(Object obj) {
		log.info("JsUtil:"+obj);
	}

	public void print(int obj) {
		log.info("JsUtil:"+obj);
	}

	public void print(long obj) {
		log.info("JsUtil:"+obj);
	}

	public void print(double obj) {
		log.info("JsUtil:"+obj);
	}

	public void print(float obj) {
		log.info("JsUtil:"+obj);
	}

	public void print(char obj) {
		log.info("JsUtil:"+obj);
	}

	public void errorln(Object obj) {
		System.err.println("JsUtil:" + obj);
	}

	public void errorln(int obj) {
		System.err.println("JsUtil:" + obj);
	}

	public void errorln(long obj) {
		System.err.println("JsUtil:" + obj);
	}

	public void errorln(double obj) {
		System.err.println("JsUtil:" + obj);
	}

	public void errorln(float obj) {
		System.err.println("JsUtil:" + obj);
	}

	public void errorln(char obj) {
		System.err.println("JsUtil:" + obj);
	}

	public void error(Object obj) {
		System.err.print("JsUtil:" + obj);
	}

	public void error(int obj) {
		System.err.print("JsUtil:" + obj);
	}

	public void error(long obj) {
		System.err.print("JsUtil:" + obj);
	}

	public void error(double obj) {
		System.err.print("JsUtil:" + obj);
	}

	public void error(float obj) {
		System.err.print("JsUtil:" + obj);
	}

	public void error(char obj) {
		System.err.print("JsUtil:" + obj);
	}

}
