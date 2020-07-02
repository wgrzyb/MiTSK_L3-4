package sim.core;

public abstract class SimStep {
	private Manager simMgr;
	
	public SimStep(Manager mgr) {
		if (mgr!=null) {
			simMgr = mgr;
			// 	rejestracja w Managerze symulacji
			simMgr.registerSimStep(this);
		}
	}
	
	public final double simTime() {
		if (simMgr!=null)
			return simMgr.simTime();
		else 
			return 0.0;
	}

	public abstract void stateChange() throws Exception;
}
