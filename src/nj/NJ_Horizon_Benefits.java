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
public class NJ_Horizon_Benefits implements Parser {

	static String[] tokens;

	static String text;
	
	static String start_date;
	
	static String end_date;

	public enum docType {
		tier, oon, in, DEFAULT
	}

	public NJ_Horizon_Benefits(String s_date, String e_date) throws IOException {
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

		String temp;
		docType type = docType.DEFAULT;
		Boolean oon_oop_deduct = false;

		int temp_index = 2;
		int carrier_id = 18;
		StringBuilder carrier_plan_id = new StringBuilder("");
		StringBuilder product_name = new StringBuilder(filename);
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

		while (!tokens[temp_index - 1].equals("deductible?")) {
			temp_index++;
		}

		deductible_indiv.append(tokens[temp_index]);
		temp_index++;
		int temp_index2 = temp_index;
		while (temp_index2 < temp_index + 10) {
			if (tokens[temp_index2].equals("Tier")) {
				type = docType.tier;
				break;
			}
			if (tokens[temp_index2].equals("out-of-network")) {
				type = docType.oon;
				break;
			}
			if (tokens[temp_index2].equals("in-network")) {
				type = docType.in;
				break;
			}
			temp_index2++;
		}
		switch (type) {
		case tier:
			while (!tokens[temp_index].equals("family")) {
				deductible_family.append(tokens[temp_index]);
				temp_index++;
			}
			deductible_indiv.append(" (Tier 1)/");
			deductible_family.append(" (Tier 1)/");
			while (!tokens[temp_index].contains("providers")) {
				temp_index++;
			}
			temp_index++;
			deductible_indiv.append(tokens[temp_index++] + " (Tier 2)");
			deductible_family.append(tokens[temp_index] + " (Tier 2)");
			oon_deductible_indiv.append("N/A");
			oon_deductible_family.append("N/A");
			break;
		case oon:
			oon_deductible_indiv = deductible_indiv;
			deductible_indiv = new StringBuilder("");
			while (!tokens[temp_index].equals("family")) {
				oon_deductible_family.append(tokens[temp_index]);
				temp_index++;
			}
			deductible_indiv.append("N/A");
			deductible_family.append("N/A");
			break;
		case in:
			while (!tokens[temp_index].equals("family")) {
				deductible_family.append(tokens[temp_index]);
				temp_index++;
			}
			oon_deductible_indiv.append("N/A");
			oon_deductible_family.append("N/A");
			break;
		case DEFAULT:
			deductible_family.append("N/A");
			oon_deductible_indiv.append("N/A");
			oon_deductible_family.append("N/A");
			break;
		}

		while (!tokens[temp_index].contains("out–of–")) {
			temp_index++;
		}
		while (!tokens[temp_index - 1].equals("providers")) {
			temp_index++;
		}
		System.out.println(type);
		if (type == docType.tier) {
			oop_max_indiv.append(tokens[temp_index] + " (Tier 1)/");
			oop_max_family.append(tokens[++temp_index] + " (Tier 1)/");
			while (!tokens[temp_index - 1].contains("providers")) {
				temp_index++;
			}
			oop_max_indiv.append(tokens[temp_index] + " (Tier 2)");
			oop_max_family.append(tokens[++temp_index] + " (Tier 2)");
			oon_oop_max_indiv.append("N/A");
			oon_oop_max_family.append("N/A");
		} else {
			oop_max_indiv.append(tokens[temp_index++]);
			oop_max_family.append(tokens[temp_index]);
			temp_index2 = temp_index;
			while (temp_index2 < temp_index + 10) {
				if (tokens[temp_index2 - 1].equals("providers")) {
					oon_oop_max_indiv.append(tokens[temp_index++]);
					oon_oop_max_family.append(tokens[temp_index]);
					oon_oop_deduct = true;
				}
				temp_index2++;
			}
			if (!oon_oop_deduct) {
				oon_oop_max_indiv.append("N/A");
				oon_oop_max_family.append("N/A");
			}
		}

		while (!tokens[temp_index].equals("Primary")) {
			temp_index++;
		}
		while (!tokens[temp_index - 1].equals("illness")) {
			temp_index++;
		}
		dr_visit_copay.append(tokens[temp_index]);

		while (!tokens[temp_index].equals("Specialist")) {
			temp_index++;
		}
		specialist_visit_copay.append(tokens[temp_index + 2]);
		while (!tokens[temp_index].equals("Diagnostic")) {
			temp_index++;
		}
		System.out.println(temp_index);
		System.out.println(tokens[temp_index]);
		if (type == docType.tier) {
			for (int i = 0; i < 4; i++) {
				while (!tokens[temp_index - 1].contains("Facility")) {
					temp_index++;
				}
				while (!tokens[temp_index].equals("Radiology") & !tokens[temp_index].equals("Laboratory")
						& !tokens[temp_index - 1].contains(".")) {
					if (i == 0) {
						outpatient_diagnostic_lab.append(tokens[temp_index] + " ");
					} else if (i == 1) {
						outpatient_diagnostic_x_ray.append(tokens[temp_index] + " ");
					} else if (i == 2) {
						outpatient_diagnostic_lab.append(tokens[temp_index] + " ");
					} else if (i == 3) {
						outpatient_diagnostic_x_ray.append(tokens[temp_index] + " ");
					}
					temp_index++;
				}
				if (i == 0) {
					outpatient_diagnostic_lab.append("(Tier 1)/");
				} else if (i == 1) {
					outpatient_diagnostic_x_ray.append("(Tier 1)/");
				} else if (i == 2) {
					outpatient_diagnostic_lab.append("(Tier 2)");
				} else if (i == 3) {
					outpatient_diagnostic_x_ray.append("(Tier 2)");
				}
			}
		} else {
			for (int i = 0; i < 2; i++) {
				while (!tokens[temp_index - 1].contains("Facility") & !tokens[temp_index - 1].equals("Outpatient:")) {
					temp_index++;
				}
				while (!tokens[temp_index].equals("Radiology") & !tokens[temp_index].equals("Laboratory")
						& !tokens[temp_index - 1].contains(".") & !tokens[temp_index - 1].contains("visit")
						& !tokens[temp_index - 1].contains("deductible")) {
					if (i == 0) {
						outpatient_diagnostic_lab.append(tokens[temp_index] + " ");
					}
					if (i == 1) {
						outpatient_diagnostic_x_ray.append(tokens[temp_index] + " ");
					}
					temp_index++;
				}
			}
		}
		System.out.println(temp_index);
		System.out.println(tokens[temp_index]);
		while (!tokens[temp_index].equals("MRIs)")) {
			temp_index++;
		}
		while (!isPercentage(tokens[temp_index]) & !isDollarValue(tokens[temp_index]) ) {
			temp_index++;
		}
		while (!tokens[temp_index].contains("Office") & !tokens[temp_index + 1].contains("facility")
				& !tokens[temp_index - 1].contains(".") & !tokens[temp_index - 1].equals("deductible")) {
			outpatient_complex_imaging.append(tokens[temp_index] + " ");
			temp_index++;
		}

		if (type == docType.tier) {
			while (!tokens[temp_index - 2].equals("Generic")) {
				temp_index++;
			}
			if (isPercentage(tokens[temp_index])) {
				rx_copay.append(tokens[temp_index]);
				rx_mail_copay.append(tokens[temp_index]);
			}
		} else {
			while (!tokens[temp_index - 2].equals("Generic")) {
				temp_index++;
			}

			rx_copay.append(tokens[temp_index] + "/");
			temp_index += 2;
			rx_mail_copay.append(tokens[temp_index] + "/");

			while (!tokens[temp_index - 3].equals("Preferred")) {
				temp_index++;
			}

			rx_copay.append(tokens[temp_index] + "/");
			temp_index += 2;
			rx_mail_copay.append(tokens[temp_index] + "/");

			while (!tokens[temp_index - 3].equals("Non-preferred")) {
				temp_index++;
			}

			rx_copay.append(tokens[temp_index]);
			temp_index += 2;
			rx_mail_copay.append(tokens[temp_index]);
		}
		while (!tokens[temp_index].equals("surgery")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index]) & !isPercentage(tokens[temp_index])) {
			temp_index++;
		}
		outpatient_surgery.append(tokens[temp_index]);

		if (tokens[temp_index].equals("No")) {
			outpatient_surgery.append(" Charge");
		}

		while (!tokens[temp_index].equals("Emergency")) {
			temp_index++;
		}
		er_copay.append(tokens[temp_index + 3]);

		while (!tokens[temp_index].equals("Urgent"))

		{
			temp_index++;
		}
		urgent_care_copay.append(tokens[temp_index + 2]);

		while (!tokens[temp_index].equals("Facility"))

		{
			temp_index++;
		}
		temp_index += 5;
		in_patient_hospital.append(tokens[temp_index]);
		if (tokens[temp_index].equals("No")) {
			in_patient_hospital.append(" Charge");
		}

		while (!tokens[temp_index].equals("Rehabilitation")) {
			temp_index++;
		}
		while (!isDollarValue(tokens[temp_index]) & !isPercentage(tokens[temp_index]) ) {
			temp_index++;
		}
		physical_occupational_therapy.append(tokens[temp_index]);

		/*
		 * Incomplete: Inpatient hospital, outpatient surgery, outpatient
		 * diagnostic lab, coinsurance
		 */
		deductible_indiv = formatDeductible(deductible_indiv);
		deductible_family = formatDeductible(deductible_family);
		oon_deductible_indiv = formatDeductible(oon_deductible_indiv);
		oon_deductible_family = formatDeductible(oon_deductible_family);
		dr_visit_copay = formatString(dr_visit_copay);
		specialist_visit_copay = formatString(specialist_visit_copay);
		er_copay = formatString(er_copay);
		if (er_copay.toString().equals("No Charge") || !isPercentage(er_copay.toString())) {
			coinsurance.append("0%");
		} else {
			coinsurance.append(er_copay);
		}
		urgent_care_copay = formatString(urgent_care_copay);
		rx_copay = formatString(rx_copay);
		rx_mail_copay = formatString(rx_mail_copay);
		rx_copay = formatRx(rx_copay);
		rx_mail_copay = formatRx(rx_mail_copay);
		oop_max_indiv = formatDeductible(oop_max_indiv);
		oop_max_family = formatDeductible(oop_max_family);
		oon_oop_max_indiv = formatDeductible(oon_oop_max_indiv);
		oon_oop_max_family = formatDeductible(oon_oop_max_family);
		in_patient_hospital = formatString(in_patient_hospital);
		outpatient_diagnostic_lab = formatString(outpatient_diagnostic_lab);
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
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
		int index;
		if (input.toString().equals("No") || input.toString().equals("No ")) {
			input = new StringBuilder("No Charge");
		}
		if (input.lastIndexOf("/") != -1 & input.lastIndexOf("/") == input.length() - 1) {
			index = input.lastIndexOf("/");
			input.replace(index, index + 1, "");
		}
		if (input.lastIndexOf(";") != -1 & input.lastIndexOf(";") == input.length() - 1) {
			index = input.lastIndexOf(";");
			input.replace(index, index + 1, "");
		}
		if (input.indexOf(",") != -1) {
			index = input.indexOf(",");
			input.replace(index, index + 1, "");
		}
		while (input.indexOf(".") != -1) {
			index = input.indexOf(".");
			input.replace(index, index + 1, "");
		}
		if (input.indexOf("person") != -1) {
			index = input.indexOf("person");
			input.replace(index, index + 6, "");
		}
		while (input.indexOf("/visit") != -1) {
			index = input.indexOf("/visit");
			input.replace(index, index + 6, "");
		}
		if (input.indexOf("copay") != -1) {
			index = input.indexOf("copay");
			input.replace(index, index + 5, "");
		}
		if (input.indexOf("No") != -1 & input.indexOf(" ") == -1) {
			input.insert(input.indexOf("o") + 1, " ");
		}
		return new StringBuilder(input);
	}

	public StringBuilder formatRx(StringBuilder s) {
		int x = s.indexOf("/");
		int y = s.lastIndexOf("/");
		if (x != -1 & y != -1) {
			if (s.subSequence(0, x).equals(s.subSequence(x + 1, y))
					& s.subSequence(x + 1, y).equals(s.subSequence(y + 1, s.length()))) {
				return new StringBuilder(s.subSequence(0, x));
			}
		}
		return s;
	}

	public StringBuilder formatDeductible(StringBuilder input) {
		int index;
		while (input.indexOf("person/") != -1) {
			index = input.indexOf("person/");
			input.replace(index, index + 7, "");
		}
		return input;
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

}
