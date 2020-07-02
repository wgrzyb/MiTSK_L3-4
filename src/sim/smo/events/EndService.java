package sim.smo.events;

import sim.core.Manager;
import sim.smo.ServiceSystem;
import sim.smo.objects.EClientStates;
import sim.smo.objects.ServicePosition;

/* Klasa odpowiedzialna za zakończenie obsługi klientów */
public class EndService extends ServiceSystemEvent{

    public EndService(Manager mgr, ServiceSystem serviceSystem) { super(mgr, serviceSystem); }

    /* Zakończenie obsługi klienta, który został obsłużony */
    @Override
    public void stateChange() throws Exception {
        if(serviceSystem.isAnyServicePositionBusy()){
            for(ServicePosition servicePosition: serviceSystem.servicePositions){
                if(servicePosition.isBusy() && simTime() >= servicePosition.getServiceEndTime()) {
                    servicePosition.service.client.changeState(EClientStates.Served, simTime()); /* ustawienie statusu klientowi */
                    System.out.println(simTime()+"\t Zakończono obsługę klienta. Stanowisko "+servicePosition.id+" zakończyło obsługę klienta "+servicePosition.service.client.id+".");
                    servicePosition.endService(); /* zakończenie obsługi */
                }
            }
        }
    }
}
