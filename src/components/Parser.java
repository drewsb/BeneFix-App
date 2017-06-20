package components;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import components.Main.Carrier;
import components.Main.State;
import nj.*;
import pa.*;

public class Parser extends SwingWorker<ArrayList<Page>, String> {

	final Carrier carrierType;

	final int sheetIndex;

	final String quarter;

	final ArrayList<File> selectedPlans;

	final ArrayList<File> selectedRates;

	final ArrayList<File> selectedOutputs;

	final JTextArea textArea;

	final JProgressBar bar;

	final State state;

	public enum WPA {
		HMK, HCA
	}

	public Parser(final Carrier type, final int sheetIndex, State state, final String quarter,
			final ArrayList<File> selectedPlans, final ArrayList<File> selectedRates,
			final ArrayList<File> selectedOutputs, final JTextArea textArea, final JProgressBar bar) {
		this.carrierType = type;
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
		ArrayList<Page> pages = new ArrayList<Page>();
		HashMap<String, Page> pageMap = new HashMap<String, Page>();
		int size = selectedPlans.size() + selectedRates.size();
		int index = 0;
		String filename;
		if (!selectedPlans.isEmpty()) {
			for (File selectedPlan : selectedPlans) {
				filename = removeFileExtension(selectedPlan.getName());
				publish("Parsing: " + selectedPlan.getName() + ".\n");
				try {
					switch (state) {
					case NJ:
						switch (carrierType) {
						case AmeriHealth:
							Page amerihealth;
							NJ_Amerihealth_Rates ap = new NJ_Amerihealth_Rates(selectedPlan, 1);
							ap.printText();
							break;
						case Oxford:
							Page oxford;
							NJ_Oxford_Benefits op = new NJ_Oxford_Benefits(selectedPlan);
							oxford = op.parse(filename);
							pages.add(oxford);
							pageMap.put(filename, oxford);
							break;
						case Aetna:
							Page aetna;
							NJ_Aetna_Benefits aetna_parser = new NJ_Aetna_Benefits(selectedPlan);
							aetna = aetna_parser.parse(filename);
							pages.add(aetna);
							pageMap.put(filename, aetna);
							break;
						}
						break;
					case PA:
						switch (carrierType) {
						case UPMC:
							break;
						case Aetna:
							Page aetna_page;
							PA_Aetna_Benefits aetna_plan_parser = new PA_Aetna_Benefits(selectedPlan);
							aetna_page = aetna_plan_parser.parse(filename);
							pages.add(aetna_page);
							pageMap.put(aetna_page.product_name, aetna_page);
							break;
						case WPA:
							break;
						case CBC:
							Page cbc_page;
							PA_CBC_Benefits cbc_plan_parser = new PA_CBC_Benefits(selectedPlan);
							cbc_page = cbc_plan_parser.parse(filename);
							pages.add(cbc_page);
							break;
						}
						break;

					}

				} catch (IOException e1) {
					publish("Invalid file.\n");
					e1.printStackTrace();
				}
				String parsed = String.format("File: %s parsed\n", selectedPlan.getName());
				System.out.println(parsed);
				publish(parsed + "\n");
				index++;
				setProgress(100 * (index) / size);
			}
		}
		if (!selectedRates.isEmpty()) {
			for (File selectedRate : selectedRates) {

				publish("Parsing: " + selectedRate.getName() + ".\n");
				filename = removeFileExtension(selectedRate.getName());

				WPA wpaType;
				if (filename.contains("HCA")) {
					wpaType = WPA.HCA;
				} else {
					wpaType = WPA.HMK;
				}
				String start_date = "";
				String end_date = "";
				if (quarter.equals("Q1")) {
					start_date = "01/01/2017";
					end_date = "3/31/2017";
				}
				if (quarter.equals("Q2")) {
					start_date = "04/01/2017";
					end_date = "6/30/2017";
				}
				if (quarter.equals("Q3")) {
					start_date = "07/01/2017";
					end_date = "9/30/2017";
				}
				if (quarter.equals("Q4")) {
					start_date = "10/01/2017";
					end_date = "12/31/2017";
				}
				try {
					switch (state) {
					case PA:
						switch (carrierType) {
						case UPMC:
							pa.PA_UPMC_Page[] UPMC_pages;
							pa.PA_UPMC_Rates UPMC_parser = new pa.PA_UPMC_Rates(selectedRate, start_date, end_date);
							UPMC_pages = UPMC_parser.parse();
							pa.PA_UPMC_ExcelWriter.populateExcel(UPMC_pages, filename);
							break;
						case Aetna:
							Page[] aetna_pages;
							PA_Aetna_Rates aetna_parser = new pa.PA_Aetna_Rates(selectedRate, start_date, end_date);
							pages.addAll(aetna_parser.parse());
							break;
						case WPA:
							PA_WPA_Rates wpa_parser = new PA_WPA_Rates(selectedRate, sheetIndex, start_date, end_date);
							switch (wpaType) {
							case HCA:
								pages.addAll(wpa_parser.parseHCA());
								break;
							case HMK:
								pages.addAll(wpa_parser.parseHMK());
								break;
							}
							break;
						case NEPA:
							PA_NEPA_Rates nepa_parser = new PA_NEPA_Rates(selectedRate, sheetIndex, start_date,
									end_date);
							pages.addAll(nepa_parser.parse());
							break;
						case CPA:
							PA_CPA_Rates cpa_parser = new PA_CPA_Rates(selectedRate, sheetIndex, start_date, end_date);
							pages.addAll(cpa_parser.parse());
							break;
						case IBC:
							Page ibc_page;
							PA_IBC_Rates ibc_parser = new PA_IBC_Rates(selectedRate, start_date, end_date);
							ibc_page = ibc_parser.parse();
							pages.add(ibc_page);
							break;
						case CBC:
							Page cbc_page = null;
							PA_CBC_Rates cbc_parser = new PA_CBC_Rates(selectedRate, cbc_page, sheetIndex, quarter,
									quarter);
							cbc_page = cbc_parser.parse();
							pages.add(cbc_page);
							break;
						case Geisinger:
							String s_page = "25";
							String e_page = "97";
							pa.PA_Geisinger_Rates geisinger_parser = new pa.PA_Geisinger_Rates();
							pages.addAll(geisinger_parser.parse(selectedRate, start_date, end_date, s_page, e_page));
							break;
						}
						break;
					case NJ:
						switch (carrierType) {
						case Aetna:
							// Page[] aetna_pages;
							// PA_Aetna_Rates aetna_parser = new
							// pa.PA_Aetna_Rates(selectedRate, start_date,
							// end_date);
							// pages.addAll(aetna_parser.parse());
							// break;
							NJ_Aetna_Q2_Rates nj_aetna = new NJ_Aetna_Q2_Rates(selectedRate, start_date, end_date);
							pages.addAll(nj_aetna.getResults());
							break;
						default:
							NJ_All_Carriers_Rates parser_nj = new NJ_All_Carriers_Rates(selectedRate,
									selectedOutputs.get(0), carrierType, quarter, start_date, end_date);
							break;
						}
					}
					publish("File parsed\n");
					setProgress(100 * (index + 1) / size);
					index++;

				} catch (IOException e1) {
					publish("Invalid file.\n");
					e1.printStackTrace();
				}
			}
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

}
