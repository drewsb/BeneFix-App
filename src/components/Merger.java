package components;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Main.Carrier;

public class Merger {
	
	public static ArrayList<Page> merge(String path1, String path2, Carrier carrier) throws IOException {
		ArrayList<Page> result;
		switch (carrier) {
		case AmeriHealth: 
			result = mergeAmeriHealthSpreadsheets(path1, path2);
			break;
		case Oxford:
			result = mergeOxfordSpreadsheets(path1, path2);
			break;
		default:
			result = new ArrayList<Page>();
		}
		return result;
	}
	
	public static ArrayList<Page> mergeOxfordSpreadsheets(String path1, String path2) throws IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		FileInputStream master_fis = new FileInputStream(path1);
		FileInputStream benefits_fis = new FileInputStream(path2);
		XSSFWorkbook master = new XSSFWorkbook(master_fis);
		XSSFWorkbook benefits = new XSSFWorkbook(benefits_fis);
		
		XSSFColor xred = new XSSFColor(new java.awt.Color(240, 128, 128));

		XSSFCellStyle highlighter = master.createCellStyle();
		highlighter.setFillForegroundColor(xred);
	    highlighter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    XSSFCellStyle noHighlighter = master.createCellStyle();
	    noHighlighter.setFillPattern(FillPatternType.NO_FILL);
	    
	    XSSFSheet sheet = master.getSheetAt(7);
	    
		//Store all benefits plans and its row number into a map
		XSSFSheet benefits_sheet = benefits.getSheetAt(0);
		int numBenefits = benefits_sheet.getLastRowNum();
		DataFormatter df = new DataFormatter();
		Map<Integer, String> benefits_map = new HashMap<Integer, String>();
		Map<Integer, String> rx_map = new HashMap<Integer, String>();
		for (int i = 0; i <= numBenefits; i++) {
			XSSFRow row = benefits_sheet.getRow(i);
			XSSFCell cell = row.getCell(4);
			String plan = cell.getStringCellValue().toLowerCase();
			plan = plan.replaceAll(",", "");
			benefits_map.put(i, plan);
			XSSFCell rx_cell = row.getCell(15);
			String rx_val = df.formatCellValue(rx_cell);
			rx_map.put(i, rx_val);
		}
		
		int numRows = sheet.getLastRowNum();
		for (int i = 1; i <= numRows; i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(2);
			String name = cell.getStringCellValue().toLowerCase();
			name = name.replaceAll(",", "");
			String[] tokens = name.split(" ");
			boolean matched = false;
			for (int k = 0; k <= numBenefits; k++) {
				String s = benefits_map.get(k);
				String rx_copay_str = rx_map.get(k);
				if (matchesOxford(s, tokens, rx_copay_str)) {
					System.out.println("matched!");
					Page p = null; //Grant's method here
					result.add(p);		
					matched = true;
					cell.setCellStyle(noHighlighter);
					break;
				}
			}
			if (!matched) {
				System.out.println("no result: " + name);
				cell.setCellStyle(highlighter);
			}
		}
		FileOutputStream fos = new FileOutputStream(path1);
		master.write(fos);
		fos.flush();
		fos.close();
		master.close();
		benefits.close();
		return result;
	}
	
	public static boolean matchesOxford(String str, String[] tokens, String rx_copay) {
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			
			if (token.equals("non-gated")) {
				if (!str.contains("ng") && !str.contains("non-gated")) {
					return false;
				}
			} else if (token.equals("w/")) {
				return true;
//				token = tokens[i + 1];
//				if (!rx_copay.contains(token)) {
//					return false;
//				}
//				i++;
			} else if (token.equals("primary")) {
				if (!str.contains("prim adv")) {
					return false;
				} else {
					i++;
				}
			}
			else {
				if (!str.contains(token)) {
					return false;
				}
			}
		}
		return true;
	}
 	
	public static ArrayList<Page> mergeAmeriHealthSpreadsheets(String path1, String path2) throws IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		
		HashMap<String, Set<String>> map = initAmerihealthMap();
		FileInputStream master_fis = new FileInputStream(path1);
		FileInputStream benefits_fis = new FileInputStream(path2);
		XSSFWorkbook master = new XSSFWorkbook(master_fis);
		XSSFWorkbook benefits = new XSSFWorkbook(benefits_fis);
		
		XSSFColor xred = new XSSFColor(new java.awt.Color(240, 128, 128));

		XSSFCellStyle highlighter = master.createCellStyle();
		highlighter.setFillForegroundColor(xred);
	    highlighter.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    XSSFCellStyle noHighlighter = master.createCellStyle();
	    noHighlighter.setFillPattern(FillPatternType.NO_FILL);
	    
	    XSSFSheet sheet = master.getSheetAt(4);
	    //TODO: Later on make sure that the sheet is the right one programmatically
	    
		//Store all benefits plans and its row number into a map
		XSSFSheet benefits_sheet = benefits.getSheetAt(0);
		int numBenefits = benefits_sheet.getLastRowNum();
		DataFormatter df = new DataFormatter();
		Map<Integer, String> benefits_map = new HashMap<Integer, String>();
		Map<Integer, String> rx_map = new HashMap<Integer, String>();
		for (int i = 0; i <= numBenefits; i++) {
			XSSFRow row = benefits_sheet.getRow(i);
			XSSFCell cell = row.getCell(4);
			XSSFCell rx_cell = row.getCell(15);
			String plan = cell.getStringCellValue().toLowerCase();
			benefits_map.put(i, plan);
			String rx_val = df.formatCellValue(rx_cell);
			rx_map.put(i, rx_val);
		}
		
		//String matching 
		int numRows = sheet.getLastRowNum();

		for (int i = 1; i <= numRows; i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(2);
			XSSFCell rx_copay = row.getCell(15);
			rx_copay.setCellType(CellType.STRING);
			String name = cell.getStringCellValue().toLowerCase();
			String[] tokens = name.split(" ");
			boolean matched = false;
			for (int k = 0; k <= numBenefits; k++) {
				String s = benefits_map.get(k);
				String rx_copay_str = rx_map.get(k);
				if (matchesAmerihealth(s, tokens, map, rx_copay_str)) {
					System.out.println("matched!");
					Page p = null; //Grant's method here
					result.add(p);		
					matched = true;
					cell.setCellStyle(noHighlighter);
					break;
				}
			}
			if (!matched) {
				System.out.println("no result: " + name);
				cell.setCellStyle(highlighter);
			}
		}
		FileOutputStream fos = new FileOutputStream(path1);
		master.write(fos);
		fos.flush();
		fos.close();
		master.close();
		benefits.close();
		return result;
	}
	
	//Tests if all potential tokens in a String array are contained within a source string
	private static boolean matchesAmerihealth(String str, String[] tokens, HashMap<String, Set<String>> map, String rx_copay) {
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			token = token.replaceAll(",", "");
			if (token.equals("local") || token.equals("regional") || token.equals("national")
					|| token.equals("amerihealth")) {
				Set<String> res = map.get(token);
				if (!containsSet(str, res)) {
					return false;
				} else {
					i++;
				}
			} else if (token.equals("h.s.a")) {
				if (!str.contains("hsa")) {
					return false;
				}
			} else if (token.equals("seh") || token.equals("(6)") || token.equals("(7)") 
					|| token.equals("coins") || token.equals("advantage") || token.equals("value")) {
				continue;
			}  else if (token.equals("100%/100%")) {
				if (!str.contains("100%")) {
					return false;
				}
			}  else if (token.equals("90%/90%")) {
				if (!str.contains("90%")) {
					return false;
				}
			} else if (token.equals("tier")){
				if (!str.contains("tier1") && !str.contains("tier 1")) {
					System.out.println("Hits here");
					return false;
				} else {
					i++;
				}
			} else if (token.equals("rx")) {
				token = tokens[i + 1];
				String[] rx_comps = token.split("/");
				for (int j = 0; j < rx_comps.length; j++) {
					System.out.print(rx_comps[j] + " ");
				}
				System.out.println("");
				System.out.println("Rx copay string: " + rx_copay);
 				for (int j = 0; j < rx_comps.length; j++) {
					if (!rx_copay.contains(rx_comps[j])) {
						return false;
					}
				}
				i++;
			} else if (token.equals("max")) {
				if (!rx_copay.contains("max")) {
					return false;
				}
			} else if (map.containsKey(token)) { 
				Set<String> vals = map.get(token);
				if (!containsSet(str, vals)) {
					return false;
				}
			} else {
				if (!str.contains(token)) {
					return false;
				}
			}
					
		}
		return true;
	}
	
	private static boolean containsSet(String str, Set<String> set) {
		for (String s: set) {
			if (str.contains(s)) {
				return true;
			}
		}
		return false;
	}
	private static HashMap<String, Set<String>> initAmerihealthMap() {
		HashMap<String, Set<String>> map = new HashMap<String, Set<String>>();
		
		//SEH
		Set<String> seh = new HashSet<String>();
		seh.add("");
		map.put("seh", seh);
		
		//Gold
		Set<String> gold = new HashSet<String>();
		gold.add("gold");
		gold.add("gld");
		map.put("gold", gold);
		
		//Bronze
		Set<String> bronze = new HashSet<String>();
		bronze.add("bnz");
		bronze.add("bronze");
		map.put("bronze", bronze);
		
		//Silver
		Set<String> silver = new HashSet<String>();
		silver.add("silver");
		silver.add("slv");
		map.put("silver", silver);
		
		//Platinum
		Set<String> platinum = new HashSet<String>();
		platinum.add("platinum");
		platinum.add("plt");
		map.put("platinum", platinum);
		
		//Local
		Set<String> local = new HashSet<String>();
		local.add("local");
		local.add("val");
		local.add("value");
		map.put("local", local);
		
		//Regional
		Set<String> regional = new HashSet<String>();
		regional.add("prefd");
		regional.add("preferred");
		regional.add("pfd");
		regional.add("pref");
		map.put("regional", regional);
		
		//National
		Set<String> national = new HashSet<String>();
		national.add("ntl");
		national.add("national");
		map.put("national", national);
		
		//Plus
		Set<String> plus = new HashSet<String>();
		plus.add("+");
		map.put("plus", plus);
		
		//Amerihealth
		Set<String> amerihealth = new HashSet<String>();
		amerihealth.add("ah");
		map.put("amerihealth", amerihealth);
		
		//Advantage
		Set<String> advantage = new HashSet<String>();
		advantage.add("advantage");
		advantage.add("advntg");
		map.put("advantage", advantage);
		//pos plt pos+ val $20/$40/90% 
		return map;
	}
	
	
	
}
