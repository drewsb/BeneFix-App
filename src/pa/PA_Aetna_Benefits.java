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
 */
public class PA_Aetna_Benefits implements Parser {
	
	ArrayList<Page> pages;

	static String[] tokens;

	static String text;
	
	String start_date;
	
	String end_date;

	public PA_Aetna_Benefits(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
	}

	@SuppressWarnings("unused")
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
		
		
		this.tokens = text.split(" |\n"); // Split pdf text by spaces and
											// new line chars
		for (String s : tokens) {
			System.out.println(s);
		}
		System.out.println("TOKENS******************");

		int x;

		int temp_index = 2;
		Boolean rx_covered = false;
		Boolean rx_mail_covered = false;

		String product_name = "";

		int carrier_id = 0;
		String carrier_plan_id = "";
		String plan_pdf_file_name = filename;
		String deductible_indiv = "";
		String deductible_family = "";
		String oon_deductible_indiv = "";
		String oon_deductible_family = "";
		String coinsurance = "";
		String dr_visit_copay = "";
		String specialist_visit_copay = "";
		String er_copay = "";
		String urgent_care_copay = "";
		String rx_copay = "";
		String rx_mail_copay = "";
		String oop_max_indiv = "";
		String oop_max_family = "";
		String oon_oop_max_indiv = "";
		String oon_oop_max_family = "";
		String in_patient_hospital = "";
		String outpatient_diagnostic_lab = "";
		String outpatient_surgery = "";
		String outpatient_diagnostic_x_ray = "";
		String outpatient_complex_imaging = "";
		String physical_occupational_therapy = "";
		String group_rating_area = "";
		String service_zones = "";
		HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
		HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

		while (!tokens[temp_index - 2].equals("BENEFITS")) {
			temp_index++;
		}
		while (!tokens[temp_index + 1].equals("PA")) {
			product_name += tokens[temp_index] + " ";
			temp_index++;
		}
		while (!tokens[temp_index].equals("year)")) {
			temp_index++;
		}
		temp_index++;
		if (tokens[temp_index].equals("Not")) {
			deductible_indiv = "n/a";
			deductible_family = "n/a";
		} else {
			deductible_indiv = tokens[temp_index];
			deductible_family = tokens[temp_index + 2];
		}
		while (!tokens[temp_index].equals("Family")) {
			temp_index++;
		}
		temp_index++;
		if (tokens[temp_index].equals("Not")) {
			oon_deductible_indiv = "n/a";
			oon_deductible_family = "n/a";
		} else {
			oon_deductible_indiv = tokens[temp_index];
			oon_deductible_indiv = tokens[temp_index + 2];
		}
		temp_index += 40;
		while (!tokens[temp_index].equals("Coinsurance")) {
			temp_index++;
		}
		temp_index += 8;
		coinsurance = tokens[temp_index];
		while (!tokens[temp_index].equals("deductible)")) {
			temp_index++;
		}
		temp_index++;
		oop_max_indiv = tokens[temp_index];
		oop_max_family = tokens[temp_index + 2];
		temp_index+=4;
		if(tokens[temp_index].equals("Not")){
			oon_oop_max_indiv = "n/a";
			oon_oop_max_family = "n/a";
		}
		else{
			while(!tokens[temp_index].equals("Individual")){
				oon_oop_max_indiv += tokens[temp_index];
				temp_index++;
			}
			temp_index++;
			while(!tokens[temp_index].equals("Family")){
				oon_oop_max_family += tokens[temp_index];
				temp_index++;
			}
		}
		temp_index += 20;
		while (!tokens[temp_index].equals("Office")) {
			temp_index++;
		}
		temp_index += 4;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			dr_visit_copay += tokens[temp_index] + " ";
			temp_index++;
		}
		temp_index += 20;
		while (!tokens[temp_index].equals("Specialist")) {
			temp_index++;
		}
		temp_index += 4;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			specialist_visit_copay += tokens[temp_index] + " ";
			temp_index++;
		}
		temp_index += 400;
		while (!tokens[temp_index].equals("Outpatient")) {
			temp_index++;
		}
		temp_index += 3;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			outpatient_diagnostic_lab += tokens[temp_index] + " ";
			temp_index++;
		}
		while (!tokens[temp_index].equals("Outpatient")) {
			temp_index++;
		}
		temp_index += 9;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			outpatient_diagnostic_x_ray += tokens[temp_index] + " ";
			temp_index++;
		}
		while (!tokens[temp_index].equals("required.")) {
			temp_index++;
		}
		temp_index++;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			outpatient_complex_imaging += tokens[temp_index] + " ";
			temp_index++;
		}
		while (!tokens[temp_index].equals("Urgent")) {
			temp_index++;
		}
		temp_index += 9;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			urgent_care_copay += tokens[temp_index] + " ";
			temp_index++;
		}
		while (!tokens[temp_index].equals("Emergency")) {
			temp_index++;
		}
		temp_index += 2;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			er_copay += tokens[temp_index] + " ";
			temp_index++;
		}
		temp_index += 20;
		while (!tokens[temp_index].equals("Inpatient")) {
			temp_index++;
		}
		temp_index += 10;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			in_patient_hospital += tokens[temp_index] + " ";
			temp_index++;
		}
		while (!tokens[temp_index].equals("Outpatient")) {
			temp_index++;
		}
		temp_index += 12;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			outpatient_surgery += tokens[temp_index] + " ";
			temp_index++;
		}
		temp_index += 250;
		while (!tokens[temp_index].equals("Occupational")) {
			temp_index++;
		}
		while (!tokens[temp_index].equals("Out-of-Network")) {
			temp_index++;
		}
		temp_index += 2;
		while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
				& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
			physical_occupational_therapy += tokens[temp_index] + " ";
			temp_index++;
		}
		temp_index += 250;
		while (!tokens[temp_index].equals("Retail")) {
			temp_index++;
		}
		System.out.println(temp_index);
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				if ((i == 0 & !rx_covered) || (i == 1 & !rx_mail_covered)) {
					while (!tokens[temp_index].equals("Generic:")) {
						temp_index++;
					}
					temp_index++;
					if (tokens[temp_index].equals("Covered")) {
						if (i == 0) {
							rx_copay = "Covered in full after deductible";
							rx_covered = true;
						} else {
							rx_mail_copay = "Covered in full after deductible";
							rx_mail_covered = true;
						}
						break;
					}
					while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
							& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
						if (i == 0) {
							rx_copay += tokens[temp_index] + " ";
						} else {
							rx_mail_copay += tokens[temp_index] + " ";
						}
						temp_index++;
					}
					if (i == 0) {
						rx_copay = rx_copay.substring(0, rx_copay.length()-1) +  "/";
					} else {
						rx_mail_copay = rx_mail_copay.substring(0, rx_mail_copay.length()-1) +  "/";
					}
				}
			}
			if ((i == 0 & !rx_covered) || (i == 1 & !rx_mail_covered)) {
				for (int k = 0; k < 2; k++) {
					while (!tokens[temp_index].equals("Preferred") & !tokens[temp_index].equals("Generic")) {
						temp_index++;
					}
					temp_index += 3;
					while (!tokens[temp_index - 1].equals("waived") & !tokens[temp_index].equals("Not")
							& !tokens[temp_index - 1].equals("copayment") & !tokens[temp_index - 2].equals("after")) {
						if (i == 0) {
							rx_copay += tokens[temp_index] + " ";
						} else {
							rx_mail_copay += tokens[temp_index] + " ";
						}
						temp_index++;
					}
					if (i == 0 & k == 0) {
						rx_copay = rx_copay.substring(0, rx_copay.length()-1) +  "/";
					} else if (k == 0) {
						rx_mail_copay = rx_mail_copay.substring(0, rx_mail_copay.length()-1) +  "/";
					}
				}
			}
			temp_index += 50;
		}

		deductible_indiv = formatString(deductible_indiv);
		deductible_family = formatString(deductible_family);
		oon_deductible_indiv = formatString(oon_deductible_indiv);
		oon_deductible_family = formatString(oon_deductible_family);
		coinsurance = "0%";
		dr_visit_copay = formatString(dr_visit_copay);
		specialist_visit_copay = formatString(specialist_visit_copay);
		er_copay = formatString(er_copay);
		urgent_care_copay = formatString(urgent_care_copay);
		rx_copay = formatString(rx_copay);
		rx_mail_copay = formatString(rx_mail_copay);
		oop_max_indiv = formatString(oop_max_indiv);
		oop_max_family = formatString(oop_max_family);
		oon_oop_max_indiv = formatString(oon_oop_max_indiv);
		in_patient_hospital = formatString(in_patient_hospital);
		outpatient_diagnostic_lab = formatString(outpatient_diagnostic_lab);
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
		outpatient_complex_imaging = formatString(outpatient_complex_imaging);
		physical_occupational_therapy = formatString(physical_occupational_therapy);

		Page new_page = new MedicalPage(carrier_id, carrier_plan_id, start_date, end_date, product_name, plan_pdf_file_name,
				deductible_indiv, deductible_family, oon_deductible_indiv, oon_deductible_family, coinsurance,
				dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay, rx_copay, rx_mail_copay,
				oop_max_indiv, oop_max_family, oon_oop_max_indiv, oon_oop_max_family, in_patient_hospital,
				outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray, outpatient_complex_imaging,
				physical_occupational_therapy, "", service_zones, "", 0, non_tobacco_dict, tobacco_dict);
		pages.add(new_page);
		return pages;
	}

	public String formatString(String input) {
		int index;
		if (input.contains("Not Applicable") || input.contains("Not Applicable ")) {
			return "n/a";
		}
		if (input.contains(" copayment per visit")) {
			index = input.indexOf(" copayment per visit");
			input = input.substring(0, index) + input.substring(index + 20, input.length());
		}
		if (input.contains(" copayment")) {
			index = input.indexOf(" copayment");
			input = input.substring(0, index) + input.substring(index + 10, input.length());
		}
		if (input.contains(",")) {
			index = input.indexOf(",");
			String afterComma = input.substring(index + 1, input.length());
			if (!containsChar(afterComma)) {
				input = input.substring(0, index) + input.substring(index + 1, input.length());
			}
		}
		return input;
	}

	public Boolean containsChar(String input) {
		char[] arr = input.toCharArray();
		for (char c : arr) {
			if (c != ' ') {
				return true;
			}
		}
		return false;
	}

}
