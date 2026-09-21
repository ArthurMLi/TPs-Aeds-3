package repository;

import entities.Pergunta;
import files.ArquivoPergunta;
import files.ArquivoUsuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Camada de controle da entidade Pergunta.
 *
 * Como o CrudUsuario, nao faz entrada nem saida de dados: devolve null quando a
 * operacao deu certo e uma mensagem de erro quando alguma regra barrou.
 */
public class CrudPergunta {

    private final ArquivoPergunta arqPerguntas;

    public CrudPergunta() throws Exception {
        this.arqPerguntas = new ArquivoPergunta();
    }

    public CrudPergunta(ArquivoPergunta arqPerguntas) {
        this.arqPerguntas = arqPerguntas;
    }

    public ArquivoPergunta getArquivoPergunta() {
        return arqPerguntas;
    }

    /**
     * Liga o arquivo de usuarios para que a inclusao valide a integridade
     * referencial: so se cria pergunta para um idUsuario que exista de fato.
     */
    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        arqPerguntas.setArquivoUsuario(arqUsuarios);
    }

    // ------------------------------------------------------------------
    // Inclusao
    // ------------------------------------------------------------------

    /**
     * Inclui uma pergunta. O usuario informa apenas o texto e as palavras-chave;
     * o idUsuario vem de quem esta logado, as datas de criacao e alteracao vem
     * do relogio, a nota comeca em zero e a pergunta nasce ativa.
     */
    public String incluir(int idUsuario, String texto, String palavrasChave) {
        if (idUsuario <= 0) {
            return "Usuário inválido.";
        }
        if (texto == null || texto.trim().isEmpty()) {
            return "O texto da pergunta não pode ser vazio.";
        }
        if (palavrasChave == null || palavrasChave.trim().isEmpty()) {
            return "As palavras-chave não podem ser vazias.";
        }
        try {
            arqPerguntas.create(new Pergunta(idUsuario, texto.trim(), palavrasChave.trim()));
            return null;
        } catch (Exception e) {
            return "Não foi possível incluir a pergunta: " + e.getMessage();
        }
    }

    public String criar(int idUsuario, String texto, String palavrasChave) {
        return incluir(idUsuario, texto, palavrasChave);
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    public Pergunta ler(int id) {
        try {
            return arqPerguntas.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Devolve todas as perguntas do usuario, da mais antiga para a mais recente.
     * O caminho percorrido e: idUsuario -> arvore B+ -> lista de idPergunta ->
     * indice direto ID -> endereco -> registro no arquivo de dados.
     */
    public Pergunta[] listar(int idUsuario) {
        try {
            Pergunta[] perguntas = arqPerguntas.readAllByUsuario(idUsuario);
            Arrays.sort(perguntas, Comparator.comparingLong(Pergunta::getCriacao));
            return perguntas;
        } catch (Exception e) {
            return new Pergunta[0];
        }
    }

    public Pergunta[] listarPorUsuario(int idUsuario) {
        return listar(idUsuario);
    }

    /** Apenas as perguntas ainda ativas (as arquivadas somem das listagens publicas). */
    public Pergunta[] listarAtivas(int idUsuario) {
        Pergunta[] todas = listar(idUsuario);
        ArrayList<Pergunta> ativas = new ArrayList<>();
        for (Pergunta p : todas) {
            if (p.isAtiva()) {
                ativas.add(p);
            }
        }
        return ativas.toArray(new Pergunta[0]);
    }

    // ------------------------------------------------------------------
    // Alteracao
    // ------------------------------------------------------------------

    /**
     * Altera texto e palavras-chave. Campos deixados em branco sao mantidos.
     * ID, idUsuario e estado nao entram: IDs nunca mudam, cada usuario so
     * gerencia as proprias perguntas, e o estado so muda pelo arquivamento.
     */
    public String alterar(int id, int idUsuario, String novoTexto, String novasPalavras) {
        try {
            Pergunta pergunta = arqPerguntas.read(id);
            if (pergunta == null) {
                return "Pergunta não encontrada.";
            }
            if (pergunta.getIdUsuario() != idUsuario) {
                return "Esta pergunta pertence a outro usuário.";
            }
            if (!pergunta.isAtiva()) {
                return "Uma pergunta arquivada não pode ser alterada.";
            }
            if (novoTexto != null && !novoTexto.trim().isEmpty()) {
                pergunta.setPergunta(novoTexto.trim());
            }
            if (novasPalavras != null && !novasPalavras.trim().isEmpty()) {
                pergunta.setPalavrasChave(novasPalavras.trim());
            }
            return arqPerguntas.update(pergunta) ? null : "Não foi possível alterar a pergunta.";
        } catch (Exception e) {
            return "Não foi possível alterar a pergunta: " + e.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // Arquivamento e exclusao
    // ------------------------------------------------------------------

    /**
     * Arquiva a pergunta (ativa = false). Nao e exclusao: o registro continua
     * valido no arquivo e nos indices, porque outras entidades criadas por
     * outros usuarios (respostas, votos) dependem dela. O arquivamento e
     * definitivo: nao existe desarquivar.
     */
    public String arquivar(int id, int idUsuario) {
        try {
            Pergunta pergunta = arqPerguntas.read(id);
            if (pergunta == null) {
                return "Pergunta não encontrada.";
            }
            if (pergunta.getIdUsuario() != idUsuario) {
                return "Esta pergunta pertence a outro usuário.";
            }
            if (!pergunta.isAtiva()) {
                return "Esta pergunta já está arquivada.";
            }
            return arqPerguntas.arquivar(id) ? null : "Não foi possível arquivar a pergunta.";
        } catch (Exception e) {
            return "Não foi possível arquivar a pergunta: " + e.getMessage();
        }
    }

    /** Exclusao fisica (lapide). Usada apenas na cascata da exclusao de usuario. */
    public String excluir(int id) {
        try {
            if (arqPerguntas.read(id) == null) {
                return "Pergunta não encontrada.";
            }
            return arqPerguntas.delete(id) ? null : "Não foi possível excluir a pergunta.";
        } catch (Exception e) {
            return "Não foi possível excluir a pergunta: " + e.getMessage();
        }
    }

    public String excluirTodasDoUsuario(int idUsuario) {
        try {
            arqPerguntas.deleteAllByUsuario(idUsuario);
            return null;
        } catch (Exception e) {
            return "Não foi possível excluir as perguntas do usuário: " + e.getMessage();
        }
    }

    public void fechar() throws Exception {
        arqPerguntas.close();
    }
}
