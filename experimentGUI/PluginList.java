package experimentGUI;

import java.util.Vector;

import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.plugins.AnswerRequiredPlugin;
import experimentGUI.plugins.CodeViewerPlugin;
import experimentGUI.plugins.ExternalProgramsPlugin;
import experimentGUI.plugins.InactivityPlugin;
import experimentGUI.plugins.MailPlugin;
import experimentGUI.plugins.MaxTimePlugin;
import experimentGUI.plugins.PHPExportPlugin;
import experimentGUI.plugins.QuestionListPlugin;
import experimentGUI.plugins.ValidSubjectCodePlugin;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;

/**
 * Class to store active plugins, allows calls to all plugins within one line of code.
 *
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
public class PluginList {
	/**
	 * the order of the plugins in this vector assigns the order of the calls
	 */
	private static Vector<PluginInterface> plugins = new Vector<PluginInterface>() {
		private static final long serialVersionUID = 1L;
		{
			add(new InactivityPlugin());
			add(new ValidSubjectCodePlugin());
			add(new CodeViewerPlugin());
			add(new QuestionListPlugin());
			add(new MailPlugin());
			add(new MaxTimePlugin());
			add(new ExternalProgramsPlugin());
			add(new AnswerRequiredPlugin());
//			add(new KeyPressedPlugin());
			add(new PHPExportPlugin());
		}
	};
	/**
	 *
	 * @return
	 * 	Vector describing all active plugins
	 */
	public static Vector<PluginInterface> getPlugins() {
		return plugins;
	}

	/**
	 * Adds and activates a plugin
	 * @param plugin
	 * 	Plugin to be added and activated
	 */
	public static void add(PluginInterface plugin) {
		plugins.add(plugin);
	}

	/**
	 * Removes and deactives a plugin
	 * @param plugin
	 * 	Plugin to be deactivated
	 * @return
	 * 	<b>true</b> if a plugin is removed<br>
	 * 	<b>false</b> if it wasn't active
	 */
	public static boolean remove(PluginInterface plugin) {
		return plugins.remove(plugin);
	}

	/**
	 * Returns the settings shown in the settings tab of the experiment viewer for all plugins at once.
	 * @param node
	 *  Node to be get the settings for
	 * @return
	 *  settings shown in the settings tab of the experiment viewer for all plugins at once
	 */
	public static SettingsComponentDescription getSettingsComponentDescription(
			QuestionTreeNode node) {
		SettingsComponentDescription result = null;
		for (PluginInterface plugin : plugins) {
			try {
				SettingsComponentDescription desc = plugin.getSettingsComponentDescription(node);
				if (desc!=null) {
					if (result==null) {
						result=desc;
					} else {
						result.addNextComponent(desc);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Calls the experimentViewerRun() function of all plugins at once
	 * @param experimentViewer
	 *  ExperimentViewer that has been started
	 */
	public static void experimentViewerRun(ExperimentViewer experimentViewer) {
		for (PluginInterface plugin : plugins) {
			try {
				plugin.experimentViewerRun(experimentViewer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Calls the denyEnterNode() function for all plugins at once
	 * @param node
	 *  The node to be entered
	 * @return
	 * 	<b>true</b> if the node may be entered<br/>
	 *  <b>false</b> if any plugin denies the entrance
	 */
	public static boolean denyEnterNode(QuestionTreeNode node) {
		for (PluginInterface plugin : plugins) {
			try {
				if (plugin.denyEnterNode(node)) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Calls the enterNode() function for all plugins at once
	 * @param node
	 *  the node to be entered
	 */
	public static void enterNode(QuestionTreeNode node) {
		for (PluginInterface plugin : plugins) {
			try {
				plugin.enterNode(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Calls the denyExitNode() for all plugins at once.
	 * @param currentNode
	 *  the node to be exited
	 * @return
	 *  The resulting String from the first plugin denying exiting the node,
	 *  an empty String if a plugin returned an empty String and no real messages were returned,
	 *  null if all plugins allow exiting the node
	 */
	public static String denyNextNode(QuestionTreeNode currentNode) {
		String result = null;
		for (PluginInterface plugin : plugins) {
			try {
				String ret = plugin.denyNextNode(currentNode);
				if (ret!=null) {
					result="";
					if (ret.length()>0) {
						return ret;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Calls the exitNode() function for all plugins at once
	 * @param node
	 *  the node to be exited
	 */
	public static void exitNode(QuestionTreeNode node) {
		for (PluginInterface plugin : plugins) {
			try {
				plugin.exitNode(node);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Calls the finishExperiment() function for all plugins at once
	 * @return
	 *  The finishing messages of all plugins in HTML coding, with an own p-tag for every plugin.
	 */
	public static String finishExperiment() {
		String result = "";
		for (PluginInterface plugin : plugins) {
			try {
				String ret = plugin.finishExperiment();
				if (ret!=null && ret.length()>0) {
					result+="<p>"+ret+"</p>";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
