package sim.smo.objects;

/* Stany w jakich może znaleźć się klient */
public enum EClientStates {
    WaitingInQueue,
    LeftBecauseOfImpatience,
    InServicePosition,
    Served
}
