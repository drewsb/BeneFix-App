package components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.ss.usermodel.CellStyle;
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

public class Parser extends SwingWorker<ArrayList<Page>, String> {

	final Carrier carrierType;

	final int sheetIndex;

	final String quarter;

	final ArrayList<File> selectedPlans;

	final ArrayList<File> selectedRates;
	
	final ArrayList<File> selectedOutputs;

	final JTextArea textArea;

	final JProgressBar bar;
	
	final State state;

	public enum WPA {
		HMK, HCA
	}

	public Parser(final Carrier type, final int sheetIndex, State state, final String quarter,
			final ArrayList<File> selectedPlans, final ArrayList<File> selectedRates, 
			final ArrayList<File> selectedOutputs, final JTextArea textArea, final JProgressBar bar) {
	    this.carrierType = type;
	    this.sheetIndex = sheetIndex;
	    this.quarter = quarter;
	    this.selectedPlans = selectedPlans;
	    this.selectedRates = selectedRates;
	    this.textArea = textArea;
	    this.bar = bar;
	    this.state = state;
	    this.selectedOutputs = selectedOutputs;
	  }


	@Override
	protected ArrayList<Page> doInBackground() throws Exception {
		ArrayList<Page> pages = new ArrayList<Page>();
		HashMap<String, Page> pageMap = new HashMap<String, Page>();
		int size = selectedPlans.size() + selectedRates.size();
		int index = 0;
		String filename;
		if (!selectedPlans.isEmpty()) {
			System.out.println(carrierType.toString());
			for (File selectedPlan : selectedPlans) {
				filename = removeFileExtension(selectedPlan.getName());
				publish("Parsing: " + selectedPlan.getName() + ".\n");
				try {
					switch (carrierType) {
					case UPMC:
						break;
					case Aetna:
						Page aetna_page;
						PA_Aetna_Benefits aetna_plan_parser = new PA_Aetna_Benefits(selectedPlan);
						aetna_page = aetna_plan_parser.parse(filename);
						pages.add(aetna_page);
						pageMap.put(aetna_page.product_name, aetna_page);
						break;
					case WPA:
						break;
					case CBC:
						Page cbc_page;
						PA_CBC_Benefits cbc_plan_parser = new PA_CBC_Benefits(selectedPlan);
						cbc_page = cbc_plan_parser.parse(filename);
						pages.add(cbc_page);
						break;
					case AmeriHealth:
						Page amerihealth;
						NJ_Amerihealth_Rates ap = new NJ_Amerihealth_Rates(selectedPlan, 1);
						ap.printText();
						break;
					case Oxford: 
						Page oxford;
						NJ_Oxford_Benefits op = new NJ_Oxford_Benefits(selectedPlan);
						oxford = op.parse(filename);
						pages.add(oxford);
						pageMap.put(filename, oxford);
						break;

					}

				} catch (IOException e1) {
					publish("Invalid file.\n");
					e1.printStackTrace();
				}
				String parsed = String.format("File: %s parsed\n", selectedPlan.getName());
				System.out.println(parsed);
				publish(parsed + "\n");
				index++;
				setProgress(100 * (index) / size);
			}
		}
		if (!selectedRates.isEmpty()) {
			for (File selectedRate : selectedRates) {

				publish("Parsing: " + selectedRate.getName() + ".\n");
				filename = removeFileExtension(selectedRate.getName());

				WPA wpaType;
				if (filename.contains("HCA")) {
					wpaType = WPA.HCA;
				}
				else{
					wpaType = WPA.HMK;
				}
				String start_date = "";
				String end_date = "";
				if (quarter.equals("Q1")) {
					start_date = "01/01/2017";
					end_date = "3/31/2017";
				}
				if (quarter.equals("Q2")) {
					start_date = "04/01/2017";
					end_date = "6/30/2017";
				}
				if (quarter.equals("Q3")) {
					start_date = "07/01/2017";
					end_date = "9/30/2017";
				}
				if (quarter.equals("Q4")) {
					start_date = "10/01/2017";
					end_date = "12/31/2017";
				}
				try {
					switch (state) {
					case PA:
						switch (carrierType) {
						case UPMC:
							pa.PA_UPMC_Page[] UPMC_pages;
							pa.PA_UPMC_Rates UPMC_parser = new pa.PA_UPMC_Rates(selectedRate, start_date, end_date);
							UPMC_pages = UPMC_parser.parse();
							pa.PA_UPMC_ExcelWriter.populateExcel(UPMC_pages, filename);
							break;
						case Aetna:
							Page[] aetna_pages;
							PA_Aetna_Rates aetna_parser = new pa.PA_Aetna_Rates(selectedRate, start_date,
									end_date);
							pages.addAll(aetna_parser.parse());
							break;
						case WPA:
							PA_WPA_Rates wpa_parser = new PA_WPA_Rates(selectedRate, sheetIndex, start_date, end_date);
							switch (wpaType) {
							case HCA:
								pages.addAll(wpa_parser.parseHCA());
								break;
							case HMK:
								pages.addAll(wpa_parser.parseHMK());
								break;
							}
							break;
						case NEPA:
							PA_NEPA_Rates nepa_parser = new PA_NEPA_Rates(selectedRate, sheetIndex, start_date, end_date);
							pages.addAll(nepa_parser.parse());
							break;
						case CPA:
							PA_CPA_Rates cpa_parser = new PA_CPA_Rates(selectedRate, sheetIndex, start_date, end_date);
							pages.addAll(cpa_parser.parse());
							break;
						case IBC:
							Page ibc_page;
							PA_IBC_Rates ibc_parser = new PA_IBC_Rates(selectedRate, start_date, end_date);
							ibc_page = ibc_parser.parse();
							pages.add(ibc_page);
							break;
						case CBC:
							Page cbc_page = null;
							PA_CBC_Rates cbc_parser = new PA_CBC_Rates(selectedRate, cbc_page, sheetIndex, quarter, quarter);
							cbc_page = cbc_parser.parse();
							pages.add(cbc_page);
							break;
						case Geisinger:
							String s_page = "25";
							String e_page = "97";
							pa.PA_Geisinger_Rates geisinger_parser = new pa.PA_Geisinger_Rates();
							pages.addAll(geisinger_parser.parse(selectedRate, start_date, end_date, s_page, e_page));
							break;
						}
						break;
					case NJ:
						switch (carrierType) {
						case Aetna:
//							Page[] aetna_pages;
//							PA_Aetna_Rates aetna_parser = new pa.PA_Aetna_Rates(selectedRate, start_date,
//									end_date);
//							pages.addAll(aetna_parser.parse());							
//							break;
							NJ_Aetna_Q2_Rates nj_aetna = new NJ_Aetna_Q2_Rates(selectedRate, start_date, end_date);
							pages.addAll(nj_aetna.getResults());
							break;
						default:
							NJ_All_Carriers_Rates parser_nj = new NJ_All_Carriers_Rates(selectedRate, selectedOutputs.get(0), 
									carrierType, quarter, start_date, end_date);
							break;
						}
					}
					publish("File parsed\n");
					setProgress(100 * (index+1) / size);
					index++;

				} catch (IOException e1) {
					publish("Invalid file.\n");
					e1.printStackTrace();
				}
			}
		}
		return pages;
	}


	public static String removeFileExtension(String input) {
		return input.substring(0, input.lastIndexOf("."));
	}

	public ArrayList<Page> getValue() throws Exception {
	    return this.get();
	}
	
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
				System.out.println("Reaches here");
				for (int j = 2; j < 49; j++) {
					XSSFCell c1 = row.getCell(j);
					XSSFCell c2 = srcRow.getCell(j);
					System.out.println("Index: " + j);
					if (c1.getNumericCellValue() != c2.getNumericCellValue()) {
						c1.setCellStyle(style2);
						c2.setCellStyle(style2a);
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


	 @Override
	  protected void process(final List<String> chunks) {
	    // Updates the messages text area
		//System.out.println("WHEWHHEW");
	    for (final String string : chunks) {
	      textArea.append(string);
	    }
	  }

}
