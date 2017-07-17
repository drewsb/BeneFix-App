package pa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import components.DentalPage;
import components.PDFManager;
import components.Page;
import components.Parser;

public class PA_Oxford_Dental_Benefits implements Parser {

	
	public PA_Oxford_Dental_Benefits() {
		
	}
	
	public ArrayList<Page> parse(File file, String fileName) throws FileNotFoundException, IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		PDFManager pdfmanager = new PDFManager(file);
		String text = pdfmanager.ToText();
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		int index = 0;
		DentalPage page = new DentalPage();
		StringBuffer sb = new StringBuffer();
		
		while (!tokens[index].equals("plan")) {
			index++;
		}
		index++;
		while(!tokens[index].contains("/")) {
			sb.append(tokens[index] + " ");
			index++;
		}
		String[] last_name_comp = tokens[index].split("/");
		sb.append(last_name_comp[0]);
		
		page.product_name = sb.toString();
		
		while (!tokens[index].equals("Deductible")) {
			index++;
		}
		index++;
		String indiv = tokens[index];
		
		while (!tokens[index].equals("Deductible")) {
			index++;
		}
		index++;
		String fam = tokens[index];
		if (fam.length() > 4) {
			fam = fam.substring(0, fam.length() / 2);
		}
		page.deductible_ind_fam = indiv + "/" + fam;
		
		while (!tokens[index].equals("services.)")) {
			index++;
		}
		index++;
		page.annual_max = tokens[index];
		
		while (!tokens[index].equals("PREVENTIVE")) {
			index++;
		}
		index++;
		
		page.class_I_diagnostic_preventive = tokens[index];
		
		while (!tokens[index].equals("PREVENTIVE")) {
			index++;
		}
		page.class_II_basic = tokens[--index];
		int class2 = index;
		
		while (!tokens[index].equals("extractions)")) {
			index++;
		}
		page.class_III_major = tokens[++index];
		int class3 = index;
		
		int count = 0;
		while (!tokens[count].equals("Endodontics")) {
			count++;
		}
		if (count > class2 && count < class3) {
			page.endodonitcs = page.class_II_basic;
			page.periodontics = page.class_II_basic;
		} else {
			page.endodonitcs = page.class_III_major;
			page.periodontics = page.class_III_major;
		}
		
		try {
			while (!tokens[index].equals("ORTHODONTIC")) {
				index++;
			}
			while (!tokens[index].equals("bite")) {
				index++;
			}
			page.orthodontics = tokens[++index];
			count = 0;
			
		} catch (IndexOutOfBoundsException e) {
			page.orthodonitics_lifetime_maximum = "Not Covered";
			page.orthodontics = "Not Covered";
		}
		page.printPage();
		result.add(page);
		return result;
	}
}
