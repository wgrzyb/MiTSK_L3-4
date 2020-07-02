package sim.smo.objects;

import sim.random.SimGenerator;

/* Klasa reprezentująca klienta */
public class Client {
    static int n_clients = 0;
    public int id; /* unikalny identyfikator klienta */
    public double t_impatience; /* czas zniecierpliwienia klienta; maksymalny czas jaki klient może przebywać w kolejce */
    EClientStates state; /* stan w jakim znajduje się klient */
    public double t_start; /* punkt czasu, w którym został ustawiony aktualny stan klienta */

    public Client(double t_start, double t_impatience_min, double t_impatience_max){
        this.id = ++n_clients;
        SimGenerator sg = new SimGenerator();
        this.t_impatience = sg.uniform(t_impatience_min,t_impatience_max); /* wygenerowanie nowej liczby o rozkładzie jednostajnym (t_impatience_min, t_impatience_max) - czas zniecierpliwienia klienta */
        changeState(EClientStates.WaitingInQueue, t_start);
    }

    /* Ustawia stan klientowi */
    public void changeState(EClientStates state, double curr_time){
        if(this.state != state) {
            this.state = state;
            this.t_start = curr_time;
        } else {
            System.out.println("Klient: "+this.id+" znajduje się już w podanym stanie!");
        }
    }

    /* Zwraca informację, czy klient czeka w kolejce */
    public boolean isWaitingInQueue(){
        return state == EClientStates.WaitingInQueue;
    }

    /* Zwraca informację, czy klient jest obsługiwany */
    public boolean isInServicePosition(){
        return state == EClientStates.InServicePosition;
    }

    /* Zwraca informację, czy klient opuścił kolejkę z powodu zniecierpliwienia */
    public boolean hasLeftBecauseOfImpatience(){
        return state == EClientStates.LeftBecauseOfImpatience;
    }

    /* Zwraca informację, czy klient został obsłużony */
    public boolean isServed(){
        return state == EClientStates.Served;
    }

    /* Zwraca punkt czasu, w którym klient opuści kolejkę z powodu zniecierpliwienia */
    public double getTimeLeave(){
        return (state == EClientStates.WaitingInQueue) ? t_start + t_impatience : Double.POSITIVE_INFINITY;
    }
}
