package experimentGUI.experimentEditor.tabbedPane.editorTabs;

import java.awt.BorderLayout;

import experimentGUI.experimentEditor.tabbedPane.ExperimentEditorTab;
import experimentGUI.util.QuestionViewPane;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;

/**
 * Preview of the Content entered in the ContentEditorPanel
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
@SuppressWarnings("serial")
public class ContentViewerPanel extends ExperimentEditorTab {
	/**
	 * the pane featuring the preview
	 */
	private QuestionViewPane viewerPane;

	/**
	 * Constructor
	 */
	public ContentViewerPanel() {
		setLayout(new BorderLayout());
		this.setOpaque(false);
	}

	/**
	 * loads the current content and displays it appropriately, called by EditorTabbedPane
	 */
	public void activate(QuestionTreeNode selected) {
		removeAll();
		updateUI();
		if (selected!=null) {
			viewerPane = new QuestionViewPane(selected);
			add(viewerPane,BorderLayout.CENTER);
		}
	}

	/**
	 * saves any changes to the tree, i.e. does nothing, called by EditorTabbedPane
	 */
	@Override
	public void save() {
	}
}
