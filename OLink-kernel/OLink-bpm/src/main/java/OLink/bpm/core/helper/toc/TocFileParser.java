/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package OLink.bpm.core.helper.toc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import OLink.bpm.core.helper.toc.impl.DocumentReader;
import OLink.bpm.core.helper.toc.impl.Toc;
import OLink.bpm.core.multilanguage.action.MultiLanguageUtil;
import OLink.bpm.util.StringUtil;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TocFileParser extends DefaultHandler {

	private DocumentReader reader;

	private final static String TOC_FILE_NAME = "toc";

	private static TocFileParser instance;

	private Map<String, Toc> language2tocMap = new HashMap<String, Toc>();

	private TocFileParser() {
	}

	public static TocFileParser getInstance() {
		if (instance == null) {
			instance = new TocFileParser();
		}

		return instance;
	}

	public Toc getToc(String language) throws IOException, SAXException, ParserConfigurationException {
		if (StringUtil.isBlank(language)) {
			language = MultiLanguageUtil.getUserLanguage(Locale.getDefault()); // 获取系统默认语言环境
		}
		if (!language2tocMap.containsKey(language)) {
			String fileName = "i18n/" + TOC_FILE_NAME + "_" + language + ".xml";
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);
			Toc toc = parse(in);

			language2tocMap.put(language, toc);
		}

		return language2tocMap.get(language);
	}

	public Toc getToc() throws IOException, SAXException, ParserConfigurationException {
		return getToc("");
	}

	public Toc parse(InputStream in) throws IOException, SAXException, ParserConfigurationException {
		if (reader == null) {
			reader = new DocumentReader();
		}
		if (in != null) {
			try {
				Toc toc = (Toc) reader.read(in);
				return toc;
			} finally {
				in.close();
			}
		} else {
			throw new FileNotFoundException();
		}
	}

	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		InputStream in = ClassLoader.getSystemResourceAsStream("toc.xml");
		Toc toc = new TocFileParser().parse(in);
		ITopic[] topics = toc.getTopics();
		System.out.println(toc.getLabel());
		System.out.println(topics.length);
		for (int i = 0; i < topics.length; i++) {
			System.out.println(topics[i].getId() + " : " + topics[i].getLabel() + " : " + topics[i].getHref());
		}

		ITopic topic = toc.getTopicById("domain_list");
		System.out.println("find by id-->" + topic.getId() + " : " + topic.getLabel() + " : " + topic.getHref());
	}
}
