package memoriacache.utilidades;

import java.util.HashMap;
import java.util.Map;

public class Utilidades {

    //Mapa com cada letra e seu respectivo número em hex
    private static final Map<Character, Integer> hexTable = new HashMap<>();

    //Carrega a tabela hex
    static {
        char c = 'A';
        int num = 10;
        while (c != 'G')
            hexTable.put(c++, num++);
    }

    //Checa se uma string não é um número inteiro
    public static boolean naoEhDigito(String string) {
        try {
            Integer.parseInt(string);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    //Transforma um hex em binário
    public static String hexToBin(String hex) {
        StringBuilder bin = new StringBuilder();
        for (char c : hex.toCharArray()) {
            String s = String.valueOf(c);
            int i = naoEhDigito(s) ? hexTable.get(c) : Integer.parseInt(s);
            bin.append(intToBin(i));
        }

        return bin.toString();
    }

    //Transforma um inteiro em binário
    public static String intToBin(int i) {
        StringBuilder bin = new StringBuilder();
        int divide = 8;
        while (i != 0) {
            if (i / divide > 0) {
                bin.append("1");
                i %= divide;
            } else
                bin.append("0");

            divide /= 2;
        }

        while (bin.length() != 4)
            bin.append("0");

        return bin.toString();
    }

    //Pegar o número x em que 2^x = n;
    public static int pegarPotencia(int n) {
        if (n == 1) return 0;
        int pot = 1;
        while (Math.pow(2, pot) != n)
            pot++;
        return pot;
    }
}
