package components;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

public interface Parser {
	
	public ArrayList<Page> parse(File file, String filename) throws EncryptedDocumentException, InvalidFormatException, IOException;
	
	
	/*
	 * Default method to retrieve the value of a cell from an Excel workbook.
	 * Note: This method assumes that the parser is parsing an Excel file, NOT a PDF
	 */
	default String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			return Double.toString(cell.getNumericCellValue());
		case STRING:
			return cell.getStringCellValue();
		default:
			return null;
		}
	}
	
	/*
	 * Default method to retrieve the number of rows which contain non-empty cells
	 * Note: This method assumes that the parser is parsing an Excel file, NOT a PDF
	 */
	default int getNumRows(Sheet sheet) {
		int row_index = 0;
		int col_index = 0;
		Row r; 
		Cell cell;
		r = sheet.getRow(row_index++);  
		while(r == null & row_index < 1000){
			r = sheet.getRow(row_index++);  
		}
		if(row_index == 1000){
			return -1; //Return -1, invalid excel file
		}
		cell = r.getCell(col_index);
		while(getCellValue(cell).isEmpty()){
			r = sheet.getRow(row_index++);  cell = r.getCell(col_index);
		}
		System.out.println(row_index);
		while(getCellValue(cell) != null){
			System.out.println(row_index);
			r = sheet.getRow(row_index++);  
			if(r == null){
				return row_index;
			}
			cell = r.getCell(col_index);
			if(cell == null){
				return row_index;
			}
		}
		return row_index-1;
	}

}
	