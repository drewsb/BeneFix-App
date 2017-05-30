package components;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class IBC_Parser {

	static String text;

	static Page[] pages;

	static int numPages;

	static String start_date;

	static String end_date;

	public IBC_Parser(File file, String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
		components.PDFManager pdfManager = new components.PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
		numPages = pdfManager.getNumPages();
		pages = new Page[numPages];
	}

	public Page parse() {
		String[] tokens = text.split(" |\n"); // Split pdf text by spaces and
											// new line chars
		for(String s : tokens){
			System.out.println(s);
		}
		int carrier_id = 12;
		int token_length = tokens.length;
		String product_name = "";
		String plan_code = "";
		String rating_area = "";
		String plan_name = "";
		String state = "PA";
		int age_count = 20;
		HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
		HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

		int product_token_length = 0;
		int temp_index = 30;
		while(!tokens[temp_index].contains("Region:")){
			product_name += tokens[temp_index] + " ";
			product_token_length++;
			temp_index++;
		}
		rating_area = tokens[temp_index+1];
		temp_index+= product_token_length + 1;
		while(!tokens[temp_index].equals("Age")){
			plan_name+=tokens[temp_index] + " ";
			temp_index++;
		}
		temp_index+=10;
		non_tobacco_dict.put("0-20",  valueToDouble(tokens[temp_index+1]));
		non_tobacco_dict.put("0-20",  valueToDouble(tokens[temp_index+2]));
		temp_index+=3;
		while(!tokens[temp_index].equals("Age")){
			age_count++;
			System.out.println(temp_index);
			non_tobacco_dict.put(tokens[temp_index],  valueToDouble(tokens[temp_index+1]));
			tobacco_dict.put(tokens[temp_index],  valueToDouble(tokens[temp_index+2]));
			temp_index+=3;
		}
		age_count++;
		temp_index+=8;
		while(age_count < 65){
			non_tobacco_dict.put(tokens[temp_index],  valueToDouble(tokens[temp_index+1]));
			tobacco_dict.put(tokens[temp_index],  valueToDouble(tokens[temp_index+2]));
			temp_index+=3;
			age_count++;
		}
		
		Page page = new Page(carrier_id, "", start_date, end_date, plan_name, "", "", "", "", "",
				"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", rating_area, "",
				product_name, state, 0, non_tobacco_dict, tobacco_dict);
		
		page.printPage();

		// System.out.println("*****************************************************");
		return page;

	}
	
	
	public Double valueToDouble(String input){
		if(input.contains(",")){
			int index = input.indexOf(",");
			input = input.substring(0,index) + input.substring(index+1,input.length());
		}
		return Double.parseDouble(input.substring(1, input.length()));
	}

}
