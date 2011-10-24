package experimentGUI.experimentEditor.tabbedPane.editorTabs.contentEditorToolBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import experimentGUI.util.Pair;

/**
 * Box for the ContentEditorToolBar, adding HTML tags to change font styles
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
@SuppressWarnings("serial")
public class FontStyleBox extends JComboBox implements ActionListener{

	private RSyntaxTextArea editArea;
	private ArrayList<Pair<String, String>> fontStyles;

	public FontStyleBox(RSyntaxTextArea editArea) {
		super();
		this.editArea = editArea;
		fontStyles = new ArrayList<Pair<String, String>>();
		fontStyles.add(new Pair<String, String>("Fett", "b"));
		fontStyles.add(new Pair<String, String>("Kursiv", "i"));
		fontStyles.add(new Pair<String, String>("Unterstrichen", "u"));

		this.addItem("Schrifttyp");
		for(int i=0; i<fontStyles.size(); i++) {
			this.addItem(fontStyles.get(i).getKey());
		}
		this.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		if (this.getSelectedIndex() != 0) {
			String tag = fontStyles.get(this.getSelectedIndex()-1).getValue();
			String text = editArea.getSelectedText();
			text = text==null ? "" : text;
			editArea.replaceSelection("<"+tag+">"+text+"</"+tag+">");
			this.setSelectedIndex(0);
		}
	}

}
