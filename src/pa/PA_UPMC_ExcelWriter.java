package pa;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/*
 * Uses Apache Poi package found at https://www.apache.org. 
 */
public class PA_UPMC_ExcelWriter {
	
	public static void main(String[] args){

	}

	/*
	 * Input: Array of page objects. 
	 * Creates a new workbook sheet every compilation. First populates the excel sheet with template data,
	 * then the necessary data from the array of pages. Output file is called "BenefixData.xlsx". 
	 */
	public static void populateExcel(PA_UPMC_Page[] pages, String filename) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("BenefixData");
         
        //Template data given by Benefix. 
        String[] templateData = {"start_date", "end_date",	"plan_name", "pharmacy_rider",
        		"states",	"group_rating_areas",	"zero_eighteen",	"nineteen_twenty",	"twenty_one",
        		"twenty_two",	"twenty_three",	"twenty_four",	"twenty_five",	"twenty_six",	"twenty_seven",
        		"twenty_eight","twenty_nine",	"thirty",	"thirty_one",	"thirty_two",	"thirty_three",	
        		"thirty_four",	"thirty_five",	"thirty_six",	"thirty_seven",	"thirty_eight",	"thirty_nine",
        		"forty","forty_one", "forty_two",	"forty_three",	"forty_four",	"forty_five",	"forty_six",	
        		"forty_seven",	"forty_eight",	"forty_nine",	"fifty",	"fifty_one",	"fifty_two",
        		"fifty_three",	"fifty_four",	"fifty_five",	"fifty_six",	"fifty_seven",	
        		"fifty_eight",	"fifty_nine","sixty",	"sixty_one",	"sixty_two",	"sixty_three",
        		"sixty_four",	"sixty_five_plus" };
 
        int rowCount = 0;
        int colCount = 0;
        
        //Populate with template data
        Row row = sheet.createRow(rowCount);     
        for (String header : templateData) {
            Cell cell = row.createCell(colCount++);
            cell.setCellValue((String) header);
        }
        
        //Populate with PDF data
        for(PA_UPMC_Page p : pages){
        	if(p==null){
        		continue;
        	}
        	System.out.printf("PAGE: %d\n", p.page);
            colCount = 0;
            row = sheet.createRow(++rowCount);     
            Cell cell = row.createCell(colCount++);
            cell.setCellValue((String) p.start_date);
            cell = row.createCell(colCount++);
            cell.setCellValue((String) p.end_date);
            cell = row.createCell(colCount++);
            cell.setCellValue((String) p.plan_name);
            cell = row.createCell(colCount++);
            cell.setCellValue((String) p.pharmacy_rider);
            cell = row.createCell(colCount++);
            cell.setCellValue((String) p.state);
            cell = row.createCell(colCount++);
            cell.setCellValue((String) p.group_rating_area);
            cell = row.createCell(colCount++);
            cell.setCellValue(p.non_tobacco_dict.get("0-20"));
            cell = row.createCell(colCount++);
            cell.setCellValue(p.non_tobacco_dict.get("0-20"));
            for(int i = 0; i < 44; i++){
            	cell = row.createCell(colCount++);
            	String index = String.format("%d", i+21);
                cell.setCellValue(p.non_tobacco_dict.get(index));
            }
            cell = row.createCell(colCount++);
            cell.setCellValue(p.non_tobacco_dict.get("65+"));
        }
         
        String outputName = String.format("%s_data.xlsx", filename);
        //Create output file
        try (FileOutputStream outputStream = new FileOutputStream(outputName)) {
            workbook.write(outputStream);
        }
        
	}
	
}
