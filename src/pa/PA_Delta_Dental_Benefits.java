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

	public PA_Delta_Dental_Benefits() throws FileNotFoundException, IOException {
	}
	
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		DentalPage page = new DentalPage();
		
		String text = pdfmanager.ToText();
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		
		ArrayList<Page> pages = new ArrayList<Page>();
		pages.add(page);
		return pages;
	}
	
}
