package components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Main.Carrier;
import components.Main.State;
import nj.NJ_Aetna_Benefits;
import nj.NJ_Aetna_Q2_Rates;
import nj.NJ_All_Carriers_Rates;
import nj.NJ_Amerihealth_Rates;
import nj.NJ_Oxford_Benefits;
import pa.PA_Aetna_Benefits;
import pa.PA_Aetna_Rates;
import pa.PA_CBC_Benefits;
import pa.PA_CBC_Rates;
import pa.PA_CPA_Rates;
import pa.PA_IBC_Rates;
import pa.PA_NEPA_Rates;
import pa.PA_WPA_Rates;

public interface Parser {

	public enum WPA {
		HMK, HCA
	}

	public static String removeFileExtension(String input) {
		return input.substring(0, input.lastIndexOf("."));
	}

	public ArrayList<PageInterface> getValue() throws Exception;
	
	public static void compareAetnaWorkbooks(String path1, String path2) throws IOException {
		System.out.println(path1 + " " + path2);
		final String output = path1;
		final String otherWorkbook = path2;
		FileInputStream fis = new FileInputStream(output);
		FileInputStream fis2 = new FileInputStream(otherWorkbook);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFWorkbook workbook2 = new XSSFWorkbook(fis2);
	
		
		XSSFColor xred = new XSSFColor(new java.awt.Color(240, 128, 128));
		XSSFColor xgreen = new XSSFColor(new java.awt.Color(0, 255, 127));
		XSSFColor xblue = new XSSFColor(new java.awt.Color(135, 206, 250));

		
		XSSFCellStyle style1 = workbook2.createCellStyle();
		style1.setFillForegroundColor(xred);
	    style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(xgreen);
	    style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle style2a = workbook2.createCellStyle();
		style2a.cloneStyleFrom(style2);
	    style2a.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		XSSFCellStyle style3 = workbook.createCellStyle();
		style3.setFillForegroundColor(xblue);
	    style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    XSSFCellStyle noHighlight = workbook.createCellStyle();
	    noHighlight.setFillPattern(FillPatternType.NO_FILL);
	    XSSFCellStyle noHighlight2 = workbook2.createCellStyle();
	    noHighlight2.cloneStyleFrom(noHighlight);

				
		XSSFSheet sheet = workbook.getSheetAt(0);
		XSSFSheet srcSheet = workbook2.getSheetAt(0);
		int numRows = srcSheet.getLastRowNum();
		int index = 1;
		System.out.println("Numrows: " + srcSheet.getLastRowNum());
		for (int i = 1; i <= numRows; i++) {
			System.out.println("First index: " + index + " Second index: " + i);
			XSSFRow row = sheet.getRow(index);
			XSSFRow srcRow = srcSheet.getRow(i);
			XSSFCell cell = row.getCell(0);
			XSSFCell cell2 = srcRow.getCell(0);
			String productName = cell.getStringCellValue().toLowerCase().replaceAll("\\s", "");
			String productName2 = cell2.getStringCellValue().toLowerCase().replaceAll("\\s", "");
			if (productName2.contains("(6)") || productName2.contains("(7)")) {
				productName2 = productName2.substring(0, productName2.length() - 3);
			}
			String ratingarea1;
			if (row.getCell(1).getCellTypeEnum() == CellType.STRING) {
				ratingarea1 = row.getCell(1).getStringCellValue().substring(5);

			} else {
				System.out.println(row.getCell(1).getNumericCellValue());
				ratingarea1 = Integer.toString((int) row.getCell(1).getNumericCellValue()).substring(5);
			}
			System.out.println(ratingarea1);
			productName = productName + ratingarea1;
			String ratingarea2 = Integer.toString((int) srcRow.getCell(1).getNumericCellValue()).substring(2);
			productName2 = productName2 + ratingarea2;
			System.out.println("Name 1: " + productName + " , Name 2: " + productName2);
			
			int result = productName.compareTo(productName2);
			System.out.println("Compare to result: " + result);
			if (result == 0) {
				//49
				cell.setCellStyle(noHighlight);
				cell2.setCellStyle(noHighlight2);
				System.out.println("Reaches here");
				for (int j = 2; j < 49; j++) {
					XSSFCell c1 = row.getCell(j);
					XSSFCell c2 = srcRow.getCell(j);
					System.out.println("Index: " + j);
					if (c1.getNumericCellValue() != c2.getNumericCellValue()) {
						c1.setCellStyle(style2);
						c2.setCellStyle(style2a);
					} else {
						c1.setCellStyle(noHighlight);
						c2.setCellStyle(noHighlight2);
					}
				}
				index++;
			} else if (result > 0 ){
				System.out.println("Reaches here instead");
				cell2.setCellStyle(style1);
			} else {
				System.out.println("Reaches here thirdly");
				i--;
				index++;
				cell.setCellStyle(style3);
				System.out.println(cell.getCellStyle().getFillForegroundXSSFColor().getARGB()); //.getFillBackgroundXSSFColor().getRGB()[2]
			}
		}
		if (index < sheet.getLastRowNum()) {
			for (int i = index; i < sheet.getLastRowNum(); i++) {
				//Highlight the remaining cells
				XSSFRow c1row = sheet.getRow(i);
				c1row.getCell(0).setCellStyle(style3);
			}
		}
        FileOutputStream outputStream;
        FileOutputStream outputStream2;
		try {
			outputStream = new FileOutputStream(output);
			outputStream2 = new FileOutputStream(otherWorkbook);
            workbook.write(outputStream);
            workbook2.write(outputStream2);
            outputStream.flush();
            outputStream2.flush();
            outputStream.close();
            outputStream2.close();
            fis.close();
            fis2.close();
            workbook.close();
            workbook2.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}

}
