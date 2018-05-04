/***************************************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package OLink.bpm.core.helper.search;

/**
 * A search result containing a document reference, score, summary, etc.
 */
public class SearchHit {

	private String href;
	private String title;
	private String summary;
	private String id;
	private float score;

	public SearchHit() {
	}

	/**
	 * Constructs a new SearchHit.
	 * 
	 * @param href
	 *            the href to the document
	 * @param title
	 *            a label describing the hit
	 * @param summary
	 *            a summary paragraph further describing the hit
	 * @param id
	 *            the unique id of the document
	 */
	public SearchHit(String id, String href, String title, String summary, float score) {
		this.id = id;
		this.href = href;
		this.title = title;
		this.summary = summary;
		this.score = score;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHref() {
		return href;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public float getScore() {
		return score;
	}

	public int hashCode() {
		return href.hashCode();
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return "{id:'" + getId() + "', title:'" + getTitle() + "', href: '" + getHref() + "', score: " + getScore() + "}";
	}
}
