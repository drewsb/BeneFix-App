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
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import components.MedicalPage;
import components.PDFManager;
import components.Page;
import components.Parser;

public class PA_Geisinger_Benefits implements Parser {
	
	String start_date;
	String end_date;
	int temp_counter = 0;
	
	int page_counter = 0;
	
	//class variables
	static String file_name = "pdf.txt";
	static String text;
	static String[] pdfPagesText;
	static ArrayList<Page> products;
 	static int numPages;
	static ArrayList<ArrayList<String>> tokenPages;
	static String start_page_string, end_page_string;
	static Integer start_page, end_page;
	static PDFManager pdfManager;
	
	
	// rating area dictionaries
		static String[] ra2 = { "Potter", "Cameron" };
		static String[] ra3 = { "Clinton", "Lycoming", "Luzerne", "Monroe", "Wayne", "Lackawanna", "Wyoming", "Susquehanna",
				"Tioga", "Bradford", "Sullivan", "Carbon", "Pike" };
		static String[] ra5 = { "Jefferson", "Clearfield", "Cambria", "Blair", "Huntingdon", "Somerset" };
		static String[] ra6 = { "Centre", "Mifflin", "Union", "Snyder", "Northumberland", "Montour", "Columbia",
				"Schuylkill", "Lehigh", "Northampton" };
		static String[] ra7 = { "Adams", "Berks", "York", "Lancaster" };
		static String[] ra9 = { "Juniata", "Perry", "Dauphin", "Cumberland", "Lebanon", "Fulton" };
	
	public PA_Geisinger_Benefits(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
		products = new ArrayList<Page>();
	}

	public ArrayList<Page> parse(File file, String filename) throws InvalidPasswordException, IOException {
		
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
		for (int i = 24; i < (96 + 1); i++) {
			products.add(handler(pages_arraylist, i)); //made handler function because of weird issues going from PDF to text
			i++;
		}	
		
		return products;
	}
	
	public MedicalPage handler(ArrayList<PDDocument> pages_arraylist, int i) throws IOException {
		text = pdfManager.ToText(pages_arraylist.get(i));
		String lines[] = text.split("\n"); // split page into strings
		
		//CREATE MEDICAL PAGE, otherwise gets overwritten
		MedicalPage page = new MedicalPage();
		
		// rating area bools
		boolean rating2 = true;
		boolean rating3 = true;
		boolean rating5 = true;
		boolean rating6 = true;
		boolean rating7 = true;
		boolean rating9 = true;
		
		//rx tiers
		String tier1 = "";
		String tier2 = "";
		String tier3 = "";
		String tier4 = "";
		String tier5 = "";
		String tier6 = "";
		
		//System.out.println(text);
		
		//handle all the different key phrases/regex
		for (int x = 0; x < lines.length; x++) {
			if (lines[x].toLowerCase().contains("mail order rx")) {
				//System.out.println(lines[x]);	
				String[] tokens = lines[x].split("\\s+");
				if (tokens[4].contains("/")) {
					page.rx_mail_copay = tokens[3] + "/" + tokens[5];
				} else if (tokens[4].contains("after")) {
					page.rx_mail_copay = tokens[3] + " " + tokens[4] + " " + tokens[5];
				} else if (tokens[4].contains("copayment") || tokens[4].contains("copay")) {
					page.rx_mail_copay = tokens[3] + " " + tokens[4];
				} else {
					page.rx_mail_copay = tokens[3];
				}
			} else if (lines[x].toLowerCase().contains("tier 1")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[7].contains("coinsurance")) {
					tier1 = tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9] + " " + tokens[10];
				} else if (tokens[7].contains("after")) {
					tier1 = tokens[6] + " " + tokens[7] + " " + tokens[8];
				} else {
					tier1 = tokens[6];
				}
			} else if (lines[x].toLowerCase().contains("tier 2")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[7].contains("coinsurance")) {
					tier2 = tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9] + " " + tokens[10];
				} else if (tokens[7].contains("after")) {
					tier2 = tokens[6] + " " + tokens[7] + " " + tokens[8];
				} else {
					tier2 = tokens[6];
				}
			} else if (lines[x].toLowerCase().contains("tier 3")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[7].contains("coinsurance")) {
					tier3 = tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9] + " " + tokens[10];
				} else if (tokens[7].contains("after")) {
					tier3 = tokens[6] + " " + tokens[7] + " " + tokens[8];
				} else {
					tier3 = tokens[6];
				}
			} else if (lines[x].toLowerCase().contains("tier 4")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[7].contains("coinsurance")) {
					tier4 = tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9] + " " + tokens[10];
				} else if (tokens[7].contains("after")) {
					tier4 = tokens[6] + " " + tokens[7] + " " + tokens[8];
				} else {
					tier4 = tokens[6];
				}
			} else if (lines[x].toLowerCase().contains("tier 5")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[6].contains("coinsurance")) {
					tier5 = tokens[5] + " " + tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9];
				} else if (tokens[6].contains("after")) {
					tier5 = tokens[5] + " " + tokens[6] + " " + tokens[7];
				} else if (lines[x].toLowerCase().contains("moop")) {
					tier5 = tokens[5] + " " + tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9] + " " + tokens[10];
				} else {
					tier5 = tokens[6];
				}
			} else if (lines[x].toLowerCase().contains("tier 6")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[6].contains("coinsurance")) {
					tier6 = tokens[5] + " " + tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9];
				} else if (tokens[6].contains("after")) {
					tier6 = tokens[5] + " " + tokens[6] + " " + tokens[7];
				} else {
					tier6 = tokens[5];
				}
			} else if (lines[x].toLowerCase().contains("medical ehb deductible")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[3].contains("/")) {
					String[] sub_tokens = tokens[3].split("/");
					page.deductible_indiv = sub_tokens[0].replace(",", "");
					page.deductible_family = sub_tokens[1].replace(",", "");
					if (tokens[4].contains("/")) {
						String[] oon_sub_tokens = tokens[4].split("/");
						page.oon_deductible_indiv = oon_sub_tokens[0].replace(",", "");
						page.oon_deductible_family = oon_sub_tokens[1].replace(",", "");
					} else {
						page.oon_deductible_indiv = tokens[4].replace(",", "");
						page.oon_deductible_family = tokens[6].replace(",", "");
					}	
				} else {
					page.deductible_indiv = tokens[3].replace(",", "");
					page.deductible_family = tokens[5].replace(",", "");
					if (tokens[6].contains("/")) {
						String[] oon_sub_tokens = tokens[6].split("/");
						page.oon_deductible_indiv = oon_sub_tokens[0].replace(",", "");
						page.oon_deductible_family = oon_sub_tokens[1].replace(",", "");
					} else if (tokens[6].contains("Limited")) {
						page.oon_deductible_indiv = "N/A";
						page.oon_deductible_family = "N/A";
					} else {
						page.oon_deductible_indiv = tokens[6].replace(",", "");
						page.oon_deductible_family = tokens[8].replace(",", "");
					}
				}
			} else if (lines[x].toLowerCase().contains("coinsurance")) {
				String[] tokens = lines[x].split("\\s+");
				page.coinsurance = tokens[1];
			} else if (lines[x].toLowerCase().contains("maximum out of pocket")) {
				//System.out.println(lines[x]);
				//System.out.println(lines[x + 1]); //need subsequent line, it holds benefit values
				String[] tokens = lines[x + 1].split("\\s+");
				if (tokens[3].contains("/")) {
					String[] sub_tokens = tokens[3].split("/");
					page.oop_max_indiv = sub_tokens[0].replace(",", "");
					page.oop_max_family = sub_tokens[1].replace(",", "");
					if (tokens[4].contains("/")) {
						String[] oon_sub_tokens = tokens[4].split("/");
						page.oon_oop_max_indiv = oon_sub_tokens[0].replace(",", "");
						page.oon_oop_max_family = oon_sub_tokens[1].replace(",", "");
					} else {
						page.oon_oop_max_indiv = tokens[4].replace(",", "");
						page.oon_oop_max_family = tokens[6].replace(",", "");
					}
				} else {
					page.oop_max_indiv = tokens[3].replace(",", "");
					page.oop_max_family = tokens[5].replace(",", "");
					if (tokens[6].contains("/")) {
						String[] oon_sub_tokens = tokens[6].split("/");
						page.oon_oop_max_indiv = oon_sub_tokens[0].replace(",", "");
						page.oon_oop_max_family = oon_sub_tokens[1].replace(",", "");
					} else if (tokens[6].contains("Limited")) {
						page.oon_oop_max_indiv = "N/A";
						page.oon_oop_max_family = "N/A";
					} else {
						page.oon_oop_max_indiv = tokens[6].replace(",", "");
						if (tokens.length == 9) {
							page.oon_oop_max_family = tokens[8].replace(",", "");
						} else if (tokens[7].contains("/")) {
							page.oon_oop_max_family = tokens[7].replace("/", "").replace(",", "");
						} else {
							page.oon_oop_max_family = tokens[7].replace(",", "");
						}
					}
				}
			} else if (lines[x].toLowerCase().contains("primary care visit")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[10].contains("/")) {
					page.dr_visit_copay = tokens[9] + "/" + tokens[11];
				} else if (tokens[10].contains("after")) {
					page.dr_visit_copay = tokens[9] + " " + tokens[10] + " " + tokens[11];
				} else {
					page.dr_visit_copay = tokens[9];
				}
			} else if (lines[x].toLowerCase().contains("specialist")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[5].contains("/")) {
					page.specialist_visit_copay = tokens[4] + "/" + tokens[6];
				} else if (tokens[5].contains("after")) {
					page.specialist_visit_copay = tokens[4] + " " + tokens[5] + " " + tokens[6];
				} else {
					page.specialist_visit_copay = tokens[4];
				}
			} else if (lines[x].toLowerCase().contains("emergency room")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[4].contains("/")) {
					page.er_copay = tokens[3] + "/" + tokens[5];
				} else if (tokens[4].contains("after")) {
					page.er_copay = tokens[3] + " " + tokens[4] + " " + tokens[5];
				} else {
					page.er_copay = tokens[3];
				}
			} else if (lines[x].toLowerCase().contains("urgent care")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[6].contains("/")) {
					page.urgent_care_copay = tokens[5] + "/" + tokens[7];
				} else if (tokens[6].contains("after")) {
					page.urgent_care_copay = tokens[5] + " " + tokens[6] + " " + tokens[7];
				} else {
					page.urgent_care_copay = tokens[5];
				}
			} else if (lines[x].toLowerCase().contains("inpatient hospital")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[7].contains("per")) {
					page.in_patient_hospital = tokens[6] + " " + tokens[7] + " " + tokens[8] + " " + tokens[9] + " " + tokens[10];
				} else if (tokens[7].contains("after")) {
					page.in_patient_hospital = tokens[6] + " " + tokens[7] + " " + tokens[8];
				} else {
					page.in_patient_hospital = tokens[6];
				}
			} else if (lines[x].toLowerCase().contains("imaging")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[5].contains("/")) {
					page.outpatient_complex_imaging = tokens[4] + "/" + tokens[6];
					page.outpatient_diagnostic_x_ray = page.outpatient_complex_imaging;
				} else if (tokens[5].contains("after")) {
					page.outpatient_complex_imaging = tokens[4] + " " + tokens[5] + " " + tokens[6];
					page.outpatient_diagnostic_x_ray = page.outpatient_complex_imaging;
				} else {
					page.outpatient_complex_imaging = tokens[4];
					page.outpatient_diagnostic_x_ray = page.outpatient_complex_imaging;
				}
			} else if (lines[x].toLowerCase().contains("occupational")) {
				//System.out.println(lines[x]);
				//System.out.println(lines[x + 1]);
				String[] tokens = lines[x + 1].split("\\s+");
				if (tokens[3].contains("/")) {
					page.physical_occupational_therapy = tokens[2] + "/" + tokens[4];
				} else if (tokens[3].contains("after")) {
					page.physical_occupational_therapy = tokens[2] + " " + tokens[3] + " " + tokens[4];
				} else {
					page.physical_occupational_therapy = tokens[2];
				}
			} else if (lines[x].toLowerCase().contains("laboratory")) {
				//System.out.println(lines[x]);
				//System.out.println(lines[x + 1]);
				String[] tokens = lines[x + 1].split("\\s+");
				if (tokens[2].contains("/")) {
					page.outpatient_diagnostic_lab = tokens[1] + "/" + tokens[3];
				} else if (tokens[2].contains("after")) {
					page.outpatient_diagnostic_lab = tokens[1] + " " + tokens[2] + " " + tokens[3];
				} else {
					page.outpatient_diagnostic_lab = tokens[1];
				}
			} else if (lines[x].toLowerCase().contains("outpatient surgery")) {
				//System.out.println(lines[x]);
				String[] tokens = lines[x].split("\\s+");
				if (tokens[5].contains("/")) {
					page.outpatient_surgery = tokens[4] + "/" + tokens[6];
				} else if (tokens[5].contains("after")) {
					page.outpatient_surgery = tokens[4] + " " + tokens[5] + " " + tokens[6];
				} else {
					page.outpatient_surgery = tokens[4];
				}
			} else if (lines[x].toLowerCase().contains("geisinger marketplace")) {
				//System.out.println(lines[x]);
				page.product_name = lines[x].replace("Geisinger ", "");
			}
		}
		page.rx_copay = tier1 + "/" + tier2 + "/" + tier3 + "/" + tier4 + "/" + tier5 + "/" + tier6;
		page.printPage();
		return page;
		}
	
}
