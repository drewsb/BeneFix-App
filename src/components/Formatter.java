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
	
	public static Boolean isDecimal(String s) {
		return s.contains(".") & containsInteger(s);
	}

	public static Boolean isDollarValue(String s) {
		return s.contains("$");
	}

	public static String removeDecimal(String s) {
		if (s.indexOf(".") != -1) {
			return s.substring(0, s.indexOf("."));
		} else {
			return s;
		}
	}

	public static String getCoinsurance(String s) {
		String coinsurance = "";
		int index = s.indexOf("%");
		if (Character.isDigit(s.charAt(index - 2))) {
			coinsurance += s.charAt(index - 2);
		}
		if (Character.isDigit(s.charAt(index - 1))) {
			coinsurance += s.charAt(index - 1);
		}
		coinsurance += "%";
		return coinsurance;
	}

	public static String removeFileExtension(String input) {
		return input.substring(0, input.lastIndexOf("."));
	}

	public static String getPercentage(String s) {
		String[] arr = s.split("\\s");
		for (String r : arr) {
			if (isPercentage(r)) {
				return r;
			}
		}
		return "";
	}

	public static Boolean containsInteger(String s) {
		char[] arr = s.toCharArray();
		for (char c : arr) {
			if (Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	public static void printDictionary(HashMap<String, Double> map) {
		for (Map.Entry<String, Double> entry : map.entrySet()) {
			System.out.printf("Key: %s\n", entry.getKey());
			System.out.printf("Value: %s\n\n", entry.getValue());
		}

	}

	public static String formatRatingArea(String r) {
		String[] delims = { "Rating Area ", "Area ", "Number " };
		r = r.replaceAll(", ", "/");
		r = r.replaceAll(",", "/");
		r = removeStrings(r, delims);
		return r;
	}

	public static String getStartDate(String q) {
		if (q.equals("Q1")) {
			return "2017/01/01";
		}
		if (q.equals("Q2")) {
			return "2017/04/01";
		}
		if (q.equals("Q3")) {
			return "2017/07/01";
		}
		if (q.equals("Q4")) {
			return "2017/10/01";
		}
		return "";
	}

	public static String getEndDate(String q) {
		if (q.equals("Q1")) {
			return "2017/3/31";
		}
		if (q.equals("Q2")) {
			return "2017/6/30";
		}
		if (q.equals("Q3")) {
			return "2017/9/30";
		}
		if (q.equals("Q4")) {
			return "2017/12/31";
		}
		return "";
	}

}
