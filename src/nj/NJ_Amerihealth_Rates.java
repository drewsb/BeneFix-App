package nj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import components.PDFManager;
import components.Page;

public class NJ_Amerihealth_Rates {
	
	String text;
	String[] tokens;
	int pageNum;
	int currIndex;
	ArrayList<Page> pages;
	
	public NJ_Amerihealth_Rates(File file, int pageNum) throws IOException {
		PDFManager pdfmanager = new PDFManager();
		pdfmanager.setFilePath(file.getAbsolutePath());
		this.text = pdfmanager.ToText();
		this.tokens = text.split(" |\n");
		System.out.println("Total number of tokens: " + tokens.length);
		this.pageNum = pageNum;
		this.currIndex = 0;
		pages = new ArrayList<Page>();
	}	
	
	public void printText() {
		System.out.println(this.text);
	}
	
	public Page parse() {
		Page p1 = new Page();
		Page p2 = new Page();
		Page p3 = new Page();
		Page p4 = new Page();
		Page p5 = new Page();
		Page p6 = new Page();
		
		ArrayList<Page> currPages = new ArrayList<Page>();
		currPages.add(p1);
		currPages.add(p2);
		currPages.add(p3);
		currPages.add(p4);
		currPages.add(p5);
		currPages.add(p6);

		for (Page p: currPages) {
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
