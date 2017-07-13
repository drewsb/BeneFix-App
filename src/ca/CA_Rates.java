package ca;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import components.PDFManager;
import components.Page;
import components.Parser;
import components.Formatter;
import components.MedicalPage;

/*
 * Primary parsing class used to parse a pdf and create and populate an excel sheet. Assumes pdf template is shown 
 */
public class CA_Rates implements Parser {
	
	static ArrayList<Page> products;

	static String[] tokens;

	static String text;

	static String start_date;

	static String end_date;

	final Set<String> network_prefixes = new HashSet<String>();

	final Set<String> plan_prefixes = new HashSet<String>();
	
	final Set<String> plan_suffixes = new HashSet<String>();
	
	final Set<String> carrier_prefixes = new HashSet<String>();
	
	final Set<String> metals = new HashSet<String>();

	public CA_Rates(String s_date, String e_date) throws IOException {
		products = new ArrayList<Page>();
		
		start_date = s_date;
		end_date = e_date;

		network_prefixes.add("Prudent");
		network_prefixes.add("Premier");
		network_prefixes.add("Performance");
		network_prefixes.add("Select");
		network_prefixes.add("Advantage");
		network_prefixes.add("SignatureValue");
		network_prefixes.add("PureCare");
		network_prefixes.add("Full");
		network_prefixes.add("WholeCare");
		network_prefixes.add("Alliance");
		network_prefixes.add("Salud");
		network_prefixes.add("Focus");

		plan_prefixes.add("HMO");
		plan_prefixes.add("PPO");
		plan_prefixes.add("HSP");
		plan_prefixes.add("EPO");
		
		plan_suffixes.add("A");
		plan_suffixes.add("B");
		plan_suffixes.add("C");
		plan_suffixes.add("D");
		
		carrier_prefixes.add("Anthem");
		carrier_prefixes.add("Health");
		carrier_prefixes.add("Kaiser");
		carrier_prefixes.add("Sharp");
		carrier_prefixes.add("Sutter");
		carrier_prefixes.add("UnitedHealthcare");
		carrier_prefixes.add("Western");

		
		metals.add("Bronze");
		metals.add("Silver");
		metals.add("Gold");
		metals.add("Platinum");
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
		int index = 0;
		int rating_area_index = 1;
		Boolean continued = false;
		
		StringBuilder metal = new StringBuilder("");
		
		int carrier_id = 1; // Needs to be changed
		StringBuilder carrier_plan_id = new StringBuilder("");
		StringBuilder start_date = new StringBuilder("");
		StringBuilder end_date = new StringBuilder("");
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

		while (index < tokens.length-150) {
			StringBuilder product_name = new StringBuilder("");
			HashMap<String, Double> non_tobacco_dict = new HashMap<String, Double>();
			HashMap<String, Double> tobacco_dict = new HashMap<String, Double>();

			if(!carrier_prefixes.contains(tokens[index]) || tokens[index+1].equals("Care")){
				if(!metals.contains(tokens[index]) || (tokens[index].equals("Health") & tokens[index+1].equals("Care"))){
					if(continued){
						rating_area_index++;
					}
					continued = !continued;
				}
				
				while (!tokens[index + 1].contains("0-18")) {
					index++;
				}
				metal = new StringBuilder(tokens[index]);
				while (!tokens[index - 1].equals("PLAN")) {
					index++;
				}
			}
		
			StringBuilder carrier = new StringBuilder("");
			while (!network_prefixes.contains(tokens[index])) {
				carrier.append(tokens[index] + "_");
				index++;
			}

			while(!plan_suffixes.contains(tokens[index+1])){
				index++;
			}

			StringBuilder plan = new StringBuilder("");
			plan.append(tokens[index] + "_");
			plan.append(tokens[index+1]);
			index+=2;
			
			product_name.append(carrier);
			product_name.append(metal + "_");
			//product_name.append(network + "_");
			product_name.append(plan);
			//product_name.append(String.format("Area_%d", rating_area_index));
			
			double rate = Double.parseDouble(tokens[index++]);
			non_tobacco_dict.put("0-18", rate);
			rate = Double.parseDouble(tokens[index]);
			non_tobacco_dict.put("19-20", rate);
			
			for(int i = 21; i < 65; i++){
				rate = Double.parseDouble(tokens[++index]);
				non_tobacco_dict.put(Integer.toString(i), rate);
			}
			rate = Double.parseDouble(tokens[index]);
			non_tobacco_dict.put("65+", rate);
			MedicalPage new_page = new MedicalPage(carrier_id, carrier_plan_id.toString(), "", "", product_name.toString(),
					plan_pdf_file_name.toString(), deductible_indiv.toString(), deductible_family.toString(),
					oon_deductible_indiv.toString(), oon_deductible_family.toString(), coinsurance.toString(),
					dr_visit_copay.toString(), specialist_visit_copay.toString(), er_copay.toString(),
					urgent_care_copay.toString(), rx_copay.toString(), rx_mail_copay.toString(), oop_max_indiv.toString(),
					oop_max_family.toString(), oon_oop_max_indiv.toString(), oon_oop_max_family.toString(),
					in_patient_hospital.toString(), outpatient_diagnostic_lab.toString(), outpatient_surgery.toString(),
					outpatient_diagnostic_x_ray.toString(), outpatient_complex_imaging.toString(),
					physical_occupational_therapy.toString(), Integer.toString(rating_area_index), service_zones.toString(), "CA", 0, non_tobacco_dict,
					tobacco_dict);
			
			new_page.printPage();
			products.add(new_page);
			System.out.println(index);
			index++;
		}
		

		return products;
	}

}
