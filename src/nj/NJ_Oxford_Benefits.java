package nj;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.*;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class NJ_Oxford_Benefits implements Parser {

	static String[] tokens;

	static String text;
	
	static String start_date;
	
	static String end_date;

	public NJ_Oxford_Benefits(String s_date, String e_date) throws IOException {
		start_date = s_date;
		end_date = e_date;
	}

	@SuppressWarnings("unused")
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
		
		this.tokens = text.split("[\\s\\r\\n]+"); // Split pdf text by spaces
													// and new line chars

		for (String s : tokens) {
			System.out.println(s);
		}
		System.out.println("TOKENS******************");
		int temp_index = 1;
		int carrier_id = 18;
		StringBuilder carrier_plan_id = new StringBuilder("");
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

		while (!tokens[temp_index+1].contains("Office:") & !tokens[temp_index].contains("Not")) {
			outpatient_complex_imaging.insert(0, tokens[temp_index] + " ");
			temp_index--;
		}

		for (int i = 0; i < 3; i++) {
			while (!tokens[temp_index - 1].contains("Retail:") || tokens[temp_index].equals("Up")) {
				temp_index++;
			}
			rx_copay.append(tokens[temp_index]);
			if (i != 2) {
				rx_copay.append("/");
			}
			while (!tokens[temp_index - 2].equals("Mail") || tokens[temp_index].equals("Up")) {
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
		while (!tokens[temp_index].contains("Facility")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index+1]) & !isPercentage(tokens[temp_index+1])) {
			if(tokens[temp_index].contains("Charge") || tokens[temp_index].contains("No")){
				in_patient_hospital = new StringBuilder("No Charge");
				break;
			}
			in_patient_hospital.insert(0,tokens[temp_index] + " ");
			temp_index--;
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
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
		outpatient_diagnostic_lab = outpatient_diagnostic_x_ray;
		outpatient_complex_imaging = formatString(outpatient_complex_imaging);
		physical_occupational_therapy = formatString(physical_occupational_therapy);
		MedicalPage new_page = new MedicalPage(carrier_id, carrier_plan_id.toString(), start_date, end_date, product_name.toString(),
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
		ArrayList<Page> pages = new ArrayList<Page>();
		pages.add(new_page);
		return pages;
	}

	public StringBuilder formatString(StringBuilder input) {
		String[] delims = {"covered", "Not", "Urgent", "Emergency", ",",".", "*", "person", "copay", "per", "visit", "Individual",
				"Free Standing Provider", "service", "Rehabilitation", "outpatient", "Diagnostic", "after", "ded", "admission", "co-ins"};
		input = Formatter.removeStrings(input, delims);
		if (!input.toString().equals("N/A")) {
			input = Formatter.removeString(input, "/");
		}
		if (input.length() > 0) {
			if (input.charAt(0) == ' ') {
				input.deleteCharAt(0);
			}
		}
		return new StringBuilder(input);
	}

	public StringBuilder formatRx(StringBuilder s) {
		if(s.indexOf("Not")!=-1){
			s = new StringBuilder("N/A");
			return s;
		}
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
		String[] delims = {"Not", "covered", "ded", "co-ins", "after", ".", "Facility"};
		s = Formatter.removeStrings(s,delims);
		System.out.println(s);
		char[] arr = s.toString().toCharArray();
		char c = arr[0];
		System.out.println(c);
		int index = 0;
		while(c == ' ' & index < arr.length){
			s.deleteCharAt(0);
			System.out.println(s);
			index++;
			c = arr[index];
			System.out.println(c);
		}
		return s;
	}


}
