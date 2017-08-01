package pa;
import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
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
public class PA_CPA_Rates implements Parser{
	
	static ArrayList<Page> products;
	
	final int sheet_index;
	
	public Sheet sheet;
	
	static Iterator<Row> iterator;	
	
	final String start_date;
	
	final String end_date;
	
	public PA_CPA_Rates(int sheet_index, String s_date, String e_date) throws IOException{
		this.sheet_index = sheet_index;
		this.start_date = s_date;
		this.end_date = e_date;
		products = new ArrayList<Page>();
	
    }
	
	public ArrayList<Page> parse(File file, String filename){
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
		int carrier_id = 9;
		int col_index = 2;
		int row_index = 1;
		Row r = sheet.getRow(row_index);
        int numRows = sheet.getPhysicalNumberOfRows();
		int numCols = r.getPhysicalNumberOfCells();
		
		String state = "PA";
		
		System.out.println(sheet_index);
        while(col_index < numCols){
			HashMap<String,Double> non_tobacco_dict = new HashMap<String,Double>();		
			HashMap<String,Double> tobacco_dict = new HashMap<String,Double>();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String product = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String dates = cell.getStringCellValue();
			row_index+=2;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_id = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String form_num = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String rating_area = Formatter.removeString(cell.getStringCellValue(), "Area ");
			rating_area = rating_area.replace(", ", "/");
			//rating_area = rating_area.substring(0, rating_area.length());
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String network = String.format("HIGHMARK-%s",cell.getStringCellValue());
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String metal = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String plan_name = cell.getStringCellValue();
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String deductible = getCellValue(cell);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String coinsurance = getCellValue(cell);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String copays = getCellValue(cell);
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			String oop_maximum = getCellValue(cell);
			row_index+=2;
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			non_tobacco_dict.put("0-20", Formatter.formatValue(getCellValue(cell)));
			cell = r.getCell(col_index+1);
			tobacco_dict.put("0-20", Formatter.formatValue(getCellValue(cell)));
			r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			for(int i = 21; i < 65; i++){
				non_tobacco_dict.put(String.valueOf(i), Formatter.formatValue(getCellValue(cell)));
				cell = r.getCell(col_index+1);
				tobacco_dict.put(String.valueOf(i), Formatter.formatValue(getCellValue(cell)));
				r = sheet.getRow(row_index++); cell = r.getCell(col_index);
			}
			non_tobacco_dict.put("65+", Formatter.formatValue(getCellValue(cell)));
			cell = r.getCell(col_index+1);
			tobacco_dict.put("65+", Formatter.formatValue(getCellValue(cell)));
			System.out.println(product);
			System.out.println(dates);
			System.out.println(plan_id);
			System.out.println(form_num);
			System.out.println(rating_area);
			System.out.println(network);
			System.out.println(metal);
			System.out.println(plan_name);
			System.out.println(deductible);
			System.out.println(coinsurance);
			System.out.println(copays);
			System.out.println(oop_maximum);
			
			for(Map.Entry<String, Double> entry : non_tobacco_dict.entrySet()){
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
			}
			MedicalPage page = new MedicalPage(carrier_id, plan_id, start_date, end_date, plan_name, "", 
					deductible, "", "", "", coinsurance, "", "", "", "", "", "", oop_maximum, "", "",
					"", "", "", "", "", "", "", rating_area, "", state, page_index, non_tobacco_dict, tobacco_dict);
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
