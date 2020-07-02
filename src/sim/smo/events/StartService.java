package sim.smo.events;

import sim.core.Manager;
import sim.smo.ServiceSystem;
import sim.smo.objects.Client;
import sim.smo.objects.EClientStates;
import sim.smo.objects.ServicePosition;

/* Klasa odpowiedzialna za rozpoczęcie obsługi klientów */
public class StartService extends ServiceSystemEvent {

    public StartService(Manager mgr, ServiceSystem serviceSystem) {
        super(mgr, serviceSystem);
    }

    /* Rozpoczęcie obsługi klienta, który czeka w kolejce */
    @Override
    public void stateChange() throws Exception {
        while(serviceSystem.isAnyClientInQueue() && serviceSystem.isAnyServicePositionFree()){
            ServicePosition servicePosition = serviceSystem.getFreeServicePosition(); /* pobranie wolnego stanowiska */
            Client client = serviceSystem.getClientFromQueue(); /* pobranie (pierwszego) klienta czekającego w kolejce */
            serviceSystem.queue.remove(client); /* usunięcie klienta z kolejki */
            serviceSystem.queueLength.setValue(serviceSystem.queue.size(), simTime()); /* ustawienie wartości dla monitorowanej zmiennej queueLength */
            servicePosition.startService(client, simTime()); /* rozpoczęcie obsługi */
            serviceSystem.waitTime.setValue(simTime()-client.t_start, simTime()); /* ustawienie wartości dla monitorowanej zmiennej waitTime */
            client.changeState(EClientStates.InServicePosition, simTime()); /* ustawienie statusu klientowi */
            System.out.println(simTime()+"\t Rozpoczęto obsługę klienta. Stanowisko "+servicePosition.id+" rozpoczęło obsługę klienta "+client.id+".");
        }
    }
}
