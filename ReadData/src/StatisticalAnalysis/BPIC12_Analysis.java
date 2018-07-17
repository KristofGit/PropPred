package StatisticalAnalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import LogRepresentation.ExecutionEvent;
import LogRepresentation.ExecutionTrace;
import LogRepresentation.ProcessModel;

public class BPIC12_Analysis {

	public static void countDifferentActivities(ProcessModel model)
	{
		List<ExecutionTrace> allTraces = model.getAllTraces();
		
		Set<String> allActivityNames = new HashSet<String>();
		Map<String, Integer> activityCounts = new HashMap<>();

		for(ExecutionTrace eachTrace : allTraces)
		{
			List<ExecutionEvent> allExecutionEvents = eachTrace.getAllEvents();
			
			List<String> activityNames = allExecutionEvents.stream().map(x->x.getActivityName()).collect(Collectors.toList());
			
			allActivityNames.addAll(activityNames);

			for(String eachActivity :activityNames)
			{
				if(activityCounts.containsKey(eachActivity))
				{
					Integer count = activityCounts.get(eachActivity);
					activityCounts.put(eachActivity, count+1);
				}
				else
				{
					activityCounts.put(eachActivity, 1);
				}
			}
		}
		
		System.out.println(allActivityNames);
		
		
		
		System.out.println(activityCounts);
	}
	
}
