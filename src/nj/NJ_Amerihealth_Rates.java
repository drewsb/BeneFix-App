package nj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.MedicalPage;

public class NJ_Amerihealth_Rates implements Parser {
	
	String text;
	String[] tokens;
	int pageNum;
	int currIndex;
	ArrayList<MedicalPage> pages;
	
	public NJ_Amerihealth_Rates(int pageNum) throws IOException {
		this.pageNum = pageNum;
		this.currIndex = 0;
		pages = new ArrayList<MedicalPage>();
	}	
	
	public void printText() {
		System.out.println(this.text);
	}
	
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		PDFManager pdfmanager = new PDFManager();
		pdfmanager.setFilePath(file.getAbsolutePath());
		this.text = pdfmanager.ToText();
		this.tokens = text.split(" |\n");
		System.out.println("Total number of tokens: " + tokens.length);
		
		
		MedicalPage p1 = new MedicalPage();
		MedicalPage p2 = new MedicalPage();
		MedicalPage p3 = new MedicalPage();
		MedicalPage p4 = new MedicalPage();
		MedicalPage p5 = new MedicalPage();
		MedicalPage p6 = new MedicalPage();
		
		ArrayList<MedicalPage> currPages = new ArrayList<MedicalPage>();
		currPages.add(p1);
		currPages.add(p2);
		currPages.add(p3);
		currPages.add(p4);
		currPages.add(p5);
		currPages.add(p6);

		for (MedicalPage p: currPages) {
			p.plan_pdf_file_name += "SEH " + tokens[currIndex];
		}
		currIndex++;
		
		
		return null;
	}
	
	public void printCurr() {
		System.out.println(tokens[currIndex]);
	}
	
	public void incr() {
		this.currIndex++;
	}
	
}
