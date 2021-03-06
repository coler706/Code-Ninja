package program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.accessibility.AccessibleContext;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Caret;
import javax.swing.text.Document;

public class CodePane extends JPanel implements MouseListener,
		MouseMotionListener, KeyListener {
	public SimpleDocument doc = new SimpleDocument();
	public int selectionStart = 0;
	public int selectionEnd = 0;
	private BlinkTimer cursorTimer = new BlinkTimer(500, this);
	// private BlinkTimer cursorTimer = new BlinkTimer(500, this);
	public Point mousePos = new Point(0, 0);
	public int caretPosInLine = 0;
	public int charInLine = 0;
	public JScrollPane scroller;
	public int language = Language.PYTHON;
	private SyntaxHighlighter syntax = new SyntaxHighlighter(language);
	private ArrayList<Color> coloring = syntax.parse(doc.doc);

	public Insets getMargin() {
		return new Insets(5, 5, 5, 5);
	}

	public Point getSelectionYEnds() {
		int sS = Math.min(selectionStart, selectionEnd);
		int sE = Math.max(selectionStart, selectionEnd);
		double lineStartY = this.getMargin().top;
		boolean selectedLine = false;
		double lineEndY = this.getMargin().top;
		Point caretPos = new Point(0, 0);
		Point caretPos2 = new Point(0, 0);
		// System.out.println(this.getBounds());
		this.setBackground(Color.decode("#fdf6e3"));
		if (this.isFocusOwner()) {
			// this.setBackground(Color.decode("#00f6e3"));
		}
		// this.
		// this.setLocation(50, 0);
		// System.out.println("paint");
		// super.paint(g);

		// g.drawLine(0,0,mousePos.x,mousePos.y);
		int line = 1;
		double lineY = this.getFontMetrics(this.getFont()).getHeight();
		int caretPosInLine = 0;
		int maxLine = 0;
		double charX = 50;

		if (sE == 0) {
			caretPos2 = new Point((int) (this.getMargin().left
					+ this.getInsets().left + 50),
					(int) (this.getMargin().top + this.getFontMetrics(
							this.getFont()).getDescent()));
			selectedLine = true;
		}
		// this.getDocument().

		for (int i = 0; i < this.doc.doc.size(); i++) {

			Character c = this.doc.doc.get(i);

			if (new Character(c).equals(new Character('\n'))) {
				if (i < Math.max(selectionStart, selectionEnd)
						&& i > Math.min(selectionStart, selectionEnd) - 1) {

				}
				lineEndY = lineY;
				if (selectedLine) {

					selectedLine = false;
				}
				lineStartY = lineY;
				line++;
				lineY += this.getFontMetrics(this.getFont()).getHeight();
				charX = 50;
				if (sE == i + 1) {
					selectedLine = true;
					caretPos2 = new Point((int) (this.getMargin().left
							+ this.getInsets().left + charX),
							(int) (this.getMargin().top + lineY + this
									.getFontMetrics(this.getFont())
									.getDescent()));

				}
				if (sS == i + 1) {
					selectedLine = true;
					caretPos2 = new Point((int) (this.getMargin().left
							+ this.getInsets().left + charX),
							(int) (this.getMargin().top
									+ lineY
									- this.getFontMetrics(this.getFont())
											.getHeight() + this.getFontMetrics(
									this.getFont()).getDescent()));

				}
			} else {
				if (charX + this.getFontMetrics(this.getFont()).charWidth(c) < this
						.getWidth() - this.getMargin().right) {

				} else {
					lineY += this.getFontMetrics(this.getFont()).getHeight();
					charX = 50;
				}
				if (i < Math.max(selectionStart, selectionEnd)
						&& i > Math.min(selectionStart, selectionEnd) - 1) {

				}

				charX += this.getFontMetrics(this.getFont()).charWidth(c);

				if (sE == i + 1) {

					caretPos2 = new Point((int) (this.getMargin().left
							+ this.getInsets().left + charX),
							(int) (this.getMargin().top + lineY + this
									.getFontMetrics(this.getFont())
									.getDescent()));

				}
				if (sS == i + 1) {

					caretPos = new Point((int) (this.getMargin().left
							+ this.getInsets().left + charX),
							(int) (this.getMargin().top
									+ lineY
									- this.getFontMetrics(this.getFont())
											.getHeight() + this.getFontMetrics(
									this.getFont()).getDescent()));

				}
				charInLine++;
			}
		}
		if (sE == this.doc.doc.size()) {
			selectedLine = true;
			caretPos2 = new Point((int) (this.getMargin().left
					+ this.getInsets().left + charX),
					(int) (this.getMargin().top + lineY + this.getFontMetrics(
							this.getFont()).getDescent()));

		}
		if (sS == this.doc.doc.size()) {
			selectedLine = true;
			caretPos2 = new Point(
					(int) (this.getMargin().left + this.getInsets().left + charX),
					(int) (this.getMargin().top + lineY
							- this.getFontMetrics(this.getFont()).getHeight() + this
							.getFontMetrics(this.getFont()).getDescent()));

		}
		return new Point(caretPos.y, caretPos2.y);
	}

	public CodePane() {
		this.doc.insertString(0, 0, "for i in range(10):\n    print(i)");
		try {
			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(
					"primer.ttf")));
		} catch (IOException | FontFormatException e) {
			// Handle exception
		}
		// this.addKeyListener(this);
		try {
			this.setFont(Font.createFont(Font.TRUETYPE_FONT,
					new File("ProFont.ttf")).deriveFont(Font.PLAIN, 20));
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.enableInputMethods(true);
		this.setFocusable(true);
		this.setAutoscrolls(true);
		this.setFocusTraversalKeysEnabled(false);

	}

	public AccessibleContext getAccessibleContext() {
		return this.accessibleContext;
	}

	public void replaceSelection(String content) {

		int dot = selectionStart;
		int mark = selectionEnd;

		int p0 = Math.min(dot, mark);
		int p1 = Math.max(dot, mark);
		doc.insertString(p0, p1, content);
		selectionStart = p0 + content.length();
		selectionEnd = selectionStart;

	}

	public void paint(Graphics g) {
		coloring = syntax.parse(doc.doc);
		double lineStartY = this.getMargin().top
				+ this.getFontMetrics(this.getFont()).getDescent();
		boolean selectedLine = false;
		double lineEndY = this.getMargin().top;
		Point caretPos = new Point(0, 0);
		double lineNumberAlignment = new String(lineCount() + "").length()
				* g.getFontMetrics().stringWidth("0");
		// System.out.println(new String(lineCount()+"").length());
		// System.out.println(this.getBounds());
		this.setBackground(Color.decode("#fdf6e3"));
		if (this.isFocusOwner()) {
			// this.setBackground(Color.decode("#00f6e3"));
		}
		// this.
		// this.setLocation(50, 0);
		// System.out.println("paint");
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		// g.setColor(Color.WHITE);
		// g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.decode("#657b83"));
		// g.drawLine(0,0,mousePos.x,mousePos.y);
		int line = 1;
		double lineY = g.getFontMetrics().getHeight();
		int charInLine = 0;
		int maxLine = 0;
		double charX = 50;

		if (selectionEnd == 0) {
			caretPos = new Point((int) (this.getMargin().left
					+ this.getInsets().left + 50),
					(int) (this.getMargin().top + g2.getFontMetrics()
							.getDescent()));
			selectedLine = true;
		}
		// this.getDocument().
		g2.drawString(line + "",
				(int) (this.getMargin().left + this.getInsets().left
						+ lineNumberAlignment / 2
						- g.getFontMetrics().stringWidth(line + "") + 25),
				(int) (lineY + this.getMargin().top));
		for (int i = 0; i < this.doc.doc.size(); i++) {

			Character c = this.doc.doc.get(i);

			if (new Character(c).equals(new Character('\n'))) {
				if (i < Math.max(selectionStart, selectionEnd)
						&& i > Math.min(selectionStart, selectionEnd) - 1) {
					g2.setColor(Color.decode("#eee8d5"));
					g2.fillRect((int) (charX + this.getMargin().left + this
							.getInsets().left), (int) (lineY
							+ this.getMargin().top
							- g2.getFontMetrics().getHeight() + g2
							.getFontMetrics().getDescent()), 5, g2
							.getFontMetrics().getHeight());
				}
				lineEndY = (int) (lineY + this.getMargin().top + g2
						.getFontMetrics().getDescent());
				if (selectedLine) {

					g2.setColor(Color.decode("#eee8d5"));
					g2.fillRect(
							(int) (this.getMargin().left + this.getInsets().left),
							(int) lineStartY, 45, (int) (lineEndY - lineStartY));

					g2.setColor(Color.decode("#657b83"));
					g2.drawString(line + "", (int) (this.getMargin().left
							+ this.getInsets().left + lineNumberAlignment / 2
							- g.getFontMetrics().stringWidth(line + "") + 25),
							(int) (lineStartY - (-g2.getFontMetrics()
									.getHeight() + g2.getFontMetrics()
									.getDescent())));
					selectedLine = false;
				}
				lineStartY = (int) (lineY + this.getMargin().top + g2
						.getFontMetrics().getDescent());
				line++;
				lineY += g.getFontMetrics().getHeight();
				charX = 50;
				if (selectionEnd == i + 1) {
					selectedLine = true;
					caretPos = new Point((int) (this.getMargin().left
							+ this.getInsets().left + charX),
							(int) (this.getMargin().top + lineY
									- g2.getFontMetrics().getHeight() + g2
									.getFontMetrics().getDescent()));

				}
				g2.setColor(Color.decode("#657b83"));
				g2.drawString(
						line + "",
						(int) (this.getMargin().left + this.getInsets().left
								+ lineNumberAlignment / 2
								- g.getFontMetrics().stringWidth(line + "") + 25),
						(int) (lineY + this.getMargin().top));
			} else {
				if (charX + g2.getFontMetrics().charWidth(c) < this.getWidth()
						- this.getMargin().right) {

				} else {
					lineY += g2.getFontMetrics().getHeight();
					charX = 50;
				}
				if (i < Math.max(selectionStart, selectionEnd)
						&& i > Math.min(selectionStart, selectionEnd) - 1) {
					g2.setColor(Color.decode("#eee8d5"));
					g2.fillRect((int) (charX + this.getMargin().left + this
							.getInsets().left), (int) (lineY
							+ this.getMargin().top
							- g2.getFontMetrics().getHeight() + g2
							.getFontMetrics().getDescent()), g2
							.getFontMetrics().charWidth(c), g2.getFontMetrics()
							.getHeight());
				}
				// g2.setColor(Color.decode("#657b83"));
				g2.setColor(coloring.get(i));
				g2.drawString(
						c + "",
						(int) (charX + this.getMargin().left + this.getInsets().left),
						(int) (lineY + this.getMargin().top));

				charX += g2.getFontMetrics().charWidth(c);
				if (selectionEnd == i + 1) {
					selectedLine = true;
				}
				if (selectionEnd == i + 1) {

					caretPos = new Point((int) (this.getMargin().left
							+ this.getInsets().left + charX),
							(int) (this.getMargin().top + lineY
									- g2.getFontMetrics().getHeight() + g2
									.getFontMetrics().getDescent()));

				}
				charInLine++;
			}
		}
		lineEndY = lineY;
		if (selectedLine) {
			lineEndY = (int) (lineY + this.getMargin().top + g2
					.getFontMetrics().getDescent());
			if (selectedLine) {

				g2.setColor(Color.decode("#eee8d5"));
				g2.fillRect(
						(int) (this.getMargin().left + this.getInsets().left),
						(int) lineStartY, 45, (int) (lineEndY - lineStartY));

				g2.setColor(Color.decode("#657b83"));
				g2.drawString(
						line + "",
						(int) (this.getMargin().left + this.getInsets().left
								+ lineNumberAlignment / 2
								- g.getFontMetrics().stringWidth(line + "") + 25),
						(int) (lineStartY - (-g2.getFontMetrics().getHeight() + g2
								.getFontMetrics().getDescent())));
				selectedLine = false;
			}
			selectedLine = false;
		}
		g2.setColor(Color.decode("#657b83"));
		if (cursorTimer.value == 1 && this.isFocusOwner()) {
			g2.fillRect(caretPos.x, caretPos.y, 2, g2.getFontMetrics()
					.getHeight());
		}
		this.setPreferredSize(new Dimension(100, Math.max(
				(int) (lineY + this.getMargin().bottom*2+this.getInsets().bottom+ g2
						.getFontMetrics().getDescent()), 100)));
		this.revalidate();

		// this.getParent().p
	}

	public int lineCount() {

		int count = 1;
		// System.out.println(s.toString());
		for (Character c : this.doc.doc) {

			if (new Character(c).equals(new Character('\n'))) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.grabFocus();
		this.requestFocusInWindow();
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.repaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		this.repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.grabFocus();
		this.requestFocusInWindow();
		this.selectionEnd = getTextPosFromCursor(arg0);
		this.selectionStart = this.selectionEnd;
		this.caretPosInLine=this.doc.whatPosInLineHasPos(this.selectionEnd);
		this.repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		this.selectionEnd = getTextPosFromCursor(arg0);
		this.caretPosInLine=this.doc.whatPosInLineHasPos(this.selectionEnd);
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		this.selectionEnd = getTextPosFromCursor(arg0);
		this.caretPosInLine=this.doc.whatPosInLineHasPos(this.selectionEnd);
		this.repaint();
		
		if (this.scroller != null) {
			System.out.println(this.scroller.getVerticalScrollBar()
					.getVisibleAmount());
			int selectionEndY = this.selectionEnd < this.selectionStart ? getSelectionYEnds().x
					: getSelectionYEnds().y
							- this.getFontMetrics(this.getFont()).getHeight();
			if ((this.selectionEnd < this.selectionStart ? getSelectionYEnds().x
					: getSelectionYEnds().y
							- this.getFontMetrics(this.getFont()).getHeight()) < this.scroller
					.getVerticalScrollBar().getValue()) {

				System.out.print(arg0.getPoint().y);
				this.scroller.getVerticalScrollBar().setValue(
						this.scroller.getVerticalScrollBar().getValue()
								+ Math.min(-5, Math.max(arg0.getPoint().y
										- this.scroller.getVerticalScrollBar()
												.getValue(), -500)));
			}
			if ((this.selectionEnd < this.selectionStart ? getSelectionYEnds().x
					+ this.getFontMetrics(this.getFont()).getHeight()
					: getSelectionYEnds().y) > this.scroller
					.getVerticalScrollBar().getValue()
					+ this.scroller.getVerticalScrollBar().getVisibleAmount()) {
				this.scroller.getVerticalScrollBar().setValue(
						this.scroller.getVerticalScrollBar().getValue()
								+ Math.max(5, Math.min(arg0.getPoint().y
										- this.scroller.getVerticalScrollBar()
												.getValue()
										- this.scroller.getVerticalScrollBar()
												.getVisibleAmount(), 500)));
			}
		}
	}

	public int getTextPosFromCursor(MouseEvent arg0) {
		int select = 0;
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int line = 1;

		double lineY = fm.getHeight();
		int charInLine = 0;
		double charX = 50;
		double minDist = 1000000000;
		double minDistY = 100000;
		// this.getDocument().
		int charDX = (int) (charX + this.getMargin().left + this.getInsets().left);
		int charDY = (int) (this.getMargin().top - fm.getHeight() / 2);
		int distY = Math.abs(arg0.getY() - charDY);
		int dist = Math.abs(arg0.getX() - charDX);
		if (distY < minDistY) {
			minDist = dist;
			minDistY = distY;
			select = 0;
		} else if (distY == minDistY) {
			if (dist < minDist) {
				minDist = dist;
				minDistY = distY;
				select = 0;
			}
		}
		for (int i = 0; i < this.doc.doc.size(); i++) {

			Character c = this.doc.doc.get(i);

			if (new Character(c).equals(new Character('\n'))) {
				line++;
				charDX = (int) (charX + this.getMargin().left + this
						.getInsets().left);
				charDY = (int) (lineY + this.getMargin().top - fm.getHeight() / 2);
				distY = Math.abs(arg0.getY() - charDY);
				dist = Math.abs(arg0.getX() - charDX);
				if (distY < minDistY) {
					minDist = dist;
					minDistY = distY;
					select = i;
				} else if (distY == minDistY) {
					if (dist < minDist) {
						minDist = dist;
						minDistY = distY;
						select = i;
					}
				}
				lineY += fm.getHeight();
				charX = 50;
				charDX = (int) (charX + this.getMargin().left + this
						.getInsets().left);
				charDY = (int) (lineY + this.getMargin().top - fm.getHeight() / 2);
				distY = Math.abs(arg0.getY() - charDY);
				dist = Math.abs(arg0.getX() - charDX);
				if (distY < minDistY) {
					minDist = dist;
					minDistY = distY;
					select = i + 1;
				} else if (distY == minDistY) {
					if (dist < minDist) {
						minDist = dist;
						minDistY = distY;
						select = i + 1;
					}
				}

				/*
				 * if (Math.sqrt(Math.pow(arg0.getX() - charDX, 2) +
				 * Math.pow(arg0.getX() - charDX, 2)) > 1) {
				 * 
				 * }
				 */

			} else {
				if (charX + fm.charWidth(c) < this.getWidth()
						- this.getMargin().right) {

				} else {
					lineY += fm.getHeight();
					charX = 50;
				}
				charDX = (int) (charX + this.getMargin().left + this
						.getInsets().left);
				charDY = (int) (lineY + this.getMargin().top - fm.getHeight() / 2);
				distY = Math.abs(arg0.getY() - charDY);
				dist = Math.abs(arg0.getX() - charDX);
				if (distY < minDistY) {
					minDist = dist;
					minDistY = distY;
					select = i;
				} else if (distY == minDistY) {
					if (dist < minDist) {
						minDist = dist;
						minDistY = distY;
						select = i;
					}
				}
				charX += fm.charWidth(c);

				charInLine++;
			}
		}
		charDX = (int) (charX + this.getMargin().left + this.getInsets().left);
		charDY = (int) (lineY + this.getMargin().top - fm.getHeight() / 2);
		distY = Math.abs(arg0.getY() - charDY);
		dist = Math.abs(arg0.getX() - charDX);
		if (distY < minDistY) {
			minDist = dist;
			minDistY = distY;
			select = this.doc.doc.size();
		} else if (distY == minDistY) {
			if (dist < minDist) {
				minDist = dist;
				minDistY = distY;
				select = this.doc.doc.size();
			}
		}
		return select;

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub\

		mousePos = arg0.getPoint();
		mousePos.translate(-arg0.getComponent().getX(), -arg0.getComponent()
				.getY());
		this.repaint();
	}

	public void keyPressedFlip(KeyEvent arg0, boolean fake) {

		int Tmask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		int Tkey = KeyEvent.CTRL_MASK == Tmask ? KeyEvent.VK_CONTROL : 157;
		if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE
				|| arg0.getKeyCode() == KeyEvent.VK_DELETE) {

			if (selectionStart != selectionEnd) {
				this.replaceSelection("");
			} else {
				if (this.selectionStart > 0) {
					this.selectionStart--;
					this.replaceSelection("");
				}
			}
		} else {
			
			if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
				if (selectionStart != selectionEnd) {
					this.selectionEnd = Math.min(this.selectionStart,this.selectionEnd);
					this.selectionStart = this.selectionEnd;
				} else {
					if (this.selectionStart > 0) {
						this.selectionStart--;
						this.selectionEnd = this.selectionStart;
					}
				}
			} else if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
				if (selectionStart != selectionEnd) {
					this.selectionEnd = Math.max(this.selectionStart,this.selectionEnd);
					this.selectionStart = this.selectionEnd;
				} else {
					if (this.selectionStart < this.doc.doc.size()) {
						this.selectionStart++;
						this.selectionEnd = this.selectionStart;
					}
				}
			} else if (arg0.getKeyCode() == KeyEvent.VK_UP) {
				if (selectionStart != selectionEnd) {
					this.selectionEnd = Math.min(this.selectionStart,this.selectionEnd);
					this.selectionStart = this.selectionEnd;
				} else {
					
					if (this.selectionStart > 0) {
						if (this.doc.whatLineHasPos(this.selectionStart) > 0) {
							System.out.println("line of is:"+this.doc.whatLineHasPos(this.selectionStart));
							this.selectionStart=this.doc.posForPosInLine(this.caretPosInLine,this.doc.whatLineHasPos(this.selectionStart)-1);
							this.selectionEnd = this.selectionStart;
						}
					}
				}
			} else if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
				if (selectionStart != selectionEnd) {
					this.selectionEnd = Math.max(this.selectionStart,this.selectionEnd);
					this.selectionStart = this.selectionEnd;
				} else {
					if (this.selectionStart < this.doc.doc.size()) {
						if (this.doc.whatLineHasPos(this.selectionStart) < this.doc.lineCount()-1) {
							this.selectionStart=this.doc.posForPosInLine(this.caretPosInLine,this.doc.whatLineHasPos(this.selectionStart)+1);
							this.selectionEnd = this.selectionStart;
						}
					}
					
				}
			} else if (arg0.getKeyCode() == KeyEvent.VK_SHIFT
					|| arg0.getKeyCode() == KeyEvent.VK_TAB
					|| arg0.getKeyCode() == Tkey
					|| (arg0.getKeyCode() == KeyEvent.VK_V && (arg0
							.getModifiers() & Tmask) == Tmask)
					|| (arg0.getKeyCode() == KeyEvent.VK_C && (arg0
							.getModifiers() & Tmask) == Tmask)
					|| (arg0.getKeyCode() == KeyEvent.VK_A && (arg0
							.getModifiers() & Tmask) == Tmask)
					|| (arg0.getKeyCode() == KeyEvent.VK_R && (arg0
							.getModifiers() & Tmask) == Tmask)) {
				if (arg0.getKeyCode() == KeyEvent.VK_V
						&& (arg0.getModifiers() & Tmask) == Tmask) {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					String result = "";

					Clipboard clipboard = toolkit.getSystemClipboard();
					try {
						result = (String) clipboard
								.getData(DataFlavor.stringFlavor);
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					this.replaceSelection(result);
				}
				if (arg0.getKeyCode() == KeyEvent.VK_TAB) {
					this.replaceSelection("    " + "");
					this.repaint();
					if (this.scroller != null) {
						System.out.println(this.scroller.getVerticalScrollBar()
								.getVisibleAmount());
						if (getSelectionYEnds().x < this.scroller
								.getVerticalScrollBar().getValue()) {
							this.scroller.getVerticalScrollBar().setValue(
									getSelectionYEnds().x);
						}
						if (getSelectionYEnds().y > this.scroller
								.getVerticalScrollBar().getValue()
								+ this.scroller.getVerticalScrollBar()
										.getVisibleAmount()) {
							this.scroller.getVerticalScrollBar()
									.setValue(
											getSelectionYEnds().y
													- this.getFontMetrics(
															this.getFont())
															.getHeight());
						}
					}

				}
				if (arg0.getKeyCode() == KeyEvent.VK_C
						&& (arg0.getModifiers() & Tmask) == Tmask) {
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					String collect = "";

					for (int i = Math.min(selectionStart, selectionEnd); i < Math
							.max(selectionStart, selectionEnd); i++) {
						collect = collect + this.doc.doc.get(i);
					}
					Clipboard clipboard = toolkit.getSystemClipboard();
					StringSelection selection = new StringSelection(collect);
					clipboard.setContents(selection, selection);

				}
				if (arg0.getKeyCode() == KeyEvent.VK_A
						&& (arg0.getModifiers() & Tmask) == Tmask) {
					this.selectionStart = 0;
					this.selectionEnd = this.doc.doc.size();

				}
			} else {
				// System.out.println(arg0.getModifiers() & KeyEvent.CTRL_MASK);
				// System.out.println(arg0.getKeyCode());

				this.replaceSelection(arg0.getKeyChar() + "");
				this.repaint();
				if (this.scroller != null) {
					System.out.println(this.scroller.getVerticalScrollBar()
							.getVisibleAmount());
					if (getSelectionYEnds().x < this.scroller
							.getVerticalScrollBar().getValue()) {
						this.scroller.getVerticalScrollBar().setValue(
								getSelectionYEnds().x);
					}
					if (getSelectionYEnds().y > this.scroller
							.getVerticalScrollBar().getValue()
							+ this.scroller.getVerticalScrollBar()
									.getVisibleAmount()) {
						this.scroller.getVerticalScrollBar().setValue(
								getSelectionYEnds().y
										- this.getFontMetrics(this.getFont())
												.getHeight());
					}
				}
				if (new Character('\n').equals(arg0.getKeyChar())) {
					int tabStore = this.doc.tabsForLineForPos(selectionEnd - 1);
					for (int i = 0; i < tabStore; i++) {
						this.replaceSelection("    ");
					}
				}
				if (!fake) {

				}
			}
		}
		if(!(arg0.getKeyCode() == KeyEvent.VK_UP||arg0.getKeyCode() == KeyEvent.VK_DOWN)){
			this.caretPosInLine=this.doc.whatPosInLineHasPos(this.selectionEnd);
		}

	}

	public void keyPressed(KeyEvent arg0) {
		int Tmask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		int Tkey = KeyEvent.CTRL_MASK == Tmask ? KeyEvent.VK_CONTROL : 157;
		keyPressedFlip(arg0, false);

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		coloring = syntax.parse(doc.doc);
		// TODO Auto-generated method stub
		this.repaint();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {

		// System.out.println(arg0.getKeyChar());
		this.repaint();
	}

	public class BlinkTimer implements ActionListener {
		private Timer cursorTimer;
		public int value = 0;
		private JComponent user;

		public BlinkTimer(int time, JComponent u) {
			cursorTimer = new Timer(time, this);
			cursorTimer.start();
			user = u;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			value = (value + 1) % 2;
			this.user.repaint();
		}

	}

	public class KeyTimer implements ActionListener {
		private Timer cursorTimer;
		public KeyEvent k;
		public int value = 0;
		private CodePane user;

		public KeyTimer(int time, CodePane u, KeyEvent ke) {
			k = ke;
			cursorTimer = new Timer(time, this);
			cursorTimer.setInitialDelay(100);
			cursorTimer.start();
			user = u;

		}

		public void setKeyEvent(KeyEvent ke) {
			cursorTimer.stop();
			k = ke;
			cursorTimer.setInitialDelay(100);
			cursorTimer.start();

		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			user.keyPressedFlip(k, true);
			this.user.repaint();
		}

	}
}