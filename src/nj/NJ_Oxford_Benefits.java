package nj;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import components.PDFManager;
import components.Page;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class NJ_Oxford_Benefits {

	static String[] tokens;

	static String text;

	public NJ_Oxford_Benefits(File file) throws IOException {
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
	}

	@SuppressWarnings("unused")
	public Page parse(String filename) {
		this.tokens = text.split("[\\s\\r\\n]+"); // Split pdf text by spaces
													// and new line chars

		for (String s : tokens) {
			System.out.println(s);
		}
		System.out.println("TOKENS******************");
		int temp_index = 1;
		int carrier_id = 18;
		StringBuilder carrier_plan_id = new StringBuilder("");
		StringBuilder start_date = new StringBuilder("");
		StringBuilder end_date = new StringBuilder("");
		StringBuilder product_name = new StringBuilder("");
		StringBuilder plan_pdf_file_name = new StringBuilder(filename);
		StringBuilder deductible_indiv = new StringBuilder("");
		StringBuilder deductible_family = new StringBuilder("");
		StringBuilder oon_deductible_indiv = new StringBuilder("");
		StringBuilder oon_deductible_family = new StringBuilder("");
		StringBuilder coinsurance = new StringBuilder("");
		StringBuilder dr_visit_copay = new StringBuilder("");
		StringBuilder specialist_visit_copay = new StringBuilder("");
		StringBuilder er_copay = new StringBuilder("");
		StringBuilder urgent_care_copay = new StringBuilder("");
		StringBuilder rx_copay = new StringBuilder("");
		StringBuilder rx_mail_copay = new StringBuilder("");
		StringBuilder oop_max_indiv = new StringBuilder("");
		StringBuilder oop_max_family = new StringBuilder("");
		StringBuilder oon_oop_max_indiv = new StringBuilder("");
		StringBuilder oon_oop_max_family = new StringBuilder("");
		StringBuilder in_patient_hospital = new StringBuilder("");
		StringBuilder outpatient_diagnostic_lab = new StringBuilder("");
		StringBuilder outpatient_surgery = new StringBuilder("");
		StringBuilder outpatient_diagnostic_x_ray = new StringBuilder("");
		StringBuilder outpatient_complex_imaging = new StringBuilder("");
		StringBuilder outpatient_physical_occupational_therapy = new StringBuilder("");
		StringBuilder group_rating_area = new StringBuilder("");
		StringBuilder physical_occupational_therapy = new StringBuilder("");
		StringBuilder service_zones = new StringBuilder("");
		HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
		HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

		while (!tokens[temp_index].equals("Coverage")) {
			product_name.append(tokens[temp_index] + " ");
			temp_index++;
		}
		while (!tokens[temp_index - 1].equals("deductible?")) {
			temp_index++;
		}
		if (isDollarValue(tokens[temp_index])) {
			deductible_indiv.append(tokens[temp_index]);
			deductible_family.append(tokens[temp_index]);
			oon_deductible_indiv.append("N/A");
			oon_deductible_family.append("N/A");
		} else {
			while (!tokens[temp_index - 1].equals("Network:") & !tokens[temp_index - 1].equals("Non-Network:")) {
				temp_index++;
			}
			if (tokens[temp_index - 1].equals("Non-Network:")) {
				deductible_indiv.append("N/A");
				deductible_family.append("N/A");
				oon_deductible_indiv.append(tokens[temp_index++]);
				oon_deductible_family.append(tokens[temp_index]);
			} else {
				deductible_indiv.append(tokens[temp_index++]);
				deductible_family.append(tokens[temp_index]);
				oon_deductible_indiv.append("N/A");
				oon_deductible_family.append("N/A");

				int temp_index2 = temp_index;
				while (temp_index2 < temp_index + 10) {
					if (tokens[temp_index2 - 1].equals("Non-Network:")) {
						oon_deductible_indiv = new StringBuilder(tokens[temp_index2++]);
						oon_deductible_family = new StringBuilder(tokens[temp_index2]);
						break;
					}
					temp_index2++;
				}
			}
		}

		while (!tokens[temp_index].equals("out-of-pocket")) {
			temp_index++;
		}

		while (!tokens[temp_index - 1].equals("Network:") & !tokens[temp_index - 1].equals("Non-Network:")) {
			temp_index++;
		}
		oop_max_indiv.append(tokens[temp_index++]);
		oop_max_family.append(tokens[temp_index]);
		oon_oop_max_indiv.append("N/A");
		oon_oop_max_family.append("N/A");

		int temp_index2 = temp_index;
		while (temp_index2 < temp_index + 10) {
			if (tokens[temp_index2 - 1].equals("Non-Network:")) {
				oon_oop_max_indiv = new StringBuilder(tokens[temp_index2++]);
				oon_oop_max_family = new StringBuilder(tokens[temp_index2]);
				break;
			}
			temp_index2++;
		}
		while (!tokens[temp_index + 1].contains("Primary")) {
			temp_index++;
		}
		while (!isPercentage(tokens[temp_index + 1]) & !isDollarValue(tokens[temp_index + 1])) {
			dr_visit_copay.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}

		while (!tokens[temp_index + 1].contains("Specialist")) {
			temp_index++;
		}
		while (!isPercentage(tokens[temp_index + 1]) & !isDollarValue(tokens[temp_index + 1])) {
			specialist_visit_copay.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}

		while (!tokens[temp_index - 3].equals("have") || !tokens[temp_index - 1].equals("test")) {
			temp_index++;
		}
		while (!tokens[temp_index - 1].contains("Diagnostic")) {
			outpatient_diagnostic_x_ray.append(tokens[temp_index] + " ");
			if (tokens[temp_index].contains("Diagnostic") & tokens[temp_index].contains("Charge")) {
				outpatient_diagnostic_x_ray = new StringBuilder("No Charge");
				break;
			}
			temp_index++;
		}
		while (!tokens[temp_index + 1].contains("Imaging")) {
			temp_index++;
		}

		while (!isPercentage(tokens[temp_index + 1]) & !isDollarValue(tokens[temp_index + 1])) {
			outpatient_complex_imaging.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}

		for (int i = 0; i < 3; i++) {
			while (!tokens[temp_index - 1].contains("Retail:")) {
				temp_index++;
			}
			rx_copay.append(tokens[temp_index]);
			if (i != 2) {
				rx_copay.append("/");
			}
			while (tokens[temp_index - 2].equals("Mail")) {
				temp_index++;
			}
			rx_mail_copay.append(tokens[temp_index]);
			if (i != 2) {
				rx_mail_copay.append("/");
			}
		}

		while (!tokens[temp_index - 2].contains("outpatient") & !tokens[temp_index - 1].contains("surgery")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index + 1]) & !isPercentage(tokens[temp_index + 1])) {
			temp_index++;
		}
		while (!tokens[temp_index].contains("Facility")) {
			outpatient_surgery.append(tokens[temp_index] + " ");
			temp_index++;
		}

		while (!tokens[temp_index].contains("Emergency")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index + 1]) & !isPercentage(tokens[temp_index + 1])) {
			er_copay.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}
		while (!tokens[temp_index].contains("Urgent")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index + 1]) & !isPercentage(tokens[temp_index + 1])) {
			urgent_care_copay.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}

		while (!tokens[temp_index - 2].equals("hospital") || !tokens[temp_index - 1].equals("stay")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index]) & !isPercentage(tokens[temp_index])) {
			temp_index++;
		}
		while (!tokens[temp_index].contains("Facility")) {
			in_patient_hospital.append(tokens[temp_index] + " ");
			temp_index++;
		}
		while (!tokens[temp_index].contains("Rehabilitation")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index + 1]) & !isPercentage(tokens[temp_index + 1])) {
			physical_occupational_therapy.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}
		/*
		 * Incomplete: Inpatient hospital, outpatient surgery, outpatient
		 * diagnostic lab, coinsurance
		 */

		deductible_indiv = formatString(deductible_indiv);
		deductible_family = formatString(deductible_family);
		oon_deductible_indiv = formatString(oon_deductible_indiv);
		oon_deductible_family = formatString(oon_deductible_family);
		dr_visit_copay = formatString(dr_visit_copay);
		specialist_visit_copay = formatString(specialist_visit_copay);
		er_copay = formatString(er_copay);
		if (er_copay.toString().equals("No Charge") || !isPercentage(er_copay.toString())) {
			coinsurance.append("0%");
		} else {
			coinsurance.append(er_copay);
		}
		urgent_care_copay = formatString(urgent_care_copay);
		rx_copay = formatRx(rx_copay);
		rx_mail_copay = formatRx(rx_mail_copay);
		oop_max_indiv = formatString(oop_max_indiv);
		oop_max_family = formatString(oop_max_family);
		oon_oop_max_indiv = formatString(oon_oop_max_indiv);
		oon_oop_max_family = formatString(oon_oop_max_family);
		in_patient_hospital = formatInpatientHospital(in_patient_hospital);
		// in_patient_hospital = formatString(in_patient_hospital);
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
		outpatient_diagnostic_lab = outpatient_diagnostic_x_ray;
		outpatient_complex_imaging = formatString(outpatient_complex_imaging);
		physical_occupational_therapy = formatString(physical_occupational_therapy);
		Page new_page = new Page(carrier_id, carrier_plan_id.toString(), "", "", product_name.toString(),
				plan_pdf_file_name.toString(), deductible_indiv.toString(), deductible_family.toString(),
				oon_deductible_indiv.toString(), oon_deductible_family.toString(), coinsurance.toString(),
				dr_visit_copay.toString(), specialist_visit_copay.toString(), er_copay.toString(),
				urgent_care_copay.toString(), rx_copay.toString(), rx_mail_copay.toString(), oop_max_indiv.toString(),
				oop_max_family.toString(), oon_oop_max_indiv.toString(), oon_oop_max_family.toString(),
				in_patient_hospital.toString(), outpatient_diagnostic_lab.toString(), outpatient_surgery.toString(),
				outpatient_diagnostic_x_ray.toString(), outpatient_complex_imaging.toString(),
				physical_occupational_therapy.toString(), "", service_zones.toString(), "", 0, non_tobacco_dict,
				tobacco_dict);

		new_page.printPage();
		return new_page;
	}

	public StringBuilder formatString(StringBuilder input) {
		input = removeString(input, "covered");
		input = removeString(input, "Not");
		input = removeString(input, "Urgent");
		input = removeString(input, "Emergency");
		input = removeString(input, ",");
		input = removeString(input, ".");
		input = removeString(input, "*");
		input = removeString(input, "person");
		input = removeString(input, "copay");
		input = removeString(input, "per");
		input = removeString(input, "visit");
		input = removeString(input, "Individual");
		input = removeString(input, "Free Standing Provider");
		input = removeString(input, "service");
		input = removeString(input, "Rehabilitation");
		input = removeString(input, "outpatient");
		input = removeString(input, "Diagnostic");
		input = removeString(input, "after");
		input = removeString(input, "ded");
		input = removeString(input, "admission");
		input = removeString(input, "co-ins");
		if (!input.toString().equals("N/A")) {
			input = removeString(input, "/");
		}
		if (input.length() > 0) {
			if (input.charAt(0) == ' ') {
				input.deleteCharAt(0);
			}
		}
		return new StringBuilder(input);
	}

	public StringBuilder formatRx(StringBuilder s) {
		int x = s.indexOf("/");
		int y = s.lastIndexOf("/");
		if (s.subSequence(0, x).equals(s.subSequence(x + 1, y))
				& s.subSequence(x + 1, y).equals(s.subSequence(y + 1, s.length()))) {
			return new StringBuilder(s.subSequence(0, x));
		}
		return s;
	}

	public static Boolean isPercentage(String s) {
		return s.contains("%");
	}

	public static Boolean isDollarValue(String s) {
		return s.contains("$");
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

	public StringBuilder formatInpatientHospital(StringBuilder s) {
		s = removeString(s, "Not");
		s = removeString(s, "No");
		s = removeString(s, "covered");
		s = removeString(s, "ded");
		s = removeString(s, "co-ins");
		s = removeString(s, "after");
		s = removeString(s, ".");
		return s;
	}

	public StringBuilder removeString(StringBuilder s, String r) {
		while (s.indexOf(r) != -1) {
			int index = s.indexOf(r);
			s.replace(index, index + r.length(), "");
		}
		return s;

	}

}
