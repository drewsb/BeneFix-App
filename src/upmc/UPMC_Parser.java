package upmc;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;


/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class UPMC_Parser {
	
	static String text;
	
	static UPMC_Page[] pages;
	
	static int numUMPC_Pages;
	
	static String start_date;
	
	static String end_date;
	
	public UPMC_Parser(File file, String s_date, String e_date) throws IOException{
		start_date = s_date;
		end_date = e_date;
	    components.PDFManager pdfManager = new components.PDFManager();
	    pdfManager.setFilePath(file.getAbsolutePath());
	    text = pdfManager.ToText();
	    numUMPC_Pages = pdfManager.getNumPages();
	    pages = new UPMC_Page[numUMPC_Pages];
	}
	
	public UPMC_Page[] parse(){
		String[] tokens = text.split(" |\n");   //Split pdf text by spaces and new line chars
		int base_row = 0;  //Denotes starting index for each page in the array of tokens
		int page_index = 1;
		int token_length = tokens.length;
		while(page_index <= numUMPC_Pages){
			int gap = 0;
			int age_count = 20;
			String rider = "";
			String plan_code = "";
			String rating_area = "";
			String plan_name = "";
			String state = "PA";
			HashMap<String,Double> non_tobacco_dict = new HashMap<String,Double>();		
			HashMap<String,Double> tobacco_dict = new HashMap<String,Double>();
			
			int temp_index = base_row+20;
			while(!tokens[temp_index].equals("UPMC")){
				temp_index++;
			}
			plan_code = tokens[temp_index-1];
			char[] charArr = tokens[temp_index].toCharArray();
			while(charArr.length < 2 || (charArr[0]!='1' && charArr[1]!='F')){
				plan_name += " ";
				plan_name += tokens[temp_index];
				temp_index++;
				charArr = tokens[temp_index].toCharArray();
				gap++;
			}
			while(!tokens[temp_index].equals("Rating")){
				rider += tokens[temp_index++] + " ";
				gap++;
			}
			rating_area = tokens[temp_index] + " " + tokens[temp_index+1] + " " + tokens[temp_index+2];
			gap += 7;
			int start_index = temp_index+3;
			int start_gap = start_index - base_row;
			int i = start_index;
			while(age_count <= 65){  		//Construct hashmap mapping age to rate
				if(tokens[i].equals("65")){
					non_tobacco_dict.put("65+", Double.valueOf(tokens[i+3])); 
					tobacco_dict.put("65+", Double.valueOf(tokens[i+4])); 
					break;
				}
				try {
					non_tobacco_dict.put(tokens[i], Double.valueOf(tokens[i+1])); 
					tobacco_dict.put(tokens[i], Double.valueOf(tokens[i+2])); 
					i+=gap;
					age_count++;
		        } catch (NumberFormatException e) {
		        	while(!tokens[i].equals(String.format("%d", age_count)) || tokens[i-1].equals("to")){
//			        	System.out.println(i);
//			        	System.out.println(tokens[i]);
			        	i++;
		        	}
		            //i+=start_gap - gap + 4;
		            page_index++;
		        }	
			}
			while(!tokens[i].equals("Effective")){
				i++;
				if(i==token_length){
					break;
				}
			}
			base_row=i;    //Update base_row to beginning of next page
			UPMC_Page page = new UPMC_Page(start_date, end_date, rider, plan_code, page_index, rating_area, plan_name, state, non_tobacco_dict, tobacco_dict);

			pages[page_index-1] = page;
			
			//System.out.println("*****************************************************");
			page_index++;
		}
		return pages;
		
	}

	
}
