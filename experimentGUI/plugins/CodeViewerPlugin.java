package experimentGUI.plugins;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFrame;

import experimentGUI.PluginInterface;
import experimentGUI.experimentEditor.tabbedPane.settingsEditorPanel.SettingsComponentDescription;
import experimentGUI.experimentEditor.tabbedPane.settingsEditorPanel.SettingsPluginComponentDescription;
import experimentGUI.experimentEditor.tabbedPane.settingsEditorPanel.settingsComponents.SettingsCheckBox;
import experimentGUI.experimentEditor.tabbedPane.settingsEditorPanel.settingsComponents.SettingsDirectoryPathChooser;
import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.plugins.codeViewerPlugin.CodeViewer;
import experimentGUI.plugins.codeViewerPlugin.CodeViewerPluginInterface;
import experimentGUI.plugins.codeViewerPlugin.CodeViewerPluginList;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;

public class CodeViewerPlugin implements PluginInterface {
	public final static String KEY = "codeviewer";
	
	ExperimentViewer experimentViewer;
	int count = 1;

	private Rectangle bounds;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription(
			QuestionTreeNode node) {
		if (node.getType().equals(QuestionTreeNode.TYPE_CATEGORY)) {
			SettingsPluginComponentDescription result = new SettingsPluginComponentDescription(KEY, "Codeviewer aktivieren");
			result.addSubComponent(new SettingsComponentDescription(SettingsDirectoryPathChooser.class, CodeViewer.KEY_PATH, "Pfad der Quelltexte:"));
			result.addSubComponent(new SettingsComponentDescription(SettingsCheckBox.class, CodeViewer.KEY_EDITABLE, "Quelltext editierbar"));
			for (CodeViewerPluginInterface plugin : CodeViewerPluginList.getPlugins()) {
				SettingsComponentDescription desc = plugin.getSettingsComponentDescription();
				if (desc!=null) {
					result.addSubComponent(desc);
					while ((desc = desc.getNextComponentDescription()) != null) {
						result.addSubComponent(desc);
					}
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public void experimentViewerRun(ExperimentViewer experimentViewer) {
		this.experimentViewer=experimentViewer;
	}

	@Override
	public Object enterNode(QuestionTreeNode node) {	
		boolean enabled = Boolean.parseBoolean(node.getAttributeValue(KEY));
		if (enabled) {
			String savePath = experimentViewer.getSaveDir().getPath()+System.getProperty("file.separator")+(count++)+"_"+node.getName()+"_codeviewer";		
			CodeViewer cv = new CodeViewer(node.getAttribute(KEY), new File(savePath));
			if (bounds==null) {
				Point location = experimentViewer.getLocation();
				cv.setLocation(new Point(location.x+20, location.y+20));
			} else {
				cv.setBounds(bounds);
			}
			cv.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			cv.setVisible(true);
			for (CodeViewerPluginInterface plugin : CodeViewerPluginList.getPlugins()) {
				plugin.onFrameCreate(node.getAttribute(KEY), cv);
			}
			return cv;
		}
		return null;
	}

	@Override
	public void exitNode(QuestionTreeNode node, Object pluginData) {
		CodeViewer cv = (CodeViewer)pluginData;
		if (cv!=null) {
			for (CodeViewerPluginInterface plugin : CodeViewerPluginList.getPlugins()) {
				plugin.onClose();
			}
			bounds = cv.getBounds();
			cv.dispose();
			cv=null;
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}

	@Override
	public String finishExperiment() {
		return null;
	}
}
