/**
 * Diese Klasse stellt ein Panel dar, in welcher Elemente nach dem BoxLayout angeordnet werden (horizontal).
 * Zudem kann eine Tabelle hinzugef�gt werden, dies geschieht indem ein Panel mit FormLayout hinzugef�gt wird.
 * Es werden spezielle "add-Methoden" bereitgestellt um Elemente Formatiert hinzuzuf�gen
 * 
 * @author Markus K�ppen, Andreas Hasselberg
 */

package test;

import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class PanelContainer extends JPanel {

	public static final boolean HORIZONTAL = true;
	public static final boolean VERTICAL = false;

	private ArrayList<JComponent> components;

	/**
	 * Standartkonstruktor, es wird ein leeres Panel mit horizontalem BoxLayout
	 * erstellt.
	 */
	public PanelContainer() {
		super();
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setAlignmentX(LEFT_ALIGNMENT);
		this.setAlignmentY(TOP_ALIGNMENT);
		components = new ArrayList<JComponent>();
	}

	/**
	 * L�scht eine Komponente an einem Index Ist dieser zu gro� wird false
	 * zur�ckgegeben
	 * 
	 * @param index
	 *            Index der Komponente die gel�scht werden soll
	 * @return true wenn L�schung vorgenommen, false wenn nicht
	 */
	public boolean removeComponent(int index) {
		if (index < components.size()) {
			components.remove(index);
			this.remove(index);
			updateUI();
			return true;
		}
		return false;
	}

	/**
	 * Gibt die Anzahl der Komponenten im PanelContainer zur�ck
	 * 
	 * @return Anzahl der Komponenten
	 */
	public int getComponentCount() {
		return components.size();
	}

	/**
	 * F�gt eine Komponente an der Position i ein
	 * 
	 * @param comp
	 *            Komponente die eingef�gt werden soll
	 * @param i
	 *            Einf�geposition (indexbasiert)
	 */
	public void addComponent(JComponent comp, int i) {
		components.add(i, comp);
	}

	/**
	 * F�gt eine Komponente am Ende an
	 * 
	 * @param comp
	 *            Komponente die eingef�gt werden soll
	 */
	public void addComponent(JComponent comp) {
		components.add(comp);
	}
}
