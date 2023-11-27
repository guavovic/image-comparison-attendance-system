package com.salvar;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

import java.net.URISyntaxException;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SalvarDiretorio {

    public String path;
    public String pacote;
    private String ip;
    private String ipAtual;
    
    public SalvarDiretorio() {
    	
        caminhoAtual();
    }

    public void caminhoAtual() {
        try {
            // Obtém a URL do recurso da classe Inicializador
            URL url = SalvarDiretorio.class.getProtectionDomain().getCodeSource().getLocation();

            // Converte a URL para URI para lidar com espaços e caracteres especiais
            path = url.toURI().getPath();

            // Obtém o caminho do pacote da classe
            pacote = SalvarDiretorio.class.getPackage().getName().replace(".", "/");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        verificarMaquina();
    }

    public void verificarMaquina() {
        try {
            InetAddress enderecoIp = InetAddress.getLocalHost();

            System.out.println("\n Endereço IP do computador: " + enderecoIp.getHostAddress());
            //textArea.append("\n Endereço IP do computador: " + enderecoIp.getHostAddress());
            
            ip = enderecoIp.getHostAddress();

            String caminhoArquivoIP = path + pacote + "/ipPC.txt";
            //String caminhoArquivoDiretorio = path + pacote + "/diretorio.txt";
            
            File arquivoIP = new File(caminhoArquivoIP);
            //File arquivoDiretorio = new File(caminhoArquivoIP);
            
            setPegarIP();

            if (arquivoIP.exists() && ipAtual.equals(ip)) {
                System.out.println("\n IPs são iguais. Não é necessário salvar o caminho.");
                //textArea.append("\n IPs são iguais. Não é necessário salvar o caminho.");
                
            } else {
                if (arquivoIP.exists()) {
                    System.out.println("\n IPs diferentes. Deletando arquivo ipPC.txt.");
                    //textArea.append("\n IPs diferentes. Deletando arquivo ipPC.txt.");
                    
                    if (arquivoIP.delete()) {
                        System.out.println("Arquivo ipPC.txt deletado com sucesso.");
                        //textArea.append("Arquivo ipPC.txt deletado com sucesso.");
                        
                        /*
                        if (arquivoDiretorio.exists()) {
                        	System.out.println("Arquivo diretorio.txt deletado com sucesso.");
                        } */
                    } else {
                        System.out.println("Não foi possível deletar o arquivo ipPC.txt.");
                        //textArea.append("Não foi possível deletar o arquivo ipPC.txt.");
                    }
                }

                System.out.println("\n Salvando IP e Caminho.");
                //textArea.append("\n Salvando IP e Caminho.");
                
                salvarIp();
                setPegarIP();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void salvarIp() {
        if (ip != null) {
            String caminhoArquivo = path + pacote + "/ipPC.txt";

            try {
                File arquivo = new File(caminhoArquivo);

                if (!arquivo.exists()) {
                    arquivo.createNewFile();
                }

                FileWriter fw = new FileWriter(arquivo.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(ip);
                bw.newLine();

                bw.close();

                System.out.println("\n Arquivo ipPC criado com sucesso.");
                //textArea.append("\n Arquivo ipPC criado com sucesso.");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("\n Endereço IP é nulo. Não foi possível salvar.");
            //textArea.append("\n Endereço IP é nulo. Não foi possível salvar.");
        }
    }

    public void setPegarIP() {
        try (BufferedReader br = new BufferedReader(new FileReader(path + pacote + "/ipPC.txt"))) {
            String linha;

            // Lê cada linha do arquivo até o final
            while ((linha = br.readLine()) != null) {
                ipAtual = linha;
                System.out.println("\n IP lido do arquivo: " + ipAtual);
                //textArea.append("\n IP lido do arquivo: " + ipAtual);
            }

            if (ipAtual.equals(ip)) {
                salvarCaminho();
                System.out.println("\n Ip igual");
                //textArea.append("\n Ip igual");
            } else {
                System.out.println("\n Ip diferente");
                //textArea.append("\n Ip diferente");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void salvarCaminho() {
        String caminhoArquivo = path + pacote + "/diretorio.txt";

        String conteudo = path + pacote + "/";

        // Tentar criar o arquivo e escrever nele
        try {
            File arquivo = new File(caminhoArquivo);

            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }

            FileWriter fw = new FileWriter(arquivo.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(conteudo);

            bw.close();

            System.out.println("\n Arquivo diretorio criado com sucesso.");
            //textArea.append("\n Arquivo diretorio criado com sucesso.");
            //setPegarCaminho();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String setPegarCaminho() {

        String endereco = null;

        try (BufferedReader br = new BufferedReader(new FileReader(path + pacote + "/diretorio.txt"))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                System.out.println("\n " + linha);
                //textArea.append("\n " + linha);
                
                endereco = linha;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return endereco;
    }

}
