package com.processamento;

import javax.imageio.ImageIO;
import javax.swing.JTextArea;

import com.salvar.SalvarDiretorio;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ComparadorImagem {

	private static final int DIMENSAO_PREDEFINIDA = 100; // Defina a dimensão desejada
	private static final double PESO_HISTOGRAMA = 0.4;
	private static final double PESO_PIXEL_A_PIXEL = 0.3;
	private static final double PESO_METRICA_DISTANCIA = 0.3;
	private static String novoEndereco1;
	private static String novoEndereco2;
	//private static boolean pontoObtido = false;

	public static void setComparar(JTextArea textArea) {

		ConverterImagem cv = new ConverterImagem();
		cv.setConverter(textArea);

		SalvarDiretorio sd = new SalvarDiretorio();
		String endereco1 = sd.setPegarCaminho();
		String endereco2 = sd.setPegarCaminho();

		String parteSalvar = "salvar/";
		String parteSubstituir1 = "processamento/database";
		String parteSubstituir2 = "processamento/validar";

		novoEndereco1 = endereco1.replace(parteSalvar, parteSubstituir1);
		novoEndereco2 = endereco2.replace(parteSalvar, parteSubstituir2);

		// Colocar_diretorio
		String diretorio1 = novoEndereco1;
		String diretorio2 = novoEndereco2;

		// System.out.println("\n teste: " + diretorio1);

		String imagemReferenciaPath = diretorio2 + "\\funcionario.png";
		String pastaImagensPath = diretorio1;

		try {
			BufferedImage imagemReferencia = redimensionarImagem(ImageIO.read(new File(imagemReferenciaPath)));
			int[] histogramaReferencia = CalculadorHistograma.calcularHistograma(imagemReferencia);

			File pastaImagens = new File(pastaImagensPath);
			File[] imagens = pastaImagens.listFiles();

			if (imagens != null) {
				for (File imagem : imagens) {
					if (imagem.isFile()) {
						BufferedImage imagemAtual = redimensionarImagem(ImageIO.read(imagem));
						int[] histogramaAtual = CalculadorHistograma.calcularHistograma(imagemAtual);

						double similaridadeHistograma = calcularSimilaridadeHistograma(histogramaReferencia,
								histogramaAtual);
						double similaridadePixelAPixel = calcularSimilaridadePixelAPixel(imagemReferencia, imagemAtual);
						double similaridadeMetricaDistancia = calcularSimilaridadeMetricaDistancia(histogramaReferencia,
								histogramaAtual);

						// Calcula a similaridade total usando pesos
						double similaridadeTotal = (PESO_HISTOGRAMA * similaridadeHistograma)
								+ (PESO_PIXEL_A_PIXEL * similaridadePixelAPixel)
								+ (PESO_METRICA_DISTANCIA * similaridadeMetricaDistancia);

						BigDecimal bd = new BigDecimal(similaridadeTotal);
						bd = bd.setScale(6, RoundingMode.HALF_UP);

						double resultadoSimilaridadeTotal = bd.doubleValue();

						System.out.println("\n " + imagem.getName() + ": Similaridade Total: "
								+ (resultadoSimilaridadeTotal * 100) + "%" + "\n");
						textArea.append("\n " + imagem.getName() + ": Similaridade Total: "
								+ (resultadoSimilaridadeTotal * 100) + "%" + "\n");

						if (similaridadeTotal >= 0.85) {
							System.out.println("\n OK - Imagem parecida: " + imagem.getName() + "\n");
							textArea.append("\n  ( OK - Imagem parecida: " + imagem.getName() + " )\n");

							textArea.append("\n =================================");
							textArea.append("\n  Validação de ponto finalizada!");
							textArea.append("\n =================================" + "\n");
							
							Registrar registrar = new Registrar();
							registrar.setSalvarPonto();
						}
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static double calcularSimilaridadeHistograma(int[] histogramaReferencia, int[] histogramaAtual) {
		double somaDiferencas = 0;

		for (int i = 0; i < histogramaReferencia.length; i++) {
			somaDiferencas += Math.abs(histogramaReferencia[i] - histogramaAtual[i]);
		}

		double mediaDiferencas = somaDiferencas / histogramaReferencia.length;
		return 1.0 - (mediaDiferencas / 255); // Normaliza para o intervalo [0, 1]
	}

	private static double calcularSimilaridadePixelAPixel(BufferedImage imagemReferencia, BufferedImage outraImagem) {
		int largura = imagemReferencia.getWidth();
		int altura = imagemReferencia.getHeight();
		int totalPixels = largura * altura;
		int diferencaTotal = 0;

		for (int y = 0; y < altura; y++) {
			for (int x = 0; x < largura; x++) {
				int corReferencia = imagemReferencia.getRGB(x, y);
				int corAtual = outraImagem.getRGB(x, y);

				// Extrai os componentes de cor (vermelho, verde, azul) de cada pixel
				int redReferencia = (corReferencia >> 16) & 0xFF;
				int greenReferencia = (corReferencia >> 8) & 0xFF;
				int blueReferencia = corReferencia & 0xFF;

				int redAtual = (corAtual >> 16) & 0xFF;
				int greenAtual = (corAtual >> 8) & 0xFF;
				int blueAtual = corAtual & 0xFF;

				// Calcula a diferença média absoluta entre os componentes de cor
				int diferencaRed = Math.abs(redReferencia - redAtual);
				int diferencaGreen = Math.abs(greenReferencia - greenAtual);
				int diferencaBlue = Math.abs(blueReferencia - blueAtual);

				// Soma as diferenças para o pixel atual
				diferencaTotal += (diferencaRed + diferencaGreen + diferencaBlue) / 3;
			}
		}

		// Calcula a média das diferenças para todos os pixels
		double diferencaMedia = (double) diferencaTotal / totalPixels;

		// Normaliza a diferença média para o intervalo [0, 1]
		return 1.0 - (diferencaMedia / 255.0);
	}

	private static double calcularSimilaridadeMetricaDistancia(int[] histogramaReferencia, int[] histogramaAtual) {

		// Retorna um valor entre 0 e 1 indicando a similaridade
		double distanciaEuclidiana = CalculadorMetricaDistancia.calcularDistanciaEuclidiana(histogramaReferencia,
				histogramaAtual);
		double distanciaManhattan = CalculadorMetricaDistancia.calcularDistanciaManhattan(histogramaReferencia,
				histogramaAtual);

		// Normaliza as distâncias para o intervalo [0, 1]
		double maxDistanciaEuclidiana = Math.sqrt(255 * 255 * histogramaReferencia.length);
		double maxDistanciaManhattan = 255 * histogramaReferencia.length;

		double similaridadeEuclidiana = 1.0 - (distanciaEuclidiana / maxDistanciaEuclidiana);
		double similaridadeManhattan = 1.0 - (distanciaManhattan / maxDistanciaManhattan);

		return (similaridadeEuclidiana + similaridadeManhattan) / 2.0;
	}

	private static BufferedImage redimensionarImagem(BufferedImage imagem) {
		int novaLargura = DIMENSAO_PREDEFINIDA;
		int novaAltura = DIMENSAO_PREDEFINIDA;

		BufferedImage redimensionada = new BufferedImage(novaLargura, novaAltura, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = redimensionada.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(imagem, 0, 0, novaLargura, novaAltura, null);
		g.dispose();

		return redimensionada;
	}
}
