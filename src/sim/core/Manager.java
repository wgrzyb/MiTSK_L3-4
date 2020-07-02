package sim.core;
import java.util.LinkedList;

public class Manager {	
	private double startSimTime = 0.0;
	private double stopSimTime = Double.MAX_VALUE;
	private double currentSimTime = startSimTime;
	private double timeStep = 1.0;
	private static Manager simMgr; // Singleton
	private boolean simulationStarted = false;
	// Lista workerów, którzy są składowymi kroku symulacji
	private LinkedList<SimStep> simStepWorkers = new LinkedList<>();
	
	public static Manager getInstance(double startSimTime, double timeStep) {
		if (simMgr == null) {
			simMgr = new Manager(startSimTime, timeStep);
		}
		return simMgr;
	}
	
	private Manager(double startSimTime, double timeStep) {
		if (startSimTime>0.0) 
			this.startSimTime = startSimTime;		
			this.timeStep = timeStep;
	}

	public void registerSimStep(SimStep step) {
		if (step!=null)
			simStepWorkers.add(step);
	}
	
	public final double simTime() {
		return currentSimTime;
	}
	
	public final void stopSimulation() {
		simulationStarted = false;
	}

	public final void startSimulation() throws Exception {
		simulationStarted = true;
		// DO WYKONANIA NA LABORATORIUM:
		System.out.println(simTime()+"\t Symulacja rozpoczęta.");
		while(this.simulationStarted && this.currentSimTime < this.stopSimTime){
			runStebByStep();
			this.currentSimTime += this.timeStep;
		}
		stopSimulation();
		System.out.println(simTime()+"\t Symulacja zakończona.");
	}
	
	public void setEndSimTime(double endSimTime) {
		this.stopSimTime = endSimTime;
	}
	
	private final void runStebByStep() throws Exception {
		// DO WYKONANIA NA LABORATORIUM:
		for(SimStep simStepWorker : this.simStepWorkers){
			simStepWorker.stateChange();
		}
	}
}
