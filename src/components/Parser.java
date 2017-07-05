package components;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.w3c.dom.events.EventException;

public interface Parser {
	
	public ArrayList<Page> parse(File file, String filename) throws EventException, IOException, InvalidFormatException;

}
	