package files;

import aed3.Arquivo;
import aed3.ArvoreBMais;
import aed3.ElementoLista;
import aed3.ListaInvertida;
import aed3.ParDataId;
import aed3.ParIdId;
import entities.Pergunta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.PalavrasChave;

public class ArquivoPergunta extends Arquivo<Pergunta> {

    ArvoreBMais<ParIdId> relUsuarioPergunta;
    ArvoreBMais<ParDataId> relDataPergunta;
    private final ListaInvertida indicePalavras;

    /**
     * Referencia opcional ao arquivo de usuarios, usada apenas para garantir a
     * integridade referencial na inclusao (nao se cria pergunta apontando para
     * um idUsuario que nao existe).
     */
    private ArquivoUsuario arqUsuarios;

    public ArquivoPergunta() throws Exception {
        super("perguntas", Pergunta.class.getConstructor());
        relUsuarioPergunta = new ArvoreBMais<>(
                ParIdId.class.getConstructor(),
                5,
                "./dados/perguntas/relUsuarioPergunta.db");
        relDataPergunta = new ArvoreBMais<>(
                ParDataId.class.getConstructor(),
                5,
                "./dados/perguntas/relDataPergunta.db");
        indicePalavras = new ListaInvertida(
                5,
                "./dados/perguntas/dicionarioPalavras.listainv.db",
                "./dados/perguntas/blocosPalavras.listainv.db");
    }

    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        this.arqUsuarios = arqUsuarios;
    }

    /**
     * Inclui a pergunta no arquivo de dados e registra o par
     * (idUsuario, idPergunta) na arvore B+, que e o que permite responder
     * "quais sao as perguntas deste usuario" sem varrer o arquivo inteiro.
     */
    @Override
    public int create(Pergunta pergunta) throws Exception {
        if (pergunta == null) {
            throw new IllegalArgumentException("Pergunta nao pode ser nula.");
        }

        if (arqUsuarios != null && arqUsuarios.read(pergunta.getIdUsuario()) == null) {
            throw new Exception("Usuario " + pergunta.getIdUsuario() + " nao existe.");
        }
        if (PalavrasChave.tokens(pergunta.getPalavrasChave()).isEmpty()) {
            throw new Exception("A pergunta precisa ter ao menos uma palavra-chave valida.");
        }
        int id = super.create(pergunta);
        relUsuarioPergunta.create(new ParIdId(pergunta.getIdUsuario(), id));
        relDataPergunta.create(new ParDataId(pergunta.getCriacao(), id));
        indexar(pergunta);
        return id;
    }

    private void indexar(Pergunta pergunta) throws Exception {
        List<String> tokens = PalavrasChave.tokens(pergunta.getPalavrasChave());
        if (tokens.isEmpty()) {
            throw new Exception("A pergunta precisa ter ao menos uma palavra-chave valida.");
        }
        // Cada ocorrencia vale 1/total. A ListaInvertida nao aceita o mesmo ID
        // duas vezes no termo, entao a repeticao soma na frequencia ja gravada.
        float frequencia = 1f / tokens.size();
        for (String termo : tokens) {
            ElementoLista atual = indicePalavras.read(termo, pergunta.getId());
            if (atual == null) {
                indicePalavras.create(termo, new ElementoLista(pergunta.getId(), frequencia));
            } else {
                indicePalavras.update(termo,
                        new ElementoLista(pergunta.getId(), atual.getFrequencia() + frequencia));
            }
        }
        indicePalavras.incrementaEntidades();
    }

    private void removerDoIndice(Pergunta pergunta) throws Exception {
        for (String termo : PalavrasChave.tokens(pergunta.getPalavrasChave())) {
            indicePalavras.delete(termo, pergunta.getId());
        }
        indicePalavras.decrementaEntidades();
    }

    public Pergunta[] readByKeywords(String consulta) throws Exception {
        List<String> termos = PalavrasChave.tokens(consulta);
        Map<Integer, Double> pontuacoes = new HashMap<>();
        int entidades = indicePalavras.numeroEntidades();
        if (entidades <= 0) {
            return new Pergunta[0];
        }
        for (String termo : termos) {
            ElementoLista[] elementos = indicePalavras.read(termo);
            if (elementos.length == 0) {
                continue;
            }
            double idf = Math.log10((double) entidades / elementos.length);
            for (ElementoLista elemento : elementos) {
                pontuacoes.put(elemento.getId(),
                        pontuacoes.containsKey(elemento.getId())
                                ? pontuacoes.get(elemento.getId()) + elemento.getFrequencia() * idf
                                : elemento.getFrequencia() * idf);
            }
        }
        ArrayList<PerguntaComPontuacao> resultados = new ArrayList<>();
        for (Map.Entry<Integer, Double> entrada : pontuacoes.entrySet()) {
            Pergunta pergunta = read(entrada.getKey());
            if (pergunta != null && pergunta.isAtiva()) {
                resultados.add(new PerguntaComPontuacao(pergunta, entrada.getValue()));
            }
        }
        resultados.sort((a, b) -> {
            int comparacao = Double.compare(b.pontuacao, a.pontuacao);
            return comparacao != 0 ? comparacao
                    : Integer.compare(a.pergunta.getId(), b.pergunta.getId());
        });
        Pergunta[] perguntas = new Pergunta[resultados.size()];
        for (int i = 0; i < resultados.size(); i++) {
            perguntas[i] = resultados.get(i).pergunta;
        }
        return perguntas;
    }

    private static class PerguntaComPontuacao {
        private final Pergunta pergunta;
        private final double pontuacao;

        private PerguntaComPontuacao(Pergunta pergunta, double pontuacao) {
            this.pergunta = pergunta;
            this.pontuacao = pontuacao;
        }
    }

    public Pergunta[] readAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioPergunta.read(new ParIdId(idUsuario, -1));
        if (lista == null) {
            return new Pergunta[0];
        }

        ArrayList<Pergunta> aux = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            ParIdId par = lista.get(i);
            Pergunta pergunta = super.read(par.getId2());
            if (pergunta != null) {
                aux.add(pergunta);
            }
        }

        Pergunta[] resp = new Pergunta[aux.size()];
        for (int i = 0; i < aux.size(); i++) {
            resp[i] = aux.get(i);
        }

        return resp;
    }

    public Pergunta[] readAllByData() throws Exception {
        ArrayList<ParDataId> lista = relDataPergunta.read(null);
        ArrayList<Pergunta> perguntas = new ArrayList<>();

        if (lista != null) {
            for (int i = lista.size() - 1; i >= 0; i--) {
                ParDataId indice = lista.get(i);
                Pergunta pergunta = super.read(indice.getId());
                if (pergunta != null && pergunta.isAtiva()) {
                    perguntas.add(pergunta);
                }
            }
        }

        return perguntas.toArray(new Pergunta[0]);
    }

    /**
     * Altera a pergunta e carimba a data/hora de alteracao.
     * O idUsuario e imutavel: se ele pudesse mudar, o par gravado na arvore B+
     * ficaria apontando para o usuario errado e a listagem quebraria.
     */
    @Override
    public boolean update(Pergunta pergunta) throws Exception {
        Pergunta atual = super.read(pergunta.getId());
        if (atual == null) {
            return false;
        }
        if (atual.getIdUsuario() != pergunta.getIdUsuario()) {
            throw new Exception("O idUsuario de uma pergunta nao pode ser alterado.");
        }
        // Valida antes de gravar: depois do super.update nao ha como desfazer.
        if (pergunta.isAtiva() && PalavrasChave.tokens(pergunta.getPalavrasChave()).isEmpty()) {
            throw new Exception("A pergunta precisa ter ao menos uma palavra-chave valida.");
        }
        Pergunta copiaAnterior = new Pergunta(atual.getId(), atual.getIdUsuario(),
                atual.getCriacao(), atual.getAlteracao(), atual.getNota(),
                atual.getPergunta(), atual.getPalavrasChave(), atual.isAtiva());
        pergunta.setCriacao(atual.getCriacao());
        pergunta.setAlteracao(System.currentTimeMillis());
        boolean atualizado = super.update(pergunta);
        if (atualizado) {
            if (atual.isAtiva() && pergunta.isAtiva()) {
                removerDoIndice(copiaAnterior);
                indexar(pergunta);
            } else if (atual.isAtiva()) {
                removerDoIndice(copiaAnterior);
            } else if (pergunta.isAtiva()) {
                indexar(pergunta);
            }
        }
        return atualizado;
    }

    public boolean arquivar(int id) throws Exception {
        Pergunta pergunta = super.read(id);
        if (pergunta != null) {
            pergunta.setAtiva(false);
            return this.update(pergunta);
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Pergunta pergunta = super.read(id);
        if (pergunta != null) {
            if (pergunta.isAtiva()) {
                removerDoIndice(pergunta);
            }
            relUsuarioPergunta.delete(new ParIdId(pergunta.getIdUsuario(), id));
            relDataPergunta.delete(new ParDataId(pergunta.getCriacao(), id));

            return super.delete(id);
        }
        return false;
    }

    public void deleteAllByUsuario(int idUsuario) throws Exception {
        ArrayList<ParIdId> lista = relUsuarioPergunta.read(new ParIdId(idUsuario, -1));

        if (lista != null) {
            for (ParIdId parUsuario : lista) {
                int idPergunta = parUsuario.getId2();

                // Necessário para descobrir a data usada no índice
                Pergunta pergunta = super.read(idPergunta);

                if (pergunta != null) {
                    if (pergunta.isAtiva()) {
                        removerDoIndice(pergunta);
                    }
                    relDataPergunta.delete(
                            new ParDataId(pergunta.getCriacao(), idPergunta));

                    super.delete(idPergunta);
                }

                relUsuarioPergunta.delete(parUsuario);
            }
        }
    }
}
