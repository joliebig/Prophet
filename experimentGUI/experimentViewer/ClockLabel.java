/**


 * Diese Klasse repräsentiert eine Stoppuhr.
 * @author Markus Köppen, Andreas Hasselberg
 */

package experimentGUI.experimentViewer;

import java.text.DecimalFormat;

import javax.swing.JLabel;

import experimentGUI.util.questionTreeNode.QuestionTreeNode;


@SuppressWarnings("serial")
public class ClockLabel extends JLabel implements Runnable {

	private long startTime=0;
	private boolean isStarted=false;
	private boolean isRunning=false;
	private boolean isStopped=false;
	private long currentTime=0;
	private String caption;
	private Thread myThread;
	private QuestionTreeNode questionNode = null;

	public ClockLabel(QuestionTreeNode questionNode, String caption) {
		this.questionNode = questionNode;
		this.caption = caption;
	}

	/**
	 * Konstruktor durch welchen aktuelle Uhrzeit im Label angezeigt wird
	 *
	 * @param caption Beschriftung die vor der Uhrzeit stehen soll
	 */
	public ClockLabel(String caption) {
		this(null, caption);
	}

	/**
	 * Konstruktor für eine "unsichtbare" Uhr
	 */
	public ClockLabel() {
		this(null);
	}

	/**
	 * Startet die Stoppuhr
	 */
	public void start() {
		if (!isStarted) {
			myThread = new Thread(this);
			myThread.start();
		}
	}

	/**
	 * Pausiert die Stoppuhr
	 */
	public void pause() {
		if (isRunning) {
			saveTime();
			isRunning = false;
		}
	}

	/**
	 * L�sst die Stoppuhr ihre Arbeit wieder aufnehmen
	 */
	public void resume() {
		if(isStarted && !isRunning && !isStopped) {
			synchronized (this) {
				isRunning = true;
				notify();
			}
			startTime = System.currentTimeMillis() - currentTime;
		}
	}

	/**
	 * Stoppt die Stoppuhr
	 */
	public void stop() {
		if (isStarted && !isStopped) {
			saveTime();
			isRunning = false;
			isStopped = true;
			myThread.notify();
			myThread = null;
		}
	}

	/**
	 * Methode um Abzufragen ob die Stoppuhr gerade l�uft
	 *
	 * @return true wenn die Stoppuhr l�uft, sonst false
	 */
	public boolean isActive() {
		return isRunning;
	}

	/**
	 * Die Run Methode des Stoppuhr Threads - alle 1 sek ausgef�hrt Aktualisiert
	 * die derzeitige Zeit und schreibt diese bei Benutzung des ensprechenden
	 * Konstruktors in das JLabel
	 */
	public void run() {
		startTime = System.currentTimeMillis();
		isStarted = true;
		isRunning = true;
		isStopped = false;
		currentTime = 0;
		while (!isStopped) {
			long myTime = getCurrentTime();
			if (caption != null) {
				DecimalFormat df = new DecimalFormat("#00");
				long seconds = (myTime/1000)%60;
				long minutes = (myTime/1000)/60;
				setText(caption + ": " + df.format(minutes) + ":" + df.format(seconds));
			} else {
				setText(" ");
			}
			try {
				Thread.sleep(200);
				synchronized (this) {
					if (!isRunning) {
						wait();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	/**
	 * Gibt die aktuell gestoppte Zeit zur�ck
	 * @return Zeit Sekunden
	 */
	public long getCurrentTime() {
		if (isRunning) {
			currentTime = System.currentTimeMillis() - startTime;
		}
		return currentTime;
	}

	private void saveTime() {
		if (isStarted && questionNode!=null) {
			questionNode.setAnswerTime(getCurrentTime());
		}
	}
}
