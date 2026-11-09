package util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class PalavrasChave {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            // artigos, preposicoes e contracoes
            "a", "ao", "aos", "as", "o", "os", "um", "uma", "uns", "umas",
            "ante", "apos", "ate", "com", "contra", "de", "desde", "em", "entre",
            "para", "perante", "por", "sem", "sob", "sobre", "tras",
            "da", "das", "do", "dos", "dum", "duma", "duns", "dumas",
            "na", "nas", "no", "nos", "num", "numa", "nuns", "numas",
            "pela", "pelas", "pelo", "pelos",
            // pronomes e conjuncoes
            "e", "essa", "esse", "esta", "este", "isso", "isto", "ha", "mas",
            "nao", "nem", "ou", "que", "se", "sua", "suas", "seu", "seus",
            // numerais por extenso
            "zero", "dois", "duas", "tres", "quatro", "cinco", "seis", "sete",
            "oito", "nove", "dez", "onze", "doze", "treze", "catorze", "quatorze",
            "quinze", "dezesseis", "dezessete", "dezoito", "dezenove", "vinte",
            "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta",
            "noventa", "cem", "cento", "mil",
            "primeiro", "primeira", "segundo", "segunda", "terceiro", "terceira",
            // proprias do dominio: toda pergunta do sistema tem esse formato
            "como", "qual", "quais", "quando", "onde", "quem", "porque",
            "pergunta", "perguntas", "resposta", "respostas", "duvida", "duvidas",
            "ajuda"));

    private PalavrasChave() {
    }

    public static List<String> tokens(String texto) {
        if (texto == null) {
            return Arrays.asList();
        }
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{Nd}]+", " ")
                .trim();
        if (normalizado.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.stream(normalizado.split("\\s+"))
                .filter(palavra -> !STOP_WORDS.contains(palavra))
                .filter(palavra -> !palavra.matches("\\p{Nd}+")) // numerais em digitos
                .collect(Collectors.toList());
    }
}
