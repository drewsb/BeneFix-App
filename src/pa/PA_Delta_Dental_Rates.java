package pa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import components.PDFManager;
import components.Page;
import components.Parser;

//DEPRECATED
public class PA_Delta_Dental_Rates implements Parser {

	@Override
	public ArrayList<Page> parse(File file, String filename)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		
		ArrayList<Page> result = new ArrayList<Page>();
		PDFManager pdfmanager = new PDFManager(file);
		for (int i = 0; i < pdfmanager.getNumPages(); i++) {
			result.addAll(parsePage(pdfmanager, i));
		}
		
		return result;
	}
	
	public ArrayList<Page> parsePage(PDFManager pdfmanager, int pageNum) throws IOException {
		ArrayList<Page> result = new ArrayList<Page>();
		
		String text = pdfmanager.ToText(pageNum, pageNum);
		System.out.println(text);
		text = text.replaceAll("\\s", ";");
		String[] tokens = text.split(";");
		int index = 0;
		
		
		while (true) {
			try {				
				while (!tokens[index].equals("Area")) {
					index++;
				}
				index++;
				
				String currArea = tokens[index];
				while (!tokens[index].equals("Tier")) {
					index++;
				}
				String currTier = tokens[index - 1];
				int tier = Integer.parseInt(currTier);
				
				
			} catch (IndexOutOfBoundsException e) {
				return result;
			}
		}
	}

}
