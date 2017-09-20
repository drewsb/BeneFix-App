package nj;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;

//import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Formatter;
import components.MedicalPage;
import components.Page;
import components.Parser;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class NJ_Oxford_Rates implements Parser {

	Sheet sheet;

	int sheet_index;

	static ArrayList<MedicalPage> products;

	static Iterator<Row> iterator;

	static String start_date;

	static String end_date;

	private Workbook workbook;

	public NJ_Oxford_Rates(String s_date, String e_date, int sheet_index) throws IOException {
		this.sheet_index = sheet_index;
		start_date = s_date;
		end_date = e_date;
		products = new ArrayList<MedicalPage>();
	}

	public ArrayList<Page> parse(File file, String filename) {
		ArrayList<Page> products = new ArrayList<Page>();
		try {
			FileInputStream excelFile = new FileInputStream(file);
			workbook = new XSSFWorkbook(excelFile);
			this.sheet = workbook.getSheetAt(sheet_index);
			iterator = sheet.iterator();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int carrier_id = 9;
		String plan_id = "";
		String rating_area = "";

		Cell cell;
		String temp;
		int row_index = 0;
		int col_index = 0;
		Row r = sheet.getRow(row_index);
		cell = r.getCell(col_index);
		String cell_val = cell.getStringCellValue();
		while (!cell_val.toLowerCase().contains("plan")) {
			r = sheet.getRow(++row_index);
			if (r.getCell(col_index) == null) {
				cell = r.createCell(col_index);
			} else {
				cell = r.getCell(col_index);
			}
			cell_val = cell.getStringCellValue();
		}
		r = sheet.getRow(row_index += 2);
		cell = r.getCell(col_index);

		while (cell != null) {
			col_index = 0;
			HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
			Double val = 0.0;

			try {
				r = sheet.getRow(row_index);
				cell = r.getCell(col_index++);
				plan_id = getCellValue(cell);

				cell = r.getCell(col_index);
				rating_area = getCellValue(cell);
			} catch (NullPointerException e) {
				return products;
			}

			col_index = 4;
			String ageBand = "";

			while (!ageBand.contains("65")) {
				System.out.println(row_index);
				r = sheet.getRow(row_index++);
				ageBand = getCellValue(r.getCell(3));
				cell = r.getCell(col_index);
				
				val = Double.parseDouble(getCellValue(cell));

				if (ageBand.equals("0-20")) {
					non_tobacco_dict.put("0-18", val);
					non_tobacco_dict.put("19-20", val);
				} else {
					non_tobacco_dict.put(Formatter.removeDecimal(ageBand), val);
				}
			}
			non_tobacco_dict.put("65+", val);
			MedicalPage page = new MedicalPage(carrier_id, plan_id, "", "", "", "", "", "", "", "", "", "", "", "", "",
					"", "", "", "", "", "", "", "", "", "", "", "", rating_area, "", "NJ", 0, non_tobacco_dict, null);
			products.add(page);

			page.printPage();
		}
		return products;
	}

}
