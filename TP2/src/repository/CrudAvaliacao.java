package repository;

import entities.Avaliacao;
import entities.Resposta;
import files.ArquivoAvaliacao;
import files.ArquivoResposta;
import files.ArquivoUsuario;
import java.util.ArrayList;

/**
 * Camada de controle da entidade Avaliacao.
 *
 * Gerencia o registro e cancelamento de avaliacoes (votos), garantindo que:
 * um usuario nao avalie a propria resposta;
 * um usuario nao avalie a mesma resposta mais de uma vez;
 *  a nota e a contagem de avaliacoes da resposta sejam ajustadas automaticamente.
 */
public class CrudAvaliacao {

    private final ArquivoAvaliacao arqAvaliacoes;
    private ArquivoResposta arqRespostas;
    private ArquivoUsuario arqUsuarios;

    public CrudAvaliacao() throws Exception {
        this.arqAvaliacoes = new ArquivoAvaliacao();
    }

    public CrudAvaliacao(ArquivoAvaliacao arqAvaliacoes) {
        this.arqAvaliacoes = arqAvaliacoes;
    }

    public ArquivoAvaliacao getArquivoAvaliacao() {
        return arqAvaliacoes;
    }

    public void setArquivoResposta(ArquivoResposta arqRespostas) {
        this.arqRespostas = arqRespostas;
        this.arqAvaliacoes.setArquivoResposta(arqRespostas);
    }

    public void setArquivoUsuario(ArquivoUsuario arqUsuarios) {
        this.arqUsuarios = arqUsuarios;
        this.arqAvaliacoes.setArquivoUsuario(arqUsuarios);
    }

    /**
     * Registra uma avaliacao feita por um usuario para uma resposta.
     * Atualiza automaticamente a nota (+1 se positiva, -1 se negativa) e o contador
     * de avaliacoes da resposta correspondente.
     */
    public String avaliar(int idUsuario, int idResposta, boolean positiva) {
        if (idUsuario <= 0) {
            return "Usuario invalido.";
        }
        if (idResposta <= 0) {
            return "Resposta invalida.";
        }
        try {
            if (arqUsuarios != null && arqUsuarios.read(idUsuario) == null) {
                return "Usuario nao encontrado.";
            }
            if (arqRespostas == null) {
                return "Arquivo de respostas nao vinculado.";
            }
            Resposta resposta = arqRespostas.read(idResposta);
            if (resposta == null || !resposta.isAtiva()) {
                return "Resposta nao encontrada ou inativa.";
            }
            if (resposta.getIdUsuario() == idUsuario) {
                return "Voce nao pode avaliar a sua propria resposta.";
            }

            Avaliacao existente = arqAvaliacoes.readByUsuarioEResposta(idUsuario, idResposta);
            if (existente != null) {
                return "Voce ja avaliou esta resposta.";
            }

            // Grava a avaliacao
            Avaliacao nova = new Avaliacao(idUsuario, idResposta, positiva);
            arqAvaliacoes.create(nova);

            // Ajusta a nota e o numero de avaliacoes da resposta
            short notaAtual = resposta.getNota();
            resposta.setNota((short) (notaAtual + (positiva ? 1 : -1)));
            resposta.setAvaliacoes((short) (resposta.getAvaliacoes() + 1));
            arqRespostas.update(resposta);

            return null;
        } catch (Exception e) {
            return "Nao foi possivel registrar a avaliacao: " + e.getMessage();
        }
    }

    /**
     * Cancela uma avaliacao existente realizada pelo usuario, revertendo o impacto
     * na nota e decrementando o numero de avaliacoes da resposta correspondente.
     */
    public String cancelar(int idAvaliacao, int idUsuario) { //############################
        try {
            Avaliacao avaliacao = arqAvaliacoes.read(idAvaliacao);
            if (avaliacao == null) {
                return "Avaliacao nao encontrada.";
            }
            if (avaliacao.getIdUsuario() != idUsuario) {
                return "Esta avaliacao pertence a outro usuario.";
            }

            if (arqRespostas != null) {
                Resposta resposta = arqRespostas.read(avaliacao.getIdResposta());
                if (resposta != null) {
                    short ajuste = (short) (avaliacao.isAvaliacao() ? 1 : -1);
                    resposta.setNota((short) (resposta.getNota() - ajuste));
                    if (resposta.getAvaliacoes() > 0) {
                        resposta.setAvaliacoes((short) (resposta.getAvaliacoes() - 1));
                    }
                    arqRespostas.update(resposta);
                }
            }

            return arqAvaliacoes.delete(idAvaliacao) ? null : "Nao foi possivel cancelar a avaliacao.";
        } catch (Exception e) {
            return "Nao foi possivel cancelar a avaliacao: " + e.getMessage();
        }
    }

    public Avaliacao ler(int id) {
        try {
            return arqAvaliacoes.read(id);
        } catch (Exception e) {
            return null;
        }
    }

    public Avaliacao buscarPorUsuarioEResposta(int idUsuario, int idResposta) {
        try {
            return arqAvaliacoes.readByUsuarioEResposta(idUsuario, idResposta);
        } catch (Exception e) {
            return null;
        }
    }

    public ArrayList<Avaliacao> listarPorUsuario(int idUsuario) {
        try {
            return arqAvaliacoes.readAllByUsuario(idUsuario);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ArrayList<Avaliacao> listarPorResposta(int idResposta) {
        try {
            return arqAvaliacoes.readAllByResposta(idResposta);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String excluirTodasDaResposta(int idResposta) {
        try {
            arqAvaliacoes.deleteAllByResposta(idResposta);
            return null;
        } catch (Exception e) {
            return "Nao foi possivel excluir as avaliacoes da resposta: " + e.getMessage();
        }
    }

    public String excluirTodasDoUsuario(int idUsuario) {
        try {
            arqAvaliacoes.deleteAllByUsuario(idUsuario);
            return null;
        } catch (Exception e) {
            return "Nao foi possivel excluir as avaliacoes do usuario: " + e.getMessage();
        }
    }

    public void fechar() throws Exception {
        arqAvaliacoes.close();
    }
}
