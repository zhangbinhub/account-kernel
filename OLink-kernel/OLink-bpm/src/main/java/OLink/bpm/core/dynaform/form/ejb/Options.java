/*
 * Created on 2005-2-9
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.form.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class Options {
	/**
	 * 
	 * 
	 * @uml.property name="options"
	 */
	private Collection<Option> options = new ArrayList<Option>();

	/**
	 * 
	 */
	public Options() {
		super();
	}

	/**
	 * 添加选项
	 * 
	 * @param opt
	 *            选项对象
	 */
	public void add(Option opt) {
		options.add(opt);
	}

	/**
	 * 移除选项
	 * 
	 * @param opt
	 *            选项对象
	 */
	public void remove(Option opt) {
		options.remove(opt);
	}

	/**
	 * 根据参数,增加选项
	 * 
	 * @param option
	 *            显示值
	 * @param value
	 *            真实值
	 */
	public void add(String option, String value) {
		Option opt = new Option(option, value);
		add(opt);
	}

	/**
	 * 根据参数,增加选项
	 * 
	 * @param option
	 *            显示值
	 * @param value
	 *            真实值
	 * @param def
	 *            是否默认选中
	 */
	public void add(String option, String value, boolean def) {
		Option opt = new Option(option, value, def);
		add(opt);
	}

	/**
	 * 移除选项
	 * 
	 * @param option
	 *            显示值
	 * @param value
	 *            真实值
	 */
	public void remove(String option, String value) {
		Option opt = new Option(option, value);
		remove(opt);
	}

	/**
	 * 创建选项对象
	 * 
	 * @return 选项对象
	 */
	public static Options createOptions() {
		return new Options();
	}

	/**
	 * 获取增加的选项对象
	 * 
	 * @return 选项集合
	 * @uml.property name="options"
	 */
	public Collection<Option> getOptions() {
		return options;
	}

	public String toJSON() {
		StringBuffer buffer = new StringBuffer();
		if (options != null && !options.isEmpty()) {
			buffer.append("{");
			for (Iterator<Option> iterator = options.iterator(); iterator.hasNext();) {
				Option option = iterator.next();
				buffer.append("'" + option.getOption() + "': '" + option.getValue() + "'");
				buffer.append(",");
			}
			buffer.deleteCharAt(buffer.lastIndexOf(","));
			buffer.append("}");
		}

		return buffer.toString();
	}
}
