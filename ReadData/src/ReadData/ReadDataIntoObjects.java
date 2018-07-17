package ReadData;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import CSVReaderHelpdesk.CSVHandlerHelpdesk;
import Configuration.DataSource;
import LogRepresentation.ProcessModel;
import XESReaderBPIC12.XESHandlerBPIC_12;

public class ReadDataIntoObjects {
	
	public ProcessModel readData(DataSource source, boolean filterDuplicateAcitivites)
	{
		ProcessModel result = null;
		switch (source) {
		case BPIC_2012:
			result = readBPIC12(source.getPath(), filterDuplicateAcitivites);
			break;
		case Helpdesk:
			result = readHelpDesk(source.getPath());
			break;
		}
		return result;
	}
	
	private ProcessModel readHelpDesk(String path)
	{
		CSVHandlerHelpdesk read = new CSVHandlerHelpdesk();
		
		ProcessModel process = null;
		try {
			process = read.readFile(new File(path));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return process;
	}
	
	private ProcessModel readBPIC12(String path, boolean filterDuplicateAcitivites)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser;
        
        XESHandlerBPIC_12 userhandler = new XESHandlerBPIC_12(filterDuplicateAcitivites);

		try {
			saxParser = factory.newSAXParser();
			
		    saxParser.parse(new File(path), userhandler);
		  
		} catch (Exception e) {
			System.out.println(e);		
		}
		
		return userhandler.getProcess();
	}
	
	
	/*First check if we even need windows
	 * private ReadDataIntoObjects(Duration windowSize, Instant windowEnd) 
	{

		Instant windowStart = windowEnd.minus(windowSize);
		
		readFromXes(windowStart, windowEnd);
	}
	
	//trace between the xes file must 
	private void readFromXes(Instant start, Instant end)
	{
		
	}*/
}
