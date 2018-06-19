package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private final int MAX_WIDTH = 1600;
	private final int MAX_HEIGHT = 900;

	private static boolean interrupted = false;

	int stepNumber, ppl;
	int FPS = 60;
	boolean[][] cellStates, cellCopia;
	List<Dimension> activeCells, copiaLista;
	long start, elapsedTime;

	public int getStepNumber() {
		return stepNumber;
	}

	public String getFps() {
		Integer fps = FPS;
		return fps.toString();
	}

	public void setFps(int n) {
		FPS = n;
	}

	public GameWindow(List<Dimension> cellList) {
		activeCells = new ArrayList<>();
		copiaLista = new ArrayList<>();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
	}

	public GameWindow() {
		activeCells = new ArrayList<>();
		copiaLista = new ArrayList<>();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		stepNumber = 0;
		cellStates = new boolean[MAX_WIDTH][MAX_HEIGHT];
		cellCopia = new boolean[MAX_WIDTH][MAX_HEIGHT];
		cellStates[200][200] = true;
		cellStates[201][200] = true;
		cellStates[201][202] = true;
		cellStates[203][201] = true;
		cellStates[204][200] = true;
		cellStates[205][200] = true;
		cellStates[206][200] = true;
		activeCells.add(new Dimension(200, 200));
		activeCells.add(new Dimension(201, 200));
		activeCells.add(new Dimension(201, 202));
		activeCells.add(new Dimension(203, 201));
		activeCells.add(new Dimension(204, 200));
		activeCells.add(new Dimension(205, 200));
		activeCells.add(new Dimension(206, 200));

		ppl = 7;

	}

	private long getFpsToShow(long e) {
		if (1000 / e < 50) {
			System.out.println("[FPS Drops] " + 1000 / e + "fps");
		}
		return 1000 / e;
	}

	public void start() {
		interrupted = false;
	}

	public void stop() {
		interrupted = true;
	}

	synchronized public void repintar(JFrame f) {
		while (true) {
			start = System.currentTimeMillis();
			if (interrupted == true) {
				elapsedTime = System.currentTimeMillis() - start;
				continue;
			}
			long fpsToShow = 0;
			try {
				wait(1000 / FPS);
				if (elapsedTime > 0) {
					fpsToShow = getFpsToShow(elapsedTime);
				}
				f.setTitle("Alex's Game of Life | Gen. nº " + stepNumber + " | FPS: " + fpsToShow);
			} catch (Exception e) {
				System.out.println(e);
			}
			super.repaint();
			stepNumber++;
			elapsedTime = System.currentTimeMillis() - start;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.WHITE);

		paintActives(g2);
		g2.drawString("Step nº " + stepNumber, 10, getHeight() - 10);
		g2.drawString(ppl + " living cells", 10, getHeight() - 25);

	}

	// TODO mejorar el algoritmo para el arrayList de activas
	private void paintActives(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		cellCopia = new boolean[MAX_WIDTH][MAX_HEIGHT];
		copiaLista = new ArrayList<>();
		for (Dimension cell : activeCells) {
			// pintamos la actual ya que por defecto nos recorremos las vivas
			g2.setColor(Color.WHITE);
			g2.drawRect(cell.width, cell.height, 1, 1);
			int count = 0;
			// recorremos primero las de alrededor para buscar las inactivas
			for (int i = cell.width - 1; i <= cell.width + 1; i++) {
				if (cell.width < 0 || cell.width >= MAX_WIDTH) {
					continue;
				}
				for (int j = cell.height - 1; j <= cell.height + 1; j++) {
					if (cell.height < 0 || cell.height >= MAX_HEIGHT) {
						continue;
					}
					if (cell.width == i && cell.height == j) {
						continue;
					}

					if (i < 0 || i >= MAX_WIDTH) {
						continue;
					}
					if (j < 0 || j >= MAX_HEIGHT) {
						continue;
					}

					// hacemos el contains en la copiaLista para que no chequee de nuevo las que
					// acaban de cambiar
					if (!cellStates[i][j] && !copiaLista.contains(new Dimension(i, j))) {
						// logica de la inactiva, miramos las de su alrededor
						checkInactives(new Dimension(i, j), g2);
					}
					if (cellStates[i][j]) {
						// si la celda está activa, sumamos para ver que debe pasar con la cell que
						// estamos calculando
						count++;
					}
				}
			}
			// se muere la celda
			if (count < 2) {
				g2.setColor(Color.BLACK);
				g2.drawRect(cell.width, cell.height, 1, 1);
				ppl--;
				cellCopia[cell.width][cell.height] = false;
				// sobrepoblación
			} else if (count > 3) {
				g2.setColor(Color.BLACK);
				g2.drawRect(cell.width, cell.height, 1, 1);
				ppl--;
				cellCopia[cell.width][cell.height] = false;
				// sobrevive
			} else if (count == 2 || count == 3) {
				g2.setColor(Color.WHITE);
				g2.drawRect(cell.width, cell.height, 1, 1);
				cellCopia[cell.width][cell.height] = true;
				copiaLista.add(cell);
			}
		}
		cellStates = cellCopia;
		activeCells = copiaLista;
	}

	private void checkInactives(Dimension cell, Graphics2D g2) {
		int count = 0;
		for (int i = cell.width - 1; i <= cell.width + 1; i++) {
			if (cell.width < 0 || cell.width >= MAX_WIDTH) {
				continue;
			}
			for (int j = cell.height - 1; j <= cell.height + 1; j++) {
				if (cell.height < 0 || cell.height >= MAX_HEIGHT) {
					continue;
				}
				if (cell.width == i && cell.height == j) {
					continue;
				}

				if (i < 0 || i >= MAX_WIDTH) {
					continue;
				}
				if (j < 0 || j >= MAX_HEIGHT) {
					continue;
				}

				if (cellStates[i][j]) {
					count++;
				}
			}
		}
		// si contamos 3 alrededor, nace la celda
		if (count == 3) {
			g2.setColor(Color.WHITE);
			g2.drawRect(cell.width, cell.height, 1, 1);
			ppl++;
			cellCopia[cell.width][cell.height] = true;
			copiaLista.add(cell);
		}
	}

	// TODO fin de algoritmo nuevo

	private void checkCells(Graphics2D g2) {

		cellCopia = new boolean[MAX_WIDTH][MAX_HEIGHT];

		// Recorremos ventana
		for (int x = 0; x < cellStates.length; x++) {
			for (int y = 0; y < cellStates[x].length; y++) {
				if (cellStates[x][y]) {
					g2.setColor(Color.WHITE);
					g2.drawRect(x, y, 1, 1);
					int count = 0;
					for (int i = x - 1; i <= x + 1; i++) {
						if (x < 0 || x >= MAX_WIDTH) {
							continue;
						}
						for (int j = y - 1; j <= y + 1; j++) {

							if (y < 0 || y >= MAX_HEIGHT) {
								continue;
							}

							if (x == i && y == j) {
								continue;
							}

							if (i < 0 || i >= MAX_WIDTH) {
								continue;
							}
							if (j < 0 || j >= MAX_HEIGHT) {
								continue;
							}

							if (cellStates[i][j]) {
								count++;
							}
						}
					}
					// se muere la celda
					if (count < 2) {
						g2.setColor(Color.BLACK);
						g2.drawRect(x, y, 1, 1);
						ppl--;
						cellCopia[x][y] = false;
						// sobrepoblación
					} else if (count > 3) {
						g2.setColor(Color.BLACK);
						g2.drawRect(x, y, 1, 1);
						ppl--;
						cellCopia[x][y] = false;
						// sobrevive
					} else if (count == 2 || count == 3) {
						g2.setColor(Color.WHITE);
						g2.drawRect(x, y, 1, 1);
						cellCopia[x][y] = true;
					}
				} else {
					int count = 0;
					for (int i = x - 1; i <= x + 1; i++) {
						if (x < 0 || x >= MAX_WIDTH) {
							continue;
						}
						for (int j = y - 1; j <= y + 1; j++) {

							if (y < 0 || y >= MAX_HEIGHT) {
								continue;
							}

							if (i < 0 || i >= MAX_WIDTH) {
								continue;
							}
							if (j < 0 || j >= MAX_HEIGHT) {
								continue;
							}
							if (cellStates[i][j]) {
								count++;
							}
						}
					}
					// nace la celda
					if (count == 3) {
						// fórmula para el get de la posición de la nueva cell
						g2.setColor(Color.WHITE);
						g2.drawRect(x, y, 1, 1);
						ppl++;
						cellCopia[x][y] = true;
					}
				}
			}
		}
		g2.setColor(Color.WHITE);
		cellStates = cellCopia;
	}

}
