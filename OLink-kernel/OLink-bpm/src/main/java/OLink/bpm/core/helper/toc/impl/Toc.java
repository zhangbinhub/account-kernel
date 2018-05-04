/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 *******************************************************************************/
package OLink.bpm.core.helper.toc.impl;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.core.helper.toc.IToc;
import OLink.bpm.core.helper.toc.ITopic;
import org.w3c.dom.Element;

public class Toc extends UAElement implements IToc {

	public static final String NAME = "toc"; //$NON-NLS-1$
	public static final String ATTRIBUTE_LABEL = "label"; //$NON-NLS-1$
	public static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	public static final String ATTRIBUTE_TOPIC = "topic"; //$NON-NLS-1$
	public static final String ATTRIBUTE_LINK_TO = "link_to"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ID = "id"; //$NON-NLS-1$
	public static final String ATTRIBUTE_ICON = "icon"; //$NON-NLS-1$
	public static final String ATTRIBUTE_SORT = "sort"; //$NON-NLS-1$

	private Map<String, ITopic> href2TopicMap;
	private Map<String, ITopic> id2TopicMap;

	public Toc(IToc src) {
		super(NAME, src);
		setHref(src.getHref());
		setLabel(src.getLabel());
		ITopic topic = src.getTopic(null);
		if (topic != null) {
			setTopic(topic.getHref());
		}
		appendChildren(src.getChildren());
	}

	public Toc(Element src) {
		super(src);
	}

	/*
	 * Creates a mapping of all topic hrefs to ITopics.
	 */
	private Map<String, ITopic> createHref2TopicMap() {
		Map<String, ITopic> map = new HashMap<String, ITopic>();
		ITopic[] topics = getTopics();
		for (int i = 0; i < topics.length; ++i) {
			createHref2TopicMapAux(map, topics[i]);
		}
		return map;
	}

	/*
	 * Creates a mapping of all topic hrefs to ITopics under the given ITopic
	 * and stores in the given Map.
	 */
	private void createHref2TopicMapAux(Map<String, ITopic> map, ITopic topic) {
		String href = topic.getHref();
		if (href != null) {
			map.put(href, topic);
			int anchorIx = href.lastIndexOf("#"); //$NON-NLS-1$
			if (anchorIx >= 0) { // anchor exists, drop it and add href again
				// to
				// map
				String simpleHref = href.substring(0, anchorIx);
				if (!map.containsKey(simpleHref)) {
					map.put(simpleHref, topic);
				}
			}
		}
		ITopic[] subtopics = topic.getSubtopics();
		if (subtopics != null) {
			for (int i = 0; i < subtopics.length; ++i) {
				if (subtopics[i] != null) {
					createHref2TopicMapAux(map, subtopics[i]);
				}
			}
		}
	}

	public String getHref() {
		return getAttribute(ATTRIBUTE_HREF);
	}

	public String getIcon() {
		return getAttribute(ATTRIBUTE_ICON);
	}

	public boolean isSorted() {
		return "true".equalsIgnoreCase(getAttribute(ATTRIBUTE_SORT)); //$NON-NLS-1$
	}

	/*
	 * Returns a mapping of all topic hrefs to ITopics.
	 */
	private Map<String, ITopic> getHref2TopicMap() {
		if (href2TopicMap == null) {
			href2TopicMap = createHref2TopicMap();
		}
		return href2TopicMap;
	}

	private Map<String, ITopic> getId2TopicMap() {
		if (id2TopicMap == null) {
			id2TopicMap = createId2TopicMap();
		}
		return id2TopicMap;
	}

	/**
	 * 创建主题ID与主题的映射
	 * 
	 */
	private Map<String, ITopic> createId2TopicMap() {
		Map<String, ITopic> map = new HashMap<String, ITopic>();
		ITopic[] topics = getTopics();
		for (int i = 0; i < topics.length; ++i) {
			// 根主题
			createId2TopicMapAux(map, topics[i]);
		}
		return map;
	}

	/**
	 * 递归创建主题ID与主题的映射
	 * 
	 * @param map
	 *            汇总映射
	 * @param topic
	 *            上级主题
	 */
	private void createId2TopicMapAux(Map<String, ITopic> map, ITopic topic) {
		String id = topic.getId();
		if (id != null) {
			map.put(id, topic);
			int anchorIx = id.lastIndexOf("#"); //$NON-NLS-1$
			// anchor exists, drop it and add href again to map
			if (anchorIx >= 0) {
				String simpleId = id.substring(0, anchorIx);
				if (!map.containsKey(simpleId)) {
					map.put(simpleId, topic);
				}
			}
		}

		// 有子主题
		ITopic[] subtopics = topic.getSubtopics();
		if (subtopics != null && subtopics.length > 0) {
			for (int i = 0; i < subtopics.length; ++i) {
				if (subtopics[i] != null) {
					createId2TopicMapAux(map, subtopics[i]);
				}
			}
		}
	}

	public String getLabel() {
		return getAttribute(ATTRIBUTE_LABEL);
	}

	public String getLinkTo() {
		return getAttribute(ATTRIBUTE_LINK_TO);
	}

	public String getTopic() {
		return getAttribute(ATTRIBUTE_TOPIC);
	}

	public ITopic getTopicById(String id) {
		return getId2TopicMap().get(id);
	}

	public ITopic getTopic(String href) {
		return getHref2TopicMap().get(href);
	}

	public ITopic[] getTopics() {
		return (ITopic[]) getChildren(ITopic.class);
	}

	public void setLabel(String label) {
		setAttribute(ATTRIBUTE_LABEL, label);
	}

	public void setLinkTo(String linkTo) {
		setAttribute(ATTRIBUTE_LINK_TO, linkTo);
	}

	public void setTopic(String href) {
		setAttribute(ATTRIBUTE_TOPIC, href);
	}

	public void setHref(String href) {
		setAttribute(ATTRIBUTE_HREF, href);
	}
}
