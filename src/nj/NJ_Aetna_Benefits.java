package nj;

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
public class NJ_Aetna_Benefits implements Parser {

	static String[] tokens;

	static String text;
	
	String start_date;
	
	String end_date;

	public NJ_Aetna_Benefits(String s_date, String e_date) {
		start_date = s_date;
		end_date = e_date;
	}

	enum type {
		ONE, TWO
	}

	@SuppressWarnings("unused")
	public ArrayList<Page> parse(File file, String filename) throws IOException {
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
		
		
		this.tokens = text.split("[\\s\\r\\n]+"); // Split pdf text by spaces
													// and new line chars

		String rx_max_string = "";
		String rx_mail_max_string = "";
		Boolean rx_max = false;
		Boolean x_ray = false;
		Boolean lab_no_charge = false;

		int count = 0;
		int index = 1;
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

		type colType = type.ONE;

		while (!((tokens[index - 1].equals("Employee") || tokens[index - 1].equals("Individual"))
				& isDollarValue(tokens[index]))) {
			if (tokens[index].toLowerCase().contains("non-designated")) {
				colType = type.TWO;
			}
			index++;
		}
		deductible_indiv.append(tokens[index]);
		index += 3;
		deductible_family.append(tokens[index]);
		oon_deductible_indiv.append("N/A");
		oon_deductible_family.append("N/A");

		int temp_index = index;
		while (temp_index < index + 10) {
			if (tokens[temp_index].contains("Out-of-network")) {
				index = temp_index + 2;
				oon_deductible_indiv = new StringBuilder(tokens[index]);
				index += 3;
				oon_deductible_family = new StringBuilder(tokens[index]);
				break;
			}
			if (tokens[temp_index].contains("Non-designated")) {
				colType = type.TWO;
				deductible_indiv.append(" (Designated)/");
				deductible_family.append(" (Designated)/");
				index = temp_index + 3;
				deductible_indiv.append(tokens[index] + " (Non-designated)");
				index += 3;
				deductible_family.append(tokens[index] + " (Non-designated)");
				break;
			}
			temp_index++;
		}

		while (!((tokens[index - 1].equals("Employee") || tokens[index - 1].equals("Individual"))
				& isDollarValue(tokens[index]))) {
			index++;
		}
		oop_max_indiv.append(tokens[index]);
		index += 3;
		oop_max_family.append(tokens[index]);
		oon_oop_max_indiv.append("N/A");
		oon_oop_max_family.append("N/A");

		temp_index = index;
		while (temp_index < index + 10) {
			if (tokens[temp_index].contains("Out-of-network")) {
				index = temp_index + 2;
				oon_oop_max_indiv = new StringBuilder(tokens[index]);
				index += 3;
				oon_oop_max_family = new StringBuilder(tokens[index]);
				break;
			}
			if (tokens[temp_index].contains("Non-designated")) {
				oop_max_indiv.append(" (Designated)/");
				oop_max_family.append(" (Designated)/");
				index = temp_index + 3;
				oop_max_indiv.append(tokens[index] + " (Non-designated)");
				index += 3;
				oop_max_family.append(tokens[index] + " (Non-designated)");
				break;
			}
			temp_index++;
		}

		if (colType == type.ONE) {
			/*
			 * Dr visit copay
			 */
			while (!tokens[index + 1].contains("Primary")) {
				index++;
			}
			while (!isPercentage(tokens[index + 1]) & !isDollarValue(tokens[index + 1])) {
				dr_visit_copay.insert(0, tokens[index] + " ");
				index--;
			}

			/*
			 * Specialist visit copay
			 */
			while (!tokens[index + 1].contains("Specialist")) {
				index++;
			}
			while (!isPercentage(tokens[index + 1]) & !isDollarValue(tokens[index + 1])) {
				specialist_visit_copay.insert(0, tokens[index] + " ");
				index--;
			}

			/*
			 * Outpatient Diagnostic x-ray/lab
			 */

			while (!tokens[index + 1].contains("Diagnostic")) {
				index++;
			}
			while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
				if (tokens[index].contains("ray")) {
					x_ray = true;
				}
				outpatient_diagnostic_x_ray.insert(0, tokens[index] + " ");
				index--;
			}
			if (!x_ray) {
				outpatient_diagnostic_lab = outpatient_diagnostic_x_ray;
			} else {
				index--;
				while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
					outpatient_diagnostic_lab.insert(0, tokens[index] + " ");
					index--;
				}
			}
			/*
			 * Outpatient complex imaging
			 */
			while (!tokens[index + 1].contains("Imaging")) {
				index++;
			}

			while (!isPercentage(tokens[index + 1]) & !isDollarValue(tokens[index + 1])) {
				outpatient_complex_imaging.insert(0, tokens[index] + " ");
				index--;
			}
		} else {
			/*
			 * Dr visit copay
			 */
			while (!tokens[index + 1].contains("Primary")) {
				index++;
			}
			while (!isPercentage(tokens[index + 1]) & !isDollarValue(tokens[index + 1])) {
				dr_visit_copay.insert(0, tokens[index]);
				index--;
			}
			while (!isPercentage(tokens[index]) & !isDollarValue(tokens[index])) {
				index--;
			}
			dr_visit_copay.append(" (Designated)/" + tokens[index]);
			dr_visit_copay.append(" (Non-Designated)");

			/*
			 * Specialist visit copay
			 */
			while (!tokens[index + 1].contains("Specialist")) {
				index++;
			}
			while (!isPercentage(tokens[index + 1]) & !isDollarValue(tokens[index + 1])) {
				specialist_visit_copay.insert(0, tokens[index]);
				index--;
			}
			if (tokens[index].equals("coinsurance")) {
				index++;
			}
			specialist_visit_copay.append(" (Designated)/" + tokens[index]);
			specialist_visit_copay.append(" (Non-Designated)");

			/*
			 * Outpatient Diagnostic X-ray/Lab
			 */
			while (!tokens[index + 1].contains("Diagnostic")) {
				index++;
			}
			while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])
					& !tokens[index + 1].equals("No")) {
				if (tokens[index + 1].equals("No")) {
					outpatient_diagnostic_x_ray = new StringBuilder("No Charge");
					break;
				}
				outpatient_diagnostic_x_ray.insert(0, tokens[index] + " ");
				index--;
			}
			if (isPercentage(tokens[index]) || isDollarValue(tokens[index])) {
				outpatient_diagnostic_x_ray.append(" (Designated)/");
				outpatient_diagnostic_x_ray.append(tokens[index]);
				outpatient_diagnostic_x_ray.append(" (Non-Designated)");
				outpatient_diagnostic_lab = outpatient_diagnostic_x_ray;
			} else {
				index--;
				while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])
						& !tokens[index + 1].contains("No")) {
					outpatient_diagnostic_lab.insert(0, tokens[index] + " ");
					index--;
				}
				if (tokens[index + 1].contains("No")) {
					outpatient_diagnostic_lab = new StringBuilder("No Charge");
					lab_no_charge = true;
				}
				outpatient_diagnostic_x_ray.append(" (Designated)/");
				if (!lab_no_charge) {
					outpatient_diagnostic_lab.append(" (Designated)/");
				}
				index--;
				while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
					if (!lab_no_charge) {
						outpatient_diagnostic_lab.append(tokens[index]);
					}
					outpatient_diagnostic_x_ray.append(tokens[index]);
					index--;
				}
				outpatient_diagnostic_x_ray.append(" (Non-Designated)");
				if (!lab_no_charge) {
					outpatient_diagnostic_lab.append(" (Non-Designated)");
				}
			}
			/*
			 * Complex imaging
			 */
			while (!tokens[index + 1].contains("Imaging")) {
				index++;
			}

			while (!isPercentage(tokens[index + 1]) & !isDollarValue(tokens[index + 1])) {
				outpatient_complex_imaging.append(tokens[index]);
				index--;
			}
			outpatient_complex_imaging.append(" (Designated)/" + tokens[index]);
			outpatient_complex_imaging.append(" (Non-Designated)");

		}

		while (!tokens[index].contains("Preferred/") & !tokens[index].contains("Formulary/")) {
			index++;
		}
		System.out.println(index);
		while (count < 2) {
			while (!isPercentage(tokens[index]) & !isDollarValue(tokens[index])) {
				index--;
			}
			System.out.println(index);
			if (tokens[index + 1].equals("maximum")) {
				rx_max = true;
				if (count == 0) {
					rx_mail_max_string = tokens[index];
				} else {
					rx_max_string = tokens[index];
				}
			} else {
				if (count == 0) {
					rx_mail_copay.append(tokens[index]);
				} else {
					rx_copay.append(tokens[index]);
				}
				count++;
			}
			index--;
		}
		if (rx_max) {
			rx_copay.append(String.format(" (max %s)", rx_max_string));
			rx_mail_copay.append(String.format(" (max %s)", rx_mail_max_string));
		}
		rx_max = false;
		rx_copay.append("/");
		rx_mail_copay.append("/");
		count = 0;
		while ((!tokens[index].contains("Preferred") & !tokens[index].contains("Formulary"))
				|| !tokens[index + 1].contains("brand")) {
			index++;
		}
		while (count < 2) {
			while (!(isPercentage(tokens[index]) || isDollarValue(tokens[index]))) {
				index--;
			}
			System.out.println(index);
			if (tokens[index + 1].equals("maximum")) {
				rx_max = true;
				if (count == 0) {
					rx_mail_max_string = tokens[index];
				} else {
					rx_max_string = tokens[index];
				}
			} else {
				if (count == 0) {
					rx_mail_copay.append(tokens[index]);
				} else {
					rx_copay.append(tokens[index]);
				}
				count++;
			}
			index--;

		}
		if (rx_max) {
			rx_copay.append(String.format(" (max %s)", rx_max_string));
			rx_mail_copay.append(String.format(" (max %s)", rx_mail_max_string));
		}
		rx_max = false;
		rx_copay.append("/");
		rx_mail_copay.append("/");
		count = 0;
		while (!tokens[index].contains("Non-preferred") & !tokens[index].contains("Non-formulary")) {
			index++;
		}
		while (count < 2) {
			while (!(isPercentage(tokens[index]) || isDollarValue(tokens[index]))) {
				index--;
			}
			System.out.println(index);
			if (tokens[index + 1].equals("maximum")) {
				rx_max = true;
				if (count == 0) {
					rx_mail_max_string = tokens[index];
				} else {
					rx_max_string = tokens[index];
				}
			} else {
				if (count == 0) {
					rx_mail_copay.append(tokens[index]);
				} else {
					rx_copay.append(tokens[index]);
				}
				count++;
			}
			index--;
		}
		if (rx_max) {
			rx_copay.append(String.format(" (max %s)", rx_max_string));
			rx_mail_copay.append(String.format(" (max %s)", rx_mail_max_string));
		}

		/*
		 * Outpatient surgery
		 */
		while (!tokens[index - 2].contains("outpatient") & !tokens[index - 1].contains("surgery")) {
			index++;
		}

		while (!tokens[index].contains("Facility")) {
			index--;
		}
		while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
			outpatient_surgery.append(tokens[index]);
			index--;
		}
		if (colType == type.TWO) {
			outpatient_surgery.append(" (Designated)/" + tokens[index]);
			outpatient_surgery.append(" (Non-designated)");
		}

		while (!tokens[index].contains("Emergency")) {
			index++;
		}
		count = 0;
		while (count < 1) {
			while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
				er_copay.insert(0, tokens[index] + " ");
				index--;
			}
			if (!tokens[index].contains("after")) {
				count++;
			} else {
				er_copay.insert(0, tokens[index] + " ");
			}
			index--;
		}
		while (!tokens[index].contains("Urgent")) {
			index++;
		}
		while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
			urgent_care_copay.insert(0, tokens[index] + " ");
			index--;
		}

		/*
		 * In Patient Hospital
		 */
		while (!tokens[index - 2].contains("stay") & !tokens[index - 1].contains("surgery")) {
			index++;
		}

		while (!tokens[index].contains("Facility")) {
			index--;
		}
		while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
			in_patient_hospital.insert(0, tokens[index] + " ");
			index--;
		}
		if (colType == type.TWO) {
			in_patient_hospital.append("(Designated)/" + tokens[index]);
			in_patient_hospital.append(" (Non-designated)");
		}

		while (!tokens[index].contains("Rehabilitation")) {
			index++;
		}
		while (!isDollarValue(tokens[index + 1]) & !isPercentage(tokens[index + 1])) {
			physical_occupational_therapy.insert(0, tokens[index]);
			if (physical_occupational_therapy.indexOf("/") != -1) {
				removeString(physical_occupational_therapy, "/");
			}
			index--;
		}
		if (colType == type.TWO) {
			physical_occupational_therapy.append(" (Designated)/" + tokens[index]);
			physical_occupational_therapy.append(" (Non-designated)");
		}

		product_name = formatProductName(filename);
		deductible_indiv = formatDeductible(deductible_indiv);
		deductible_family = formatDeductible(deductible_family);
		oon_deductible_indiv = formatDeductible(oon_deductible_indiv);
		oon_deductible_family = formatDeductible(oon_deductible_family);
		dr_visit_copay = formatString(dr_visit_copay);
		specialist_visit_copay = formatString(specialist_visit_copay);
		er_copay = formatCare(er_copay);
		if (er_copay.toString().equals("No Charge") || !isPercentage(er_copay.toString())) {
			coinsurance.append("0%");
		} else {
			coinsurance.append(er_copay);
		}
		urgent_care_copay = formatCare(urgent_care_copay);
		rx_copay = formatString(rx_copay);
		rx_mail_copay = formatString(rx_mail_copay);
		oop_max_indiv = formatDeductible(oop_max_indiv);
		oop_max_family = formatDeductible(oop_max_family);
		oon_oop_max_indiv = formatDeductible(oon_oop_max_indiv);
		oon_oop_max_family = formatDeductible(oon_oop_max_family);
		in_patient_hospital = formatInpatientHospital(in_patient_hospital);
		in_patient_hospital = formatString(in_patient_hospital);
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
		outpatient_diagnostic_lab = formatString(outpatient_diagnostic_lab);
		outpatient_complex_imaging = formatString(outpatient_complex_imaging);
		physical_occupational_therapy = formatString(physical_occupational_therapy);
		System.out.println(outpatient_diagnostic_x_ray.toString());
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
		String[] delimiters = { "covered", "Not", "Urgent", "Emergency", ",", ".", "*", ";", "person", "copay/",
				"copay", "per", "visit", "Individual", "coinsurance", "service", "Rehabilitation", "outpatient",
				"Diagnostic", "after", "deductible", "does", "not", "apply", "facility", "Facility", "/day", "standing",
				"free", "for" };
		input = removeStrings(input, delimiters);
		if (input.length() > 0) {
			if (input.charAt(0) == ' ' || input.charAt(0) == '/') {
				input.deleteCharAt(0);
			}
		}
		if (input.indexOf("days1-5") != -1) {
			int index = input.indexOf("days1-5");
			input.insert(index + 4, " ");
		}
		if (input.indexOf(",") != -1) {
			int index = input.indexOf(",");
			if (input.charAt(index - 1) == ' ') {
				input.deleteCharAt(index - 1);
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

	public StringBuilder formatCare(StringBuilder s) {
		String[] delims = { "Emergency", "waived", "/", "copay", "visit", "Urgent", "coinsurance" };
		s = removeStrings(s, delims);
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

	public StringBuilder formatProductName(String x) {
		StringBuilder s = new StringBuilder(x);
		String[] delims = { "Summary", "Of", "Benefit", "Coverage" };
		s = removeStrings(s, delims);
		return s;
	}

	public StringBuilder formatDeductible(StringBuilder s) {
		String[] delims = { ";", "family", ".", ",", "what", "What" };
		s = removeStrings(s, delims);
		return s;
	}

	public StringBuilder formatInpatientHospital(StringBuilder s) {
		String[] delims = { "Not", "covered", "afted ded" };
		s = removeStrings(s, delims);
		return s;
	}

	public StringBuilder removeStrings(StringBuilder s, String[] delims) {
		for (String r : delims) {
			while (s.indexOf(r) != -1) {
				int index = s.indexOf(r);
				s.replace(index, index + r.length(), "");
			}
		}
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
