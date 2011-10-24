package experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins;

import experimentGUI.plugins.codeViewerPlugin.CodeViewer;
import experimentGUI.plugins.codeViewerPlugin.CodeViewerPluginInterface;
import experimentGUI.plugins.codeViewerPlugin.tabbedPane.EditorPanel;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
import experimentGUI.util.settingsComponents.components.SettingsCheckBox;

public class LineNumbersPlugin implements CodeViewerPluginInterface {
	public static final String KEY = "linenumbers";
	private QuestionTreeNode selected;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription() {
		SettingsComponentDescription result = new SettingsComponentDescription(SettingsCheckBox.class,"linenumbers_default", "Zeilennummern anzeigen");
		//result.addNextComponent(new SettingsComponentDescription(SettingsCheckBox.class,"linenumbers_toggle", "Zeilennummern ein- und ausschaltbar"));
		return result;
	}

	@Override
	public void init(QuestionTreeNode selected) {
		this.selected=selected;
	}

	@Override
	public void onFrameCreate(CodeViewer viewer) {
	}

	@Override
	public void onEditorPanelCreate(EditorPanel editorPanel) {
		boolean lineNumbers = Boolean.parseBoolean(selected.getAttributeValue("linenumbers_default"));
		editorPanel.getScrollPane().setLineNumbersEnabled(lineNumbers);
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onEditorPanelClose(EditorPanel editorPanel) {
		// TODO Auto-generated method stub

	}
}
