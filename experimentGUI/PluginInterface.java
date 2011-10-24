package experimentGUI;

import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
/**
 * Interface for all plugins to be used immediately within the product. It represents methods called on special occasions.
 *
 * @author Andreas Hasselberg
 * @author Markus Köppen
 *
 */
public interface PluginInterface {
	/**
	 * Delivers settings components shown in the settings tab of the experiment editor
	 * @param node
	 * 	The currently active node within the experiment viewer
	 * @return
	 * 	A SettingsComponentDescription that describes the possible settings of the plugin or null if there are none
	 */
	public SettingsComponentDescription getSettingsComponentDescription(QuestionTreeNode node);

	/**
	 * Called once when the experiment viewer is initialized. May be used to manipulate the viewer.
	 * @param experimentViewer
	 * 	The initialized experiment viewer
	 */
	public void experimentViewerRun(ExperimentViewer experimentViewer);

	/**
	 * Called right before a new node is entered. If any plugin denies the currentNode to be entered (e.g. because of a timeout), it will just be skipped.
	 * @param curentNode
	 * 	The node to be entered
	 * @return
	 * 	true if the node shall not be visited<br>
	 * 	false if it might be visited
	 */
	public boolean denyEnterNode(QuestionTreeNode node);

	/**
	 * Called when a node is entered, at the beginning of categories or questions. Is not called if a plugin denied the entrance of that node.
	 * @param node
	 * 	The node entered
	 */
	public void enterNode(QuestionTreeNode node);

	/**
	 * Called right before a new node is opened in the ExperimentViewer. If any plugin denies the currentNode to be finished (for example because needed answers are missing) the next Node won't be opened.
	 * @param curentNode
	 * 	The node to be finished
	 * @return
	 * 	A message shown to the subject to indicate what needs to be done to accept finishing this node (e.g. enter a needed answer)
	 */
	public String denyNextNode(QuestionTreeNode currentNode);

	/**
	 * Called when a node is exited, i.e. after a question, after all active questions of a category are exited, when the Experiment is finished (but before finishExperiment()). Is not called if node was not entered previously.
	 * @param node
	 * 	The node to be exited
	 */
	public void exitNode(QuestionTreeNode node);

	/**
	 * Called after all nodes have been visited. Allows the plugin to do last steps upon finishing the experiment
	 * @return
	 * 	A message shown to the subject at experiment's end
	 */
	public String finishExperiment();
}
