package util;

import java.security.MessageDigest;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Crypto {

    public static String normalizar(String str) {
        if (str == null)
            return "";
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("").toLowerCase().trim();
    }

    public static String gerarHash(String str) {
        if (str == null)
            str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(str.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(str.hashCode());
        }
    }

    public static String hashSenha(String senha) {
        return gerarHash(senha);
    }

    public static String hashResposta(String resposta) {
        return gerarHash(normalizar(resposta));
    }
}
