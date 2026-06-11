package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Punto di accesso centralizzato per la creazione di {@link EntityManager}
 * tramite una {@link EntityManagerFactory} configurata.
 * <p>
 * Implementa il pattern Singleton per garantire che esista una sola factory
 * per l'intera applicazione, basata sull'unità di persistenza
 * {@code GestioneAllenamentiPU} definita nel file {@code persistence.xml}.
 * </p>
 *
 * <p>
 * <b>Attenzione:</b> il Singleton non è thread-safe; in un ambiente
 * multi-thread occorre sincronizzare il metodo {@link #getInstance()}
 * o utilizzare un inizializzatore statico.
 * </p>
 */
public class JpaUtil {

    private static JpaUtil instance;

    private final EntityManagerFactory emf;

    /**
     * Costruttore privato. Inizializza la {@link EntityManagerFactory}
     * leggendo la configurazione dell'unità di persistenza.
     */
    private JpaUtil() {
        emf = Persistence.createEntityManagerFactory("GestioneAllenamentiPU");
    }

    /**
     * Restituisce l'unica istanza di {@code JpaUtil}.
     * <p>
     * L'istanza viene creata al primo accesso (lazy initialization).
     * </p>
     *
     * @return l'istanza Singleton
     */
    public static JpaUtil getInstance() {
        if (instance == null) {
            instance = new JpaUtil();
        }
        return instance;
    }

    /**
     * Crea e restituisce un nuovo {@link EntityManager}.
     * <p>
     * Ogni chiamata produce un manager indipendente, che deve essere chiuso
     * dal chiamante dopo l'uso (tipicamente tramite il pattern try-finally
     * o try-with-resources).
     * </p>
     *
     * @return una nuova istanza di {@link EntityManager}
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Chiude definitivamente la {@link EntityManagerFactory} e
     * reimposta l'istanza Singleton a {@code null}.
     * <p>
     * Dopo la chiamata, ogni ulteriore utilizzo di {@code getEntityManager()}
     * richiederà una nuova inizializzazione tramite {@code getInstance()}.
     * </p>
     */
    public void chiudi() {
        emf.close();
        instance = null;
    }
}