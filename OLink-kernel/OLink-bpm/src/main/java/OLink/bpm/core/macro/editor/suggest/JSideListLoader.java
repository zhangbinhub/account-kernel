/*
 * JSide is an Integrated Development Environment for JavaScript Copyright
 * (C) 2006 JSide Development Team
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package OLink.bpm.core.macro.editor.suggest;

import java.util.LinkedList;

/**
 * List loader implementation
 * 
 * @author Arnab Karmakar
 * @version 1.0
 */
class JSideListLoader
{
	private LinkedList<String> searchableList;

	/**
	 * Constructor - initializes and populates the searchable list
	 * 
	 * @param strSearchableList An array of searchable items
	 */
	JSideListLoader(String[] strSearchableList)
	{
		// initialize list
		searchableList = new LinkedList<String>();
		// populate searchable list
		for (int i = 0; i < strSearchableList.length; i++)
			searchableList.add(strSearchableList[i]);
	}

	/**
	 * Searches the list for give input string
	 * 
	 * @param criteria The search criteria
	 * @return An array of possible matches
	 */
	String[] searchList(String criteria)
	{
		LinkedList<String> matchedList = new LinkedList<String>();
		if (criteria != null && criteria.trim().length() > 0)
		{
			for (int i = 0; i < searchableList.size(); i++)
			{
				String value = searchableList.get(i).toString();
				if (criteria.length() <= value.length()
						&& value.substring(0, criteria.length()).equals(
								criteria))
					matchedList.add(searchableList.get(i));
			}
		}

		String strMatchedList[] = new String[matchedList.size()];
		for (int i = 0; i < strMatchedList.length; i++)
			strMatchedList[i] = String.valueOf(matchedList.get(i).toString());

		return strMatchedList;
	}
}
