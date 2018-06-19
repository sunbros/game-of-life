package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ImageReader {

	JDialog dialog;
	BufferedImage image;
	static int scale = 3;
	static int horizontalCellSpacing = 5;
	static int verticalCellSpacing = 5;
	static int cellWidth = 30;
	static int cellHeight = 30;
	static JLabel panelDialog;
	static JTextField hCellSpacingtf;
	static JTextField vCellSpacingtf;
	static JTextField cellWidthtf;
	static JTextField cellHeighttf;
	static JTextField scaletf;
	List<Dimension> activeCells = new ArrayList<>();

	private void openDialog() {
		if (image != null) {

			final int TEXTFIELD_WIDTH = 5;

			JPanel panelBotones = new JPanel();
			JLabel hCellSpacingLabel = new JLabel("Horizontal Cell Spacing: ");
			hCellSpacingtf = new JTextField(TEXTFIELD_WIDTH);
			hCellSpacingtf.getDocument().addDocumentListener(repaintListener);
			hCellSpacingtf.setText(((Integer) horizontalCellSpacing).toString());
			JLabel vCellSpacingLabel = new JLabel("Vertical Cell Spacing: ");
			vCellSpacingtf = new JTextField(TEXTFIELD_WIDTH);
			vCellSpacingtf.getDocument().addDocumentListener(repaintListener);
			vCellSpacingtf.setText(((Integer) verticalCellSpacing).toString());
			JLabel cellWidthLabel = new JLabel("Cell Width: ");
			cellWidthtf = new JTextField(TEXTFIELD_WIDTH);
			cellWidthtf.getDocument().addDocumentListener(repaintListener);
			cellWidthtf.setText(((Integer) cellWidth).toString());
			JLabel cellHeightLabel = new JLabel("Cell Height: ");
			cellHeighttf = new JTextField(TEXTFIELD_WIDTH);
			cellHeighttf.getDocument().addDocumentListener(repaintListener);
			cellHeighttf.setText(((Integer) cellHeight).toString());
			JLabel scaleLabel = new JLabel("Scale: ");
			scaletf = new JTextField(TEXTFIELD_WIDTH);
			scaletf.getDocument().addDocumentListener(repaintListener);
			scaletf.setText(((Integer) scale).toString());

			panelBotones.add(hCellSpacingLabel);
			panelBotones.add(hCellSpacingtf);
			panelBotones.add(vCellSpacingLabel);
			panelBotones.add(vCellSpacingtf);
			panelBotones.add(cellWidthLabel);
			panelBotones.add(cellWidthtf);
			panelBotones.add(cellHeightLabel);
			panelBotones.add(cellHeighttf);
			panelBotones.add(scaleLabel);
			panelBotones.add(scaletf);

			panelDialog = new JLabel() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g) {
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
			};

			dialog = new JDialog();
			dialog.add(panelBotones, BorderLayout.NORTH);
			dialog.add(panelDialog, BorderLayout.CENTER);

			// botón inferior para cargar revisar la imagen

			JButton btGenerate = new JButton("Generate");

			btGenerate.addActionListener((e) -> {
				try {
					getCellsFromImage();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			});

			JPanel panelGenerar = new JPanel();
			panelGenerar.add(btGenerate);

			dialog.add(panelGenerar, BorderLayout.SOUTH);

			dialog.setTitle("Load image data");

			panelDialog.repaint();

			dialog.setPreferredSize(new Dimension(image.getWidth() * scale, image.getHeight() * scale
					+ panelGenerar.getPreferredSize().height + panelBotones.getPreferredSize().height));

			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
	}

	private void lockActiveCells() throws InterruptedException {
		if (activeCells.size() == 0) {
			synchronized (activeCells) {
				wait();
			}
		}
	}

	private void unlockActiveCells() throws InterruptedException {
		synchronized (activeCells) {
			notify();
		}
	}

	public List<Dimension> getActiveCells() {
		return activeCells;
	}

	private synchronized void getCellsFromImage() throws InterruptedException {
		// valores de cada pixel
		if (image != null) {
			activeCells = new ArrayList<>();
			int img_width = image.getWidth();
			int img_height = image.getHeight();
			for (int x = 0; x < img_width + horizontalCellSpacing; x = x + (cellWidth / 2)) {
				for (int y = 0; y < img_height + verticalCellSpacing; y = y + (cellHeight / 2)) {
					if (x > img_width || y > img_height) {
						continue;
					}
					int pixel_value = image.getRGB(x, y);

					int alpha = (pixel_value >> 24) & 0xff;
					int red = (pixel_value >> 16) & 0xff;
					int green = (pixel_value >> 8) & 0xff;
					int blue = pixel_value & 0xff;
					// System.out.format("(%d, %d, %d, %d)\n", red, green, blue, alpha);
					if (red < 120 && green < 120 && blue < 120) {
						synchronized (activeCells) {
							activeCells.add(new Dimension(x, y));
						}
					}
				}
			}
		}
		unlockActiveCells();
		dialog.dispose();
	}

	static DocumentListener repaintListener = new DocumentListener() {

		@Override
		public void removeUpdate(DocumentEvent e) {
			if (panelDialog != null) {
				try {
					scale = Integer.parseInt(scaletf.getText());
					horizontalCellSpacing = Integer.parseInt(hCellSpacingtf.getText());
					verticalCellSpacing = Integer.parseInt(vCellSpacingtf.getText());
					cellWidth = Integer.parseInt(cellWidthtf.getText());
					cellHeight = Integer.parseInt(cellHeighttf.getText());
					panelDialog.repaint();
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
			}
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			if (panelDialog != null) {
				scale = Integer.parseInt(scaletf.getText());
				horizontalCellSpacing = Integer.parseInt(hCellSpacingtf.getText());
				verticalCellSpacing = Integer.parseInt(vCellSpacingtf.getText());
				cellWidth = Integer.parseInt(cellWidthtf.getText());
				cellHeight = Integer.parseInt(cellHeighttf.getText());
				panelDialog.repaint();
			}
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			return;
		}
	};

	public ImageReader() throws IOException, InterruptedException {
		final JFileChooser fc = new JFileChooser();
		int value_fc = fc.showOpenDialog(new JPanel());
		if (value_fc == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			BufferedImage img = ImageIO.read(f);
			image = img;
			lockActiveCells();
			openDialog();
		}
	}

}
