package components;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	/*
	 * This class only handles single page Excel sheets, issues occur when giving it a multi-sheet Excel file and attempting to 
	 * select the correct sheet.
	 */
	
	public static ArrayList<Page> merge(String path1, String path2, Carrier carrier) throws IOException {
		ArrayList<Page> result;
		switch (carrier) {
		case AmeriHealth: 
			result = mergeAmeriHealthSpreadsheets(path1, path2);
			break;
		case Oxford:
			result = mergeOxfordSpreadsheets(path1, path2);
			break;
		case Aetna:
			result = mergeAetnaSpreadsheets(path1, path2);
			break;
		case Horizon:
			result = mergeHorizonSpreadsheets(path1, path2);
			break;
		default:
			result = new ArrayList<Page>();
		}
		return result;
	}
	
	public static ArrayList<Page> mergeHorizonSpreadsheets(String path1, String path2) throws IOException {
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
	    
	    XSSFSheet sheet = master.getSheetAt(0);
	    
		//Store all benefits plans and its row number into a map
		XSSFSheet benefits_sheet = benefits.getSheetAt(0);
		int numBenefits = benefits_sheet.getLastRowNum();
		DataFormatter df = new DataFormatter();
		Map<Integer, String> benefits_map = new HashMap<Integer, String>();
		Map<Integer, String> rx_map = new HashMap<Integer, String>();
		for (int i = 1; i <= numBenefits; i++) {
			XSSFRow row = benefits_sheet.getRow(i);
			XSSFCell cell = row.getCell(4);
			String plan = cell.getStringCellValue().toLowerCase();
			plan = plan.replaceAll(",", "");
			benefits_map.put(i, plan);
			System.out.println("Plan: " + plan);
			XSSFCell rx_cell = row.getCell(15);
			String rx_val = df.formatCellValue(rx_cell);
			rx_map.put(i, rx_val);
		}
		
		int numRows = sheet.getLastRowNum();
		for (int i = 1; i <= numRows; i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell cell = row.getCell(4);
			if (cell == null) {
				break;
			}
			String name = cell.getStringCellValue().toLowerCase();
			name = name.replaceAll(",", "");
			System.out.println("Testing..." + name + " at line " + i);
			String[] tokens = name.split(" ");
			boolean matched = false;
			for (int k = 1; k <= numBenefits; k++) {
				String s = benefits_map.get(k);
				String rx_copay_str = rx_map.get(k);
 				if (matchesHorizon(s, tokens, rx_copay_str)) {
					System.out.println("matched with: " + s);
					Page p = mergeMedical(master, benefits, i, k);
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
	
	public static boolean matchesHorizon(String str, String[] tokens, String rx_copay) {
//		System.out.println("String to match: " + str);
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (token.contains("/")) {
				if (token.contains("(g")) {
					return true;
				} else {
					String[] token_comps = token.split("/");
					for (int j = 0; j < token_comps.length; j++) {
						if (!str.contains(token_comps[j])) {
//							System.out.println("Fails on split: " + token_comps[j]);
							return false;
						}
					}
				}
			} else if (token.equals("bluecard")) {
				if (!str.contains("bluecard") && !str.contains("blue card") && !str.contains("bl")) {
//					System.out.println("Fails on bluecard");
					return false;
				} else {
					i++;
				}
			} else if (token.contains("p") && token.contains(")")) {
				return true;
			}else {
				if (!str.contains(token)) {
//					System.out.println("Fails on " + token);
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static ArrayList<Page> mergeAetnaSpreadsheets(String path1, String path2) throws IOException {
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
	    
	    XSSFSheet sheet = master.getSheetAt(0);
	    
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
			if (cell == null) {
				break;
			}
			String name = cell.getStringCellValue().toLowerCase();
			name = name.replaceAll(",", "");
			System.out.println("Testing..." + name + " at line " + i);
			String[] tokens = name.split(" ");
			boolean matched = false;
			for (int k = 0; k <= numBenefits; k++) {
				String s = benefits_map.get(k);
				String rx_copay_str = rx_map.get(k);
				if (matchesAetna(s, tokens, rx_copay_str)) {
					System.out.println("matched with: " + s);
					Page p = mergeSheets(benefits, master, k, i, 0 , 0, Carrier.Aetna);
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
	
	public static boolean matchesAetna(String str, String[] tokens, String rx_copay) {
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (token.contains("/")) {
				String[] token_comps = token.split("/");
				for (int j = 0; j < token_comps.length; j++) {
					if (!str.contains(token_comps[j] + " ")) {
						return false;
					}
				}
			} else if (token.contains("%")) {
				token = token.replaceAll("%", "_");
				if (!str.contains(token)) {
					return false;
				}
			} else if (token.equals("(6)") || token.equals("(7)")) {
				continue;
			}
			else {
				if (!str.contains(token)) {
					return false;
				}
			}
		}
		return true;
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
	    
	    XSSFSheet sheet = master.getSheetAt(0);
	    
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
					Page p = mergeSheets(benefits, master, k, i, 0 , 0, Carrier.Oxford);
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
			} else if (token.equals("gated"))  {
				if (!str.contains("gated") && !str.contains(" g ")) {
					return false;
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
	    
	    XSSFSheet sheet = master.getSheetAt(0);
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
					Page p = mergeSheets(benefits, master, k, i, 0 , 0, Carrier.AmeriHealth);
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

	public static Page mergeSheets(XSSFWorkbook benefits, XSSFWorkbook rates, int benefits_line,
			int rates_line, int benefits_sheet_number, int rates_sheet_number, Carrier carrier_type) throws IOException {
		
		DataFormatter df = new DataFormatter();
		
		int carrier_id;
		String carrier_plan_id;
		String start_date = "7/1/2017"; //these dates are temporary until 
		String end_date = "9/31/2017";	//we build logic to handle different quarters
		String product_name;
		String plan_pdf_file_name;
		String deductible_indiv;
		String deductible_family;
		String oon_deductible_indiv;
		String oon_deductible_family;
		String coinsurance;
		String dr_visit_copay;
		String specialist_visit_copay;
		String er_copay; 
		String urgent_care_copay;
		String rx_copay; 
		String rx_mail_copay; 
		String oop_max_indiv; 
		String oop_max_family; 
		String oon_oop_max_indiv;
		String oon_oop_max_family; 
		String in_patient_hospital; 
		String outpatient_diagnostic_lab; 
		String outpatient_surgery;
		String outpatient_diagnostic_x_ray; 
		String outpatient_complex_imaging; 
		String physical_occupational_therapy; 
		String group_rating_area = "";
		String service_zones;
		String state;
		int pages = 0;  
		HashMap<String,Double> non_tob_dict = new HashMap<String, Double>(); 
		HashMap<String,Double> tob_dict = new HashMap<String, Double>();
		
        XSSFWorkbook benefits_workbook = benefits;
        XSSFWorkbook rates_workbook = rates;
        XSSFSheet benefits_sheet = benefits_workbook.getSheetAt(benefits_sheet_number);
        XSSFSheet rates_sheet = rates_workbook.getSheetAt(rates_sheet_number);
        XSSFCell benefits_cell;
        XSSFCell rates_cell;
        XSSFRow benefits_row = benefits_sheet.getRow(benefits_line);
        XSSFRow rates_row = rates_sheet.getRow(rates_line);
        XSSFRow rates_row_zero = rates_sheet.getRow(0);
        
        int benefits_column = 0;
        int rates_name_column = 0;
        int rates_base_column = 0;
        int rates_gra_column = 0;
        
        //set carrier
        switch (carrier_type) {
		case AmeriHealth: 
			carrier_id = 20;
			break;
		case Oxford:
			carrier_id = 18;
			break;
		case Aetna:
			carrier_id = 10;
			break;
		case Horizon:
			carrier_id = 19;
			break;
		default:
			carrier_id = 0;
		}
        
        //get benefits 
        benefits_column++;
        benefits_column++;
        benefits_column++;
        benefits_column++;
        benefits_column++;
        benefits_column++;
        
        benefits_cell = benefits_row.getCell(benefits_column);
        deductible_indiv = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        deductible_family = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_deductible_indiv = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_deductible_family = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        coinsurance = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        dr_visit_copay = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        specialist_visit_copay = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        er_copay = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        urgent_care_copay = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        rx_copay = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        rx_mail_copay = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oop_max_indiv = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oop_max_family = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_oop_max_indiv = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_oop_max_family = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        in_patient_hospital = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_diagnostic_lab = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_surgery = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_diagnostic_x_ray = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_complex_imaging = df.formatCellValue(benefits_cell);
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        physical_occupational_therapy = df.formatCellValue(benefits_cell);
        
        //get rates
        rates_row = rates_sheet.getRow(0);
        rates_cell = rates_row.getCell(0);
        
        while (!(rates_cell.getStringCellValue().toLowerCase().contains("group"))) {
        	rates_gra_column++;
        	rates_cell = rates_row.getCell(rates_gra_column);
        }
        rates_row = rates_sheet.getRow(rates_line);
        rates_cell = rates_row.getCell(rates_gra_column);
        
        if (rates_cell.getCellTypeEnum() == CellType.NUMERIC) {
        	group_rating_area = Double.toString(rates_cell.getNumericCellValue());
        } else if (rates_cell.getCellTypeEnum() == CellType.STRING) {
        	group_rating_area = rates_cell.getStringCellValue();
        }
        
        rates_row = rates_sheet.getRow(0);
        rates_cell = rates_row.getCell(0);
        
        while (!(rates_cell.getStringCellValue().toLowerCase().contains("base"))) {
        	rates_base_column++;
        	rates_cell = rates_row.getCell(rates_base_column);
        }
        rates_row = rates_sheet.getRow(rates_line);
        
        rates_cell = rates_row.getCell(2);
        product_name = rates_cell.getStringCellValue();
        
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("0-20", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("19-20", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        
        if (rates_cell.getCellTypeEnum() == CellType.STRING) {
        	double twenty_one = Double.parseDouble(rates_cell.getStringCellValue());
            non_tob_dict.put("21", round(twenty_one, 2));
        } else {
        	non_tob_dict.put("21", round(rates_cell.getNumericCellValue(), 2));
        }
        
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("22", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("23", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("24", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("25", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("26", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("27", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("28", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("29", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("30", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("31", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("32", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("33", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("34", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("35", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("36", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("37", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("38", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("39", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("40", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("41", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("42", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("43", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("44", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("45", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("46", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("47", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("48", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("49", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("50", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("51", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("52", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("53", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("54", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("55", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("56", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("57", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("58", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("59", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("60", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("61", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("62", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("63", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("64", round(rates_cell.getNumericCellValue(), 2));
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("65+", round(rates_cell.getNumericCellValue(), 2));
        
        
        
        Page page = new MedicalPage(carrier_id, "", "", "", product_name, "",
    			deductible_indiv, deductible_family, oon_deductible_indiv, oon_deductible_family,
    	coinsurance, dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay,
    	rx_copay, rx_mail_copay, oop_max_indiv, oop_max_family, oon_oop_max_indiv,
    	oon_oop_max_family, in_patient_hospital, outpatient_diagnostic_lab, outpatient_surgery,
    	outpatient_diagnostic_x_ray, outpatient_complex_imaging, physical_occupational_therapy, group_rating_area,
    	"", "NJ", pages, non_tob_dict, tob_dict);
        
        //page.printPage();
		return page;
	}
	
	public static void compareAetnaWorkbooks(String path1, String path2) throws IOException {
		System.out.println(path1 + " " + path2);
		final String output = path1;
		final String otherWorkbook = path2;
		FileInputStream fis = new FileInputStream(output);
		FileInputStream fis2 = new FileInputStream(otherWorkbook);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFWorkbook workbook2 = new XSSFWorkbook(fis2);

		XSSFColor xred = new XSSFColor(new java.awt.Color(240, 128, 128));
		XSSFColor xgreen = new XSSFColor(new java.awt.Color(0, 255, 127));
		XSSFColor xblue = new XSSFColor(new java.awt.Color(135, 206, 250));

		XSSFCellStyle style1 = workbook2.createCellStyle();
		style1.setFillForegroundColor(xred);
		style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(xgreen);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle style2a = workbook2.createCellStyle();
		style2a.cloneStyleFrom(style2);
		style2a.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle style3 = workbook.createCellStyle();
		style3.setFillForegroundColor(xblue);
		style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFCellStyle noHighlight = workbook.createCellStyle();
		noHighlight.setFillPattern(FillPatternType.NO_FILL);
		XSSFCellStyle noHighlight2 = workbook2.createCellStyle();
		noHighlight2.cloneStyleFrom(noHighlight);

		XSSFSheet sheet = workbook.getSheetAt(0);
		XSSFSheet srcSheet = workbook2.getSheetAt(0);
		int numRows = srcSheet.getLastRowNum();
		int index = 1;
		System.out.println("Numrows: " + srcSheet.getLastRowNum());
		for (int i = 1; i <= numRows; i++) {
			System.out.println("First index: " + index + " Second index: " + i);
			XSSFRow row = sheet.getRow(index);
			XSSFRow srcRow = srcSheet.getRow(i);
			XSSFCell cell = row.getCell(0);
			XSSFCell cell2 = srcRow.getCell(0);
			String productName = cell.getStringCellValue().toLowerCase().replaceAll("\\s", "");
			String productName2 = cell2.getStringCellValue().toLowerCase().replaceAll("\\s", "");
			if (productName2.contains("(6)") || productName2.contains("(7)")) {
				productName2 = productName2.substring(0, productName2.length() - 3);
			}
			String ratingarea1;
			if (row.getCell(1).getCellTypeEnum() == CellType.STRING) {
				ratingarea1 = row.getCell(1).getStringCellValue().substring(5);

			} else {
				System.out.println(row.getCell(1).getNumericCellValue());
				ratingarea1 = Integer.toString((int) row.getCell(1).getNumericCellValue()).substring(5);
			}
			System.out.println(ratingarea1);
			productName = productName + ratingarea1;
			String ratingarea2 = Integer.toString((int) srcRow.getCell(1).getNumericCellValue()).substring(2);
			productName2 = productName2 + ratingarea2;
			System.out.println("Name 1: " + productName + " , Name 2: " + productName2);

			int result = productName.compareTo(productName2);
			System.out.println("Compare to result: " + result);
			if (result == 0) {
				// 49
				cell.setCellStyle(noHighlight);
				cell2.setCellStyle(noHighlight2);
				System.out.println("Reaches here");
				for (int j = 2; j < 49; j++) {
					XSSFCell c1 = row.getCell(j);
					XSSFCell c2 = srcRow.getCell(j);
					System.out.println("Index: " + j);
					if (c1.getNumericCellValue() != c2.getNumericCellValue()) {
						c1.setCellStyle(style2);
						c2.setCellStyle(style2a);
					} else {
						c1.setCellStyle(noHighlight);
						c2.setCellStyle(noHighlight2);
					}
				}
				index++;
			} else if (result > 0) {
				System.out.println("Reaches here instead");
				cell2.setCellStyle(style1);
			} else {
				System.out.println("Reaches here thirdly");
				i--;
				index++;
				cell.setCellStyle(style3);
				System.out.println(cell.getCellStyle().getFillForegroundXSSFColor().getARGB()); // .getFillBackgroundXSSFColor().getRGB()[2]
			}
		}
		if (index < sheet.getLastRowNum()) {
			for (int i = index; i < sheet.getLastRowNum(); i++) {
				// Highlight the remaining cells
				XSSFRow c1row = sheet.getRow(i);
				c1row.getCell(0).setCellStyle(style3);
			}
		}
		FileOutputStream outputStream;
		FileOutputStream outputStream2;
		try {
			outputStream = new FileOutputStream(output);
			outputStream2 = new FileOutputStream(otherWorkbook);
			workbook.write(outputStream);
			workbook2.write(outputStream2);
			outputStream.flush();
			outputStream2.flush();
			outputStream.close();
			outputStream2.close();
			fis.close();
			fis2.close();
			workbook.close();
			workbook2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static double round(double value, int decimal_place) {
	    if (decimal_place < 0) 
	    	throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(decimal_place, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static Page mergeMedical(XSSFWorkbook rates, XSSFWorkbook benefits, int rates_line, int benefits_line) {
		DataFormatter df = new DataFormatter();
		Page page = new MedicalPage();
		XSSFSheet ratesSheet = rates.getSheetAt(0);
		XSSFSheet benefitsSheet = benefits.getSheetAt(0);
		XSSFRow ratesRow = ratesSheet.getRow(rates_line);
		XSSFRow benefitsRow = benefitsSheet.getRow(benefits_line);
		
		for (int i = 6; i < 27; i++) {
			XSSFCell ratesCell = ratesRow.getCell(i);
			XSSFCell benefitsCell = benefitsRow.getCell(i);
			
			String data = "";
			if (benefitsCell.getCellTypeEnum() == CellType.NUMERIC) {
				data = String.valueOf(Formatter.formatValue(df.formatCellValue(benefitsCell)));
			} else {
				data = benefitsCell.getStringCellValue();
			}
			
			ratesCell.setCellValue(data);
		}
		
		
		return page;
	}
	
	
}
