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

public class PA_Highmark_Dental_Benefits implements Parser {

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
		int index = 0;
		
		DentalPage page = new DentalPage();
		
		while (!tokens[index].equals("Dental")) {
			index++;
		}
		index++;
		StringBuilder sb = new StringBuilder();
		while (!tokens[index].equals("In-Network")) {
			sb.append(tokens[index] + " ");
			index++;
		}
		page.product_name = sb.toString();
		
		while (!tokens[index].equals("Services)")) {
			index++;
		}
		while (!tokens[index].contains("/")) {
			index++;
		}
		page.deductible_ind_fam = tokens[index];
		
		while (!tokens[index].equals("member")) {
			index++;
		}
		page.annual_max = tokens[++index];
		
		while (!tokens[index].equals("Exams")) {
			index++;
		}
		page.class_I_diagnostic_preventive = tokens[++index];
		
		while (!tokens[index].equals("Resins")) {
			index++;
		}
		page.class_II_basic = tokens[++index];
		
		while (!tokens[index].equals("Crowns")) {
			index++;
		}
		page.class_III_major = tokens[++index];
		
		while (!tokens[index].equals("Treatment")) {
			index++;
		}
		try {
			String orthoStr = tokens[++index];
			Integer.parseInt(orthoStr.substring(0, orthoStr.length() - 1));
			page.orthodontics = orthoStr;
		} catch (NumberFormatException e) {
			page.orthodontics = "Not Covered";
		}

		while (!tokens[index].equals("dependent")) {
			index++;
		}
		while (!tokens[index].contains("$") && !tokens[index].equals("Not")) {
			index++;
		}
		if (tokens[index].contains("$")) {
			page.orthodonitics_lifetime_maximum = tokens[index];
		} else {
			page.orthodonitics_lifetime_maximum = "Not Covered";
		}

		
		int endoCount = 0;
		while (!tokens[endoCount].equals("Endodontics")) {
			endoCount++;
		}
		endoCount++;
		page.endodonitcs = tokens[endoCount];
		
		int perioCount = 0;
		while (!tokens[perioCount].equals("Periodontics")) {
			perioCount++;
		}
		while (true) {
			try {
				Integer.parseInt(tokens[perioCount].substring(0, tokens[perioCount].length() - 1));
				page.periodontics = tokens[perioCount];
				break;
			} catch (NumberFormatException e) {
				perioCount++;
			}
		}
		
		page.printPage();
		result.add(page);
		return result;
	}

	
}
