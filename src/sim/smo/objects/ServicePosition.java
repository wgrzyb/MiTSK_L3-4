package sim.smo.objects;

import sim.monitors.MonitoredVar;
import sim.random.SimGenerator;

/* Klasa reprezentująca stanowisko - pozycję obsługi */
public class ServicePosition {
    static int n_service_positions = 0;
    public int id; /* unikalny identyfikator stanowiska */
    public Service service; /* obecna obsługa klienta, jeśli null to stanowisko jest wolne */
    double t_service_min; /* minimalny czas obsługi przez stanowisko */
    double t_service_max; /* maksymalny czas obsługi przez stanowisko */
    public MonitoredVar serviceTime; /* monitorowana zmienna reprezentująca czasy obsługi klientów */

    public ServicePosition(double t_service_min, double t_service_max) {
        this.id = ++n_service_positions;
        this.service = null;
        this.t_service_min = t_service_min;
        this.t_service_max = t_service_max;
        this.serviceTime = new MonitoredVar();
    }

    /* Zwraca informację, czy stanowisko jest zajęte */
    public boolean isBusy() {
        return service != null;
    }

    /* Zwraca informację, czy stanowisko jest wolne */
    public boolean isFree() {
        return service == null;
    }

    /* Wygenerowanie czasu obsługi */
    private double generateServiceTime(){
        SimGenerator sg = new SimGenerator();
        return sg.uniform(t_service_min,t_service_max); /* wygenerowanie liczby o rozkładzie jednostajnym (t_service_min, t_service_max) - czas obsługi przez stanowisko */
    }

    /* Rozpoczęcie obsługi klienta */
    public void startService(Client client, double curr_time) {
        double t_service = generateServiceTime();
        this.service = new Service(client, curr_time, curr_time + t_service);
        serviceTime.setValue(t_service, curr_time); /* ustawienie wartości dla monitorowanej zmiennej serviceTime */
    }

    /* Zwraca punkt czasu, w którym zakończy się obsługa klienta */
    public double getServiceEndTime() throws Exception {
        if(service != null) {
            return service.t_end;
        } else {
            throw new Exception("Service Position "+this.id+" is NOT busy now.");
        }
    }

    /* Zakończenie obsługi klienta */
    public void endService() {
        this.service = null;
    }
}
