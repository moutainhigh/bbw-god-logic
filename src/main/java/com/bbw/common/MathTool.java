package com.bbw.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 字符串表达式运算。支持 四则运算、括号，sqrt、sin、cos、tan,及 factor `^`
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年7月25日 下午3:02:47
 */
public class MathTool {
	/**
	 * 运算基本的表达式。如：( 5 + 2 * 3 ) / 7
	 * 
	 * @param str
	 * @return
	 */
	public static double eval(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ')
					nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			// | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.')
						nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z')
						nextChar();
					String func = str.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char) ch);
				}

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}

	/**
	 * 四舍五入保存2位小数
	 * 
	 * @param f
	 * @return
	 */
	public static double halfUp2(double f) {
		BigDecimal b = new BigDecimal(f);
		double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	/**
	 * <pre>
	 * 第一个数除以第二个数，四舍五入保留2位小数。
	 * 除数为0返回0
	 * </pre>
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static BigDecimal divHalfUp2(int first, int second) {
		BigDecimal firstD = new BigDecimal(first);
		BigDecimal secondD = new BigDecimal(second);
		if (second != 0) {
			BigDecimal v = firstD.divide(secondD, 2, RoundingMode.HALF_UP);
			return v;
		}
		return new BigDecimal("0.00");
	}

	/**
	 *  * 10进制整数转换为N进制整数。 10进制转换为N进制的方法是：这个10进制数除以N,求出余数，并把余数倒叙排列。 除N取余，倒叙排列  * @param tenRadix  *            十进制整数  * @param radix
	 *  *            要转换的进制数，例如，要转成2进制数，radix就传入2  * @return radix进制的字符串  
	 */
	public static String string10ToN(int tenRadix, int radix) {
		// 进制编码支持9+26=35进制
		String code = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder buf = new StringBuilder();
		int remainder = 0;
		while (tenRadix != 0) {
			remainder = tenRadix % radix;// 求余数
			tenRadix = tenRadix / radix;// 除以基数
			buf.append(code.charAt(remainder));// 保存余数，记得要倒叙排列
		}
		buf.reverse();// 倒叙排列
		return buf.toString();
	}

	/**
	 * 返回x的ex次幂。
	 * 
	 * @param x 底数
	 * @param ex 幂指数
	 * @return x的ex次幂
	 */
	public static int pow(int x, int ex) {
		int result = 1;
		for (int i = 0; i < ex; i++) {
			result *= x;
		}
		return result;
	}

	/**
	 *  * 返回N进制对应的10进制数。  *  * @param N_num  *            N进制数  * @param radix  *            N进制计数  * @return N进制数对应的10进制数  
	 */
	public static int stringNTo10(String N_num, int radix) {
		StringBuilder stringBuilder = new StringBuilder(N_num);
		stringBuilder.reverse();// 反转字符，为了把权重最大的放在最右边，便于下面从左到右遍历，根据下标求权重。
		// 如果不反转，从右向左遍历(从字符串下标大的一端)也可以
		char bitCh;
		int result = 0;
		for (int i = 0; i < stringBuilder.length(); i++) {
			bitCh = stringBuilder.charAt(i);
			if (bitCh >= '0' && bitCh <= '9') {
				// '0'对应的ASCII码整数：48
				result += (int) (bitCh - '0') * pow(radix, i);
			} else if (bitCh >= 'A' && bitCh <= 'Z') {
				// 减去'A'的ASCII码值(65),再加上10
				result += ((int) (bitCh - 'A') + 10) * pow(radix, i);
			} else if (bitCh >= 'a' && bitCh <= 'z') {
				// 减去'a'的ASCII码值(97),再加上10
				result += ((int) (bitCh - 'a') + 10) * pow(radix, i);
			}
		}
		return result;
	}

	/**
	 * double精确加法
	 * 
	 * @param values
	 * @return
	 */
	public static double add(double... values) {
		BigDecimal result = new BigDecimal(0);
		for (double d : values) {
			BigDecimal b = new BigDecimal(Double.toString(d));
			result = result.add(b);
		}
		return result.doubleValue();
	}

	/**
	 * double精确减法
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static double subtract(double d1, double d2) {
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.subtract(b2).doubleValue();
	}
	
	/**
	 * 返回整百分比的
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static String getRate(int num1,int num2) {
        return getRate(num1, num2,0);
	}
	
	/**
	 * 返回指定小数点位数的 百分比  如：f=1时 返回 10.1% ; f=0时10%
	 * @param num1
	 * @param num2
	 * @param f 百分比小数位
	 * @return
	 */
	public static String getRate(int num1,int num2,int f) {
        // 创建一个数值格式化对象  
		if (num1==0 || num2==0) {
			return "0%";
		}
        NumberFormat numberFormat = NumberFormat.getInstance();  
        // 设置精确到小数点后2位  
        numberFormat.setMaximumFractionDigits(f);  
        String result = numberFormat.format((float) num1 / (float) num2 * 100);  
        return result + "%";
	}

	public static int getInt(Double b) {
		return b.intValue();
	}
}
