package components;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import components.Main.Carrier;
import components.Main.Plan;
import components.Main.State;

/*
 * Uses Apache Poi package found at https://www.apache.org. 
 */
public class ExcelWriter {

	final String filename;
	
	final ArrayList<Page> plans;

	final Carrier carrierType;

	final State state;
	
	final Plan plan;
	
	final JTextArea log;
	

	public ExcelWriter(String filename, ArrayList<Page> plans, Carrier carrierType, State state, Plan plan, JTextArea log) {
		this.filename = filename;
		this.plans = plans;
		this.carrierType = carrierType;
		this.state = state;
		this.plan = plan;
		this.log = log;
	}

	/*
	 * Input: Array of page objects. Creates a new workbook sheet every
	 * compilation. First populates the excel sheet with template data, then the
	 * necessary data from the array of pages. Output file is called
	 * "BenefixData.xlsx".
	 */
	@SuppressWarnings("unchecked")
	public void populateExcel() throws IOException {
		XSSFWorkbook non_tob_workbook = new XSSFWorkbook();
		// Template data given by Benefix.

		switch (plan) {
		case Medical:
			ArrayList<MedicalPage> medical_products = new ArrayList<MedicalPage>();
			medical_products.addAll((Collection<? extends MedicalPage>) plans);
			populateMedicalExcel(medical_products, non_tob_workbook, false);
			if (medical_products.get(0).hasTobaccoRates) {
				XSSFWorkbook tob_workbook = new XSSFWorkbook();
				populateMedicalExcel(medical_products, tob_workbook, true);
			}
			break;
		case Dental:
			ArrayList<DentalPage> dental_products = new ArrayList<DentalPage>();
			dental_products.addAll((Collection<? extends DentalPage>) plans);
			populateDentalExcel(dental_products);
			break;
		case Vision:
			ArrayList<VisionPage> vision_products = new ArrayList<VisionPage>();
			vision_products.addAll((Collection<? extends VisionPage>) plans);
			populateVisionExcel(vision_products);
			break;
		}

	}

	public void populateMedicalExcel(ArrayList<MedicalPage> products, XSSFWorkbook workbook,
			Boolean parsingTobacco) throws FileNotFoundException, IOException {
		String[] templateData = { "carrier_id", "carrier_plan_id", "start_date", "end_date", "product_name",
				"plan_pdf_file_name", "deductible_indiv", "deductible_family", "oon_deductible_individual",
				"oon_deductible_family", "coinsurance", "dr_visit_copay", "specialist_visits_copay", "er_copay",
				"urgent_care_copay", "rx_copay", "rx_mail_copay", "oop_max_indiv", "oop_max_family",
				"oon_oop_max_individual", "oon_oop_max_family", "in_patient_hospital", "outpatient_diagnostic_lab",
				"outpatient_surgery", "outpatient_diagnostic_x_ray", "outpatient_complex_imaging",
				"physical_occupational_therapy", "state", "group_rating_areas", "service_zones", "zero_eighteen",
				"nineteen_twenty", "twenty_one", "twenty_two", "twenty_three", "twenty_four", "twenty_five",
				"twenty_six", "twenty_seven", "twenty_eight", "twenty_nine", "thirty", "thirty_one", "thirty_two",
				"thirty_three", "thirty_four", "thirty_five", "thirty_six", "thirty_seven", "thirty_eight",
				"thirty_nine", "forty", "forty_one", "forty_two", "forty_three", "forty_four", "forty_five",
				"forty_six", "forty_seven", "forty_eight", "forty_nine", "fifty", "fifty_one", "fifty_two",
				"fifty_three", "fifty_four", "fifty_five", "fifty_six", "fifty_seven", "fifty_eight", "fifty_nine",
				"sixty", "sixty_one", "sixty_two", "sixty_three", "sixty_four", "sixty_five_plus" };
		
		Sheet sheet = workbook.createSheet("BenefixData");

		int rowCount = 0;
		int colCount = 0;
		int max_age;

		if (carrierType == Carrier.IBC || carrierType == Carrier.Aetna) {
			max_age = 64;
		} else {
			max_age = 65;
		}

		// Populate with template data
		Row row = sheet.createRow(rowCount);
		for (String header : templateData) {
			Cell cell = row.createCell(colCount++);
			cell.setCellValue((String) header);
		}

		// Populate with data
		for (MedicalPage p : products) {
			if (p == null) {
				continue;
			}
			colCount = 0;
			row = sheet.createRow(++rowCount);
			Cell cell = row.createCell(colCount++);
			cell.setCellValue(p.carrier_id);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.carrier_plan_id);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.start_date);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.end_date);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.product_name);
			cell = row.createCell(colCount++);
			if (!p.plan_pdf_file_name.isEmpty()) {
				cell.setCellValue((String) p.plan_pdf_file_name + ".pdf");
			}
			cell = row.createCell(colCount++);
			cell.setCellValue(p.deductible_indiv);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.deductible_family);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.oon_deductible_indiv);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.oon_deductible_family);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.coinsurance);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.dr_visit_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.specialist_visit_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.er_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.urgent_care_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.rx_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.rx_mail_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.oop_max_indiv);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.oop_max_family);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.oon_oop_max_indiv);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.oon_oop_max_family);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.in_patient_hospital);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.outpatient_diagnostic_lab);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.outpatient_surgery);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.outpatient_diagnostic_x_ray);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.outpatient_complex_imaging);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.physical_occupational_therapy);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.state);
			cell = row.createCell(colCount++);
			cell.setCellValue(Formatter.formatRatingArea(p.group_rating_area));
			cell = row.createCell(colCount++);
			cell.setCellValue(p.service_zones);
			if (parsingTobacco) {
				if (p.tobacco_dict.containsKey("0-18") || p.tobacco_dict.containsKey("0-20")) {
					cell = row.createCell(colCount++);
					if (p.tobacco_dict.containsKey("0-18")) {
						cell.setCellValue(p.tobacco_dict.get("0-18"));
						cell = row.createCell(colCount++);
						cell.setCellValue(p.tobacco_dict.get("19-20"));
					} else {
						cell.setCellValue(p.tobacco_dict.get("0-20"));
						cell = row.createCell(colCount++);
						cell.setCellValue(p.tobacco_dict.get("0-20"));
					}
					for (int i = 21; i < 65; i++) {
						cell = row.createCell(colCount++);
						String index = String.format("%d", i);
						cell.setCellValue(p.tobacco_dict.get(index));
					}
					cell = row.createCell(colCount++);
					cell.setCellValue(p.tobacco_dict.get("65+"));
				}
			} else {
				if (p.non_tobacco_dict.containsKey("0-18") || p.non_tobacco_dict.containsKey("0-20")) {
					cell = row.createCell(colCount++);
					if (p.non_tobacco_dict.containsKey("0-18")) {
						cell.setCellValue(p.non_tobacco_dict.get("0-18"));
						cell = row.createCell(colCount++);
						cell.setCellValue(p.non_tobacco_dict.get("19-20"));
					} else {
						cell.setCellValue(p.non_tobacco_dict.get("0-20"));
						cell = row.createCell(colCount++);
						cell.setCellValue(p.non_tobacco_dict.get("0-20"));
					}
					for (int i = 21; i < 65; i++) {
						cell = row.createCell(colCount++);
						String index = String.format("%d", i);
						cell.setCellValue(p.non_tobacco_dict.get(index));
					}
					cell = row.createCell(colCount++);
					cell.setCellValue(p.non_tobacco_dict.get("65+"));
				}
			}
		}

		for (int x = 0; x < row.getPhysicalNumberOfCells(); x++) {
			sheet.autoSizeColumn(x);
		}
		String outputName = "";
		if (!parsingTobacco) {
			outputName = String.format("%s_data.xlsx", filename);
		} else {
			outputName = String.format("%s_tobacco_data.xlsx", filename);
		}
		// Create output file
		try (FileOutputStream outputStream = new FileOutputStream(outputName)) {
			workbook.write(outputStream);
		}
		String output = String.format("Output file: %s\n", outputName);
		log.append(output);
		workbook.close();
	}

	public static void populateMedicalTobaccoRates() {

	}

	public void populateDentalExcel(ArrayList<DentalPage> products) throws FileNotFoundException, IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("BenefixData");
		String[] templateData = { "group", "carrier", "carrier_id", "product_name", "sic_level", "start_date",
				"end_date", "states", "group_rating_areas", "zip_codes", "contribution_type", "minimum_enrolled",
				"minimum_participation", "class_I_diagnostic_&_preventive", "class_II_basic", "class_III_major",
				"endodonitcs", "periodontics", "annual_max", "office_visit_copay", "deductible_ind_fam", "orthodontics",
				"orthodonitics_lifetime_maximum", "waiting_period", "R&C / MAC", "One Tier", "Two Tier E", "Two Tier F",
				"Three Tier E", "Three Tier ED", "Three Tier F", "Four Tier E", "Four Tier EA", "Four Tier EC",
				"Four Tier F" };

		int rowCount = 0;
		int colCount = 0;

		Row row = sheet.createRow(rowCount);
		for (String header : templateData) {
			Cell cell = row.createCell(colCount++);
			cell.setCellValue((String) header);
		}

		for (Page product : products) {
			if (product == null) {
				continue;
			}
			DentalPage p = (DentalPage) product;
			colCount = 0;
			row = sheet.createRow(++rowCount);
			Cell cell = row.createCell(colCount++);
			cell.setCellValue(p.group);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.carrier);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.carrier_id);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.product_name);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.sic_level);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.start_date);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.end_date);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.states);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.group_rating_areas);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.zip_codes);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.contribution_type);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.minimum_enrolled);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.minimum_participation);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.class_I_diagnostic_preventive);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.class_II_basic);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.class_III_major);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.endodonitcs);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.periodontics);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.annual_max);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.office_visit_copay);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.deductible_ind_fam);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.orthodontics);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.orthodonitics_lifetime_maximum);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.waiting_period);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.rc_mac);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.one_tier);
			cell = row.createCell(colCount++);
			cell.setCellValue((String) p.two_tier_e);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.two_tier_f);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.three_tier_e);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.three_tier_f);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.four_tier_e);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.four_tier_ea);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.four_tier_ec);
			cell = row.createCell(colCount++);
			cell.setCellValue(p.four_tier_f);
		}

		String dentalFilename = filename + "_Dental";

		String outputName = String.format("%s_data.xlsx", dentalFilename);
		// Create output file
		try (FileOutputStream outputStream = new FileOutputStream(outputName)) {
			workbook.write(outputStream);
		}
		workbook.close();
	}

	public static void populateVisionExcel(ArrayList<VisionPage> products) throws FileNotFoundException, IOException {
	}

}