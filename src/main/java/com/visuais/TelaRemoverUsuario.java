package com.visuais;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TelaRemoverUsuario {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TelaRemoverUsuario window = new TelaRemoverUsuario();
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
	public TelaRemoverUsuario() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame(" Remover Funcionario");
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setBounds(100, 100, 351, 243);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(frame);
		frame.setResizable(false);
		
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
		panel_1.setBounds(0, 0, 335, 33);
		panel_1.setBackground(new Color(74, 149, 149));
		frame.getContentPane().add(panel_1);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(0, 182, 335, 23);
		panel_2.setBackground(new Color(74, 149, 149));
		frame.getContentPane().add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(106, 181, 181));
		panel_3.setBounds(0, 29, 335, 16);
		frame.getContentPane().add(panel_3);
		
		JButton btnNewButton = new JButton("Remover");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton.setBounds(70, 135, 87, 23);
		frame.getContentPane().add(btnNewButton);
		
		textField = new JTextField();
		textField.setBounds(172, 80, 132, 25);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("ID do funcionário:");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel.setBounds(30, 80, 132, 20);
		frame.getContentPane().add(lblNewLabel);
		
		JButton btnNewButton_1 = new JButton("Cancelar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 10));
		btnNewButton_1.setBounds(177, 135, 87, 23);
		frame.getContentPane().add(btnNewButton_1);
	}

}
