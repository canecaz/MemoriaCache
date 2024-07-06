package memoriacache.programa;

import memoriacache.config.Config;
import memoriacache.enums.Substitution;
import memoriacache.utilidades.Utilidades;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LerParametros {

    private final Scanner scanner;

    //Ler todos os parâmetros necessários para a simulação
    public LerParametros(Config config) {
        File file = new File("parametros.txt");
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Arquivo 'parâmetros.txt' não encontrado.");
        }

        int politicaEscrita = pegarPoliticaDoScanner();
        config.setPoliticaEscrita(politicaEscrita);

        int tamanhoLinha = pegarIntDoScanner("Insira o tamanho da linha (potência de 2): ");
        config.setTamanhoLinha(tamanhoLinha);

        int numeroLinhas = pegarIntDoScanner("Insira o número de linhas (potência de 2): ");
        config.setNumeroLinhas(numeroLinhas);

        int associatividade = pegarAssociatividadeDoScanner(numeroLinhas);
        config.setAssociatividade(associatividade);

        config.setNumeroConjuntos(numeroLinhas / associatividade);

        int hitTime = pegarTempoDoScanner("Insira o hit time (em nanosegundos): ");
        config.setHitTime(hitTime);

        Substitution substitution = pegarSubstituicaoDoScanner();
        config.setSubstitution(substitution);

        int readTime = pegarTempoDoScanner("Insira o tempo de leitura da MP (em nanosegundos): ");
        config.setReadTime(readTime);

        int writeTime = pegarTempoDoScanner("Insira o tempo de escrita da MP (em nanosegundos): ");
        config.setWriteTime(writeTime);

//        System.out.print("Insira o nome do arquivo texto que será gerado: ");
        String arquivoTexto = scanner.nextLine().split(": ")[1];
        if (arquivoTexto.toLowerCase().endsWith(".txt"))
            arquivoTexto = arquivoTexto.substring(0, arquivoTexto.length() - 4);
        arquivoTexto += ".txt";

        config.setArquivoTexto(arquivoTexto);

        String arquivoEntrada = pegarArquivoEntrada();
        config.setArquivoEntrada(arquivoEntrada);

        scanner.close();
    }

    //Solicita a política de escrita até que uma válida seja inserida
    private int pegarPoliticaDoScanner() {
        int i;
        while (true) {
//            System.out.print("Insira a política de escrita (0 - write-through | 1 - write-back): ");
            String s = scanner.nextLine().split(": ")[1];
            if (Utilidades.naoEhDigito(s)) {
                System.out.println("Número inválido!");
                continue;
            }

            i = Integer.parseInt(s);
            if (i != 0 && i != 1) {
                System.out.println("O número precisa ser ou 0 ou 1.");
                continue;
            }

            break;
        }

        return i;
    }

    //Solicita um inteiro até que um válido, potência de 2, seja inserido
    private int pegarIntDoScanner(String message) {
        int i;
        while (true) {
//            System.out.print(message);
            String s = scanner.nextLine().split(": ")[1];
            if (Utilidades.naoEhDigito(s)) {
                System.out.println("Número inválido!");
                continue;
            }

            i = Integer.parseInt(s);
            if (!ehPotenciaDe2(i)) {
                System.out.println("O número precisa ser potência de 2!");
                continue;
            }

            if (i < 1) {
                System.out.println("O número precisa ser maior que 0.");
                continue;
            }

            break;
        }

        return i;
    }

    //Solicita a associatividade até que uma válida seja inserida
    private int pegarAssociatividadeDoScanner(int numeroLinhas) {
        int i;
        while (true) {
            i = pegarIntDoScanner("Insira a associatividade (potência de 2): ");
            if (i > numeroLinhas) {
                System.out.println("O número tem que ser entre 1 e o número de linhas (" + numeroLinhas + ")!");
                continue;
            }

//            if (i == numeroLinhas) {
//                System.out.println("Não utilize o mesmo número que utilizou para o número de linhas.");
//                continue;
//            }

            break;
        }

        return i;
    }

    //Solicita um tempo até que um válido seja inserido
    private int pegarTempoDoScanner(String message) {
        int i;
        while (true) {
//            System.out.print(message);
            String s = scanner.nextLine().split(": ")[1];
            if (Utilidades.naoEhDigito(s)) {
                System.out.println("Número inválido!");
                continue;
            }

            i = Integer.parseInt(s);
            if (i < 1) {
                System.out.println("O número precisa ser maior que 0.");
                continue;
            }

            break;
        }

        return i;
    }

    //Solicita a política de substituição até que uma válida seja inserida
    private Substitution pegarSubstituicaoDoScanner() {
        Substitution substitution;
        while (true) {
//            System.out.print("Insira a política de substituição (LFU | LRU | Random): ");
            String s = scanner.nextLine().split(": ")[1];
            if (!s.equalsIgnoreCase("lfu") && !s.equalsIgnoreCase("lru")
                    && !s.equalsIgnoreCase("random")) {
                System.out.println("Política inválida!");
                continue;
            }

            substitution = Substitution.valueOf(s.toUpperCase());
            break;
        }

        return substitution;
    }

    //Solicita o arquivo de entrada que um válido seja inserido
    private String pegarArquivoEntrada() {
        String string;
        while (true) {
//            System.out.print("Insira o arquivo de entrada (teste | oficial): ");
            string = scanner.nextLine().split(": ")[1].toLowerCase();

            if (!string.equalsIgnoreCase("teste") && !string.equalsIgnoreCase("oficial")) {
                System.out.println("Arquivo inválido!");
                continue;
            }

            break;
        }

        return string + ".cache";
    }

    //Checa se um inteiro é potência de 2
    private boolean ehPotenciaDe2(int n) {
        if (n == 0)
            return false;

        while (n != 1) {
            if (n % 2 != 0)
                return false;

            n = n / 2;
        }

        return true;
    }
}
