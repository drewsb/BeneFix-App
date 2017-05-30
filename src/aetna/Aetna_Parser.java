package aetna;

import java.io.File;

import java.io.IOException;
import java.util.HashMap;

import upmc.UPMC_Page;


/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class Aetna_Parser {
	
	static String text;
	
	static Aetna_Page[] pages;
	
	static int numPages;
	
	static String start_date;
	
	static String end_date;
	
	public Aetna_Parser(File file, String s_date, String e_date) throws IOException{
		start_date = s_date;
		end_date = e_date;
		components.PDFManager pdfManager = new components.PDFManager();
	    pdfManager.setFilePath(file.getAbsolutePath());
	    text = pdfManager.ToText();
	    numPages = pdfManager.getNumPages();
	    pages = new Aetna_Page[numPages];
	}
	
	public Aetna_Page[] parse(){
		int base_row = 0;  //Denotes starting index for each page in the array of tokens
		for(int page_index = 1; page_index <= numPages; page_index++){
			String rating_area = "";
			String plan_id = "";
			String plan_name = "";
			String state = "";
			HashMap<String,Double> age_dict = new HashMap<String,Double>();
			
			String[] tokens = text.split(" |\n");   //Split pdf text by spaces and new line chars
			rating_area = tokens[base_row+9];
			plan_id = tokens[base_row+13];
			
			int count = 16;							//Keeps track of last index of plan_row string
			String temp = tokens[base_row+count];
			plan_name = temp;
			while(!temp.equals("Age")){				//Iterate through entire plan name 
				count++;
				plan_name+=" " + temp;
				temp = tokens[base_row+count];
			}
			int start_index = base_row+count + 6;
			int end_index = base_row+count + 95;
			for(int i = start_index; i <= end_index; i+=2){  		//Construct hashmap mapping age to rate
				age_dict.put(tokens[i], Double.valueOf(tokens[i+1])); 
			}
			base_row+=count+159;    //Update base_row to beginning of next page
			state = tokens[base_row-1];
			Aetna_Page page = new Aetna_Page(start_date, end_date, page_index,rating_area,plan_id,plan_name, state, age_dict);
			pages[page_index-1] = page;
		}
		return pages;
	}

	
}
