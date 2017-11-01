package components;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import components.Main.Carrier;
import components.Main.State;
import de.DE_Aetna_Benefits;
import de.DE_Aetna_Rates;
import de.DE_Highmark_Rates;
import de.DE_UHC_Benefits;
import de.DE_UHC_Rates;
import components.Main.Plan;
import nj.*;
import pa.*;
import ca.*;
import oh.*;

public class Delegator extends SwingWorker<ArrayList<Page>, String> {

	final String otherSelection;

	final Carrier carrierType;

	final Plan planType;

	final int sheetIndex;

	final String quarter;

	final ArrayList<File> selectedPlans;

	final ArrayList<File> selectedRates;

	final ArrayList<File> selectedOutputs;

	final JTextArea textArea;

	final JProgressBar bar;

	final State state;

	ArrayList<Page> pages;

	HashMap<String, MedicalPage> pageMap;

	int index;

	int size;

	String start_date;

	String end_date;

	WPA wpaType;

	public enum WPA {
		HMK, HCA
	}

	public enum ParserType {
		plans, rates
	}

	public Delegator(final String otherSelection, final Carrier carrierType, Plan planType, final int sheetIndex,
			State state, final String quarter, final ArrayList<File> selectedPlans, final ArrayList<File> selectedRates,
			final ArrayList<File> selectedOutputs, final JTextArea textArea, final JProgressBar bar) {
		this.otherSelection = otherSelection;
		this.carrierType = carrierType;
		this.planType = planType;
		this.sheetIndex = sheetIndex;
		this.quarter = quarter;
		this.selectedPlans = selectedPlans;
		this.selectedRates = selectedRates;
		this.textArea = textArea;
		this.bar = bar;
		this.state = state;
		this.selectedOutputs = selectedOutputs;
	}

	@Override
	protected ArrayList<Page> doInBackground() throws Exception {
		pages = new ArrayList<Page>();
		pageMap = new HashMap<String, MedicalPage>();

		start_date = Formatter.getStartDate(quarter);
		end_date = Formatter.getEndDate(quarter);

		size = selectedPlans.size() + selectedRates.size();
		index = 0;
		if (!selectedPlans.isEmpty()) {
			parsePlans();
		}
		if (!selectedRates.isEmpty()) {
			wpaType = null;
			parseRates();
		}
		return pages;
	}

	public static String removeFileExtension(String input) {
		return input.substring(0, input.lastIndexOf("."));
	}

	public ArrayList<Page> getValue() throws Exception {
		return this.get();
	}

	@Override
	protected void process(final List<String> chunks) {
		// Updates the messages text area
		// System.out.println("WHEWHHEW");
		for (final String string : chunks) {
			textArea.append(string);
		}
	}

	public void parsePlans() throws EncryptedDocumentException, IOException, OpenXML4JException {
		ArrayList<Page> plan_pages;
		Parser plan_parser = getParser(ParserType.plans);
		for (File selectedPlan : selectedPlans) {
			String filename = selectedPlan.getName();
			plan_pages = plan_parser.parse(selectedPlan, filename);
			pages.addAll(plan_pages);

			String parsed = String.format("File: %s parsed\n", selectedPlan.getName());
			System.out.println(parsed);
			publish(parsed + "\n");
			index++;
			setProgress(100 * (index) / size);
		}
	}

	public void parseRates() throws EncryptedDocumentException, IOException, OpenXML4JException {
		ArrayList<Page> rate_pages;
		if (carrierType == Carrier.WPA) {
			if (selectedRates.get(0).getName().contains("HCA")) {
				wpaType = WPA.HCA;
			} else {
				wpaType = WPA.HMK;
			}
		}
		Parser plan_parser = getParser(ParserType.rates);
		for (File selectedRate : selectedRates) {
			String filename = removeFileExtension(selectedRate.getName());
			rate_pages = plan_parser.parse(selectedRate, filename);
			pages.addAll(rate_pages);
			String parsed = String.format("File: %s parsed\n", selectedRate.getName());
			System.out.println(parsed);
			publish(parsed + "\n");
			index++;
			setProgress(100 * (index) / size);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public Parser getParser(ParserType type) throws EncryptedDocumentException, InvalidFormatException, IOException {
		if (!otherSelection.equals("None")) {
			if (otherSelection.equals("PDF Mapper")) {
				return new PlanPDFMapper();
			}
			if (otherSelection.equals("Code Retriever")) {
				return new CodeRetriever();
			}
			if (otherSelection.equals("Base Rate Retriever")) {
				return new BaseRateRetriever();
			}
		}
		switch (type) {
		case plans:
			switch (planType) {
			case Medical:
				switch (state) {
				case PA:
					switch (carrierType) {
					case Aetna:
						return new PA_Aetna_AFA_Benefits(start_date, end_date);
					case UPMC:
						/*
						 * Needs to be finished return new
						 * PA_UPMC_Benefits(start_date,end_date); break; case
						 * CPA: /* No class created yet.
						 */
						break;
					case NEPA:
						/*
						 * No class created yet.
						 */
					case WPA:
						return new PA_WPA_Benefits(start_date, end_date);
					case IBC:
						return new PA_IBC_Benefits_Grant(start_date, end_date);
					case CBC:
						return new PA_CBC_Benefits(start_date, end_date);
					case Geisinger:
						return new PA_Geisinger_Benefits(start_date, end_date);
					case Oxford:
						return new PA_UHC_Benefits(start_date, end_date);
					case UHC:
						return new PA_UHC_Benefits(start_date, end_date);
					}
					break;
				case NJ:
					switch (carrierType) {
					case Aetna:
						return new NJ_Aetna_Benefits(start_date, end_date);
					case AmeriHealth:
						return new NJ_Amerihealth_Benefits(start_date, end_date);
					case Oxford:
						return new NJ_Oxford_Benefits(start_date, end_date);
					case Horizon:
						return new NJ_Horizon_Benefits(start_date, end_date);
					case Cigna:
						/*
						 * Too few plans to be automated yet. Needs to be
						 * finished return new
						 * NJ_Cigna_Benefits(start_date,end_date);
						 */
					}
					break;
				case OH:
					switch (carrierType) {
					case Anthem:
						return new OH_Anthem_Benefits(start_date, end_date);
					}
					break;
				case DE:
					switch (carrierType) {
					case Aetna:
						return new DE_Aetna_Benefits(start_date, end_date);
					case UHC:
						return new DE_UHC_Benefits(start_date, end_date);
					}
					break;
				}
			case Dental:
				switch (state) {
				case NJ:
					break;
				case PA:
					switch (carrierType) {
					case Delta:
						return new PA_Delta_Dental_Benefits();
					case Oxford:
						// DEPRECATED/NOT FINISHED
						return new PA_Oxford_Dental_Benefits();
					case United_Concordia:
						return new PA_United_Concordia_Dental_Benefits();
					case CPA:
						return new PA_Highmark_Dental_Benefits();
					case Aetna:
						return new PA_Aetna_Dental_Benefits();
					}
					break;
				}
				break;
			case Vision:
				break;
			}
		case rates:
			switch (planType) {
			case Medical:
				switch (state) {
				case PA:
					switch (carrierType) {
					case Aetna:
						return new PA_Aetna_Rates(start_date, end_date);
					case UPMC:
						return new PA_UPMC_Rates(start_date, end_date);
					case CPA:
						return new PA_CPA_Rates(sheetIndex, start_date, end_date);
					case NEPA:
						return new PA_NEPA_Rates(sheetIndex, start_date, end_date);
					case WPA:
						return new PA_WPA_Rates(wpaType, sheetIndex, start_date, end_date);
					case IBC:
						return new PA_IBC_Rates(start_date, end_date);
					case CBC:
						return new PA_CBC_Rates(start_date, end_date);
					case Geisinger:
						return new PA_Geisinger_Rates(start_date, end_date);
					case Oxford:
						return new PA_UHC_Rates(sheetIndex, start_date, end_date);
					}

					break;
				case NJ:
					switch (carrierType) {
					case Oxford:
						return new NJ_Oxford_Rates(start_date, end_date, sheetIndex);
					}
				case OH:
					switch (carrierType) {
					case Anthem:
						return new OH_Anthem_Rates(start_date, end_date);
					}
					break;
				case CA:
					return new CA_Rates(start_date, end_date);
				case DE:
					switch (carrierType) {
					case Aetna:
						return new DE_Aetna_Rates(start_date, end_date, sheetIndex);
					case UHC:
						return new DE_UHC_Rates(start_date, end_date, sheetIndex);
					case Highmark:
						return new DE_Highmark_Rates(start_date, end_date, sheetIndex);
					}
				}
			case Dental:
				switch (state) {
				case PA:
					switch (carrierType) {
					case Delta:
						// DEPRECATED/NOT FINISHED
						return new PA_Delta_Dental_Rates();
					}
					break;
				case NJ:
					break;
				}
				break;
			case Vision:
				break;
			}
		}

		return null;
	}

}