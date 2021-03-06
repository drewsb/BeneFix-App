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
public class PA_NEPA_Rates implements Parser {

	static ArrayList<Page> products;

	static Sheet sheet;

	static Iterator<Row> iterator;

	static String start_date;

	static String end_date;
	
	static int sheet_index;

	public PA_NEPA_Rates(int s_index, String s_date, String e_date) throws IOException{
		sheet_index = s_index;
		start_date = s_date;
		end_date = e_date;
    }

	public ArrayList<Page> parse(File file, String filename){
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
		int carrier_id = 14;
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
    	//String product = cell.getStringCellValue();
    	row_index = 6;
    	while(col_index < numCols){
			HashMap<String,Double> non_tobacco_dict = new HashMap<String,Double>();
			HashMap<String,Double> tobacco_dict = new HashMap<String,Double>();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_id = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String form_num = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String rating_area = Formatter.removeString(cell.getStringCellValue(), "Rating Area ");
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String counties = cell.getStringCellValue();
			row_index++;
			//String network = String.format("HIGHMARK-",cell.getStringCellValue());
			String network = "HIGHMARK-Z";
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String metal = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String product = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String deductible = "";
			switch(cell.getCellTypeEnum()){
			case STRING:
				deductible = cell.getStringCellValue();
				break;
			case NUMERIC:
				deductible = Double.toString(cell.getNumericCellValue());
				break;
			}
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String coinsurance = "";
			switch(cell.getCellTypeEnum()){
			case STRING:
				coinsurance = cell.getStringCellValue();
				break;
			case NUMERIC:
				coinsurance = Double.toString(cell.getNumericCellValue());
				break;
			}
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String copays = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String oop_maximum = "";
			switch(cell.getCellTypeEnum()){
			case STRING:
				oop_maximum = cell.getStringCellValue();
				break;
			case NUMERIC:
				oop_maximum = Double.toString(cell.getNumericCellValue());
				break;
			}
			row_index+=2;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			non_tobacco_dict.put("0-20", cell.getNumericCellValue());
			cell = r.getCell(col_index+1);
			tobacco_dict.put("0-20", cell.getNumericCellValue());
			for(int i = 21; i < 65; i++){
				r = sheet.getRow(row_index++); cell = r.getCell(col_index);
				non_tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
				cell = r.getCell(col_index+1);
				tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
			}
			non_tobacco_dict.put("65+", cell.getNumericCellValue());
			cell = r.getCell(col_index+1);
			tobacco_dict.put("65+", cell.getNumericCellValue());
			MedicalPage page = new MedicalPage(carrier_id, plan_id, start_date, end_date, product, filename,
					deductible, "", "", "", coinsurance, "", "", "", "", "", "", oop_maximum, "", "",
					"", "", "", "", "", "", "", rating_area, "", state, page_index,
					non_tobacco_dict, tobacco_dict);
			products.add(page);
        	col_index+=2;
    		row_index = 6;
        	page_index++;
        }
//        for(NEPA_Page p : products){
//        	p.printPage();
//        }
        return products;
	}


}
