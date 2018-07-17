package Configuration;

public enum DataSource {

	BPIC_2012("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Automatic Business Process Redesign/Daten/BPIC 12 financial_log.xes/data.xes"),
	Helpdesk("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Automatic Business Process Redesign/Daten/helpdesk.csv");

	private final String pathToXML;
	
	private DataSource(String path)
	{
		this.pathToXML = path;
	}
	
	public String getPath()
	{
		return pathToXML;
	}
}
