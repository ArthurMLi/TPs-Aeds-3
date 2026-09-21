package repository;

import entities.Pergunta;
import files.ArquivoPergunta;

public class CrudPergunta {

    private ArquivoPergunta arqPerguntas;

    public CrudPergunta() throws Exception {
        arqPerguntas = new ArquivoPergunta();
    }

    public CrudPergunta(ArquivoPergunta arqPerguntas) {
        this.arqPerguntas = arqPerguntas;
    }

    public ArquivoPergunta getArquivoPergunta() {
        return arqPerguntas;
    }

    public String criar(Pergunta pergunta) {
        if (pergunta == null) {
            return "Pergunta nao pode ser nula.";
        }
        if (pergunta.getIdUsuario() <= 0) {
            return "ID do usuario invalido.";
        }
        if (pergunta.getPergunta() == null || pergunta.getPergunta().trim().isEmpty()) {
            return "O texto da pergunta nao pode ser vazio.";
        }
        if (pergunta.getPalavrasChave() == null || pergunta.getPalavrasChave().trim().isEmpty()) {
            return "As palavras-chave nao podem ser vazias.";
        }
        try {
            arqPerguntas.create(pergunta);
            return null;
        } catch (Exception e) {
            return "Erro ao criar pergunta.";
        }
    }

    public String criar(int idUsuario, String texto, String palavrasChave) {
        if (idUsuario <= 0) {
            return "ID do usuario invalido.";
        }
        if (texto == null || texto.trim().isEmpty()) {
            return "O texto da pergunta nao pode ser vazio.";
        }
        if (palavrasChave == null || palavrasChave.trim().isEmpty()) {
            return "As palavras-chave nao podem ser vazias.";
        }
        try {
            Pergunta pergunta = new Pergunta(idUsuario, texto.trim(), palavrasChave.trim());
            arqPerguntas.create(pergunta);
            return null;
        } catch (Exception e) {
            return "Erro ao criar pergunta.";
        }
    }

    public String incluir(Pergunta pergunta) {
        return criar(pergunta);
    }

    public String incluir(int idUsuario, String texto, String palavrasChave) {
        return criar(idUsuario, texto, palavrasChave);
    }

    public Pergunta ler(int id) {
        try {
            return arqPerguntas.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    public Pergunta buscar(int id) {
        return ler(id);
    }

    public Pergunta[] listar(int idUsuario) {
        try {
            return arqPerguntas.readAllByUsuario(idUsuario);
        } catch (Exception e) {
            return new Pergunta[0];
        }
    }

    public Pergunta[] listarPorUsuario(int idUsuario) {
        return listar(idUsuario);
    }

    public String alterar(Pergunta pergunta) {
        if (pergunta == null) {
            return "Pergunta nao pode ser nula.";
        }
        if (pergunta.getPergunta() == null || pergunta.getPergunta().trim().isEmpty()) {
            return "O texto da pergunta nao pode ser vazio.";
        }
        if (pergunta.getPalavrasChave() == null || pergunta.getPalavrasChave().trim().isEmpty()) {
            return "As palavras-chave nao podem ser vazias.";
        }
        try {
            pergunta.setAlteracao(System.currentTimeMillis());
            boolean ok = arqPerguntas.update(pergunta);
            if (!ok) {
                return "Pergunta nao encontrada para atualizacao.";
            }
            return null;
        } catch (Exception e) {
            return "Erro ao alterar pergunta.";
        }
    }

    public String alterar(int id, String novoTexto, String novasPalavras) {
        try {
            Pergunta pergunta = arqPerguntas.read(id);
            if (pergunta == null) {
                return "Pergunta nao encontrada.";
            }
            if (novoTexto != null && !novoTexto.trim().isEmpty()) {
                pergunta.setPergunta(novoTexto.trim());
            }
            if (novasPalavras != null && !novasPalavras.trim().isEmpty()) {
                pergunta.setPalavrasChave(novasPalavras.trim());
            }
            pergunta.setAlteracao(System.currentTimeMillis());
            boolean ok = arqPerguntas.update(pergunta);
            if (!ok) {
                return "Erro ao atualizar pergunta.";
            }
            return null;
        } catch (Exception e) {
            return "Erro ao alterar pergunta.";
        }
    }

    public String atualizar(Pergunta pergunta) {
        return alterar(pergunta);
    }

    public String atualizar(int id, String novoTexto, String novasPalavras) {
        return alterar(id, novoTexto, novasPalavras);
    }

    public String arquivar(int id) {
        try {
            Pergunta pergunta = arqPerguntas.read(id);
            if (pergunta == null) {
                return "Pergunta nao encontrada.";
            }
            if (!pergunta.isAtiva()) {
                return "Pergunta ja esta arquivada.";
            }
            boolean ok = arqPerguntas.arquivar(id);
            if (!ok) {
                return "Nao foi possivel arquivar a pergunta.";
            }
            return null;
        } catch (Exception e) {
            return "Erro ao arquivar pergunta.";
        }
    }

    public String excluir(int id) {
        try {
            Pergunta pergunta = arqPerguntas.read(id);
            if (pergunta == null) {
                return "Pergunta nao encontrada.";
            }
            boolean ok = arqPerguntas.delete(id);
            if (!ok) {
                return "Nao foi possivel excluir a pergunta.";
            }
            return null;
        } catch (Exception e) {
            return "Erro ao excluir pergunta.";
        }
    }

    public String deletar(int id) {
        return excluir(id);
    }

    public String excluirTodasDoUsuario(int idUsuario) {
        try {
            arqPerguntas.deleteAllByUsuario(idUsuario);
            return null;
        } catch (Exception e) {
            return "Erro ao excluir perguntas do usuario.";
        }
    }

    public void fechar() throws Exception {
        arqPerguntas.close();
    }
}
