package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main {

	static JFrame f;
	static GameWindow gm;
	static List<Dimension> cellsFromImage;
	static ImageReader reader;
	protected static Thread tReader;

	public static void main(String[] args) throws InterruptedException {

		f = new JFrame();
		f.setLayout(new BorderLayout());
		gm = new GameWindow();
		f.setTitle("Alex's Game of Life");

		JPanel p = new JPanel();
		JLabel fps = new JLabel("FPS: ");
		JTextField tfFps = new JTextField();
		tfFps.setText(gm.getFps());
		tfFps.setColumns(5);
		tfFps.setHorizontalAlignment(JTextField.CENTER);
		tfFps.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if (tfFps.getText().length() > 3) {
					return;
				}
				if (tfFps != null && !tfFps.getText().isEmpty() && !tfFps.getText().toString().equals("0")) {
					gm.setFps(Integer.parseInt(tfFps.getText()));
				}
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		JButton btShowLogs = new JButton("Show log");
		JButton btLoadImg = new JButton("Load image");
		btShowLogs.setEnabled(false);
		btShowLogs.setToolTipText("Not implemented yet");

		GridBagLayout g = new GridBagLayout();
		g.rowHeights = new int[] { 0, 0, 10, 0 };
		g.rowWeights = new double[] { 0, 0, 1, 0 };
		p.setLayout(g);
		GridBagConstraints gbc_label = new GridBagConstraints();
		GridBagConstraints gbc_textfield = new GridBagConstraints();
		GridBagConstraints gbc_logsButton = new GridBagConstraints();
		GridBagConstraints gbc_imgButton = new GridBagConstraints();

		gbc_label.gridx = 1;
		gbc_label.gridy = 0;

		gbc_textfield.gridx = 2;
		gbc_textfield.gridy = 0;

		gbc_logsButton.gridx = 1;
		gbc_logsButton.gridy = 2;
		gbc_logsButton.gridwidth = 2;

		gbc_imgButton.gridx = 0;
		gbc_imgButton.gridy = 0;
		gbc_imgButton.insets = new Insets(0, 0, 0, 50);
		gbc_imgButton.gridheight = 4;

		p.add(fps, gbc_label);
		p.add(tfFps, gbc_textfield);
		p.add(btShowLogs, gbc_logsButton);
		p.add(btLoadImg, gbc_imgButton);

		p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		f.add(p, BorderLayout.NORTH);

		f.add(gm, BorderLayout.SOUTH);

		btLoadImg.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e) {
				tReader = new Thread(() -> {
					try {
						reader = new ImageReader();
						reader.getActiveCells();
					} catch (IOException | InterruptedException e1) {
						e1.printStackTrace();
					}
				});
				tReader.start();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});

		Thread gameThread = new Thread(() -> {
			gm.repintar(f);
		});
		gameThread.start();

		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

	}

}
