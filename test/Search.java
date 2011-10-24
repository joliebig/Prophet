package test;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Search extends JPanel implements KeyListener, ActionListener,
		MouseListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String text;
	private String word;
	JTextArea textarea = null;
	private Vector<Integer> v; // Vector f�r die Positionen der Vorkommen
	private int lastPos; // Position des aktuellen Vorkommen

	final Color HILIT_COLOR_GRAY = Color.LIGHT_GRAY;
	final Color HILIT_COLOR_YELLOW = Color.YELLOW;

	Highlighter hilit;
	Highlighter.HighlightPainter painterGray;
	Highlighter.HighlightPainter painterYellow;

	private JTextField search_textfield_word;
	private JButton search_btn_down;
	private JButton search_btn_up;
	private JLabel search_lbl_close;
	private JLabel search_lbl_text;
	private JCheckBox search_chkbx_upperlower;

	/*
	 * Konstruktoren für mehrere Komponenten (ausser Textarea) müglich - nur
	 * extra erstellen und in Methoden dann überprüfen
	 */
	public Search(JTextArea textarea) {
		super();
		this.textarea = textarea;
		this.word = "";
		lastPos = -1;
		v = new Vector<Integer>();

		search_lbl_close = new JLabel("x");
		JLabel search_lbl_suchen = new JLabel("Suchen:");
		search_textfield_word = new JTextField();
		search_textfield_word.setColumns(10);
		search_btn_down = new JButton("Abwärts");
		search_btn_up = new JButton("Aufwärts");
		search_lbl_text = new JLabel("");
		search_chkbx_upperlower = new JCheckBox("Groß-/Kleinschreibung");

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(search_lbl_close)
						.addGap(18)
						.addComponent(search_lbl_suchen)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(search_textfield_word,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(search_btn_down)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(search_btn_up).addGap(6)
						.addComponent(search_chkbx_upperlower).addGap(6)
						.addComponent(search_lbl_text)
						.addContainerGap(55, Short.MAX_VALUE)));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				search_lbl_close))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(7)
																		.addComponent(
																				search_btn_down))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(7)
																		.addComponent(
																				search_btn_up))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				search_chkbx_upperlower))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				search_lbl_text))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(8)
																		.addComponent(
																				search_textfield_word,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				search_lbl_suchen)))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		setLayout(groupLayout);

		search_textfield_word.addKeyListener(this);
		search_btn_up.addActionListener(this);
		search_btn_down.addActionListener(this);
		search_chkbx_upperlower.addActionListener(this);
		search_lbl_close.addMouseListener(this);

		hilit = new DefaultHighlighter();
		painterGray = new DefaultHighlighter.DefaultHighlightPainter(
				HILIT_COLOR_GRAY);
		painterYellow = new DefaultHighlighter.DefaultHighlightPainter(
				HILIT_COLOR_YELLOW);
		textarea.setHighlighter(hilit);
	}

	/*
	 * KeyEvents für das Suchfeld
	 */
	@Override
	public void keyReleased(KeyEvent ke) {
		// wenn enter oder ein alphanumerisches Zeichen gedrückt wurde Suche neu
		// starten - klappt nicht
		// if ((ke.getKeyCode() == KeyEvent.VK_ALPHANUMERIC)||(ke.getKeyCode()
		// == KeyEvent.VK_ENTER)) {
		if (!(ke.getKeyCode() == KeyEvent.VK_ENTER)) {
			cancelSearch();
		}
		if (setWord(search_textfield_word.getText())) {
			getPos(true);
		}
	}

	/*
	 * ActionEvents für die Suchbuttons und der Checkbox
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		// Vorwärtssuche auslösen
		if (ae.getSource() == search_btn_down) {
			getPos(true);
		}
		// Rückwärtssuche auslösen
		if (ae.getSource() == search_btn_up) {
			getPos(false);
		}
		// Checkbox
		if (ae.getSource() == search_chkbx_upperlower) {
			cancelSearch();
			setWord(search_textfield_word.getText());
			getPos(true);
		}
	}

	/*
	 * MouseEvent für das schließen Label
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		invisible();
	}

	/*
	 * Allgemeine Suchmethode Baut einen Vector auf, der als Index der
	 * gefundenen Werte dient. Alle Ergebnisse werden grau Hinterlegt
	 * eingef�rbt, das aktuelle gelb. LastSearchPosition gibt dabei anfangs das
	 * letzte aktuelle Ergebnis an und muss zum Ende aktualisiert werden, damit
	 * es das aktuelle angibt
	 *
	 * @param mod boolean der angibt ob vorw�rts (true) oder r�ckw�rts (false)
	 * gesucht werden soll
	 *
	 * @return true wenn was gefunden, sonst false
	 */
	public boolean getPos(boolean forward) {
		// liste aller vorkommen aufbauen (nur wenn n�tig)
		if (word.equals("")) {
			cancelSearch();
			return false;
		}
		text = search_chkbx_upperlower.isSelected() ? textarea.getText()
				: textarea.getText().toLowerCase();
		word = search_chkbx_upperlower.isSelected() ? word : word.toLowerCase();
		if (lastPos == -1) {
			v.removeAllElements();
			// Position in Abh�ngigkeit ob mit Unterscheidung der Gro�-
			// Kleinschreibung finden
			int pos = text.indexOf(word);
			int i = 0;
			while (pos != -1) {
				i++;
				v.add(pos);
				pos = text.indexOf(word, pos + 1);
			}
		}
		if (!v.isEmpty()) {
			// Vorw�rtseinstellung
			if (forward) {
				if (lastPos >= v.size() - 1) {
					search_lbl_text
							.setText("Textende erreicht. Die Suche wurde vom Textanfang neu gestartet.");
				} else {
					search_lbl_text.setText("");
				}
				lastPos = (lastPos >= v.size() - 1) ? 0 : lastPos + 1;
			}
			// R�ckw�rtseinstellung
			if (!forward) {
				if (lastPos <= 0) {
					search_lbl_text
							.setText("Textanfang erreicht. Die Suche wurde vom Textende neu gestartet.");
				} else {
					search_lbl_text.setText("");
				}
				lastPos = lastPos <= 0 ? v.size() - 1 : lastPos - 1;
			}
			// Ergebnisse einf�rben und Cursor setzen
			showHighlights();
			search_textfield_word.setForeground(Color.BLACK);
			search_textfield_word.setBackground(Color.WHITE);
			textarea.setCaretPosition(v.get(lastPos));
			return true;
		}
		search_textfield_word.setForeground(Color.WHITE);
		search_textfield_word.setBackground(Color.RED);
		return false;
	}

	/*
	 * L�sst alle Suchergebnissfarbhinterlegungen im Farbschema anzeigen
	 */
	public void showHighlights() {
		hilit.removeAllHighlights();
		for (int j = 0; j < v.size(); j++) {
			try {
				if (j == lastPos) {
					hilit.addHighlight(v.get(j), v.get(j) + word.length(),
							painterYellow);
				} else {
					hilit.addHighlight(v.get(j), v.get(j) + word.length(),
							painterGray);
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * L�sst alle Suchergebnissfarbhinterlegungen verschwinden
	 */
	public void hideHighlights() {
		hilit.removeAllHighlights();
	}

	/*
	 * Setzt das derzeitige Suchwort
	 *
	 * @param word Das Wort was nun gesucht werden soll
	 *
	 * @return true wenn es ein echtes Wort ist, false wenn es ein Leerstring
	 * ist
	 */
	public boolean setWord(String word) {
		this.word = word;
		return word.equals("") ? false : true;
	}

	/*
	 * F�hrt alle n�tigen Vorg�nge aus um eine Suche abzubrechen
	 */
	public void cancelSearch() {
		search_textfield_word.setForeground(Color.BLACK);
		search_textfield_word.setBackground(Color.WHITE);
		hideHighlights();
		setWord("");
		lastPos = -1;
	}

	/*
	 * Zeigt diese Klasse optisch an
	 */
	public void visible() {
		showHighlights();
		this.setVisible(true);
	}

	/*
	 * L�sst diese Klasse optisch verschwinden
	 */
	public void invisible() {
		hideHighlights();
		this.setVisible(false);
	}

	/*
	 * ungenutze Funktionen
	 */
	@Override
	public void keyPressed(KeyEvent ke) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
