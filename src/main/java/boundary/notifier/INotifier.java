package boundary.notifier;

/**
 * Interfaccia per l'invio di notifiche email relative agli eventi
 * del ciclo di vita di una sessione di allenamento.
 * <p>
 * Le implementazioni concrete possono utilizzare un servizio SMTP,
 * un broker di messaggistica o un semplice logger, a seconda del
 * contesto di esecuzione.
 * </p>
 */
public interface INotifier {

    /**
     * Invia un'email di notifica al completamento di una sessione.
     *
     * @param destinatario indirizzo email del destinatario
     */
    void sendMailComplete(String destinatario);

    /**
     * Invia un'email di notifica alla creazione di una nuova sessione.
     *
     * @param destinatario indirizzo email del destinatario
     */
    void sendMailCreate(String destinatario);
}