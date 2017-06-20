package components;


import java.awt.BorderLayout;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pa.PA_CBC_Rates;
import pa.PA_CBC_Benefits;

import java.util.concurrent.ExecutionException;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import nj.NJ_Amerihealth_Rates;
import nj.NJ_Oxford_Parser_Temp;

public class Main extends JPanel implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	static private final String newline = "\n";
	JButton planButton, rateButton, parseButton, outputButton, compareButtonF1, 
	compareButtonF2, compareButton , tokenizeButton;
	JComboBox<String> carrierBox;
	JComboBox<String> sheetBox;
	JComboBox<String> dateBox;
	JComboBox<String> stateBox;
	JProgressBar progressBar;
	public JTextArea log;
	JFileChooser fc;
	Parser parser;
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
	State selectedState;
	int progress;
	Carrier carrierType;
	HashMap<String, Set<String>> carriersInState;

	public enum Carrier {
		UPMC, Aetna, CPA, NEPA, WPA, IBC, CBC, AmeriHealth, Oxford, Cigna, Horizon, Geisinger, Compare
	}
	
	public enum State{
		NJ, PA
	}

	public Main() {
		super(new BorderLayout());
		carriersInState = new HashMap<String, Set<String>>();
		year = "2017";

		selectedPlans = new ArrayList<File>();
		selectedRates = new ArrayList<File>();
		compareFiles1 = new ArrayList<File>();
		compareFiles2 = new ArrayList<File>();

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
		
		outputButton = new JButton("Output file",
				createImageIcon("directory"));
		outputButton.addActionListener(this);
		
		compareButtonF1 = new JButton("First file");
		compareButtonF1.addActionListener(this);
		
		compareButtonF2 = new JButton("Second file");
		compareButtonF2.addActionListener(this);
		
		compareButton = new JButton("Compare the two files");
		compareButton.addActionListener(this);
		
		// Options for the JComboBox

		String[] PAcorps = { "Aetna", "UPMC", "CPA", "NEPA", "WPA", "IBC", "CBC", "Geisinger", "UHC"};

		Set<String> PAcarriers = new HashSet<String>(Arrays.asList(PAcorps));
		carriersInState.put("PA", PAcarriers);
		
		String[] NJcorps = {"AmeriHealth", "Aetna", "Cigna", "Horizon", "Oxford"};
		Set<String> NJcarriers = new HashSet<String>(Arrays.asList(NJcorps));
		carriersInState.put("NJ", NJcarriers);

		String[] sheets = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
				,"11","12","13","14","15","16","17","18","19","20"};

		String[] quarters = { "Q1", "Q2", "Q3", "Q4" };
		
		Set<String> states = carriersInState.keySet();
		
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
		carrierBox = new JComboBox<String>(carriersInState.get("PA").toArray
				(new String[carriersInState.get("PA").size()]));
		sheetBox = new JComboBox<String>(sheets);
		dateBox = new JComboBox<String>(quarters);
		stateBox = new JComboBox<String>(states.toArray(new String[states.size()]));
		stateBox.addActionListener(this);
		

		JPanel progressPanel = new JPanel();
		progressPanel.add(progressBar);

		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(planButton);
		buttonPanel.add(rateButton);
		buttonPanel.add(outputButton);
		buttonPanel.add(stateLbl);
		buttonPanel.add(stateBox);
		buttonPanel.add(carrierLbl);
		buttonPanel.add(carrierBox);
		buttonPanel.add(sheetLbl);
		buttonPanel.add(sheetBox);
		buttonPanel.add(quarterLbl);
		buttonPanel.add(dateBox);
		buttonPanel.add(tokenizeButton);
		buttonPanel.add(parseButton);
		
		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.add(compareButtonF1);
		buttonPanel2.add(compareButtonF2);
		buttonPanel2.add(compareButton);
		
		JPanel overall = new JPanel(new GridLayout(3, 1));
		overall.add(progressPanel);
		overall.add(buttonPanel);
		overall.add(buttonPanel2);
		

		// Add the buttons and the log to this panel.
		add(overall, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.SOUTH);
		
		
		//Remember to delete default settings
		dateBox.setSelectedItem("Q3");
		stateBox.setSelectedItem("NJ");
		carrierBox.setSelectedItem("Oxford");
	}

	public void actionPerformed(ActionEvent e) {
		// Handle open plan button action.
		if (e.getSource() == planButton) {
			progressBar.setValue(0);
			int returnVal = fc.showOpenDialog(Main.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedPlans = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));

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
			if (returnVal ==JFileChooser.APPROVE_OPTION) {
				selectedOutputs = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));
				
				for (File f: selectedOutputs) {
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
				selectedRates = new ArrayList<File>(Arrays.asList(fc.getSelectedFiles()));

				// This is where a real application would open the file.
				for (File f : selectedRates) {
					log.append("Opening: " + f.getName() + "." + newline);
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

		} 
		else if (e.getSource() == tokenizeButton) {
			progressBar.setValue(0);
			if(!selectedPlans.isEmpty()){
				Tokenizer tokenizer = new Tokenizer(selectedPlans);
				try {
					tokenizer.tokenize();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.append("Output file: Tokens.xlsx");
				log.setCaretPosition(log.getDocument().getLength());
			}
			else if(!selectedRates.isEmpty()){
				Tokenizer tokenizer = new Tokenizer(selectedPlans);
				try {
					tokenizer.tokenize();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.append("Output file: Tokens.xlsx");
				log.setCaretPosition(log.getDocument().getLength());
			}
			else{
				log.append("No files selected");
			}
		}
		else if (e.getSource() == parseButton) {
			if (selectedPlans == null && selectedRates == null) {
				log.append("No files selected." + newline);
				return;
			}
			String currState = (String) stateBox.getSelectedItem();
			switch (currState) {
			case "NJ": 
				selectedState = State.NJ;
				break;
			case "PA":
				selectedState = State.PA;
				break;
			}
			checkCarrier();

			parser = new Parser(carrierType, progress, selectedState, 
					(String) dateBox.getSelectedItem(), selectedPlans, selectedRates, selectedOutputs, log, progressBar);
			parser.addPropertyChangeListener(new PropertyChangeListener() {
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
								pages = parser.get();
								if (carrierType != Carrier.Compare) {
									createExcel();
								}
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
			parser.execute();
		} else if (e.getSource() == stateBox) {
			String state = (String) stateBox.getSelectedItem();
			carrierBox.removeAllItems();
			Set<String> c = carriersInState.get(state);
			for (String carrier: c) {
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
			if (compareFiles1.size() == 0 && compareFiles2.size() == 0) {
				log.append("Make sure you choose both of your files to compare!");
			}
			File f1 = compareFiles1.get(0);
			File f2 = compareFiles2.get(0);
			String path1 = f1.getAbsolutePath();
			String path2 = f2.getAbsolutePath();
			try {
				Parser.compareAetnaWorkbooks(path1, path2);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void createExcel() {
		try {
			if (pages.size() > 1) {
				filename = String.format("%s_%s_%s", carrierType.toString(), (String) dateBox.getSelectedItem(), year);
			}
			else{
				filename = removeFileExtension(selectedPlans.get(0).getName());
			}
			ExcelWriter.populateExcel(pages, filename, carrierType, selectedState);
			String output = String.format("Output file: %s_data.xlxs" + newline, filename);
			log.append(output);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	public static String removeFileExtension(String input) {
		return input.substring(0, input.lastIndexOf("."));
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
