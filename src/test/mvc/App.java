package test.mvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coffee.ioc.core.annotation.Component;


@Component(singleton=false)
public class App {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Pattern pattern = Pattern.compile("([a-zA-Z_]\\w*\\.)*[a-zA-Z_]\\w*");
		//Pattern pattern = Pattern.compile("[a-bA-Z0-9]");
		Matcher matcher =  pattern.matcher("_a ");

		System.out.println(matcher.matches());
		
		
		System.out.print("abc".startsWith("abcd"));
	}

}
