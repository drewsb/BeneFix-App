package components;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;
import javax.swing.UIManager;

public class Main extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	static private final String newline = "\n";

	JButton planButton, rateButton, parseButton, outputButton, compareButtonF1, compareButtonF2, compareButton,
			tokenizeButton, clearButton;
	JComboBox<String> typeBox;
	JComboBox<String> carrierBox;
	JComboBox<String> otherBox;
	JComboBox<String> sheetBox;
	JComboBox<String> dateBox;
	JComboBox<String> stateBox;
	JComboBox<String> selectionBox;
	JProgressBar progressBar;
	public JTextArea log;
	JFileChooser fc;
	Delegator delegator;
	ArrayList<File> selectedPlans;
	ArrayList<File> selectedRates;
	ArrayList<File> selectedTokenFiles;
	ArrayList<File> selectedOutputs;
	ArrayList<File> compareFiles1;
	ArrayList<File> compareFiles2;
	ArrayList<Page> pages;
	String filename;
	Boolean done;
	String year;
	String selectedOperation;
	State selectedState;
	Carrier carrierType;
	Plan planType;
	HashMap<String, Set<String>> medicalCarriers;
	HashMap<String, Set<String>> dentalCarriers;
	HashMap<String, Set<String>> sourceCarriers;

	public enum Carrier {
		Anthem, UPMC, Aetna, CPA, Highmark, NEPA, WPA, IBC, CBC, AmeriHealth, UHC, Oxford, Cigna, Horizon, Geisinger, Delta, United_Concordia, NONE
	}

	public enum State {
		NJ, PA, CA, OH, DE
	}

	public enum Plan {
		Medical, Dental, Vision
	}

	public Main() {
		super(new BorderLayout());
		medicalCarriers = new HashMap<String, Set<String>>();
		dentalCarriers = new HashMap<String, Set<String>>();
		sourceCarriers = medicalCarriers;
		year = "2017";

		selectedPlans = new ArrayList<File>();
		selectedRates = new ArrayList<File>();
		compareFiles1 = new ArrayList<File>();
		compareFiles2 = new ArrayList<File>();
		selectedOutputs = new ArrayList<File>();
		selectedTokenFiles = new ArrayList<File>();
		pages = new ArrayList<Page>();

		// Create the log first, because the action listeners
		// need to refer to it.
		log = new JTextArea(20, 100);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		// Create a file chooser
		fc = new JFileChooser();

		fc.setMultiSelectionEnabled(true);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		Dimension dim = new Dimension();
		dim.setSize(300, 20);
		progressBar.setPreferredSize(dim);

		// Create the open button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).

		planButton = new JButton("Upload plans",
				createImageIcon("/Users/drewboyette/Documents/Fall 2016/CIS 121/BeneFixApp/src/components/file.png"));
		planButton.addActionListener(this);

		rateButton = new JButton("Upload rates",
				createImageIcon("/Users/drewboyette/Documents/Fall 2016/CIS 121/BeneFixApp/src/components/file.png"));
		rateButton.addActionListener(this);

		outputButton = new JButton("Output file", createImageIcon("directory"));
		outputButton.addActionListener(this);

		compareButtonF1 = new JButton("First file");
		compareButtonF1.addActionListener(this);

		compareButtonF2 = new JButton("Second file");
		compareButtonF2.addActionListener(this);

		compareButton = new JButton("Perform the operation");
		compareButton.addActionListener(this);

		clearButton = new JButton("Clear plans");
		clearButton.addActionListener(this);

		// Options for the JComboBox

		String[] PAcorps = { "Aetna", "UPMC", "CPA", "NEPA", "WPA", "IBC", "CBC", "Geisinger", "UHC" };
		Set<String> PAcarriers = new HashSet<String>(Arrays.asList(PAcorps));
		medicalCarriers.put("PA", PAcarriers);

		String[] NJcorps = { "AmeriHealth", "Aetna", "Cigna", "Horizon", "Oxford" };
		Set<String> NJcarriers = new HashSet<String>(Arrays.asList(NJcorps));
		medicalCarriers.put("NJ", NJcarriers);

		String[] CAcorps = { "Anthem", "HealthNet", "Kaiser", "Sharp", "Sutter", "UHC", "Western" };
		Set<String> CAcarriers = new HashSet<String>(Arrays.asList(CAcorps));
		medicalCarriers.put("CA", CAcarriers);

		String[] OHcorps = { "Anthem" };
		Set<String> OHcarriers = new HashSet<String>(Arrays.asList(OHcorps));
		medicalCarriers.put("OH", OHcarriers);

		String[] DEcorps = { "Aetna", "UHC", "Highmark" };
		Set<String> DEcarriers = new HashSet<String>(Arrays.asList(DEcorps));
		medicalCarriers.put("DE", DEcarriers);

		String[] PA_dental = { "Aetna", "CPA", "Delta", "Oxford", "United Concordia" };
		Set<String> PA_dental_carriers = new HashSet<String>(Arrays.asList(PA_dental));
		dentalCarriers.put("PA", PA_dental_carriers);

		String[] NJ_dental = {};
		Set<String> NJ_dental_carriers = new HashSet<String>(Arrays.asList(NJ_dental));
		dentalCarriers.put("NJ", NJ_dental_carriers);

		String[] OH_dental = {};
		Set<String> OH_dental_carriers = new HashSet<String>(Arrays.asList(OH_dental));
		dentalCarriers.put("OH", OH_dental_carriers);

		String[] sheets = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20" };

		String[] quarters = { "Q1", "Q2", "Q3", "Q4" };

		Set<String> states = medicalCarriers.keySet();

		String[] selection = { "Compare", "Merge", "Plan Name Map" };

		String[] otherOptions = { "None", "Code Retriever", "Base Rate Retriever", "PDF Mapper" };

		String[] types = { "Medical", "Dental", "Vision" };

		// Create the save button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
		parseButton = new JButton("Parse", createImageIcon("images/Save16.gif"));
		parseButton.addActionListener(this);

		tokenizeButton = new JButton("Tokenize", createImageIcon("directory"));
		tokenizeButton.addActionListener(this);

		JLabel carrierLbl = new JLabel("Carrier:");
		JLabel sheetLbl = new JLabel("Sheet:");
		JLabel quarterLbl = new JLabel("Quarter:");
		JLabel stateLbl = new JLabel("State:");
		JLabel otherLbl = new JLabel("Other:");
		JLabel selectionLbl = new JLabel("Operation: ");
		JLabel typeLbl = new JLabel("Type: ");

		carrierBox = new JComboBox<String>(
				medicalCarriers.get("PA").toArray(new String[medicalCarriers.get("PA").size()]));
		sheetBox = new JComboBox<String>(sheets);
		sheetBox.addActionListener(this);
		dateBox = new JComboBox<String>(quarters);
		dateBox.addActionListener(this);
		stateBox = new JComboBox<String>(states.toArray(new String[states.size()]));
		stateBox.addActionListener(this);
		otherBox = new JComboBox<String>(otherOptions);
		otherBox.addActionListener(this);
		selectionBox = new JComboBox<String>(selection);
		selectionBox.addActionListener(this);
		typeBox = new JComboBox<String>(types);
		typeBox.addActionListener(this);

		JPanel progressPanel = new JPanel();
		progressPanel.add(progressBar);

		JPanel planPanel = new JPanel();
		planPanel.add(planButton);
		planPanel.add(rateButton);
		planPanel.add(outputButton);
		planPanel.add(tokenizeButton);
		planPanel.add(clearButton);

		// For layout purposes, put the buttons in a separate panel
		JPanel typePanel = new JPanel(); // use FlowLayout
		typePanel.add(otherLbl);
		typePanel.add(otherBox);
		typePanel.add(typeLbl);
		typePanel.add(typeBox);
		typePanel.add(stateLbl);
		typePanel.add(stateBox);
		typePanel.add(carrierLbl);
		typePanel.add(carrierBox);
		typePanel.add(sheetLbl);
		typePanel.add(sheetBox);
		typePanel.add(quarterLbl);
		typePanel.add(dateBox);
		typePanel.add(parseButton);

		JPanel typePanel2 = new JPanel();
		typePanel2.add(selectionLbl);
		typePanel2.add(selectionBox);
		typePanel2.add(compareButtonF1);
		typePanel2.add(compareButtonF2);
		typePanel2.add(compareButton);

		JPanel overall = new JPanel(new GridLayout(4, 1));
		overall.add(progressPanel);
		overall.add(planPanel);
		overall.add(typePanel);
		overall.add(typePanel2);

		// Add the buttons and the log to this panel.
		add(overall, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.SOUTH);

		// Remember to delete default settings
		dateBox.setSelectedItem("Q4");
		typeBox.setSelectedItem("Medical");
		stateBox.setSelectedItem("NJ");
		carrierBox.setSelectedItem("AmeriHealth");
		selectionBox.setSelectedItem("Plan Name Map");
	}

	@SuppressWarnings("static-access")
	public void actionPerformed(ActionEvent e) {
		// Handle open plan button action.
		if (e.getSource() == planButton) {
			progressBar.setValue(0);
			int returnVal = fc.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if(selectedPlans.isEmpty()){
					selectedPlans = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));
				}
				else{
					selectedPlans.addAll(new ArrayList<File>(Arrays.asList(fc.getSelectedFiles())));
				}
				// This is where a real application would open the file.
				for (File f : selectedPlans) {
					log.append("Opening: " + f.getName() + "." + newline);
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == outputButton) {
			int returnVal = fc.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedOutputs = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));

				for (File f : selectedOutputs) {
					log.append("Will output to: " + f.getName() + "." + newline);
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
		}
		// Handle open rate button action.
		else if (e.getSource() == rateButton) {
			progressBar.setValue(0);
			int returnVal = fc.showOpenDialog(Main.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				if(selectedRates.isEmpty()){
					selectedRates = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));
				}
				else{
					selectedRates.addAll(new ArrayList<File>(Arrays.asList(fc.getSelectedFiles())));
				}

				// This is where a real application would open the file.
				for (File f : selectedRates) {
					log.append("Opening: " + f.getName() + "." + newline);
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

		} else if (e.getSource() == tokenizeButton) {
			progressBar.setValue(0);
			if (!selectedPlans.isEmpty()) {
				Tokenizer tokenizer = new Tokenizer(selectedPlans);
				try {
					tokenizer.tokenize();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.append("Output file: Tokens.xlsx");
				log.setCaretPosition(log.getDocument().getLength());
			} else if (!selectedRates.isEmpty()) {
				Tokenizer tokenizer = new Tokenizer(selectedRates);
				try {
					tokenizer.tokenize();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.append("Output file: Tokens.xlsx");
				log.setCaretPosition(log.getDocument().getLength());
			} else {
				log.append("No files selected");
			}
		} else if (e.getSource() == parseButton) {
			if (selectedPlans == null && selectedRates == null) {
				log.append("No files selected." + newline);
				return;
			}

			/*
			 * Retrieve user inputs and set the according variables
			 */
			checkState();
			checkCarrier();
			checkPlan();

			System.out.println(sheetBox.getSelectedItem());
			delegator = new Delegator((String) otherBox.getSelectedItem(), carrierType, planType,
					sheetBox.getSelectedIndex(), selectedState, (String) dateBox.getSelectedItem(), selectedPlans,
					selectedRates, selectedOutputs, log, progressBar);
			delegator.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent event) {
					switch (event.getPropertyName()) {
					case "progress":
						progressBar.setIndeterminate(false);
						progressBar.setValue((Integer) event.getNewValue());
						// System.out.println(event.getNewValue());
						break;
					case "state":
						switch ((StateValue) event.getNewValue()) {
						case DONE:
							try {
								pages = (delegator).get();
								createExcel();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						case STARTED:
						case PENDING:
							break;
						}
						break;
					}
				}
			});

			System.out.println("executing");
			(delegator).execute();
		} else if (e.getSource() == stateBox) {
			String state = (String) stateBox.getSelectedItem();
			carrierBox.removeAllItems();
			Set<String> c = sourceCarriers.get(state);
			for (String carrier : c) {
				carrierBox.addItem(carrier);
			}
		} else if (e.getSource() == compareButtonF1) {
			int returnVal = fc.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				compareFiles1 = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));

				// This is where a real application would open the file.
				if (compareFiles1.size() == 1) {
					log.append("Opening: " + compareFiles1.get(0).getName() + "." + newline);
				} else {
					log.append("Please enter only one file");
					compareFiles1.clear();
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == compareButtonF2) {
			int returnVal = fc.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				compareFiles2 = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));

				// This is where a real application would open the file.
				if (compareFiles2.size() == 1) {
					log.append("Opening: " + compareFiles2.get(0).getName() + "." + newline);
				} else {
					log.append("Please enter only one file");
					compareFiles2.clear();
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		} else if (e.getSource() == compareButton) {
			checkCarrier();
			checkSelectedOperation();
			if (compareFiles1.size() == 0 && compareFiles2.size() == 0) {
				log.append("Make sure you choose both of your files to compare!");
				return;
			}
			File f1 = compareFiles1.get(0);
			File f2 = compareFiles2.get(0);
			String path1 = f1.getAbsolutePath();
			String path2 = f2.getAbsolutePath();
			try {
				if (this.selectedOperation.equals("Merge")) {
					System.out.println(path1);
					ArrayList<Page> result = Merger.merge(path1, path2, carrierType,
							(String) dateBox.getSelectedItem());
					filename = String.format("%s_%s_%s_%s", selectedState.toString(), carrierType.toString(),
							(String) dateBox.getSelectedItem(), year);
					ExcelWriter merge_excel = new ExcelWriter(filename, result, carrierType, selectedState, planType,
							log);
					merge_excel.populateExcel();
				} else if (this.selectedOperation.equals("Compare")) {
					Merger.compareAetnaWorkbooks(path1, path2);
				} else if (this.selectedOperation.equals("Plan Name Map")) {
					Merger.mapIDName(path1, path2, Formatter.removeFileExtension(f2.getName()));
					log.append("Output File: " + Formatter.removeFileExtension(f2.getName()) + "_Final.xslx");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == selectionBox) {
			this.selectedOperation = (String) selectionBox.getSelectedItem();
			System.out.println(this.selectedOperation);
		} else if (e.getSource() == clearButton) {
			selectedPlans.clear();
			selectedRates.clear();
			selectedOutputs.clear();
			compareFiles1.clear();
			compareFiles2.clear();
			pages.clear();
			log.append("Cleared files.\n");
		} else if (e.getSource() == typeBox) {
			checkState();
			checkPlan();
			switch (planType) {
			case Medical:
				this.sourceCarriers = medicalCarriers;
				break;
			case Dental:
				this.sourceCarriers = dentalCarriers;
				break;
			}
			carrierBox.removeAllItems();
			Set<String> c = sourceCarriers.get(selectedState.toString());
			if (!c.isEmpty()) {
				for (String carrier : c) {
					carrierBox.addItem(carrier);
				}
			}
		}
	}

	public void checkSelectedOperation() {
		this.selectedOperation = (String) selectionBox.getSelectedItem();
	}

	public void createExcel() {
		checkPlan();
		checkCarrier();
		checkState();
		if (pages.size() == 0) {
			log.append("No plans in array" + newline);
			return;
		}
		try {
			if (otherBox.getSelectedItem() == "Base Rate Retriever") {
				filename = "Base_Rate";
			} else if (pages.size() > 1) {
				if (selectedState.equals(State.CA)) {
					filename = String.format("%s_%s_%s", selectedState.toString(), (String) dateBox.getSelectedItem(),
							year);
				} else {
					filename = String.format("%s_%s_%s_%s", selectedState.toString(), carrierType.toString(),
							(String) dateBox.getSelectedItem(), year);
				}
			} else {
				if (!selectedPlans.isEmpty()) {
					filename = Formatter.removeFileExtension(selectedPlans.get(0).getName());
				} else {
					filename = Formatter.removeFileExtension(selectedRates.get(0).getName());
				}
			}
			ExcelWriter writer = new ExcelWriter(filename, pages, carrierType, selectedState, planType, log);
			writer.populateExcel();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void checkState() {
		if (stateBox.getSelectedItem().equals("PA")) {
			this.selectedState = State.PA;
		}
		if (stateBox.getSelectedItem().equals("NJ")) {
			this.selectedState = State.NJ;
		}
		if (stateBox.getSelectedItem().equals("OH")) {
			this.selectedState = State.OH;
		}
		if (stateBox.getSelectedItem().equals("CA")) {
			this.selectedState = State.CA;
		}
		if (stateBox.getSelectedItem().equals("DE")) {
			this.selectedState = State.DE;
		}
	}

	public void checkCarrier() {
		if (carrierBox.getSelectedItem().equals("UPMC")) {
			this.carrierType = Carrier.UPMC;
		} else if (carrierBox.getSelectedItem().equals("Aetna")) {
			carrierType = Carrier.Aetna;
		} else if (carrierBox.getSelectedItem().equals("WPA")) {
			this.carrierType = Carrier.WPA;
		} else if (carrierBox.getSelectedItem().equals("NEPA")) {
			this.carrierType = Carrier.NEPA;
		} else if (carrierBox.getSelectedItem().equals("CPA")) {
			this.carrierType = Carrier.CPA;
		} else if (carrierBox.getSelectedItem().equals("UHC")) {
			this.carrierType = Carrier.UHC;
		} else if (carrierBox.getSelectedItem().equals("Highmark")) {
			this.carrierType = Carrier.Highmark;
		} else if (carrierBox.getSelectedItem().equals("IBC")) {
			this.carrierType = Carrier.IBC;
		} else if (carrierBox.getSelectedItem().equals("CBC")) {
			this.carrierType = Carrier.CBC;
		} else if (carrierBox.getSelectedItem().equals("AmeriHealth")) {
			this.carrierType = Carrier.AmeriHealth;
		} else if (carrierBox.getSelectedItem().equals("Oxford")) {
			this.carrierType = Carrier.Oxford;
		} else if (carrierBox.getSelectedItem().equals("Cigna")) {
			this.carrierType = Carrier.Cigna;
		} else if (carrierBox.getSelectedItem().equals("Horizon")) {
			this.carrierType = Carrier.Horizon;
		} else if (carrierBox.getSelectedItem().equals("Geisinger")) {
			this.carrierType = Carrier.Geisinger;
		} else if (carrierBox.getSelectedItem().equals("Delta")) {
			this.carrierType = Carrier.Delta;
		} else if (carrierBox.getSelectedItem().equals("Anthem")) {
			this.carrierType = Carrier.Anthem;
		} else if (carrierBox.getSelectedItem().equals("United Concordia")) {
			this.carrierType = Carrier.United_Concordia;
		}
	}

	public void checkPlan() {
		if (typeBox.getSelectedItem().equals("Medical")) {
			this.planType = Plan.Medical;
		} else if (typeBox.getSelectedItem().equals("Dental")) {
			this.planType = Plan.Dental;
		} else if (typeBox.getSelectedItem().equals("Vision")) {
			this.planType = Plan.Vision;
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Main.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("BeneFix Parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new Main());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}
