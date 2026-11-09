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
            "a", "ao", "aos", "as", "com", "da", "das", "de", "do", "dos",
            "e", "em", "entre", "essa", "esse", "esta", "este", "ha", "isso",
            "isto", "na", "nas", "no", "nos", "o", "os", "para", "por", "que",
            "se", "sem", "sua", "suas", "seu", "seus", "um", "uma", "uns", "umas"));

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
                .collect(Collectors.toList());
    }
}
