package sim.smo.events;

import sim.core.Manager;
import sim.smo.ServiceSystem;
import sim.smo.objects.Client;
import sim.smo.objects.EClientStates;

/* Klasa odpowiedzialna za opuszczanie kolejki przez klientów, którzy przebywali w niej dłużej niż ich czas zniecierpliwienia */
public class LeaveQueue extends ServiceSystemEvent {

    public LeaveQueue(Manager mgr, ServiceSystem serviceSystem) {
        super(mgr, serviceSystem);
    }

    /* Opuszczenie przez klientów kolejki z powodu zniecierpliwienia */
    @Override
    public void stateChange() {
        for(int i=0; i<serviceSystem.queue.size(); i++) {
            Client client = serviceSystem.queue.get(i);
            if(simTime() >= client.getTimeLeave()){
                serviceSystem.queue.remove(client); /* opuszczenie kolejki przez klienta */
                serviceSystem.queueLength.setValue(serviceSystem.queue.size(), simTime()); /* ustawienie wartości dla monitorowanej zmiennej queueLength */
                client.changeState(EClientStates.LeftBecauseOfImpatience, simTime()); /* ustawienie statusu klientowi */
                System.out.println(simTime()+"\t Opuszczenie kolejki przez klienta. Klient "+client.id+" opuścił kolejkę z powodu zniecierpliwienia.");
            }
        }

    }
}
