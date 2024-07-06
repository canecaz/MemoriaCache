package memoriacache.programa;

import memoriacache.config.Config;
import memoriacache.objetos.Conjunto;
import memoriacache.utilidades.Utilidades;

import java.io.*;
import java.util.*;

public class Programa {

    //Todos os parâmetros inseridos pelo usuário
    private final Config config;

    //Todos os inteiros necessários durante a simulação
    private int quantiaLeitura, quantiaEscrita,
            hitsEscrita, hitsLeitura, missesEscrita, missesLeitura,
            enderecoEscrita, enderecoLeitura;

    //Mapa que simula uma cache
    private final Map<String, Conjunto> cache = new HashMap<>();

    //Set contendo os endereços com dirty-bit (usado no write-back)
    private final Set<String> enderecosDirtyBit = new HashSet<>();

    public Programa() {
        //Inicia a configuração e pede os parâmetros para o usuário
        this.config = new Config();
        new LerParametros(config);

        //Leitura do arquivo de entrada
        lerArquivo();
    }

    private void lerArquivo() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(config.getArquivoEntrada()));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.toUpperCase();
                String[] split = line.split(" ");

                //Gerencia o dado presente em cada linha do arquivo
                gerenciarDado(split);
            }

            //Adiciona os endereços restantes no set de dirty-bits na quantia de escritas
            quantiaEscrita += enderecosDirtyBit.size();

            //Calcula o total de hits e misses
            int hits = hitsEscrita + hitsLeitura;
            int misses = missesEscrita + missesLeitura;

            //Cálculos de taxas de acerto e tempo médio de acesso
            double taxaDeAcertoGlobal = (double) (hits - 1) / (hits + misses);
            double tempoMedio = taxaDeAcertoGlobal * config.getHitTime() + (1 - taxaDeAcertoGlobal)
                    * (config.getHitTime() + config.getReadTime());
            taxaDeAcertoGlobal *= 100;

            double taxaDeAcertoEscrita = ((double) hitsEscrita / (hitsEscrita + missesEscrita)) * 100;
            double taxaDeAcertoLeitura = ((double) hitsLeitura / (hitsLeitura + missesLeitura)) * 100;

            //Gera o arquivo de saída com os resultados obtidos
            gerarResultados(taxaDeAcertoGlobal, taxaDeAcertoEscrita, taxaDeAcertoLeitura, tempoMedio);

            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void gerarResultados(double taxaDeAcertoGlobal, double taxaDeAcertoEscrita,
                                 double taxaDeAcertoLeitura, double tempoMedio) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(config.getArquivoTexto()));

            escreverLinha(bw, "Política de escrita: " +
                    config.getPoliticaEscrita() + " (write-" +
                    (config.getPoliticaEscrita() == 0 ? "through" : "back") + ")");
            escreverLinha(bw, "Tamanho da linha: " + config.getTamanhoLinha() + " bytes");
            escreverLinha(bw, "Número de linhas: " + config.getNumeroLinhas());
            escreverLinha(bw, "Número de conjuntos: " + config.getNumeroConjuntos());
            escreverLinha(bw, "Associatividade: " + config.getAssociatividade());
            escreverLinha(bw, "Hit Time: " + config.getHitTime() + " ns");
            escreverLinha(bw, "Política de substituição: " + config.getSubstitution().getName());
            escreverLinha(bw, "Tempo de leitura da MP: " + config.getReadTime() + " ns");
            escreverLinha(bw, "Tempo de escrita da MP: " + config.getWriteTime() + " ns");
            escreverLinha(bw, "Arquivo de entrada utilizado: " + config.getArquivoEntrada());
            escreverLinha(bw, "Endereços no arquivo de entrada: " + (enderecoEscrita + enderecoLeitura) +
                    " (" + enderecoEscrita + " de escrita e " + enderecoLeitura + " de leitura)");
            escreverLinha(bw, " ");
            escreverLinha(bw, "Cache hits: " + (hitsLeitura + hitsEscrita));
            escreverLinha(bw, "Cache misses: " + (missesLeitura + missesEscrita));
            escreverLinha(bw, "Leituras na MP: " + quantiaLeitura);
            escreverLinha(bw, "Escritas da MP: " + quantiaEscrita);
            escreverLinha(bw, "Total de escritas e leituras na MP: " + (quantiaLeitura + quantiaEscrita));
            escreverLinha(bw, String.format("Taxa de acerto de escrita: %.4f%%", taxaDeAcertoEscrita));
            escreverLinha(bw, String.format("Taxa de acerto de leitura: %.4f%%", taxaDeAcertoLeitura));
            escreverLinha(bw, String.format("Taxa de acerto global: %.4f%%", taxaDeAcertoGlobal));
            escreverLinha(bw, String.format("Tempo médio de acesso: %.4f ns", tempoMedio));

            bw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void escreverLinha(BufferedWriter bw, String linha) throws IOException {
        bw.write(linha);
        bw.newLine();
    }

    //Gerenciamento dos dados de entrada
    private void gerenciarDado(String[] split) {
        //Transformação da string hex em binário
        String bin = Utilidades.hexToBin(split[0]);

        //Cálculo dos bits necessários para cada campo do endereço
        int numeroConjuntos = config.getNumeroConjuntos();
        int tamanhoLinha = config.getTamanhoLinha();
        int bitsConjunto = Utilidades.pegarPotencia(numeroConjuntos);
        int bitsPalavra = Utilidades.pegarPotencia(tamanhoLinha);
        int bitsRotulo = 32 - bitsConjunto - bitsPalavra;

        String rest = bin;
        String rotulo = rest.substring(0, bitsRotulo);
        rest = rest.substring(bitsRotulo);
        String conjunto = rest.substring(0, bitsConjunto);

        boolean read = split[1].equalsIgnoreCase("R");
        boolean writeBack = config.getPoliticaEscrita() == 1;
        boolean hit = false;

        if (read)
            enderecoLeitura++;
        else
            enderecoEscrita++;

        //Conjunto não está na cache
        if (!cache.containsKey(conjunto)) {
            //Se for operação de leitura, ou operação de escrita write-back
            //write-back traz o bloco para a cache quando ele não está presente
            if (read || writeBack) {
                //Cria um novo conjunto e adiciona o bloco, colocando na cache
                Conjunto c = new Conjunto();
                c.getLinkedList().add(rotulo);
                cache.put(conjunto, c);

                //Incrementa a quantia de misses, dependendo da operação
                if (read)
                    missesLeitura++;
                else
                    missesEscrita++;

                //Incrementa a quantia de leituras
                quantiaLeitura++;
            }
        } else {
            Conjunto c = cache.get(conjunto);

            //Busca do bloco dentro do conjunto
            for (String tag : c.getLinkedList()) {
                if (tag.equalsIgnoreCase(rotulo)) {
                    //Cache hit
                    hit = true;

                    //Incrementa a quantia de hits, dependendo da operação
                    if (read)
                        hitsLeitura++;
                    else
                        hitsEscrita++;

                    //Coloca uma espécie de "tempo" de acesso no bloco
                    c.getTempoUsado().put(rotulo, c.getCurrentTempo());

                    //Incrementa o próximo tempo de acesso
                    c.addCurrentTempo();

                    //Adiciona 1 na quantia de hits do bloco
                    c.getQuantiaHits().put(rotulo, c.getQuantiaHits().getOrDefault(rotulo, 0) + 1);
                    break;
                }
            }

            //Cache miss
            if (!hit) {
                if (read)
                    missesLeitura++;
                else
                    missesEscrita++;

                quantiaLeitura++;

                //Checa se o conjunto já possui a quantia máxima de blocos
                if (c.getLinkedList().size() >= config.getAssociatividade()) {
                    //Conjunto já possui a quantia máxima de blocos, substituição necessária
                    substituirBloco(c, rotulo);
                } else {
                    //Há espaço para blocos no conjunto
                    //Adiciona o bloco no conjunto
                    c.getLinkedList().add(rotulo);
                    c.getTempoUsado().put(rotulo, c.getCurrentTempo());
                    c.addCurrentTempo();
                }
            }
        }

        if (!read) {
            if (!writeBack)
                //Incrementa a quantia de escritas se a política utilizada é a write-through
                quantiaEscrita++;
            else
                //Adiciona o endereço no set de dirty-bits se a política utilizada é a write-back
                enderecosDirtyBit.add(bin);
        }
    }

    //Substituição de blocos
    private void substituirBloco(Conjunto c, String rotulo) {
        String blocoRemovido = null;
        switch (config.getSubstitution()) {
            case LRU: {
                //Procura o bloco menos recentemente usado
                int menor = Integer.MAX_VALUE;
                String menorKey = null;
                for (String key : c.getLinkedList())
                    c.getTempoUsado().putIfAbsent(key, 0);
                for (String key : c.getTempoUsado().keySet()) {
                    if (c.getTempoUsado().get(key) < menor) {
                        menor = c.getTempoUsado().get(key);
                        menorKey = key;
                    }
                }

                //Se por algum motivo a variável ainda for null, para a função
                if (menorKey == null) return;

                int index = -1;
                for (int i = 0; i < c.getLinkedList().size(); i++) {
                    if (c.getLinkedList().get(i).equalsIgnoreCase(menorKey)) {
                        index = i;
                        break;
                    }
                }

                //Se por algum motivo o index ainda for -1, para a função
                if (index == -1) return;

                //Substitui o bloco antigo pelo novo
                blocoRemovido = c.getLinkedList().get(index);
                c.getLinkedList().set(index, rotulo);
                c.getTempoUsado().remove(menorKey);
                c.getQuantiaHits().remove(menorKey);
                c.getTempoUsado().put(rotulo, c.getCurrentTempo());
                c.addCurrentTempo();
                break;
            }

            case LFU: {
                //Procura o bloco que teve menos acertos
                int menor = Integer.MAX_VALUE;
                String menorKey = null;
                for (String key : c.getLinkedList())
                    c.getQuantiaHits().putIfAbsent(key, 0);
                for (String key : c.getQuantiaHits().keySet()) {
                    if (c.getQuantiaHits().get(key) < menor) {
                        menor = c.getQuantiaHits().get(key);
                        menorKey = key;
                    }
                }

                //Se por algum motivo a variável ainda for null, para a função
                if (menorKey == null) return;

                int index = -1;
                for (int i = 0; i < c.getLinkedList().size(); i++) {
                    if (c.getLinkedList().get(i).equalsIgnoreCase(menorKey)) {
                        index = i;
                        break;
                    }
                }

                //Se por algum motivo o index ainda for -1, para a função
                if (index == -1) return;

                //Substitui o bloco antigo pelo novo
                blocoRemovido = c.getLinkedList().get(index);
                c.getLinkedList().set(index, rotulo);
                c.getTempoUsado().remove(menorKey);
                c.getQuantiaHits().remove(menorKey);
                c.getTempoUsado().put(rotulo, c.getCurrentTempo());
                c.addCurrentTempo();
                break;
            }

            case RANDOM: {
                //Escolhe um bloco aleatório dentro do conjunto
                int randomIndex = new Random().nextInt(c.getLinkedList().size());
                String key = c.getLinkedList().get(randomIndex);

                //Substitui o bloco antigo pelo novo
                blocoRemovido = key;
                c.getLinkedList().set(randomIndex, rotulo);
                c.getTempoUsado().remove(key);
                c.getQuantiaHits().remove(key);
                c.getTempoUsado().put(rotulo, c.getCurrentTempo());
                c.addCurrentTempo();
                break;
            }
        }

        //Se a política de escrita for write-back e o bloco foi alterado em algum momento, atualizar a memória principal
        if (blocoRemovido != null && config.getPoliticaEscrita() == 1 && enderecosDirtyBit.contains(blocoRemovido)) {
            quantiaEscrita++;
            enderecosDirtyBit.remove(blocoRemovido);
        }
    }

}
