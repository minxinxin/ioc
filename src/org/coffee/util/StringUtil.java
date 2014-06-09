package org.coffee.util;

public class StringUtil {
	/**
	 *<pre>
	 *判断字符串是否不可用
	 *不可用: null, "", !str.matches(regex) --> return true
	 * @param str 待检测的String
	 * @param regex 使用正则表达式进行匹配
	 * @return 如果是不可用的String 则返回true 否则返回false( 字符串可用)
	 * </pre>
	 * */
	public static boolean isInvaildString(String str,String regex){
		if(str == null || str.trim().equals(""))
			return true;
		if(regex != null){
			if(str.matches(regex))
				return false;
			else
				return true;
		}
		return false;
		
	}
}
