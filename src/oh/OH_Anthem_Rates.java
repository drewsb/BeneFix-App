package oh;

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
public class OH_Anthem_Rates implements Parser {

	Sheet sheet;

	static ArrayList<MedicalPage> products;

	static Iterator<Row> iterator;

	static String start_date;

	static String end_date;

	private Workbook workbook;

	public OH_Anthem_Rates(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
		products = new ArrayList<MedicalPage>();
	}

	public ArrayList<Page> parse(File file, String filename) {
		ArrayList<Page> products = new ArrayList<Page>();
		try {
			FileInputStream excelFile = new FileInputStream(file);
			workbook = new XSSFWorkbook(excelFile);
			this.sheet = workbook.getSheetAt(0);
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
		int row_index = 1;
		int col_index = 2;
		Row r = sheet.getRow(row_index);
		cell = r.getCell(col_index);
		String contract_code = "";

		int numRows = getNumRows(sheet);
		int numCols = r.getPhysicalNumberOfCells();

		System.out.println(numRows);
		System.out.println(numCols);

		String state = "PA";

		while (cell != null) {
			HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
			Double val = 0.0;
			col_index = 2;

			try {
				r = sheet.getRow(row_index);
				cell = r.getCell(col_index++);
				contract_code = getCellValue(cell);

				cell = r.getCell(col_index++);
				plan_id = getCellValue(cell);

				cell = r.getCell(++col_index);
				rating_area = getCellValue(cell);
			} catch (NullPointerException e) {
				return products;
			}

			System.out.println(contract_code);
			col_index = 8;
			int ageBand = 0;

			while (ageBand < 65) {
				r = sheet.getRow(row_index++);
				Double x = r.getCell(7).getNumericCellValue();
				ageBand = x.intValue();
				if (ageBand == 17) {
					continue;
				}
				cell = r.getCell(col_index);
				val = Double.parseDouble(getCellValue(cell));
				
				System.out.println(ageBand);
				System.out.println(val);
				if (ageBand == 18) {
					non_tobacco_dict.put("0-18", val);
				} else if (ageBand == 19) {
					non_tobacco_dict.put("19-20", val);
				} else if (ageBand == 110) {
					non_tobacco_dict.put("64", val);
				} else {
					non_tobacco_dict.put(String.valueOf(ageBand), val);
				}
			}
			non_tobacco_dict.put("65+", val);
			MedicalPage page = new MedicalPage(carrier_id, plan_id, "", "", "", "", "", "", "", "", "", "", "", "", "",
					"", "", "", "", "", "", "", "", "", "", "", "", rating_area, "", state, 0, non_tobacco_dict, null);
			page.setContractCode(contract_code);
			products.add(page);
		
			row_index++;
		}
		return products;
	}

}
