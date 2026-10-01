package com.processamento;

import com.salvar.SalvarDiretorio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registrar {

	private static String novoEndereco;

	public void setSalvarPonto() {
		SalvarDiretorio sd = new SalvarDiretorio();
		String endereco = sd.setPegarCaminho();

		String parteSalvar = "salvar/";
		String parteSubstituir = "processamento\\registrosSalvos\\Ana Souza\\";

		novoEndereco = endereco.replace(parteSalvar, parteSubstituir);

		String diretorio = novoEndereco;

		Date dataAtual = new Date();
		SimpleDateFormat formatadorData = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dataFormatada = formatadorData.format(dataAtual);

		String nomeArquivo = "arquivo_" + dataFormatada + ".txt";
		String caminhoArquivo = diretorio + nomeArquivo;

		String conteudo = "Acrescentar";

		try {
			File arquivo = new File(caminhoArquivo);

			FileWriter escritor = new FileWriter(arquivo);

			BufferedWriter bufferEscrita = new BufferedWriter(escritor);

			bufferEscrita.write(conteudo);

			bufferEscrita.close();
			escritor.close();

			System.out.println("String gravada com sucesso no arquivo: " + nomeArquivo);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
