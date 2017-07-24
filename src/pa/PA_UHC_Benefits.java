package pa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.MedicalPage;

public class PA_UHC_Benefits implements Parser {
	
	String start_date;
	
	String end_date;
	
	PDFManager pdfmanager;
	
	ArrayList<Page> results;
	
	public PA_UHC_Benefits(String s_date, String e_date) throws FileNotFoundException, IOException {
		start_date = s_date;
		end_date = e_date;
	}
	
	public enum UHC_Benefits_Type {
		ONE, TWO
	}
	
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		pdfmanager = new PDFManager(file);
		int numPages = pdfmanager.getNumPages();
		
		UHC_Benefits_Type type;
		String text = pdfmanager.ToText();
		System.out.println(text);
		MedicalPage page = new MedicalPage();
		page.plan_pdf_file_name = filename;
		
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		int index = 1;
		
		StringBuffer sb = new StringBuffer();
		
		while (!tokens[index].equals("UHC")) {
			index++;
		}
		
		//Plan name
		while (!tokens[index].equals("Pennsylvania")) {
			sb.append(tokens[index++] + " ");
		}
		page.plan_name = sb.toString();
		System.out.println("Plan name: " + page.plan_name);
		sb.setLength(0);
		
		while (!tokens[index].equals("deductible)")) {
			index++;
		}
		index++;
		try {
			int val = Integer.parseInt(tokens[index].substring(1));
			page.dr_visit_copay = "$" + val;
		} catch (NumberFormatException e) {
			try {
				int value = Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
				page.dr_visit_copay = value + "%";
			} catch (NumberFormatException exception) {
				page.dr_visit_copay = "N/A";
			}
		}
		System.out.println("Dr copay: " + page.dr_visit_copay);
		index++;

		//Individual deductible
		try {
			int val = Integer.parseInt(tokens[index].substring(1));
			page.deductible_indiv = "$" + val;
		} catch (NumberFormatException e) {
			page.deductible_indiv = "$0";
			while (!tokens[index].equals("deductible.")) {
				index++;
			}
		}
		System.out.println("Individual deductible: " + page.deductible_indiv);
		index++;
		
		//Co-insurance
		if (tokens[index + 3].equals("co-insurance.")) {
			page.coinsurance = "0%";
		} else {
			page.coinsurance = tokens[index] + " after deductible";
		}
		System.out.println("Co-insurance: " + page.coinsurance);
		index++;

		
		while (!tokens[index].equals("Individual")) {
			index++;
		}
		index++;
		
		//Individual deductible
		try {
			Integer.parseInt(tokens[index]);
			page.deductible_indiv = tokens[index] + " per year";
			index += 2;
		} catch (NumberFormatException e) {
			page.deductible_indiv = "$0";
		}
		System.out.println("Deductible indiv: " + page.deductible_indiv);
		
		while (!tokens[index].equals("Family")) {
			index++;
		}
		index++;
		
		//Family deductible
		try {
			Integer.parseInt(tokens[index]);
			page.deductible_family = tokens[index] + " per year";
			index += 2;
		} catch (NumberFormatException e) {
			page.deductible_family = "$0";
		}
		System.out.println("Deductible family: " + page.deductible_family);
		
		while (!tokens[index].equals("Individual")) {
			index++;
		}
		index++;
		while (!tokens[index].equals("Individual")) {
			index++;
		}
		index++;
		
		
		//OOP Individual
		try {
			Integer.parseInt(tokens[index].replaceAll("[$,]", ""));
			page.oop_max_indiv = tokens[index] + " per year";
		} catch (NumberFormatException e) {
			page.oop_max_indiv = "$0";
		}
		System.out.println("OOP Indiv: " + page.oop_max_indiv);

		
		while (!tokens[index].equals("Family")) {
			index++;
		}
		index++;
		
		//OOP Family
		try {
			Integer.parseInt(tokens[index].replaceAll("[$,]", ""));
			page.oop_max_family = tokens[index] + " per year";
		} catch (NumberFormatException e) {
			page.oop_max_family = "$0";
		}
		System.out.println("OOP Family: " + page.oop_max_family);
		
		while (!tokens[index].equals("Emergency")) {
			index++;
		}
		index += 6;
		try {
			Integer.parseInt(tokens[index].replaceAll("[$,]", ""));
		} catch (NumberFormatException e) {
			try {
				
			} catch (NumberFormatException excep) {
				
			}
		}
		
 		ArrayList<Page> pages = new ArrayList<Page>();
 		pages.add(page);
		return pages;
	}
}
