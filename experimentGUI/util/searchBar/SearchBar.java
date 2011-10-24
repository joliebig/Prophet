package experimentGUI.util.searchBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * This class adds a JTextPane to a searchbar which is created. With this
 * searchBar the user can search through the text in the JTextPane User
 *
 * @author Robert Futrell, Markus Köppen, Andreas Hasselberg
 */

public class SearchBar extends JToolBar implements ActionListener {
	private static final long serialVersionUID = 1L;

	public static final String CAPTION_HIDE = "X";
	public static final String CAPTION_NEXT = "Abwärts";
	public static final String CAPTION_PREVIOUS = "Aufwärts";
	public static final String CAPTION_REGEX = "Regex";
	public static final String CAPTION_MATCH_CASE = "Groß-/Kleinschreibung";

	public static final String ACTION_HIDE = "Hide";
	public static final String ACTION_NEXT = "FindNext";
	public static final String ACTION_PREVIOUS = "FindPrevious";

	public static final String MESSAGE_NOT_FOUND = "Text nicht gefunden.";

	private RSyntaxTextArea textArea;

	private JButton hideButton = new JButton(CAPTION_HIDE);
	private JTextField searchField = new JTextField(30);
	private JButton forwardButton = new JButton(CAPTION_NEXT);
	private JButton backwardButton = new JButton(CAPTION_PREVIOUS);
	private JCheckBox regexCB = new JCheckBox(CAPTION_REGEX);
	private JCheckBox matchCaseCB = new JCheckBox(CAPTION_MATCH_CASE);

	private Vector<SearchBarListener> listeners = new Vector<SearchBarListener>();

	public void addSearchBarListener(SearchBarListener l) {
		listeners.add(l);
	}

	public void removeSearchBarListener(SearchBarListener l) {
		listeners.remove(l);
	}

	/**
	 * Grabs the focus
	 */
	public void grabFocus() {
		searchField.grabFocus();
	}

	public SearchBar(RSyntaxTextArea textArea) {
		this.textArea=textArea;
		this.setFloatable(false);
		// Create a toolbar with searching options.
		hideButton.setActionCommand(ACTION_HIDE);
		hideButton.addActionListener(this);
		add(hideButton);
		searchField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					forwardButton.doClick();
				}
			}
		});
		add(searchField);
		forwardButton.setActionCommand(ACTION_NEXT);
		forwardButton.addActionListener(this);
		add(forwardButton);
		backwardButton.setActionCommand(ACTION_PREVIOUS);
		backwardButton.addActionListener(this);
		add(backwardButton);
		add(regexCB);
		add(matchCaseCB);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.equals(ACTION_HIDE)) {
			setVisible(false);
			return;
		}

		String text = searchField.getText();
		if (text.length() == 0) {
			return;
		}

		boolean matchCase = matchCaseCB.isSelected();
		boolean wholeWord = false;
		boolean regex = regexCB.isSelected();

		boolean forward = false;

		if (command.equals(ACTION_NEXT)) {
			forward = true;
		} else if (command.equals(ACTION_PREVIOUS)) {
			forward = false;
		} else {
			return;
		}

		boolean found = SearchEngine.find(textArea, text, forward,
				matchCase, wholeWord, regex);
		if (!found) {
			JOptionPane.showMessageDialog(this, MESSAGE_NOT_FOUND);

			for (SearchBarListener l : listeners) {
				l.searched(command, text, false);
			}
		} else {
			for (SearchBarListener l : listeners) {
				l.searched(command, text, true);
			}
		}
	}

	public RSyntaxTextArea getTextArea() {
		return textArea;
	}

	public JButton getHideButton() {
		return hideButton;
	}

	public JTextField getSearchField() {
		return searchField;
	}

	public JButton getForwardButton() {
		return forwardButton;
	}

	public JButton getBackwardButton() {
		return backwardButton;
	}

	public JCheckBox getRegexCB() {
		return regexCB;
	}

	public JCheckBox getMatchCaseCB() {
		return matchCaseCB;
	}


}
