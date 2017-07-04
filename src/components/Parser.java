package components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public interface Parser {
	
	public ArrayList<Page> parse(File file, String filename) throws EncryptedDocumentException, InvalidFormatException, IOException;

}
