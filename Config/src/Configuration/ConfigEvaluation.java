package Configuration;

public class ConfigEvaluation {
	
	//Note, you can control if the proposed DL extension is used or not in the
	//PropabilityPredictionActivityAndTime class
	
	//WORKS WELL FOR BPIC ACTIVIVTY - Without RULES
	
	/*public final static DataSource dataToUse = DataSource.BPIC_2012;
	public static double DataSeparataionForTraining = 2.0/3;
	
	public static double minSupportCause = 1.001;
	public static double minSupportEffectLongTerm = 0.75;
	public static double minSupportEffectShorTermActivity = 0.80;
	public static double minSupportEffectShorTermDuration = 0.80;

	
	//how many events to look arround when searching similar traces based on a given event
	public static double EqualEventIndexWideningRelative = 0.05;
	public static double MostSimTracesTaken = 0.05;
	public static int FixedAmountOfMostSimTraces = 200;
	public static int PrefixForSimCalculation = 10; //TODO DASS MUSS NOCH INS PAPER REIN
	
	public static double histMostLikelySeparation = 0.2;*/
	
	
	//WORKS WELL FOR HELPDEST ACTIVITY - WITHOUT RULES
	public final static DataSource dataToUse = DataSource.Helpdesk;
	public static double DataSeparataionForTraining = 2.0/3;
	
	public static double minSupportCause = 1.001;
	public static double minSupportEffectLongTerm = 0.75;
	public static double minSupportEffectShorTermActivity = 0.80;
	public static double minSupportEffectShorTermDuration = 0.80;

	
	//how many events to look arround when searching similar traces based on a given event
	public static double EqualEventIndexWideningRelative = 0.05;
	public static double MostSimTracesTaken = 0.05;
	public static int FixedAmountOfMostSimTraces = 10;
	public static int PrefixForSimCalculation = 10;
	
	public static double histMostLikelySeparation = 0.1;
	
	
	//WORKS WELL FOR BPIC DURATION - WITHOUT RULES
	/*
	 	public final static DataSource dataToUse = DataSource.BPIC_2012;
	  public static double DataSeparataionForTraining = 2.0/3;
	 
	
	public static double minSupportCause = 1.001;
	public static double minSupportEffectLongTerm = 0.75;
	public static double minSupportEffectShorTermActivity = 0.80;
	public static double minSupportEffectShorTermDuration = 0.80;

	
	//how many events to look arround when searching similar traces based on a given event
	public static double EqualEventIndexWideningRelative = 0.2;
	public static double MostSimTracesTaken = 0.05;
	public static int FixedAmountOfMostSimTraces = 50;
	public static int PrefixForSimCalculation = 20;
	
	public static double histMostLikelySeparation = 0.2;
	
	//WORKS WELL FOR HELPDESK DURATION - WITHOUT RULES
	
	 public static double DataSeparataionForTraining = 2.0/3;
	 
		public final static DataSource dataToUse = DataSource.Helpdesk;

	public static double minSupportCause = 1.001;
	public static double minSupportEffectLongTerm = 0.75;
	public static double minSupportEffectShorTermActivity = 0.80;
	public static double minSupportEffectShorTermDuration = 0.80;

	
	//how many events to look arround when searching similar traces based on a given event
	public static double EqualEventIndexWideningRelative = 0.2;
	public static double MostSimTracesTaken = 0.05;
	public static int FixedAmountOfMostSimTraces = 200;
	public static int PrefixForSimCalculation = 6;
	
	public static double histMostLikelySeparation = 0.2;*/

	
	
	
	
	
	
	
	
	
	
	
	/*public static double DataSeparataionForTraining = 2.0/3;
	
	public static double minSupportCause = 1.001;
	public static double minSupportEffectLongTerm = 0.75;
	public static double minSupportEffectShorTermActivity = 0.80;
	public static double minSupportEffectShorTermDuration = 0.80;

	
	//how many events to look arround when searching similar traces based on a given event
	public static double EqualEventIndexWideningRelative = 0.1;
	public static double MostSimTracesTaken = 0.05;
	public static int FixedAmountOfMostSimTraces = 200;
	public static int PrefixForSimCalculation = 5;
	
	public static double histMostLikelySeparation = 0.3;*/

}
