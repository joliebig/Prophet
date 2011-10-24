package experimentGUI.experimentEditor.tabbedPane;

import javax.swing.JPanel;

import experimentGUI.util.questionTreeNode.QuestionTreeNode;

/**
 * Abstract class representing an ExperimentEditorTab used by the ExperimentEditorTabbedPane
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
@SuppressWarnings("serial")
public abstract class ExperimentEditorTab extends JPanel {
	/**
	 * Informs the Tab that a new node might have been selected (reload)
	 * @param sel
	 *  the node that has been opened
	 */
	public abstract void activate(QuestionTreeNode sel);
	/**
	 * advises the tab to save all changes (e.g. before a file save, upon loading a new node, on tab change)
	 */
	public abstract void save();
}
