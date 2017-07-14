package pa;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Formatter;
import components.MedicalPage;
import components.Page;
import components.Parser;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class PA_UHC_Rates implements Parser {

	static ArrayList<Page> products;

	static Sheet sheet;

	static Iterator<Row> iterator;

	static String start_date;

	static String end_date;

	static int sheet_index;

	HashMap<String, String> codeMap;

	public PA_UHC_Rates(int s_index, String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
		sheet_index = s_index;

		codeMap = new HashMap<String, String>();
		codeMap.put("UHC Choice 100 Gold 1000", "AK9T");
		codeMap.put("UHC Choice Direct Gold $1,000-1", "");
		codeMap.put("UHC Choice Direct Gold $1,000-2", "AC4W");
		codeMap.put("UHC Choice Direct Gold $1,500", "AC1S");
		codeMap.put("UHC Choice Direct Gold 500", "AC1O");
		codeMap.put("UHC Choice Direct HSA Gold", "$1,500");
		codeMap.put("UHC Choice Direct HSA Gold 1400", "AK96");
		codeMap.put("UHC Choice Direct Platinum 250", "AC41");
		codeMap.put("UHC Choice Direct Platinum 0-1", "AC1K");
		codeMap.put("UHC Choice Direct Platinum 0-2", "ALAB");
		codeMap.put("UHC Choice Direct Platinum 0-3", "AK99");
		codeMap.put("UHC Choice HSA Bronze 5600", "AK9Q");
		codeMap.put("UHC Choice HSA Bronze 6500", "AK9R");
		codeMap.put("UHC Choice HSA Gold 1400", "AK9Y");
		codeMap.put("UHC Choice HSA Silver 1700-1", "AK9I");
		codeMap.put("UHC Choice HSA Silver 2000", "AK9O");
		codeMap.put("UHC Choice HSA Silver 2500-1", "AK9M");
		codeMap.put("UHC Choice Plus 100 Gold 1000", "AK9S");
		codeMap.put("UHC Choice Plus Direct Gold 1000-1", "AC1R");
		codeMap.put("UHC Choice Plus Direct Gold 1000-2", "AC4X");
		codeMap.put("UHC Choice Plus Direct Gold 1500", "AC1T");
		codeMap.put("UHC Choice Plus Direct Gold 500", "AC1P");
		codeMap.put("UHC Choice Plus Direct HSA Gold $1,500", "");
		codeMap.put("UHC Choice Plus Direct HSA Gold 1400", "AK97");
		codeMap.put("UHC Choice Plus Direct Platinum 250", "AC42");
		codeMap.put("UHC Choice Plus Direct Platinum 0-1", "AM8P");
		codeMap.put("UHC Choice Plus Direct Platinum 0-2", "ALAC");
		codeMap.put("UHC Choice Plus Direct Platinum 0-3", "AK98");
		codeMap.put("UHC Choice Plus HSA Gold 1400 AK9Z", "");
		codeMap.put("UHC Choice Plus HSA Silver 1700-2", "AK9J");
		codeMap.put("UHC Choice Plus HSA Silver 2000", "AK9P");
		codeMap.put("UHC Choice Plus HSA Silver 2500", "AK9N");
		codeMap.put("UHC Choice Plus Silver 2000", "AK9U");
		codeMap.put("UHC Choice Plus Silver 2250-2", "AK9L");
		codeMap.put("UHC Choice Plus Silver 3000", "AK9X");
		codeMap.put("UHC Choice Silver 2000", "AK9V");
		codeMap.put("UHC Choice Silver 2250-1", "AK9K");
		codeMap.put("UHC Choice Silver 3000", "AK9W");
		codeMap.put("UHC Non-Differential PPO Gold 1500", "");
		codeMap.put("UHC Non-Differential Silver $2,850", "");
	}

	public ArrayList<Page> parse(File file, String filename) {
		products = new ArrayList<Page>();
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
		int carrier_id = 11;
		int col_index = 2;
		int row_index = 1;
		Row r = sheet.getRow(7);
		int numRows = sheet.getPhysicalNumberOfRows();
		int numCols = r.getPhysicalNumberOfCells();
		String state = "PA";

		row_index = 2;
		col_index = 2;
		r = sheet.getRow(row_index);
		cell = r.getCell(col_index);

		row_index = 6;
		while (col_index < numCols) {
			HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String plan_id = cell.getStringCellValue();
			System.out.println(plan_id);
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String form_num = cell.getStringCellValue();
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String rating_area = Formatter.removeString(cell.getStringCellValue(), "Rating Area ");
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String counties = cell.getStringCellValue();
			row_index++;
			// String network =
			// String.format("HIGHMARK-",cell.getStringCellValue());
			String network = "HIGHMARK-Z";
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String metal = cell.getStringCellValue();
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String product = cell.getStringCellValue();
			product += " " + codeMap.get(product);
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String deductible = "";
			switch (cell.getCellTypeEnum()) {
			case STRING:
				deductible = cell.getStringCellValue();
				break;
			case NUMERIC:
				deductible = Double.toString(cell.getNumericCellValue());
				break;
			}
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String coinsurance = "";
			switch (cell.getCellTypeEnum()) {
			case STRING:
				coinsurance = cell.getStringCellValue();
				break;
			case NUMERIC:
				coinsurance = Double.toString(cell.getNumericCellValue());
				break;
			}
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String copay = "";
			switch (cell.getCellTypeEnum()) {
			case STRING:
				copay = cell.getStringCellValue();
				break;
			case NUMERIC:
				copay = Double.toString(cell.getNumericCellValue());
				break;
			}
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			String oop_maximum = "";
			switch (cell.getCellTypeEnum()) {
			case STRING:
				oop_maximum = cell.getStringCellValue();
				break;
			case NUMERIC:
				oop_maximum = Double.toString(cell.getNumericCellValue());
				break;
			}
			row_index += 2;
			r = sheet.getRow(row_index++);
			cell = r.getCell(col_index);
			System.out.println(row_index);
			System.out.println(col_index);
			non_tobacco_dict.put("0-20", Double.valueOf(getCellValue(cell)));
			cell = r.getCell(col_index + 1);
			tobacco_dict.put("0-20", Double.valueOf(getCellValue(cell)));
			for (int i = 21; i < 65; i++) {
				r = sheet.getRow(row_index++);
				cell = r.getCell(col_index);
				non_tobacco_dict.put(String.valueOf(i), Double.valueOf(getCellValue(cell)));
				cell = r.getCell(col_index + 1);
				tobacco_dict.put(String.valueOf(i), Double.valueOf(getCellValue(cell)));
			}
			non_tobacco_dict.put("65+", Double.valueOf(getCellValue(cell)));
			cell = r.getCell(col_index + 1);
			tobacco_dict.put("65+", Double.valueOf(getCellValue(cell)));
			MedicalPage page = new MedicalPage(carrier_id, plan_id, start_date, end_date, product, "", deductible, "",
					"", "", coinsurance, "", "", "", "", "", "", oop_maximum, "", "", "", "", "", "", "", "", "",
					rating_area, "", state, page_index, non_tobacco_dict, tobacco_dict);
			col_index += 2;
			row_index = 6;
			page_index++;

			// Ignore row if plan name includes "catalyst"
			String search_term1 = "catalyst";
			String search_term2 = "navigate";
			if (product.toLowerCase().indexOf(search_term1.toLowerCase()) != -1) {
				continue;
			} else if (product.toLowerCase().indexOf(search_term2.toLowerCase()) != -1) {
				continue;
			} else {
				products.add(page);
			}
		}
		// for(NEPA_Page p : products){
		// p.printPage();
		// }
		return products;
	}

}
