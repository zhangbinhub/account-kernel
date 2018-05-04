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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import nl.justobjects.pushlet.util.Log;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Indexer for HTML files.
 * 
 * @author nicholas
 * 
 */
public class IndexHTML {

	private  boolean deleting = false; // true during deletion pass
	private  IndexReader reader; // existing index
	private  IndexWriter writer; // new index being built
	private  TermEnum uidIter; // document id iterator
	private  File root; // 根目录

	/**
	 * 
	 * @param indexDir
	 *            索引目录
	 * @param rootDir
	 *            被索引文件根目录
	 * @throws Exception
	 */
	public void createIndexForFiles(String indexDir, String rootDir) throws Exception {
		createIndexForFiles(indexDir, rootDir, false);
	}

	/**
	 * 
	 * @param indexDir
	 *            索引目录
	 * @param rootDir
	 *            被索引文件根目录
	 * @param create
	 *            是否创建
	 * @throws Exception
	 */
	public void createIndexForFiles(String indexDir, String rootDir, boolean create) throws Exception {
		root = new File(rootDir);
		File index = new File(indexDir);

		if (root == null) {
			System.err.println("Specify directory to index");
			return;
		}

		Date start = new Date();

		if (!create) { // delete stale docs
			deleting = true;
			indexDocs(root, index, create);
		}

		// 标准分词器
		// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		Analyzer analyzer = new IKAnalyzer(); // 中文分词器

		if (!index.exists()) { // 创建索引目录
			if(!index.mkdirs()){
				Log.warn("Failed to create folder ("+indexDir+")");
				throw new IOException("Failed to create folder ("+indexDir+")");
			}
		}

		writer = new IndexWriter(FSDirectory.open(index), analyzer, create, new IndexWriter.MaxFieldLength(1000000));
		indexDocs(root, index, create); // add new docs

		System.out.println("Optimizing index...");
		writer.optimize();
		writer.close();

		Date end = new Date();

		System.out.print(end.getTime() - start.getTime());
		System.out.println(" total milliseconds");
	}

	private void indexDocs(File file, File index, boolean create) throws Exception {
		if (!create) { // incrementally update
			reader = IndexReader.open(FSDirectory.open(index), false); // open
			// existing index
			uidIter = reader.terms(new Term("id", "")); // init id iterator

			indexDocs(file);

			if (deleting) { // delete rest of stale docs
				//Update By xgy 2012.11.29
				while (uidIter.term() != null && uidIter.term().field().equals("id")) {
					System.out.println("deleting " + uidIter.term().text());
					reader.deleteDocuments(uidIter.term());
					uidIter.next();
				}
				deleting = false;
			}

			uidIter.close(); // close uid iterator
			reader.close(); // close existing index
		} else
			// don't have exisiting
			indexDocs(file);
	}

	private void indexDocs(File file) throws Exception {
		if (file.isDirectory()) { // if a directory
			String[] files = file.list(); // list its files
			Arrays.sort(files); // sort the files
			for (int i = 0; i < files.length; i++)
				// recursively index them
				indexDocs(new File(file, files[i]));
		} else if (file.getPath().endsWith(".html") || // index .html files
				file.getPath().endsWith(".htm") || // index .htm files
				file.getPath().endsWith(".txt")) { // index .txt files

			if (uidIter != null) {
				String id = HTMLDocument.uid(); // construct uid for doc

				while (uidIter.term() != null && uidIter.term().field().equals("id") && uidIter.term().text().compareTo(id) < 0) {
					if (deleting) { // delete stale docs
						System.out.println("deleting " + uidIter.term().text());
						reader.deleteDocuments(uidIter.term());
					}
					uidIter.next();
				}

				if (uidIter.term() != null && uidIter.term().field().equals("id") && uidIter.term().text().compareTo(id) == 0) {
					uidIter.next(); // keep matching docs
				} else if (!deleting) { // add new docs
					Document doc = HTMLDocument.document(root, file);
					System.out.println("adding " + doc.get("path"));
					writer.addDocument(doc);
				}
			} else { // creating a new index
				Document doc = HTMLDocument.document(root, file);
				System.out.println("adding " + doc.get("path"));
				writer.addDocument(doc); // add docs unconditionally
			}
		}
	}

	/** Indexer for HTML files. */
	public static void main(String[] argv) {
		try {
			argv = new String[] { "-index", "D:\\java\\index", "-create", "D:\\java\\JDK_API_1_6_zh_CN" };

			String usage = "IndexHTML [-create] [-index <index>] <root_directory>";

			if (argv.length == 0) {
				System.err.println("Usage: " + usage);
				return;
			}

			String indexStr = "index";
			String rootStr = "";
			boolean create = false;

			for (int i = 0; i < argv.length; i++) {
				if (argv[i].equals("-index")) { // parse -index option
					indexStr = argv[++i];
				} else if (argv[i].equals("-create")) { // parse -create option
					create = true;
				} else if (i != argv.length - 1) {
					System.err.println("Usage: " + usage);
					return;
				} else
					rootStr = argv[i];
			}

			if (rootStr == null) {
				System.err.println("Specify directory to index");
				System.err.println("Usage: " + usage);
				return;
			}

			// 创建索引
			new IndexHTML().createIndexForFiles(indexStr, rootStr, create);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
