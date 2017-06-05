package components;

import java.io.*;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import cbc.*;

public class FileChooser extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static private final String newline = "\n";
	JButton planButton, rateButton, parseButton;
	JComboBox<String> carrierBox;
	JComboBox<String> sheetBox;
	JComboBox<String> dateBox;
	JTextArea log;
	JFileChooser fc;
	File[] selectedPlans;
	File[] selectedRates;
	ArrayList<Page> pages;
	String filename;
	Boolean file;
	String year;
	Carrier carrierType;
	WPA wpaType;

	public enum Carrier {
		UPMC, Aetna, CPA, NEPA, WPA, IBC, CBC
	}

	public enum WPA {
		HMK, HCA
	}

	public FileChooser() {
		super(new BorderLayout());
		
		year = "2017";

		// Create the log first, because the action listeners
		// need to refer to it.
		log = new JTextArea(20, 80);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		// Create a file chooser
		fc = new JFileChooser();

		fc.setMultiSelectionEnabled(true);

		// Uncomment one of the following lines to try a different
		// file selection mode. The first allows just directories
		// to be selected (and, at least in the Java look and feel,
		// shown). The second allows both files and directories
		// to be selected. If you leave these lines commented out,
		// then the default mode (FILES_ONLY) will be used.
		//
		// fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// Create the open button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).

		planButton = new JButton("Upload plans",
				createImageIcon("/Users/drewboyette/Documents/Fall 2016/CIS 121/BeneFixApp/src/components/file.png"));
		planButton.addActionListener(this);

		rateButton = new JButton("Upload rates",
				createImageIcon("/Users/drewboyette/Documents/Fall 2016/CIS 121/BeneFixApp/src/components/file.png"));
		rateButton.addActionListener(this);

		// Options for the JComboBox
		String[] carriers = { "Aetna", "UPMC", "CPA", "NEPA", "WPA", "IBC", "CBC" };

		String[] sheets = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

		String[] quarters = { "Q1", "Q2", "Q3", "Q4" };

		// Create the save button. We use the image from the JLF
		// Graphics Repository (but we extracted it from the jar).
		parseButton = new JButton("Parse", createImageIcon("images/Save16.gif"));
		parseButton.addActionListener(this);

		JLabel carrierLbl = new JLabel("Carrier:");
		JLabel sheetLbl = new JLabel("Sheet:");
		JLabel quarterLbl = new JLabel("Quarter:");
		carrierBox = new JComboBox<String>(carriers);
		sheetBox = new JComboBox<String>(sheets);
		dateBox = new JComboBox<String>(quarters);

		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(planButton);
		buttonPanel.add(rateButton);
		buttonPanel.add(carrierLbl);
		buttonPanel.add(carrierBox);
		buttonPanel.add(sheetLbl);
		buttonPanel.add(sheetBox);
		buttonPanel.add(quarterLbl);
		buttonPanel.add(dateBox);
		buttonPanel.add(parseButton);

		// Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		// Handle open plan button action.
		if (e.getSource() == planButton) {
			int returnVal = fc.showOpenDialog(FileChooser.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedPlans = fc.getSelectedFiles();

				// This is where a real application would open the file.
				for (File f : selectedPlans) {
					log.append("Opening: " + f.getName() + "." + newline);
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

		}
		// Handle open rate button action.
		else if (e.getSource() == rateButton) {
			int returnVal = fc.showOpenDialog(FileChooser.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				selectedRates = fc.getSelectedFiles();

				// This is where a real application would open the file.
				for (File f : selectedRates) {
					log.append("Opening: " + f.getName() + "." + newline);
				}
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

		} else if (e.getSource() == parseButton) {
			if (selectedPlans == null && selectedRates == null) {
				log.append("No rates selected." + newline);
				return;
			}
			parse();
		}
	}
	
	
	public void parse(){
		checkCarrier((String) carrierBox.getSelectedItem(), filename);
		pages = new ArrayList<Page>();
		if (selectedPlans != null) {
			for (File selectedPlan : selectedPlans) {
				filename = selectedPlan.getName();
				try {
					switch (carrierType) {
					case UPMC:
						break;
					case Aetna:
						break;
					case WPA:
						switch (wpaType) {
						case HCA:
							break;
						case HMK:
							break;
						}
						break;
					case CBC:
						Page cbc_page;
						CBC_Plan_Parser cbc_plan_parser = new cbc.CBC_Plan_Parser(selectedPlan);
						cbc_page = cbc_plan_parser.parse(filename);
						pages.add(cbc_page);
						String parsed = String.format("File: %s parsed\n", selectedPlan.getName());
						log.append(parsed);
						break;
					}

				} catch (IOException e1) {
					log.append("Invalid file." + newline);
					e1.printStackTrace();
				}
			}
		}
		if (selectedRates != null) {
			for (File selectedRate : selectedRates) {
				log.append("Parsing: " + selectedRate.getName() + "." + newline);
				int sheet = Integer.parseInt((String) sheetBox.getSelectedItem());
				filename = removeFileExtension(selectedRate.getName());

				String start_date = "";
				String end_date = "";
				String quarter = (String) dateBox.getSelectedItem();
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
						aetna.Aetna_Page[] aetna_pages;
						aetna.Aetna_Parser aetna_parser = new aetna.Aetna_Parser(selectedRate, start_date,
								end_date);
						aetna_pages = aetna_parser.parse();
						aetna.Aetna_ExcelWriter.populateExcel(aetna_pages, filename);
						break;
					case WPA:
						WPA_Parser wpa_parser = new WPA_Parser(selectedRate, sheet, start_date, end_date);
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
						NEPA_Parser nepa_parser = new NEPA_Parser(selectedRate, sheet, start_date, end_date);
						pages.addAll(nepa_parser.parse());
						break;
					case CPA:
						CPA_Parser cpa_parser = new CPA_Parser(selectedRate, sheet, start_date, end_date);
						pages.addAll(cpa_parser.parse());
						break;
					case IBC:
						Page ibc_page;
						IBC_Parser ibc_parser = new IBC_Parser(selectedRate, start_date, end_date);
						ibc_page = ibc_parser.parse();
						pages.add(ibc_page);
					case CBC:
						Page cbc_page = null;
						CBC_Parser cbc_parser = new CBC_Parser(selectedRate, cbc_page, sheet, quarter, quarter);
						// cbc_page = cbc_parser.parse();
						// for(File f: selectedFiles){
						// if(f.contains(filename){
						//
						// }
						// }
						// pages.addAll(cbc_page);
					}
					log.append("File parsed" + newline);

				} catch (IOException e1) {
					log.append("Invalid file." + newline);
					e1.printStackTrace();
				}
			}
		}
		try {
			if (pages.size() > 1) {
				filename = String.format("%s_%s_%s", carrierType.toString(),
						(String) dateBox.getSelectedItem(), year);
			}
			ExcelWriter.populateExcel(pages, filename, carrierType);
			String output = String.format("Output file: %s_data.xlxs" + newline, filename);
			log.append(output);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void checkCarrier(String carrier, String filename) {
		if (carrier.equals("UPMC")) {
			this.carrierType = Carrier.UPMC;
		} else if (carrierBox.getSelectedItem().equals("Aetna")) {
			carrierType = Carrier.Aetna;
		} else if (carrierBox.getSelectedItem().equals("WPA")) {
			this.carrierType = Carrier.WPA;
			if (filename.contains("HCA")) {
				this.wpaType = WPA.HCA;
			}
			if (filename.contains("HMK")) {
				this.wpaType = WPA.HMK;
			}
		} else if (carrierBox.getSelectedItem().equals("NEPA")) {
			this.carrierType = Carrier.NEPA;
		} else if (carrierBox.getSelectedItem().equals("CPA")) {
			this.carrierType = Carrier.CPA;
		} else if (carrierBox.getSelectedItem().equals("IBC")) {
			this.carrierType = Carrier.IBC;
		} else if (carrierBox.getSelectedItem().equals("CBC")) {
			this.carrierType = Carrier.CBC;
		}
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = FileChooser.class.getResource(path);
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
		JFrame frame = new JFrame("FileChooserDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new FileChooser());

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