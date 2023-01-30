public class Node {
    public int idParticipant;
    public int punctaj;
    public int rapiditate;
    Node next;

    public Node() {}

    public Node(int idParticipant, int punctaj, int rapiditate) {
        this.idParticipant = idParticipant;
        this.punctaj = punctaj;
        this.rapiditate = rapiditate;
    }
}
