package boundary.notifier;

/**
 * Decoratore asincrono per {@link INotifier} che esegue l'invio di
 * ogni email in un thread separato.
 * <p>
 * Utilizza internamente il Singleton {@link Notifier} per l'invio
 * effettivo del messaggio, restituendo immediatamente il controllo
 * al chiamante senza attendere il completamento dell'operazione SMTP.
 * </p>
 * <p>
 * Indicato in scenari in cui il tempo di risposta dell'interfaccia
 * utente ha priorità rispetto alla conferma di avvenuto invio.
 * </p>
 */
public class ThreadNotifier implements INotifier {

    private static ThreadNotifier instance;

    /**
     * Costruttore privato per il pattern Singleton.
     */
    private ThreadNotifier() {
    }

    /**
     * Restituisce l'unica istanza del notifier asincrono, creandola
     * se necessario.
     *
     * @return l'istanza Singleton
     */
    public static ThreadNotifier getInstance() {
        if (instance == null) {
            instance = new ThreadNotifier();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     * <p>
     * L'invio viene delegato a {@link Notifier#sendMailComplete(String)}
     * in un nuovo thread, in modo che il metodo ritorni immediatamente.
     * </p>
     */
    @Override
    public void sendMailComplete(String destinatario) {
        new Thread(() -> Notifier.getInstance().sendMailComplete(destinatario)).start();
    }

    /**
     * {@inheritDoc}
     * <p>
     * L'invio viene delegato a {@link Notifier#sendMailCreate(String)}
     * in un nuovo thread, in modo che il metodo ritorni immediatamente.
     * </p>
     */
    @Override
    public void sendMailCreate(String destinatario) {
        new Thread(() -> Notifier.getInstance().sendMailCreate(destinatario)).start();
    }
}