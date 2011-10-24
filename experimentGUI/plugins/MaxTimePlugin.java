package experimentGUI.plugins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JOptionPane;

import experimentGUI.PluginInterface;
import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
import experimentGUI.util.settingsComponents.SettingsPluginComponentDescription;
import experimentGUI.util.settingsComponents.components.SettingsCheckBox;
import experimentGUI.util.settingsComponents.components.SettingsTextField;

public class MaxTimePlugin implements PluginInterface {

	public class TimeOut implements Runnable {
		private long startTime;
		private long duration;

		private boolean started;
		private boolean stopped;

		private Thread myThread;

		private String message;
		private boolean disable;
		private boolean submit;
		private QuestionTreeNode node;

		private TimeOut(QuestionTreeNode node, long duration, String message, boolean disable, boolean submit) {
			this.node=node;
			this.duration=duration;
			this.message=message;
			this.disable=disable;
			this.submit=submit;

			stopped=false;
		}

		public boolean start() {
			if (!started && duration>0) {
				started=true;
				startTime = System.currentTimeMillis();
				myThread = new Thread(this);
				myThread.start();
				return true;
			}
			return false;
		}

		public boolean stop() {
			if (started && !stopped) {
				stopped=true;
				duration = Math.max(0, duration-(System.currentTimeMillis()-startTime));
				myThread.interrupt();
				if (duration==0) {
					action();
				}
				return true;
			}
			return false;
		}

		public boolean resume() {
			if (started && stopped && duration>0) {
				stopped=false;
				startTime = System.currentTimeMillis();
				myThread = new Thread(this);
				myThread.start();
				return true;
			}
			return false;
		}

		private void action() {
			boolean affected;
			if (node!=null) {
				affected = isTimeOutAffectedBy(currentNode,node);
			} else {
				affected=true;
			}

			if (message!=null && message.length()>0) {
				if (affected) {
					JOptionPane.showMessageDialog(experimentViewer, message);
				} else {
					allMessages.put(node, message);
				}
			}

			if (node!=null && disable) {
				timeOuts.add(node);
				if (submit && affected) {
					experimentViewer.forceNext(true);
				}
			}
		}

		@Override
		public void run() {
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				return;
			}
			stop();
		}
	}

	private static final String KEY = "max_time";
	private static final String KEY_MAX_TIME = "time";
	private static final String KEY_HARD_EXIT = "hard_exit";
	private static final String KEY_HARD_EXIT_WARNING = "show_warning";
	private static final String KEY_HARD_EXIT_WARNING_TIME = "warning_time";
	private static final String KEY_HARD_EXIT_WARNING_MESSAGE = "warning_message";
	private static final String KEY_IGNORE_TIMEOUT = "ignore_timeout";
	private static final String KEY_MESSAGE = "message";

	private ExperimentViewer experimentViewer;

	private HashMap<QuestionTreeNode,String> allMessages = new HashMap<QuestionTreeNode,String>();
	private HashMap<QuestionTreeNode,Vector<TimeOut>> allClocks = new HashMap<QuestionTreeNode,Vector<TimeOut>>();
	private HashSet<QuestionTreeNode> timeOuts = new HashSet<QuestionTreeNode>();

	private QuestionTreeNode experimentNode;
	private QuestionTreeNode currentNode;
	private boolean activateForExperiment;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription(QuestionTreeNode node) {
		SettingsPluginComponentDescription result = new SettingsPluginComponentDescription(KEY,
				"Timeout", true);
		if (node.isExperiment()) {
			result.addSubComponent(new SettingsComponentDescription(SettingsTextField.class, KEY_MAX_TIME,
					"Maximale Laufzeit (gesamtes Experiment, in Minuten):"));
		}
		if (node.isCategory()) {
			result.addSubComponent(new SettingsComponentDescription(SettingsTextField.class, KEY_MAX_TIME,
					"Maximale Laufzeit (gesamte Kategorie, in Sekunden):"));
		}
		if (node.isQuestion()) {
			result.addSubComponent(new SettingsComponentDescription(SettingsTextField.class, KEY_MAX_TIME,
					"Maximale Laufzeit (diese Frage, in Sekunden):"));
		}
		SettingsPluginComponentDescription hardExit = new SettingsPluginComponentDescription(KEY_HARD_EXIT,
				"Angezeigte Frage bei Zeitüberschreitung beenden (harter Timeout)", true);
		SettingsPluginComponentDescription warning = new SettingsPluginComponentDescription(KEY_HARD_EXIT_WARNING,
				"Probanden vorwarnen", true);
		warning.addSubComponent(new SettingsComponentDescription(SettingsTextField.class,KEY_HARD_EXIT_WARNING_TIME,
				"Vorwarnzeit (Sekunden):"));
		warning.addSubComponent(new SettingsComponentDescription(SettingsTextField.class,KEY_HARD_EXIT_WARNING_MESSAGE,
				"Nachricht:"));
		hardExit.addSubComponent(warning);
		result.addSubComponent(hardExit);
		result.addSubComponent(new SettingsComponentDescription(SettingsTextField.class,KEY_MESSAGE,
				"Nachricht bei TimeOut (optional):"));
		if (node.isCategory()) {
			result.addNextComponent(new SettingsComponentDescription(SettingsCheckBox.class,KEY_IGNORE_TIMEOUT,
					"Experiment-TimeOut ignorieren; gilt auch für alle Unterknoten"));
		}
		return result;
	}

	@Override
	public void experimentViewerRun(ExperimentViewer experimentViewer) {
		this.experimentViewer = experimentViewer;
	}

	private boolean isTimeOuted(QuestionTreeNode node) {
		while (node!=null) {
			if (timeOuts.contains(node)) {
				return true;
			}
			if (Boolean.parseBoolean(node.getAttributeValue(KEY_IGNORE_TIMEOUT))) {
				return false;
			}
			node=(QuestionTreeNode)node.getParent();
		}
		return false;
	}

	private boolean isTimeOutAffectedBy(QuestionTreeNode node, QuestionTreeNode timeOutNode) {
		while (node!=null) {
			if (node==timeOutNode) {
				return true;
			}
			if (Boolean.parseBoolean(node.getAttributeValue(KEY_IGNORE_TIMEOUT))) {
				System.out.println("<-- false (hero: "+node.getName()+")");
				return false;
			}
			node=(QuestionTreeNode)node.getParent();
		}
		return false;
	}

	@Override
	public boolean denyEnterNode(QuestionTreeNode node) {
		if (allMessages.size()>0) {
			Iterator<Entry<QuestionTreeNode,String>> it = allMessages.entrySet().iterator();
			while (it.hasNext()) {
				Entry<QuestionTreeNode,String> entry = it.next();
				if (isTimeOutAffectedBy(node,entry.getKey())) {
					JOptionPane.showMessageDialog(experimentViewer, entry.getValue());
					it.remove();
				}
			}
		}
		return isTimeOuted(node);
	}

	private void startTimers(QuestionTreeNode node) {
		Vector<TimeOut> nodeClocks = allClocks.get(node);
		if (nodeClocks==null) {
			boolean enabled = Boolean.parseBoolean(node.getAttributeValue(KEY));
			if (!enabled) {
				return;
			}
			QuestionTreeNode pluginNode = node.getAttribute(KEY);

			nodeClocks = new Vector<TimeOut>();
			allClocks.put(node, nodeClocks);

			long maxTime = Long.parseLong(pluginNode.getAttributeValue(KEY_MAX_TIME));
			if (node.isExperiment()) {
				maxTime*=60;
			}
			String message = pluginNode.getAttributeValue(KEY_MESSAGE);

			if (maxTime<=0) {
				return;
			}
			boolean hard = Boolean.parseBoolean(pluginNode.getAttributeValue(KEY_HARD_EXIT));
			TimeOut mainTimeOut = new TimeOut(node, maxTime*1000, message, true, hard);
			nodeClocks.add(mainTimeOut);
			mainTimeOut.start();

			if (!hard) {
				return;
			}
			QuestionTreeNode hardNode = pluginNode.getAttribute(KEY_HARD_EXIT);
			boolean hardWarning = Boolean.parseBoolean(hardNode.getAttributeValue(KEY_HARD_EXIT_WARNING));

			if (!hardWarning) {
				return;
			}

			QuestionTreeNode hardWarningNode = hardNode.getAttribute(KEY_HARD_EXIT_WARNING);

			String hardWarningTimeString = hardWarningNode.getAttributeValue(KEY_HARD_EXIT_WARNING_TIME);
			String hardWarningMessage = hardWarningNode.getAttributeValue(KEY_HARD_EXIT_WARNING_MESSAGE);

			if (hardWarningTimeString==null || hardWarningTimeString.length()>0 ||
					hardWarningMessage==null || hardWarningMessage.length()>0) {
				return;
			}
			long hardWarningTime = maxTime-Long.parseLong(hardWarningTimeString);

			if (hardWarningTime<=0) {
				return;
			}

			TimeOut warningTimeOut = new TimeOut(node, hardWarningTime*1000, hardWarningMessage, false, false);
			nodeClocks.add(warningTimeOut);
			warningTimeOut.start();
		} else {
			for (TimeOut clock : nodeClocks) {
				clock.resume();
			}
		}
	}

	@Override
	public void enterNode(QuestionTreeNode node) {
		currentNode = node;
		if (node.isExperiment()) {
			experimentNode = node;
			activateForExperiment = true;
			return;
		}
		if (activateForExperiment) {
			startTimers(experimentNode);
			activateForExperiment=false;
		}
		startTimers(node);
	}

	@Override
	public String denyNextNode(QuestionTreeNode currentNode) {
		return null;
	}

	@Override
	public void exitNode(QuestionTreeNode node) {
		Vector<TimeOut> nodeClocks = allClocks.get(node);
		if (nodeClocks!=null) {
			for (TimeOut clock : nodeClocks) {
				clock.stop();
			}
		}
	}

	@Override
	public String finishExperiment() {
		return null;
	}
}
