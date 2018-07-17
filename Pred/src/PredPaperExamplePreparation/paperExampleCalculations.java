package PredPaperExamplePreparation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import HelperClasses.DurationClassHelper;
import HelperClasses.DurationClassHelper.ActivitFromToWithDurations;
import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionEventPair;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;
import TraceSimilarity.DamerauLevenshtein_activity_and_dur;
import TraceSimilarity.DamerauLevenshtein_activity_and_dur.ItemEqualityNew;

public class paperExampleCalculations {

	private static Instant base = Instant.now();
	
	public static void main(String[] args) {
		
		ExecutionTrace t1 = getT1();
		ExecutionTrace t2 = getT2();
		ExecutionTrace t3 = getT3();
		ExecutionTrace t4 = getT4();

		ExecutionTrace t1CUT = t1.getTraceWithEventsBeforeIndex(5);
		ExecutionTrace t2CUT = t2.getTraceWithEventsBeforeIndex(4);		
		
		ExecutionTrace t3CUT_1 = t3.getTraceWithEventsBeforeIndex(3);
		ExecutionTrace t3CUT_2 = t3.getTraceWithEventsBeforeIndex(5);

		ExecutionTrace t4CUT = t4.getTraceWithEventsBeforeIndex(6);

		DurationClassHelper.fillDurationClasses(ProcessModel.of(t2,t3,t4));
		
		DamerauLevenshtein_activity_and_dur dl = new DamerauLevenshtein_activity_and_dur(1);
		double t1_t2 = dl.distance(distanceAdvanced, t1CUT.getAsEventPairs(), t2CUT.getAsEventPairs());
		double t1_t3_1 = dl.distance(distanceAdvanced, t1CUT.getAsEventPairs(), t3CUT_1.getAsEventPairs());
		double t1_t3_2 = dl.distance(distanceAdvanced, t1CUT.getAsEventPairs(), t3CUT_2.getAsEventPairs());
		double t1_t4 = dl.distance(distanceAdvanced, t1CUT.getAsEventPairs(), t4CUT.getAsEventPairs());

		System.out.println("distance t1_t2  "+t1_t2);
		System.out.println("distance t1_t3_1  "+t1_t3_1);
		System.out.println("distance t1_t3_2  "+t1_t3_2);
		System.out.println("distance t1_t4  "+t1_t4);

	}
	private static ExecutionTrace getT4()
	{
		ExecutionTrace t1 = new ExecutionTrace();
		ExecutionEvent e1 = new ExecutionEvent(null, "C", getInstant(17));
		ExecutionEvent e2 = new ExecutionEvent(null, "A", getInstant(21));
		ExecutionEvent e3 = new ExecutionEvent(null, "A", getInstant(22));
		ExecutionEvent e4 = new ExecutionEvent(null, "A", getInstant(25));
		ExecutionEvent e5 = new ExecutionEvent(null, "F", getInstant(30));
		ExecutionEvent e6 = new ExecutionEvent(null, "E", getInstant(37));

		//t1.addNewEvent(ExecutionEvent.getStartEvent());
		t1.addNewEvent(e1);
		t1.addNewEvent(e2);
		t1.addNewEvent(e3);
		t1.addNewEvent(e4);
		t1.addNewEvent(e5);
		t1.addNewEvent(e6);
		//t1.addNewEvent(ExecutionEvent.getEndEvent());
		
		return t1;
	}
	private static ExecutionTrace getT3()
	{
		ExecutionTrace t1 = new ExecutionTrace();
		ExecutionEvent e1 = new ExecutionEvent(null, "A", getInstant(40));
		ExecutionEvent e2 = new ExecutionEvent(null, "F", getInstant(45));
		ExecutionEvent e3 = new ExecutionEvent(null, "E", getInstant(49));
		ExecutionEvent e4 = new ExecutionEvent(null, "F", getInstant(51));
		ExecutionEvent e5 = new ExecutionEvent(null, "E", getInstant(57));
		ExecutionEvent e6 = new ExecutionEvent(null, "D", getInstant(63));

	//	t1.addNewEvent(ExecutionEvent.getStartEvent());
		t1.addNewEvent(e1);
		t1.addNewEvent(e2);
		t1.addNewEvent(e3);
		t1.addNewEvent(e4);
		t1.addNewEvent(e5);
		t1.addNewEvent(e6);
	//	t1.addNewEvent(ExecutionEvent.getEndEvent());
		
		return t1;
	}
	private static ExecutionTrace getT2()
	{
		ExecutionTrace t1 = new ExecutionTrace();
		ExecutionEvent e1 = new ExecutionEvent(null, "A", getInstant(49));
		ExecutionEvent e2 = new ExecutionEvent(null, "E", getInstant(54));
		ExecutionEvent e3 = new ExecutionEvent(null, "F", getInstant(61));
		ExecutionEvent e4 = new ExecutionEvent(null, "E", getInstant(68));
		ExecutionEvent e5 = new ExecutionEvent(null, "B", getInstant(69));
		ExecutionEvent e6 = new ExecutionEvent(null, "D", getInstant(78));

//		t1.addNewEvent(ExecutionEvent.getStartEvent());
		t1.addNewEvent(e1);
		t1.addNewEvent(e2);
		t1.addNewEvent(e3);
		t1.addNewEvent(e4);
		t1.addNewEvent(e5);
		t1.addNewEvent(e6);
	//	t1.addNewEvent(ExecutionEvent.getEndEvent());
		
		return t1;
	}
	private static ExecutionTrace getT1()
	{
		ExecutionTrace t1 = new ExecutionTrace();
		ExecutionEvent e1 = new ExecutionEvent(null, "A", getInstant(23));
		ExecutionEvent e2 = new ExecutionEvent(null, "E", getInstant(32));
		ExecutionEvent e3 = new ExecutionEvent(null, "E", getInstant(37));
		ExecutionEvent e4 = new ExecutionEvent(null, "F", getInstant(40));
		ExecutionEvent e5 = new ExecutionEvent(null, "E", getInstant(47));
		ExecutionEvent e6 = new ExecutionEvent(null, "D", getInstant(53));

	//	t1.addNewEvent(ExecutionEvent.getStartEvent());
		t1.addNewEvent(e1);
		t1.addNewEvent(e2);
		t1.addNewEvent(e3);
		t1.addNewEvent(e4);
		t1.addNewEvent(e5);
		t1.addNewEvent(e6);
	//	t1.addNewEvent(ExecutionEvent.getEndEvent());
		
		return t1;
	}
	
	private static Instant getInstant(int timestamp)
	{
		return base.plus(timestamp, ChronoUnit.MINUTES);
	}
	
	private static ItemEqualityNew<ExecutionEventPair> distanceAdvanced = new ItemEqualityNew<ExecutionEventPair>()
	{
		@Override
		public boolean isMajorSimiar(ExecutionEventPair one, ExecutionEventPair two) {
			//same activity
						
			return Objects.equals(one.getEventOne().getActivityName(), two.getEventOne().getActivityName()) &&
					Objects.equals(one.getEventTwo().getActivityName(), two.getEventTwo().getActivityName());
		}

		@Override
		public boolean isMinorSimilar(ExecutionEventPair one, ExecutionEventPair two) {
			
			if(one.containsArtificalEvent() || two.containsArtificalEvent())
			{
				return true;
			}
			
			ActivitFromToWithDurations binOne = DurationClassHelper.getDurationBinFor(one.getEventOne(), one.getEventTwo());
			ActivitFromToWithDurations binTwo = DurationClassHelper.getDurationBinFor(two.getEventOne(), two.getEventTwo());
				
			return binOne.equals(binTwo);
		}

		@Override
		public double minorSimilarCost(ExecutionEventPair one, ExecutionEventPair two, double totalPossibleCost) {
			//how far aparte are the duration classes
			
			ActivitFromToWithDurations binOne = DurationClassHelper.getDurationBinFor(one.getEventOne(), one.getEventTwo());
			ActivitFromToWithDurations binTwo = DurationClassHelper.getDurationBinFor(two.getEventOne(), two.getEventTwo());
				
			int amount = DurationClassHelper.getAmountOfBins(one.getEventOne(), one.getEventTwo());
			int difference = DurationClassHelper.getBinsBetween(binOne, binTwo);
				
			return (totalPossibleCost*(1-(difference/(double)amount)));
		}

	};

}
