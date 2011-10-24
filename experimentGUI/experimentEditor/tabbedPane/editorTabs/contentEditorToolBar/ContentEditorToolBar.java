package experimentGUI.experimentEditor.tabbedPane.editorTabs.contentEditorToolBar;

import java.awt.Component;

import javax.swing.JToolBar;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * Toolbar for the ContentEditorPanel
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
@SuppressWarnings("serial")
public class ContentEditorToolBar extends JToolBar {
	/**
	 * The editor area it is working with
	 */
	private RSyntaxTextArea editArea;

	/**
	 * Constructor
	 * @param ea
	 * 	Editor Area to work with
	 */
	public ContentEditorToolBar(RSyntaxTextArea ea) {
		editArea = ea;
		setFloatable(false);

		add(new FontStyleBox(editArea));
		add(new FontSizeBox(editArea));
		add(new FormularBox(editArea));
		add(new MacroBox(editArea));
//		add(new MultipleNameTestButton(editArea, "Namenstest"));
	}
	/**
	 * enable and disable the toolbar
	 */
	public void setEnabled(boolean enabled) {
		for (Component c : this.getComponents()) {
			c.setEnabled(enabled);
		}
	}
}
