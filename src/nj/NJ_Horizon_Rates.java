package nj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.MedicalPage;
import components.Page;
import components.Parser;

public class NJ_Horizon_Rates implements Parser {

	public ArrayList<Page> parse(File file, String fileName) throws IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		DataFormatter df = new DataFormatter();
		
		for (int index = 0; index < 4; index++) {
			XSSFSheet sheet = workbook.getSheetAt(index);
			XSSFRow nameRow = sheet.getRow(2);
			XSSFCell nameCell = nameRow.getCell(6);
			String name = nameCell.getStringCellValue();
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
				
				String val1 = df.formatCellValue(row.getCell(cellIndex++));
				String val2 = df.formatCellValue(row.getCell(cellIndex++));
				String val3 = df.formatCellValue(row.getCell(cellIndex++));
				String val4 = df.formatCellValue(row.getCell(cellIndex++));
				String val5 = df.formatCellValue(row.getCell(cellIndex++));
				String val6 = df.formatCellValue(row.getCell(cellIndex++));
				
				map1.put(age, val1);
				map2.put(age, val2);
				map3.put(age, val3);
				map4.put(age, val4);
				map5.put(age, val5);
				map6.put(age, val6);

				
//				map1.put(age, Double.parseDouble(df.formatCellValue(row.getCell(cellIndex++)).substring(1)));
//				map2.put(age, Double.parseDouble(df.formatCellValue(row.getCell(cellIndex++)).substring(1)));
//				map3.put(age, Double.parseDouble(df.formatCellValue(row.getCell(cellIndex++)).substring(1)));
//				map4.put(age, Double.parseDouble(df.formatCellValue(row.getCell(cellIndex++)).substring(1)));
//				map5.put(age, Double.parseDouble(df.formatCellValue(row.getCell(cellIndex++)).substring(1)));
//				map6.put(age, Double.parseDouble(df.formatCellValue(row.getCell(cellIndex++)).substring(1)));
				
				System.out.println("Age: " + age);
				System.out.println("Map 1: " + map1.get(age));
				System.out.println("Map 2: " + map2.get(age));
				System.out.println("Map 3: " + map3.get(age));
				System.out.println("Map 4: " + map4.get(age));
				System.out.println("Map 5: " + map5.get(age));
				System.out.println("Map 6: " + map6.get(age));
				System.out.println("---------------------------");
			}
			
			page1.non_tobacco_dict = map1;
			page2.non_tobacco_dict = map2;
			page3.non_tobacco_dict = map3;
			page4.non_tobacco_dict = map4;
			page5.non_tobacco_dict = map5;
			page6.non_tobacco_dict = map6;
		}
		
		
		return result;
	}
}
