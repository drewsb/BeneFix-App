package nj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Main.Carrier;
import components.PDFManager;

/*
 * NEEDS TO BE CHANGED
 * Must use excel writer class in main
 */
public class NJ_All_Carriers_Rates {
	
	PDFManager pdfmanager;
	File inputFile;
	File outputFile;
	String startDate;
	String endDate;
	String quarter;

	HashMap<String, String> results;
	Carrier carrier;
	
	final String otherWorkbook = "NJ Q2 Aetna.xlsx";

	public NJ_All_Carriers_Rates(File file, File outputFile, Carrier carrier, 
			String quarter, String startDate, String endDate) throws FileNotFoundException, IOException {
		this.pdfmanager = new PDFManager(file);
		this.inputFile = file;
		this.outputFile = outputFile;
		this.startDate = startDate;
		this.endDate = endDate;
		this.quarter = quarter;
		this.carrier = carrier;
		this.results = new HashMap<String, String>();
		parse();
		writeToOutputFile();
	}
	
	public void parse() throws IOException {
		String text = pdfmanager.ToText(1, pdfmanager.getNumPages());
		System.out.println(text);
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String currLine = lines[i];
			String[] tokens = currLine.split(" ");
			
			if (tokens.length == 0 || (!tokens[0].equals("NJ") && !tokens[0].equals("State")
					&& !tokens[0].equals("SEH") && !tokens[0].equals("HMO") && !tokens[0].equals("POS")
					&& !tokens[0].equals("Direct") && !tokens[0].equals("Advantage") && !tokens[0].equals("OMNIA")
					&& !tokens[0].equals("PPO") && !tokens[0].equals("EPO") && !tokens[0].equals("Prim")
					&& !tokens[0].equals("Primary"))) {
				continue;
			}

			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < tokens.length - 3; j++) {
				sb.append(tokens[j] + " ");
			}
			String planName = sb.toString();
			planName = planName.replaceAll("\\s+","").toLowerCase();
			planName = planName.replaceAll("\\.+", "");
			System.out.println("Name in map: " + planName);
			String q1str = tokens[tokens.length - 3];
			String q2str = tokens[tokens.length - 2];
			String q3str = tokens[tokens.length - 1];
			
			if (quarter.equals("Q1")) {
				results.put(planName, q1str);
			} else if (quarter.equals("Q2")) {
				results.put(planName, q2str);
			} else if (quarter.equals("Q3")) {
				results.put(planName, q3str);
			} else {
				//Put Q4 rates in here
			}
		}
	}
	
	public void writeToOutputFile() throws IOException {;
		FileInputStream fis = new FileInputStream(outputFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		
		XSSFCellStyle styleCurrencyFormat = workbook.createCellStyle();
		styleCurrencyFormat.setDataFormat((short) 8);
		
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

			srcString = srcString.replaceAll("\\s+","").toLowerCase();
			srcString = srcString.replaceAll("\\.+", "");
			if (carrier == Carrier.Oxford && srcString.substring(srcString.length() - 3, 
					srcString.length()).equals("(6)")) {
				srcString = srcString.substring(0, srcString.length() - 3);
			} else if (carrier == Carrier.Oxford && srcString.contains("primaryadvantage")) {
				srcString = srcString.replace("primaryadvantage", "primadv");
				srcString = srcString + "(primaryadvantage)";
			}
			
			System.out.println("Name in excel: " + srcString);
			String input = results.get(srcString);
			System.out.println(input);
			if (input == null) {
				input = "0000";
			}
			double dInput = Double.parseDouble(input.substring(1));
			XSSFCell des = row.getCell(12);
			des.setCellValue(dInput);
//			System.out.println("Row: " + i);
//			System.out.println("Col: " + des.getColumnIndex());
//			System.out.println("Input: " + input);
//			System.out.println("--------------------------");
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
