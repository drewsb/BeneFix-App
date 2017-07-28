package pa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.Attributes.Name;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import components.DentalPage;
import components.PDFManager;
import components.Page;
import components.Parser;

public class PA_Aetna_Dental_Benefits implements Parser {

	@Override
	public ArrayList<Page> parse(File file, String filename)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		ArrayList<Page> result = new ArrayList<Page>();
		PDFManager pdfmanager = new PDFManager(file);
		String text = pdfmanager.ToText();
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		DentalPage page;
		int index = 0;
		
		while (!tokens[index].equals("Individual")) {
			index++;
		}
		int count = 0;
		
		while (!tokens[index].equals("Family")) {
			if (!tokens[index].equals("")) {
				count++;
			}
			index++;
		}
		System.out.println("Count: " + count);
		if (count == 2) {
			result.addAll(typeOne(tokens, filename));
		} else {
			result.addAll(typeTwo(tokens, filename));
		}
	
		return result;
	}
	
	public ArrayList<DentalPage> typeOne(String[] tokens, String filename) {
		System.out.println("TYPE ONE");
		DentalPage page = new DentalPage();
		ArrayList<DentalPage> result = new ArrayList<DentalPage>();
		int index = 0;
		while (!tokens[index].equals("Individual")) {
			index++;
		}
		index++;
		page.deductible_ind_fam = tokens[index];
		if (!page.deductible_ind_fam.equals("None")) {
			while (true) {
				index++;
				try {
					Integer.parseInt(tokens[index].substring(1));
					break;
				} catch (NumberFormatException e) {
					index++;
				} catch (StringIndexOutOfBoundsException e) {
					index++;
				}
			}
			page.deductible_ind_fam += "/" + tokens[index];
		}
	
		while (!tokens[index].equals("Services") && !tokens[index].equals("Services**")) {
			index++;
		}
		page.class_I_diagnostic_preventive = tokens[++index];
		
		while (!tokens[index].equals("Services")) {
			index++;
		}
		page.class_II_basic = tokens[++index];
		
		while (!tokens[index].equals("Services")) {
			index++;
		}
		page.class_III_major = tokens[++index];
		
		if (page.class_I_diagnostic_preventive.equals("Not")) {
			page.class_I_diagnostic_preventive = "Not Covered";
		}
		if (page.class_II_basic.equals("Not")) {
			page.class_II_basic = "Not Covered";
		}
		if (page.class_III_major.equals("Not")) {
			page.class_III_major = "Not Covered";
		}
		
		while (!tokens[index].equals("Maximum") && !tokens[index].equals("Maximum*")) {
			index++;
		}
		page.annual_max = tokens[++index];
		
		while (!tokens[index].equals("Copay")) {
			index++;
		}
		page.office_visit_copay = tokens[++index];
		
		while (!tokens[index].equals("Services") && !tokens[index].equals("Services**")) {
			index++;
		}
		page.orthodontics = tokens[++index];
		if (page.orthodontics.equals("Not")) {
			page.orthodontics = "Not Covered";
		}
		
		while(!tokens[index].equals("Maximum")) {
			index++;
		}
		page.orthodonitics_lifetime_maximum = tokens[++index];
		if (page.orthodonitics_lifetime_maximum.equals("Not")) {
			page.orthodonitics_lifetime_maximum = "Not Covered";
		}	
		if (page.orthodonitics_lifetime_maximum.equals("***")) {
			page.orthodonitics_lifetime_maximum = "See PDF";
		}
		
		filename = filename.replaceAll("[\\s-_]", ";");
		String[] name = filename.split(";");
		if (name[0].contains("+")) {
			page.group = name[0];
			page.contribution_type = name[1];
		} else {
			page.group = name[0] + "-" + name[1];
			page.contribution_type = name[2];

		}
		page.carrier = "Aetna";
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 3; i < name.length; i++) {
			sb.append(name[i] + " ");
		}
		page.product_name = sb.toString();
		page.states = "PA";
		
		result.add(page);
		page.printPage();
		return result;
	}
	
	public ArrayList<DentalPage> typeTwo(String[] tokens, String filename) {
		System.out.println("TYPE TWO");
		DentalPage page1 = new DentalPage();
		DentalPage page2 = new DentalPage();
		ArrayList<DentalPage> result = new ArrayList<DentalPage>();
		
		int index = 0;
		while (!tokens[index].equals("Individual")) {
			index++;
		}
		index++;
		page1.deductible_ind_fam = tokens[index];
		page2.deductible_ind_fam = tokens[++index];
		if (!page1.deductible_ind_fam.equals("None")) {
			index++;
			while (true) {
				try {
					Integer.parseInt(tokens[index].substring(1));
					break;
				} catch (NumberFormatException e) {
					index++;
				} catch (StringIndexOutOfBoundsException e) {
					index++;
				}
			}
			page1.deductible_ind_fam += "/" + tokens[index];
		}
		if (!page2.deductible_ind_fam.equals("None")) {
			index++;
			while (true) {
				try {
					Integer.parseInt(tokens[index].substring(1));
					break;
				} catch (NumberFormatException e) {
					index++;
				} catch (StringIndexOutOfBoundsException e) {
					index++;
				}
			}
			page2.deductible_ind_fam += "/" + tokens[index];
		}
	
		while (!tokens[index].equals("Services")) {
			index++;
		}
		page1.class_I_diagnostic_preventive = tokens[++index];
		page2.class_I_diagnostic_preventive = tokens[++index];
		
		while (!tokens[index].equals("Services")) {
			index++;
		}
		page1.class_II_basic = tokens[++index];
		page2.class_II_basic = tokens[++index];
		
		while (!tokens[index].equals("Services")) {
			index++;
		}
		page1.class_III_major = tokens[++index];
		page2.class_III_major = tokens[++index];
		
		if (page1.class_I_diagnostic_preventive.equals("Not")) {
			page1.class_I_diagnostic_preventive = "Not Covered";
		}
		if (page1.class_II_basic.equals("Not")) {
			page1.class_II_basic = "Not Covered";
		}
		if (page1.class_III_major.equals("Not")) {
			page1.class_III_major = "Not Covered";
		}
		if (page2.class_I_diagnostic_preventive.equals("Not")) {
			page2.class_I_diagnostic_preventive = "Not Covered";
		}
		if (page2.class_II_basic.equals("Not")) {
			page2.class_II_basic = "Not Covered";
		}
		if (page2.class_III_major.equals("Not")) {
			page2.class_III_major = "Not Covered";
		}
		
		while (!tokens[index].equals("Maximum") && !tokens[index].equals("Maximum*")) {
			index++;
		}
		page1.annual_max = tokens[++index];
		page2.annual_max = tokens[++index];
		
		while (!tokens[index].equals("Copay")) {
			index++;
		}
		page1.office_visit_copay = tokens[++index];
		page2.office_visit_copay = tokens[++index];
		
		while (!tokens[index].equals("Services") && !tokens[index].equals("Services**")) {
			index++;
		}
		page1.orthodontics = tokens[++index];
		if (page1.orthodontics.equals("Not")) {
			page1.orthodontics = "Not Covered";
			page2.orthodontics = tokens[index + 2];
			if (page2.orthodontics.equals("copay")) {
				page2.orthodontics = tokens[index + 3];
			}
		} else {
			page2.orthodontics = tokens[++index];
			if (page2.orthodontics.equals("copay")) {
				page2.orthodontics = tokens[++index];
			}
		}
		if (page2.orthodontics.equals("Not")) {
			page2.orthodontics = "Not Covered";
		}
		
		while (!tokens[index].equals("Maximum")) {
			index++;
		}
		page1.orthodonitics_lifetime_maximum = tokens[++index];
		if (page1.orthodonitics_lifetime_maximum.equals("Not")) {
			page1.orthodonitics_lifetime_maximum = "Not Covered";
			page2.orthodonitics_lifetime_maximum = tokens[index + 2];
		} else {
			page2.orthodonitics_lifetime_maximum = tokens[++index];
		}
		if (page2.orthodonitics_lifetime_maximum.equals("Not")) {
			page2.orthodonitics_lifetime_maximum = "Not Covered";
		}
		
		if (page1.orthodonitics_lifetime_maximum.equals("***")) {
			page1.orthodonitics_lifetime_maximum = "See PDF";
		}
		if (page2.orthodonitics_lifetime_maximum.equals("***")) {
			page2.orthodonitics_lifetime_maximum = "See PDF";
		}
		
		filename = filename.replaceAll("[\\s-_]", ";");
		String[] name = filename.split(";");
		for (int i = 0; i < name.length; i++) {
			System.out.println(name[i]);
		}
		if (name[0].contains("+")) {
			page1.group = name[0];
			page2.group = name[0];
			page1.contribution_type = name[1];
			page2.contribution_type = name[1];
		} else {
			page1.group = name[0] + "-" + name[1];
			page2.group = name[0] + "-" + name[1];
			page1.contribution_type = name[2];
			page2.contribution_type = name[2];
		}
		page1.carrier = "Aetna";
		page2.carrier = "Aetna";
		
		StringBuilder product1 = new StringBuilder();
		StringBuilder product2 = new StringBuilder();
		
		for (int i = 3; i < name.length; i++) {
			product1.append(name[i] + " ");
			product2.append(name[i] + " ");
		}
		product1.append("(1)");
		product2.append("(2)");
		page1.product_name = product1.toString();
		page2.product_name = product2.toString();
		page1.states = "PA";
		page2.states = "PA";
		
		page1.printPage();
		page2.printPage();
		result.add(page1);
		result.add(page2);
		return result;
	}
}
