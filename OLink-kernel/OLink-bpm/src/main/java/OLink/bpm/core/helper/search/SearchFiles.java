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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;

/** Simple command-line based search demo. */
public class SearchFiles {

	public DataPackage<SearchHit> doSearchByField(String field, String queryStr) throws IOException {
		return doSearchByField(field, queryStr, 1, 1000000);
	}

	/**
	 * 根据索引字段查询
	 * 
	 * @param field
	 *            索引的字段
	 * @param queryStr
	 *            查询内容
	 * @return
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	public DataPackage<SearchHit> doSearchByField(String field, String queryStr, int pageNo, int linesPerPage) throws IOException {
		IndexReader reader = null;
		try {
			String appRealPath = Environment.getInstance().getApplicationRealPath();
			String index = appRealPath + Web.INDEX_DIR;

			// only searching, so read-only=true
			reader = IndexReader.open(FSDirectory.open(new File(index)), true);

			Searcher searcher = new IndexSearcher(reader);
			// 在索引器中使用IKSimilarity相似度评估器
			searcher.setSimilarity(new IKSimilarity());

			// Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
			// analyzer = new IKAnalyzer(); // 中文分词器
			// QueryParser parser = new QueryParser(field, analyzer);

			// 根据某个字段进行查找
			// Query query = parser.parse(line);
			Query query = IKQueryParser.parse(field, queryStr); // 中文解析
			System.out.println("Searching for: " + query.toString(field));

			return doPagingSearch(searcher, query, pageNo, linesPerPage);
		} catch (CorruptIndexException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private DataPackage<SearchHit> doPagingSearch(Searcher searcher, Query query, int pageNo, int linesPerPage) throws IOException {
		DataPackage<SearchHit> dataPackage = new DataPackage<SearchHit>();

		long totallines = (long) pageNo * linesPerPage;

		TopScoreDocCollector collector = TopScoreDocCollector.create((int) totallines, false);
		searcher.search(query, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		dataPackage.setRowCount(collector.getTotalHits());
		int numTotalHits = collector.getTotalHits();
		System.out.println(numTotalHits + " total matching documents");

		int start = (int) Math.min(numTotalHits, totallines - linesPerPage);
		int end = (int) Math.min(numTotalHits, totallines);

		collector = TopScoreDocCollector.create(numTotalHits, false);
		searcher.search(query, collector);
		hits = collector.topDocs().scoreDocs;

		// 分页查询
		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			if (path != null) {
				SearchHit hit = (SearchHit) createObject(doc, SearchHit.class);
				if (hit != null) {
					hit.setScore(hits[i].score);
					System.out.println((i + 1) + ". " + path);
					if (hit.getTitle() != null) {
						System.out.println(hit.toString());
					}

					dataPackage.getDatas().add(hit);
				}
			} else {
				System.out.println((i + 1) + ". " + "No path for this document");
			}
		}

		return dataPackage;
	}

	/**
	 * 根据文档数据创建相应类型的对象
	 * 
	 * @param doc
	 *            lucene查询结果
	 * @param clazz
	 *            类
	 * @return
	 */
	private Object createObject(Document doc, Class<SearchHit> clazz) {
		Method method = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			Object obj = clazz.newInstance();

			PropertyDescriptor[] propdescs = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propdescs.length; i++) {
				PropertyDescriptor propdesc = propdescs[i];
				method = propdesc.getWriteMethod();
				if (method != null) {
					try {
						method.invoke(obj, doc.get(propdesc.getName()));
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}

			return obj;
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}
}
