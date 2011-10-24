package experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import experimentGUI.plugins.codeViewerPlugin.CodeViewer;
import experimentGUI.plugins.codeViewerPlugin.CodeViewerPluginInterface;
import experimentGUI.plugins.codeViewerPlugin.recorder.loggingTreeNode.LoggingTreeNode;
import experimentGUI.plugins.codeViewerPlugin.tabbedPane.EditorPanel;
import experimentGUI.util.questionTreeNode.QuestionTreeNode;
import experimentGUI.util.searchBar.GlobalSearchBar;
import experimentGUI.util.searchBar.SearchBar;
import experimentGUI.util.searchBar.SearchBarListener;
import experimentGUI.util.settingsComponents.SettingsComponentDescription;
import experimentGUI.util.settingsComponents.SettingsPluginComponentDescription;
import experimentGUI.util.settingsComponents.components.SettingsCheckBox;

public class SearchBarPlugin implements CodeViewerPluginInterface {
	public final static String KEY = "searchable";
	public final static String KEY_DISABLE_REGEX = "disableregex";
	public final static String KEY_ENABLE_GLOBAL = "enableglobal";

	public final static String TYPE_SEARCH = "search";
	public final static String ATTRIBUTE_ACTION = "action";
	public final static String ATTRIBUTE_QUERY = "query";
	public final static String ATTRIBUTE_SUCCESS = "success";
	private CodeViewer viewer;

	QuestionTreeNode selected;
	boolean enabled;

	HashMap<EditorPanel, SearchBar> map;
	GlobalSearchBar globalSearchBar;

	@Override
	public SettingsComponentDescription getSettingsComponentDescription() {
		SettingsPluginComponentDescription result = new SettingsPluginComponentDescription(KEY, "Suchfunktion einschalten", true);
		result.addSubComponent(new SettingsComponentDescription(SettingsCheckBox.class,KEY_DISABLE_REGEX, "Regex deaktivieren"));
		result.addSubComponent(new SettingsComponentDescription(SettingsCheckBox.class,KEY_ENABLE_GLOBAL, "Globale Suche aktivieren"));
		return result;
	}
	@Override
	public void init(QuestionTreeNode selected) {
		this.selected=selected;
		enabled = Boolean.parseBoolean(selected.getAttributeValue(KEY));
	}
	@Override
	public void onFrameCreate(CodeViewer v) {
		viewer=v;
		if (enabled) {
			map = new HashMap<EditorPanel,SearchBar>();
			JMenuItem findMenuItem = new JMenuItem("Suchen");
			findMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.Event.CTRL_MASK));
			findMenuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					SearchBar curr = map.get(viewer.getTabbedPane().getSelectedComponent());
					if (curr!=null) {
						curr.setVisible(true);
						curr.grabFocus();
					}
				}

			});
			viewer.addMenuItemToEditMenu(findMenuItem);

			boolean activateGlobal = Boolean.parseBoolean(selected.getAttribute(KEY).getAttributeValue(KEY_ENABLE_GLOBAL));
			if (activateGlobal) {
				globalSearchBar = new GlobalSearchBar(viewer.getShowDir(), v);
				globalSearchBar.setVisible(true);

				globalSearchBar.addSearchBarListener(new SearchBarListener() {

					@Override
					public void searched(String action, String query,
							boolean success) {
						LoggingTreeNode node = new LoggingTreeNode(TYPE_SEARCH);
						node.setAttribute(ATTRIBUTE_ACTION, action);
						node.setAttribute(ATTRIBUTE_QUERY, query);
						node.setAttribute(ATTRIBUTE_SUCCESS, ""+success);
						viewer.getRecorder().addLoggingTreeNode(node);
					}

				});

				if (Boolean.parseBoolean(selected.getAttribute(KEY).getAttributeValue(KEY_DISABLE_REGEX))) {
					globalSearchBar.getRegexCB().setVisible(false);
				}

				JSplitPane vsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
				JPanel curr = (JPanel)viewer.getContentPane();

				vsplit.setTopComponent(curr);
				vsplit.setBottomComponent(globalSearchBar);
				vsplit.setDividerLocation(400);
				globalSearchBar.setMinimumSize(new Dimension(400, 200));
				viewer.setContentPane(vsplit);

				JMenuItem findGlobalMenuItem = new JMenuItem("Global suchen");
				findGlobalMenuItem.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.Event.CTRL_MASK));
				findGlobalMenuItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						globalSearchBar.setVisible(true);
						globalSearchBar.grabFocus();
					}

				});
				viewer.addMenuItemToEditMenu(findGlobalMenuItem);
			}
		}
	}
	@Override
	public void onEditorPanelCreate(EditorPanel editorPanel) {
		if (enabled) {
			RSyntaxTextArea textPane = editorPanel.getTextArea();
			SearchBar searchBar = new SearchBar(textPane);
			searchBar.setVisible(true);
			searchBar.addSearchBarListener(new SearchBarListener() {

				@Override
				public void searched(String action, String query,
						boolean success) {
					LoggingTreeNode node = new LoggingTreeNode(TYPE_SEARCH);
					node.setAttribute(ATTRIBUTE_ACTION, action);
					node.setAttribute(ATTRIBUTE_QUERY, query);
					node.setAttribute(ATTRIBUTE_SUCCESS, ""+success);
					viewer.getRecorder().addLoggingTreeNode(node);
				}

			});

			if (Boolean.parseBoolean(selected.getAttribute(KEY).getAttributeValue(KEY_DISABLE_REGEX))) {
				searchBar.getRegexCB().setVisible(false);
			}

			editorPanel.add(searchBar, BorderLayout.SOUTH);
			map.put(editorPanel, searchBar);
		}
	}
	@Override
	public void onClose() {
	}
	@Override
	public void onEditorPanelClose(EditorPanel editorPanel) {
		if (enabled) {
			map.remove(editorPanel);
		}
	}
}
