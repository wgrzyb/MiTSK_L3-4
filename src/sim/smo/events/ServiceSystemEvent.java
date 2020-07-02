package sim.smo.events;

import sim.core.Manager;
import sim.core.SimStep;
import sim.smo.ServiceSystem;

/* Klasa abstrakcyjna reprezentująca zdarzenia pojawiające się w trakcie symulacji */
public abstract class ServiceSystemEvent extends SimStep {
    ServiceSystem serviceSystem;

    public ServiceSystemEvent(Manager mgr, ServiceSystem serviceSystem) {
        super(mgr);
        this.serviceSystem = serviceSystem;
    }

    public abstract void stateChange() throws Exception;
}
