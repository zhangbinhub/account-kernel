package pers.acp.tools.match;

import pers.acp.tools.exceptions.OperateException;
import pers.acp.tools.utility.CommonUtility;

import java.util.LinkedList;

/**
 * 四则运算类
 *
 * @author zb
 */
public final class Operate {

    private final LinkedList<Character> priStack = new LinkedList<>();

    private final LinkedList<Double> numStack = new LinkedList<>();

    /**
     * 进行四则运算
     *
     * @param str 运算表达式
     * @return 运算结果:double
     */
    public double caculate(String str) throws OperateException {
        String temp;
        StringBuilder tempNum = new StringBuilder();
        StringBuilder string = new StringBuilder().append(str).append("#");

        while (string.length() != 0) {
            temp = string.substring(0, 1);
            string.delete(0, 1);
            if (!isNum(temp)) {
                if (!CommonUtility.isNullStr(tempNum.toString())) {
                    double num = Double.parseDouble(tempNum.toString());
                    numStack.push(num);
                    tempNum.delete(0, tempNum.length());
                }
                while (!compare(temp.charAt(0)) && (!priStack.isEmpty())) {
                    double a = numStack.pop();
                    double b;
                    if (numStack.isEmpty()) {
                        b = 0D;
                    } else {
                        b = numStack.pop();
                    }
                    char ope = priStack.pop();
                    double result;
                    switch (ope) {
                        case '+':
                            result = b + a;
                            numStack.push(result);
                            break;
                        case '-':
                            result = b - a;
                            numStack.push(result);
                            break;
                        case '*':
                            result = b * a;
                            numStack.push(result);
                            break;
                        case '/':
                            result = b / a;
                            if (Double.isInfinite(result) || Double.isNaN(result)) {
                                throw new OperateException("in division,the divisor is Illegal");
                            }
                            numStack.push(result);
                            break;
                        case '^':
                            result = Math.pow(b, a);
                            numStack.push(result);
                            break;
                    }
                }
                if (temp.charAt(0) != '#') {
                    priStack.push(temp.charAt(0));
                    if (temp.charAt(0) == ')') {
                        priStack.pop();
                        priStack.pop();
                    }
                }
            } else {
                tempNum = tempNum.append(temp);
            }
        }
        return numStack.pop();
    }

    /**
     * 判断传入的字符是否合法
     *
     * @param temp 传入的字符串
     * @return 是否合法
     */
    private boolean isNum(String temp) {
        return temp.matches("[0-9]|\\.");
    }

    /**
     * 比较当前操作符与栈顶元素操作符优先级,如果比栈顶元素优先级高,则返回true,否则返回false
     *
     * @param str 需要进行比较的字符
     * @return 比较结果 true代表比栈顶元素优先级高,false代表比栈顶元素优先级低
     */
    private boolean compare(char str) {
        if (priStack.isEmpty()) {
            return true;
        }
        char last = priStack.peek();
        if (last == '(') {
            return true;
        }
        switch (str) {
            case '#':
                return false;
            case '(':
                return true;
            case ')':
                return false;
            case '^':
                return last == '*' || last == '/' || last == '+' || last == '-';
            case '*':
                return last == '+' || last == '-';
            case '/':
                return last == '+' || last == '-';
            case '+':
                return false;
            case '-':
                return false;
        }
        return true;
    }
}
