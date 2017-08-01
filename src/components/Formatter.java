package components;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Formatter {

	public static StringBuilder removeString(StringBuilder s, String r) {
		while (s.indexOf(r) != -1) {
			int index = s.indexOf(r);
			s.replace(index, index + r.length(), "");
		}
		return s;
	}

	public static String removeString(String s, String r) {
		s = s.replaceAll(r, "");
		return s;
	}

	public static String removeStrings(String s, String[] delims) {
		for (String r : delims) {
			s = s.replaceAll(r, "");
		}
		return s;
	}

	public static StringBuilder removeStrings(StringBuilder s, String[] delims) {
		for (String r : delims) {
			while (s.indexOf(r) != -1) {
				int index = s.indexOf(r);
				s.replace(index, index + r.length(), "");
			}
		}
		return s;
	}

	public static StringBuilder removeCommas(StringBuilder s) {
		while (s.indexOf(",") != -1) {
			int index = s.indexOf(",");
			s.replace(index, index + 1, "");
		}
		return s;
	}

	public static Double formatValue(String s) {
		return Double.parseDouble(s.replaceAll("[$,]", ""));
	}

	public static Boolean isPercentage(String s) {
		return s.contains("%");
	}

	public static Boolean isDollarValue(String s) {
		return s.contains("$");
	}
	
	public static String getPercentage(String s){
		String[] arr = s.split("\\s");
		for(String r : arr){
			if(isPercentage(r)){
				return r;
			}
		}
		return "";
	}
	
	public static void printDictionary(HashMap<String, Double>  map){
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.printf("Key: %s\n", entry.getKey());
			System.out.printf("Value: %s\n\n", entry.getValue());
		}

	}
	
	public static String formatRatingArea(String r){
		String[] delims = {"Rating Area ", "Area "};
		r = r.replaceAll(", ", "/");
		r = r.replaceAll(",", "/");
		r = removeStrings(r, delims);
		return r;
	}

}
