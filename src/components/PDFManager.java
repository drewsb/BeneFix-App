package components;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.multipdf.Splitter; 

public class PDFManager {
    
   private PDFParser parser;
   private PDFTextStripper pdfStripper;
   private PDDocument pdDoc ;
   private COSDocument cosDoc ;
   
   private String Text ;
   private String filePath;
   private File file;
   
   private int numPages;

   public PDFManager() {
        
   }
   
   public String ToText() throws IOException
   {
       this.pdfStripper = null;
       this.pdDoc = null;
       this.cosDoc = null;
       
       file = new File(filePath);
       parser = new PDFParser(new RandomAccessFile(file,"r")); 
       
       parser.parse();
       cosDoc = parser.getDocument();
       pdfStripper = new PDFTextStripper();
       pdDoc = new PDDocument(cosDoc);
       numPages = pdDoc.getNumberOfPages();
       pdfStripper.setStartPage(1);
       pdfStripper.setEndPage(numPages);
  
       
       Text = pdfStripper.getText(pdDoc);
       pdDoc.close();
       return Text;
   }
   
   public String ToText(int current_page) throws IOException
   {
       this.pdfStripper = null;
       this.pdDoc = null;
       this.cosDoc = null;
       
       file = new File(filePath);
       parser = new PDFParser(new RandomAccessFile(file,"r")); 
       
       parser.parse();
       cosDoc = parser.getDocument();
       pdfStripper = new PDFTextStripper();
       pdDoc = new PDDocument(cosDoc);
       numPages = pdDoc.getNumberOfPages();
       pdfStripper.setStartPage(current_page);
       pdfStripper.setEndPage(current_page);
  
       
       Text = pdfStripper.getText(pdDoc);
       pdDoc.close();
       return Text;
   }
   
   public String ToText(PDDocument current_document) throws IOException
   {
       this.pdfStripper = null;
       this.pdDoc = null;
       this.cosDoc = null;
       
       pdfStripper = new PDFTextStripper();
       pdDoc = current_document;
       Text = pdfStripper.getText(pdDoc);
       pdDoc.close();
       return Text;
   }
   
   public int getNumPages() {
	   return numPages;
   }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
   
}