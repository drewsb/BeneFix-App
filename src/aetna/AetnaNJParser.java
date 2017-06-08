package aetna;

import java.io.File;
import java.io.IOException;

import components.PDFManager;

public class AetnaNJParser {
	
	String filename;
	
	public AetnaNJParser(String filename) throws IOException {
		this.filename = filename;
		PDFManager manager = new PDFManager();
		File file = new File(filename);
		manager.setFilePath(file.getAbsolutePath());
		System.out.println(manager.ToText(2, 2));
	}
	
	
}
