//package com.vero.compiler.scan;
//
//
//import java.awt.Color;
//import java.awt.EventQueue;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//
//import javax.swing.ButtonGroup;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JRadioButton;
//import javax.swing.JTextArea;
//import javax.swing.JTextField;
//import javax.swing.border.EmptyBorder;
//
//import main.Launcher;
//
//public class GUI_GramerToExpr extends JFrame {
//
//	private static final long serialVersionUID = 1L;
//	private JPanel contentPane;
//	private JTextField tf_gcInput;
//	private JTextField tf_convertResult;
//	private JTextField tf_gteInput;
//	private ButtonGroup bg = new ButtonGroup();
//	private int leftOrRight = -1;
//	private GrammarConverter gc = new GrammarConverter();
//	private GramToExpr gte = new GramToExpr();
//
//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					GUI_GramerToExpr frame = new GUI_GramerToExpr();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}
//
//	/**
//	 * Create the frame.
//	 */
//	public GUI_GramerToExpr() {
//		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(100, 100, 540, 480);
//		setLocationRelativeTo(null);
//		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		setContentPane(contentPane);
//		contentPane.setLayout(null);
//
//		JPanel panel_gc = new JPanel();
//		panel_gc.setBounds(10, 10, 504, 112);
//		contentPane.add(panel_gc);
//		panel_gc.setLayout(null);
//
//		JLabel label_input = new JLabel(
//				"\u8BF7\u8F93\u5165\u76F8\u5E94\u7684\u6587\u6CD5\uFF1A");
//		label_input.setBounds(10, 67, 126, 28);
//		panel_gc.add(label_input);
//
//		JLabel label_select = new JLabel("\u8F6C\u6362\u4E3A\uFF1A");
//		label_select.setBounds(10, 35, 70, 28);
//		panel_gc.add(label_select);
//
//		JRadioButton rdbtn_left = new JRadioButton(
//				"\u5DE6\u7EBF\u6027\u6587\u6CD5");
//		rdbtn_left.setBounds(74, 38, 99, 23);
//		panel_gc.add(rdbtn_left);
//
//		JRadioButton rdbtn_right = new JRadioButton(
//				"\u53F3\u7EBF\u6027\u6587\u6CD5");
//		rdbtn_right.setBounds(175, 38, 99, 23);
//		panel_gc.add(rdbtn_right);
//
//		bg.add(rdbtn_left);
//		bg.add(rdbtn_right);
//
//		rdbtn_left.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent e) {
//				// TODO Auto-generated method stub
//				JRadioButton jrp = (JRadioButton) e.getSource();
//				if (jrp.isSelected())
//					leftOrRight = 1;
//			}
//		});
//
//		rdbtn_right.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent e) {
//				// TODO Auto-generated method stub
//				JRadioButton jrp = (JRadioButton) e.getSource();
//				if (jrp.isSelected())
//					leftOrRight = 2;
//			}
//		});
//
//		tf_gcInput = new JTextField();
//		tf_gcInput.setBounds(135, 66, 139, 32);
//		panel_gc.add(tf_gcInput);
//		tf_gcInput.setColumns(10);
//
//		JLabel label_convertResult = new JLabel(
//				"\u8F6C\u6362\u7ED3\u679C\uFF1A");
//		label_convertResult.setBounds(359, 42, 77, 15);
//		panel_gc.add(label_convertResult);
//
//		tf_convertResult = new JTextField();
//		tf_convertResult.setBounds(359, 66, 137, 31);
//		panel_gc.add(tf_convertResult);
//		tf_convertResult.setColumns(10);
//
//		JLabel label = new JLabel("\u6587\u6CD5\u8F6C\u6362\uFF1A");
//		label.setBounds(10, 10, 70, 15);
//		panel_gc.add(label);
//
//		JButton btn_convert1 = new JButton("\u8F6C\u6362");
//		btn_convert1.setBounds(279, 67, 70, 28);
//		panel_gc.add(btn_convert1);
//
//		JLabel lblaa = new JLabel(
//				"\u4EA7\u751F\u5F0F\u7528\u201C-\u201D\u8868\u793A\u63A8\u51FA\uFF0C\u5F62\u5982\uFF1AA-a");
//		lblaa.setForeground(Color.RED);
//		lblaa.setBounds(90, 10, 406, 15);
//		panel_gc.add(lblaa);
//		btn_convert1.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String input = tf_gcInput.getText().trim();
//				if (!input.isEmpty() && leftOrRight != -1) {
//					tf_convertResult.setText(gc.convert(input, leftOrRight));
//				}
//			}
//		});
//
//		JPanel panel_gte = new JPanel();
//		panel_gte.setBounds(10, 132, 504, 300);
//		contentPane.add(panel_gte);
//		panel_gte.setLayout(null);
//
//		JLabel label_1 = new JLabel(
//				"\u53F3\u7EBF\u6027\u6587\u6CD5\u8F6C\u4E3A\u6B63\u89C4\u5F0F\uFF1A");
//		label_1.setBounds(10, 10, 544, 15);
//		panel_gte.add(label_1);
//
//		JLabel label_input2 = new JLabel(
//				"\u8BF7\u8F93\u5165\u5408\u6CD5\u7684\u53F3\u7EBF\u6027\u6587\u6CD5\uFF1A");
//		label_input2.setBounds(10, 134, 166, 15);
//		panel_gte.add(label_input2);
//
//		tf_gteInput = new JTextField();
//		tf_gteInput.setBounds(173, 126, 149, 32);
//		panel_gte.add(tf_gteInput);
//		tf_gteInput.setColumns(10);
//
//		JTextArea textArea = new JTextArea();
//		textArea.setEditable(false);
//		textArea.setForeground(Color.RED);
//		textArea.setBounds(10, 35, 484, 84);
//		textArea.setText("������Լ����\n1.�ķ�Ϊ�Ϸ����������ķ������磺S=aS+b\n2.�ķ���ʼ����Ϊ��S��\n3.����ĵ�һ������ʽΪ��ʼ���ŵĲ���ʽ");
//		panel_gte.add(textArea);
//
//		JButton button_add = new JButton("\u6DFB\u52A0\u4EA7\u751F\u5F0F");
//		button_add.setBounds(332, 125, 149, 32);
//		panel_gte.add(button_add);
//
//		JLabel label_2 = new JLabel("\u6587\u6CD5\uFF1A");
//		label_2.setBounds(10, 173, 54, 15);
//		panel_gte.add(label_2);
//
//		JTextArea textArea_gramar = new JTextArea();
//		textArea_gramar.setBounds(10, 198, 183, 92);
//		panel_gte.add(textArea_gramar);
//
//		JLabel label_3 = new JLabel("\u8F6C\u6362\u7ED3\u679C\uFF1A");
//		label_3.setBounds(281, 168, 76, 15);
//		panel_gte.add(label_3);
//
//		JTextArea textArea_result = new JTextArea();
//		textArea_result.setBounds(281, 198, 213, 92);
//		textArea_result.setLineWrap(true);
//		panel_gte.add(textArea_result);
//
//		JButton btn_convert2 = new JButton("\u8F6C\u6362");
//		btn_convert2.setBounds(203, 206, 69, 32);
//		panel_gte.add(btn_convert2);
//
//		JButton btn_clear = new JButton("\u6E05\u7A7A");
//		btn_clear.setBounds(203, 248, 69, 30);
//		panel_gte.add(btn_clear);
//
//		button_add.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				String input = tf_gteInput.getText().trim();
//				if (!input.isEmpty()) {
//					textArea_gramar.append(input + "\n");
//					gte.addInput(input);
//					tf_gteInput.setText("");
//				}
//			}
//		});
//
//		btn_convert2.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if (gte.getInputSize() > 0) {
//					gte.analyze(gte.getInputSize());
//					textArea_result.setText(gte.getResult());
//				}
//			}
//		});
//		btn_clear.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				gte.clear();
//				tf_gteInput.setText("");
//				textArea_gramar.setText("");
//				textArea_result.setText("");
//			}
//		});
//
//		addWindowListener(new WindowAdapter() {// ���ڹرշ���ѡ�񴰿�
//			@Override
//			public void windowClosing(WindowEvent e) {
//				Launcher frame = new Launcher();
//				frame.setVisible(true);
//			}
//		});
//
//	}
//}
