package components;

import java.util.HashMap;
import java.util.Map;


/**
 * Page class for a PDF page. Holds necessary data to populate an excel sheet.
 */
public class Page {
	public int carrier_id;
	public String carrier_plan_id;
	public String start_date;
	public String end_date;
	public String product_name;
	public String plan_pdf_file_name;
	public String deductible_indiv;
	public String deductible_family;
	public String oon_deductible_individual;
	public String oon_deductible_family;
	public String coinsurance;
	public String dr_visit_copay;
	public String specialist_visit_copay;
	public String er_copay;
	public String urgent_care_copay;
	public String rx_copay;
	public String rx_mail_copay;
	public String oop_max_indiv;
	public String oop_max_family;
	public String oon_oop_max_individual;
	public String oon_oop_max_family;
	public String in_patient_hospital;
	public String outpatient_diagnostic_lab;
	public String outpatient_surgery;
	public String outpatient_diagnostic_x_ray;
	public String outpatient_complex_imaging;
	public String physical_occupational_therapy;
	public String group_rating_area;
	public String service_zones;
	public String state;
	public HashMap<String,Double> non_tobacco_dict;
	public HashMap<String,Double> tobacco_dict;
	public int page;

	public Page(int carrier_id, String carrier_plan_id, String start_date, String end_date, String product_name, String plan_pdf_file_name,
			String deductible_indiv, String deductible_family, String oon_deductible_individual, String oon_deductible_family,
	String coinsurance, String dr_visit_copay, String specialist_visit_copay, String er_copay, String urgent_care_copay,
	String rx_copay, String rx_mail_copay, String oop_max_indiv, String oop_max_family, String oon_oop_max_individual,
	String oon_oop_max_family, String in_patient_hospital, String outpatient_diagnostic_lab, String outpatient_surgery,
	String outpatient_diagnostic_x_ray, String outpatient_complex_imaging, String physical_occupational_therapy, String group_rating_area,
	String service_zones, String state, int page,  HashMap<String,Double> non_tob_dict, HashMap<String,Double> tob_dict){
		this.carrier_id = carrier_id;
		this.carrier_plan_id = carrier_plan_id;
		this.start_date = start_date;
		this.end_date = end_date;;
		this.product_name = product_name;
		this.plan_pdf_file_name = plan_pdf_file_name;
		this.deductible_indiv = deductible_indiv;
		this.deductible_family = deductible_family;
		this.oon_deductible_individual = oon_deductible_individual;
		this.oon_deductible_family = oon_deductible_family;
		this.coinsurance = coinsurance;
		this.dr_visit_copay = dr_visit_copay;
		this.specialist_visit_copay = specialist_visit_copay;
		this.er_copay = er_copay;
		this.urgent_care_copay = urgent_care_copay;
		this.rx_copay = rx_copay;
		this.rx_mail_copay = rx_mail_copay;
		this.oop_max_indiv = oop_max_indiv;
		this.oop_max_family = oop_max_family;
		this.oon_oop_max_individual = oon_oop_max_individual;
		this.oon_oop_max_family = oon_oop_max_family;
		this.in_patient_hospital = in_patient_hospital;
		this.outpatient_diagnostic_lab = outpatient_diagnostic_lab;
		this.outpatient_surgery =  outpatient_surgery;
		this.outpatient_diagnostic_x_ray =  outpatient_diagnostic_x_ray;
		this.outpatient_complex_imaging = outpatient_complex_imaging;
		this.physical_occupational_therapy = physical_occupational_therapy;
		this.group_rating_area = group_rating_area;
		this.service_zones = service_zones;
		this.state = state;
		this.non_tobacco_dict = non_tob_dict;
		this.tobacco_dict = tob_dict;
		this.page = page;
	}
	
	public Page() {
		this.carrier_id = 0;
		this.carrier_plan_id = "";
		this.start_date = "" ;
		this.end_date = "";
		this.product_name = "";
		this.plan_pdf_file_name = "";
		this.deductible_indiv = "";
		this.deductible_family = "";
		this.oon_deductible_individual = "";
		this.oon_deductible_family = "";
		this.coinsurance = "";
		this.dr_visit_copay = "";
		this.specialist_visit_copay = "";
		this.er_copay = "";
		this.urgent_care_copay = "";
		this.rx_copay = "";
		this.rx_mail_copay = "";
		this.oop_max_indiv = "";
		this.oop_max_family = "";
		this.oon_oop_max_individual = "";
		this.oon_oop_max_family = "";
		this.in_patient_hospital = "";
		this.outpatient_diagnostic_lab = "";
		this.outpatient_surgery =  "";
		this.outpatient_diagnostic_x_ray =  "";
		this.outpatient_complex_imaging = "";
		this.physical_occupational_therapy = "";
		this.group_rating_area = "";
		this.service_zones = "";
		this.state = "";
		this.non_tobacco_dict = new HashMap<String, Double>();
		this.tobacco_dict = new HashMap<String, Double>();
		this.page = 0;
	}
	
	/*
	 * Print method used for debugging purposes.
	 */
	public void printPage(){
		System.out.printf("Start date: %s\n", this.start_date);
		System.out.printf("End date: %s\n", this.end_date);
		System.out.printf("Page no.: %d\n", this.page);
		System.out.printf("Rating Area: %s\n", this.group_rating_area);
		System.out.printf("Coinsurance: %s\n", this.coinsurance);
		System.out.printf("Plan Name: %s\n", this.product_name);
		System.out.printf("OOP Maximum: %s\n", this.oop_max_indiv);
		System.out.printf("State: %s\n", this.state);
		System.out.printf("Non-Tobacco Rates\n");
		for( Map.Entry<String, Double> key : this.non_tobacco_dict.entrySet()){
			System.out.printf("Key: %s  |  Value: %.2f\n", key.getKey(), key.getValue());
		}
		System.out.printf("Tobacco Rates\n");
		for( Map.Entry<String, Double> key : this.tobacco_dict.entrySet()){
			System.out.printf("Key: %s  |  Value: %.2f\n", key.getKey(), key.getValue());
		}


	}
}
