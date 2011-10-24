package test;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * this class contains a method to send an E-Mail (with attachments)
 *
 * @author zeja
 * @author Markus Köppen, Andreas Hasselberg
 */
public class Email {

	/**
	 * constants for the Email infos
	 */
	private static final String password = "2010ErstellterExperimentVerteiler.";
	private static final String username = "Verteiler.Experiment";
	private static final String userAddress = "Verteiler.Experiment@web.de";
	// GeheimFrage: Lieblingsfilm: Das Experiment
	private static final String smtpHost = "smtp.web.de";

	/**
	 * Sends an EMail
	 *
	 * @param recipientsAddress
	 *            adress of the recipient
	 * @param subject
	 *            subject of the email
	 * @param text
	 *            text of the email
	 * @param filePath
	 *            attachment (null if not used)
	 */
	public static boolean sendMail(String recipientsAddress, String subject, String text, String filePath) {
		try {
			MailAuthenticator auth = new MailAuthenticator(username, password);
			Properties properties = new Properties();
			properties.put("mail.smtp.host", smtpHost);
			properties.put("mail.smtp.auth", "true");

			Session session = Session.getDefaultInstance(properties, auth);
			Message msg = new MimeMessage(session);

			MimeMultipart content = new MimeMultipart("alternative");
			MimeBodyPart message = new MimeBodyPart();
			message.setText(text);
			message.setHeader("MIME-Version", "1.0");
			message.setHeader("Content-Type", message.getContentType());
			content.addBodyPart(message);
			if (filePath != null) {
				DataSource fileDataSource = new FileDataSource(filePath);
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
				messageBodyPart.setFileName(new File(filePath).getName());
				content.addBodyPart(messageBodyPart);
			}
			msg.setContent(content);
			msg.setSentDate(new Date());
			msg.setFrom(new InternetAddress(userAddress));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientsAddress, false));
			msg.setSubject(subject);
			Transport.send(msg);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * main method to test the methods
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String recipientsAddress = "hasselbe@gmail.com"; // somereceiver@web.de
		String subject = "Test";
		String text = "text";

		System.out.println(Email.sendMail(recipientsAddress, subject, text, null));
	}

	static class MailAuthenticator extends Authenticator {
		private final String user;
		private final String password;

		/**
		 * Der Konstruktor erzeugt ein MailAuthenticator Objekt<br>
		 * aus den beiden Parametern user und passwort.
		 *
		 * @param user
		 *            String, der Username fuer den Mailaccount.
		 * @param password
		 *            String, das Passwort fuer den Mailaccount.
		 */
		public MailAuthenticator(String user, String password) {
			this.user = user;
			this.password = password;
		}

		/**
		 * Diese Methode gibt ein neues PasswortAuthentication Objekt zurueck.
		 *
		 * @see javax.mail.Authenticator#getPasswordAuthentication()
		 */
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(this.user, this.password);
		}
	}
}