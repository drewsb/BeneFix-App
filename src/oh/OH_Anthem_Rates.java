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
public class OH_Anthem_Rates implements Parser{
		
	Sheet sheet;
	
	static ArrayList<MedicalPage> products;
		
	static Iterator<Row> iterator;	
	
	static String start_date;
	
	static String end_date;

	private Workbook workbook;
	
	public OH_Anthem_Rates(String s_date, String e_date) throws IOException{
		start_date = s_date;
		end_date = e_date;
		products = new ArrayList<MedicalPage>();
    }
	
	@SuppressWarnings({ "unused", "incomplete-switch" })
	public ArrayList<Page> parse(File file, String filename){	
		System.out.println("1");
		ArrayList<Page> products = new ArrayList<Page>();
		try {
			System.out.println("2");
            FileInputStream excelFile = new FileInputStream(file);
    		System.out.println("3");
            workbook = new XSSFWorkbook(excelFile);
    		System.out.println("4");
            this.sheet = workbook.getSheetAt(0);
            iterator = sheet.iterator();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		System.out.println("5");
		Cell cell;
		String temp;
		int page_index = 1;
		int carrier_id = 9;
		int col_index = 1;
		int row_index = 3;
		Row r = sheet.getRow(row_index);
        int numRows = getNumRows(sheet);
		int numCols = r.getPhysicalNumberOfCells();
		
		System.out.println(numRows);
		System.out.println(numCols);
		
		String state = "PA";
		
        while(row_index < numRows){
        	
        }
        return products;
	}

	
}
