package com.visuais;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.processamento.ComparadorImagem;
import com.registro.usuario.CarregarDados;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class TelaCentral {

	private JFrame frame;
	private JLabel labelHorario;
	public String nome;
    public String turno;
    public String funcao;
    public String id;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaCentral window = new TelaCentral();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TelaCentral() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(" Tela do Funcionário");
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 638, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(frame);
		frame.setResizable(false);
		
		JTextArea textArea = new JTextArea();
		textArea.setForeground(new Color(255, 255, 255));
		textArea.setBackground(new Color(0, 0, 0));
		textArea.setBounds(289, 92, 296, 175);
		frame.getContentPane().add(textArea);

		// Adiciona o JTextArea a um JScrollPane
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(289, 92, 317, 175);
		frame.getContentPane().add(scrollPane);
		
		labelHorario = new JLabel();
        labelHorario.setFont(new Font("Arial", Font.BOLD, 13));
        labelHorario.setBounds(475, 286, 124, 17);
        frame.getContentPane().add(labelHorario);

		JButton btnNewButton = new JButton("SAIR");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 System.exit(0);
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton.setBounds(540, 40, 60, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Registros");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TelaRegistroCentral registro = new TelaRegistroCentral();
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_1.setBounds(420, 40, 89, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Bater Ponto");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComparadorImagem ci = new ComparadorImagem();
				ci.setComparar(textArea);
			}
		});
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_2.setBounds(306, 40, 104, 23);
		frame.getContentPane().add(btnNewButton_2);
		
		CarregarDados carregarDados = new CarregarDados();
        String[] listaDados = carregarDados.setBuscar();
		
        nome = listaDados[0];
        turno = listaDados[1];
        funcao = listaDados[2];
        id = listaDados[3];
        
		JLabel lblNewLabel = new JLabel("Nome: " + nome);
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel.setBounds(20, 89, 259, 28);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblFacepoint = new JLabel("FacePoint");
		lblFacepoint.setForeground(new Color(154, 214, 181));
		lblFacepoint.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblFacepoint.setBounds(20, 5, 93, 23);
		frame.getContentPane().add(lblFacepoint);
		
		JLabel lblFacepoint_1 = new JLabel("FacePoint");
		lblFacepoint_1.setForeground(new Color(0, 128, 64));
		lblFacepoint_1.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblFacepoint_1.setBounds(23, 7, 93, 23);
		frame.getContentPane().add(lblFacepoint_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 622, 33);
		panel_1.setBackground(new Color(74, 149, 149));
		frame.getContentPane().add(panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 278, 622, 33);
		panel_2.setBackground(new Color(74, 149, 149));
		frame.getContentPane().add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(106, 181, 181));
		panel_3.setBounds(0, 29, 622, 49);
		frame.getContentPane().add(panel_3);
		
		JLabel lblId = new JLabel("Turno: " + turno);
		lblId.setForeground(new Color(0, 0, 0));
		lblId.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblId.setBounds(20, 128, 259, 28);
		frame.getContentPane().add(lblId);
		
		JLabel lblId_1 = new JLabel("Função: " + funcao);
		lblId_1.setForeground(new Color(0, 0, 0));
		lblId_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblId_1.setBounds(20, 167, 259, 28);
		frame.getContentPane().add(lblId_1);
		
		JLabel lblId_1_1 = new JLabel("ID: " + id);
		lblId_1_1.setForeground(new Color(0, 0, 0));
		lblId_1_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblId_1_1.setBounds(20, 206, 259, 28);
		frame.getContentPane().add(lblId_1_1);
		
		// Configura um temporizador para atualizar o horário a cada segundo
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirHorarioLocal();
            }
        });
        timer.start();
		
	}
	
	private void exibirHorarioLocal() {
        // Obtém o horário local
        Date horarioLocal = new Date();

        // Formata a data e hora
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");

        String horarioFormatado = formato.format(horarioLocal);

        // Atualiza o texto na interface gráfica
        labelHorario.setText(horarioFormatado);
    }
}
