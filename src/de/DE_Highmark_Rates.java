package de;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.MedicalPage;
import components.Page;
import components.Parser;
import names.Product_Name.Metal;

public class DE_Highmark_Rates implements Parser {

	Sheet sheet;

	int sheet_index;

	static ArrayList<Page> products;

	static Iterator<Row> iterator;

	static String start_date;

	static String end_date;

	public DE_Highmark_Rates(String s_date, String e_date, int sheet_index) throws IOException {
		this.sheet_index = sheet_index;
		start_date = s_date;
		end_date = e_date;
		products = new ArrayList<Page>();
	}
	
	public ArrayList<Page> parse(File file, String filename) {
		try {
			FileInputStream excelFile = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(excelFile);
			sheet = workbook.getSheetAt(sheet_index);
			iterator = sheet.iterator();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Cell cell;
		int page_index = 1;
		int carrier_id = 15;
		int col_index = 2;
		int row_index = 3;
		Row r = sheet.getRow(5);
		int numRows = sheet.getPhysicalNumberOfRows();
		int numCols = r.getPhysicalNumberOfCells();
		String state = "DE";

		while (col_index < numCols) {
			HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String plan_id = getCellValue(cell);
			System.out.println(plan_id);
			row_index++;
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			try{
				Metal.valueOf(getCellValue(cell));
			}
			catch(IllegalArgumentException e){
				r = sheet.getRow(row_index++);
				cell = r.getCell(col_index);
			}
			String metal = getCellValue(cell);
			System.out.println(metal);
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String plan_name = getCellValue(cell);
			System.out.println(plan_name);
			row_index ++;
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			non_tobacco_dict.put("0-20", cell.getNumericCellValue());
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			for (int i = 21; i < 65; i++) {
				System.out.println(row_index);
				System.out.println(col_index);
				System.out.println(cell.getNumericCellValue());
				non_tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
				r = sheet.getRow(row_index++);
				cell = r.getCell(col_index);
			}
			non_tobacco_dict.put("65+", cell.getNumericCellValue());
			MedicalPage page = new MedicalPage(carrier_id, plan_id, start_date, end_date, plan_name, "", "", "",
					"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
					"", "", state, page_index, non_tobacco_dict, null);
			products.add(page);
			col_index += 2;
			row_index = 3;
			page_index++;
		}
		return products;
	}
}
