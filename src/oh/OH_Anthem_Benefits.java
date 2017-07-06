package oh;

import java.io.File;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.*;

public class OH_Anthem_Benefits implements Parser{

	static ArrayList<Page> plans;

	static Sheet sheet;

	String start_date;

	String end_date;

	public OH_Anthem_Benefits(String s_date, String e_date) {
		start_date = s_date;
		end_date = e_date;
		plans = new ArrayList<Page>();
	}

	public ArrayList<Page> parse(File file, String filename) throws EncryptedDocumentException, InvalidFormatException {
		try {
			FileInputStream inp = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(inp);
			sheet = workbook.getSheetAt(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Cell cell;
		int carrier_id = 15; // Change this
		int col_index = 0;
		int row_index = 1;
		Row r;
		int numRows = sheet.getPhysicalNumberOfRows();
		String state = "OH";

		String temp_copay = "";
		String temp_coins = "";
		String temp_max = "";
		
		String contract_code = "";
		String product = "";
		String carrier_plan_id = "";
		String plan_pdf_file_name = filename;
		String deductible_indiv = "";
		String deductible_family = "";
		String oon_deductible_indiv = "";
		String oon_deductible_family = "";
		String coinsurance = "";
		String dr_visit_copay = "";
		String specialist_visit_copay = "";
		String er_copay = "";
		String urgent_care_copay = "";
		String rx_copay = "";
		String rx_mail_copay = "";
		String oop_max_indiv = "";
		String oop_max_family = "";
		String oon_oop_max_indiv = "";
		String oon_oop_max_family = "";
		String in_patient_hospital = "";
		String outpatient_diagnostic_lab = "";
		String outpatient_surgery = "";
		String outpatient_diagnostic_x_ray = "";
		String outpatient_complex_imaging = "";
		String physical_occupational_therapy = "";
		String group_rating_area = "";
		String service_zone = "";
		HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
		HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

		while (row_index < numRows) {
			r = sheet.getRow(row_index);

			// Contract code
			cell = r.getCell(col_index);
			contract_code = getCellValue(cell);
			col_index++;

			// Service zone
			cell = r.getCell(col_index);
			service_zone = getCellValue(cell);
			col_index++;

			// Carrier plan id
			cell = r.getCell(col_index);
			carrier_plan_id = getCellValue(cell);
			col_index += 6;

			// Product
			cell = r.getCell(col_index);
			product = getCellValue(cell);
			col_index += 5;

			// Deductible indiv
			cell = r.getCell(col_index);
			deductible_indiv = getCellValue(cell);
			col_index++;

			// Deductible family
			cell = r.getCell(col_index);
			deductible_family = getCellValue(cell);
			col_index += 3;

			// Oon deductible indiv
			cell = r.getCell(col_index);
			oon_deductible_indiv = getCellValue(cell);
			col_index++;

			// Oon deductible family
			cell = r.getCell(col_index);
			oon_deductible_family = getCellValue(cell);
			col_index += 3;

			// Oop max indiv
			cell = r.getCell(col_index);
			oop_max_indiv = getCellValue(cell);
			col_index++;

			// Oop max family
			cell = r.getCell(col_index);
			oop_max_family = getCellValue(cell);
			col_index++;

			// Oon oop max indiv
			cell = r.getCell(col_index);
			oon_oop_max_indiv = getCellValue(cell);
			col_index++;

			// Oon oop max family
			cell = r.getCell(col_index);
			oon_oop_max_family = getCellValue(cell);
			col_index += 2;

			// Dr visit copay
			cell = r.getCell(col_index++);
			dr_visit_copay = getCellValue(cell);
			cell = r.getCell(col_index);
			temp_coins = getCellValue(cell);
			if (temp_coins.isEmpty()) {
				temp_coins = "0%";
			}
			dr_visit_copay = formatString(dr_visit_copay, temp_coins);
			col_index += 6;

			//coinsurance
			cell = r.getCell(col_index);
			coinsurance = getCellValue(cell);			
			col_index+=11;
			
			// Specialist visit copay
			cell = r.getCell(col_index++);
			specialist_visit_copay = getCellValue(cell);
			cell = r.getCell(col_index);
			temp_coins = getCellValue(cell);
			if (temp_coins.isEmpty()) {
				temp_coins = "0%";
			}
			specialist_visit_copay = formatString(specialist_visit_copay, temp_coins);
			col_index += 14;
			
			// Inpatient hospital
			cell = r.getCell(col_index++);
			in_patient_hospital = getCellValue(cell);
			cell = r.getCell(col_index);
			temp_coins = getCellValue(cell);
			in_patient_hospital = formatString(in_patient_hospital, temp_coins);
			col_index += 10;

			// Er copay
			cell = r.getCell(col_index++);
			er_copay = getCellValue(cell);
			cell = r.getCell(col_index);
			temp_coins = getCellValue(cell);
			er_copay = formatString(er_copay, temp_coins);
			col_index += 8;

			// Urgent copay
			cell = r.getCell(col_index++);
			urgent_care_copay = getCellValue(cell);
			cell = r.getCell(col_index);
			temp_coins = getCellValue(cell);
			urgent_care_copay = formatString(urgent_care_copay, temp_coins);
			col_index += 48;

			//Rx_copay
			for(int i = 0; i < 5; i++){
				cell = r.getCell(col_index++);
				temp_copay = getCellValue(cell);
				System.out.println(temp_copay);
				cell = r.getCell(col_index++);
				temp_coins = getCellValue(cell);
				cell = r.getCell(col_index);
				temp_max = getCellValue(cell);
				if(temp_copay.isEmpty() & temp_coins.isEmpty() & temp_max.isEmpty()){
					rx_copay = coinsurance;
					break;
				}
				if(temp_copay.isEmpty()){
					rx_copay += String.format("%s coinsurance ($%s max)", temp_coins, temp_max);
				}
				else{
					rx_copay += String.format("$%s", temp_copay);
				}
				if(i!= 4){
					rx_copay += "/";
				}
				col_index += 2;
			}
			
			
			MedicalPage new_page = new MedicalPage(carrier_id, carrier_plan_id, "", "", product, plan_pdf_file_name, deductible_indiv,
					deductible_family, oon_deductible_indiv, oon_deductible_family, coinsurance, dr_visit_copay,
					specialist_visit_copay, er_copay, urgent_care_copay, rx_copay, rx_mail_copay, oop_max_indiv,
					oop_max_family, oon_oop_max_indiv, oon_oop_max_family, in_patient_hospital,
					outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray,
					outpatient_complex_imaging, physical_occupational_therapy, "", service_zone, "", 0,
					non_tobacco_dict, tobacco_dict);
			new_page.printPage();
			plans.add(new_page);
			row_index+=16;
			col_index = 0;
		}
		// for(WPA_Page p : products){
		// p.printPage();
		// }
		return plans;
	}
	
	public String formatString(String s, String temp_coins){
		if (!s.isEmpty()) {
			s = String.format("$%s (%s coinsurance)", s, temp_coins);
		} else {
			s = String.format("%s coinsurance", temp_coins);
		}
		return s;
	}
}
