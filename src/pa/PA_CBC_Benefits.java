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
public class PA_CBC_Benefits implements Parser {

	static String[] tokens;

	static String text;
	
	String start_date;
	
	String end_date;

	cbcType type;

	public enum cbcType {
		ONE, TWO, THREE, DEFAULT
	}

	public PA_CBC_Benefits(String s_date, String e_date) {
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
//		 for(String s : tokens){
//		 System.out.println(s);
//		 }
//		 System.out.println("TOKENS******************");
		int x;
		Boolean covered = false;
		Boolean none = false;
		Boolean valuePassed = false;

		int temp_index = 0;
		while (!tokens[temp_index].equals("Benefit")) {
			temp_index++;
		}
		temp_index += 3;
		String product_name = "";
		while (!tokens[temp_index].equals("THIS")) {
			if (!tokens[temp_index].isEmpty()) {
				product_name += tokens[temp_index] + " ";
			}
			temp_index++;
		}

		temp_index = 100;
		while (temp_index < 200) {
			if (tokens[temp_index].equals("person")) {
				type = cbcType.ONE;
				break;
			} else if (tokens[temp_index].equals("Participating")) {
				type = cbcType.TWO;
				break;
			} else if (tokens[temp_index].equals("PCP-Directed")) {
				type = cbcType.THREE;
				break;
			}
			temp_index++;
		}
		if(type == null){
			type = cbcType.DEFAULT;
		}
		//System.out.println(type.toString());
		int carrier_id = 0;
		String carrier_plan_id = "";
		String plan_pdf_file_name = filename;
		String deductible_indiv = "";
		String deductible_family = "";
		String oon_deductible_individual = "";
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
		String oon_oop_max_individual = "";
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
		switch (type) {
		case ONE:
			deductible_indiv = tokens[temp_index - 2];
			deductible_family = tokens[temp_index + 2];
			oon_deductible_individual = "n/a";
			oon_deductible_family = "n/a";
			while (!tokens[temp_index].equals("Clinic)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index].equals("copayment") & (!tokens[temp_index].isEmpty() || !valuePassed)) {
				if (!tokens[temp_index].isEmpty()) {
					dr_visit_copay += tokens[temp_index] + " ";
					valuePassed = true;
				}
				temp_index++;
			}
			while (!tokens[temp_index].equals("Specialist")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index].isEmpty() & !tokens[temp_index].equals("copayment")) {
				specialist_visit_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Emergency")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index].isEmpty()) {
				er_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Urgent")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				urgent_care_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Admission)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index].isEmpty()) {
				in_patient_hospital += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("(facility)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index].isEmpty()) {
				outpatient_surgery += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 5;
			while (!tokens[temp_index].equals("Coinsurance")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index + 1].equals("coinsurance") & !tokens[temp_index].isEmpty()) {
				coinsurance += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 40;
			while (!tokens[temp_index].equals("ends.")) {
				temp_index++;
			}
			temp_index += 2;
			oop_max_indiv = tokens[temp_index];
			oop_max_family = tokens[temp_index + 4];
			oon_oop_max_individual = "n/a";
			oon_oop_max_family = "n/a";
			temp_index += 300;
			while (!tokens[temp_index].equals("High")) {
				temp_index++;
			}
			temp_index += 10;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance") 
					& !tokens[temp_index].isEmpty()) {
				outpatient_complex_imaging += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Radiology")) {
				temp_index++;
			}
			temp_index += 7;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance")
					& !tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				outpatient_diagnostic_x_ray += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 5;
			outpatient_diagnostic_lab = "Independent lab: ";
			while (!tokens[temp_index].isEmpty()) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			outpatient_diagnostic_lab += "Facility-owned lab: ";
			temp_index += 6;
			while (!tokens[temp_index].isEmpty()) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 8;
			while (!tokens[temp_index].equals("period)")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				physical_occupational_therapy += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 450;
			break;
		case TWO:
			while (!tokens[temp_index].equals("per")) {
				if (tokens[temp_index].equals("None")) {
					none = true;
				}
				temp_index++;
			}
			if (none) {
				deductible_indiv = "0";
				deductible_family = "0";
				oon_deductible_individual = tokens[temp_index - 1];
				oon_deductible_family = tokens[temp_index + 3];
			} else {
				deductible_indiv = tokens[temp_index - 1];
				deductible_family = tokens[temp_index + 3];
				oon_deductible_individual = tokens[temp_index + 7];
				oon_deductible_family = tokens[temp_index + 11];
			}
			while (!tokens[temp_index].equals("Clinic)")) {
				temp_index++;
			}
			temp_index++;
			while (tokens[temp_index].isEmpty()) {
				temp_index++;
			}
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance")) {
				if (!tokens[temp_index].isEmpty()) {
					dr_visit_copay += tokens[temp_index] + " ";
				}
				temp_index++;
			}
			while (!tokens[temp_index].equals("Specialist")) {
				temp_index++;
			}
			// need to add "after deductible"
			temp_index += 3;
			while (tokens[temp_index].isEmpty()) {
				temp_index++;
			}
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance")) {
				if (!tokens[temp_index].isEmpty()) {
					specialist_visit_copay += tokens[temp_index] + " ";
				}
				temp_index++;
			}
			while (!tokens[temp_index].equals("Emergency")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index].isEmpty()) {
				er_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Urgent")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index + 2].equals("Inpatient") & !tokens[temp_index - 1].equals("deductible")
					& !tokens[temp_index - 1].equals("Applicable")) {
				if (!tokens[temp_index].isEmpty()) {
					urgent_care_copay += tokens[temp_index] + " ";
				}
				temp_index++;
			}

			while (!tokens[temp_index].equals("Admission)") & !tokens[temp_index].equals("Day)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index + 1].equals("coinsurance")) {
				in_patient_hospital += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("(facility)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index + 1].equals("coinsurance")) {
				outpatient_surgery += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 5;
			while (!tokens[temp_index].equals("Coinsurance")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index + 1].equals("coinsurance") & !tokens[temp_index].isEmpty()) {
				coinsurance += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 30;
			while (!tokens[temp_index].equals("ends.")) {
				temp_index++;
			}
			temp_index += 2;
			oop_max_indiv = tokens[temp_index];
			temp_index++;
			while (!tokens[temp_index].contains("$")) {
				temp_index++;
			}
			oop_max_family = tokens[temp_index];
			temp_index++;
			while (!tokens[temp_index].contains("$")) {
				temp_index++;
			}
			oon_oop_max_individual = tokens[temp_index];
			temp_index++;
			while (!tokens[temp_index].contains("$")) {
				temp_index++;
			}
			oon_oop_max_family = tokens[temp_index];
			temp_index += 330;
			while (!tokens[temp_index].equals("High")) {
				temp_index++;
			}
			temp_index += 11;
			while(tokens[temp_index].isEmpty()){
				temp_index++;
			}
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance")) {
				outpatient_complex_imaging += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Radiology")) {
				temp_index++;
			}
			temp_index += 6;
			while (tokens[temp_index].isEmpty()) {
				temp_index++;
			}
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance")
					& !tokens[temp_index].equals("copayment")) {
				outpatient_diagnostic_x_ray += tokens[temp_index] + " ";
				temp_index++;
			}
			outpatient_diagnostic_lab = "Independent lab: ";
			while (!tokens[temp_index].equals("Independent")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index].equals("copayment")
					& !tokens[temp_index+1].equals("coinsurance")) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Facility-owned")) {
				temp_index++;
			}
			outpatient_diagnostic_lab += "Facility-owned lab: ";
			temp_index += 3;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index].equals("copayment")
					& !tokens[temp_index+1].equals("coinsurance")) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 30;
			while (!tokens[temp_index].equals("period)")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index + 1].equals("coinsurance") & !tokens[temp_index].isEmpty()) {
				physical_occupational_therapy += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 400;
			break;
		case THREE:
			while (!tokens[temp_index].equals("per")) {
				if (tokens[temp_index].equals("None")) {
					none = true;
				}
				temp_index++;
			}
			if (none) {
				deductible_indiv = "0";
				deductible_family = "0";
				oon_deductible_individual = tokens[temp_index - 1];
				oon_deductible_family = tokens[temp_index + 3];
			} else {
				deductible_indiv = tokens[temp_index - 1];
				deductible_family = tokens[temp_index + 3];
				oon_deductible_individual = tokens[temp_index + 16];
				oon_deductible_family = tokens[temp_index + 20];
			}
			while (!tokens[temp_index].equals("Pediatrician)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index].equals("copayment") & (!tokens[temp_index].isEmpty() || !valuePassed)) {
				if (!tokens[temp_index].isEmpty()) {
					dr_visit_copay += tokens[temp_index] + " ";
					valuePassed = true;
				}
				temp_index++;
			}
			while (!tokens[temp_index].equals("Specialist")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index].isEmpty() & !tokens[temp_index + 1].equals("coinsurance")) {
				specialist_visit_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Emergency")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index].isEmpty()) {
				er_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 4;
			while (!tokens[temp_index].equals("copayment")) {
				urgent_care_copay += tokens[temp_index];
				temp_index++;
			}
			while (!tokens[temp_index - 1].equals("Admission)")) {
				temp_index++;
			}
			while (!tokens[temp_index + 1].equals("coinsurance")) {
				in_patient_hospital += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("(facility)")) {
				temp_index++;
			}
			temp_index++;
			while (!tokens[temp_index + 1].equals("coinsurance")) {
				outpatient_surgery += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 10;
			while (!tokens[temp_index].equals("Coinsurance")) {
				temp_index++;
			}
			temp_index++;

			while (!tokens[temp_index + 1].equals("coinsurance") & !tokens[temp_index].isEmpty()) {
				coinsurance += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 30;
			while (!tokens[temp_index].equals("ends.")) {
				temp_index++;
			}
			temp_index += 2;
			oop_max_indiv = tokens[temp_index];
			temp_index++;
			while (!tokens[temp_index].contains("$")) {
				temp_index++;
			}
			oop_max_family = tokens[temp_index];
			temp_index++;
			while (!tokens[temp_index].contains("$")) {
				temp_index++;
			}
			oon_oop_max_individual = tokens[temp_index];
			temp_index++;
			while (!tokens[temp_index].contains("$")) {
				temp_index++;
			}
			oon_oop_max_family = tokens[temp_index];
			temp_index += 400;
			while (!tokens[temp_index].equals("High")) {
				temp_index++;
			}
			temp_index += 12;
			while(tokens[temp_index].isEmpty()){
				temp_index++;
			}
			while (!tokens[temp_index - 1].equals("deductible")) {
				outpatient_complex_imaging += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Radiology")) {
				temp_index++;
			}
			temp_index += 9;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index + 1].equals("coinsurance")
					& !tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				outpatient_diagnostic_x_ray += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 9;
			while (!tokens[temp_index].equals("Independent")) {
				temp_index++;
			}
			temp_index += 4;
			outpatient_diagnostic_lab = "Independent lab: ";
			while (!tokens[temp_index + 2].equals("coinsurance")) {
				if(!tokens[temp_index].isEmpty()){
					outpatient_diagnostic_lab += tokens[temp_index] + " ";
				}
				temp_index++;
			}
			outpatient_diagnostic_lab += "Facility-owned lab: ";
			temp_index += 10;
			while (!tokens[temp_index].equals("Facility-owned")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index + 2].equals("coinsurance")) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 40;
			while (!tokens[temp_index].equals("period)") && !tokens[temp_index].equals("period")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				physical_occupational_therapy += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 500;
			break;
		}
		
		while (!tokens[temp_index].equals("Generic")) {
			temp_index++;
		}
		rx_copay = tokens[temp_index + 4];
		if (rx_copay.equals("Covered")) {
			rx_copay = "Covered in full after deductible";
			rx_mail_copay = "Covered in full after deductible";
			covered = true;
		}
		if (!covered) {
			if (type == cbcType.ONE || type == cbcType.TWO) {
				while (!tokens[temp_index].equals("copayment")) {
					temp_index++;
				}
				rx_mail_copay = tokens[temp_index + 1];
				for (int i = 0; i < 3; i++) {
					temp_index += 14;
					while (!tokens[temp_index].equals("copayment")) {
						temp_index++;
					}
					rx_copay += "/" + tokens[temp_index - 1];
					rx_mail_copay += "/" + tokens[temp_index + 1];
				}
			} else {
				rx_mail_copay = tokens[temp_index + 8];
				for (int i = 0; i < 3; i++) {
					temp_index += 10;
					while (!tokens[temp_index].equals("Drugs")) {
						temp_index++;
					}
					temp_index++;
					while (tokens[temp_index].isEmpty()) {
						temp_index++;
					}
					while (!tokens[temp_index].equals("copay")) {
						rx_copay += "/" + tokens[temp_index];
						temp_index++;
					}
					temp_index += 3;
					while (!tokens[temp_index].equals("copay")) {
						rx_mail_copay += "/" + tokens[temp_index];
						temp_index++;
					}
				}

			}
		}
		deductible_indiv = formatString(deductible_indiv);
		deductible_family = formatString(deductible_family);
		oon_deductible_individual = formatString(oon_deductible_individual);
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
		oon_oop_max_individual = formatString(oon_oop_max_individual);
		in_patient_hospital = formatString(in_patient_hospital);
		outpatient_diagnostic_lab = formatString(outpatient_diagnostic_lab);
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
		outpatient_complex_imaging = formatString(outpatient_complex_imaging);
		physical_occupational_therapy = formatString(physical_occupational_therapy);

		MedicalPage new_page = new MedicalPage(carrier_id, carrier_plan_id, "", "", product_name, plan_pdf_file_name,
				deductible_indiv, deductible_family, oon_deductible_individual, oon_deductible_family, coinsurance,
				dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay, rx_copay, rx_mail_copay,
				oop_max_indiv, oop_max_family, oon_oop_max_individual, oon_oop_max_family, in_patient_hospital,
				outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray, outpatient_complex_imaging,
				physical_occupational_therapy, "", service_zones, "", 0, non_tobacco_dict, tobacco_dict);
		
		ArrayList<Page> pages = new ArrayList<Page>();
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
