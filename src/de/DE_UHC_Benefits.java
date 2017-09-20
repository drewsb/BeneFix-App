package de;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.Formatter;
import components.MedicalPage;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class DE_UHC_Benefits implements Parser {

	static String[] tokens;

	static String text;

	String start_date;

	String end_date;

	public DE_UHC_Benefits(String s_date, String e_date) {
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
		int temp_index = 2;
		int carrier_id = 18;

		Boolean hasHospital = false;
		Boolean hasNetwork = false;
		Boolean hasNonNetwork = false;

		StringBuilder carrier_plan_id = new StringBuilder("");
		StringBuilder start_date = new StringBuilder("2017/10/01");
		StringBuilder end_date = new StringBuilder("2017/12/31");
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

		while (!tokens[temp_index - 1].equals("year")) {
			temp_index++;
		}
		while (!tokens[temp_index].equals("Coverage")) {
			product_name.append(tokens[temp_index] + " ");
			temp_index++;
		}
		while (!tokens[temp_index - 1].equals("deductible?")) {
			temp_index++;
		}

		if (!tokens[temp_index].contains("Network")) {
			deductible_indiv.append(tokens[temp_index]);
			deductible_family = deductible_indiv;
			oon_deductible_indiv = deductible_indiv;
			oon_deductible_family = deductible_indiv;
		} else {
			int test_index = temp_index;
			while (!tokens[test_index].contains("Generally")) {
				if (tokens[test_index].equals("Network:")) {
					hasNetwork = true;
				}
				if (tokens[test_index].equals("Non-Network:")) {
					hasNonNetwork = true;
					break;
				}
				test_index++;
			}

			if (hasNetwork) {
				while (!tokens[temp_index - 1].contains("Network")) {
					temp_index++;
				}
				if (tokens[temp_index].equals("$0")) {
					deductible_indiv.append(tokens[temp_index]);
					deductible_family = deductible_indiv;
				} else {
					while (!tokens[temp_index + 1].contains("Individual")) {
						temp_index++;
					}

					deductible_indiv.append(tokens[temp_index++]);

					while (!Formatter.isDollarValue(tokens[temp_index])) {
						temp_index++;
					}
					deductible_family.append(tokens[temp_index]);
				}
			} else {
				deductible_indiv.append("n/a");
				deductible_family = deductible_indiv;
			}

			if (hasNonNetwork) {
				while (!tokens[temp_index - 1].contains("Network")) {
					temp_index++;
				}
				if (tokens[temp_index].equals("$0")) {
					oon_deductible_indiv.append(tokens[temp_index]);
					oon_deductible_family = deductible_indiv;
				} else {
					while (!tokens[temp_index + 1].contains("Individual")) {
						temp_index++;
					}

					oon_deductible_indiv.append(tokens[temp_index++]);

					while (!Formatter.isDollarValue(tokens[temp_index])) {
						temp_index++;
					}
					oon_deductible_family.append(tokens[temp_index]);
				}
			} else {
				oon_deductible_indiv.append("n/a");
				oon_deductible_family = deductible_indiv;
			}
		}
		while (!tokens[temp_index].equals("out-of-pocket")) {
			temp_index++;
		}
		temp_index += 5;

		if (!tokens[temp_index].contains("Network")) {
			oop_max_indiv.append(tokens[temp_index]);
			oop_max_family = deductible_indiv;
			oon_oop_max_indiv = deductible_indiv;
			oon_oop_max_family = deductible_indiv;
		} else {
			hasNetwork = false;
			hasNonNetwork = false;
			int test_index = temp_index;
			while (!tokens[test_index].contains("Limit")) {
				if (tokens[test_index].equals("Network:")) {
					hasNetwork = true;
				}
				if (tokens[test_index].equals("Non-Network:")) {
					hasNonNetwork = true;
					break;
				}
				test_index++;
			}

			if (hasNetwork) {
				while (!tokens[temp_index - 1].contains("Network")) {
					temp_index++;
				}
				if (tokens[temp_index].equals("$0")) {
					oop_max_indiv.append(tokens[temp_index]);
					oop_max_family = deductible_indiv;
				} else {
					while (!tokens[temp_index + 1].contains("Individual")) {
						temp_index++;
					}

					oop_max_indiv.append(tokens[temp_index++]);

					while (!Formatter.isDollarValue(tokens[temp_index])) {
						temp_index++;
					}
					oop_max_family.append(tokens[temp_index]);
				}
			} else {
				oop_max_indiv.append("n/a");
				oop_max_family = oop_max_indiv;
			}

			if (hasNonNetwork) {
				while (!tokens[temp_index - 1].contains("Network")) {
					temp_index++;
				}
				if (tokens[temp_index].equals("$0")) {
					oon_oop_max_indiv.append(tokens[temp_index]);
					oon_oop_max_family = oop_max_indiv;
				} else {
					while (!tokens[temp_index + 1].contains("Individual")) {
						temp_index++;
					}

					oon_oop_max_indiv.append(tokens[temp_index++]);

					while (!Formatter.isDollarValue(tokens[temp_index])) {
						temp_index++;
					}
					oon_oop_max_family.append(tokens[temp_index]);
				}
			} else {
				oon_oop_max_indiv.append("n/a");
				oon_oop_max_family = deductible_indiv;
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

		while (!tokens[temp_index].contains("Diagnostic")) {
			temp_index++;
		}

		while (!tokens[temp_index - 1].contains("Hospital")) {
			if (tokens[temp_index].equals("Free")) {
				hasHospital = true;
			}
			if (Formatter.isPercentage(tokens[temp_index]) & !hasHospital) {
				break;
			}
			temp_index++;
		}

		outpatient_diagnostic_lab.append(tokens[temp_index]);
		outpatient_diagnostic_x_ray = outpatient_diagnostic_lab;

		while (!tokens[temp_index - 1].contains("MRI")) {
			temp_index++;
		}
		if (hasHospital) {
			while (!tokens[temp_index - 1].contains("Hospital")) {
				temp_index++;
			}
		}
		outpatient_complex_imaging.append(tokens[temp_index]);

		for (int i = 0; i < 3; i++) {
			while (!tokens[temp_index - 1].contains("Retail") || !Formatter.isDollarValue(tokens[temp_index])) {
				temp_index++;
			}
			rx_copay.append(tokens[temp_index++] + "/");

			while (!tokens[temp_index - 1].equals("Mail-Order:") || !Formatter.isDollarValue(tokens[temp_index])) {
				temp_index++;
			}
			rx_mail_copay.append(tokens[temp_index] + "/");
		}

		while (!tokens[temp_index].equals("surgery")) {
			temp_index++;
		}
		if (Formatter.isPercentage(tokens[temp_index + 7]) || Formatter.isDollarValue(tokens[temp_index + 7])) {
			outpatient_surgery.append(tokens[temp_index + 7]);
		} else {
			while (!tokens[temp_index - 1].contains("Hospital")) {
				temp_index++;
			}
			outpatient_surgery.append(tokens[temp_index]);

			if (tokens[temp_index].equals("No")) {
				outpatient_surgery.append(" Charge");
			}
		}
		while (!tokens[temp_index].equals("Emergency")) {
			temp_index++;
		}
		er_copay.append(tokens[temp_index + 3]);

		while (!tokens[temp_index].equals("Urgent")) {
			temp_index++;
		}
		urgent_care_copay.append(tokens[temp_index + 2]);

		while (!tokens[temp_index].equals("Facility")) {
			temp_index++;
		}
		temp_index += 5;
		in_patient_hospital.append(tokens[temp_index]);
		if (tokens[temp_index].equals("No")) {
			in_patient_hospital.append(" Charge");
		}

		while (!tokens[temp_index - 2].equals("Rehabilitation")) {
			temp_index++;
		}
		physical_occupational_therapy.append(tokens[temp_index]);

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
		in_patient_hospital = formatString(in_patient_hospital);
		outpatient_diagnostic_lab = formatString(outpatient_diagnostic_lab);
		outpatient_surgery = formatString(outpatient_surgery);
		outpatient_diagnostic_x_ray = formatString(outpatient_diagnostic_x_ray);
		outpatient_complex_imaging = formatString(outpatient_complex_imaging);
		physical_occupational_therapy = formatString(physical_occupational_therapy);

		MedicalPage new_page = new MedicalPage(carrier_id, carrier_plan_id.toString(), start_date.toString(), end_date.toString(), product_name.toString(),
				plan_pdf_file_name.toString(), deductible_indiv.toString(), deductible_family.toString(),
				oon_deductible_indiv.toString(), oon_deductible_family.toString(), coinsurance.toString(),
				dr_visit_copay.toString(), specialist_visit_copay.toString(), er_copay.toString(),
				urgent_care_copay.toString(), rx_copay.toString(), rx_mail_copay.toString(), oop_max_indiv.toString(),
				oop_max_family.toString(), oon_oop_max_indiv.toString(), oon_oop_max_family.toString(),
				in_patient_hospital.toString(), outpatient_diagnostic_lab.toString(), outpatient_surgery.toString(),
				outpatient_diagnostic_x_ray.toString(), outpatient_complex_imaging.toString(),
				physical_occupational_therapy.toString(), "", service_zones.toString(), "DE", 0, non_tobacco_dict,
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
		if (input.indexOf(".") != -1) {
			index = input.indexOf(".");
			input.replace(index, index + 1, "");
		}
		if (input.indexOf("person") != -1) {
			index = input.indexOf("person");
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
		// if (s.subSequence(0, x).equals(s.subSequence(x + 1, y))
		// & s.subSequence(x + 1, y).equals(s.subSequence(y + 1, s.length()))) {
		// return new StringBuilder(s.subSequence(0, x));
		// }
		return new StringBuilder(s.subSequence(0, y));
	}

	public StringBuilder formatXRay(StringBuilder s) {
		int a = s.indexOf("/");
		int b = s.indexOf("(");
		if (a == -1 || b == -1) {
			return s;
		}
		int x = Math.min(a, b);
		return new StringBuilder(s.subSequence(0, x));
	}

	public StringBuilder formatLab(StringBuilder s) {
		int x = s.lastIndexOf("/");
		int y = s.lastIndexOf("(");
		int z = s.lastIndexOf(",");
		int end;
		if (z > x) {
			end = Math.min(y, z);
		} else {
			end = y;
		}
		return new StringBuilder(s.subSequence(x + 1, end));
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
