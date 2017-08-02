package pa;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import components.MedicalPage;
import components.PDFManager;
import components.Page;
import components.Parser;

public class PA_Geisinger_Rates implements Parser {
	
	int temp_counter = 0;
	
	int page_counter = 0;

	static String file_name = "pdf.txt";
	static String text;
	static ArrayList<Page> products;
	static String[] pdfPagesText;
	static int numPages;
	static String start_date;
	static String end_date;
	static ArrayList<ArrayList<String>> tokenPages;
	static String start_page_string, end_page_string;
	static Integer start_page, end_page;

	static PDFManager pdfManager;

	public PA_Geisinger_Rates(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;	
		products = new ArrayList<Page>();
	}

	static Integer number_of_age_bands = 46;

	// rating area dictionaries
	static String[] ra2 = { "Potter", "Cameron" };
	static String[] ra3 = { "Clinton", "Lycoming", "Luzerne", "Monroe", "Wayne", "Lackawanna", "Wyoming", "Susquehanna",
			"Tioga", "Bradford", "Sullivan", "Carbon", "Pike" };
	static String[] ra5 = { "Jefferson", "Clearfield", "Cambria", "Blair", "Huntingdon", "Somerset" };
	static String[] ra6 = { "Centre", "Mifflin", "Union", "Snyder", "Northumberland", "Montour", "Columbia",
			"Schuylkill", "Lehigh", "Northampton" };
	static String[] ra7 = { "Adams", "Berks", "York", "Lancaster" };
	static String[] ra9 = { "Juniata", "Perry", "Dauphin", "Cumberland", "Lebanon", "Fulton" };

	public ArrayList<Page> parse(File file, String filename) throws IOException {

		// set variables from FileChooser.java
		tokenPages = new ArrayList<ArrayList<String>>();

		// variables for Page creation
		String product = "";
		Integer carrier_id = 13;
		String state = "PA";

		// create new PDFmanager object
		pdfManager = new PDFManager();
		PDDocument document = PDDocument.load(file);

		// split PDF, rates are every other page for Geisinger
		Splitter splitter = new Splitter();
		List<PDDocument> pages = splitter.split(document);
		ArrayList<PDDocument> pages_arraylist = new ArrayList<PDDocument>(pages);
		
		//get start page
//		try {
//			do {
//				text = pdfManager.ToText(pages_arraylist.get(page_counter)); 
//				page_counter++;
//			} while (!text.toLowerCase().contains("benefits effective") && !text.toLowerCase().contains("rates effective"));
//			start_page = page_counter;
//		} catch (NullPointerException e) {
//			System.out.println(e.getStackTrace());
//		}
		
		//get end page
//		try {
//			do {
//				text = pdfManager.ToText(pages_arraylist.get(page_counter)); 
//				page_counter++;
//			} while (text.toLowerCase().contains("benefits effective") && text.toLowerCase().contains("rates effective"));
//			end_page = page_counter;
//		} catch (NullPointerException e) {
//			System.out.println(e.getStackTrace());
//		}

		// convert rate pages to text in page range, add to array list
		for (int i = 25; i < (97 + 1); i++) {
			text = pdfManager.ToText(pages_arraylist.get(i));
			String lines[] = text.split("\n"); // split page into strings
			
			for (int omg = 0; omg < lines.length; omg++) {
				System.out.println(lines[omg]);
			}
			// rating area bools
			boolean rating2 = true;
			boolean rating3 = true;
			boolean rating5 = true;
			boolean rating6 = true;
			boolean rating7 = true;
			boolean rating9 = true;
			
			// create hash maps
			HashMap<String, Double> tobacco_dict2 = new HashMap<String, Double>();
			HashMap<String, Double> non_tobacco_dict2 = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict3 = new HashMap<String, Double>();
			HashMap<String, Double> non_tobacco_dict3 = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict5 = new HashMap<String, Double>();
			HashMap<String, Double> non_tobacco_dict5 = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict6 = new HashMap<String, Double>();
			HashMap<String, Double> non_tobacco_dict6 = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict7 = new HashMap<String, Double>();
			HashMap<String, Double> non_tobacco_dict7 = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict9 = new HashMap<String, Double>();
			HashMap<String, Double> non_tobacco_dict9 = new HashMap<String, Double>();
			

			// get plan name and set rating areas
			for (int x = number_of_age_bands; x < lines.length; x++) {
				if (lines[x].contains("geisinger") || lines[x].contains("Geisinger")) {
					ArrayList<String> temp_arraylist = new ArrayList<String>(formatPlanName(lines[x]));
					product = temp_arraylist.get(1);
				}
				if (lines[x].contains("counties")) {
					rating2 = false;
					rating3 = false;
					rating5 = false;
					rating6 = false;
					rating7 = false;
					rating9 = false;
					for (int q = 0; q < 3; q++) {
						for (int p = 0; p < ra2.length; p++) {
							if (lines[x + q].contains(ra2[p])) {
								rating2 = true;
							}
						}
						for (int p = 0; p < ra3.length; p++) {
							if (lines[x + q].contains(ra3[p])) {
								rating3 = true;
							}
						}
						for (int p = 0; p < ra5.length; p++) {
							if (lines[x + q].contains(ra5[p])) {
								rating5 = true;
							}
						}	
						for (int p = 0; p < ra6.length; p++) {
							if (lines[x + q].contains(ra6[p])) {
								rating6 = true;
							}
						}	
						for (int p = 0; p < ra7.length; p++) {
							if (lines[x + q].contains(ra7[p])) {
								rating7 = true;
							}
						}
						for (int p = 0; p < ra9.length; p++) {
							if (lines[x + q].contains(ra9[p])) {
								rating9 = true;
							}
						}
					}
				}
			}

			// get tobacco and non tobacco rates for each rating area
			for (int k = 0; k < number_of_age_bands; k++) {
				String[] tokens = lines[k].split("\\s+"); // split current string into tokens
				ArrayList<String> token_list = new ArrayList<String>(Arrays.asList(tokens));
				if (token_list.get(0).contains("65")) {
					token_list.set(0, token_list.get(0));
					ListIterator<String> it = token_list.listIterator(0);
					System.out.println(it.next());
					System.out.println(it.next());
					System.out.println(it.next());
					if (rating2 == true) {
						Double non_2 = Double.parseDouble(formatString(it.next()));
						Double tob_2 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict2.put("65+", non_2); //format string to get rid of any commas
						tobacco_dict2.put("65+", tob_2);
					}
					if (rating3 == true) {
						Double non_3 = Double.parseDouble(formatString(it.next()));
						Double tob_3 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict3.put("65+", non_3);
						tobacco_dict3.put("65+", tob_3);
					}
					if (rating5 == true) {
						Double non_5 = Double.parseDouble(formatString(it.next()));
						Double tob_5 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict5.put("65+", non_5);
						tobacco_dict5.put("65+", tob_5);
					}
					if (rating6 == true) {
						Double non_6 = Double.parseDouble(formatString(it.next()));
						Double tob_6 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict6.put("65+", non_6);
						tobacco_dict6.put("65+", tob_6);
					}
					if (rating7 == true) {
						Double non_7 = Double.parseDouble(formatString(it.next()));
						Double tob_7 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict7.put("65+", non_7);
						tobacco_dict7.put("65+", tob_7);
					}
					if (rating9 == true) {
						Double non_9 = Double.parseDouble(formatString(it.next()));
						Double tob_9 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict9.put("65+", non_9);
						tobacco_dict9.put("65+", tob_9);
					}
				} 
				else if (!token_list.get(0).contains("65")) {
					ListIterator<String> it = token_list.listIterator(0);
					System.out.println(it.next());
					if (rating2 == true) {
						Double non_2 = Double.parseDouble(formatString(it.next()));
						Double tob_2 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict2.put(token_list.get(0), non_2); //format string to get rid of any commas
						tobacco_dict2.put(token_list.get(0), tob_2);
					}
					if (rating3 == true) {
						Double non_3 = Double.parseDouble(formatString(it.next()));
						Double tob_3 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict3.put(token_list.get(0), non_3);
						tobacco_dict3.put(token_list.get(0), tob_3);
					}
					if (rating5 == true) {
						Double non_5 = Double.parseDouble(formatString(it.next()));
						Double tob_5 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict5.put(token_list.get(0), non_5);
						tobacco_dict5.put(token_list.get(0), tob_5);
					}
					if (rating6 == true) {
						Double non_6 = Double.parseDouble(formatString(it.next()));
						Double tob_6 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict6.put(token_list.get(0), non_6);
						tobacco_dict6.put(token_list.get(0), tob_6);
					}
					if (rating7 == true) {
						Double non_7 = Double.parseDouble(formatString(it.next()));
						Double tob_7 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict7.put(token_list.get(0), non_7);
						tobacco_dict7.put(token_list.get(0), tob_7);
					}
					if (rating9 == true) {
						Double non_9 = Double.parseDouble(formatString(it.next()));
						Double tob_9 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict9.put(token_list.get(0), non_9);
						tobacco_dict9.put(token_list.get(0), tob_9);
					}
				}
			}
			
			if (rating2 == true) {
				products.add(new MedicalPage(carrier_id, "", start_date, end_date, 
						product, "", 
						"", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "2",
						"", state, i, non_tobacco_dict2, tobacco_dict2));
			}
			if (rating3 == true) {
				products.add(new MedicalPage(carrier_id, "", start_date, end_date, 
						product, "", 
						"", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "3",
						"", state, i, non_tobacco_dict3, tobacco_dict3));
			}
			if (rating5 == true) {
				products.add(new MedicalPage(carrier_id, "", start_date, end_date, 
						product, "", 
						"", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "5",
						"", state, i, non_tobacco_dict5, tobacco_dict5));
			}
			if (rating6 == true) {
				products.add(new MedicalPage(carrier_id, "", start_date, end_date, 
						product, "", 
						"", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "6",
						"", state, i, non_tobacco_dict6, tobacco_dict6));
			}
			if (rating7 == true) {
				products.add(new MedicalPage(carrier_id, "", start_date, end_date, 
						product, "", 
						"", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "7",
						"", state, i, non_tobacco_dict7, tobacco_dict7));
			}
			if (rating9 == true) {
				products.add(new MedicalPage(carrier_id, "", start_date, end_date, 
						product, "", 
						"", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "", "", "", "", "", "9",
						"", state, i, non_tobacco_dict9, tobacco_dict9));
			}
			
			i++; // skip benefits page
		}
		//products.get(temp_counter).printPage();
		temp_counter++;
		return products;
	}

	public String formatString(String input) {
		String output = "";
		if (input.contains(",")) {
			int index = input.indexOf(",");
			output = input.substring(0, index) + input.substring(index + 1, input.length());
			return output;
		} else {
			return input;
		}

	}

	// get carrier and plan name/product name
	public ArrayList<String> formatPlanName(String input) {
		ArrayList<String> output = new ArrayList<String>();
		String string1 = "";
		String string2 = "";
		if (input.contains("Geisinger") && input.contains("Platinum")) {
			// int medal_index = input.indexOf("Platinum");
			int carrier_index = input.indexOf("Geisinger");
			string1 = input.substring(carrier_index, carrier_index);
			string2 = input.substring(carrier_index, input.length());
			output.add(string1);
			output.add(string2);
			return output;
		} else if (input.contains("Geisinger") && input.contains("Gold")) {
			// int medal_index = input.indexOf("Gold");
			int carrier_index = input.indexOf("Geisinger");
			string1 = input.substring(carrier_index, carrier_index);
			string2 = input.substring(carrier_index, input.length());
			output.add(string1);
			output.add(string2);
			return output;
		} else if (input.contains("Geisinger") && input.contains("Silver")) {
			// int medal_index = input.indexOf("Silver");
			int carrier_index = input.indexOf("Geisinger");
			string1 = input.substring(carrier_index, carrier_index);
			string2 = input.substring(carrier_index, input.length());
			output.add(string1);
			output.add(string2);
			return output;
		} else if (input.contains("Geisinger") && input.contains("Bronze")) {
			// int medal_index = input.indexOf("Bronze");
			int carrier_index = input.indexOf("Geisinger");
			string1 = input.substring(carrier_index, carrier_index);
			string2 = input.substring(carrier_index, input.length());
			output.add(string1);
			output.add(string2);
			return output;
		} else {
			return output;
		}
	}
}
