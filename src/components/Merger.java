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
					Page p = mergeSheets(benefits, master, k, i, 0 , 0);
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
					Page p = mergeSheets(benefits, master, k, i, 0 , 0);
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
			int rates_line, int benefits_sheet_number, int rates_sheet_number) throws IOException {
		
		System.out.println("at least the method is being called");
		
		int carrier_id;
		String carrier_plan_id;
		String start_date;
		String end_date;
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
        
        //get benefits 
        benefits_cell = benefits_row.getCell(benefits_column);
        Double carrier_id_double = benefits_cell.getNumericCellValue();
        carrier_id =  carrier_id_double.intValue();
        benefits_column++;
        benefits_column++;
        benefits_column++;
        benefits_column++;
        benefits_column++;
        benefits_column++;
        
        benefits_cell = benefits_row.getCell(benefits_column);
        deductible_indiv = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        deductible_family = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_deductible_indiv = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_deductible_family = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        coinsurance = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        dr_visit_copay = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        specialist_visit_copay = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        er_copay = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        urgent_care_copay = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        rx_copay = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        rx_mail_copay = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oop_max_indiv = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oop_max_family = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_oop_max_indiv = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        oon_oop_max_family = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        in_patient_hospital = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_diagnostic_lab = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_surgery = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_diagnostic_x_ray = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        outpatient_complex_imaging = benefits_cell.getStringCellValue();
        benefits_column++;
        benefits_cell = benefits_row.getCell(benefits_column);
        physical_occupational_therapy = benefits_cell.getStringCellValue();
        
        
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
        non_tob_dict.put("0-20", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("19-20", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        double twenty_one = Double.parseDouble(rates_cell.getStringCellValue());
        non_tob_dict.put("21", twenty_one);
//        String temp_value3 = rates_cell.getStringCellValue();
//        System.out.println(temp_value3);
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("22", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("23", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("24", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("25", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("26", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("27", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("28", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("29", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("30", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("31", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("32", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("33", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("34", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("35", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("36", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("37", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("38", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("39", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("40", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("41", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("42", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("43", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("44", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("45", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("46", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("47", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("48", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("49", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("50", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("51", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("52", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("53", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("54", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("55", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("56", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("57", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("58", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("59", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("60", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("61", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("62", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("63", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("64", rates_cell.getNumericCellValue());
        rates_base_column++;
        rates_cell = rates_row.getCell(rates_base_column);
        non_tob_dict.put("65+", rates_cell.getNumericCellValue());
        
        
        
        
        
        
        
        
        Page page = new Page(carrier_id, "", "", "", product_name, "",
    			deductible_indiv, deductible_family, oon_deductible_indiv, oon_deductible_family,
    	coinsurance, dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay,
    	rx_copay, rx_mail_copay, oop_max_indiv, oop_max_family, oon_oop_max_indiv,
    	oon_oop_max_family, in_patient_hospital, outpatient_diagnostic_lab, outpatient_surgery,
    	outpatient_diagnostic_x_ray, outpatient_complex_imaging, physical_occupational_therapy, group_rating_area,
    	"", "NJ", pages, non_tob_dict, tob_dict);
        
        page.printPage();
           
		
		return page;
	}
	
	
	
}
