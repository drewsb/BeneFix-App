package pa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.Formatter;
import components.MedicalPage;

public class PA_WPA_Benefits implements Parser {

	String text;

	String[] tokens;

	String start_date;

	String end_date;

	PDFManager pdfmanager;

	ArrayList<Page> pages;

	public PA_WPA_Benefits(String s_date, String e_date) throws FileNotFoundException, IOException {
		start_date = s_date;
		end_date = e_date;
	}

	public ArrayList<Page> parse(File file, String filename) throws IOException {
		System.out.println(filename);
		pages = new ArrayList<Page>();
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();

		this.tokens = text.split("[\\s\\r\\n]+"); // Split pdf text by spaces
													// and
		// new line chars
		int index = 1;
		String product_name = "";

		Boolean foundPercentage = false;

		int carrier_id = 9;
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

		while (!tokens[index - 1].equals("of")) {
			index++;
		}

		while (!tokens[index].equals("Benefits")) {
			product_name += tokens[index] + " ";
			index++;
		}

		while (!tokens[index - 1].equals("Family")) {
			index++;
		}

		while (!(Formatter.isDollarValue(tokens[index]) || tokens[index].equals("None"))) {
			index++;
		}

		deductible_indiv = tokens[index];
		deductible_family = tokens[index + 1];
		if (deductible_indiv.equals("None") || deductible_family.contains("Combined")) {
			deductible_family = deductible_indiv;
			index--;
		}
		index += 2;
		while (!Formatter.isDollarValue(tokens[index])) {
			index++;
		}
		oon_deductible_indiv = tokens[index];
		oon_deductible_family = tokens[index + 1];
		if (oon_deductible_family.contains("Combined")) {
			oon_deductible_family = oon_deductible_indiv;
		}

		while (!tokens[index - 1].equals("Family")) {
			index++;
		}

		while (!(Formatter.isDollarValue(tokens[index]) || tokens[index].equals("None"))) {
			index++;
		}

		oop_max_indiv = tokens[index];
		oop_max_family = tokens[index + 1];
		if (oop_max_indiv.equals("None") || oop_max_family.contains("Combined")) {
			oop_max_family = oop_max_indiv;
			index--;
		}
		index += 2;
		while (!Formatter.isDollarValue(tokens[index])) {
			index++;
		}
		oon_oop_max_indiv = tokens[index];
		oon_oop_max_family = tokens[index + 1];
		if (oon_oop_max_family.contains("Combined")) {
			oon_oop_max_family = oon_oop_max_indiv;
		}

		while (!tokens[index].equals("Primary")) {
			index++;
		}

		while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
			index++;
		}

		while (!(tokens[index - 1].equals("apply)") || tokens[index - 1].equals("deductible")
				|| tokens[index - 1].equals("copayment"))) {
			if (foundPercentage & Formatter.isPercentage(tokens[index])) {
				break;
			}
			if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
				foundPercentage = true;
			}
			dr_visit_copay += tokens[index] + " ";
			index++;
		}

		foundPercentage = false;

		while (!tokens[index].equals("Specialist")) {
			index++;
		}

		while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
			index++;
		}

		while (!(tokens[index - 1].equals("apply)") || tokens[index - 1].equals("deductible")
				|| tokens[index - 1].equals("copayment"))) {
			if (foundPercentage & Formatter.isPercentage(tokens[index])) {
				break;
			}
			if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
				foundPercentage = true;
			}
			specialist_visit_copay += tokens[index] + " ";
			index++;
		}

		foundPercentage = false;

		int temp_index = index + 200;

		while (!tokens[index].equals("Urgent") & index < temp_index) {
			index++;
		}

		if (index == temp_index) {
			index -= 200;
			urgent_care_copay = "N/A";
			in_patient_hospital = "N/A";

			while (!tokens[index].equals("Emergency")) {
				index++;
			}

			while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
				index++;
			}

			while (!tokens[index].equals("Spinal")) {
				er_copay += tokens[index] + " ";
				index++;
			}

		} else {
			index += 4;

			while (!(tokens[index - 1].equals("apply)") || tokens[index - 1].equals("deductible")
					|| tokens[index - 1].equals("copayment"))) {
				if (foundPercentage & Formatter.isPercentage(tokens[index])) {
					break;
				}
				if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
					foundPercentage = true;
				}
				urgent_care_copay += tokens[index] + " ";
				index++;
			}

			foundPercentage = false;

			while (!tokens[index - 1].equals("Inpatient") && !tokens[index].equals("Emergency")) {
				index++;
			}

			if (tokens[index - 1].equals("Inpatient")) {
				foundPercentage = false;

				while (!(tokens[index - 1].equals("deductible") || tokens[index - 1].equals("copayment"))) {
					if (foundPercentage & Formatter.isPercentage(tokens[index])) {
						break;
					}
					if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
						foundPercentage = true;
					}
					in_patient_hospital += tokens[index] + " ";
					index++;
				}

				while (!tokens[index].equals("Emergency")) {
					index++;
				}

				while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
					index++;
				}

				while (!tokens[index].equals("Ambulance")) {
					er_copay += tokens[index] + " ";
					index++;
				}
			} else {
				while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
					index++;
				}

				while (!tokens[index].equals("Ambulance")) {
					er_copay += tokens[index] + " ";
					index++;
				}

				while (!tokens[index - 1].equals("Inpatient")) {
					index++;
				}
				while (!(tokens[index - 1].equals("deductible") || tokens[index - 1].equals("copayment"))) {
					in_patient_hospital += tokens[index] + " ";
					index++;
				}
			}

		}

		while (!tokens[index].equals("Occupational")) {
			index++;
		}

		index += 2;

		foundPercentage = false;

		while (!(tokens[index - 1].equals("apply)") || tokens[index - 1].equals("deductible")
				|| tokens[index - 1].equals("copayment"))) {
			if (foundPercentage & Formatter.isPercentage(tokens[index])) {
				break;
			}
			if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
				foundPercentage = true;
			}
			physical_occupational_therapy += tokens[index] + " ";
			index++;
		}

		while (!tokens[index].equals("Diagnostic")) {
			index++;
		}

		while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
			index++;
		}

		foundPercentage = false;

		while (!(tokens[index - 1].equals("deductible") || tokens[index - 1].equals("copayment"))) {
			if (foundPercentage & Formatter.isPercentage(tokens[index])) {
				break;
			}
			if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
				foundPercentage = true;
			}
			outpatient_complex_imaging += tokens[index] + " ";
			index++;
		}

		while (!tokens[index].equals("Diagnostic")) {
			index++;
		}

		while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
			index++;
		}

		foundPercentage = false;

		while (!(tokens[index - 1].equals("deductible") || tokens[index - 1].equals("copayment"))) {
			if (foundPercentage & Formatter.isPercentage(tokens[index])) {
				break;
			}
			if (!foundPercentage & Formatter.isPercentage(tokens[index])) {
				foundPercentage = true;
			}
			outpatient_diagnostic_lab += tokens[index] + " ";
			index++;
		}

		while (!tokens[index - 1].equals("Supply)")) {
			index++;
		}

		if (!Formatter.isPercentage(tokens[index]) & !Formatter.isDollarValue(tokens[index])) {
			while (!(tokens[index - 1].equals("deductible") || tokens[index - 1].equals("copayment"))) {
				rx_copay += tokens[index] + " ";
				index++;
			}
			while (!tokens[index - 1].equals("Supply)")) {
				index++;
			}
			while (!(tokens[index - 1].equals("deductible") || tokens[index - 1].equals("copayment"))) {
				rx_mail_copay += tokens[index] + " ";
				index++;
			}
		} else {
			Boolean third_attribute = false;
			temp_index = tokens[index].indexOf("/");
			rx_copay = tokens[index].substring(0, temp_index + 1);
			System.out.println(rx_copay);

			index++;
			while (tokens[index].indexOf('/') == -1) {
				index++;
			}

			temp_index = tokens[index].indexOf("/");
			rx_copay += tokens[index].substring(0, temp_index);
			System.out.println(rx_copay);

			temp_index = index + 5;
			index++;
			while (tokens[index].indexOf('/') == -1 & index < temp_index) {
				index++;
			}
			if (index != temp_index) {
				temp_index = tokens[index].indexOf("/");
				rx_copay += "/" + tokens[index].substring(0, temp_index);
				third_attribute = true;
			}

			while (!tokens[index - 1].equals("Supply)")) {
				index++;
			}

			rx_mail_copay = tokens[index] + "/";
			index++;
			while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
				index++;
			}

			rx_mail_copay += tokens[index];
			if (third_attribute) {
				index++;
				while (!Formatter.isDollarValue(tokens[index]) && !Formatter.isPercentage(tokens[index])) {
					index++;
				}
				rx_mail_copay += "/" + tokens[index];
			}
		}

		outpatient_diagnostic_x_ray = outpatient_diagnostic_lab;
		outpatient_surgery = in_patient_hospital;

		String[] delims = { "generic", "copayment", "formulary" };

		rx_copay = Formatter.removeStrings(rx_copay, delims);
		rx_mail_copay = Formatter.removeStrings(rx_mail_copay, delims);

		if (Formatter.getPercentage(dr_visit_copay).isEmpty()) {
			coinsurance = Formatter.getPercentage(in_patient_hospital);
		} else {
			coinsurance = Formatter.getPercentage(dr_visit_copay);
		}

		Page new_page = new MedicalPage(carrier_id, carrier_plan_id, start_date, end_date, product_name,
				plan_pdf_file_name, deductible_indiv, deductible_family, oon_deductible_indiv, oon_deductible_family,
				coinsurance, dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay, rx_copay,
				rx_mail_copay, oop_max_indiv, oop_max_family, oon_oop_max_indiv, oon_oop_max_family,
				in_patient_hospital, outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray,
				outpatient_complex_imaging, physical_occupational_therapy, "PA", service_zones, "", 0, non_tobacco_dict,
				tobacco_dict);
		pages.add(new_page);
		new_page.printPage();
		return pages;
	}
}
