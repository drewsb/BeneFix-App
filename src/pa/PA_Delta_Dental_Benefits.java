package pa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import components.DentalPage;
import components.PDFManager;
import components.Page;
import components.Parser;

public class PA_Delta_Dental_Benefits implements Parser{
	
	PDFManager pdfmanager;
	DentalPage page;

	public PA_Delta_Dental_Benefits() throws FileNotFoundException, IOException {
		
	}
	
	public DentalPage getPage() {
		return this.page;
	}
	
	public enum Type {
		One, Two
	}
	
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		this.pdfmanager = new PDFManager(file);
		String text = pdfmanager.ToText();

		Type type = Type.One;
		
		DentalPage page = new DentalPage();
		
		page.carrier = "Delta";
		page.start_date = "2017-01-01";
		page.end_date = "2017-12-01";
		
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		text = text.replaceAll(",", "");
		String[] tokens = text.split(";");
		int index = 0; 
		boolean atEnd = false;
		
		int i = 0;
		while (!tokens[i].equals("Sample")) {
			i++;
		}
		if (i < 5) {
			atEnd = true;
		}
		
		if (!atEnd) {
			while (!tokens[index].equals("with")) {
				index++;
			}
			index++;
				
			StringBuilder stb1 = new StringBuilder();
			for (int j = 0; j < 3; j++) {
				String groupStr = "";
				if (tokens[index + j].equals("-")) {
					groupStr = "to ";
				} else {
					groupStr = tokens[index + j] + " ";
				}
				stb1.append(groupStr);
			}
			page.group = stb1.toString();
			page.minimum_enrolled = tokens[index];
		}
		
		while (!tokens[index].equals("Benefits1") && !tokens[index].equals("Benefits") && !tokens[index].equals("Benefits¹")) {
			index++;
			System.out.println("reaches here"); 
		}
		index++;
		
		StringBuilder sb = new StringBuilder();
		while (!tokens[index].equals("Employer")) {
			sb.append(tokens[index] + " ");
			index++;
		}
		page.product_name = sb.toString();
		System.out.println("PRoduct name: " + page.product_name);
		
		while (!tokens[index].equals("solution")) {
			index++;
		}
		index++;
		
		page.class_I_diagnostic_preventive = tokens[index];
		index++;
		
		while (true) {
			try {
				String s = tokens[index];
				if (s.length() < 2) {
					index++;
					continue;
				}
				Integer.parseInt(s.substring(0, s.length() - 1));
				break;
			} catch (NumberFormatException e) {
				index++;
			}
		}

		page.class_II_basic = tokens[index];
		
		while (!tokens[index].equals("implants)")) {
			index++;
		}
		index++;
		try {
			Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
			page.class_III_major = tokens[index];
		} catch (NumberFormatException e) {
			page.class_III_major = "Not covered";
		}
				
		while (!tokens[index].equals("Services") && !tokens[index].substring(0, tokens[index].length() - 1).equals("Services")) {
			index++;
		}
		index++;
 		
		try {
			Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
			page.endodonitcs = tokens[index];
			page.periodontics = tokens[index];
		} catch (NumberFormatException e) {
			page.endodonitcs = "Not Covered";
			page.periodontics = "Not Covered";
		}
		
		while (!tokens[index].equals("19")) {
			index++;
		}
		index++;
		
		try {
			Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
			page.orthodontics = tokens[index];
			index++;
		} catch (NumberFormatException e) {
			page.orthodontics = "Not Covered";
			index += 2;
		}

		try {
			Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
			index++;
		} catch (NumberFormatException e) {
		}
		
		if (tokens[index].equals("Orthodontic")) {
			index += 3;
			page.orthodonitics_lifetime_maximum = tokens[index];
		} else {
			page.orthodonitics_lifetime_maximum = "Not Covered";
		}
		
		while (!tokens[index].equals("family)")) {
			index++;
		}
		index++;
		
		page.deductible_ind_fam = tokens[index] + "/ calendar year";
		
		while (!tokens[index].equals("D&P?")) {
			index++;
		}
		index++;
		
		if (tokens[index].equals("Yes")) {
			page.class_I_diagnostic_preventive = page.class_I_diagnostic_preventive + "; deductible waived";
		}
		
		String str = "";
		while (true) {
			if (tokens[index].length() <= 1) {
				index++;
				continue;
			} else if (tokens[index].length() == 6) {
				str = tokens[index].substring(0, 5);
			} else {
				str = tokens[index];
			}
			try {
				Integer.parseInt(str.substring(1));
				break;
			} catch (NumberFormatException e) {
				index++;
			}
		}
		page.annual_max = str;
		
		if (atEnd) {
			while (!tokens[index].equals("businesses")) {
				index++;
			}
			index+=2;
			
			StringBuilder stb = new StringBuilder();
			for (int j = 0; j < 3; j++) {
				String groupStr = "";
				if (tokens[index + j].equals("-")) {
					groupStr = "to ";
				} else {
					groupStr = tokens[index + j] + " ";
				}
				stb.append(groupStr);
			}
			page.group = stb.toString();
			page.minimum_enrolled = tokens[index];
		}
		
		page.printPage();
		
		ArrayList<Page> pages = new ArrayList<Page>();
		pages.add(page);
		return pages;
	}
	
}
