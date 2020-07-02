package sim.smo;

import sim.core.Manager;
import sim.monitors.Diagram;
import sim.monitors.MonitoredVar;
import sim.monitors.Statistics;
import sim.smo.events.EndService;
import sim.smo.events.GenerateClients;
import sim.smo.events.LeaveQueue;
import sim.smo.events.StartService;
import sim.smo.objects.Client;
import sim.smo.objects.ServicePosition;
import java.awt.Color;
import java.util.ArrayList;

/* Klasa pełniąca rolę koordynatora symulacji obsługi klientów */
public class ServiceSystem {
    private static ServiceSystem serviceSystem; // Singleton
    public Manager mgr;
    public ArrayList<ServicePosition> servicePositions; /* lista stanowisk obsługi */
    public ArrayList<Client> clients; /* lista wygenerowanych klientów */
    public ArrayList<Client> queue; /* lista klientów znajdujących się w kolejce */
    double t_impatience_min;
    double t_impatience_max;
    double a;
    public MonitoredVar queueLength; /* monitorowana zmienna reprezentująca długość kolejki */
    public MonitoredVar waitTime; /* monitorowana zmienna reprezentująca czasy czekania obsłużonych klientów w kolejce */

    public static ServiceSystem getInstance(int n_service_positions, double startSimTime, double timeStep, double t_service_min, double t_service_max, double t_impatience_min, double t_impatience_max, double a) {
        if (serviceSystem == null) {
            serviceSystem = new ServiceSystem(n_service_positions, startSimTime, timeStep, t_service_min, t_service_max, t_impatience_min, t_impatience_max, a);
        }
        return serviceSystem;
    }

    /* Argumenty:
       int n_service_positions -- liczba pozycji obsługi
       double startSimTime -- punkt czasu rozpoczęcia symulacji
       double timeStep -- krok zmiany czasu podczas symulacji
       double t_service_min -- minimalny czas obsługi przez stanowisko (parametr wykorzystywany przy generowaniu czasu obsługi klienta)
       double t_service_max -- maksymalny czas obsługi przez stanowisko (parametr wykorzystywany przy generowaniu czasu obsługi klienta)
       double t_impatience_min -- minimalny czas zniecierpliwienia klientów (parametr wykorzystywany przy generowaniu czasu zniecierpliwienia klienta)
       double t_impatience_max -- maksymalny czas zniecierpliwienia klientów (parametr wykorzystywany przy generowaniu czasu zniecierpliwienia klienta)
       double a -- parametr funkcji wykładniczej wykorzystywany przy generowaniu odstępu czasu pomiędzy pojawieniem się kolejnych klientów
    */
    private ServiceSystem(int n_service_positions, double startSimTime, double timeStep, double t_service_min, double t_service_max, double t_impatience_min, double t_impatience_max, double a){
        if(t_service_min > t_service_max || t_impatience_min > t_impatience_max) {
            throw new IllegalArgumentException();
        }
        this.mgr = Manager.getInstance(startSimTime, timeStep);
        /* Utworzenie stanowisk obsługi */
        this.servicePositions = new ArrayList<>();
        for(int i=0; i<n_service_positions; i++){
            servicePositions.add(new ServicePosition(t_service_min, t_service_max));
        }

        this.clients = new ArrayList<>();
        this.queue = new ArrayList<>();

        queueLength = new MonitoredVar();
        queueLength.setValue(queue.size());

        waitTime = new MonitoredVar();

        this.t_impatience_min = t_impatience_min;
        this.t_impatience_max = t_impatience_max;
        this.a = a;

        /* Powołanie obiektów funkcji zmiany stanu
           obiekty te rejestrowane są przez Menager-a symulacji poprzez wywołanie: simMngr.registerSimStep(this) w konstruktorze klasy nadrzędnej (SimStep) */
        new GenerateClients(mgr, this, mgr.simTime(), t_impatience_min, t_impatience_max, a);
        new EndService(mgr, this);
        new StartService(mgr, this);
        new LeaveQueue(mgr, this);
    }

    /* Rozpoczęcie symulacji */
    public void simulate(double endSimTime) throws Exception {
        mgr.setEndSimTime(endSimTime);
        mgr.startSimulation();
        showStats();
        showDiagrams();
    }

    /* Zwraca informację, czy jest jakieś wolne stanowisko */
    public boolean isAnyServicePositionFree(){
        for(ServicePosition servicePosition: servicePositions) {
            if(servicePosition.isFree()){
                return true;
            }
        }
        return false;
    }

    /* Zwraca informację, czy jest jakieś zajęte stanowisko */
    public boolean isAnyServicePositionBusy(){
        for(ServicePosition servicePosition: servicePositions) {
            if(servicePosition.isBusy()){
                return true;
            }
        }
        return false;
    }

    /* Zwraca wolne stanowisko */
    public ServicePosition getFreeServicePosition() throws Exception {
        for(ServicePosition servicePosition: servicePositions) {
            if(servicePosition.isFree()){
                return servicePosition;
            }
        }
        throw new Exception("There is NOT any free service position.");
    }

    /* Zwraca informację, czy jest jakiś klient w kolejce */
    public boolean isAnyClientInQueue(){
        return queue.size()>0;
    }

    /* Zwraca klienta czekającego w kolejce */
    public Client getClientFromQueue() throws Exception {
        for(Client client: queue) {
            return client;
        }
        throw new Exception("There is NOT any client in queue.");
    }

    /* Wypisuje statystyki */
    private void showStats(){
        int n_servedClients=0;
        int n_leftClients=0;
        int n_clientsInServicePosition=0;
        int n_clientsInQueue=0;
        for(Client client: clients){
            if(client.isServed()){
                n_servedClients++;
            } else if (client.hasLeftBecauseOfImpatience()) {
                n_leftClients++;
            } else if (client.isInServicePosition()) {
                n_clientsInServicePosition++;
            } else if (client.isWaitingInQueue()) {
                n_clientsInQueue++;
            }
        }
        System.out.println("================================================");
        System.out.println("STATYSTYKI:");
        System.out.println("================================================");
        System.out.println("Liczba wygenerowanych klientów: "+clients.size());
        System.out.println("Liczba obsłużonych klientów: "+n_servedClients);
        System.out.println("Liczba klientów, która opuściła kolejkę z powodu zniecierpliwienia: "+n_leftClients);
        System.out.println("Liczba klientów obecnie obsługiwanych (w momencie zakończenia symulacji): "+n_clientsInServicePosition+"| Liczba stanowisk: "+servicePositions.size());
        System.out.println("Liczba klientów czekających w kolejce (w momencie zakończenia symulacji): "+n_clientsInQueue+"| Długość kolejki (w momencie zakończenia symulacji): "+queue.size());
        System.out.println("************************");
        double minWaitTime = Statistics.min(waitTime);
        double maxWaitTime = Statistics.max(waitTime);
        double meanWaitTime = Statistics.arithmeticMean(waitTime);
        System.out.println("Minimalny czas oczekiwania na obsługę (przez obsłużonych klientów):"+minWaitTime);
        System.out.println("Maksymalny czas oczekiwania na obsługę (przez obsłużonych klientów):"+maxWaitTime);
        System.out.println("Średni czas oczekiwania na obsługę (przez obsłużonych klientów):"+meanWaitTime);
        System.out.println("************************");
        double minImpatienceTime = clients.stream().mapToDouble(client -> client.t_impatience).min().orElse(Double.POSITIVE_INFINITY);
        double maxImpatienceTime = clients.stream().mapToDouble(client -> client.t_impatience).max().orElse(Double.POSITIVE_INFINITY);
        double meanImpatienceTime = clients.stream().mapToDouble(client -> client.t_impatience).average().orElse(Double.POSITIVE_INFINITY);
        System.out.println("Minimalny czas zniecierpliwienia klientów: "+minImpatienceTime);
        System.out.println("Maksymalny czas zniecierpliwienia klientów: "+maxImpatienceTime);
        System.out.println("Średni czas zniecierpliwienia klientów: "+meanImpatienceTime);
        System.out.println("************************");
        System.out.println("Średni czas obsługi przez poszczególne stanowiska:");
        for(ServicePosition servicePosition: servicePositions) {
            double meanServiceTime = Statistics.arithmeticMean(servicePosition.serviceTime);
            System.out.println("* Stanowisko "+servicePosition.id+" - "+meanServiceTime);
        }
        System.out.println("************************");
        double minQueueLength = Statistics.min(queueLength);
        double maxQueueLength = Statistics.max(queueLength);
        double meanQueueLength = Statistics.arithmeticMean(queueLength);
        System.out.println("Minimalna długość kolejki: "+minQueueLength);
        System.out.println("Maksymalna długość kolejki: "+maxQueueLength);
        System.out.println("Średnia długość kolejki: "+meanQueueLength);
        System.out.println("================================================");
    }

    private void showDiagrams(){
        Diagram d1 = new Diagram(Diagram.DiagramType.TIME_FUNCTION,"Czasy oczekiwania na obsługę");
        d1.add(waitTime, java.awt.Color.BLUE);
        d1.show();

        Diagram d2 = new Diagram(Diagram.DiagramType.TIME_FUNCTION,"Długość kolejki");
        d2.add(queueLength, java.awt.Color.BLUE);
        d2.show();

        Diagram d3 = new Diagram(Diagram.DiagramType.TIME_FUNCTION,"Czasy obsługi stanowisk");
        for (ServicePosition servicePosition : servicePositions) {
            Color color = new Color((int) (Math.random() * 0x1000000));
            d3.add(servicePosition.serviceTime, color);
        }
        d3.show();
    }
}
