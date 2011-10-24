package experimentGUI.util.settingsComponents.components;

import java.awt.BorderLayout;
import javax.swing.JCheckBox;

import experimentGUI.util.settingsComponents.SettingsComponent;


@SuppressWarnings("serial")
public class SettingsCheckBox extends SettingsComponent {
	private JCheckBox myCheckBox;

	public SettingsCheckBox() {
		setLayout(new BorderLayout());
		myCheckBox = new JCheckBox();
		add(myCheckBox,BorderLayout.CENTER);
	}

	public void setCaption(String cap) {
		myCheckBox.setText(cap);
	}
	public void loadValue() {
		myCheckBox.setSelected(Boolean.parseBoolean(getTreeNode().getValue()));
	}

	public void saveValue() {
		getTreeNode().setValue(""+myCheckBox.isSelected());
	}
}
