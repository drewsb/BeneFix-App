package pa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import components.DentalPage;
import components.PDFManager;
import components.Page;
import components.Parser;

public class PA_United_Concordia_Dental_Benefits implements Parser {

	@Override
	public ArrayList<Page> parse(File file, String filename)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		
		PDFManager pdfmanager = new PDFManager(file);
		String text = pdfmanager.ToText();
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		DentalPage page = new DentalPage();
		int index = 0;
		
		while (!tokens[index].toLowerCase().equals("groups")) {
			index++;
		}
		index++;
		StringBuilder sb = new StringBuilder();
		while (!tokens[index].equals("Network:")) {
			sb.append(tokens[index] + " ");
			index++;
		}
		page.product_name = sb.toString();
		
		while (!tokens[index].equals("Exams")) {
			index++;
		}
		index++;
		while (true) {
			try {
				Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
				break;
			} catch (NumberFormatException e) {
				index++;
			} catch (IndexOutOfBoundsException ee) {
				index++;
			}
		}
		page.class_I_diagnostic_preventive = tokens[index];

		while (!tokens[index].equals("Fillings)") && !tokens[index].equals("(Fillings)")) {
			index++;
		}
		while (true) {
			try {
				Integer.parseInt(tokens[index].substring(0, tokens[index].length() - 1));
				break;
			} catch (NumberFormatException e) {
				index++;
			} catch (IndexOutOfBoundsException ee) {
				index++;
			}
		}
		page.class_II_basic = tokens[index];
		
		while (!tokens[index].equals("Crowns")) {
			index++;
		}
		page.class_III_major = tokens[++index];
		
		while (!tokens[index].equals("Treatment")) {
			index++;
		}
		page.orthodontics = tokens[++index];
		if (page.orthodontics.equals("0%")) {
			page.orthodontics = "Not Covered";
		} else {
			page.orthodontics = page.orthodontics + " IN, " + tokens[++index] + " OON up to age 19"; 
		}
		
		while (!tokens[index].equals("family)")) {
			index++;
		}
		page.deductible_ind_fam = tokens[++index];
		
		while (!tokens[index].equals("person)")) {
			index++;
		}
		page.annual_max = tokens[++index].split("/")[0];
		
		while (!tokens[index].equals("person)")) {
			index++;
		}
		page.orthodonitics_lifetime_maximum = tokens[++index];
		if (page.orthodonitics_lifetime_maximum.equals("$0")) {
			page.orthodonitics_lifetime_maximum = "Not Covered";
		}
		
		page.endodonitcs = page.class_II_basic;
		page.periodontics = page.class_II_basic;
		page.carrier = "United Concordia";
		page.states = "PA";
		page.waiting_period = "No";
		result.add(page);
		page.printPage();
		
		return result;
	}

}
