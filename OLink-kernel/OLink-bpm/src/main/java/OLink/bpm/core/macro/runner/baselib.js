importPackage(Packages.OLink.bpm.core.macro.runner);
importPackage(Packages.OLink.bpm.util.file); // 文件工具
importPackage(Packages.OLink.bpm.util.http); // Http、URL工具
importPackage(Packages.OLink.bpm.util.mail); // 邮件工具

/**
 * 公共变量MAP对象
 */
var GLOBAL_MAP = new Packages.java.util.HashMap();

/**
 * 获取当前打开文档的ID
 * 
 * @return 获取当前打开文档的ID
 */
function getId() {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getId();
	}
	return "";
}

/**
 * 获取当前打开文档中Item的值
 * 
 * @return 获取当前打开文档中Item的值
 * 
 * @param fieldName
 *            当前打开文档的字段名
 */
function getItemValue(fieldName) {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getItemValueAsString(fieldName);
	}
	return "";
}

/**
 * 获取当前打开文档中Item的值,且以字符串形式返回
 * 
 * @return 获取当前打开文档中Item的值,且以字符串形式返回
 * 
 * @param fieldName
 *            当前打开文档的字段名
 */
function getItemValueAsString(fieldName) {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getItemValueAsString(fieldName);
	}
	return "";
}

/**
 * 获取当前打开文档中Item的值,且以日期形式返回
 * 
 * @return 获取当前打开文档中Item的值,且以日期形式返回
 * 
 * @param fieldName
 *            当前打开文档的字段名
 */
function getItemValueAsDate(fieldName) {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getItemValueAsDate(fieldName);
	}
	return null;
}

/**
 * 获取当前打开文档中Item的值,且以double形式返回
 * 
 * @return 获取当前打开文档中Item的值,且以double形式返回
 * 
 * @param fieldName
 *            当前打开文档的字段名
 */
function getItemValueAsDouble(fieldName) {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getItemValueAsDouble(fieldName);
	}
	return 0;
}

/**
 * @return 获取当前打开文档中Item的值,且以整型值形式返回
 * 
 * @param fieldName:当前打开文档的字段名
 */
function getItemValueAsInt(fieldName) {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getItemValueAsInt(fieldName);
	}
	return 0;
}

/**
 * 根据子文档名，获取当前文档的子文档个数
 * 
 * @return 根据子文档名，获取当前文档的子文档个数
 * 
 * @param formName
 *            当前打开文档的子文档名
 */
function countSubDocument(formName) {
	var doc = $CURRDOC.getCurrDoc();
	var total = 0;
	if (doc != null) {
		if (doc != null) {
			var subdocs = doc.getChilds(formName);
			if (subdocs != null && subdocs.size() > 0) {
				total = subdocs.size();
			}
		}
	}
	return total;
}

/**
 * 根据子文档名和字段名，获取当前打开文档的子文档中字段的值总和
 * 
 * @return 根据子文档名和字段名，获取当前打开文档的子文档中字段的值总和
 * 
 * @param formName
 *            当前打开文档的子文档名
 * @param fieldName
 *            子文档的字段名
 */
function sumSubDocument(formName, fieldName) {
	var doc = $CURRDOC.getCurrDoc();
	var accurateCalculate = $BEANFACTORY
			.createObject("OLink.bpm.util.Arith");
	var total = 0;
	if (doc != null) {
		var subdocs = doc.getChilds(formName);
		if (subdocs != null && subdocs.size() > 0) {
			for (var iter = subdocs.iterator(); iter.hasNext();) {
				var subdoc = iter.next();
				total += subdoc.getItemValueAsDouble(fieldName);
			}
		}
	}
	return accurateCalculate.round(total, 2);
}

/**
 * 返回当前打开文档对象
 * 
 * @return 返回当前打开文档对象
 */
function getCurrentDocument() {
	var doc = $CURRDOC.getCurrDoc();
	return doc;
}

/**
 * 返回当前登录用户所属企业域ID
 * 
 * @return 返回当前登录用户所属企业域ID
 */
function getDomainid() {
	return $WEB.getDomainid();
}

/**
 * 返回当前打开文档的父文档对象
 * 
 * @return 返回当前打开文档的父文档对象
 */
function getParentDocument() {
	var doc = $CURRDOC.getCurrDoc();
	var parentid = getParameter("parentid");
	var user = $WEB.getWebUser();
	var parent = null;

	if (user != null) {
		// 以参数获取
		if (parentid != null) {
			parent = user.getFromTmpspace(parentid);
		}

		// 以对象获取
		if (parent == null && doc != null) {
			if (doc.getParentid() != null) {
				parent = user.getFromTmpspace(doc.getParentid());
			}
		}
	}

	if (parent == null && doc != null) {
		parent = doc.getParent();
	}

	return parent;
}

/**
 * 获取关联的文档，当包含元素不存在父子关系时生效
 * 
 * @return 获取关联的文档，当包含元素不存在父子关系时生效
 */
function getRelateDocument() {
	return getParentDocument();
}

/**
 * 获取文档中Item的值,且以字符串形式返回
 * 
 * @return 获取文档中Item的值,且以字符串形式返回
 * 
 * @param docid
 *            文档的id标识
 * @param fieldName
 *            文档的字段名
 */
function getDocItemValue(docid, fieldName) {

	var process = getDocProcess($WEB.getApplication());
	var doc = process.doView(docid);
	if (doc != null) {
		return doc.getItemValueAsString(fieldName);
	}
	return "";
}

/**
 * 获取文档中Item的值,且以字符串形式返回
 * 
 * @return 获取文档中Item的值,且以字符串形式返回
 * 
 * @param docid
 *            文档的id标识
 * @param fieldName
 *            文档的字段名
 */
function getDocItemValueAsString(docid, fieldName) {
	var process = getDocProcess($WEB.getApplication());
	var doc = process.doView(docid);
	if (doc != null) {
		return doc.getItemValueAsString(fieldName);
	}
	return "";
}

/**
 * 获取文档中Item的值,且以double形式返回
 * 
 * @return 获取文档中Item的值,且以double形式返回
 * 
 * @param docid
 *            文档的id标识
 * @param fieldName
 *            文档的字段名
 */
function getDocItemValueAsDouble(docid, fieldName) {
	var process = getDocProcess($WEB.getApplication());
	var doc = process.doView(docid);
	if (doc != null) {
		return doc.getItemValueAsDouble(fieldName);
	}
	return 0.0;
}

/**
 * 获取文档中Item的值,且以日期形式返回
 * 
 * @return 获取文档中Item的值,且以日期形式返回
 * 
 * @param docid
 *            文档的id标识
 * @param fieldName
 *            文档的字段名
 */
function getDocItemValueAsDate(docid, fieldName) {

	var process = getDocProcess($WEB.getApplication());
	var doc = process.doView(docid);
	if (doc != null) {
		return doc.getItemValueAsDate(fieldName);
	}
	return null;
}

/**
 * 获取文档中Item的值,且以整型值形式返回
 * 
 * @return 获取文档中Item的值,且以整型值形式返回
 * 
 * @param docid
 *            文档的id标识
 * @param fieldName
 *            文档的字段名
 */
function getDocItemValueAsInt(docid, fieldName) {
	var process = getDocProcess($WEB.getApplication());
	var doc = process.doView(docid);
	if (doc != null) {
		return doc.getItemValueAsInt(fieldName);
	}
	return 0;
}

/**
 * 根据给定的docid，获取Document对象
 * 
 * @return 根据给定的docid，获取Document对象
 * 
 * @param docid
 *            文档的id标识
 */
function findDocument(docid) {
	var process = getDocProcess($WEB.getApplication());
	var doc = process.doView(docid);
	return doc;
}

/**
 * 根据dql查询符合条件的Document，结果以Collection返回
 * 
 * @return 根据dql查询符合条件的Document，结果以Collection返回
 * 
 * @param dql
 *            dql查询符合条件语句
 */
function queryByDQL(dql) {
	var process = getDocProcess($WEB.getApplication());
	var dpg = process.queryByDQL(dql, $WEB.getDomainid());
	return dpg.datas;
}

/**
 * 根据dql(带缓存)查询符合条件的Document，结果以Collection返回
 * 
 * @return 根据dql(带缓存)查询符合条件的Document，结果以Collection返回
 * 
 * @param dql
 *            dql查询符合条件语句
 */
function queryByDQLWithCache(dql) {
	var process = getDocProcess($WEB.getApplication());
	var dpg = process.queryByDQLWithCache(dql, $WEB.getDomainid());
	if (dpg)
		return dpg.datas;
	return null;
}

/**
 * 根据DQL获取文档
 * 
 * @param dql
 *            dql查询符合条件语句
 * @return 根据DQL获取文档
 */
function findByDQL(dql) {
	var process = getDocProcess($WEB.getApplication());
	return process.findByDQL(dql, $WEB.getDomainid());
}

/**
 * 根据SQL获取文档
 * 
 * @return 根据SQL获取文档
 * 
 * @param sql
 *            sql查询符合条件语句
 */
function findBySQL(sql) {
	var process = getDocProcess($WEB.getApplication());
	return process.findBySQL(sql, $WEB.getDomainid());
}

/**
 * 根据dql和域名查询符合条件的Document，结果以Collection返回
 * 
 * @return 根据dql和域名查询符合条件的Document，结果以Collection返回
 * 
 * @param dql
 *            dql查询符合条件语句
 * @param domainName
 *            企业域名称
 */
function queryByDQLDomain(dql, domainName) {
	var process = getDocProcess($WEB.getApplication());
	var dpg = process.queryByDQLDomainName(dql, domainName);
	return dpg.datas;
}

/**
 * 根据dql统计符合条件的Document数量
 * 
 * @return 根据dql统计符合条件的Document数量
 * 
 * @param dql
 *            dql查询符合条件语句
 */
function countByDQL(dql) {
	var process = getDocProcess($WEB.getApplication());
	var count = process.countByDQL(dql, $WEB.getDomainid());
	return count;
}

/**
 * 根据SQL汇总记录数
 * 
 * @param sql
 *            SQL查询语句
 * @return 返回统计结果
 */
function countBySQL(sql) {
	var process = getDocProcess($WEB.getApplication());
	var count = process.countBySQL(sql, $WEB.getDomainid());
	return count;
}

/**
 * 根据dql汇总符合条件的Document的指定字段
 * 
 * @return 根据dql汇总符合条件的Document的指定字段
 * 
 * @param dql
 *            dql查询符合条件语句
 * @param fieldName
 *            文档的字段名
 */
function sumByDQL(dql, fieldName) {
	var process = getDocProcess($WEB.getApplication());
	var sum = process.sumByDQL(dql, fieldName, $WEB.getDomainid());
	return sum;
}

/**
 * 根据sql汇总符合条件的Document的指定字段
 * 
 * @return 根据sql汇总符合条件的Document的指定字段
 * 
 * @param sql
 *            sql查询符合条件语句
 * @param fieldName
 *            文档的字段名
 */
function sumBySQL(sql) {
	var process = getDocProcess($WEB.getApplication());
	var sum = process.sumBySQL(sql, $WEB.getDomainid());
	return sum;
}

/**
 * 检查其参数是否为数字格式的字符串，是返回true,否则返回false
 * 
 * @return 检查其参数是否为数字格式的字符串，是返回true,否则返回false
 * 
 * @param str
 *            字符串型参数
 */
function isNumberText(str) {
	var retvar = $TOOLS.STRING_UTIL.isNumber(str);
	return retvar;
}

/**
 * 检查其参数是否为日期格式的字符串，是返回true,否则返回false
 * 
 * @return 检查其参数是否为日期格式的字符串，是返回true,否则返回false
 * 
 * @param str
 *            字符串型参数
 */
function isDateText(str) {
	var retvar = $TOOLS.STRING_UTIL.isDate(str);
	return retvar;
}

/**
 * 按照指定的分割符，切割文本，将分割好的结果用通过数组返回
 * 
 * @return 按照指定的分割符，切割文本，将分割好的结果用通过数组返回
 * 
 * @param str
 *            需要拆分的字符串
 * @param separator
 *            分割符
 */
function splitText(str, separator) {
	var retvar = $TOOLS.STRING_UTIL.split(str, separator);
	return retvar;
}

/**
 * 按照指定的分割符，切割文本，将分割好的结果用通过数组返回
 * 
 * @return 按照指定的分割符，切割文本，将分割好的结果用通过数组返回
 * 
 * @param str
 *            需要拆分的字符串
 * @param separator
 *            分割字串
 */
function splitString(str, separator) {
	var retvar = $TOOLS.STRING_UTIL.splitString(str, separator);
	return retvar;
}

/**
 * 将指定的字符串数组按照指定的分隔符组合成字符串，返回字符串
 * 
 * @return 将指定的字符串数组按照指定的分隔符组合成字符串，返回字符串
 * 
 * @param strs
 *            字符串数组
 */
function joinText(strs) {
	var retvar = $TOOLS.STRING_UTIL.unite(strs);
	return retvar;
}

/**
 * 获取当日日期
 * 
 * @return 获取当日日期
 */
function getToday() {
	var retvar = $TOOLS.DATE_UTIL.getToday();
	return retvar;
}

/**
 * 获取日期
 * 
 * @return 获取日期
 * 
 * @param date
 *            日期型参数
 */
function getDay(date) {
	var retvar = $TOOLS.DATE_UTIL.dayOfDate(date);
	return retvar;
}

/**
 * 获取月份
 * 
 * @return 获取月份
 * 
 * @param date
 *            日期型参数
 */
function getMonth(date) {
	var retvar = $TOOLS.DATE_UTIL.monthOfDate(date);
	return retvar;
}

/**
 * 获取年份
 * 
 * @return 获取年份
 * 
 * @param date
 *            日期型参数
 */
function getYear(date) {
	var retvar = $TOOLS.DATE_UTIL.yearOfDate(date);
	return retvar;
}

/**
 * 将字符串按给定格式转换为日期型
 * 
 * @return 将字符串按给定格式转换为日期型
 * 
 * @param str
 *            待转换的字符串,需符合format指定的格式
 * @param format
 *            日期格式
 */
function parseDate(str, format) {
	var retvar = $TOOLS.DATE_UTIL.parseDate(str, format);
	return retvar;
}

/**
 * 获取相隔年份数
 * 
 * @return 获取相隔年份数
 * 
 * @param startDate
 *            “yyyy-MM-dd”格式的字符串
 * @param endDate
 *            “yyyy-MM-dd”格式的字符串
 */
function diffYears(startDate, endDate) {
	var retvar = $TOOLS.DATE_UTIL.getDistinceYear(startDate, endDate);
	return retvar;
}

/**
 * 获取相隔月份数
 * 
 * @return 获取相隔月份数
 * 
 * @param startDate
 *            “yyyy-MM-dd”格式的字符串
 * @param endDate
 *            “yyyy-MM-dd”格式的字符串
 */
function diffMonths(startDate, endDate) {
	var retvar = $TOOLS.DATE_UTIL.getDistinceMonth(startDate, endDate);
	return retvar;
}

/**
 * 获取相隔天数
 * 
 * @return 获取相隔天数
 * 
 * @param startDate
 *            “yyyy-MM-dd”格式的字符串
 * @param endDate
 *            “yyyy-MM-dd”格式的字符串
 */
function diffDays(startDate, endDate) {
	var retvar = $TOOLS.DATE_UTIL.getDistinceDay(startDate, endDate);
	return retvar;
}

/**
 * 校正年份
 * 
 * @return 校正年份
 * 
 * @param date
 *            日期型参数
 * @param num
 *            正负整数
 */
function adjustYear(date, num) {
	var retvar = $TOOLS.DATE_UTIL.getNextDateByYearCount(date, num);
	return retvar;
}

/**
 * 校正月份
 * 
 * @return 校正月份
 * 
 * @param date
 *            日期型参数
 * @param num
 *            正负整数
 */
function adjustMonth(date, num) {
	var retvar = $TOOLS.DATE_UTIL.getNextDateByMonthCount(date, num);
	return retvar;
}

/**
 * 校正天数
 * 
 * @return 校正天数
 * 
 * @param date
 *            日期型参数
 * @param num
 *            正负整数
 */
function adjustDay(date, num) {
	var retvar = $TOOLS.DATE_UTIL.getNextDateByDayCount(date, num);
	return retvar;
}

/**
 * 每次调用时指定计数器都会自动增长1(根据计算器名,以0为基元),可用作生成增长序列号
 * 
 * @return 每次调用时指定计数器都会自动增长1(根据计算器名,以0为基元),可用作生成增长序列号
 * 
 * @param countLabel
 *            字符串型参数
 */
function countNext(countLabel) {
	var process = new Packages.OLink.bpm.core.counter.ejb.CounterProcessBean($WEB
			.getApplication());
	var retvar = process.getNextValue(countLabel, $WEB.getApplication(), $WEB
					.getDomainid());
	return retvar;
}

/**
 * 返回“前缀 + 增长序列号”
 * 
 * @return 返回“前缀 + 增长序列号”
 * 
 * @param headText
 *            作为前缀的字符串
 * @param isYear
 *            boolean型,前缀中是否包含年份
 * @param isMonth
 *            boolean型,前缀中是否包含月份
 * @param isDay
 *            boolean型,前缀中是否包含日期
 * @param digit
 *            数值型，指定增长序列号的位数
 */
function countNext2(headText, isYear, isMonth, isDay, digit) {
	var process = new Packages.OLink.bpm.core.counter.ejb.CounterProcessBean($WEB
			.getApplication());

	var dateUtil = $BEANFACTORY.createObject("OLink.bpm.util.DateUtil");

	var countLabel = headText;
	if (isYear) {
		countLabel += dateUtil.format(dateUtil.getToday(), "yy");
	}
	if (isMonth) {
		countLabel += dateUtil.format(dateUtil.getToday(), "MM");
	}
	if (isDay) {
		countLabel += dateUtil.format(dateUtil.getToday(), "dd");
	}
	var count = process.getNextValue(countLabel, $WEB.getApplication(), $WEB
					.getDomainid());
	var val = "";
	if (count < 10) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 100) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 1000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 10000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 100000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 1000000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 10000000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else
		val += count;
	var retvar = countLabel + val;
	return retvar;
}

/**
 * 根据计数器名称获取计数器的当前计数值
 * 
 * @return 根据计数器名称获取计数器的当前计数值
 * 
 * @param countLabel
 *            字符串型参数
 */
function getLastCount(countLabel) {
	var process = new Packages.OLink.bpm.core.counter.ejb.CounterProcessBean($WEB
			.getApplication());
	var retvar = process.getLastValue(countLabel, $WEB.getApplication(), $WEB
					.getDomainid());
	return retvar;
}

/**
 * 返回“前缀 +年月日+ 计数器的当前计数值”
 * 
 * @return 返回“前缀 +年月日+ 计数器的当前计数值”
 * 
 * @param headText
 *            作为前缀的字符串
 * @param isYear
 *            boolean型,前缀中是否包含年份
 * @param isMonth
 *            boolean型,前缀中是否包含月份
 * @param isDay
 *            boolean型,前缀中是否包含日期
 * @param digit
 *            数值型，指定增长序列号的位数
 */
function getLastCount2(headText, isYear, isMonth, isDay, digit) {
	var process = new Packages.OLink.bpm.core.counter.ejb.CounterProcessBean($WEB
			.getApplication());

	var dateUtil = $BEANFACTORY.createObject("OLink.bpm.util.DateUtil");

	var countLabel = headText;
	if (isYear) {
		countLabel += dateUtil.format(dateUtil.getToday(), "yy");
	}
	if (isMonth) {
		countLabel += dateUtil.format(dateUtil.getToday(), "MM");
	}
	if (isDay) {
		countLabel += dateUtil.format(dateUtil.getToday(), "dd");
	}
	var count = process.getLastValue(countLabel, $WEB.getApplication(), $WEB
					.getDomainid());
	var val = "";
	if (count < 10) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 100) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 1000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 10000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 100000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 1000000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else if (count < 10000000) {
		for (var temp = 1; temp <= digit - count.toString().length; temp++) {
			val += "0";
		}
		val += count;
	} else
		val += count;
	var retvar = countLabel + val;
	return retvar;
}

/**
 * 重置以其参数为类型生成的计算累计值,使其归0
 * 
 * @param countLabel
 *            字符串型参数
 */
function resetCounter(countLabel) {
	var process = new Packages.OLink.bpm.core.counter.ejb.CounterProcessBean($WEB
			.getApplication());
	process.doRemoveByName(countLabel, $WEB.getApplication(), $WEB
					.getDomainid());
}

/**
 * 返回项目id号
 * 
 * @return 返回项目id号
 */
function getApplication() {
	return $WEB.getApplication();
}

/**
 * 返回当前登录用户对象
 * 
 * @return 返回当前登录用户对象
 */
function getWebUser() {
	return $WEB.getWebUser();
}

/**
 * 判断是否为正数
 * 
 * @return 判断是否为正数
 * 
 * @param num
 *            数字型参数
 */
function isPositive(num) {
	if (num > 0) {
		return true;
	} else {
		return false;
	}
}

/**
 * 判断是否为负数
 * 
 * @return 判断是否为负数
 * 
 * @param num
 *            数字型参数
 */
function isNegative(num) {
	if (num < 0) {
		return true;
	} else {
		return false;
	}
}

/**
 * 提供精确的小数位四舍五入处理
 * 
 * @return 提供精确的小数位四舍五入处理
 * 
 * @param num
 *            需要四舍五入的数字
 * @param pos
 *            小数点后保留几位
 */
function round(num, pos) {
	var accurateCalculate = $BEANFACTORY
			.createObject("OLink.bpm.util.Arith");
	return accurateCalculate.round(num, pos);
}

/**
 * 返回符合查询符合条件语句的文档中keyFieldName字段的所有值集合，作为下拉框控件的选项
 * 
 * @return 返回符合查询符合条件语句的文档中keyFieldName字段的所有值集合，作为下拉框控件的选项
 * 
 * @param dql
 *            查询符合条件语句
 * @param keyFieldName
 *            文档的字段名(当为数组时[0]作为真实值，[1]作为显示值)
 * @param blankFirst
 *            默认是否为空选项
 */
function getOptionsByDQL(dql, keyFieldName, blankFirst) {
	var opts = $TOOLS.createOptions();
	if (blankFirst) {
		opts.add("", "");
	}
	var docs = queryByDQL(dql);
	for (var iter = docs.iterator(); iter.hasNext();) {
		var doc = iter.next();
		var value = "";
		var text = "";
		if (keyFieldName.constructor == Array) {
			value = doc.getItemValueAsString(keyFieldName[0]);
			text = doc.getItemValueAsString(keyFieldName[1]);
		} else {
			value = doc.getItemValueAsString(keyFieldName);
			text = doc.getItemValueAsString(keyFieldName);
		}

		opts.add(text, value);
	}
	return opts;
}

/**
 * 返回大于等于其数字参数的最小整数
 * 
 * @return 返回大于等于其数字参数的最小整数
 * 
 * @param num
 *            数字参数
 */
function toCeil(num) {
	return Math.ceil(num);
}

/**
 * 返回小于等于其数字参数的最大整数
 * 
 * @return 返回小于等于其数字参数的最大整数
 * 
 * @param num
 *            数字参数
 */
function toFloor(num) {
	return Math.floor(num);
}

/**
 * 检查其参数是否为电子邮箱地址格式的字符串，是返回true,否则返回false
 * 
 * @return 检查其参数是否为电子邮箱地址格式的字符串，是返回true,否则返回false
 * 
 * @param str
 *            字符串型参数
 */
function isMailAddressText(str) {
	var t = /^\w+@\w+(\.\w+)+/;
	var g = /^\w+\.\w+@\w+(\.\w+)+/;
	if (t.test(str) == false && g.test(str) == false) {
		return false;
	}
	return true;
}

/**
 * 把其日期参数转化成指定格式的字符串
 * 
 * @return 把其日期参数转化成指定格式的字符串
 * @param date
 *            日期型参数
 * @param formatText
 *            指定字符串格式
 */
function format(date, formatText) {
	return $TOOLS.DATE_UTIL.format(date, formatText);
}

/**
 * 按其参数指定的格式获取当前时间，并以字符串的形式返回
 * 
 * @return 按其参数指定的格式获取当前时间，并以字符串的形式返回
 * @param date
 *            日期型参数
 * @param formatText
 *            字符串型参数，指定需要显示的格式，如"yyyy-MM-dd HH:mm:ss"
 */
function getCurDate(formatText) {
	return $TOOLS.DATE_UTIL.format($TOOLS.DATE_UTIL.getToday(), formatText)
}

/**
 * 获取相隔小时数
 * 
 * @return 获取相隔小时数
 * 
 * @param startDate
 *            “yyyy-MM-dd HH:mm:ss”格式的字符串
 * @param endDate
 *            “yyyy-MM-dd HH:mm:ss”格式的字符串
 */
function diffHours(startDate, endDate) {
	var diff = $TOOLS.DATE_UTIL.getDistinceTime(startDate, endDate);
	return diff;
}

/**
 * 获取相隔工作天数
 * 
 * @return 获取相隔工作天数
 * 
 * @param startDate
 *            “yyyy-MM-dd HH:mm:ss”格式的字符串
 * @param endDate
 *            “yyyy-MM-dd HH:mm:ss”格式的字符串
 */
function getWorkingDayCount(startDate, endDate) {
	var user = $WEB.getWebUser();
	var count = $TOOLS.DATE_UTIL.getWorkingDayCount(startDate, endDate, user
					.getCalendarType());
	return count;
}

/**
 * 获取相隔工作小时数
 * 
 * @return 获取相隔工作小时数
 * @param startDate
 *            “yyyy-MM-dd HH:mm:ss”格式的字符串
 * @param endDate
 *            “yyyy-MM-dd HH:mm:ss”格式的字符串
 */
function getWorkingTimesCount(startDate, endDate) {
	var user = $WEB.getWebUser();
	var count = $TOOLS.DATE_UTIL.getWorkingTimesCount(startDate, endDate, user
					.getCalendarType());
	return count;
}
/**
 * 生成系统类对象
 * 
 * @return 生成系统类对象
 * @param pathText
 *            类的名字空间,如"OLink.bpm.util.DateUtil";
 * 
 */
function createObject(pathText) {
	var obj = $BEANFACTORY.createObject(pathText);
	return obj;
}

/**
 * 生成系统类对象
 * 
 * @return 生成系统类对象
 * @param pathText
 *            类的名字空间,如"OLink.bpm.core.department.ejb.DepartmentProcess";
 * 
 */
function createProcess(pathText) {
	var process = $PROCESSFACTORY.createProcess(pathText);
	return process;
}

/**
 * 返回文档操作对象
 * 
 * @param applicationid
 *            软件标识ID
 * @return 文档操作对象
 */
function getDocProcess(applicationid) {
	return Packages.OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean
			.createMonitoProcess(applicationid);
}

/**
 * 返回文件上传对象
 * 
 * @param applicationid
 *            软件标识ID
 * @return 文件上传对象
 */
function getUploadProcess(applicationid) {
	return Packages.OLink.bpm.core.upload.ejb.UploadProcessBean
			.createMonitoProcess(applicationid);
}

/**
 * 获取参数列表对象，可以对参数进行基本的增,删和格式化参数
 * 
 * @return 参数列表对象
 */
function getParamsTable() {
	return $WEB.getParamsTable();
}

/**
 * 创建参数对象
 * 
 * @return 返回ParamsTable的新实例对象
 */
function createParamsTable() {
	return new Packages.OLink.bpm.base.action.ParamsTable();
}

/**
 * 获取参数值,以字符串的形式返回
 * 
 * @param paramName
 *            参数名;
 * @return 返回参数值
 * 
 */
function getParameter(paramName) {
	return $WEB.getParameterAsString(paramName);
}

/**
 * 获取参数值,以";"进行切割格式化成文本型,以字符串的形式返回
 * 
 * @param paramName
 *            参数名;
 * @return 返回参数值
 */
function getParameterAsText(paramName) {
	return $WEB.getParameterAsText(paramName);
}

/**
 * 获取参数值,以浮点型的形式返回
 * 
 * @param paramName
 *            参数名;
 * @return 返回参数值
 * 
 */
function getParameterAsDouble(paramName) {
	return $WEB.getParameterAsDouble(paramName);
}

/**
 * 获取参数值,并以字符数组的形式返回
 * 
 * @param paramName
 *            参数名;
 * @return 返回参数值，以字符数组对象返回
 */
function getParameterAsArray(paramName) {
	var params = $WEB.getParameterAsArray(paramName);
	if (params!=null && params.length==1 && params[0]!=null && params[0].indexOf(";")>0)
		params = splitText(params[0],";");
	return params;
}

/**
 * 返回的部分请求的URI，指示请求的范围内。 上下文路径总是先在一个请求的URI。 路径以一个“/”字符，但并没有结束的"/"字符。
 * 在默认（根）Servlet的情况下，此方法返回""。该容器不解码此字符串。
 * 
 * @return 一个String指定请求的URI部分，指示请求的上下文
 */
function getContextPath() {
	return $WEB.getParamsTable().getContextPath();
}

/**
 * 判断值是否不为空,为数字时不为0,为字符串时长度大于0,为日期时不为null
 * 
 * @return 判断值是否不为空,为数字时不为0,为字符串时长度大于0,为日期时不为null
 * @param val
 *            要作判断的值;
 */
function isNotNull(val) {
	if (val != null && typeof(val) != "undefined") {
		if (typeof(val) == "number") {
			return (val != 0);
		}
		return (new java.lang.String(val).trim().length() > 0);
	}
	return false;
}
/**
 * 根据sql查询符合条件的Document，结果以Collection返回
 * 
 * @param sql
 *            查询语句参数
 * @return 根据sql查询符合条件的Document，结果以Collection返回
 */
function queryBySQL(sql) {
	var process = getDocProcess($WEB.getApplication());
	var dpg = process.queryBySQL(sql, $WEB.getDomainid());
	if (dpg != null) {
		return dpg.datas;
	} else {
		return null;
	}
}
 /**
  * 根据sql(带缓存）查询符合条件的Document，结果以Collection返回
  * 
  * @param sql
  *            查询语句参数
  * @return 根据sql查询符合条件的Document，结果以Collection返回
  */
 function queryBySQLWithCache(sql) {
 	var process = getDocProcess($WEB.getApplication());
 	var dpg = process.queryBySQLWithCache(sql, $WEB.getDomainid());
 	if (dpg != null) {
 		return dpg.datas;
 	} else {
 		return null;
 	}
 }
/**
 * 创建警告对话框
 * 
 * @param content
 *            警告信息参数
 * @return 创建警告对话框
 */
function createAlert(content) {
	return new JsMessage(JsMessage.TYPE_ALERT, content);
}
/**
 * 创建提示对话框
 * 
 * @param content
 *            提示内容参数
 * @return 返回true|false；true: 表示确认；false: 表示取消。
 */
function createConfirm(content) {
	return new JsMessage(JsMessage.TYPE_CONFIRM, content);
}

/**
 * 生成选项对象
 * 
 * @return 生成选项对象
 */
function createOptions() {
	return $TOOLS.createOptions();
}

/**
 * 发送站内短信
 * 
 * @param senderid
 *            发送者ID
 * @param receiverid
 *            接收者ID
 * @param title
 *            标题
 * @param content
 *            内容
 * 
 */
function sendMessage(senderid, receiverid, title, content) {
	var process = createProcess("OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess");
	process.doCreate(senderid, receiverid, title, content);
}

/**
 * 根据部门发送站内短信
 * 
 * @param departmentid
 *            部门ID
 * @param title
 *            标题
 * @param content
 *            内容
 * 
 */
function sendMessageByDept(departmentid, title, content) {
	var userid = getWebUser().getId();
	var process = createProcess("OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess");
	process.doCreateByDepartment(departmentid, userid, title, content);
}

/**
 * 根据角色发送短信
 * 
 * @param roleid
 *            角色ID
 * @param domainid
 *            企业域ID
 * @param title
 *            标题
 * @param content
 *            内容
 * 
 */
function sendMessageByRole(roleid, domainid, title, content) {
	var userid = getWebUser().getId();
	var process = createProcess("OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess");
	process.doCreateByRole(roleid, domainid, userid, title, content);
}
/**
 * 发送手机短信
 * 
 * @param docid
 *            发送模块表单记录ID
 * @param title
 *            标题
 * @param content
 *            内容
 * @param receiver
 *            接收者电话列表,有多个接收者,使用","做分隔符
 * @param isReply
 *            true|false,是否需要收到回复
 * @param isMass
 *            true|false,标识是否为群发,即是否有多位接收者
 */
function sendSMS(docid, title, content, receiver, isReply, isMass) {
	var sender = $MESSAGE.getSMSManager().getSender($WEB.getWebUser());
	return sender.send(docid, title, content, receiver, isReply, isMass);
}

/**
 * 根据发送模块表单记录ID获取接收者手机短信回复记录.结果以Collection返回
 * 
 * @return 根据发送模块表单记录ID获取接收者手机短信回复记录.结果以Collection返回
 */
function listReplyByDocid(docid) {
	var data = $MESSAGE.getSMSManager().queryReplyById(docid);
	if (data!=null)
		return data.datas;
	return null;
}
/**
 * 判断唯一性
 * 
 * @param fieldName
 *            字段名称
 * @param fieldValue
 *            字段值
 * @param msg
 *            预设字段值重复提示信息
 * @return 如果不唯一返回提示信息，否则返回空字符串
 */
function checkFieldUnique(fieldName, fieldValue, msg) {
	var doc = $CURRDOC.getCurrDoc();
	var dql = "$formname='" + doc.getFormShortName() + "' and " + fieldName + "='"
			+ fieldValue + "' and $id <> '" + doc.getId() + "'";
	var dpg = queryByDQL(dql);
	if (dpg.size() > 0) {
		if (msg) {
			return msg;
		}
		return fieldName + " 不能重复!";
	}
	return null;
}

/**
 * 获取当前浏览器session
 * 
 * @param sessionName
 *            session属性名
 * @return 获取当前浏览器session
 */
function getSession(sessionName) {
	var request = $WEB.getParamsTable().getHttpRequest();
	if (request != null) {
		return request.getSession().getAttribute(sessionName);
	}
	return null;
}

/**
 * 根据部门等级值获取对应等级的所有部门
 * 
 * @param level
 *            部门等级值
 * @return 返回获取到的对应等级的所有部门的集合
 */
function getDepartmentByLevel(level) {
	var process = createProcess("OLink.bpm.core.department.ejb.DepartmentProcess");
	return process.getDepartmentByLevel(level, getApplication(), getDomainid());
}

/**
 * 根据部门名称和部门等级获取部门对象ID
 * 
 * @param name
 *            部门名称
 * @param level
 *            部门等级值
 * @return 返回对应部门ID
 */
function getDeptIdByNameAndLevel(name, level) {
	var deptlist = getDepartmentByLevel(level);
	if (deptlist != null && deptlist.size() > 0) {
		for (var iter = deptlist.iterator(); iter.hasNext();) {
			var dept = iter.next();
			if (name.equals(dept.getName())) {
				return dept.getId();
			}
		}
	}
	return null;
}
/**
 * 根据角色名取角色ID
 * 
 * @param name
 *            角色名称
 * @return 返回对应角色ID
 */
function getRoleIdByName(name) {
	var role = getRoleByName(name);
	if (role != null) {
		return role.getId();
	}
	return null;
}

/**
 * 根据用户登录名取用户ID
 * 
 * @param loginno
 *            用户登录名
 * @return 返回对应用户ID
 */
function getUserIdByLoginno(loginno) {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.findUserIdByAccount(loginno, getDomainid());
}

/**
 * 获取指定部门的下级部门
 * 
 * @param parent
 *            部门ID
 * @return 返回指定部门的下级部门对象的集合
 */
function getDepartmentsByParent(parent) { // parent部门ID
	var process = createProcess("OLink.bpm.core.department.ejb.DepartmentProcess");
	return process.getDatasByParent(parent);
}

/**
 * 获取指定部门所有用户
 * 
 * @param dptid
 *            部门ID
 * @return 返回指定部门下的所有用户对象的集合
 */
function getUsersByDptId(dptid) {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.queryByDepartment(dptid);
}

/**
 * 获取指定角色下的所有用户
 * 
 * @param roleid
 *            角色ID
 * @return 返回指定角色下的所有用户对象的集合
 */
function getUsersByRoleId(roleid) {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.queryByRole(roleid);
}

/**
 * 获取指定部门并角色的所有用户
 * 
 * @param dptid
 *            部门ID
 * @param roleid
 *            角色ID
 * @return 返回指定部门并角色的所有用户对象的集合
 */
function getUsersByDptIdAndRoleId(dptid, roleid) {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.queryByDptIdAndRoleId(dptid, roleid);
}

/**
 * 获取当前域下面的所有用户
 * 
 * @return 返回当前域下面的所有用户对象的集合
 */
function getAllUsers() {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.queryByDomain(getDomainid());
}

/**
 * 发送邮件
 * 
 * @param from
 *            发送人地址
 * @param to
 *            接收人地址
 * @param subject
 *            主题
 * @param body
 *            内容
 * @param host
 *            邮件服务器地址
 * @param user
 *            邮件服务器用户名
 * @param password
 *            密码
 * @param bbc
 *            秘密抄送地址
 * @param validate
 *            是否校验
 */
function sendMail(from, to, subject, body, host, user, password, bbc, validate) {
	$EMAIL.setEmail(from, to, subject, body, host, user, password, bbc,
			validate);
	$EMAIL.send();
}

/**
 * 发送邮件给所有用户
 * 
 * @param from
 *            发送人地址
 * @param subject
 *            主题
 * @param host
 *            邮件服务器地址
 * @param user
 *            邮件服务器用户名
 * @param password
 *            密码
 * @param bbc
 *            秘密抄送地址
 * @param validate
 *            是否校验
 */
function sendMailtoAllUser(from, subject, host, user, password, bbc, validate) {
	$EMAIL.sendMailToAllUser(from, subject, host, account, password, bbc,
			validate);
}

/**
 * 以系统配置的用户发送邮件
 * 
 * @param to
 *            接收人地址
 * @param subject
 *            主题
 * @param content
 *            内容
 * @return
 */
function sendEmailBySystemUser(to, subject, content) {
	$EMAIL.sendEmailBySystemUser(to, subject, content);
}

/**
 * 获取当前软件下面的所有角色组别
 * 
 * @return 角色组的集合
 */
function getAllRoles() {
	var roleProcess = createProcess("OLink.bpm.core.role.ejb.RoleProcess");
	return roleProcess.getRolesByApplication(getApplication());
}

/**
 * 获取当前文档的状态标签
 * 
 * @return 返回当前文档的状态标签
 */
function getStateLabel() {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		return doc.getStateLabel();
	}
	return null;
}

/**
 * 获取当前记录是否审批完成
 * 
 * @return true|false
 */
function isComplete() {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		if (doc.getStateInt() == 0x00100000) {
			return true;
		}
	}
	return false;
}

/**
 * 获取指定文档是否审批完成
 * 
 * @param docid
 *            文档ID
 * @return true|false
 */
function isCompleteByDocId(docid) {
	var doc = findDocument(docid);
	if (doc != null) {
		if (doc.getStateInt() == 0x00100000) {
			return true;
		}
	}
	return false;
}

/**
 * 获取当前记录是否处在第一个节点
 * 
 * @return true|false
 */
function isFirtNode() {
	var doc = $CURRDOC.getCurrDoc();
	if (doc != null) {
		if (doc.getStateInt() == 0x00000010) {
			return true;
		}
	}
	return false;
}

/**
 * 获取指定文档是否处在第一个节点
 * 
 * @param docid
 *            文档ID
 * @return true|false
 */
function isFirtNodeByDocId(docid) {
	var doc = findDocument(docid);
	if (doc != null) {
		if (doc.getStateInt() == 0x00000010) {
			return true;
		}
	}
	return false;
}

/**
 * 获取父流程文档
 * 
 * @return 获取父流程文档
 */
function getParentFlowDoc() {
	var currDoc = $CURRDOC.getCurrDoc();
	if (currDoc != null) {
		doc = currDoc.getParentFlowDocument();
	}

	return doc;
}

/**
 * 子流程开启脚本中使用此函数，可获取到子流程启动的文档对象
 * 
 * @return 返回子流程启动文档对象
 */
function getStartDoc() {
	return $STARTUP_DOC;
}

/**
 * 获取子流程文档
 * 
 * @return 获取子流程文档
 */
function getSubFlowDocList() {
	var currDoc = $CURRDOC.getCurrDoc();
	if (currDoc != null) {
		return currDoc.getSubFlowDocuments();
	}
}

/**
 * 根据数据源名称，执行SQL查询
 * 
 * @param dsName
 *            数据源名称
 * @param sql
 *            SQL查询语句
 * @return 返回SQL查询语句执行的结果，以Collection方式返回（存储的是数据记录的Map对象）。
 */
function queryByDSName(dsName,sql){
	var process = createProcess("OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess");
	return process.queryDataSourceSQL(dsName,sql,getApplication());
}

/**
 * 根据数据源名称，执行SQL插入操作（SQL语句为：insert table ......）。
 * 
 * @param dsName
 *            数据源名称
 * @param sql
 *            SQL查询语句
 */
function insertByDSName(dsName,sql){
	var process = createProcess("OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess");
	process.queryInsert(dsName,sql,getApplication());
}

/**
 * 根据数据源名称，执行SQL更新操作（SQL语句为：update table set......）。
 * 
 * @param dsName
 *            数据源名称
 * @param sql
 *            SQL查询语句
 */
function updateByDSName(dsName,sql){
	var process = createProcess("OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess");
	process.createOrUpdate(dsName,sql,getApplication());
}

/**
 * 根据数据源名称，执行SQL删除操作（SQL语句为：delete from table ......）。
 * 
 * @param dsName
 *            数据源名称
 * @param sql
 *            SQL查询语句
 */
function deleteByDSName(dsName,sql){
	var process = createProcess("OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess");
	process.remove(dsName,sql,getApplication());
}

/**
 * 根据用户ID获取用户对象
 * 
 * @param userid
 *            用户ID
 * @return 返回用户对象
 */
function getUserById(userid) {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.doView(userid);
}

/**
 * 根据用户登录名取用户对象
 * 
 * @param loginno
 *            用户登录名
 * @return 返回对应用户对象
 */
function getUserByLoginno(loginno) {
	var userProcess = createProcess("OLink.bpm.core.user.ejb.UserProcess");
	return userProcess.login(loginno, getDomainid());
}

/**
 * 根据角色名取角色ID
 * 
 * @param name
 *            角色名称
 * @return 返回对应角色对象
 */
function getRoleByName(name) {
	var roleProcess = createProcess("OLink.bpm.core.role.ejb.RoleProcess");
	return roleProcess.doViewByName(name, getApplication());
}

/**
 * 返回文档操作对象
 * 
 * @return 文档操作对象
 */
function getDocumentProcess() {
	return Packages.OLink.bpm.core.dynaform.document.ejb.DocumentProcessBean
			.createMonitoProcess(getApplication());
}

/**
 * 获取数据源业务对象
 * 
 * @return 获取数据源业务对象
 */
function getDataSourceProcess() {
	return createProcess("OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess");
}

/**
 * 返回用户操作对象
 * 
 * @return 用户操作对象
 */
function getUserProcess() {
	return createProcess("OLink.bpm.core.user.ejb.UserProcess");
}

/**
 * 返回部门操作对象
 * 
 * @return 部门操作对象
 */
function getDepartmentProcess() {
	return createProcess("OLink.bpm.core.department.ejb.DepartmentProcess");
}

/**
 * 返回角色操作对象
 * 
 * @return 角色操作对象
 */
function getRoleProcess() {
	return createProcess("OLink.bpm.core.role.ejb.RoleProcess");
}
/**
 * 返回表单业务对象
 * 
 * @return 返回表单业务对象
 */
function getFormProcess() {
	return createProcess("OLink.bpm.core.dynaform.form.ejb.FormProcess");
}
/**
 * 返回视图业务对象
 * 
 * @return 返回视图业务对象
 */
function getViewProcess() {
	return createProcess("OLink.bpm.core.dynaform.view.ejb.ViewProcess");
}

/**
 * 获取企业域业务对象
 * 
 * @return 获取企业域业务对象
 */
function getDomainProcess() {
	return createProcess("OLink.bpm.core.domain.ejb.DomainProcess");
}

/**
 * 输出文本到控制台
 * 
 * @param text
 *            要输出文本的内容
 */
function println(text){
	$PRINTER.println(text);
}

/**
 * 将数字文本转换成整型并返回
 * 
 * @param text
 *            数字文本参数
 * @return 返回整型值
 */
function parseInt(text){
	return $TOOLS.STRING_UTIL.parseInt(text);
}

/**
 * 将数字文本转换成长整型并返回
 * 
 * @param text
 *            数字文本参数
 * @return 返回长整型值
 */
function parseLong(text){
	return $TOOLS.STRING_UTIL.parseLong(text);
}

/**
 * 将数字文本转换成浮点型并返回
 * 
 * @param text
 *            数字文本参数
 * @return 返回浮点型值
 */
function parseDouble(text){
	return $TOOLS.STRING_UTIL.parseDouble(text);
}

/**
 * 根据文档ID与流程ID获取文档最后审批记录
 * @param docid 
 * 			文档ID
 * @param flowid
 * 			流程ID
 * @return 最后审批记录
 */
function getLastRelationHis(docid, flowid){
	var process = new Packages.OLink.bpm.core.workflow.storage.runtime.ejb.RelationHISProcessBean(getApplication());
	return process.doViewLast(docid, flowid);
}

/**
 * 获取当前文档的最后审批人
 * @return 最后审批人审批记录
 */
function getLastApprover(){
	var doc = getCurrentDocument();
	var rtn = null;
	var rhis = getLastRelationHis(doc.getId(),doc.getFlowid());
	if (rhis != null) {
		var actors = rhis.getActorhiss();
		if (actors!=null && actors.size()>0) {
			for (var it = actors.iterator(); it.hasNext();) {
				rtn = it.next();
			}
		}
	}
	return rtn;
}

/**
 * 获取当前文档的最后审批人ID
 * @return 审批人ID
 */
function getLastApproverId(){
	return getLastApprover().getActorid();
}

/**
 * 获取当前文档的最后审批人名称
 * @return 审批人名称
 */
function getLastApproverName(){
	return getLastApprover().getName();
}

/**
 * 获取当前文档的最后审批时间
 * @return 审批时间
 */
function getLastApprovedTime(){
	var doc = getCurrentDocument();
	var rtn = "";
	var rhis = getLastRelationHis(doc.getId(),doc.getFlowid());
	if (rhis != null) {
		var actors = rhis.getActorhiss();
		if (actors!=null && actors.size()>0) {
			for (var it = actors.iterator(); it.hasNext();) {
				actorHis = it.next();
				if (actorHis.getProcesstime() != null) {
					rtn = $TOOLS.DATE_UTIL.getDateTimeStr(actorHis
							.getProcesstime());
				} else {
					rtn = $TOOLS.DATE_UTIL.getDateTimeStr(relHis
							.getActiontime());
				}
			}
		}
	}
	return rtn;
}

/**
 * 根据角色编号和软件id获取角色
 * 
 */

function getRoleByRoleNo(roleno, applicationid){
	var process = new Packages.OLink.bpm.core.role.ejb.RoleProcessBean();
	var role = process.findByRoleNo(roleno, applicationid);
	if(role != null){
		return role.getName();
	}else{
		return null;
	}
	
}

