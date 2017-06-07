package components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

import aetna.Aetna_Parser;
import aetna.Aetna_Plan_Parser;
import cbc.CBC_Parser;
import cbc.CBC_Plan_Parser;
import components.FileChooser.Carrier;

public class Parser extends SwingWorker<ArrayList<Page>, String> {

	final Carrier carrierType;

	final int sheetIndex;

	final String quarter;

	final ArrayList<File> selectedPlans;

	final ArrayList<File> selectedRates;

	final JTextArea textArea;

	final JProgressBar bar;

	public enum WPA {
		HMK, HCA
	}

	public Parser(final Carrier type, final int sheetIndex, final String quarter, 
			final ArrayList<File> selectedPlans, final ArrayList<File> selectedRates,
			final JTextArea textArea, final JProgressBar bar) {
	    this.carrierType = type;
	    this.sheetIndex = sheetIndex;
	    this.quarter = quarter;
	    this.selectedPlans = selectedPlans;
	    this.selectedRates = selectedRates;
	    this.textArea = textArea;
	    this.bar = bar;
	  }


	@Override
	protected ArrayList<Page> doInBackground() throws Exception {
		ArrayList<Page> pages = new ArrayList<Page>();
		HashMap<String, Page> pageMap = new HashMap<String, Page>();
		int size = selectedPlans.size() + selectedRates.size();
		int index = 0;
		String filename;
		if (!selectedPlans.isEmpty()) {
			System.out.println(carrierType.toString());
			for (File selectedPlan : selectedPlans) {
				filename = removeFileExtension(selectedPlan.getName());
				publish("Parsing: " + selectedPlan.getName() + ".\n");
				try {
					switch (carrierType) {
					case UPMC:
						break;
					case Aetna:
						Page aetna_page;
						Aetna_Plan_Parser aetna_plan_parser = new Aetna_Plan_Parser(selectedPlan);
						aetna_page = aetna_plan_parser.parse(filename);
						pages.add(aetna_page);
						pageMap.put(aetna_page.product_name, aetna_page);
						break;
					case WPA:
						break;
					case CBC:
						Page cbc_page;
						CBC_Plan_Parser cbc_plan_parser = new CBC_Plan_Parser(selectedPlan);
						cbc_page = cbc_plan_parser.parse(filename);
						pages.add(cbc_page);
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
				}
				else{
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
					switch (carrierType) {
					case UPMC:
						upmc.UPMC_Page[] UPMC_pages;
						upmc.UPMC_Parser UPMC_parser = new upmc.UPMC_Parser(selectedRate, start_date, end_date);
						UPMC_pages = UPMC_parser.parse();
						upmc.UPMC_ExcelWriter.populateExcel(UPMC_pages, filename);
						break;
					case Aetna:
						Page[] aetna_pages;
						Aetna_Parser aetna_parser = new aetna.Aetna_Parser(selectedRate, start_date,
								end_date);
						pages.addAll(aetna_parser.parse());
						break;
					case WPA:
						WPA_Parser wpa_parser = new WPA_Parser(selectedRate, sheetIndex, start_date, end_date);
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
						NEPA_Parser nepa_parser = new NEPA_Parser(selectedRate, sheetIndex, start_date, end_date);
						pages.addAll(nepa_parser.parse());
						break;
					case CPA:
						CPA_Parser cpa_parser = new CPA_Parser(selectedRate, sheetIndex, start_date, end_date);
						pages.addAll(cpa_parser.parse());
						break;
					case IBC:
						Page ibc_page;
						IBC_Parser ibc_parser = new IBC_Parser(selectedRate, start_date, end_date);
						ibc_page = ibc_parser.parse();
						pages.add(ibc_page);
					case CBC:
						Page cbc_page = null;
						CBC_Parser cbc_parser = new CBC_Parser(selectedRate, cbc_page, sheetIndex, quarter, quarter);
						cbc_page = cbc_parser.parse();
						pages.add(cbc_page);
					}
					publish("File parsed\n");
					index++;
					setProgress(100 * (index+1) / size);

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
		//System.out.println("WHEWHHEW");
	    for (final String string : chunks) {
	      textArea.append(string);
	    }
	  }

}
