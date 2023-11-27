package com.processamento;

import java.io.File;

import javax.swing.JTextArea;

import com.salvar.SalvarDiretorio;

public class ConverterImagem {

	private static String novoEndereco;
	
    public static void setConverter(JTextArea textArea) {

    	SalvarDiretorio sd = new SalvarDiretorio();
    	String endereco = sd.setPegarCaminho();
    	
    	String parteSalvar = "salvar/";
    	String parteSubstituir = "\\processamento\\validar";
    	
    	novoEndereco = endereco.replace(parteSalvar, parteSubstituir);
    	
        String diretorio = novoEndereco;
    	
        String pastaImagensPath = diretorio;

        File pastaImagens = new File(pastaImagensPath);
        File[] imagens = pastaImagens.listFiles();

        if (imagens != null) {
            for (File imagem : imagens) {
                if (imagem.isFile()) {
                	
                    String novoNome = converterNomeImagem(imagem.getName());
                    
                    System.out.println("\n\n Nome original: " + imagem.getName() + ", Novo nome: " + novoNome + "\n");
                    textArea.append("\n\n Nome original: " + imagem.getName() + ", Novo nome: " + novoNome + "\n");
                    textArea.append("\n ------------------------------------------------------------------------------------" + "\n");
                    
                    String novoCaminho = pastaImagensPath + File.separator + novoNome;

                    if (!imagem.renameTo(new File(novoCaminho))) {
                        System.out.println("\n Falha ao renomear: " + imagem.getName());
                        textArea.append("\n Falha ao renomear: " + imagem.getName());
                    }
                }
            }
        } else {
            System.out.println("\n A pasta de imagens está vazia ou não existe. \n");
            textArea.append("\n A pasta de imagens está vazia ou não existe. \n");
        }
    }

    public static String converterNomeImagem(String nomeImagem) {
      
        String nomeUsuario = extrairNomeUsuario(nomeImagem);

        String novoNome = nomeUsuario + ".png";

        return novoNome;
    }

    private static String extrairNomeUsuario(String nomeImagem) {
        int pontoIndex = nomeImagem.lastIndexOf('.');
        
        if (pontoIndex != -1) {
            nomeImagem = nomeImagem.substring(0, pontoIndex);
        }

        return "funcionario";
    }
}