package components;
 
import java.io.*;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

import geisinger.Geisinger_Parser; 



public class FileChooser extends JPanel
                             implements ActionListener {
    static private final String newline = "\n";
    JButton openButton, parseButton;
    JComboBox carrierBox;
    JComboBox sheetBox;
    JComboBox dateBox;
    JTextArea log;
    JFileChooser fc;
    File selectedFile;
    String filename;
    Carrier carrierType;
    WPA wpaType;
    String s_page;
    String e_page;

    public enum Carrier {
        UMPC, Aetna, CPA, NEPA, WPA, IBC, UHC, Geisinger
    }
    
    public enum WPA {
    	HMK, HCA
    }
 
    public FileChooser() {
        super(new BorderLayout());
 
        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(20,80);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);
 
        //Create a file chooser
        fc = new JFileChooser();
 
        //Uncomment one of the following lines to try a different
        //file selection mode.  The first allows just directories
        //to be selected (and, at least in the Java look and feel,
        //shown).  The second allows both files and directories
        //to be selected.  If you leave these lines commented out,
        //then the default mode (FILES_ONLY) will be used.
        //
        //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
 
        //Create the open button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        openButton = new JButton("Upload file",
                                 createImageIcon("/Users/grantkayes/Downloads/BeneFix App/src/components/file.png"));
        openButton.addActionListener(this);
 
        //Options for the JComboBox 
        String[] carriers = {"Aetna", "UMPC", "CPA", "NEPA", "WPA", "IBC", "UHC", "Geisinger"};
        
        String[] sheets = {"0","1","2","3","4","5","6","7","8","9","10"};
        
        String[] quarters = {"Q1", "Q2", "Q3", "Q4"};

        
        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        parseButton = new JButton("Parse",
                                 createImageIcon("images/Save16.gif"));
        parseButton.addActionListener(this);
 
        JLabel carrierLbl = new JLabel("Carrier:");
        JLabel sheetLbl = new JLabel("Sheet:");
        JLabel quarterLbl = new JLabel("Quarter:");
        carrierBox = new JComboBox(carriers);
        sheetBox = new JComboBox(sheets);
        dateBox = new JComboBox(quarters);


        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        buttonPanel.add(openButton);
        buttonPanel.add(carrierLbl);
        buttonPanel.add(carrierBox);
        buttonPanel.add(sheetLbl);
        buttonPanel.add(sheetBox);
        buttonPanel.add(quarterLbl);
        buttonPanel.add(dateBox);
        buttonPanel.add(parseButton);
        
        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        add(logScrollPane, BorderLayout.CENTER);
    }
 
    public void actionPerformed(ActionEvent e) {
        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(FileChooser.this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                //This is where a real application would open the file.
                log.append("Opening: " + selectedFile.getName() + "." + newline);
            } else {
                log.append("Open command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
            
         } 
        else if (e.getSource() == parseButton) {
        	int sheet = Integer.parseInt((String) sheetBox.getSelectedItem());
        	System.out.println(sheet);
        	filename = removeFileExtension(selectedFile.getName());
        	if(selectedFile == null){
                log.append("No file selected." + newline);
        	}
        	else {
        		ArrayList<Page> pages = null;      		
        		if(carrierBox.getSelectedItem().equals("UMPC")){
        			carrierType = Carrier.UMPC;
        		}
        		else if(carrierBox.getSelectedItem().equals("Aetna")){
        			carrierType = Carrier.Aetna;
        		}
        		else if(carrierBox.getSelectedItem().equals("WPA")){
        			carrierType = Carrier.WPA;
        			if(filename.contains("HCA")){
        				wpaType = WPA.HCA;
        			}
        			if(filename.contains("HMK")){
        				wpaType = WPA.HMK;
        			}
        		}
        		else if(carrierBox.getSelectedItem().equals("NEPA")){
        			carrierType = Carrier.NEPA;
        		}
        		else if(carrierBox.getSelectedItem().equals("CPA")){
        			carrierType = Carrier.CPA;
        		}
        		else if(carrierBox.getSelectedItem().equals("IBC")){
        			carrierType = Carrier.IBC;
        		}
        		else if(carrierBox.getSelectedItem().equals("UHC")){
        			carrierType = Carrier.UHC;
        		}
        		else if(carrierBox.getSelectedItem().equals("Geisinger")){
        			carrierType = Carrier.Geisinger;
        			int option = JOptionPane.showConfirmDialog(getParent(), "Would you like to change default pages setting?", "Page Select", JOptionPane.YES_NO_OPTION);
        			if (option == JOptionPane.YES_OPTION) {
        				JTextField field1 = new JTextField();
            			JTextField field2 = new JTextField();
            			Object[] message = {
            			    "Start Page:", field1,
            			    "End Page", field2
            			};
            			int option2 = JOptionPane.showConfirmDialog(getParent(), message, "Select which pages to parse", JOptionPane.OK_CANCEL_OPTION);
            			if (option2 == JOptionPane.OK_OPTION)
            			{
            			    s_page = field1.getText();
            			    e_page = field2.getText();
            			}
        			}
        			else if (option == JOptionPane.NO_OPTION) { 
        				s_page = "25"; //cover page is page 1
        				e_page = "97";
        			}
        		}
        		
        		String start_date = "";
        		String end_date = "";
        		String quarter = (String) dateBox.getSelectedItem();
        		if(quarter.equals("Q1")){
        			start_date = "01/01/2017";
        			end_date = "3/31/2017";
        		}
        		if(quarter.equals("Q2")){
        			start_date = "04/01/2017";
        			end_date = "6/30/2017";
        		}
        		if(quarter.equals("Q3")){
        			start_date = "07/01/2017";
        			end_date = "9/30/2017";
        		}
        		if(quarter.equals("Q4")){
        			start_date = "10/01/2017";
        			end_date = "12/31/2017";
        		}
				try {
					switch(carrierType){
					case UMPC:
		        		umpc.UMPC_Page[] umpc_pages;
	            		umpc.UMPC_Parser umpc_parser = new umpc.UMPC_Parser(selectedFile,start_date,end_date);
		        		umpc_pages = umpc_parser.parse();
		        		umpc.UMPC_ExcelWriter.populateExcel(umpc_pages, filename);
						break;
					case Aetna:
		        		aetna.Aetna_Page[] aetna_pages;
	            		aetna.Aetna_Parser aetna_parser = new aetna.Aetna_Parser(selectedFile,start_date,end_date);
		        		aetna_pages = aetna_parser.parse();
		        		aetna.Aetna_ExcelWriter.populateExcel(aetna_pages, filename);
						break;
					case WPA:
	            		WPA_Parser wpa_parser = new WPA_Parser(selectedFile, sheet,start_date,end_date);
						switch(wpaType){
						case HCA:
			        		pages = wpa_parser.parseHCA();
			        		break;
						case HMK:
			        		pages = wpa_parser.parseHMK();
			        		break;
						}
		        		ExcelWriter.populateExcel(pages, filename);
						break;
					case NEPA:
	            		NEPA_Parser nepa_parser = new NEPA_Parser(selectedFile, sheet,start_date,end_date);
		        		pages = nepa_parser.parse();
		        		ExcelWriter.populateExcel(pages, filename);
						break;
					case CPA:
	            		CPA_Parser cpa_parser = new CPA_Parser(selectedFile, sheet,start_date,end_date);
		        		pages = cpa_parser.parse();
		        		ExcelWriter.populateExcel(pages, filename);
						break;
					case IBC:
						Page ibc_page;
	            		IBC_Parser ibc_parser = new IBC_Parser(selectedFile,start_date,end_date);
		        		ibc_page = ibc_parser.parse();
		        		ExcelWriter.populateExcel(pages, filename);
					case UHC:
						ArrayList<Page> uhc_pages;
	            		UHC_Parser uhc_parser = new UHC_Parser(selectedFile, sheet,start_date,end_date);
		        		uhc_pages = uhc_parser.parse();
		        		ExcelWriter.populateExcel(uhc_pages, filename);
						break;	
					case Geisinger:
						ArrayList<Page> geisinger_pages;
						geisinger.Geisinger_Parser geisinger_parser = new geisinger.Geisinger_Parser();
						//geisinger_pages = ;
						geisinger.Geisinger_ExcelWriter.populateExcel((geisinger_parser.parse(selectedFile, start_date, end_date, s_page, e_page)), filename);
						break;
					}
						
					
	        		log.append("File parsed" + newline);
	        		String output = String.format("Output file: %s_data.xlxs" + newline, filename);
	        		log.append(output);
	        		
				} catch (IOException e1) {
	                log.append("Invalid file." + newline);
					e1.printStackTrace();
				}
        	}
        }
    }
    
//    public Carrier checkCarrier(String name){
//    	if(name.toLowerCase().contains("UMPC")){
//    		return Carrier.UMPC;
//    	}
//    	else if(name.toLowerCase().contains("Aetna")){
//    		return Carrier.Aetna;
//    	}
//    	else if(name.toLowerCase().contains("UMPC")){
//    		return Carrier.UMPC;
//    	}
//    	if(name.toLowerCase().contains("UMPC")){
//    		return Carrier.UMPC;
//    	}
//    }
 
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
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("FileChooserDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new FileChooser());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
	public static String removeFileExtension(String input){
		return  input.substring(0, input.lastIndexOf("."));
	}
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }
}