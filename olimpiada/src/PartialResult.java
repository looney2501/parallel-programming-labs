public class PartialResult {
    public int idConcurent;
    public int idProblema;
    public int punctaj;
    public int timpIncarcare;

    public PartialResult(int idConcurent, int idProblema, int punctaj, int timpIncarcare) {
        this.idConcurent = idConcurent;
        this.idProblema = idProblema;
        this.punctaj = punctaj;
        this.timpIncarcare = timpIncarcare;
    }

    @Override
    public String toString() {
        return "PartialResult{" +
                "idConcurent=" + idConcurent +
                ", idProblema=" + idProblema +
                ", punctaj=" + punctaj +
                ", timpIncarcare=" + timpIncarcare +
                '}';
    }
}
