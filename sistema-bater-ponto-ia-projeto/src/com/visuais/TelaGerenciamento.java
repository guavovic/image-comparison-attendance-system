package com.visuais;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.ImageIcon;

public class TelaGerenciamento {

	private JFrame frame;
	private JLabel labelHorario;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaGerenciamento window = new TelaGerenciamento();
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
	public TelaGerenciamento() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(" Tela de Gerenciamento");
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 816, 472);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(frame);
		frame.setResizable(false);
		
		labelHorario = new JLabel();
        labelHorario.setFont(new Font("Arial", Font.BOLD, 13));
        labelHorario.setBounds(660, 408, 124, 17);
        frame.getContentPane().add(labelHorario);

		JButton btnNewButton = new JButton("SAIR");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 System.exit(0);
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton.setBounds(710, 40, 60, 23);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Gerenciamento de Pontos");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TelaRegistroGerenciamento registro = new TelaRegistroGerenciamento();
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_1.setBounds(514, 40, 166, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("Gerenciamento de Acessos");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TelaGerenciamentoAcesso tga = new TelaGerenciamentoAcesso();
				tga.main();
			}
		});
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_2.setBounds(330, 40, 174, 23);
		frame.getContentPane().add(btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Lista de Avisos");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_3.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_3.setBounds(206, 40, 114, 23);
		frame.getContentPane().add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("Gerar Relatorio");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton_4.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_4.setBounds(82, 40, 114, 23);
		frame.getContentPane().add(btnNewButton_4);
		
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
		panel_1.setBounds(0, 0, 800, 33);
		panel_1.setBackground(new Color(74, 149, 149));
		frame.getContentPane().add(panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 400, 800, 33);
		panel_2.setBackground(new Color(74, 149, 149));
		frame.getContentPane().add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(106, 181, 181));
		panel_3.setBounds(0, 29, 800, 49);
		frame.getContentPane().add(panel_3);
		
		JLabel lblFacepoint_2 = new JLabel("FacePoint");
		lblFacepoint_2.setForeground(new Color(154, 214, 181));
		lblFacepoint_2.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblFacepoint_2.setBounds(330, 193, 136, 23);
		frame.getContentPane().add(lblFacepoint_2);
		
		JLabel lblFacepoint_1_1 = new JLabel("FacePoint");
		lblFacepoint_1_1.setForeground(new Color(0, 128, 64));
		lblFacepoint_1_1.setFont(new Font("Tahoma", Font.BOLD, 26));
		lblFacepoint_1_1.setBounds(332, 186, 136, 40);
		frame.getContentPane().add(lblFacepoint_1_1);
		
		JLabel lblNewLabel = new JLabel("_____________");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(330, 205, 136, 23);
		frame.getContentPane().add(lblNewLabel);
		
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
