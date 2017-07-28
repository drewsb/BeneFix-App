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
public class PA_CBC_Rates implements Parser{
		
	Sheet sheet;
	
	static ArrayList<MedicalPage> products;
		
	static Iterator<Row> iterator;	
	
	static String start_date;
	
	static String end_date;

	private Workbook workbook;
	
	public PA_CBC_Rates(String s_date, String e_date) throws IOException{
		start_date = s_date;
		end_date = e_date;
		products = new ArrayList<MedicalPage>();
    }
	
	@SuppressWarnings({ "unused", "incomplete-switch" })
	public ArrayList<Page> parse(File file, String filename){	
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
		
		Cell cell;
		String temp;
		int page_index = 1;
		int carrier_id = 9;
		int col_index = 1;
		int row_index = 3;
		Row r = sheet.getRow(row_index);
        int numRows = getNumRows(sheet);
		int numCols = r.getPhysicalNumberOfCells();
		
		String state = "PA";
		
        while(row_index < numRows){
			r = sheet.getRow(row_index); 
			HashMap<String,Double> non_tobacco_dict = new HashMap<String,Double>();		
			HashMap<String,Double> tobacco_dict = new HashMap<String,Double>();
			
			cell = r.getCell(col_index++);
			String plan_id = getCellValue(cell);
			
			cell = r.getCell(col_index);
			String product = getCellValue(cell);
			
			cell = r.getCell(col_index+=6);
			String rating_area = getCellValue(cell);
			
			cell = r.getCell(++col_index);
			temp = getCellValue(cell);
			non_tobacco_dict.put("0-18", Formatter.formatValue(temp));	
			
			cell = r.getCell(++col_index);
			temp = getCellValue(cell);
			non_tobacco_dict.put("19-20", Formatter.formatValue(temp));	
			
			cell = r.getCell(col_index+=2);
			for(int i = 21; i < 65; i++){
				temp = getCellValue(cell);
				non_tobacco_dict.put(Integer.toString(i),Formatter.formatValue(temp));
				cell = r.getCell(++col_index);
			}
			non_tobacco_dict.put("65+", Formatter.formatValue(temp));
			
			for(Map.Entry<String, Double> entry : non_tobacco_dict.entrySet()){
				System.out.println(entry.getKey());
				System.out.println(entry.getValue());
			}
			MedicalPage page = new MedicalPage(carrier_id, plan_id, start_date, end_date, product, "", 
					"", "", "", "", "", "", "", "", "", "", "", "", "", "",
					"", "", "", "", "", "", "", rating_area, "", state, row_index-3, non_tobacco_dict, tobacco_dict);
			products.add(page);
			
			page.printPage();
        	col_index = 1;
    		row_index++;
        }
        return products;
	}

	
}
