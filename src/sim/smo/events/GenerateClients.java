package sim.smo.events;

import sim.core.Manager;
import sim.random.SimGenerator;
import sim.smo.ServiceSystem;
import sim.smo.objects.Client;

/* Klasa odpowiedzialna za generowanie nowych klientów i dodawanie ich do symulacji*/
public class GenerateClients extends ServiceSystemEvent {
    double t_time; /* punkt czasu, w którym zdarzenie powinno zostać wykonane */
    double t_impatience_min; /* minimalny czas zniecierpliwienia klientów */
    double t_impatience_max; /* maksymalny czas zniecierpliwienia klientów */
    double a; /* parametr funkcji wykładniczej wykorzystywany przy generowaniu odstępu czasu pomiędzy pojawieniem się kolejnych klientów */

    public GenerateClients(Manager mgr, ServiceSystem serviceSystem, double t_start, double t_impatience_min, double t_impatience_max, double a) {
        super(mgr, serviceSystem);
        this.t_time = t_start; /* ustawienie czasu pojawienia się pierwszego zgłoszenia */
        this.t_impatience_min = t_impatience_min;
        this.t_impatience_max = t_impatience_max;
        this.a = a;
    }

    /* Wygenerowanie nowego klienta i dodanie go do kolejki */
    @Override
    public void stateChange() {
        if(simTime() >= this.t_time){
            Client client = new Client(simTime(), t_impatience_min, t_impatience_max); /* wygenerowanie nowego klienta */
            serviceSystem.queue.add(client); /* dodanie klienta do kolejki */
            serviceSystem.queueLength.setValue(serviceSystem.queue.size(), simTime()); /* ustawienie wartości dla monitorowanej zmiennej queueLength */
            serviceSystem.clients.add(client); /* dodanie klienta do listy klientów */
            System.out.println(simTime()+"\t Pojawił się nowy klient. Klient "+client.id+" dołączył do kolejki.");
            planNextEvent(); /* zaplanowanie pojawienia się kolejnego klienta */
        }
    }

    /* Zaplanowanie pojawienia się kolejnego klienta */
    private void planNextEvent() {
        SimGenerator sg = new SimGenerator(); /* utworzenie nowego generatora bez podania ziarna */
        double interval = sg.exponential(this.a); /* wygenerowanie nowej liczby o rozkładzie wykładniczym (a) - odstęp czasiu między pojawieniem się kolejnego klienta */
        this.t_time += interval; /* punkt czasu, w którym pojawi się kolejny klient */
    }
}
