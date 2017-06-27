package pa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import components.DentalPage;
import components.PDFManager;

public class PA_Delta_Dental_Benefits {
	
	PDFManager pdfmanager;

	public PA_Delta_Dental_Benefits(File file) throws FileNotFoundException, IOException {
		this.pdfmanager = new PDFManager(file);
		this.parse();
	}
	
	public DentalPage parse() throws IOException {
		DentalPage page = new DentalPage();
		
		String text = pdfmanager.ToText();
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		
		return page;
	}
	
}
