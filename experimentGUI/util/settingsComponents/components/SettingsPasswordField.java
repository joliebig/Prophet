package experimentGUI.util.settingsComponents.components;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import experimentGUI.util.settingsComponents.SettingsComponent;


@SuppressWarnings("serial")
public class SettingsPasswordField extends SettingsComponent{
	private JLabel caption;
	private JTextField textField;
	private final static byte XOR_KEY = 77;

	private static byte[] xor(byte[] input) {
		byte[] result = new byte[input.length];
		for (int i = 0; i < input.length; i++) {
			result[i] = (byte) (input[i] ^ XOR_KEY);
		}
		return result;
	}

	private static String encode(String s) {
		return new BASE64Encoder().encode(xor(s.getBytes()));
	}

	public static String decode(String s) {
		try {
			return new String(xor(new BASE64Decoder().decodeBuffer(s)));
		} catch (IOException e) {
			return null;
		}
	}

	public SettingsPasswordField() {
		setLayout(new BorderLayout());
		caption = new JLabel();
		add(caption, BorderLayout.NORTH);
		textField = new JPasswordField();
		add(textField, BorderLayout.CENTER);
//		textField.getDocument().addDocumentListener(new DocumentListener() {
//
//			@Override
//			public void changedUpdate(DocumentEvent arg0) {
//				saveValue();
//			}
//
//			@Override
//			public void insertUpdate(DocumentEvent arg0) {
//				saveValue();
//			}
//
//			@Override
//			public void removeUpdate(DocumentEvent arg0) {
//				saveValue();
//			}
//		});
	}

	public void setCaption(String cap) {
		caption.setText(cap);
	}

	public void loadValue() {
		textField.setText(decode(getTreeNode().getValue()));
	}

	public void saveValue() {
		getTreeNode().setValue(encode(textField.getText()));
	}
}
