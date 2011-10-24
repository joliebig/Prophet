package experimentGUI.plugins.codeViewerPlugin.recorder.recorderPlugins;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import experimentGUI.plugins.codeViewerPlugin.CodeViewer;
import experimentGUI.plugins.codeViewerPlugin.recorder.RecorderPluginInterface;
import experimentGUI.plugins.codeViewerPlugin.recorder.loggingTreeNode.LoggingTreeNode;
import experimentGUI.plugins.codeViewerPlugin.tabbedPane.EditorPanel;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
import experimentGUI.util.settingsComponents.SettingsPluginComponentDescription;
import experimentGUI.util.settingsComponents.components.SettingsTextField;

public class ScrollingPlugin implements RecorderPluginInterface {
	public final static String KEY = "scrolling";
	public final static String KEY_JOIN = "join";
	public final static String KEY_JOIN_TIME = "jointime";

	public final static String TYPE_SCROLLING = "scrolling";
	public final static String ATTRIBUTE_STARTLINE = "startline";
	public final static String ATTRIBUTE_ENDLINE = "endline";

	private boolean enabled;
	private boolean join;
	private long joinTime;

	private LoggingTreeNode currentNode;
	private JViewport currentViewPort;
	private RSyntaxTextArea currentTextArea;

	private ChangeListener myListener;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription() {
		SettingsPluginComponentDescription resultDesc = new SettingsPluginComponentDescription(KEY, "Scrollverhalten", true);
		SettingsPluginComponentDescription joinDesc = new SettingsPluginComponentDescription(KEY_JOIN, "Scrollvorgänge zusammenfassen", true);
		SettingsComponentDescription joinTimeDesc = new SettingsComponentDescription(SettingsTextField.class,KEY_JOIN_TIME, "Grenzzeit (ms, z.B. 1000)");
		joinDesc.addSubComponent(joinTimeDesc);
		resultDesc.addSubComponent(joinDesc);
		return resultDesc;
	}

	@Override
	public void onFrameCreate(QuestionTreeNode selected, CodeViewer viewer,
			LoggingTreeNode newNode) {
		enabled = Boolean.parseBoolean(selected.getAttributeValue(KEY));
		if (enabled) {
			join = Boolean.parseBoolean(selected.getAttribute(KEY).getAttributeValue(KEY_JOIN));
			if (join) {
				try {
					joinTime = Long.parseLong(selected.getAttribute(KEY).getAttribute(KEY_JOIN).getAttributeValue(KEY_JOIN_TIME));
				} catch (Exception e) {
					joinTime=0;
				}
				if (joinTime==0) {
					join=false;
				}
			}
			currentNode = null;
			currentViewPort=null;
			currentTextArea=null;
			myListener = new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent arg0) {
					Rectangle rect = currentViewPort.getViewRect();
					Point topLeft = rect.getLocation();
					int x = Math.min(currentTextArea.getWidth(), topLeft.x+rect.width);
					int y = Math.min(currentTextArea.getHeight(), topLeft.y+rect.height);
					Point bottomRight = new Point(x,y);
					int startLine;
					int endLine;
					int startOffset = currentTextArea.viewToModel(topLeft);
					int endOffset = currentTextArea.viewToModel(bottomRight);
					try {
						startLine = currentTextArea.getLineOfOffset(startOffset)+1;
						endLine = currentTextArea.getLineOfOffset(endOffset)+1;
					} catch (BadLocationException e) {
						return;
					}
					if (currentNode.getChildCount()>0) {
						LoggingTreeNode lastNode = (LoggingTreeNode)currentNode.getLastChild();
						boolean wasScrolling = lastNode.getType().equals(TYPE_SCROLLING);
						if (wasScrolling) {
							if (join) {
								//Knoten zusammenführen?
								long timeDiff = System.currentTimeMillis()-Long.parseLong(lastNode.getAttribute(LoggingTreeNode.ATTRIBUTE_TIME));
								if (timeDiff<joinTime) {
									lastNode.setAttribute(ATTRIBUTE_STARTLINE, ""+startLine);
									lastNode.setAttribute(ATTRIBUTE_ENDLINE, ""+endLine);
									lastNode.setAttribute(LoggingTreeNode.ATTRIBUTE_TIME, ""+System.currentTimeMillis());
									return;
								}
							}
							//Wenn Zeilen nicht geändert wurden: nichts machen
							int lastStartLine = Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_STARTLINE));
							int lastEndLine = Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_ENDLINE));
							if (startLine==lastStartLine && endLine==lastEndLine) {
								return;
							}
						}
					}
					//nichts zu joinen, neue Zeilen: log!
					LoggingTreeNode node = new LoggingTreeNode(TYPE_SCROLLING);
					node.setAttribute(ATTRIBUTE_STARTLINE, ""+startLine);
					node.setAttribute(ATTRIBUTE_ENDLINE, ""+endLine);
					currentNode.add(node);
				}
			};
		}
	}

	@Override
	public void onNodeChange(LoggingTreeNode newNode, EditorPanel newTab) {
		if (enabled) {
			if (currentViewPort!=null) {
				currentViewPort.removeChangeListener(myListener);
				currentViewPort=null;
				currentTextArea=null;
			}
			currentNode=newNode;
			if (newTab!=null) {
				currentViewPort = newTab.getScrollPane().getViewport();
				currentTextArea = newTab.getTextArea();
				currentViewPort.addChangeListener(myListener);
			}
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
