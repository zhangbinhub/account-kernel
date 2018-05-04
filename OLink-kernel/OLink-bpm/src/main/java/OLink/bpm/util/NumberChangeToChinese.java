package OLink.bpm.util;

public class NumberChangeToChinese {
	
	    /**
	     * 阿拉伯数字对应的汉字大写字符集合.
	     */
	    private static char[] N = new char[] { '零', '壹', '贰', '叁', '肆', '伍', '陆',
	            '柒', '捌', '玖' };

	    /**
	     * 基本单位字符集合.
	     */
	    private static char[] U = new char[] { '拾', '佰', '仟' };

	    /**
	     * 特殊单位字符集合.
	     */
	    private static char[] X = new char[] { '万', '亿', '兆' };

	    /**
	     * 
	     * @param d
	     * @return
	     */
	    public static String changeToMoney(double d) {
	        if (d > Long.MAX_VALUE)
	            throw new IllegalArgumentException("参数超出长整数的最大支持范围!");
	        String str = rawProcess(d);
	        StringBuffer result = postProcess(d, str);
	        fractionProcess(d, result);
	        return result.toString();
	    }

	    /**
	     * 小数部分处理.
	     * 
	     * @param d
	     * @param result
	     */
	    private static void fractionProcess(double d, StringBuffer result) {
	            int fraction = ((int) (d * 100)) % 100;
	                result.append(N[fraction / 10] + "角");
	                result.append(N[fraction % 10] + "分");
	    }

	    /**
	     * 后处理过程, 处理为惯用法.
	     * 
	     * @param d
	     * @param str
	     * @return
	     */
	    private static StringBuffer postProcess(double d, String str) {
	        StringBuffer result = new StringBuffer();
	        char[] chars = str.toCharArray();
	        for (int i = 0; i < chars.length; i++) {
	            char c = chars[i];
	            if (c == N[0]) {// 处理为惯用用法
	                if (i < chars.length - 1) {
	                    int ignoreType = 0;
	                    for (int j = 0; j < U.length; j++) {// 处理诸如'零仟', '零佰',
	                                                        // '零拾'等情况
	                        if (chars[i + 1] == U[j]) {
	                            ignoreType = -1;
	                            break;
	                        }
	                    }
	                    for (int j = 0; j < X.length; j++) {// 处理诸如'零亿','零万'等情况
	                        if (chars[i + 1] == X[j]) {
	                            ignoreType = 1;
	                            break;
	                        }
	                    }
	                    final int length = result.length();
	                    switch (ignoreType) {
	                    case -1:
	                        // 保证不输出相邻重复的'零', 跳过'零'后的单位符号
	                        i++;
	                        if (length > 0 && result.charAt(length - 1) != N[0])
	                            result.append(N[0]);
	                        break;
	                    case 1:
	                        // 如果单位'万','亿','兆'前面出现'零',则删除'零'
	                        i++;
	                        if (length > 0 && result.charAt(length - 1) == N[0])
	                            result.deleteCharAt(length - 1);
	                        char v = result.charAt(result.length() - 1);
	                        boolean append = true;
	                        for (int j = 0; j < X.length; j++) {// 处理诸如'亿万'的情况
	                            if (v == X[j]) {
	                                append = false;
	                                break;
	                            }
	                        }
	                        if (append)
	                            result.append(chars[i]);
	                        break;
	                    }
	                }
	            } else {
	                result.append(c);
	            }
	        }
	        // 处理最后一个字符是'零'的情况
	        int len = result.length();
	        if (len > 0 && result.charAt(len - 1) == N[0]) {
	            result.deleteCharAt(len - 1);
	        }
	        
	        if((long)d ==0)
	         return new StringBuffer(N[0]+"元");
	        else
	         return result.append("元");
	    }

	    /**
	     * 
	     * @param d
	     * @return
	     */
	    private static String rawProcess(double d) {
	        StringBuffer buf = new StringBuffer();
	        long n = (long) d;
	        int bit = 0;
	        int x = 0;
	           while (n >0) {
	            int m = (int) (n % 10);// 最低位数字
	            buf.append(N[m]);
	            n = n / 10;
	            if (n > 0) {
	                int index = bit % 4;
	                if (index == 3)
	                    buf.append(X[x++]);
	                else
	                    buf.append(U[index]);
	             }
	            bit++;
	           }
	        return buf.reverse().toString();
}

	}
