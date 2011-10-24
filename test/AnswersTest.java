package test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class AnswersTest extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static JTextPane textPane;

	public static final String[] ATTRIBUTES = {"id", "type", "weight", "answer"};
	public static final int[] PRIMARY_ATTRIBUTES = {0, 2, 3};


	private ArrayList<HashMap<String, String>> elements = new ArrayList<HashMap<String, String>>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AnswersTest frame = new AnswersTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AnswersTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		textPane = new JTextPane();
		contentPane.add(textPane, BorderLayout.CENTER);

		JButton button = new JButton("New button");
		button.addActionListener(this);
		contentPane.add(button, BorderLayout.NORTH);
	}

	public void actionPerformed(ActionEvent ae) {
		storeAnswerSpecifications(textPane.getText());
		printAnswerSpecifications();
	}

	public void printAnswerSpecifications() {
		System.out.println("Antworten: ");
		for(HashMap<String, String> hm : elements) {
			for(int i=0; i<ATTRIBUTES.length; i++) {
				System.out.print(ATTRIBUTES[i] + ":" + hm.get(ATTRIBUTES[i]));
				System.out.print(" - ");
			}
			System.out.println();
		}
	}

	public void storeAnswerSpecifications(String htmlContent) {
		//prepare analysis
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (javax.swing.text.html.HTMLDocument) kit.createDefaultDocument();
		doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		try {
			StringReader sr = new StringReader(htmlContent);
			kit.read(sr, doc, 0);
			HTMLDocument.Iterator tagIterator = doc.getIterator(HTML.Tag.INPUT);
			while (tagIterator.isValid()) {
				//store attributes of the current tag
				AttributeSet as = tagIterator.getAttributes();
				Enumeration<?> e = as.getAttributeNames();
				String[] attributesContent = new String[ATTRIBUTES.length];
				while(e.hasMoreElements()) {
					Object name = e.nextElement();
					for(int i=0; i<ATTRIBUTES.length; i++) {
						if(name.toString().equals(ATTRIBUTES[i])) {
							attributesContent[i] = as.getAttribute(name).toString();
						}
					}
				}
				boolean store = true;
				for(int i=0; i<PRIMARY_ATTRIBUTES.length; i++) {
					if(attributesContent[PRIMARY_ATTRIBUTES[i]] == null) {
						store = false;
						break;
					}
				}
				if(store) {
					HashMap<String, String> hm = new HashMap<String, String>();
					for(int i=0; i<ATTRIBUTES.length; i++) {
						hm.put(ATTRIBUTES[i], attributesContent[i]);
					}
					elements.add(hm);
				}
				tagIterator.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
