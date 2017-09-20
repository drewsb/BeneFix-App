package components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class PlanPDFMapper implements Parser {

	public PlanPDFMapper() {

	
	
	}

	@Override
	public ArrayList<Page> parse(File file, String filename)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		ArrayList<Page> pages = new ArrayList<Page>();
		
		String product_name = Formatter.removeFileExtension(filename);

		int carrier_id = 0;
		
		String start_date = "";
		String end_date = "";
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
		
		Page new_page = new MedicalPage(carrier_id, carrier_plan_id, start_date, end_date, product_name, plan_pdf_file_name,
				deductible_indiv, deductible_family, oon_deductible_indiv, oon_deductible_family, coinsurance,
				dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay, rx_copay, rx_mail_copay,
				oop_max_indiv, oop_max_family, oon_oop_max_indiv, oon_oop_max_family, in_patient_hospital,
				outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray, outpatient_complex_imaging,
				physical_occupational_therapy, "", service_zones, "", 0, non_tobacco_dict, tobacco_dict);
		pages.add(new_page);
		return pages;
	}

}
