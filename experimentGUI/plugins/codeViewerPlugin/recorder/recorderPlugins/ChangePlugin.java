package experimentGUI.plugins.codeViewerPlugin.recorder.recorderPlugins;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import experimentGUI.plugins.codeViewerPlugin.CodeViewer;
import experimentGUI.plugins.codeViewerPlugin.recorder.RecorderPluginInterface;
import experimentGUI.plugins.codeViewerPlugin.recorder.loggingTreeNode.LoggingTreeNode;
import experimentGUI.plugins.codeViewerPlugin.tabbedPane.EditorPanel;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
import experimentGUI.util.settingsComponents.SettingsPluginComponentDescription;
import experimentGUI.util.settingsComponents.components.SettingsTextField;

public class ChangePlugin implements RecorderPluginInterface {
	public final static String KEY = "change";
	public final static String KEY_JOIN = "join";
	public final static String KEY_JOIN_TIME = "jointime";
	public final static String TYPE_INSERT = "insert";
	public final static String TYPE_REMOVE = "remove";
	public final static String ATTRIBUTE_OFFSET = "offset";
	public final static String ATTRIBUTE_LENGTH = "length";
	public final static String ATTRIBUTE_CONTENT = "content";

	private boolean enabled;
	private boolean join;
	private long joinTime;

	private LoggingTreeNode currentNode;
	private Document currentDocument;

	private DocumentListener myListener;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription() {
		SettingsPluginComponentDescription resultDesc = new SettingsPluginComponentDescription(KEY, "Quelltextänderungen", true);
		SettingsPluginComponentDescription joinDesc = new SettingsPluginComponentDescription(KEY_JOIN, "Änderungen zusammenfassen", true);
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
			currentDocument=null;
			myListener = new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent arg0) {

				}

				@Override
				public void insertUpdate(DocumentEvent arg0) {
					int offset = arg0.getOffset();
					int length = arg0.getLength();
					String content = "";
					try {
						content = arg0.getDocument().getText(offset, length);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					if (join && currentNode.getChildCount()>0) {
						//Knoten zusammenf�hren?
						LoggingTreeNode lastNode = (LoggingTreeNode)currentNode.getLastChild();
						boolean wasInsert = lastNode.getType().equals(TYPE_INSERT);
						if (wasInsert) {
							long timeDiff = System.currentTimeMillis()-Long.parseLong(lastNode.getAttribute(LoggingTreeNode.ATTRIBUTE_TIME));
							int lastEndOffset = Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_OFFSET))+Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_LENGTH));
							if (timeDiff<joinTime && lastEndOffset==offset) {
								int newLength=Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_LENGTH))+length;
								String newContent = lastNode.getAttribute(ATTRIBUTE_CONTENT)+content;
								lastNode.setAttribute(ATTRIBUTE_LENGTH, ""+newLength);
								lastNode.setAttribute(ATTRIBUTE_CONTENT, newContent);
								lastNode.setAttribute(LoggingTreeNode.ATTRIBUTE_TIME, ""+System.currentTimeMillis());
								return;
							}
						}
					}
					LoggingTreeNode node = new LoggingTreeNode(TYPE_INSERT);
					node.setAttribute(ATTRIBUTE_OFFSET, ""+offset);
					node.setAttribute(ATTRIBUTE_LENGTH, ""+length);
					node.setAttribute(ATTRIBUTE_CONTENT, ""+content);
					currentNode.add(node);
				}

				@Override
				public void removeUpdate(DocumentEvent arg0) {
					int offset = arg0.getOffset();
					int length = arg0.getLength();
					if (join && currentNode.getChildCount()>0) {
						//Knoten zusammenf�hren?
						LoggingTreeNode lastNode = (LoggingTreeNode)currentNode.getLastChild();
						boolean wasRemove = lastNode.getType().equals(TYPE_REMOVE);
						if (wasRemove) {
							System.out.println("was remove");
							long timeDiff = System.currentTimeMillis()-Long.parseLong(lastNode.getAttribute(LoggingTreeNode.ATTRIBUTE_TIME));
							int lastOffset = Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_OFFSET));
							int lastLength = Integer.parseInt(lastNode.getAttribute(ATTRIBUTE_LENGTH));
							boolean wasBackspace = offset+length==lastOffset;
							boolean wasDelete = lastOffset==offset;
							if (timeDiff<joinTime && (wasBackspace || wasDelete)) {
								int newLength=lastLength+length;
								lastNode.setAttribute(ATTRIBUTE_OFFSET, ""+offset);
								lastNode.setAttribute(ATTRIBUTE_LENGTH, ""+newLength);
								lastNode.setAttribute(LoggingTreeNode.ATTRIBUTE_TIME, ""+System.currentTimeMillis());
								return;
							}
						}
					}
					LoggingTreeNode node = new LoggingTreeNode(TYPE_REMOVE);
					node.setAttribute(ATTRIBUTE_OFFSET, ""+offset);
					node.setAttribute(ATTRIBUTE_LENGTH, ""+length);
					currentNode.add(node);
				}
			};
		}
	}

	@Override
	public void onNodeChange(LoggingTreeNode newNode, EditorPanel newTab) {
		if (enabled) {
			if (currentDocument!=null) {
				currentDocument.removeDocumentListener(myListener);
				currentDocument=null;
			}
			currentNode=newNode;
			if (newTab!=null) {
				currentDocument = newTab.getTextArea().getDocument();
				currentDocument.addDocumentListener(myListener);
			}
		}
	}

	@Override
	public String getKey() {
		return KEY;
	}

}
