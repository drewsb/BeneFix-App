package components;
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

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class WPA_Parser {
	
	static ArrayList<Page> products;
	
	static Sheet sheet;
	
	static Iterator<Row> iterator;	
	
	static String start_date;
	
	static String end_date;
	
	public WPA_Parser(File file, int sheet_index, String s_date, String e_date) throws IOException{
		start_date = s_date;
		end_date = e_date;
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

    }
	
	public ArrayList<Page> parseHMK(){
		Cell cell;
		int page_index = 1;
		int carrier_id = 15;
		int col_index = 2;
		int row_index = 1;
		Row r = sheet.getRow(5);
        int numRows = sheet.getPhysicalNumberOfRows();
		int numCols = r.getPhysicalNumberOfCells();
		String state = "PA";
		
        while(col_index < numCols){
			HashMap<String,Double> non_tobacco_dict = new HashMap<String,Double>();		
			HashMap<String,Double> tobacco_dict = new HashMap<String,Double>();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String product = cell.getStringCellValue();
			System.out.println(product);
			row_index++;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_id = cell.getStringCellValue();
			row_index++;
			System.out.println(plan_id);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String form_num = cell.getStringCellValue();
			System.out.println(form_num);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String rating_area = cell.getStringCellValue();
			rating_area = rating_area.substring(5, rating_area.length());
			System.out.println(rating_area);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String network = String.format("HIGHMARK-%s",cell.getStringCellValue());
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String metal = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_name = cell.getStringCellValue();
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
			System.out.println(oop_maximum);
			row_index+=2;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			non_tobacco_dict.put("0-20", cell.getNumericCellValue());
			cell = r.getCell(col_index+1);
			tobacco_dict.put("0-20", cell.getNumericCellValue());
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			for(int i = 21; i < 65; i++){
				non_tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
				cell = r.getCell(col_index+1);
				tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
				r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			}
			non_tobacco_dict.put("65+", cell.getNumericCellValue());
			cell = r.getCell(col_index+1);
			tobacco_dict.put("65+", cell.getNumericCellValue());
			Page page = new Page(carrier_id, plan_id, start_date, end_date, product, "", 
					deductible, "", "", "", coinsurance, "", "", "", "", "", "", oop_maximum, "", "",
					"", "", "", "", "", "", "", rating_area, "", plan_name, state, page_index, non_tobacco_dict, tobacco_dict);
	        products.add(page);
        	col_index+=2;
    		row_index = 1;
        	page_index++;
        }
//        for(WPA_Page p : products){
//        	p.printPage();
//        }
        return products;
	}
	
	public ArrayList<Page> parseHCA(){
		Cell cell;
		int page_index = 1;
		int carrier_id = 15;
		int col_index = 2;
		int row_index = 1;
		Row r = sheet.getRow(5);
        int numRows = sheet.getPhysicalNumberOfRows();
		int numCols = r.getPhysicalNumberOfCells();
		String state = "PA";
		
        while(col_index < numCols){
			HashMap<String,Double> non_tobacco_dict = new HashMap<String,Double>();		
			HashMap<String,Double> tobacco_dict = new HashMap<String,Double>();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String product = cell.getStringCellValue();
			System.out.println(product);
			row_index+=2;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_id = cell.getStringCellValue();
			row_index++;
			System.out.println(plan_id);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String form_num = cell.getStringCellValue();
			System.out.println(form_num);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String rating_area = cell.getStringCellValue();
			rating_area = rating_area.substring(5, rating_area.length());
			System.out.println(rating_area);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String network = String.format("HIGHMARK-%s",cell.getStringCellValue());
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String metal = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_name = cell.getStringCellValue();
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
			System.out.println(oop_maximum);
			row_index+=2;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			non_tobacco_dict.put("0-20", cell.getNumericCellValue());
			cell = r.getCell(col_index+1);
			tobacco_dict.put("0-20", cell.getNumericCellValue());
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			for(int i = 21; i < 65; i++){
				non_tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
				cell = r.getCell(col_index+1);
				tobacco_dict.put(String.valueOf(i), cell.getNumericCellValue());
				r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			}
			non_tobacco_dict.put("65+", cell.getNumericCellValue());
			cell = r.getCell(col_index+1);
			tobacco_dict.put("65+", cell.getNumericCellValue());
			Page page = new Page(carrier_id, plan_id, start_date, end_date, product, "", 
					deductible, "", "", "", coinsurance, "", "", "", "", "", "", oop_maximum, "", "",
					"", "", "", "", "", "", "", rating_area, "", plan_name, state, page_index, non_tobacco_dict, tobacco_dict);
	        products.add(page);
        	col_index+=2;
    		row_index = 1;
        	page_index++;
        }
//        for(WPA_Page p : products){
//        	p.printPage();
//        }
        return products;
	}

	
}
