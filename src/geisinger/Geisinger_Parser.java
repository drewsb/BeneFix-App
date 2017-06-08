package geisinger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.io.FileWriter;
import java.io.BufferedWriter;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import components.Page;

import geisinger.Geisinger_Page;

public class Geisinger_Parser {
	
	int page_counter = 0;

	static String file_name = "pdf.txt";

	static String text; 

	static ArrayList<Page> products = new ArrayList<Page>();

	static String[] pdfPagesText;

	static int numPages;

	static String start_date;

	static String end_date;

	static ArrayList<ArrayList<String>> tokenPages;

	static String start_page_string, end_page_string;

	static Integer start_page, end_page;

	static components.PDFManager pdfManager;

	public Geisinger_Parser() throws IOException {}

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

	public ArrayList<Page> parse(File file, String s_date, String e_date, String s_page, String e_page) throws IOException {

		// set variables from FileChooser.java
		start_date = s_date;
		end_date = e_date;
		start_page_string = s_page;
		end_page_string = e_page;
		start_page = Integer.parseInt(start_page_string);
		end_page = Integer.parseInt(end_page_string);
		tokenPages = new ArrayList<ArrayList<String>>();

		// variables for Page creation
		String product = "";
		Integer carrier_id = 13;
		String state = "PA";

		// create new PDFmanager object
		pdfManager = new components.PDFManager();
		PDDocument document = PDDocument.load(file);

		// split PDF, rates are every other page for Geisinger
		Splitter splitter = new Splitter();
		List<PDDocument> pages = splitter.split(document);
		ArrayList<PDDocument> pages_arraylist = new ArrayList<PDDocument>(pages);

		// convert rate pages to text in page range, add to array list
		for (int i = start_page; i < (end_page + 1); i++) {
			text = pdfManager.ToText(pages_arraylist.get(i));
			System.out.println(text);
			String lines[] = text.split("\n"); // split page into strings
			
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
					System.out.println(lines[x]);
					System.out.println(lines[x + 1]);
					System.out.println(lines[x + 2]);
					for (int q = 0; q < 3; q++) {
						for (int p = 0; p < ra2.length; p++) {
							if (lines[x + q].contains(ra2[p])) {
								rating2 = true;
								//System.out.println("rating 2 is true");
							}
						}
						for (int p = 0; p < ra3.length; p++) {
							if (lines[x + q].contains(ra3[p])) {
								rating3 = true;
								//System.out.println("rating 3 is true");
							}
						}
						for (int p = 0; p < ra5.length; p++) {
							if (lines[x + q].contains(ra5[p])) {
								rating5 = true;
								//System.out.println("rating 5 is true");
							}
						}	
						for (int p = 0; p < ra6.length; p++) {
							if (lines[x + q].contains(ra6[p])) {
								rating6 = true;
								//System.out.println("rating 6 is true");
							}
						}	
						for (int p = 0; p < ra7.length; p++) {
							if (lines[x + q].contains(ra7[p])) {
								rating7 = true;
								//System.out.println("rating 7 is true");
							}
						}
						for (int p = 0; p < ra9.length; p++) {
							if (lines[x + q].contains(ra9[p])) {
								rating9 = true;
								//System.out.println("rating 9 is true");
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
					token_list.set(0, (token_list.get(0) + " " + token_list.get(1) + " " + token_list.get(2)));
					ListIterator<String> it = token_list.listIterator(0);
					System.out.println(it.next());
					System.out.println(it.next());
					System.out.println(it.next());
					if (rating2 == true) {
						Double non_2 = Double.parseDouble(formatString(it.next()));
						Double tob_2 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict2.put(token_list.get(0), non_2); //format string to get rid of any commas
						tobacco_dict2.put(token_list.get(0), tob_2);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating3 == true) {
						Double non_3 = Double.parseDouble(formatString(it.next()));
						Double tob_3 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict3.put(token_list.get(0), non_3);
						tobacco_dict3.put(token_list.get(0), tob_3);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating5 == true) {
						Double non_5 = Double.parseDouble(formatString(it.next()));
						Double tob_5 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict5.put(token_list.get(0), non_5);
						tobacco_dict5.put(token_list.get(0), tob_5);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating6 == true) {
						Double non_6 = Double.parseDouble(formatString(it.next()));
						Double tob_6 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict6.put(token_list.get(0), non_6);
						tobacco_dict6.put(token_list.get(0), tob_6);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating7 == true) {
						Double non_7 = Double.parseDouble(formatString(it.next()));
						Double tob_7 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict7.put(token_list.get(0), non_7);
						tobacco_dict7.put(token_list.get(0), tob_7);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating9 == true) {
						Double non_9 = Double.parseDouble(formatString(it.next()));
						Double tob_9 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict9.put(token_list.get(0), non_9);
						tobacco_dict9.put(token_list.get(0), tob_9);
//						System.out.println(it.next());
//						System.out.println(it.next());
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
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating3 == true) {
						Double non_3 = Double.parseDouble(formatString(it.next()));
						Double tob_3 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict3.put(token_list.get(0), non_3);
						tobacco_dict3.put(token_list.get(0), tob_3);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating5 == true) {
						Double non_5 = Double.parseDouble(formatString(it.next()));
						Double tob_5 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict5.put(token_list.get(0), non_5);
						tobacco_dict5.put(token_list.get(0), tob_5);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating6 == true) {
						Double non_6 = Double.parseDouble(formatString(it.next()));
						Double tob_6 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict6.put(token_list.get(0), non_6);
						tobacco_dict6.put(token_list.get(0), tob_6);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating7 == true) {
						Double non_7 = Double.parseDouble(formatString(it.next()));
						Double tob_7 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict7.put(token_list.get(0), non_7);
						tobacco_dict7.put(token_list.get(0), tob_7);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
					if (rating9 == true) {
						Double non_9 = Double.parseDouble(formatString(it.next()));
						Double tob_9 = Double.parseDouble(formatString(it.next()));
						non_tobacco_dict9.put(token_list.get(0), non_9);
						tobacco_dict9.put(token_list.get(0), tob_9);
//						System.out.println(it.next());
//						System.out.println(it.next());
					}
				}
			}
			
			if (rating2 == true) {
				System.out.println("adding page");
				page_counter++;
				products.add(new Page(carrier_id, "", start_date, end_date,
						product, "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "2", "", product, state, i,
						non_tobacco_dict2, tobacco_dict2));
			}
			if (rating3 == true) {
				System.out.println("adding page");
				page_counter++;
				products.add(new Page(carrier_id, "", start_date, end_date,
						product, "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "3", "", product, state, i,
						non_tobacco_dict3, tobacco_dict3));
			}
			if (rating5 == true) {
				System.out.println("adding page");
				page_counter++;
				products.add(new Page(carrier_id, "", start_date, end_date,
						product, "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "5", "", product, state, i,
						non_tobacco_dict5, tobacco_dict5));
			}
			if (rating6 == true) {
				System.out.println("adding page");
				page_counter++;
				products.add(new Page(carrier_id, "", start_date, end_date,
						product, "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "6", "", product, state, i,
						non_tobacco_dict6, tobacco_dict6));
			}
			if (rating7 == true) {
				System.out.println("adding page");
				page_counter++;
				products.add(new Page(carrier_id, "", start_date, end_date,
						product, "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "", "", "", "7", "", product, state, i,
						non_tobacco_dict7, tobacco_dict7));
			}
			if (rating9 == true) {
				System.out.println("adding page");
				page_counter++;
				products.add(new Page(carrier_id, "", start_date, end_date,
						product, "",
						"", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"1", "", "", "", "", "", "", "9", "", product, state, i,
						non_tobacco_dict9, tobacco_dict9));
			}
			// skip benefits page
			i++; 
		}
		System.out.println(page_counter);
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
