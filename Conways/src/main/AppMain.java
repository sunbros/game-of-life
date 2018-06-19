package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AppMain {

	static JFrame frame;
	static PreviewImage preview;

	static int horizontalCellSpacing = 1;
	static int verticalCellSpacing = 1;
	static int cellWidth = 15;
	static int cellHeight = 60;

	static JTextField tfCellWidth;
	static JTextField tfHorizontalCellSpacing;

	public static void main(String[] args) {

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		preview = new PreviewImage();

		JButton btLoadImage = new JButton("Load image");
		btLoadImage.addActionListener((e) -> {

			JFileChooser chooser = new JFileChooser();
			if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File f = chooser.getSelectedFile();
			if (f != null) {
				try {
					BufferedImage img = ImageIO.read(f);
					preview.setImage(img);
					updatePreview();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		JPanel textFieldGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		JLabel lblCellWidth = new JLabel("Cell width");
		tfCellWidth = new JTextField();
		tfCellWidth.setColumns(3);
		tfCellWidth.getDocument().addDocumentListener(repaintListener);

		JLabel lblHorizontalCellSpacing = new JLabel("Horizontal spacing");
		tfHorizontalCellSpacing = new JTextField();
		tfHorizontalCellSpacing.setColumns(3);
		tfHorizontalCellSpacing.getDocument().addDocumentListener(repaintListener);

		textFieldGroup.add(lblCellWidth);
		textFieldGroup.add(tfCellWidth);
		textFieldGroup.add(lblHorizontalCellSpacing);
		textFieldGroup.add(tfHorizontalCellSpacing);

		JScrollPane sp = new JScrollPane(preview);

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(textFieldGroup, BorderLayout.NORTH);
		frame.getContentPane().add(btLoadImage, BorderLayout.SOUTH);
		frame.getContentPane().add(sp, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}

	static DocumentListener repaintListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			updatePreview();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updatePreview();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			return;
		}
	};

	static void updatePreview() {

		preview.cellWidth = parseIntSafe(tfCellWidth.getText(), cellWidth);
		preview.cellHeight = cellHeight;
		preview.horizontalCellSpacing = parseIntSafe(tfHorizontalCellSpacing.getText(), horizontalCellSpacing);
		preview.verticalCellSpacing = verticalCellSpacing;

		preview.repaint();
	}

	static int parseIntSafe(String text, int defaultValue) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException nfe) {
			return defaultValue;
		}
	}
}

class PreviewImage extends JPanel {

	BufferedImage image;

	int horizontalCellSpacing = 5;
	int verticalCellSpacing = 5;
	int cellWidth = 30;
	int cellHeight = 30;

	int scale = 5;

	void setImage(BufferedImage image) {
		this.image = image;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image == null) {
			return;
		}

		Graphics2D g2d = (Graphics2D) g;
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		g2d.setTransform(at);

		g.setColor(Color.red);
		g.drawImage(image, 0, 0, null);

		int width = image.getWidth();
		int height = image.getHeight();

		for (int i = horizontalCellSpacing; i < width; i += (cellWidth + horizontalCellSpacing)) {
			g.drawLine(i, 0, i, height);
		}

		for (int i = verticalCellSpacing; i < height; i += (cellHeight + verticalCellSpacing)) {
			g.drawLine(0, i, width, i);
		}
	}
}
