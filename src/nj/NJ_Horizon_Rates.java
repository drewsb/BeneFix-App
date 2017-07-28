package nj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Formatter;
import components.MedicalPage;
import components.Page;
import components.Parser;

public class NJ_Horizon_Rates implements Parser {

	public ArrayList<Page> parse(File file, String fileName) throws IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		DataFormatter df = new DataFormatter();
		
		for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
			XSSFSheet sheet = workbook.getSheetAt(index);
				
			XSSFRow nameRow = sheet.getRow(2);
			XSSFCell nameCell = nameRow.getCell(1);
			String name = nameCell.getStringCellValue();
			
//			if (name.contains("off")) {
//				StringBuilder sb = new StringBuilder();
//				String[] tokens = name.split("\\s");
//				int x = 0;
//				while (!tokens[x].equals("off")) {
//					sb.append(tokens[x] + " ");
//					x++;
//				}
//				name = sb.toString();
//			} else if (name.contains("Compatible")) {
//				StringBuilder sb = new StringBuilder();
//				String[] tokens = name.split("\\s");
//				int x = 0;
//				while (!tokens[x].equals("Compatible")) {
//					sb.append(tokens[x] + " ");
//					x++;
//				}
//				name = sb.toString();
// 			}
			
			if (name.contains("off") || name.contains("Compatible")) {
				StringBuilder sb = new StringBuilder();
				String[] tokens = name.split("\\s");
				int x = 0;
				while (!tokens[x].equals("-")) {
					sb.append(tokens[x] + " ");
					x++;
				}
				name = sb.toString();
			}
			
			System.out.println(name);
			
			MedicalPage page1 = new MedicalPage();
			MedicalPage page2 = new MedicalPage();
			MedicalPage page3 = new MedicalPage();
			MedicalPage page4 = new MedicalPage();
			MedicalPage page5 = new MedicalPage();
			MedicalPage page6 = new MedicalPage();
			
			HashMap<String, Double> map1 = new HashMap<String, Double>();
			HashMap<String, Double> map2 = new HashMap<String, Double>();
			HashMap<String, Double> map3 = new HashMap<String, Double>();
			HashMap<String, Double> map4 = new HashMap<String, Double>();
			HashMap<String, Double> map5 = new HashMap<String, Double>();
			HashMap<String, Double> map6 = new HashMap<String, Double>();

			
			for (int i = 8; i < 54; i++) {
				int cellIndex = 1;
				
				XSSFRow row = sheet.getRow(i);
				XSSFCell ageCell = row.getCell(cellIndex++);
				String age;
				if (ageCell.getCellTypeEnum() == CellType.NUMERIC) {
					age = Double.toString(ageCell.getNumericCellValue()).substring(0, 2);
				} else {
					age = ageCell.getStringCellValue();
				}
				if (age.contains("65")) {
					age = "65+";
				}
				
				Double val1 = Formatter.formatValue(df.formatCellValue(row.getCell(cellIndex++)));
				Double val2 = Formatter.formatValue(df.formatCellValue(row.getCell(cellIndex++)));
				Double val3 = Formatter.formatValue(df.formatCellValue(row.getCell(cellIndex++)));
				Double val4 = Formatter.formatValue(df.formatCellValue(row.getCell(cellIndex++)));
				Double val5 = Formatter.formatValue(df.formatCellValue(row.getCell(cellIndex++)));
				Double val6 = Formatter.formatValue(df.formatCellValue(row.getCell(cellIndex++)));
				
				map1.put(age, val1);
				map2.put(age, val2);
				map3.put(age, val3);
				map4.put(age, val4);
				map5.put(age, val5);
				map6.put(age, val6);

			}
			
			page1.non_tobacco_dict = map1;
			page2.non_tobacco_dict = map2;
			page3.non_tobacco_dict = map3;
			page4.non_tobacco_dict = map4;
			page5.non_tobacco_dict = map5;
			page6.non_tobacco_dict = map6;
			
			page1.group_rating_area = "1";
			page2.group_rating_area = "2";
			page3.group_rating_area = "3";
			page4.group_rating_area = "4";
			page5.group_rating_area = "5";
			page6.group_rating_area = "6";
			
			page1.product_name = name;
			page2.product_name = name;
			page3.product_name = name;
			page4.product_name = name;
			page5.product_name = name;
			page6.product_name = name;
			
			result.add(page1);
			result.add(page2);
			result.add(page3);
			result.add(page4);
			result.add(page5);
			result.add(page6);
		}
		
		return result;
	}
}
