package nj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import components.PDFManager;

public class NJ_Cigna_Benefits {
	
	public PDFManager pdfmanager;
	
	public NJ_Cigna_Benefits(File file) throws FileNotFoundException, IOException {
		this.pdfmanager = new PDFManager(file);
		
	}
}
