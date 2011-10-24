package experimentGUI.experimentEditor.tabbedPane.editorTabs;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import experimentGUI.experimentEditor.tabbedPane.ExperimentEditorTab;
import experimentGUI.util.ModifiedRSyntaxTextArea;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
/**
 * A panel for taking notes for every node in the tree.
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
@SuppressWarnings("serial")
public class NoteEditorPanel extends ExperimentEditorTab {
	private HashMap<QuestionTreeNode,RTextScrollPane> scrollPanes;
	private HashMap<QuestionTreeNode, RSyntaxTextArea> editAreas = new HashMap<QuestionTreeNode,RSyntaxTextArea>();
	private QuestionTreeNode selected;
	public final static String KEY_NOTES = "notes";

	/**
	 * Constructor
	 */
	public NoteEditorPanel() {
		setLayout(new BorderLayout());
		this.setOpaque(false);
		scrollPanes = new HashMap<QuestionTreeNode,RTextScrollPane>();
	}
	/**
	 * loads the notes for a selected node into the tab, called by EditorTabbedPane
	 */
	public void activate(final QuestionTreeNode s) {
		selected=s;
		this.removeAll();
		this.updateUI();
		if (s!=null) {
			RTextScrollPane scrollPane = scrollPanes.get(selected);
			if (scrollPane==null) {
				RSyntaxDocument doc = new RSyntaxDocument(SyntaxConstants.SYNTAX_STYLE_NONE);
				try {
					doc.insertString(0, s.getAddAttribute(KEY_NOTES).getValue(), null);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				RSyntaxTextArea editArea = new ModifiedRSyntaxTextArea(doc);
				editAreas.put(s, editArea);
				editArea.setLineWrap(true);

				scrollPane = new RTextScrollPane(editArea);
				scrollPanes.put(s, scrollPane);
			}
			add(scrollPane, BorderLayout.CENTER);
		}
	}

	/**
	 * saves any changes, called by EditorTabbedPane
	 */
	@Override
	public void save() {
		if (selected!=null) {
			RSyntaxTextArea editArea = editAreas.get(selected);
			if (editArea!=null) {
				QuestionTreeNode node = selected.getAddAttribute(KEY_NOTES);
				node.setValue(editArea.getText());
			}
		}
	}
}
