package ca;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.MedicalPage;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class CA_Rates {

	static String[] tokens;

	static String text;
	
	public CA_Rates(File file) throws IOException {
		PDFManager pdfManager = new PDFManager();
		pdfManager.setFilePath(file.getAbsolutePath());
		text = pdfManager.ToText();
	}

	enum type {
		ONE, TWO
	}

	@SuppressWarnings("unused")
	public ArrayList<MedicalPage> parse(String filename) {
		this.tokens = text.split("[\\s\\r\\n]+"); // Split pdf text by spaces
													// and new line chars
		for(String s : tokens){
			System.out.println(s);
		}
		
		int carrier_id = 1; //Needs to be changed
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

		
		MedicalPage new_page = new MedicalPage(carrier_id, carrier_plan_id.toString(), "", "", product_name.toString(),
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
		return null;
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
