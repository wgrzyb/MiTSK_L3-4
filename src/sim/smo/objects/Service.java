package sim.smo.objects;

/* Klasa reprezuntująca obsługę klienta przez stanowisko */
public class Service {
    public Client client; /* obsługiwany klient */
    public double t_start; /* punkt czasu rozpoczęcia obsługi */
    public double t_end; /* punkt czasu zakończenia obsługi */

    public Service(Client client, double t_start, double t_end){
        this.client = client;
        this.t_start = t_start;
        this.t_end = t_end;
    }
}
