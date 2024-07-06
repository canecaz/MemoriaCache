package memoriacache.objetos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Conjunto {

    //Lista encadeada com os rótulos dos blocos do conjunto
    private final LinkedList<String> linkedList = new LinkedList<>();

    //Mapa para saber qual o bloco menos recentemente usado
    private final Map<String, Integer> tempoUsado = new HashMap<>();

    //Mapa para saber qual o bloco que teve menos acertos
    private final Map<String, Integer> quantiaHits = new HashMap<>();

    private int currentTempo = 0;

    public LinkedList<String> getLinkedList() {
        return linkedList;
    }

    public Map<String, Integer> getTempoUsado() {
        return tempoUsado;
    }

    public Map<String, Integer> getQuantiaHits() {
        return quantiaHits;
    }

    public int getCurrentTempo() {
        return currentTempo;
    }

    public void addCurrentTempo() {
        this.currentTempo++;
    }
}
