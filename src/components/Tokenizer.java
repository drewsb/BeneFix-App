package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Tokenizer {
	
	final ArrayList<File> files;
	
	final XSSFWorkbook workbook;

	final XSSFSheet sheet;
		
	public Tokenizer(ArrayList<File> files){
		this.workbook = new XSSFWorkbook();
		this.sheet = workbook.createSheet("Tokens");
		this.files = files;
		
	}
	
	public void tokenize() throws FileNotFoundException, IOException{
		int rowCount = 0;
		int colCount = 0;
		Row row;
		ArrayList<Row> rows = new ArrayList<Row>();
		for(int i = 0; i < 4000; i++){
			rows.add(sheet.createRow(i));
		}
		System.out.println(files.size());
		for(File f : files){
			PDFManager pdfManager = new PDFManager();
			pdfManager.setFilePath(f.getAbsolutePath());
			String text = pdfManager.ToText();
			String[] tokens = text.split("[\\s\\r\\n]+"); // Split pdf text by spaces
			for(String s : tokens){
				System.out.println(s);
				row = rows.get(rowCount++);
				Cell cell = row.createCell(colCount);
				cell.setCellValue(s);
			}
			rowCount = 0;
			colCount++;
		}
		// Create output file
		try (FileOutputStream outputStream = new FileOutputStream("Tokens.xlsx")) {
			workbook.write(outputStream);
		}
		workbook.close();
		
	}
}