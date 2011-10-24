package experimentGUI.util;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

import experimentGUI.Constants;
import experimentGUI.experimentViewer.ExperimentViewer;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;

/**
 * This class shows the html files (questions) creates the navigation and
 * navigates everything...
 *
 * @author Markus Köppen, Andreas Hasselberg
 *
 */
@SuppressWarnings("serial")
public class QuestionViewPane extends JScrollPane {
	public static final String FOOTER_FORWARD_CAPTION = "Weiter";
	public static final String FOOTER_BACKWARD_CAPTION = "Zurück";
	public static final String FOOTER_END_CATEGORY_CAPTION = "Kategorie Abschließen";
	public static final String FOOTER_START_EXPERIMENT_CAPTION = "Experiment starten";
	public static final String FOOTER_SUBJECT_CODE_CAPTION = "Probandencode:";

	// constants for the html navigation
	public static final String HTML_START = "<html><body><form>";
	public static final String HTML_DIVIDER = "<br /><br /><hr /><br /><br />";
	public static final String HTML_TYPE_SUBMIT = "submit";
	public static final String FOOTER_FORWARD = "<input name =\"" + Constants.KEY_FORWARD + "\" type=\""
			+ HTML_TYPE_SUBMIT + "\" value=\"" + FOOTER_FORWARD_CAPTION + "\" />";
	public static final String FOOTER_BACKWARD = "<input name =\"" + Constants.KEY_BACKWARD + "\" type=\""
			+ HTML_TYPE_SUBMIT + "\" value=\"" + FOOTER_BACKWARD_CAPTION + "\" />";
	public static final String FOOTER_END_CATEGORY = "<input name =\"" + Constants.KEY_FORWARD + "\" type=\""
			+ HTML_TYPE_SUBMIT + "\" value=\"" + FOOTER_END_CATEGORY_CAPTION + "\" />";
	public static final String FOOTER_EXPERIMENT_CODE = "<table><tr><td>" + FOOTER_SUBJECT_CODE_CAPTION
			+ "</td><td><input name=\"" + Constants.KEY_SUBJECT + "\" /></td></tr></table>";
	public static final String FOOTER_START_EXPERIMENT = FOOTER_EXPERIMENT_CODE + HTML_DIVIDER
			+ "<input name =\"" + Constants.KEY_FORWARD + "\" type=\"" + HTML_TYPE_SUBMIT + "\" value=\""
			+ FOOTER_START_EXPERIMENT_CAPTION + "\" />";
	public static final String HTML_END = "</form></body></html>";

	public static final String HEADER_ATTRIBUTE = "header";

	private ActionListener actionListener;
	private QuestionTreeNode questionNode;
	private JTextPane textPane;

	private FormView submitButton;
	private boolean doNotFire = false;

	/**
	 * With the call of the Constructor the data is loaded and everything is
	 * initialized. The first question is showed.
	 *
	 * @param path
	 *            path of the xml file with the data
	 * @param cqlp
	 *            the categorieQuestionListsPanel where the overview is shown
	 */
	public QuestionViewPane(final QuestionTreeNode questionNode) {
		this.questionNode = questionNode;
		textPane = new JTextPane();
		this.setViewportView(textPane);

		textPane.setEditable(false);
		textPane.setEditorKit(new HTMLEditorKit() {
			public ViewFactory getViewFactory() {
				return new HTMLEditorKit.HTMLFactory() {
					public View create(Element elem) {
						Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
						if (o instanceof HTML.Tag) {
							if (o == HTML.Tag.INPUT || o == HTML.Tag.TEXTAREA || o == HTML.Tag.SELECT) {
								FormView formView = new FormView(elem) {
									// What should happen when the buttons are
									// pressed?
									protected void submitData(String data) {
										String action = saveAnswers(data);
										if (action != null) {
											fireEvent(action);
										}
									}
								};

								if (o == HTML.Tag.INPUT
										&& elem.getAttributes().getAttribute(HTML.Attribute.NAME) != null
										&& elem.getAttributes().getAttribute(HTML.Attribute.NAME)
												.equals(Constants.KEY_FORWARD)
										&& elem.getAttributes().getAttribute(HTML.Attribute.TYPE) != null
										&& elem.getAttributes().getAttribute(HTML.Attribute.TYPE)
												.equals(HTML_TYPE_SUBMIT)) {
									submitButton = formView;
								}
								return formView;
							}
						}
						return super.create(elem);
					}
				};
			}
		});

		textPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					Desktop desktop = null;
				    if (Desktop.isDesktopSupported()) {
				        desktop = Desktop.getDesktop();
				        if(desktop.isSupported(Desktop.Action.BROWSE)) {
				        	try {
								desktop.browse(event.getURL().toURI());
							} catch (IOException e) {
					        	JOptionPane.showMessageDialog(textPane, "Fehler beim Starten des Browsers.");
							} catch (URISyntaxException e) {
					        	JOptionPane.showMessageDialog(textPane, "Fehlerhafte URL.");
							}
				        } else {
				        	JOptionPane.showMessageDialog(textPane, "Konnte keinen Standardbrowser öffnen.");
				        }
				    }  else {
			        	JOptionPane.showMessageDialog(textPane, "Konnte keinen Standardbrowser öffnen.");
				    }
				}
			}

		});

		URL trueBase = ClassLoader.getSystemResource(".");
		((javax.swing.text.html.HTMLDocument) textPane.getDocument()).setBase(trueBase);

		String questionText = HTML_START + questionNode.getValue() + HTML_DIVIDER;
		boolean questionSwitching = false;
		if (questionNode.isQuestion()) {
			questionSwitching = Boolean.parseBoolean(((QuestionTreeNode) questionNode.getParent())
					.getAttributeValue("questionswitching"));
		}
		if (hasActivePreviousNode(questionNode) && questionSwitching) {
			questionText += FOOTER_BACKWARD;
		}
		if (questionNode.isExperiment()) {
			questionText += FOOTER_START_EXPERIMENT;
		} else {
			if (hasActiveNextNode(questionNode)) {
				questionText += FOOTER_FORWARD;
			} else {
				questionText += FOOTER_END_CATEGORY;
			}
		}
		questionText += HTML_END;
		textPane.setText(questionText);
		textPane.setCaretPosition(0);
	}

	/**
	 * saves the answers returns an integer to know which button was pressed
	 *
	 * @param data
	 *            data which should be stored an consist the info about which
	 *            button was pressed
	 * @return FORWARD if forward button, BACKWARD if backward button, -1 if
	 *         neither
	 */
	private String saveAnswers(String data) {
		StringTokenizer st = new StringTokenizer(data, "&");
		String result = null;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			String key = null;
			String value = null;
			try {
				key = URLDecoder.decode(token.substring(0, token.indexOf("=")), "ISO-8859-1");
				value = URLDecoder.decode(token.substring(token.indexOf("=") + 1, token.length()),
						"ISO-8859-1");
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
			if (key.equals(Constants.KEY_FORWARD) || key.equals(Constants.KEY_BACKWARD)) {
				result = key;
			} else {
				questionNode.setAnswer(key, value);
			}
		}
		return result;
	}

	private boolean hasActiveNextNode(QuestionTreeNode node) {
		if (node.isExperiment() || node.isCategory()) {
			if (ExperimentViewer.denyEnterNode(node) || node.getChildCount() == 0) {
				return false;
			} else {
				node = (QuestionTreeNode) node.getFirstChild();
				if (!ExperimentViewer.denyEnterNode(node)) {
					return true;
				}
				return hasActiveNextNode(node);
			}
		} else if (node.isQuestion()) {
			node = (QuestionTreeNode) node.getNextSibling();
			if (node == null) {
				return false;
			}
			if (!ExperimentViewer.denyEnterNode(node)) {
				return true;
			}
			return hasActiveNextNode(node);
		} else {
			return false;
		}
	}

	private boolean hasActivePreviousNode(QuestionTreeNode node) {
		if (node.isQuestion()) {
			node = (QuestionTreeNode) node.getPreviousSibling();
			if (node == null) {
				return false;
			}
			if (!ExperimentViewer.denyEnterNode(node)) {
				return true;
			}
			return hasActivePreviousNode(node);
		} else {
			return false;
		}
	}

	public void setActionListener(ActionListener l) {
		actionListener = l;
	}

	public ActionListener getActionListener() {
		return actionListener;
	}

	public void fireEvent(String action) {
		if (actionListener != null && !doNotFire) {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action);
			actionListener.actionPerformed(event);
		}
	}

	public boolean clickSubmit() {
		if (submitButton != null && submitButton.getComponent() != null
				&& submitButton.getComponent() instanceof JButton) {
			((JButton) submitButton.getComponent()).doClick();
			return true;
		}
		return false;
	}

	public boolean saveCurrentAnswersToNode() {
		if (submitButton != null && submitButton.getComponent() != null
				&& submitButton.getComponent() instanceof JButton) {
			doNotFire = true;
			((JButton) submitButton.getComponent()).doClick();
			doNotFire = false;
			return true;
		}
		return false;
	}
}
