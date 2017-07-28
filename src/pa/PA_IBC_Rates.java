package pa;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.Formatter;
import components.MedicalPage;



/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 * NEEDS TO BE FINISHED
 */
public class PA_IBC_Rates implements Parser {

	static String text;

	static ArrayList<Page> pages;

	static int numPages;

	static String start_date;

	static String end_date;

	public PA_IBC_Rates(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
	}

	public ArrayList<Page> parse(File file, String filename) throws IOException {
		pages = new ArrayList<Page>();
		PDFManager pdfManager = new components.PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
		numPages = pdfManager.getNumPages();
		
		
		String[] tokens = text.split(" |\n"); // Split pdf text by spaces and
												// new line chars
		int token_length = tokens.length;
		int carrier_id = 12; //CHANGE
		String product_name = "";
		String plan_code = "";
		String rating_area = "";
		String plan_name = "";
		String state = "PA";
		int age_count = 20;
		HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
		HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

		int index = 2;
		
		while(!tokens[index-2].equals("through")){
			index++;
		}
		
		while(!tokens[index].equals("Region:")){
			product_name += tokens[index] + " ";
			index++;
		}
				
		rating_area = tokens[index+1];
		
		while(!tokens[index-1].equals("20")){
			index++;
		}
		
		System.out.println(tokens[index]);
		System.out.println(Formatter.formatValue(tokens[index]));
		non_tobacco_dict.put("0-20",  Formatter.formatValue(tokens[index]));
		tobacco_dict.put("0-20",  Formatter.formatValue(tokens[index+1]));
		
		index+=2;
		int age = 0;
		String token = "";
		System.out.println(filename);
		while(!token.equals("64+")){
			try{
				token = tokens[index];
				age = Integer.parseInt(token);
				non_tobacco_dict.put(String.valueOf(age),  Formatter.formatValue(tokens[index+1]));
				tobacco_dict.put(String.valueOf(age),  Formatter.formatValue(tokens[index+2]));
				index+=3;
			}
			catch(NumberFormatException e){
				index++;
			}
		}
		
		non_tobacco_dict.put("64",  Formatter.formatValue(tokens[index]));
		non_tobacco_dict.put("65+",  Formatter.formatValue(tokens[index]));
		tobacco_dict.put("64",  Formatter.formatValue(tokens[index+1]));
		tobacco_dict.put("65+",  Formatter.formatValue(tokens[index+1]));
		
		
		MedicalPage page = new MedicalPage(carrier_id, "", start_date, end_date, product_name, filename,
				"", "", "", "", "", "", "", "", "", "", "", "", "", "",
				"", "", "", "", "", "", "", rating_area, "", state, 0,
				non_tobacco_dict, tobacco_dict);
		
		page.setTobaccoRates(true);
		
		pages.add(page);
		
		Formatter.printDictionary(tobacco_dict);

		// System.out.println("*****************************************************");
		return pages;

	}

}
