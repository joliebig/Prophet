package experimentGUI.plugins;

import java.awt.BorderLayout;

import experimentGUI.PluginInterface;
import experimentGUI.experimentEditor.tabbedPane.settingsEditorPanel.SettingsComponentDescription;
import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.plugins.questionListPlugin.QuestionList;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;


public class QuestionListPlugin implements PluginInterface {
	public final static String KEY = "questionlist";

	private QuestionList overview;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription(
			QuestionTreeNode node) {
		return null;
	}

	@Override
	public void experimentViewerRun(ExperimentViewer experimentViewer) {
		overview = new QuestionList(experimentViewer.getTree());
		//overview.setPreferredSize(new Dimension(150, 2));
		experimentViewer.add(overview, BorderLayout.WEST);
	}

	@Override
	public Object enterNode(QuestionTreeNode node) {
		overview.visit(node);
		return null;
	}

	@Override
	public void exitNode(QuestionTreeNode node, Object pluginData) {		
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String finishExperiment() {
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

}
