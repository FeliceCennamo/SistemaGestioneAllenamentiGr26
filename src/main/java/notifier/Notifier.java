package notifier;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;


public class Notifier implements INotifier{

    private final String username = "ad4fc6001@smtp-brevo.com"; //Mail del server che manderà le mail
    private final String password_app = System.getenv("BREVO_KEY");
    private static Notifier instance = null;

    private Notifier(){}

    public static Notifier getInstance(){
        if(instance == null){
            instance = new Notifier();
        }

        return instance;
    }
    private Session setSMTPserver(){
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-relay.brevo.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password_app);
            }
        });
    }

    @Override
    public void sendMailCreate(String destinatario) throws IllegalArgumentException{
        if(!destinatario.contains("@")){
            throw new IllegalArgumentException();
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

    @Override
    public void sendMailComplete(String destinatario) throws IllegalArgumentException{
        if(!destinatario.contains("@")){
            throw new IllegalArgumentException();
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
