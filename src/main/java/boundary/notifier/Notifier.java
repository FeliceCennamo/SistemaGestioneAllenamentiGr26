package boundary.notifier;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/**
 * Implementazione concreta di {@link INotifier} che invia email
 * tramite il servizio SMTP di Brevo.
 * <p>
 * Le credenziali del server sono configurate in modo hard-coded per
 * l'utente SMTP, mentre la password applicativa viene letta dalla
 * variabile d'ambiente {@code BREVO_KEY}.
 * </p>
 * <p>
 * La classe segue il pattern Singleton thread-safe con inizializzazione
 * lazy, garantendo un'unica istanza per l'intera applicazione.
 * </p>
 */
public class Notifier implements INotifier {

    private static Notifier instance;
    /**
     * Nome utente del server SMTP Brevo utilizzato per l'invio.
     */
    private final String username = "ad4fc6001@smtp-brevo.com";
    /**
     * Password applicativa letta dalla variabile d'ambiente {@code BREVO_KEY}.
     */
    private final String passwordApp = System.getenv("BREVO_KEY");

    /**
     * Costruttore privato per impedire l'istanziazione diretta.
     */
    private Notifier() {
    }

    /**
     * Restituisce l'unica istanza del notifier, sincronizzata per
     * garantire la thread-safety durante la creazione iniziale.
     *
     * @return l'istanza Singleton
     */
    public static synchronized Notifier getInstance() {
        if (instance == null) {
            instance = new Notifier();
        }
        return instance;
    }

    /**
     * Configura e restituisce una sessione SMTP autenticata verso i server Brevo.
     *
     * @return una {@link Session} pronta per l'invio
     */
    private Session setSMTPserver() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-relay.brevo.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, passwordApp);
            }
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * Invia un'email informando l'atleta che un allenatore gli ha
     * assegnato una nuova sessione di allenamento.
     * </p>
     *
     * @throws IllegalArgumentException se l'indirizzo del destinatario
     *                                  non contiene il simbolo {@code @}
     */
    @Override
    public void sendMailCreate(String destinatario) {
        if (!destinatario.contains("@")) {
            throw new IllegalArgumentException("Indirizzo email non valido");
        }

        try {
            Session session = setSMTPserver();
            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("mormileluigi.lm91@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Ci sono nuovi messaggi dai tuoi allenatori!");
            message.setText("Il tuo allenatore ti ha assegnato un nuovo allenamento!\n" +
                    "Accedi all'app e inizia ad allenarti!!\n\n" +
                    "Messaggio inviato automaticamente, si prega di non rispondere");

            Transport.send(message);
            System.out.println("Email inviata con successo!");

        } catch (MessagingException e) {
            System.err.println("Errore durante l'invio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Invia un'email informando l'allenatore che un atleta ha completato
     * una sessione di allenamento precedentemente assegnata.
     * </p>
     *
     * @throws IllegalArgumentException se l'indirizzo del destinatario
     *                                  non contiene il simbolo {@code @}
     */
    @Override
    public void sendMailComplete(String destinatario) {
        if (!destinatario.contains("@")) {
            throw new IllegalArgumentException("Indirizzo email non valido");
        }

        try {
            Session session = setSMTPserver();
            session.setDebug(true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("mormileluigi.lm91@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Ci sono nuovi messaggi dai tuoi atleti");
            message.setText("Un tuo atleta ha completato un allenamento a lui assegnato!\n" +
                    "Scopri i suoi risultati e aiutalo a raggiungere i suoi obiettivi!!\n\n" +
                    "Messaggio inviato automaticamente, si prega di non rispondere");

            Transport.send(message);
            System.out.println("Email inviata con successo!");

        } catch (MessagingException e) {
            System.err.println("Errore durante l'invio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}