package experimentGUI.experimentEditor.tabbedPane;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import experimentGUI.experimentEditor.tabbedPane.editorTabs.ContentEditorPanel;
import experimentGUI.experimentEditor.tabbedPane.editorTabs.ContentViewerPanel;
import experimentGUI.experimentEditor.tabbedPane.editorTabs.NoteEditorPanel;
import experimentGUI.experimentEditor.tabbedPane.editorTabs.SettingsEditorPanel;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;

/**
 * The right-side tabbed pane of the Experiment Editor
 * @author Andreas Hasselberg
 * @author Markus Köppen
 */
@SuppressWarnings("serial")
public class ExperimentEditorTabbedPane extends JTabbedPane {
	/**
	 * the currently selected QuestionTreeNode
	 */
	private QuestionTreeNode selected;
	/**
	 * the currently open ExperimentEditorTab
	 */
	private ExperimentEditorTab currentTab;

	/**
	 * Constructor
	 */
	public ExperimentEditorTabbedPane() {
		addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (currentTab!=null) {
					save();
				}
				activate();
				currentTab = (ExperimentEditorTab)getSelectedComponent();
			}
		});

		addEditorPanel("Editor", new ContentEditorPanel());
		addEditorPanel("Vorschau", new ContentViewerPanel());
		addEditorPanel("Einstellungen", new SettingsEditorPanel());
		addEditorPanel("Notizen", new NoteEditorPanel());
	}

	/**
	 * Adds a new ExperimentEditorTab the the TabbedPane
	 * @param caption
	 * 	caption of the new tab
	 * @param panel
	 *  the tab to be added
	 */
	public void addEditorPanel(String caption, ExperimentEditorTab panel) {
		addTab(caption, null, panel, null);
	}
	/**
	 * Called upon change in the currently selected QuestionTreeNode. Saves the current tab and then loads the new node.
	 * @param selected
	 */
	public void setSelected(QuestionTreeNode selected) {
		save();
		this.selected=selected;
		activate();
	}

	/**
	 * Informs the currently active editor tab that a new node might have been opened (reload).
	 */
	private void activate() {
		((ExperimentEditorTab)getSelectedComponent()).activate(selected);
	}
	/**
	 * saves the current tab
	 */
	public void save() {
		currentTab.save();
	}
	/**
	 * Makes the given component and all its descendants transparent (looks better)
	 * @param component
	 *  the component to be made transparent
	 */
	public static void recursiveSetOpaque(JComponent component) {
		component.setOpaque(false);
		for (Component child : component.getComponents()) {
			if (child instanceof JComponent) {
				recursiveSetOpaque((JComponent)child);
			}
		}
	}
}
