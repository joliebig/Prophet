package experimentGUI.plugins.codeViewerPlugin;

import java.util.Vector;

import experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins.EditabilityPlugin;
import experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins.LineNumbersPlugin;
import experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins.PathPlugin;
import experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins.SearchBarPlugin;
import experimentGUI.plugins.codeViewerPlugin.codeViewerPlugins.SyntaxHighlightingPlugin;


public class CodeViewerPluginList {
	private static Vector<CodeViewerPluginInterface> plugins = new Vector<CodeViewerPluginInterface>() {
		private static final long serialVersionUID = 1L;
		{
			add(new PathPlugin());
			add(new EditabilityPlugin());
			add(new LineNumbersPlugin());
			add(new SearchBarPlugin());
			add(new SyntaxHighlightingPlugin());
		}
	};
	
	public static Vector<CodeViewerPluginInterface> getPlugins() {
		return plugins;
	}
	public static void add(CodeViewerPluginInterface plugin) {
		plugins.add(plugin);
	}
	public static boolean remove(CodeViewerPluginInterface plugin) {
		return plugins.remove(plugin);
	}
}