package OLink.bpm.core.helper.search;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.*;
import java.util.Date;

import OLink.bpm.core.helper.html.HTMLParser;
import org.apache.lucene.document.*;

import eWAP.core.Tools;


/** A utility for making Lucene Documents for HTML documents. */

public class HTMLDocument {
	static char dirSep = System.getProperty("file.separator").charAt(0);

	public static String uid() {
		try {
			return Tools.getSequence();
		} catch (Exception e) {
			return Long.toString(new Date().getTime());
		}

	}

	public static Document document(File f) throws IOException, InterruptedException {
		// make a new, empty document
		Document doc = new Document();

		// Add the url as a field named "path". Use a field that is
		// indexed (i.e. searchable), but don't tokenize the field into words.
		doc.add(new Field("path", f.getPath().replace(dirSep, '/'), Field.Store.YES, Field.Index.NOT_ANALYZED));

		// Add the last modified date of the file a field named "modified".
		// Use a field that is indexed (i.e. searchable), but don't tokenize
		// the field into words.
		doc.add(new Field("modified", DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		// Add the uid as a field, so that index can be incrementally
		// maintained. This field is not stored with document, it is indexed,
		// but it is not tokenized prior to indexing.
		doc.add(new Field("id", uid(), Field.Store.YES, Field.Index.NOT_ANALYZED));

		FileInputStream fis = new FileInputStream(f);
		HTMLParser parser = new HTMLParser(fis);

		// Add the tag-stripped contents as a Reader-valued Text field so it
		// will get tokenized and indexed.
		doc.add(new Field("contents", parser.getReader()));

		// Add the summary as a field that is stored and returned with
		// hit documents for display.
		doc.add(new Field("summary", parser.getSummary(), Field.Store.YES, Field.Index.NO));

		// Add the title as a field that it can be searched and that is stored.
		doc.add(new Field("title", parser.getTitle(), Field.Store.YES, Field.Index.ANALYZED));

		// return the document
		return doc;
	}

	/**
	 * 
	 * @param root
	 *            web应用toc文件夹真实路径"<WEBAPP_HOME>/toc"
	 * @param f
	 *            文件真实路径
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static Document document(File root, File f) throws IOException, InterruptedException {
		// make a new, empty document
		Document doc = new Document();

		// Add the url as a field named "path". Use a field that is
		// indexed (i.e. searchable), but don't tokenize the field into words.
		doc.add(new Field("path", f.getPath().replace(dirSep, '/'), Field.Store.YES, Field.Index.NOT_ANALYZED));

		// 添加网络路径
		String webPath = f.getAbsolutePath().substring(root.getAbsolutePath().length());
		webPath = root.getName() + webPath.replace(dirSep, '/');
		// System.out.println("href: " + webPath);
		doc.add(new Field("href", webPath, Field.Store.YES, Field.Index.NOT_ANALYZED));

		// Add the last modified date of the file a field named "modified".
		// Use a field that is indexed (i.e. searchable), but don't tokenize
		// the field into words.
		doc.add(new Field("modified", DateTools.timeToString(f.lastModified(), DateTools.Resolution.MINUTE), Field.Store.YES,
				Field.Index.NOT_ANALYZED));

		// Add the uid as a field, so that index can be incrementally
		// maintained. This field is not stored with document, it is indexed,
		// but it is not tokenized prior to indexing.
		doc.add(new Field("id", uid(), Field.Store.YES, Field.Index.NOT_ANALYZED));

		FileInputStream fis = new FileInputStream(f);
		HTMLParser parser = new HTMLParser(fis);

		// Add the tag-stripped contents as a Reader-valued Text field so it
		// will get tokenized and indexed.
		doc.add(new Field("contents", parser.getReader()));

		// Add the summary as a field that is stored and returned with
		// hit documents for display.
		doc.add(new Field("summary", parser.getSummary(), Field.Store.YES, Field.Index.NO));

		// Add the title as a field that it can be searched and that is stored.
		doc.add(new Field("title", parser.getTitle(), Field.Store.YES, Field.Index.ANALYZED));

		// return the document
		return doc;
	}

	private HTMLDocument() {
	}
}
