public class MyList {
    Node start;
    Node end;

    public MyList() {
        start = new Node();
        start.next = new Node();
        end = start.next;
    }

    public void add(int exponent, double coefficient) {
        Node prev = start;
        prev.lock();
        Node current = start.next;
        current.lock();

        //lista goala
        if (current == end) {
            Node newNode = new Node(exponent, coefficient);
            newNode.next = end;
            start.next = newNode;
        }

        //lista are cel putin un monom
        while (current != end) {
            if (current.exponent == exponent) {
                //exista monom cu exponent, ii modific coeficientul
                current.coefficient += coefficient;
                if (current.coefficient == 0) {
                    //daca coeficientul e 0, sterg monomul
                    current.next.lock();
                    prev.next = current.next;
                    current.next.unlock();
                }
                break;
            } else if (current.exponent > exponent) {
                //nu exista monom cu acel exponent, dar i-am gasit pozitia unde trebuie adaugat
                Node newNode = new Node(exponent, coefficient);
                newNode.next = current;
                prev.next = newNode;
                break;
            }
            prev.unlock();
            prev = current;
            current = current.next;
            current.lock();
        }

        if (current == end) {
            //nu exista monom cu acel exponent, trebuie adaugat la final
            Node newNode = new Node(exponent, coefficient);
            newNode.next = end;
            prev.next = newNode;
        }

        prev.unlock();
        current.unlock();
    }
}
