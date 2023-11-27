package com.visuais;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.salvar.SalvarDiretorio;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TelaRegistroGerenciamento extends JFrame {

    private JList<ArquivoItem> lista;

    private JTextField campoPesquisa;
    private JButton botaoInverter;
    private String nomeArquivoSelecionado = "";
    private String novoEndereco;
    
    private final List<ArquivoItem> nomesArquivosSemExtensao;

    public TelaRegistroGerenciamento() {
    	
    	SalvarDiretorio sd = new SalvarDiretorio();
    	String endereco = sd.setPegarCaminho();
    	
    	String parteSalvar = "salvar/";
    	String parteSubstituir = "\\processamento\\registrosSalvos";
    	
    	novoEndereco = endereco.replace(parteSalvar, parteSubstituir);
    	
    	// Colocar_diretorio
        String diretorio = novoEndereco;

        String[] nomesArquivos = new File(diretorio).list();

        nomesArquivosSemExtensao = new ArrayList<>();

        for (int i = 0; i < nomesArquivos.length; i++) {
            nomesArquivosSemExtensao.add(new ArquivoItem(i + 1, ".  " + nomesArquivos[i].replace(".txt", "")));
        }

        lista = new JList<>(nomesArquivosSemExtensao.toArray(new ArquivoItem[0]));
        lista.setCellRenderer(new ArquivoListCellRenderer());

        lista.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    nomeArquivoSelecionado = lista.getSelectedValue().getNomeArquivo() + ".txt";
                    // Message.getFileName(textArea, nomeArquivoSelecionado);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(lista);

        campoPesquisa = new JTextField(20);
        campoPesquisa.addActionListener(e -> search());
        campoPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search();
            }
        });

        botaoInverter = new JButton("Inverter Ordem");
        botaoInverter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collections.reverse(nomesArquivosSemExtensao);
                lista.setListData(nomesArquivosSemExtensao.toArray(new ArquivoItem[0]));
            }
        });

        JPanel painelPesquisa = new JPanel();
        painelPesquisa.add(campoPesquisa);
        painelPesquisa.add(botaoInverter);

        getContentPane().add(painelPesquisa, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void search() {
        String termo = campoPesquisa.getText().toLowerCase();

        // Colocar_diretorio
        String[] nomesArquivos = new File(novoEndereco).list();

        List<ArquivoItem> arquivosEncontrados = new ArrayList<>();

        for (int i = 0; i < nomesArquivos.length; i++) {
            if (nomesArquivos[i].replace(".txt", "").toLowerCase().contains(termo)) {
                arquivosEncontrados.add(new ArquivoItem(i + 1, ".  " + nomesArquivos[i].replace(".txt", "")));
            }
        }

        if (termo.isEmpty()) {
            lista.setListData(nomesArquivosSemExtensao.toArray(new ArquivoItem[0]));
        } else {
            lista.setListData(arquivosEncontrados.toArray(new ArquivoItem[0]));
        }
    }

    private static class ArquivoItem {
        private final int numero;
        private final String nomeArquivo;

        public ArquivoItem(int numero, String nomeArquivo) {
            this.numero = numero;
            this.nomeArquivo = nomeArquivo;
        }

        public int getNumero() {
            return numero;
        }

        public String getNomeArquivo() {
            return nomeArquivo;
        }

        @Override
        public String toString() {
            return numero + " " + nomeArquivo;
        }
    }

    private static class ArquivoListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ArquivoItem) {
                ArquivoItem arquivoItem = (ArquivoItem) value;
                setText(arquivoItem.toString());
            }
            return this;
        }
    }
}
