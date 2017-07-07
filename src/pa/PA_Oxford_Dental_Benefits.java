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
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		int index = 0;
		System.out.println(text);
		
		DentalPage page = new DentalPage();
		
		
		return result;
	}
}
