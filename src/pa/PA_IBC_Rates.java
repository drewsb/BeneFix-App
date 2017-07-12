package pa;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.Page;
import components.Parser;
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
		String product_name = "";
		String plan_code = "";
		String rating_area = "";
		String plan_name = "";
		String state = "PA";
		int age_count = 20;
		HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
		HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

		int temp_index = 27;
		while(!tokens[temp_index].contains("$")){
			product_name += tokens[temp_index];
			temp_index++;
		}
		temp_index+=plan_name.length() + 3;
		while(!tokens[temp_index].equals("Age")){
			plan_name+=tokens[temp_index];
			temp_index++;
		}
		temp_index+=10;
		non_tobacco_dict.put("0-20",  Double.valueOf(tokens[temp_index]+1));
		non_tobacco_dict.put("0-20",  Double.valueOf(tokens[temp_index]+2));
		temp_index+=13;
		while(tokens[temp_index].equals("Age")){
			age_count++;
			non_tobacco_dict.put(tokens[temp_index],  Double.valueOf(tokens[temp_index]+1));
			non_tobacco_dict.put(tokens[temp_index],  Double.valueOf(tokens[temp_index]+2));
			temp_index+=3;
		}
		age_count++;
		temp_index+=8;
		while(age_count < 65){
			non_tobacco_dict.put(tokens[temp_index],  Double.valueOf(tokens[temp_index]+1));
			non_tobacco_dict.put(tokens[temp_index],  Double.valueOf(tokens[temp_index]+2));
			temp_index+=3;
		}
		
//		Page page = new Page(carrier_id, plan_id, start_date, end_date, product, "", deductible, "", "", "",
//				coinsurance, "", "", "", "", "", "", oop_maximum, "", "", "", "", "", "", "", "", "", rating_area, "",
//				plan_name, state, page_index, non_tobacco_dict, tobacco_dict);

		// System.out.println("*****************************************************");
		return null;

	}

}
