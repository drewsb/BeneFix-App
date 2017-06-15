package pa;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.Page;


/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown
 */
public class PA_Aetna_Rates {

	static String text;

	static ArrayList<Page> pages;

	static int numPages;

	static String start_date;

	static String end_date;

	public PA_Aetna_Rates(File file, String s_date, String e_date) throws IOException{
		start_date = s_date;
		end_date = e_date;
		components.PDFManager pdfManager = new components.PDFManager();
	    pdfManager.setFilePath(file.getAbsolutePath());
	    text = pdfManager.ToText();
	    numPages = pdfManager.getNumPages();
	    pages = new ArrayList<Page>();
	}

	public ArrayList<Page> parse(){
		int base_row = 0;  //Denotes starting index for each page in the array of tokens
		for(int page_index = 1; page_index <= numPages; page_index++){
			String rating_area = "";
			String plan_id = "";
			String plan_name = "";
			String state = "";
			int carrier_id = 12;
			String product_name = "";
			String plan_code = "";

			HashMap<String,Double> non_tob_dict = new HashMap<String,Double>();
			HashMap<String,Double> tob_dict = new HashMap<String,Double>();

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
				non_tob_dict.put(tokens[i], Double.valueOf(tokens[i+1]));
			}
			base_row+=count+159;    //Update base_row to beginning of next page
			state = tokens[base_row-1];
			Page page = new Page(carrier_id, "", start_date, end_date, product_name, "", "", "", "", "",
					"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", rating_area, "",
					state, 0, non_tob_dict, tob_dict);
			pages.add(page);
		}
		return pages;
	}


}