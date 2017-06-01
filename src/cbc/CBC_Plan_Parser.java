package cbc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import components.PDFManager;
import components.Page;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class CBC_Plan_Parser {

	static String[] tokens;

	static String text;

	cbcType type;

	public enum cbcType {
		ONE, TWO, THREE
	}

	public CBC_Plan_Parser(File file) throws IOException {
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
	}

	@SuppressWarnings("unused")
	public Page parse(String filename) {
		this.tokens = text.split(" |\n"); // Split pdf text by spaces and
											// new line chars
		for (String s : tokens) {
			System.out.println(s);
		}
		System.out.println("**********************");
		int x;
		Boolean covered = false;
		Boolean none = false;
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
		System.out.println(type.toString());
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
			dr_visit_copay = tokens[temp_index + 2];
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
			while (!tokens[temp_index].isEmpty()) {
				coinsurance += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 45;
			oop_max_indiv = tokens[temp_index];
			oop_max_family = tokens[temp_index + 4];
			oon_oop_max_individual = "n/a";
			oon_oop_max_family = "n/a";
			temp_index += 300;
			while (!tokens[temp_index].equals("High")) {
				temp_index++;
			}
			temp_index += 10;
			while (!tokens[temp_index].isEmpty()) {
				outpatient_complex_imaging += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 9;
			while (!tokens[temp_index].isEmpty()) {
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
			dr_visit_copay = tokens[temp_index + 2];
			while (!tokens[temp_index].equals("Specialist")) {
				temp_index++;
			}
			// need to add "after deductible"
			specialist_visit_copay = tokens[temp_index + 3];
			temp_index += 6;
			while (!tokens[temp_index].equals("Emergency")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				er_copay += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Urgent")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index].equals("copayment") & !tokens[temp_index - 1].equals("Applicable")) {
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
			while (!tokens[temp_index].isEmpty()) {
				coinsurance += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 51;
			oop_max_indiv = tokens[temp_index];
			oop_max_family = tokens[temp_index + 4];
			oon_oop_max_individual = tokens[temp_index + 8];
			oon_oop_max_family = tokens[temp_index + 12];
			temp_index += 350;
			while (!tokens[temp_index].equals("High")) {
				temp_index++;
			}
			temp_index += 12;
			while (!tokens[temp_index - 1].equals("deductible")) {
				outpatient_complex_imaging += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 13;
			while (!tokens[temp_index - 1].equals("deductible")) {
				outpatient_diagnostic_x_ray += tokens[temp_index] + " ";
				temp_index++;
			}
			outpatient_diagnostic_lab = "Independent lab: ";
			while (!tokens[temp_index].equals("Independent")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index].equals("copayment")
					& !tokens[temp_index].isEmpty()) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			while (!tokens[temp_index].equals("Facility-owned")) {
				temp_index++;
			}
			outpatient_diagnostic_lab += "Facility-owned lab: ";
			temp_index += 3;
			while (!tokens[temp_index - 1].equals("deductible") & !tokens[temp_index].equals("copayment")
					& !tokens[temp_index].isEmpty()) {
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 30;
			while (!tokens[temp_index].equals("period)")) {
				temp_index++;
			}
			temp_index += 2;
			while (!tokens[temp_index].equals("copayment") & !tokens[temp_index].isEmpty()) {
				physical_occupational_therapy += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 420;
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
			dr_visit_copay = tokens[temp_index + 3];
			// need to add "after deductible"
			while (!tokens[temp_index].equals("Specialist")) {
				temp_index++;
			}
			specialist_visit_copay = tokens[temp_index + 3];
			temp_index += 10;
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
			while (!tokens[temp_index + 1].equals("coinsurance")) {
				coinsurance += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 40;
			while (!tokens[temp_index].equals("per")) {
				temp_index++;
			}
			oop_max_indiv = tokens[temp_index - 1];
			oop_max_family = tokens[temp_index + 3];
			oon_oop_max_individual = tokens[temp_index + 7];
			oon_oop_max_family = tokens[temp_index + 11];
			temp_index += 500;
			while (!tokens[temp_index].equals("High")) {
				temp_index++;
			}
			temp_index += 12;
			while (!tokens[temp_index - 1].equals("deductible")) {
				outpatient_complex_imaging += tokens[temp_index] + " ";
				temp_index++;
			}
			temp_index += 13;
			while (!tokens[temp_index].equals("Imaging)")) {
				temp_index++;
			}
			temp_index += 3;
			while (!tokens[temp_index - 1].equals("deductible")) {
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
				outpatient_diagnostic_lab += tokens[temp_index] + " ";
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
			while (!tokens[temp_index].equals("period)")) {
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
				System.out.println(temp_index);
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
					while (!tokens[temp_index].equals("copay")) {
						System.out.println(tokens[temp_index]);
						rx_copay += "/" + tokens[temp_index];
						temp_index++;
					}
					temp_index += 3;
					while (!tokens[temp_index].equals("copay")) {
						System.out.println(tokens[temp_index]);
						rx_mail_copay += "/" + tokens[temp_index];
						temp_index++;
					}
				}

			}
		}

		er_copay = formatString(er_copay);

		Page new_page = new Page(carrier_id, carrier_plan_id, "", "", product_name, plan_pdf_file_name,
				deductible_indiv, deductible_family, oon_deductible_individual, oon_deductible_family, coinsurance,
				dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay, rx_copay, rx_mail_copay,
				oop_max_indiv, oop_max_family, oon_oop_max_individual, oon_oop_max_family, in_patient_hospital,
				outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray, outpatient_complex_imaging,
				physical_occupational_therapy, "", service_zones, "", 0, non_tobacco_dict, tobacco_dict);
		// System.out.printf("deductible_indiv: %s\n", deductible_indiv);
		// System.out.printf("deductible_family: %s\n", deductible_family);
		// System.out.printf("oon_deductible_indiv: %s\n",
		// oon_deductible_individual);
		// System.out.printf("oon_deductible_family: %s\n",
		// oon_deductible_family);
		// System.out.printf("dr_visit_copay: %s\n", dr_visit_copay);
		// System.out.printf("specialist_visits_copay: %s\n",
		// specialist_visit_copay);
		// System.out.printf("er_copay: %s\n", er_copay);
		// System.out.printf("urgent_care_copay: %s\n", urgent_care_copay);
		// System.out.printf("oop_max_indiv: %s\n", oop_max_indiv);
		// System.out.printf("oop_max_family: %s\n", oop_max_family);
		// System.out.printf("oon_oop_max_indiv: %s\n", oon_oop_max_individual);
		// System.out.printf("oon_oop_max_family: %s\n", oon_oop_max_family);
		// System.out.printf("in_patient_hosptial: %s\n", in_patient_hospital);
		// System.out.printf("outpatient_diagnostic_lab: %s\n",
		// outpatient_diagnostic_lab);
		// System.out.printf("outpatient_diagnostic_x_ray: %s\n",
		// outpatient_diagnostic_x_ray);
		// System.out.printf("outpatient_complex_imaging: %s\n",
		// outpatient_complex_imaging);
		// System.out.printf("physical_occupational_therapy: %s\n",
		// physical_occupational_therapy);
		// System.out.printf("rx_copay: %s\n", rx_copay);
		// System.out.printf("rx_mail_copay: %s", rx_mail_copay);
		return new_page;
	}

	public String formatString(String input) {
		int index;
		if (input.contains("Not Applicable") || input.contains("Not Applicable ")) {
			return "n/a";
		}
		if (input.contains(",")) {
			index = input.indexOf(",");
			input = input.substring(0, index) + input.substring(index + 1, input.length());
		}
		if (input.contains(" copayment per visit")) {
			index = input.indexOf(" copayment per visit");
			input = input.substring(0, index) + "," + input.substring(index + 20, input.length());
		}
		if (input.contains(" copayment")) {
			index = input.indexOf(" copayment");
			input = input.substring(0, index) + "," + input.substring(index + 10, input.length());
		}
		return input;
	}

}
