package pa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;

import components.PDFManager;
import components.Page;
import components.MedicalPage;
import components.Parser;

public class PA_IBC_Benefits implements Parser {
	
	static String text1, text2, text3, text4, text5, text6;

	static ArrayList<Page> pages;

	static int numPages;

	static String start_date;

	static String end_date;
	
	public PA_IBC_Benefits(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
	}
	
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		
		// variables for Page creation
		String product = "";
		Integer carrier_id = 13;
		
		//arraylist of products
		ArrayList<Page> products = new ArrayList();

		//create new PDFmanager object
		PDFManager pdfManager = new PDFManager();
		PDDocument document = PDDocument.load(file);

		// split PDF, rates are every other page for Geisinger
		Splitter splitter = new Splitter();
		List<PDDocument> pages = splitter.split(document);
		ArrayList<PDDocument> pages_arraylist = new ArrayList<PDDocument>(pages);
		
		//grab text from first page, to figure out which parser type to use
		text1 = pdfManager.ToText(pages_arraylist.get(0));
		text2 = pdfManager.ToText(pages_arraylist.get(1));
		text3 = pdfManager.ToText(pages_arraylist.get(2));
		text4 = pdfManager.ToText(pages_arraylist.get(3));
		text5 = pdfManager.ToText(pages_arraylist.get(4));
		text6 = pdfManager.ToText(pages_arraylist.get(5));
		
		
		//System.out.println(text);
		
		//int token_length = tokens.length;
		String product_name = "";
		String plan_code = "";
		String rating_area = "";
		String plan_name = "";
		String state = "PA";
		
		//start here
		String[] tokens = text1.split("\n");
		String[] tokens2 = text2.split("\n");
		String[] tokens3 = text3.split("\n");
		String[] tokens4 = text4.split("\n");
		String[] tokens5 = text5.split("\n");
		String[] tokens6 = text6.split("\n");
		
		for (int i = 0; i < tokens2.length; i++) {
			System.out.println(tokens2[i]);
		}
		 
		
		//go through each line of page
		if (tokens[0].toLowerCase().contains("soldbenefits"))
			System.out.println("no plz god no");
		else if (tokens[0].toLowerCase().contains("dpos") || tokens[0].toLowerCase().contains("classic"))
			products.add(dposClassicParser(tokens, tokens2, tokens3, tokens4, tokens5, tokens6));
//		else if (tokens[0].contains(""))
//			dposParser();
//		else if (tokens[0].contains(""))
//			proactiveParser();
		
		return products;

	}
	
	public MedicalPage hmoParser() {
		return null;
	}
	
	public MedicalPage ppoParser() {
		return null;
	}
	
	public MedicalPage proactiveParser() {
		return null;
	}
	
	public MedicalPage dposClassicParser(String[] tokens_to_parse, String[] tokens_to_parse2, String[] tokens_to_parse3,
			String[] tokens_to_parse4, String[] tokens_to_parse5, String[] tokens_to_parse6) {
		
		MedicalPage page = new MedicalPage();
		
		//getting substring starting from 'Classic', break into array split by '/'
		String[] coinsurance_string = tokens_to_parse[0].substring((tokens_to_parse[0].indexOf("Classic") + 8), tokens_to_parse[0].length()).split("\\s+");
		String[] coinsurance_sub_string = coinsurance_string[0].split("/");
		if (coinsurance_sub_string.length >= 4) {
			page.coinsurance = coinsurance_sub_string[3];
		}
		
//		for (int i = 0; i < tokens_to_parse.length; i++) {
//			if (tokens_to_parse[i].length() < 3 && tokens_to_parse[i].length() > 0) {
//				
//			}
//		}
		
		for (int i = 0; i < tokens_to_parse.length; i++) {
			if (tokens_to_parse[i].contains("DEDUCTIBLE")) {
				String[] temp_tokens = tokens_to_parse[i].split("\\s+");
				if (temp_tokens.length < 2) { //several lines have deductible, so only grab the right one
					String[] sub_tokens = tokens_to_parse[i + 1].split("\\s+");
					page.deductible_indiv = sub_tokens[1].replace(",", "");
					page.oon_deductible_indiv = sub_tokens[2].replace(",", "");
					sub_tokens = tokens_to_parse[i + 2].split("\\s+");
					page.deductible_family = sub_tokens[1].replace(",", "");
					page.oon_deductible_family = sub_tokens[2].replace(",", "");
				}
			} else if (tokens_to_parse[i].contains("OUT-OF-POCKET")) {
				String[] sub_tokens = tokens_to_parse[i + 2].split("\\s+");
				page.oop_max_indiv = sub_tokens[1].replace(",", "");
				page.oon_oop_max_indiv = sub_tokens[2].replace(",", "");
				sub_tokens = tokens_to_parse[i + 3].split("\\s+");
				page.oop_max_family = sub_tokens[1].replace(",", "");
				page.oon_oop_max_family = sub_tokens[2].replace(",", "");
			} else if (tokens_to_parse[i].contains("Primary Care Services")) {
				if (tokens_to_parse[i].contains("no deductible")) {
					String[] sub_tokens = tokens_to_parse[i].split("\\s+");
					page.dr_visit_copay = sub_tokens[3] + " " + sub_tokens[4] + " " + sub_tokens[5] + " " + sub_tokens[6].replaceAll("[0-9]", "");
				} else {
					//only grab two subtokens, "$25 copayment" etc
				}
			} else if (tokens_to_parse[i].contains("Specialist Services")) {
				String[] sub_tokens = tokens_to_parse[i].split("\\s+");
				page.specialist_visit_copay = sub_tokens[2] + " " + sub_tokens[3] + " " + sub_tokens[4] + " " + sub_tokens[5].replaceAll("[0-9]", "");	
			} else if (tokens_to_parse[i].contains("OUTPATIENT X-RAY")) {
				String[] sub_tokens = tokens_to_parse[i + 1].split("\\s+");
				page.outpatient_diagnostic_x_ray = sub_tokens[2] + " " + sub_tokens[3] + " " + sub_tokens[4] + " " + sub_tokens[5].replaceAll("[0-9]", "");
			} else if (tokens_to_parse[i].contains("MRI/MRA, CT/CTA Scan, PET Scan")) {
				String[] sub_tokens = tokens_to_parse[i].split("\\s+");
				page.outpatient_complex_imaging = sub_tokens[5] + " " + sub_tokens[6] + " " + sub_tokens[7] + " " + sub_tokens[8].replaceAll("[0-9]", "");
			}
		} //end for loop through tokens1
		for (int i = 0; i < tokens_to_parse2.length; i++) {
			if (tokens_to_parse2[i].contains("OUTPATIENT LABORATORY")) {
				String[] sub_tokens = tokens_to_parse2[i].split("\\s+");
				page.outpatient_diagnostic_lab = sub_tokens[2] + " " + sub_tokens[3] + " " + sub_tokens[4].replaceAll("[0-9]", "");
			} else if (tokens_to_parse2[i].contains("OCCUPATIONAL THERAPIES")) {
				String[] sub_tokens = tokens_to_parse2[i + 2].split("\\s+");
				page.physical_occupational_therapy = sub_tokens[0] + " " + sub_tokens[1] + " " + sub_tokens[2] + " " + sub_tokens[3].replaceAll("[0-9]", "");
			} else if (tokens_to_parse2[i].contains("OUTPATIENT SURGERY")) {
				String[] sub_tokens = tokens_to_parse2[i + 1].split("\\s+");
				String[] sub_tokens2 = tokens_to_parse2[i + 2].split("\\s+");
				String[] sub_tokens3 = tokens_to_parse2[i + 3].split("\\s+");
				page.outpatient_surgery = sub_tokens[0] + " " + sub_tokens[1] + " " + sub_tokens[2] + ": " + sub_tokens[3] + " " + sub_tokens[4] + " " + sub_tokens[5]
						+ "; " + sub_tokens2[0] + " " + sub_tokens2[1] + " " + sub_tokens2[2] + ": " + sub_tokens2[3] + " " + sub_tokens[4] + " " + sub_tokens[5]
						+ "; " + sub_tokens3[0] + ": " + sub_tokens3[1] + " " + sub_tokens3[2] + " " + sub_tokens3[3]; 
			} else if (tokens_to_parse2[i].contains("EMERGENCY ROOM")) {
				String[] sub_tokens = tokens_to_parse2[i + 2].split("\\s+");
				page.er_copay = sub_tokens[0] + " " + sub_tokens[1] + " " + sub_tokens[2];
			} else if (tokens_to_parse2[i].contains("URGENT CARE")) {
				String[] sub_tokens = tokens_to_parse2[i].split("\\s+");
				page.urgent_care_copay = sub_tokens[3] + " " + sub_tokens[4] + " " + sub_tokens[5];
			} else if (tokens_to_parse2[i].contains("INPATIENT HOSPITAL SERVICES")) {
				for (int q = i; q < (i + 6); q++) {
					if (tokens_to_parse2[q].contains("Facility")) {
						String[] sub_tokens = tokens_to_parse2[q].split("\\s+");
						page.in_patient_hospital = sub_tokens[0] + ": " + sub_tokens[1] + " " + sub_tokens[2] + " " + sub_tokens[3] + "; ";
					} else if (tokens_to_parse2[q].contains("Physician")) {
						String[] sub_tokens = tokens_to_parse2[q].split("\\s+");
						page.in_patient_hospital = page.in_patient_hospital + sub_tokens[0] + ": " + sub_tokens[1] + " " + sub_tokens[2] + " " + sub_tokens[3];
					}
				}
			}
		}
		for (int i = 0; i < tokens_to_parse5.length; i++) {
			if (tokens_to_parse5[i].contains("Retail Pharmacy")) { //starts from index of key word drugs or brand or formulary
				if (tokens_to_parse5[i + 1].contains("Formulary")) {
					String sub_string = tokens_to_parse5[i + 1].substring((tokens_to_parse5[i + 1].indexOf("Formulary") + 11), tokens_to_parse5[i + 1].length());
					String sub_string2 = tokens_to_parse5[i + 2].substring((tokens_to_parse5[i + 2].indexOf("Formulary") + 11), tokens_to_parse5[i + 2].length());
					String sub_string3 = tokens_to_parse5[i + 3].substring((tokens_to_parse5[i + 3].indexOf("Brand") + 6), tokens_to_parse5[i + 3].length());
					String sub_string4 = tokens_to_parse5[i + 4].substring((tokens_to_parse5[i + 4].indexOf("Drugs") + 6), tokens_to_parse5[i + 4].length());
					page.rx_copay = sub_string + "/" + sub_string2 + "/" + sub_string3 + "/" + sub_string4;
				} else {
					String sub_string = tokens_to_parse5[i + 1].substring((tokens_to_parse5[i + 1].indexOf("Drugs") + 6), tokens_to_parse5[i + 1].length());
					String sub_string2 = tokens_to_parse5[i + 2].substring((tokens_to_parse5[i + 2].indexOf("Brand") + 6), tokens_to_parse5[i + 2].length());
					String sub_string3 = tokens_to_parse5[i + 3].substring((tokens_to_parse5[i + 3].indexOf("Drugs") + 6), tokens_to_parse5[i + 3].length());
					String sub_string4 = tokens_to_parse5[i + 4].substring((tokens_to_parse5[i + 4].indexOf("Drugs") + 6), tokens_to_parse5[i + 4].length());
					page.rx_copay = sub_string + "/" + sub_string2 + "/" + sub_string3 + "/" + sub_string4;
				}
			} else if (tokens_to_parse5[i].contains("Mail Order Pharmacy")) {
				if (tokens_to_parse5[i + 2].contains("Formulary")) {
					String sub_string = tokens_to_parse5[i + 2].substring((tokens_to_parse5[i + 2].indexOf("Formulary") + 11), tokens_to_parse5[i + 2].length());
					String sub_string2 = tokens_to_parse5[i + 3].substring((tokens_to_parse5[i + 3].indexOf("Formulary") + 11), tokens_to_parse5[i + 3].length());
					String sub_string3 = tokens_to_parse5[i + 4].substring((tokens_to_parse5[i + 4].indexOf("Drugs") + 6), tokens_to_parse5[i + 4].length());
					page.rx_mail_copay = sub_string + "/" + sub_string2 + "/" + sub_string3;
				} else {
					String sub_string = tokens_to_parse5[i + 2].substring((tokens_to_parse5[i + 2].indexOf("Drugs") + 6), tokens_to_parse5[i + 2].length());
					String sub_string2 = tokens_to_parse5[i + 3].substring((tokens_to_parse5[i + 3].indexOf("Brand") + 6), tokens_to_parse5[i + 3].length());
					String sub_string3 = tokens_to_parse5[i + 4].substring((tokens_to_parse5[i + 4].indexOf("Drugs") + 6), tokens_to_parse5[i + 4].length());
					page.rx_mail_copay = sub_string + "/" + sub_string2 + "/" + sub_string3;
				}
			}
		}
		//page.printPage();
		return page;
	}

}
