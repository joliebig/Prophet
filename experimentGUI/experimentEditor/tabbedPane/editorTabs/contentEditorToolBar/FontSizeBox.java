package experimentGUI.experimentEditor.tabbedPane.editorTabs.contentEditorToolBar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import experimentGUI.util.Pair;

/**
 * Box for the ContentEditorToolBar, adding HTML tags to change font sizes
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
@SuppressWarnings("serial")
public class FontSizeBox extends JComboBox implements ActionListener{

	private RSyntaxTextArea editArea;
	private ArrayList<Pair<String, String>> fontSizes;
	/**
	 * Constructor
	 * @param editArea
	 *  The editor area it is working with
	 */
	public FontSizeBox(RSyntaxTextArea editArea) {
		super();
		this.editArea = editArea;
		fontSizes = new ArrayList<Pair<String, String>>();
		fontSizes.add(new Pair<String, String>("-3", "-3"));
		fontSizes.add(new Pair<String, String>("-2", "-2"));
		fontSizes.add(new Pair<String, String>("-1", "-1"));
		fontSizes.add(new Pair<String, String>("+1", "+1"));
		fontSizes.add(new Pair<String, String>("+2", "+2"));
		fontSizes.add(new Pair<String, String>("+3", "+3"));

		this.addItem("Schriftgröße");
		for(int i=0; i<fontSizes.size(); i++) {
			this.addItem(fontSizes.get(i).getKey());
		}
		this.addActionListener(this);
	}
	/**
	 * triggered if the box is changed
	 */
	public void actionPerformed(ActionEvent ae) {
		if (this.getSelectedIndex() != 0) {
			String size = fontSizes.get(this.getSelectedIndex()-1).getValue();
			String text = editArea.getSelectedText();
			text = text==null ? "" : text;
			editArea.replaceSelection("<font size=\""+size+"\">"+text+"</font>");
			this.setSelectedIndex(0);
		}
	}

}