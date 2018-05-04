/*
 * Copyright (c) JForum Team
 * All rights reserved.
 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on May 31, 2004 by pieter
 * The JForum Project
 * http://www.jforum.net
 */
package OLink.bpm.util.varible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.document.ejb.Document;

/**
 * @author Nicholas
 */
public class VariableExpander {
	private VariableStore variables;
	private String pre;
	private String post;

	private Map<String, String> cache;

	public VariableExpander(VariableStore variables, String pre, String post) {
		this.variables = variables;
		this.pre = pre;
		this.post = post;
		cache = new HashMap<String, String>();
	}

	public void clearCache() {
		cache.clear();
	}

	public Collection<String> getVariableNames(String source) {
		Collection<String> rtn = new ArrayList<String>();

		String result = this.cache.get(source);

		if (source == null || result != null) {
			return rtn;
		}

		int fIndex = source.indexOf(this.pre);

		if (fIndex == -1) {
			return rtn;
		}

		String sb = new String(source);

		while (fIndex > -1) {
			int lIndex = sb.indexOf(this.post);

			int start = fIndex + this.pre.length();

			if (fIndex == 0) {
				String varName = sb.substring(start, start + lIndex - this.pre.length());
				sb = sb.substring(lIndex + 1);
				rtn.add(varName);
			} else {
				String varName = sb.substring(start, lIndex);
				sb = sb.substring(lIndex + 1);
				rtn.add(varName);
			}

			fIndex = sb.indexOf(this.pre);
		}

		return rtn;
	}

	public String expandVariables(String source) {
		String result = this.cache.get(source);

		if (source == null || result != null) {
			return result;
		}

		int fIndex = source.indexOf(this.pre);

		if (fIndex == -1) {
			return source;
		}

		StringBuffer sb = new StringBuffer(source);

		while (fIndex > -1) {
			int lIndex = sb.indexOf(this.post);

			int start = fIndex + this.pre.length();

			if (fIndex == 0) {
				String varName = sb.substring(start, start + lIndex - this.pre.length());
				if (this.variables.getVariableValue(varName) != null) {
					sb.replace(fIndex, fIndex + lIndex + 1, this.variables.getVariableValue(varName));
				}
			} else {
				String varName = sb.substring(start, lIndex);
				if (this.variables.getVariableValue(varName) != null) {
					sb.replace(fIndex, lIndex + 1, this.variables.getVariableValue(varName));
				}
			}

			fIndex = sb.indexOf(this.pre);
		}

		result = sb.toString();

		this.cache.put(source, result);

		return result;
	}

	public static void main(String[] args) {
		IScriptVariableStore store = new IScriptVariableStore();
		VariableExpander expander = new VariableExpander(store, "${", "}");
		String str = "FROM 表单名 WHERE 表单字段名 ='${doc.文档字段名}' AND 表单字段2=${params.参数字段名}";
		Collection<String> rtn = expander.getVariableNames(str);
		System.out.println(rtn);
		Document doc = new Document();
		try {
			doc.addStringItem("文档字段名", "值1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ParamsTable params = new ParamsTable();
		params.setParameter("参数字段名", "值2");

		store.parseVariableNames(rtn, doc, params);

		System.out.println(expander.expandVariables(str));
	}
}
