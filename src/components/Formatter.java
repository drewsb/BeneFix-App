package components;

public class Formatter {

	public static StringBuilder removeString(StringBuilder s, String r) {
		while (s.indexOf(r) != -1) {
			int index = s.indexOf(r);
			s.replace(index, index + r.length(), "");
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

	public static StringBuilder[] removeStringsFromSet(StringBuilder[] set, String[] delims) {
		for (StringBuilder s : set) {
			for (String r : delims) {
				while (s.indexOf(r) != -1) {
					int index = s.indexOf(r);
					s.replace(index, index + r.length(), "");
				}
			}
		}
		return set;
	}

	public static StringBuilder removeCommas(StringBuilder s) {
		while (s.indexOf(",") != -1) {
			int index = s.indexOf(",");
			s.replace(index, index + 1, "");
		}
		return s;
	}
	
	public static StringBuilder formatValue(StringBuilder s) {
		String[] delims = {",", "$"};
		return removeStrings(s,delims);
	}
	
	public static String formatValue(String s) {
		return s.replaceAll("[$,]", "");
	}

}
