package cn.superiormc.ultimateshop.utils;


import cn.superiormc.ultimateshop.managers.ConfigManager;
import cn.superiormc.ultimateshop.managers.ErrorManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Stack;

public class MathUtil {

    public static BigDecimal doCalculate(String mathStr) {
        try {
            if (!ConfigManager.configManager.getBoolean("check-math")) {
                return BigDecimal.valueOf(Double.parseDouble(mathStr));
            }
            // 后缀表达式链
            LinkedList<String> postfixList = new LinkedList<>();
            // 运算符栈
            Stack<Character> optStack = new Stack<>();
            // 多位数链
            LinkedList<Character> multiDigitList = new LinkedList<>();
            char[] arr = mathStr.toCharArray();
            for (char c : arr) {
                if (Character.isDigit(c) || '.' == c) {
                    multiDigitList.addLast(c);
                } else {
                    // 处理当前的运算符之前，先处理多位数链中暂存的数据
                    if (!multiDigitList.isEmpty()) {
                        StringBuilder temp = new StringBuilder();
                        while (!multiDigitList.isEmpty()) {
                            temp.append(multiDigitList.removeFirst());
                        }
                        postfixList.addLast(temp.toString());
                    }
                }
                // 如果当前字符是左括号，将其压入运算符栈
                if ('(' == c) {
                    optStack.push(c);
                }
                // 如果当前字符为运算符
                else if ('+' == c || '-' == c || '*' == c || '/' == c) {
                    while (!optStack.isEmpty()) {
                        char stackTop = optStack.pop();
                        // 若当前运算符的优先级高于栈顶元素，则一起入栈
                        if (compare(c, stackTop)) {
                            optStack.push(stackTop);
                            break;
                        }
                        // 否则，弹出栈顶运算符到后缀表达式，继续下一次循环
                        else {
                            postfixList.addLast(String.valueOf(stackTop));
                        }
                    }
                    optStack.push(c);
                }
                // 如果当前字符是右括号，反复将运算符栈顶元素弹出到后缀表达式，直到栈顶元素是左括号（为止，并将左括号从栈中弹出丢弃。
                else if (c == ')') {
                    while (!optStack.isEmpty()) {
                        char stackTop = optStack.pop();
                        if (stackTop != '(') {
                            postfixList.addLast(String.valueOf(stackTop));
                        } else {
                            break;
                        }
                    }
                }
            }
            // 遍历结束时，若多位数链中具有数据，说明公式是以数字结尾
            if (!multiDigitList.isEmpty()) {
                StringBuilder temp = new StringBuilder();
                while (!multiDigitList.isEmpty()) {
                    temp.append(multiDigitList.removeFirst());
                }
                postfixList.addLast(temp.toString());
            }
            // 遍历结束时，运算符栈若有数据，说明是由括号所致，需要补回去
            while (!optStack.isEmpty()) {
                postfixList.addLast(String.valueOf(optStack.pop()));
            }
            Stack<BigDecimal> numStack = new Stack<>();
            while (!postfixList.isEmpty()) {
                String item = postfixList.removeFirst();
                BigDecimal a, b;
                switch (item) {
                    case "+":
                        a = numStack.pop();
                        b = numStack.pop();
                        numStack.push(b.add(a));
                        break;
                    case "-":
                        a = numStack.pop();
                        b = numStack.pop();
                        numStack.push(b.subtract(a));
                        break;
                    case "*":
                        a = numStack.pop();
                        b = numStack.pop();
                        numStack.push(b.multiply(a));
                        break;
                    case "/":
                        a = numStack.pop();
                        b = numStack.pop();
                        numStack.push(b.divide(a, 2, RoundingMode.HALF_UP));
                        break;
                    default:
                        numStack.push(new BigDecimal(item));
                        break;
                }
            }
            return numStack.pop();
        }
        catch (NumberFormatException | EmptyStackException ep) {
            ErrorManager.errorManager.sendErrorMessage("§x§9§8§F§B§9§8[UltimateShop] §cError: Your number option can not be read, maybe" +
                    " you forgot install PlaceholderAPI plugin in your server, or you didn't enable check-math option in config.yml!");
            return BigDecimal.valueOf(0D);
        }
    }

    private static boolean compare(char curr, char stackTop) {
        if (stackTop == '(') {
            return true;
        }
        if (curr == '*' || curr == '/') {
            return stackTop == '+' || stackTop == '-';
        }
        return false;
    }
}
