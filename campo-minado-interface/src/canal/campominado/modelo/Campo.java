package canal.campominado.modelo;

import java.util.ArrayList;
import java.util.List;

public class Campo {

	private final int linha, coluna;
	public boolean minado = false, livre = false, marcado = false;

	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<>();

	Campo(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;

	}

	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}

	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
	}

	boolean addVizinho(Campo vizinho) {
		boolean linhaDif = linha != vizinho.linha;
		boolean colunaDif = coluna != vizinho.coluna;
		boolean diagonal = linhaDif && colunaDif;

		int deltaLinha = Math.abs(linha - vizinho.linha);
		int deltaColuna = Math.abs(coluna - vizinho.coluna);
		int deltaGeral = deltaColuna + deltaLinha;

		if (deltaGeral == 1 && !diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else if (deltaGeral == 2 && diagonal) {
			vizinhos.add(vizinho);
			return true;
		} else {
			return false;
		}
	}

	public void alterMarcacao() {
		if (!livre) {
			marcado = !marcado;

			if (marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			} else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}

	public boolean abrir() {
		if (!livre && !marcado) {
			if (minado) {

				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}
			setLivre(true);

			if (vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
		} else {

			return false;
		}
	}

	public boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}

	void minar() {
		minado = true;
	}

	public boolean isMinado() {
		return minado;
	}

	public boolean isMarcado() {
		return marcado;
	}

	public void setLivre(boolean livre) {
		this.livre = livre;
		if (livre) {
			notificarObservadores(CampoEvento.ABRIR);
		}
	}

	public boolean isLivre() {
		return livre;
	}

	public boolean isOcupado() {
		return !livre;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

	boolean objetivoAlcancado() {
		boolean desvendado = !minado && livre;
		boolean protegido = minado && marcado; 
		return desvendado || protegido;
	}

	public int minasVizinhas() {
		return Integer.parseInt(String.valueOf(vizinhos.stream().filter(v -> v.minado).count()));
	}

	void reiniciar() {
		livre = false;
		minado = false;
		marcado = false;
		notificarObservadores(CampoEvento.REINICIAR);
	}

}
