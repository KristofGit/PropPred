package LogRepresentation;

public class TrainingTesting {

	private final ProcessModel dataForTraining;
	private final ProcessModel dataForTesting;
	
	public TrainingTesting(ProcessModel training, ProcessModel testing)
	{
		this.dataForTesting = testing;
		this.dataForTraining = training;
	}

	public ProcessModel getDataForTraining() {
		return dataForTraining;
	}

	public ProcessModel getDataForTesting() {
		return dataForTesting;
	}
}
