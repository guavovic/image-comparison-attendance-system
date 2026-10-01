package com.registro.usuario;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.salvar.SalvarDiretorio;

public class CarregarDados {
	
	public String[] listaDados;

	public CarregarDados() {
		listaDados = new String[4];
	}

    public String[] setBuscar() {

    	SalvarDiretorio sd = new SalvarDiretorio();
    	String endereco = sd.setPegarCaminho();
    	
    	String parteSalvar = "salvar/";
    	String parteSubstituir = "\\salvar\\";
    	
    	String novoEndereco = endereco.replace(parteSalvar, parteSubstituir);
    	
        String diretorio = novoEndereco;
    	
        String caminhoArquivo = diretorio + "funcionario.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
           
        	listaDados[0] = br.readLine();
            listaDados[1] = br.readLine();
            listaDados[2] = br.readLine();
            listaDados[3] = br.readLine();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < listaDados.length; i++) {
            System.out.println("Linha " + (i+1) + ": " + listaDados[i]);
        }
        
        return listaDados;
    }
}
