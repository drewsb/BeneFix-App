package uhc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import components.PDFManager;
import components.Page;

public class Oxford_NJ_Parser {
	
	PDFManager pdfmanager;
	ArrayList<Page> result;
	
	public Oxford_NJ_Parser(File file) throws IOException {
		PDFManager pdfmanager = new PDFManager(file);
		this.pdfmanager = pdfmanager;
		result = new ArrayList<Page>();
		int numPages = pdfmanager.getNumPages();
		System.out.println(numPages);
		for (int i = 1; i <= numPages; i++) {
			parse(i);
		}
	}
	
	public ArrayList<Page> getParsed() {
		return this.result;
	}
	
	
	public void parse(int pageNum) throws IOException {
		String text = pdfmanager.ToText(pageNum, pageNum);		
		System.out.println(text);
		
		String[] tokens = text.split(" |\n");
		int index = 0;
		
		while (!tokens[index].equals("Diagnostic")) {
			index++;
		}
		
		while (index < tokens.length) {
			if (tokens[index + 1].equals("HMO")) {
				break;
			}
			
			int carrier_id = 0;
			String carrier_plan_id = "";
			String product_name = "";
			String plan_pdf_file_name =  "";
			String deductible_indiv = "";
			String deductible_family = "";
			String oon_deductible_individual = "";
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
			String oon_oop_max_individual = "";
			String oon_oop_max_family = "";
			String in_patient_hospital = "";
			String outpatient_diagnostic_lab = "";
			String outpatient_surgery = "";
			String outpatient_diagnostic_x_ray = "";
			String outpatient_complex_imaging = "";
			String physical_occupational_therapy = "";
			String group_rating_area = "";
			String service_zones = "";
			
			//Stored info but not currently in use:
			String oon_coinsurance = "";
			String outpatient_services = "";
			String outpatient_services_hospital = "";
			String laboratory_services = "";
			String major_diagnostic_freestanding = "";
			String major_diagnostic_hospital = "";
			String other_radiology = "";
			String medical_deductible_type = "";
	
			
			while (!tokens[index].equals("Oxford®") && !tokens[index].equals("PPO")
					&& !tokens[index].equals("Primary")) {
				index++;
			}
			StringBuilder sb = new StringBuilder();
			
			//Plan name
			while (!tokens[index].equals("Garden") && !tokens[index].equals("Freedom") 
					&& !tokens[index].equals("Liberty")) {
				sb.append(tokens[index]);
				index++;
			}
			product_name = sb.toString();
			
			//Network/Access
			sb.setLength(0);
			while (!tokens[index].equals("Gated")) {
				sb.append(tokens[index] + " ");
				index++;
			}
			sb.append(tokens[index]);
			index++;
			
			//Deductible in network
			sb.setLength(0);
			deductible_indiv = tokens[index];
			try {
				int deductible_fam = Integer.parseInt(deductible_indiv.substring(1));
				deductible_family = "$" + Integer.toString(deductible_fam * 2);
			} catch (NumberFormatException e) {
				deductible_family = "N/A";
			}
			index++;
			
			//Deductible out of network
			oon_deductible_individual = tokens[index];
			try {
				int oon_deductible_fam = Integer.parseInt(oon_deductible_individual.substring(1));
				oon_deductible_family = "$" + Integer.toString(oon_deductible_fam * 2);
			} catch (NumberFormatException e) {
				oon_deductible_family = "N/A";
			}
			index++;
			
			//Coinsurance
			coinsurance = tokens[index];
			index++;
			
			//Out of network coinsurance
			oon_coinsurance = tokens[index];
			index++;
			
			//OOP Max 
			oop_max_indiv = tokens[index];
			try {
				int oop_max_fam = Integer.parseInt(oop_max_indiv.substring(1));
				oop_max_family = "$" + Integer.toString(oop_max_fam * 2);
			} catch (NumberFormatException e) {
				oop_max_family = "N/A";
			}
			index++;
			
			//OOP Max out of network
			oon_oop_max_individual = tokens[index];
			try {
				int oon_oop_max_fam = Integer.parseInt(oon_oop_max_individual.substring(1));
				oon_oop_max_family = "$" + Integer.toString(oon_oop_max_fam * 2);
			} catch (NumberFormatException e) {
				oon_oop_max_family = "N/A";
			}
			index++;
			
			//Doctor visit copay
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			}
			dr_visit_copay = sb.toString();
			index++;
			System.out.println("Dr visit copay: " + dr_visit_copay);
			
			//Specialist visit copay
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			}
			specialist_visit_copay = sb.toString();
			System.out.println("Specialist copay: " + specialist_visit_copay);
			index++;
			
			//Urgent care copay
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			} else if (tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			}
			urgent_care_copay = sb.toString();
			System.out.println("Urgent care copay: "  + urgent_care_copay);
			index++;
			
			if (tokens[index].equals("") || tokens[index].equals(" ")) {
				index++;
			}
			
			//ER copay
			sb.setLength(0);
			while (!tokens[index].equals("deductible") && !tokens[index].equals("coinsurance")) {
				sb.append(tokens[index] + " ");
				index++;
			}
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			} else if (tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			} else if (tokens[index + 4].equals("deductible")) {
				sb.append(" after deductible");
				index += 4;
			}
			er_copay = sb.toString();
			System.out.println("ER Copay: " + er_copay);
			index++;
			
			//Inpatient hospital
			sb.setLength(0);
			try {
				int num = Integer.parseInt(tokens[index + 1].substring(0, tokens[index+1].length() - 1));
				sb.append(num + "%");
			} catch (NumberFormatException e) {
				while (!tokens[index].equals("admission") && !tokens[index].equals("deductible")) {
					sb.append(tokens[index] + " ");
					index++;
				}
				sb.append(tokens[index] + " ");
				if (tokens[index + 6].equals("year")) {
					for (int i = 1; i < 7; i++) {
						sb.append(tokens[index + 1] + " ");
						index++;
					}
				} else if (index + 11 < tokens.length && tokens[index + 9].equals("year")) {
					for (int i = 1; i < 10; i++) {
						sb.append(tokens[index + 1] + " ");
						index++;
					}
					if (tokens[index + 2].equals("deductible")) {
						sb.append(" after deductible");
						index += 2;
					}
				} else if (index + 12 < tokens.length && tokens[index + 10].equals("year")) {
					for (int i = 1; i < 11; i++) {
						sb.append(tokens[index + 1] + " ");
						index++;
					}
					if (tokens[index + 2].equals("deductible")) {
						sb.append(" after deductible");
						index += 2;
					} else if (tokens[index + 3].equals("deductible")) {
						sb.append(" after deductible");
						index += 3;
					}
				}
			}
			in_patient_hospital = sb.toString();
			index++;
			System.out.println("Inpatient hosptial: " + sb.toString());
			
			if (tokens[index].equals("") || tokens[index].equals(" ")) {
				index++;
			}
			
			//Outpatient Services (Freestanding)
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				index += 2;
				sb.append(" after deductible");
			} else if (tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			}
			outpatient_services = sb.toString();
			index++;
			System.out.println("Outpatient services: " + outpatient_services);
			
			//Outpatient Services (Hospital)
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				index += 2;
				sb.append(" after deductible");
			} else if (tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			}
			outpatient_services_hospital = sb.toString();
			index++;
			
			//Laboratory services
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			} else if (tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			}
			laboratory_services = tokens[index];
			index++;
			
			//Major Diagnostic (Freestanding)
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			} else if (tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			}
			major_diagnostic_freestanding = sb.toString();
			index++;
			
			if (index + 5 < tokens.length) {
				for (int i = index; i < index + 5; i++) {
					System.out.println(tokens[i]);
				}
			}
			
			//Major Diagnostic (Hospital)
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			} else if (index + 3 < tokens.length && tokens[index + 3].equals("deductible")) {
				if (tokens[index + 1].equals("copayment")) {
					sb.append(" copayment after deductible");
				} else {
					sb.append(" after deductible");
				}
				index += 3;
			} else if (index + 4 < tokens.length && tokens[index + 4].equals("deductible")) {
				sb.append(" copayment after deductible");
				index += 4;
			} else if (index + 5 < tokens.length && tokens[index + 5].equals("deductible")) {
				sb.append( "copayment after deductible");
				index += 5;
			} else if (index + 6 < tokens.length && tokens[index + 6].equals("deductible")) {
				sb.append(" copayment after deductible");
				index += 6;
			} else if (index + 7 < tokens.length && tokens[index + 7].equals("deductible")) {
				sb.append(" copayment after deductible");
				index += 7;
			}
			major_diagnostic_hospital = sb.toString();
			index++;
			System.out.println("Major diagnostic hospital: " + major_diagnostic_hospital);
			
			//Other Radiology
			sb.setLength(0);
			sb.append(tokens[index]);
			if (tokens[index + 2].equals("deductible")) {
				sb.append(" after deductible");
				index += 2;
			} else if (index + 3 < tokens.length && tokens[index + 3].equals("deductible")) {
				sb.append(" after deductible");
				index += 3;
			}
			other_radiology = sb.toString();
			index++;
			System.out.println("Other radiology: " + other_radiology);
			
			//Medical deductible type
			medical_deductible_type = tokens[index];
			index++;
			
			System.out.println("Should be rx copay token: " + tokens[index]);
			
			//Rx copay
			sb.setLength(0);
			rx_copay = tokens[index++];
			String[] rx_vals = rx_copay.split("/");
			for (int j = 0; j < rx_vals.length; j++) {
				int val = Integer.parseInt(rx_vals[j].substring(1)) * 2;
				sb.append("$" + val + "/");
			}
			sb.delete(sb.length() - 1, sb.length());
			rx_mail_copay = sb.toString();
			
			Page new_page = new Page(carrier_id, carrier_plan_id, "", "", product_name, plan_pdf_file_name,
					deductible_indiv, deductible_family, oon_deductible_individual, oon_deductible_family, coinsurance,
					dr_visit_copay, specialist_visit_copay, er_copay, urgent_care_copay, rx_copay, rx_mail_copay,
					oop_max_indiv, oop_max_family, oon_oop_max_individual, oon_oop_max_family, in_patient_hospital,
					outpatient_diagnostic_lab, outpatient_surgery, outpatient_diagnostic_x_ray, outpatient_complex_imaging,
					physical_occupational_therapy, "", service_zones, "", 0, new HashMap<String, Double>(), new HashMap<String, Double>());
			result.add(new_page);
		}
	}
}
