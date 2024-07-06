package memoriacache.enums;

public enum Substitution {

    LFU("LFU"),
    LRU("LRU"),
    RANDOM("Aleatório");

    private final String name;

    Substitution(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
