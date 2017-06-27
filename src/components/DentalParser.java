package components;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import components.Main.Carrier;
import components.Main.State;
import pa.PA_Delta_Dental_Benefits;

public class DentalParser extends SwingWorker<ArrayList<Page>, String> implements Parser {

	final Carrier carrierType;

	final int sheetIndex;

	final String quarter;

	final ArrayList<File> selectedPlans;

	final ArrayList<File> selectedRates;

	final ArrayList<File> selectedOutputs;

	final JTextArea textArea;

	final JProgressBar bar;

	final State state;

	public DentalParser(final Carrier type, final int sheetIndex, State state, final String quarter,
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
				switch (state) {
				case NJ:
					switch (carrierType) {
					case AmeriHealth:
						break;
					case Oxford:
						break;
					case Aetna:
						break;
					}
					break;
				case PA:
					switch (carrierType) {
					case UPMC:
						break;
					case Aetna:
						break;
					case WPA:
						break;
					case CBC:
						break;
					case Delta:
						PA_Delta_Dental_Benefits delta = new PA_Delta_Dental_Benefits(selectedPlan);
						break;
					}
					break;
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
				switch (state) {
				case PA:
					switch (carrierType) {
					case Delta:
						break;
					}
					break;
				case NJ:
					switch (carrierType) {
					case Aetna:
						break;
					default:
						break;
					}
				}
				publish("File parsed\n");
				setProgress(100 * (index + 1) / size);
				index++;
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
