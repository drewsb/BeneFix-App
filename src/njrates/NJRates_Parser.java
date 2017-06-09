package njrates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.FileChooser.Carrier;
import components.PDFManager;

public class NJRates_Parser {
	
	PDFManager pdfmanager;
	File inputFile;
	File outputFile;
	String startDate;
	String endDate;
	String quarter;
	LinkedHashMap<String, String> results;
	Carrier carrier;

	public NJRates_Parser(File file, File outputFile, Carrier carrier, 
			String quarter, String startDate, String endDate) throws FileNotFoundException, IOException {
		this.pdfmanager = new PDFManager(file);
		this.inputFile = file;
		this.outputFile = outputFile;
		this.startDate = startDate;
		this.endDate = endDate;
		this.quarter = quarter;
		this.carrier = carrier;
		results = new LinkedHashMap<String, String>();
		parse();
		writeToOutputFile();
	}
	
	public void parse() throws IOException {
		String text = pdfmanager.ToText(1, 1);
		System.out.println(text);
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String currLine = lines[i];
			String[] tokens = currLine.split(" ");
			
			if (tokens.length == 0 || !tokens[0].equals("NJ")) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < tokens.length - 3; j++) {
				sb.append(tokens[j] + " ");
			}
			String planName = sb.toString();
			String q1 = tokens[tokens.length - 3];
			String q2 = tokens[tokens.length - 2];
			String q3 = tokens[tokens.length - 1];
			
			System.out.println(planName);

			if (quarter.equals("Q1")) {
				results.put(planName, q1);
			} else if (quarter.equals("Q2")) {
				results.put(planName, q2);
			} else if (quarter.equals("Q3")) {
				results.put(planName, q3);
			} else {
				//Put Q4 rates in here
			}
		}
	}
	
	public void writeToOutputFile() throws IOException {
		FileInputStream fis = new FileInputStream(outputFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		int numSheets = workbook.getNumberOfSheets();
		XSSFSheet sheet = workbook.getSheetAt(0);
		for (int i = 0; i < numSheets; i++) {
			XSSFSheet currSheet = workbook.getSheetAt(i);
			String name = currSheet.getSheetName();
			String sheetCarrier = name.split(" ")[0];
			if (sheetCarrier.equals(carrier.toString())) {
				sheet = currSheet;
				break;
			}
		}
		int numRows = sheet.getLastRowNum();
		for (int i = 1; i <= numRows; i++) {
			XSSFRow row = sheet.getRow(i);
			XSSFCell src = row.getCell(2);
			String srcString = src.getStringCellValue();
			String input = results.get(srcString);
			XSSFCell des = row.getCell(12);
			des.setCellValue(input);
			des.setCellType(CellType.NUMERIC);
			System.out.println("Row: " + i);
			System.out.println("Col: " + des.getColumnIndex());
			System.out.println("Input: " + input);
			System.out.println("--------------------------");
		}
		XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
        FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputFile);
            workbook.write(outputStream);
            outputStream.close();
            fis.close();
            workbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
}
