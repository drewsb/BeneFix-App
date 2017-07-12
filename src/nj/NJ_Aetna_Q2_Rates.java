package nj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.MedicalPage;

public class NJ_Aetna_Q2_Rates implements Parser {
	
	final String output = "nj_aetna_q2.xlsx";
	final String otherWorkbook = "NJ Q2 Aetna.xlsx";

	PDFManager pdfmanager;
	
	String start_date;
	
	String end_date;
	
	int pageNum;
	
	int numPages;
	
	ArrayList<Page> result;
	
	public NJ_Aetna_Q2_Rates(String s_date, String e_date) throws FileNotFoundException, IOException {
		pageNum = 0;
		start_date = s_date;
		end_date = e_date;
		numPages = pdfmanager.getNumPages();
	}
	
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		result = new ArrayList<Page>();
		for(int a = 0; a< numPages; a++){
			this.pdfmanager = new PDFManager(file);
			pdfmanager.setFilePath(file.getAbsolutePath());
			MedicalPage p = new MedicalPage();
			p.state = "NJ";
			p.start_date = start_date;
			p.end_date = end_date;
			String text = pdfmanager.ToText(a, a);
			System.out.println(text);
			
			text = text.replaceAll("\\s", ";");
			String[] tokens = text.split(";");
			
			int index = 0;
			
			while (!tokens[index].equals("Area:")) {
				index++;
			}
			index++;
			p.group_rating_area = tokens[index];
			System.out.println("Rating area: " + p.group_rating_area);
			index += 4;
			p.carrier_plan_id = tokens[index];
			System.out.println("Plan ID: " + p.carrier_plan_id);
			index += 3;
			StringBuilder sb = new StringBuilder();
			while (!tokens[index].equals("Age")) {
				sb.append(tokens[index++] + " ");
			}
			p.product_name = sb.toString();
			System.out.println("Plan name: " + p.product_name);
			
			HashMap<String,Double> non_tob_dict = new HashMap<String,Double>();
			
			while (!tokens[index].equals("0-20")) {
				index++;
			}
			
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 6; j+=2) {
					System.out.println("Key: " + tokens[index + j] + " Value: " + tokens[index + j + 1]);
					non_tob_dict.put(tokens[index + j], Double.parseDouble(tokens[index + j + 1]));
				}
				index += 6;
			}
			p.non_tobacco_dict = non_tob_dict;
			result.add(p);
		}	
		return result;
	}
	
	public void compareWorkbooks() throws IOException {
		FileInputStream fis = new FileInputStream(output);
		FileInputStream fis2 = new FileInputStream(otherWorkbook);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFWorkbook workbook2 = new XSSFWorkbook(fis2);
		
		
	}
	
}
